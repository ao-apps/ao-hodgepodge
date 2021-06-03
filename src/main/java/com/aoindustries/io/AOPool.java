/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.io;

import com.aoindustries.collections.AoCollections;
import com.aoindustries.exception.WrappedExceptions;
import com.aoindustries.lang.Strings;
import com.aoindustries.lang.Throwables;
import com.aoindustries.util.ErrorPrinter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable generic connection pooling with dynamic flaming tiger feature.
 * <p>
 * Two lists of connections are maintained.  The first is the list of connections
 * that are ready unused, and the second is the list of connections that are
 * currently checked-out.  By using this strategy, an available connection
 * can be found without searching the entire list.
 * </p>
 * <p>
 * In addition to the global lists, a {@link ThreadLocal} list of connections
 * checked-out by the current thread is maintained.  When getting a new connection,
 * this is used to check against <code>maxConnections</code> instead of checking
 * the global lists.
 * </p>
 * <p>
 * Idea: Add some sort of thread-connection affinity, where the same connection
 *       slot will be used by the same thread when it is available.  This should
 *       help cache locality.
 * </p>
 * <p>
 * Idea: Automatically connect ahead of time in the background.  This could
 *       hide connection latency on first-use.
 * </p>
 *
 * @param  <Ex>  An arbitrary exception type that may be thrown
 *
 * @author  AO Industries, Inc.
 */
// TODO: Don't extend Thread
abstract public class AOPool<C extends AutoCloseable, Ex extends Throwable, I extends Throwable> extends Thread {

	public static final int DEFAULT_DELAY_TIME = 1 * 60 * 1000;
	public static final int DEFAULT_MAX_IDLE_TIME = 10 * 60 * 1000;

	public static final long UNLIMITED_MAX_CONNECTION_AGE = -1;
	public static final long DEFAULT_MAX_CONNECTION_AGE = 30L * 60 * 1000;

	public static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000; // Was 5 seconds for a very long time, but too sensitive to transient network problems
	public static final int DEFAULT_SOCKET_SO_LINGER = 15;

	/**
	 * The number of milliseconds between loggings of waiting on full pool.
	 */
	private static final long WAIT_LOGGING_INTERVAL = 60_1000L; // One minute

	/**
	 * All updates to the fields must be synchronized on the {@link PooledConnection} instance.
	 */
	private static class PooledConnection<C> implements Comparable<PooledConnection<C>> {

		private static final AtomicLong nextId = new AtomicLong(0);

		final long id = nextId.getAndIncrement();

		/**
		 * The current connection.
		 */
		volatile C connection;

		/**
		 * The time the connection was created
		 */
		volatile long createTime;

		/**
		 * Total time using the connection
		 */
		final AtomicLong totalTime = new AtomicLong();

		/**
		 * The time getting the connection from the pool
		 */
		volatile long startTime;

		/**
		 * The time returning the connection to the pool
		 */
		volatile long releaseTime;

		/**
		 * Counts the number of times the connection is connected
		 */
		final AtomicLong connectCount = new AtomicLong();

		/**
		 * Counts the number of times the connection is used
		 */
		final AtomicLong useCount = new AtomicLong();

		/**
		 * Keeps track of the stack trace at checkout for this connection.
		 */
		volatile Throwable allocateStackTrace;

		/**
		 * Older connections are sorted lower.
		 */
		@Override
		public int compareTo(PooledConnection<C> o) {
			if(id<o.id) return -1;
			if(id>o.id) return 1;
			return 0;
		}

		@Override
		public boolean equals(Object O) {
			return
				O!=null
				&& (O instanceof PooledConnection)
				&& id == ((PooledConnection)O).id
			;
		}

		@Override
		public int hashCode() {
			return (int) (this.id ^ (this.id >>> 32));
		}
	}

	final private int delayTime;
	final private int maxIdleTime;
	final private long startTime;
	final private int poolSize;
	final private long maxConnectionAge;

	/**
	 * Lock for wait/notify
	 */
	private static class PoolLock {}
	private final PoolLock poolLock = new PoolLock();

	/**
	 * All connections that have been created.
	 * All accesses should be behind poolLock.
	 */
	private final List<PooledConnection<C>> allConnections;

	/**
	 * Connections that are available.
	 * All accesses should be behind poolLock.
	 */
	private final PriorityQueue<PooledConnection<C>> availableConnections;

	/**
	 * Connections that are allocated.
	 * All accesses should be behind poolLock.
	 */
	private final Set<PooledConnection<C>> busyConnections;

	/**
	 * All accesses should be behind poolLock.
	 */
	private boolean isClosed = false;

	/**
	 * All access should be protected by poolLock.
	 */
	private int maxConcurrency = 0;

	/**
	 * The allocation id of the current thread.  This is used as a key in the weak map
	 * {@link #threadConnectionsByThreadId} and is tracked per-connection by {@link #allocationThreadIdByConnection}.
	 * <p>
	 * This approach allows sharing of information between threads, while still allowing garbage collection once a
	 * thread dies.
	 * </p>
	 *
	 * @see  #threadConnectionsByThreadId
	 */
	private final ThreadLocal<Long> currentThreadId = new ThreadLocal<Long>() {

		/**
		 * Incremental allocation of thread IDs, since they are not used outside the scope of this class.
		 * No benefit to randomizing the values.
		 */
		private long lastId = 0;

		@Override
		@SuppressWarnings({"deprecation", "UnnecessaryBoxing"})
		protected Long initialValue() {
			Long id;
			synchronized(threadConnectionsByThreadId) {
				// Wraparound of 64-bit identifier is unlikely, but still make sure is available for correctness
				do {
					id = new Long(++lastId); // Must be a distinct object, since is used a key in weak map
				} while(threadConnectionsByThreadId.containsKey(id));
				threadConnectionsByThreadId.put(id, new ArrayList<>());
			}
			return id;
		}
	};

	/**
	 * Connections that are checked-out by the current thread.
	 * <p>
	 * All access to this map must be synchronized on the map.
	 * </p>
	 * <p>
	 * Furthermore, access to each of the individual lists must be synchronized on the list.  When a connection is
	 * shared between threads, this list will be accessed by multiple threads.
	 * </p>
	 *
	 * @see  #currentThreadId
	 */
	private final Map<Long, List<PooledConnection<C>>> threadConnectionsByThreadId = new WeakHashMap<>();

	/**
	 * Tracks the thread ID that allocated each connection.
	 * <p>
	 * All access to this map must be synchronized on the map.
	 * </p>
	 */
	private final Map<C, Long> allocationThreadIdByConnection = new IdentityHashMap<>();

	/**
	 * All warnings are sent here if available, otherwise will be written to <code>System.err</code>.
	 */
	@SuppressWarnings("NonConstantLogger")
	protected final Logger logger;

	protected AOPool(String name, int poolSize, long maxConnectionAge, Logger logger) {
		this(DEFAULT_DELAY_TIME, DEFAULT_MAX_IDLE_TIME, name, poolSize, maxConnectionAge, logger);
	}

	@SuppressWarnings("unchecked")
	protected AOPool(int delayTime, int maxIdleTime, String name, int poolSize, long maxConnectionAge, Logger logger) {
		super(name+"&delayTime="+delayTime+"&maxIdleTime="+maxIdleTime+"&size="+poolSize+"&maxConnectionAge="+(maxConnectionAge==UNLIMITED_MAX_CONNECTION_AGE?"Unlimited":Long.toString(maxConnectionAge)));
		this.delayTime = delayTime;
		this.maxIdleTime = maxIdleTime;
		this.startTime = System.currentTimeMillis();
		setPriority(Thread.NORM_PRIORITY);
		setDaemon(true);
		this.poolSize = poolSize;
		this.maxConnectionAge = maxConnectionAge;
		if(logger==null) throw new IllegalArgumentException("logger is null");
		this.logger = logger;
		allConnections = new ArrayList<>(poolSize);
		availableConnections = new PriorityQueue<>(poolSize);
		busyConnections = AoCollections.newHashSet(poolSize);
		// TODO: Call start() after construction completed
		start();
	}

	/**
	 * Closes the underlying connection.
	 * The connection may or may not already have been {@linkplain #resetConnection(java.lang.AutoCloseable) reset}.
	 * The connection may or may not already be {@linkplain #isClosed(java.lang.AutoCloseable) closed}.
	 * <p>
	 * Please note, this is distinct from the implementation of {@link AutoCloseable} for use in try-with-resources.
	 * </p>
	 */
	protected abstract void close(C conn) throws Ex;

	/**
	 * Shuts down the pool, exceptions during close will be logged as a warning and not thrown.
	 */
	@SuppressWarnings("UseSpecificCatch")
	final public void close() {
		List<C> connsToClose;
		synchronized(poolLock) {
			try {
				// Prevent any new connections
				isClosed = true;
				// Find any connections that are available and open
				connsToClose = new ArrayList<>(availableConnections.size());
				for(PooledConnection<C> availableConnection : availableConnections) {
					synchronized(availableConnection) {
						C conn = availableConnection.connection;
						if(conn!=null) {
							availableConnection.connection = null;
							connsToClose.add(conn);
						}
					}
				}
			} finally {
				poolLock.notifyAll();
			}
		}
		// Close all of the connections
		for(C conn : connsToClose) {
			try {
				close(conn);
			} catch(ThreadDeath td) {
				throw td;
			} catch(Throwable t) {
				logger.log(Level.WARNING, null, t);
			}
		}
	}

	/**
	 * Gets the number of connections that are currently busy.
	 */
	final public int getConcurrency() {
		synchronized(poolLock) {
			return busyConnections.size();
		}
	}

	/**
	 * Gets the number of connections currently connected.
	 */
	final public int getConnectionCount() {
		int total = 0;
		synchronized(poolLock) {
			for(PooledConnection<C> pooledConnection : allConnections) {
				if(pooledConnection.connection!=null) total++;
			}
		}
		return total;
	}

	/**
	 * Gets either an available connection or creates a new connection,
	 * warning when a connection is already used by this thread.
	 * <p>
	 * If all the connections in the pool are busy and the pool is at capacity, waits until a connection becomes
	 * available.
	 * </p>
	 *
	 * @return  Either a reused or new connection
	 *
	 * @throws  I  when interrupted
	 * @throws  Ex  when an error occurs, or when a thread attempts to allocate more than half the pool
	 *
	 * @see  #getConnection(int)
	 * @see  AutoCloseable#close()
	 */
	// Note:      Is AOPool.getConnection()
	// Note: Matches AOConnectionPool.getConnection()
	// Note: Matches Database.getConnection()
	// Note: Matches DatabaseConnection.getConnection()
	public C getConnection() throws I, Ex {
		return getConnection(1);
	}

	private static long lastLoggedWait = Long.MIN_VALUE;

	/**
	 * Gets either an available connection or creates a new connection.
	 * <p>
	 * If all the connections in the pool are busy and the pool is at capacity, waits until a connection becomes
	 * available.
	 * </p>
	 *
	 * @param  maxConnections  The maximum number of connections expected to be used by the current thread.
	 *                         This should normally be one to avoid potential deadlock.
	 *                         <p>
	 *                         The connection will continue to be considered used by the allocating thread until
	 *                         released (via {@link AutoCloseable#close()}, even if the connection is shared by another
	 *                         thread.
	 *                         </p>
	 *
	 * @return  Either a reused or new connection
	 *
	 * @throws  I  when interrupted
	 * @throws  Ex  when an error occurs, or when a thread attempts to allocate more than half the pool
	 *
	 * @see  #getConnection()
	 * @see  AutoCloseable#close()
	 */
	// Note:      Is AOPool.getConnection(int)
	// Note: Matches AOConnectionPool.getConnection(int)
	// Note: Matches Database.getConnection(int)
	// Note: Matches DatabaseConnection.getConnection(int)
	@SuppressWarnings({"UseSpecificCatch", "AssignmentToCatchBlockParameter"})
	public C getConnection(int maxConnections) throws I, Ex {
		if(maxConnections < 1) maxConnections = 1;
		// Return immediately if already interrupted
		if(Thread.interrupted()) throw newInterruptedException(null, null);

		Thread thisThread = Thread.currentThread();
		Long threadId;
		List<PooledConnection<C>> threadConnections;
		synchronized(threadConnectionsByThreadId) {
			threadId = currentThreadId.get();
			threadConnections = threadConnectionsByThreadId.get(threadId);
		}
		assert threadConnections != null : "The list of connections per-thread is added to map during ThreadLocal.initialValue()";

		synchronized(threadConnections) {
			// Error or warn if this thread already has too many connections
			int useCount = threadConnections.size();
			if(useCount >= maxConnections) {
				Throwable[] allocateStackTraces = new Throwable[useCount];
				for(int c = 0; c < useCount; c++) {
					allocateStackTraces[c] = threadConnections.get(c).allocateStackTrace;
				}
				// Throw an exception if over half the pool is used by this thread
				int halfPool = poolSize / 2;
				if(halfPool < 1) halfPool = 1; // Unlikely case of one-connection pool
				if(useCount >= halfPool) {
					throw newException(
						"Thread attempting to allocate more than half of the connection pool: " + thisThread.toString(),
						new WrappedExceptions(allocateStackTraces)
					);
				}
				logger.logp(
					Level.WARNING,
					AOPool.class.getName(),
					"getConnection",
					null,
					new WrappedExceptions(
						"Warning: Thread allocated more than " + maxConnections + " "
							+ (maxConnections == 1 ? "connection" : "connections")
							+ ".  The stack trace at allocation time is included for each connection.",
						allocateStackTraces
					)
				);
			}
		}
		// Find an available pooledConnection inside poolLock, actually connect outside poolLock below
		PooledConnection<C> pooledConnection;
		synchronized(poolLock) {
			try {
				do {
					if(Thread.interrupted()) throw newInterruptedException(null, null);

					if(allConnections.size() != (availableConnections.size() + busyConnections.size())) throw new AssertionError("allConnections.size!=(availableConnections.size+busyConnections.size)");
					if(isClosed) throw newException("Pool is closed", null);
					if(!availableConnections.isEmpty()) {
						pooledConnection = availableConnections.remove();
						busyConnections.add(pooledConnection);
					} else {
						// Nothing available, is there room to make a new connection?
						if(allConnections.size()<poolSize) {
							// Create a new one
							pooledConnection = new PooledConnection<>();
							allConnections.add(pooledConnection);
							busyConnections.add(pooledConnection);
						} else {
							// Wait for a connection to become available
							pooledConnection = null;
							if(logger.isLoggable(Level.WARNING)) {
								long currentTime = System.currentTimeMillis();
								if(
									lastLoggedWait == Long.MIN_VALUE
									|| (currentTime - lastLoggedWait) >= WAIT_LOGGING_INTERVAL
									|| (lastLoggedWait - currentTime) >= WAIT_LOGGING_INTERVAL // System time reset into the past
								) {
									String eol = System.lineSeparator();
									StringBuilder message = new StringBuilder();
									message.append("Warning connection pool is full.  Please review the stacktraces of all allocations:");
									for(int i = 0, size = allConnections.size(); i < size ; i++) {
										PooledConnection<C> pc = allConnections.get(i);
										Throwable ast = pc.allocateStackTrace;
										message.append(eol).append(eol).append("Connection #").append(i + 1).append(eol);
										if(ast == null) {
											message.append("    No allocation registered.");
										} else {
											StackTraceElement[] stack = ast.getStackTrace();
											if(stack == null || stack.length == 0) {
												message.append("    No stack trace.");
											} else {
												for(StackTraceElement ste : stack) {
													message.append(eol).append("    at ").append(ste.toString());
												}
											}
										}
									}
									logger.log(Level.WARNING, message.toString());
									lastLoggedWait = currentTime;
								}
							}
							try {
								poolLock.wait();
							} catch(InterruptedException err) {
								throw newInterruptedException(null, err);
							}
						}
					}
				} while(pooledConnection == null);
				// Keep track of the maximum concurrency hit
				int concurrency = busyConnections.size();
				if(concurrency>maxConcurrency) maxConcurrency=concurrency;
				// Notify any others that may be waiting
			} finally {
				poolLock.notify();
			}
		}
		synchronized(threadConnections) {
			threadConnections.add(pooledConnection);
		}
		// If anything goes wrong during the remainder of this method, need to release the connection
		try {
			// Now that the pooledConnection is allocated, create/reuse the connection outside poolLock
			long currentTime = System.currentTimeMillis();
			C conn;
			synchronized(pooledConnection) {
				pooledConnection.startTime = currentTime;
				conn = pooledConnection.connection;
			}
			boolean doReset;
			if(conn==null || isClosed(conn)) {
				// Connect without holding lock.
				conn = getConnectionObject();
				// Close new connection if the pool was closed during connect
				boolean myIsClosed;
				synchronized(poolLock) {
					myIsClosed = isClosed;
				}
				if(myIsClosed) {
					close(conn);
					throw newException("Pool is closed", null);
				}
				synchronized(pooledConnection) {
					pooledConnection.connection = conn;
					pooledConnection.createTime = currentTime;
					pooledConnection.connectCount.incrementAndGet();
				}
				doReset=true;
			} else {
				// Was already reset when released
				doReset=false;
			}
			// TODO: Measure time used for creating this stack trace.  Is it worth it?
			Throwable allocateStackTrace = new Throwable("StackTrace at getConnection(" + maxConnections + ") for Thread named \"" + thisThread.getName() + "\"");
			synchronized(pooledConnection) {
				pooledConnection.releaseTime = 0;
				pooledConnection.useCount.incrementAndGet();
				pooledConnection.allocateStackTrace = allocateStackTrace;
			}
			if(doReset) resetConnection(conn);
			synchronized(allocationThreadIdByConnection) {
				allocationThreadIdByConnection.put(conn, threadId);
			}
			return conn;
		} catch(Throwable t0) {
			try {
				C conn;
				synchronized(pooledConnection) {
					conn = pooledConnection.connection;
					pooledConnection.connection = null;
				}
				if(conn != null) {
					synchronized(allocationThreadIdByConnection) {
						allocationThreadIdByConnection.remove(conn);
					}
					try {
						close(conn);
					} catch(Throwable t) {
						t0 = Throwables.addSuppressed(t0, t);
					}
				}
			} finally {
				try {
					synchronized(threadConnections) {
						threadConnections.remove(pooledConnection);
					}
					release(pooledConnection);
				} catch(Throwable t) {
					t0 = Throwables.addSuppressed(t0, t);
				}
			}
			if(t0 instanceof Error) throw (Error)t0;
			if(t0 instanceof RuntimeException) throw (RuntimeException)t0;
			throw newException(null, t0);
		}
	}

	/**
	 * Creates a new connection.
	 * <p>
	 * The returned connection must call {@link #release(java.lang.AutoCloseable)} on
	 * {@link AutoCloseable#close()}.  This is to support use via try-with-resources, and is
	 * distinct from {@link #isClosed(java.lang.AutoCloseable)} and {@link #close(java.lang.AutoCloseable)}, which must
	 * both work with the underlying connection.
	 * </p>
	 *
	 * @throws I when interrupted
	 * @throws Ex when error
	 */
	protected abstract C getConnectionObject() throws I, Ex;

	/**
	 * Releases a PooledConnection.  It is safe to release
	 * it multiple times.  The connection should have either
	 * been closed or reset before this is called because
	 * this makes the connection available for the next request.
	 */
	private void release(PooledConnection<C> pooledConnection) {
		try {
			long currentTime = System.currentTimeMillis();
			long useTime;
			synchronized(pooledConnection) {
				pooledConnection.releaseTime = currentTime;
				useTime = currentTime - pooledConnection.startTime;
				if(useTime>0) pooledConnection.totalTime.addAndGet(useTime);
				pooledConnection.allocateStackTrace = null;
			}
		} finally {
			// Remove from the pool
			synchronized(poolLock) {
				try {
					if(busyConnections.remove(pooledConnection)) availableConnections.add(pooledConnection);
				} finally {
					poolLock.notify();
				}
			}
		}
	}

	/**
	 * Gets the total number of connects for the entire pool.
	 */
	final public long getConnects() {
		long total = 0;
		synchronized(poolLock) {
			for(PooledConnection<C> conn : allConnections) {
				total += conn.connectCount.get();
			}
		}
		return total;
	}

	/**
	 * Gets the maximum age for connections.
	 */
	final public long getMaxConnectionAge() {
		return maxConnectionAge;
	}

	/**
	 * Gets the maximum number of connections that have been busy at once.
	 */
	final public int getMaxConcurrency() {
		synchronized(poolLock) {
			return maxConcurrency;
		}
	}

	/**
	 * Gets the maximum number of connections the pool will create at once.
	 */
	final public int getPoolSize() {
		return poolSize;
	}

	final public long getTotalTime() {
		long total = 0;
		synchronized(poolLock) {
			for(PooledConnection<C> conn : allConnections) {
				total += conn.totalTime.get();
			}
		}
		return total;
	}

	final public long getTransactionCount() {
		long total = 0;
		synchronized(poolLock) {
			for(PooledConnection<C> conn : allConnections) {
				total += conn.useCount.get();
			}
		}
		return total;
	}

	/**
	 * Determine if the underlying connection is closed.
	 * <p>
	 * Please note, this is distinct from the implementation of {@link AutoCloseable} for use in try-with-resources.
	 * </p>
	 */
	protected abstract boolean isClosed(C conn) throws Ex;

	/**
	 * Prints additional connection pool details.  Must have opened the <code>&lt;tbody&gt;</code>.
	 */
	protected void printConnectionStats(Appendable out, boolean isXhtml) throws IOException {
		out.append("  <tbody>\n");
	}

	/**
	 * Prints complete statistics about connection pool use.
	 */
	@SuppressWarnings("deprecation")
	public final void printStatisticsHTML(Appendable out, boolean isXhtml) throws IOException, Ex {
		// Get the data
		boolean myIsClosed;
		synchronized(poolLock) {
			myIsClosed = isClosed;
		}
		// Don't write while holding the lock to avoid possible blocking
		int numConnections;
		boolean[] isConnecteds;
		long[] createTimes;
		long[] connectCounts;
		long[] useCounts;
		long[] totalTimes;
		boolean[] isBusies;
		Long[] allocationThreadIds;
		long[] startTimes;
		long[] releaseTimes;
		Throwable[] allocateStackTraces;
		synchronized(poolLock) {
			numConnections = allConnections.size();
			isConnecteds = new boolean[numConnections];
			createTimes = new long[numConnections];
			connectCounts = new long[numConnections];
			useCounts = new long[numConnections];
			totalTimes = new long[numConnections];
			isBusies = new boolean[numConnections];
			allocationThreadIds = new Long[numConnections];
			startTimes = new long[numConnections];
			releaseTimes = new long[numConnections];
			allocateStackTraces = new Throwable[numConnections];
			for(int c=0;c<numConnections;c++) {
				PooledConnection<C> pooledConnection = allConnections.get(c);
				isConnecteds[c] = pooledConnection.connection != null;
				createTimes[c] = pooledConnection.createTime;
				connectCounts[c] = pooledConnection.connectCount.get();
				useCounts[c] = pooledConnection.useCount.get();
				totalTimes[c] = pooledConnection.totalTime.get();
				isBusies[c] = busyConnections.contains(pooledConnection);
				C connection;
				synchronized(pooledConnection) {
					connection = pooledConnection.connection;
				}
				Long allocationThreadId;
				if(connection == null) {
					allocationThreadId = null;
				} else {
					synchronized(allocationThreadIdByConnection) {
						allocationThreadId = allocationThreadIdByConnection.get(connection);
					}
				}
				allocationThreadIds[c] = allocationThreadId;
				startTimes[c] = pooledConnection.startTime;
				releaseTimes[c] = pooledConnection.releaseTime;
				allocateStackTraces[c] = pooledConnection.allocateStackTrace;
			}
		}
		long time = System.currentTimeMillis();
		long timeLen = time-startTime;

		// Print the stats
		out.append("<table class=\"ao-grid\">\n");
		printConnectionStats(out, isXhtml);
		out.append("    <tr><td>Max Connection Pool Size:</td><td>").append(Integer.toString(poolSize)).append("</td></tr>\n"
				+ "    <tr><td>Connection Clean Interval:</td><td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(Strings.getDecimalTimeLengthString(delayTime), out, isXhtml);
		out.append("</td></tr>\n"
				+ "    <tr><td>Max Idle Time:</td><td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(Strings.getDecimalTimeLengthString(maxIdleTime), out, isXhtml);
		out.append("</td></tr>\n"
				+ "    <tr><td>Max Connection Age:</td><td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(maxConnectionAge==UNLIMITED_MAX_CONNECTION_AGE?"Unlimited":Strings.getDecimalTimeLengthString(maxConnectionAge), out, isXhtml);
		out.append("</td></tr>\n"
				+ "    <tr><td>Is Closed:</td><td>").append(Boolean.toString(myIsClosed)).append("</td></tr>\n"
				+ "  </tbody>\n"
				+ "</table>\n");
		if(isXhtml) out.append("<br /><br />\n");
		else out.append("<br><br>\n");
		out.append("<table class=\"ao-grid\">\n"
				+ "  <thead>\n"
				+ "    <tr><th colspan=\"11\"><span style=\"font-size:large\">Connections</span></th></tr>\n"
				+ "    <tr>\n"
				+ "      <th>Connection #</th>\n"
				+ "      <th>Is Connected</th>\n"
				+ "      <th>Conn Age</th>\n"
				+ "      <th>Conn Count</th>\n"
				+ "      <th>Use Count</th>\n"
				+ "      <th>Total Time</th>\n"
				+ "      <th>% of Time</th>\n"
				+ "      <th>State</th>\n"
				+ "      <th>State Time</th>\n"
				+ "      <th>Ave Trans Time</th>\n"
				+ "      <th>Stack Trace</th>\n"
				+ "    </tr>\n"
				+ "  </thead>\n"
				+ "  <tbody>\n");

		int totalConnected = 0;
		long totalConnects = 0;
		long totalUses = 0;
		long totalTotalTime = 0;
		int totalBusy = 0;

		for(int c=0;c<numConnections;c++) {
			long connCount = connectCounts[c];
			boolean isConnected = isConnecteds[c];
			long useCount = useCounts[c];
			long totalTime = totalTimes[c];
			boolean isBusy = isBusies[c];
			if(isBusy) totalTime += time - startTimes[c];
			long stateTime = isBusy ? (time-startTimes[c]):(time-releaseTimes[c]);
			Long allocationThreadId = allocationThreadIds[c];
			out.append("    <tr>\n"
					+ "      <td>").append(Integer.toString(c+1)).append("</td>\n"
					+ "      <td>").append(isConnected?"Yes":"No").append("</td>\n"
					+ "      <td>");
			if(isConnected) com.aoindustries.util.EncodingUtils.encodeHtml(Strings.getDecimalTimeLengthString(time-createTimes[c]), out, isXhtml);
			else out.append("&#160;");
			out.append("</td>\n"
					+ "      <td>").append(Long.toString(connCount)).append("</td>\n"
					+ "      <td>").append(Long.toString(useCount)).append("</td>\n"
					+ "      <td>");
			com.aoindustries.util.EncodingUtils.encodeHtml(Strings.getDecimalTimeLengthString(totalTime), out, isXhtml);
			out.append("</td>\n"
					+ "      <td>").append(Float.toString(totalTime*100/(float)timeLen)).append("%</td>\n"
					+ "      <td>").append(isBusy?"In Use":isConnected?"Idle":"Closed");
			if(allocationThreadId != null) out.append(" by Thread #").append(allocationThreadId.toString());
			out.append("</td>\n"
					+ "      <td>");
			com.aoindustries.util.EncodingUtils.encodeHtml(Strings.getDecimalTimeLengthString(stateTime), out, isXhtml);
			out.append("</td>\n"
					+ "      <td>").append(Long.toString(totalTime*1000/useCount)).append("&#181;s</td>\n"
					+ "      <td>");
			Throwable T = allocateStackTraces[c];
			if(T == null) out.append("&#160;");
			else {
				out.append("        <a href=\"#\" onclick='var elem = document.getElementById(\"stack_").append(Integer.toString(c)).append("\").style; elem.visibility=(elem.visibility==\"visible\" ? \"hidden\" : \"visible\"); return false;'>Stack Trace</a>\n"
						+ "        <span id=\"stack_").append(Integer.toString(c)).append("\" style=\"text-align:left; white-space:nowrap; position:absolute; visibility: hidden; z-index:").append(Integer.toString(c+1)).append("\">\n"
						+ "          <pre style=\"text-align:left; background-color:white; border: 2px solid; border-color: black\">\n");
				ErrorPrinter.printStackTraces(T, out);
				out.append("          </pre>\n"
						+ "        </span>\n");
			}
			out.append("</td>\n"
					+ "    </tr>\n");

			// Update totals
			if(isConnected) totalConnected++;
			totalConnects+=connCount;
			totalUses+=useCount;
			totalTotalTime+=totalTime;
			if(isBusy) totalBusy++;
		}
		out.append("  </tbody>\n"
				+ "  <tfoot>\n"
				+ "    <tr>\n"
				+ "      <td><b>Total</b></td>\n"
				+ "      <td>").append(Integer.toString(totalConnected)).append("</td>\n"
				+ "      <td>&#160;</td>\n"
				+ "      <td>").append(Long.toString(totalConnects)).append("</td>\n"
				+ "      <td>").append(Long.toString(totalUses)).append("</td>\n"
				+ "      <td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(Strings.getDecimalTimeLengthString(totalTotalTime), out, isXhtml);
		out.append("</td>\n"
				+ "      <td>").append(Float.toString(timeLen==0 ? 0 : (totalTotalTime*100/(float)timeLen))).append("%</td>\n"
				+ "      <td>").append(Integer.toString(totalBusy)).append("</td>\n"
				+ "      <td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(Strings.getDecimalTimeLengthString(timeLen), out, isXhtml);
		out.append("</td>\n"
				+ "      <td>").append(Long.toString(totalUses==0 ? 0 : (totalTotalTime*1000/totalUses))).append("&#181;s</td>\n"
				+ "      <td>&#160;</td>\n"
				+ "    </tr>\n"
				+ "  </tfoot>\n"
				+ "</table>\n");
	}

	/**
	 * Prints complete statistics about connection pool use in XHTML.
	 *
	 * @deprecated  Please specify if is HTML or XHTML
	 */
	@Deprecated
	@SuppressWarnings("NoopMethodInAbstractClass")
	public final void printStatisticsHTML(Appendable out) throws IOException, Ex {
		
	}

	/**
	 * @deprecated  Please release to the pool by {@linkplain AutoCloseable#close() closing the connection},
	 *              preferably via try-with-resources.
	 *
	 * @see  AutoCloseable#close()
	 */
	@Deprecated
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	final public void releaseConnection(C connection) throws Ex {
		try {
			connection.close();
		} catch(Throwable t) {
			if(t instanceof Error) throw (Error)t;
			if(t instanceof RuntimeException) throw (RuntimeException)t;
			throw newException(null, t);
		}
	}

	/**
	 * Releases the database <code>Connection</code> to the <code>Connection</code> pool.
	 * <p>
	 * It is safe to call this method more than once, but only the first call will have any affect.
	 * </p>
	 * <p>
	 * If the connection is not from this pool, no action is taken.
	 * </p>
	 * <p>
	 * The connection will be {@linkplain #resetConnection(java.lang.AutoCloseable) reset} and/or
	 * {@linkplain #close(java.lang.AutoCloseable) closed}.
	 * </p>
	 *
	 * @see  #isClosed(java.lang.AutoCloseable)
	 * @see  #logConnection(java.lang.AutoCloseable)
	 * @see  #resetConnection(java.lang.AutoCloseable)
	 * @see  #close(java.lang.AutoCloseable)
	 * @see  #release(com.aoindustries.io.AOPool.PooledConnection)
	 */
	@SuppressWarnings({"UseSpecificCatch", "NestedSynchronizedStatement"})
	protected void release(C connection) throws Ex {
		// Find the threadId that had allocated the connection
		// Will not be found when already released (or not from this pool)
		Long allocationThreadId;
		synchronized(allocationThreadIdByConnection) {
			allocationThreadId = allocationThreadIdByConnection.remove(connection);
		}
		if(allocationThreadId != null) {
			// Find the set of all connections currently allocated by the allocating thread
			List<PooledConnection<C>> threadConnections;
			synchronized(threadConnectionsByThreadId) {
				threadConnections = threadConnectionsByThreadId.get(allocationThreadId);
			}
			if(threadConnections == null) throw new AssertionError("The list of connections per-thread should not have been garbage collected, since holding a reference to identifier in allocationThreadIdByConnection");
			// Find the PooledConnection for this Connection
			PooledConnection<C> pooledConnection = null;
			synchronized(threadConnections) {
				// Search backwards, since when multiple connections are allocated, they are usually released in opposite order in try-with-resources
				for(int c = threadConnections.size() - 1; c >= 0; c--) {
					PooledConnection<C> threadConnection = threadConnections.get(c);
					synchronized(threadConnection) {
						if(threadConnection.connection == connection) {
							pooledConnection = threadConnection;
							threadConnections.remove(c);
							break;
						}
					}
				}
			}
			if(pooledConnection == null) throw new AssertionError("PooledConnection not found by allocationThreadId");
			try {
				Throwable t0 = null;
				boolean closeConnection = false;
				boolean connIsClosed;
				try {
					connIsClosed = isClosed(connection);
				} catch(Throwable t) {
					t0 = Throwables.addSuppressed(t0, t);
					connIsClosed = false;
					closeConnection = true; // Force closure due to error on isClosed
				}
				if(connIsClosed) {
					// Already closed
					synchronized(pooledConnection) {
						pooledConnection.connection = null;
					}
				} else {
					if(!closeConnection && maxConnectionAge != UNLIMITED_MAX_CONNECTION_AGE) {
						long age = System.currentTimeMillis() - pooledConnection.createTime;
						// Allow time range, in case of system time resets
						closeConnection = (age <= -maxConnectionAge) || (age >= maxConnectionAge);
					}
					// Log warnings before release and/or close
					try {
						logConnection(connection);
					} catch(Throwable t) {
						t0 = Throwables.addSuppressed(t0, t);
						// Close the connection when error during logging
						closeConnection = true;
					}
					if(!closeConnection) {
						// Reset connections as they are released
						try {
							resetConnection(connection);
						} catch(Throwable t) {
							t0 = Throwables.addSuppressed(t0, t);
							// Close the connection when error during reset
							closeConnection = true;
						}
					}
					if(closeConnection) {
						// Error or max age reached, close the connection
						try {
							close(connection);
						} catch(Throwable t) {
							t0 = Throwables.addSuppressed(t0, t);
						}
						synchronized(pooledConnection) {
							pooledConnection.connection = null;
						}
					}
				}
				if(t0 != null) {
					if(t0 instanceof Error) throw (Error)t0;
					if(t0 instanceof RuntimeException) throw (RuntimeException)t0;
					throw newException(null, t0);
				}
			} finally {
				// Unallocate the connection from the pool
				release(pooledConnection);
			}
		}
	}

	/**
	 * Perform any connection logging before {@link #resetConnection(java.lang.AutoCloseable)} and/or
	 * {@link #close(java.lang.AutoCloseable)}.  This is only called on connections that are not
	 * {@linkplain #isClosed(java.lang.AutoCloseable) closed}.
	 */
	@SuppressWarnings("NoopMethodInAbstractClass")
	protected void logConnection(C conn) throws Ex {
		// Nothing by default
	}

	/**
	 * Resets the given connection for release back to the pool.
	 */
	protected abstract void resetConnection(C conn) throws I, Ex;

	/**
	 * The RefreshConnection thread polls every connection in the connection pool. If it
	 * detects a connection is idle for more than the pre-defined MAX_IDLE_TIME, it closes
	 * the connection.  It will stop when the pool is flagged as closed.
	 */
	@Override
	@SuppressWarnings({"SleepWhileInLoop", "UseSpecificCatch", "TooBroadCatch"})
	final public void run() {
		while(true) {
			try {
				try {
					sleep(delayTime);
				} catch(InterruptedException err) {
					logger.log(Level.WARNING, null, err);
				}
				long time = System.currentTimeMillis();
				List<C> connsToClose;
				synchronized(poolLock) {
					if(isClosed) return;
					// Find any connections that are available and been idle too long
					int maxIdle = maxIdleTime;
					connsToClose = new ArrayList<>(availableConnections.size());
					try {
						for(PooledConnection<C> availableConnection : availableConnections) {
							synchronized(availableConnection) {
								C conn = availableConnection.connection;
								if(conn!=null) {
									if(
										(time-availableConnection.releaseTime) > maxIdle // Idle too long
										|| (
											maxConnectionAge!=UNLIMITED_MAX_CONNECTION_AGE
											&& (
												availableConnection.createTime > time // System time reset?
												|| (time-availableConnection.createTime) >= maxConnectionAge // Max connection age reached
											)
										)
									) {
										availableConnection.connection = null;
										connsToClose.add(conn);
									}
								}
							}
						}
					} finally {
						if(!connsToClose.isEmpty()) poolLock.notify();
					}
				}
				// Close all of the connections
				for(C conn : connsToClose) {
					try {
						close(conn);
					} catch(ThreadDeath td) {
						throw td;
					} catch(Throwable t) {
						logger.log(Level.WARNING, null, t);
					}
				}
			} catch (ThreadDeath td) {
				throw td;
			} catch (Throwable t) {
				logger.log(Level.SEVERE, null, t);
			}
		}
	}

	protected abstract Ex newException(String message, Throwable cause);

	protected abstract I newInterruptedException(String message, Throwable cause);

	final public Logger getLogger() {
		return logger;
	}
}
