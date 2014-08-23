/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.io;

import com.aoindustries.util.EncodingUtils;
import com.aoindustries.util.ErrorPrinter;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedExceptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable generic connection pooling with dynamic flaming tiger feature.
 *
 * Two lists of connections are maintained.  The first is the list of connections
 * that are ready unused, and the second is the list of connections that are
 * currently checked-out.  By using this strategy, an available connection
 * can be found without searching the entire list.
 *
 * In addition to the global lists, a <code>ThreadLocal</code> list of connections
 * checked-out by the current thread is maintained.  When getting a new connection,
 * this is used to check again <code>maxConnections</code> instead of checking
 * the global lists.
 *
 * Idea: Add some sort of thread-connection affinity, where the same connection
 *       slot will be used by the same thread when it is available.  This should
 *       help cache locality.
 *
 * Idea: When connections are closed due to maxConnectionAge, automatically reconnect
 *       in the background.  This would avoid the latency of the reconnect for the
 *       connection-using thread.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AOPool<C,E extends Exception,I extends Exception> extends Thread {

    public static final int DEFAULT_DELAY_TIME = 1 * 60 * 1000;
    public static final int DEFAULT_MAX_IDLE_TIME = 10 * 60 * 1000;

    public static final long UNLIMITED_MAX_CONNECTION_AGE = -1;
    public static final long DEFAULT_MAX_CONNECTION_AGE = 30L * 60 * 1000;

    public static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000; // Was 5 seconds for a very long time, but too sensitive to transient network problems
    public static final int DEFAULT_SOCKET_SO_LINGER = 15;

    /**
     * All access to the fields should be synchronized on the <code>PooledConnection</code> instance.
     */
    private static class PooledConnection<C> implements Comparable<PooledConnection<C>> {

        private static AtomicLong nextId = new AtomicLong(0);

        final long id = nextId.getAndIncrement();

        /**
         * The current connection.
         */
        C connection;

        /**
         * The time the connection was created
         */
        long createTime;

        /**
         * Total time using the connection
         */
        long totalTime;

        /**
         * The time getting the connection from the pool
         */
        long startTime;

        /**
         * The time returning the connection to the pool
         */
        long releaseTime;

        /**
         * Counts the number of times the connection is connected
         */
        long connectCount;

        /**
         * Counts the number of times the connection is used
         */
        long useCount;

        /**
         * Keeps track of the stack trace at checkout for this connection.
         */
        Throwable allocateStackTrace;

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
    private final Object poolLock = new Object();

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
     * Connections that are checked-out by the current thread.
     */
    private final ThreadLocal<List<PooledConnection<C>>> currentThreadConnections = new ThreadLocal<List<PooledConnection<C>>>() {
        @Override
        public List<PooledConnection<C>> initialValue() {
            return new ArrayList<PooledConnection<C>>();
        }
    };

    /**
     * All warnings are sent here if available, otherwise will be written to <code>System.err</code>.
     */
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
        allConnections = new ArrayList<PooledConnection<C>>(poolSize);
        availableConnections = new PriorityQueue<PooledConnection<C>>(poolSize);
        busyConnections = new HashSet<PooledConnection<C>>(poolSize*4/3+1);
        start();
    }

    protected abstract void close(C conn) throws E;

    /**
     * Shuts down the pool, exceptions during close will be logged as a warning and not thrown.
     */
    final public void close() {
        List<C> connsToClose;
        synchronized(poolLock) {
            // Prevent any new connections
            isClosed = true;
            // Find any connections that are available and open
            connsToClose = new ArrayList<C>(availableConnections.size());
            for(PooledConnection<C> availableConnection : availableConnections) {
                synchronized(availableConnection) {
                    C conn = availableConnection.connection;
                    if(conn!=null) {
                        availableConnection.connection = null;
                        connsToClose.add(conn);
                    }
                }
            }
            poolLock.notifyAll();
        }
        // Close all of the connections
        for(C conn : connsToClose) {
            try {
                close(conn);
            } catch(Exception err) {
                logger.log(Level.WARNING, null, err);
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
                synchronized(pooledConnection) {
                    if(pooledConnection.connection!=null) total++;
                }
            }
        }
        return total;
    }

    /**
     * Gets a connection, warning of a connection is already used by this thread.
     *
     * @see  #getConnection(int)
     *
     * @throws I when interrupted
     * @throws E when error
     */
    public C getConnection() throws I, E {
        return getConnection(1);
    }

    /**
     * Gets either an available connection or creates a new connection.  If all the
     * connections in the pool are busy and the pool is at capacity, waits until a
     * connection becomes available.  Due to internal <code>ThreadLocal</code>
     * optimizations, the connection returned must be released by the current thread,
     * it should not be passed off to another thread before release.
     *
     * @param  maxConnections  The maximum number of connections expected to be used by the current thread.
     *                         This should normally be one to avoid potential deadlock.
     *
     * @return either a reused or new connection
     *
     * @throws I when interrupted
     * @throws E when error
     */
    public C getConnection(int maxConnections) throws I, E {
        // Return immediately if already interrupted
        if(Thread.interrupted()) throw newInterruptedException(null, null);

        Thread thisThread = Thread.currentThread();
        // Error or warn if this thread already has too many connections
        List<PooledConnection<C>> threadConnections = currentThreadConnections.get();
        int useCount = threadConnections.size();
        if(useCount>=maxConnections) {
            Throwable[] allocateStackTraces = new Throwable[useCount];
            for(int c=0; c<useCount; c++) {
                PooledConnection<C> threadConnection = threadConnections.get(c);
                synchronized(threadConnection) {
                    allocateStackTraces[c] = threadConnection.allocateStackTrace;
                }
            }
            // Throw an exception if over half the pool is used by this thread
            if(useCount>=(poolSize/2)) throw newException("Thread attempting to allocate more than half of the connection pool: "+thisThread.toString(), new WrappedExceptions(allocateStackTraces));
            logger.logp(
                Level.WARNING,
                AOPool.class.getName(),
                "getConnection",
                null,
                new WrappedExceptions(
                    "Warning: Thread allocated more than "+maxConnections+" "+(maxConnections==1 ? "connection" : "connections")+".  The stack trace at allocation time is included.",
                    allocateStackTraces
                )
            );
        }
        // Find an available pooledConnection inside poolLock, actually connect outside poolLock below
        PooledConnection<C> pooledConnection = null;
        synchronized(poolLock) {
            while(pooledConnection==null) {
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
                        pooledConnection = new PooledConnection<C>();
                        allConnections.add(pooledConnection);
                        busyConnections.add(pooledConnection);
                    } else {
                        // Wait for a connection to become available
                        try {
                            poolLock.wait();
                        } catch(InterruptedException err) {
                            throw newInterruptedException(null, err);
                        }
                    }
                }
            }
            // Keep track of the maximum concurrency hit
            int concurrency = busyConnections.size();
            if(concurrency>maxConcurrency) maxConcurrency=concurrency;
        }
        threadConnections.add(pooledConnection);
        // If anything goes wrong during the remainder of this method, need to release the connection
        boolean successful = false;
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
                synchronized(pooledConnection) {
                    conn = pooledConnection.connection = getConnectionObject();
                    pooledConnection.createTime = currentTime;
                    pooledConnection.connectCount++;
                }
                doReset=true;
            } else {
                // Was already reset when released
                doReset=false;
            }
            Throwable allocateStackTrace = new Throwable("StackTrace at getConnection(" + maxConnections + ") for Thread named \"" + thisThread.getName() + "\"");
            synchronized(pooledConnection) {
                pooledConnection.releaseTime = 0;
                pooledConnection.useCount++;
                pooledConnection.allocateStackTrace = allocateStackTrace;
            }
            if(doReset) resetConnection(conn);
            successful = true;
            return conn;
        } finally {
            if(!successful) {
                try {
                    C conn;
                    synchronized(pooledConnection) {
                        conn = pooledConnection.connection;
                        pooledConnection.connection = null;
                    }
                    if(conn!=null) {
                        try {
                            close(conn);
                        } catch(Exception err) {
                            logger.log(Level.WARNING, null, err);
                        }
                    }
                } finally {
                    threadConnections.remove(pooledConnection);
                    release(pooledConnection);
                }
            }
        }
    }

    /**
     * Creates a new connection.
     *
     * @throws I when interrupted
     * @throws E when error
     */
    protected abstract C getConnectionObject() throws I, E;

    /**
     * Releases a PooledConnection.  It is safe to release
     * it multiple times.  The connection should have either
     * been closed or reset before this is called because
     * this makes the connection available for the next request.
     */
    private void release(PooledConnection<C> pooledConnection) {
        long currentTime = System.currentTimeMillis();
        long useTime;
        synchronized(pooledConnection) {
            pooledConnection.releaseTime = currentTime;
            useTime = currentTime - pooledConnection.startTime;
            if(useTime>0) pooledConnection.totalTime += useTime;
            pooledConnection.allocateStackTrace = null;
        }
        // Remove from the pool
        synchronized(poolLock) {
            if(busyConnections.remove(pooledConnection)) availableConnections.add(pooledConnection);
            poolLock.notify();
        }
    }

    /**
     * Gets the total number of connects for the entire pool.
     */
    final public long getConnects() {
        long total = 0;
        synchronized(poolLock) {
            for(PooledConnection conn : allConnections) {
                synchronized(conn) {
                    total += conn.connectCount;
                }
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

    final public int getMaxConcurrency() {
        synchronized(poolLock) {
            return maxConcurrency;
        }
    }

    final public int getPoolSize() {
        return poolSize;
    }

    final public long getTotalTime() {
        long total = 0;
        synchronized(poolLock) {
            for(PooledConnection conn : allConnections) {
                synchronized(conn) {
                    total += conn.totalTime;
                }
            }
        }
        return total;
    }

    final public long getTransactionCount() {
        long total = 0;
        synchronized(poolLock) {
            for(PooledConnection conn : allConnections) {
                synchronized(conn) {
                    total += conn.useCount;
                }
            }
        }
        return total;
    }

    protected abstract boolean isClosed(C conn) throws E;

    /**
     * Prints additional connection pool details.
     */
    protected abstract void printConnectionStats(Appendable out) throws IOException;

    /**
     * Prints complete statistics about connection pool use.
     */
    public final void printStatisticsHTML(Appendable out) throws IOException, E {
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
            startTimes = new long[numConnections];
            releaseTimes = new long[numConnections];
            allocateStackTraces = new Throwable[numConnections];
            for(int c=0;c<numConnections;c++) {
                PooledConnection<C> pooledConnection = allConnections.get(c);
                synchronized(pooledConnection) {
                    isConnecteds[c] = pooledConnection.connection != null;
                    createTimes[c] = pooledConnection.createTime;
                    connectCounts[c] = pooledConnection.connectCount;
                    useCounts[c] = pooledConnection.useCount;
                    totalTimes[c] = pooledConnection.totalTime;
                    isBusies[c] = busyConnections.contains(pooledConnection);
                    startTimes[c] = pooledConnection.startTime;
                    releaseTimes[c] = pooledConnection.releaseTime;
                    allocateStackTraces[c] = pooledConnection.allocateStackTrace;
                }
            }
        }
        long time = System.currentTimeMillis();
        long timeLen = time-startTime;

        // Print the stats
        out.append("<table style='border:1px;' cellspacing='0' cellpadding='2'>\n");
        printConnectionStats(out);
        out.append("  <tr><td>Max Connection Pool Size:</td><td>").append(Integer.toString(poolSize)).append("</td></tr>\n"
                + "  <tr><td>Connection Clean Interval:</td><td>");
        EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(delayTime), out);
        out.append("</td></tr>\n"
                + "  <tr><td>Max Idle Time:</td><td>");
        EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(maxIdleTime), out);
        out.append("</td></tr>\n"
                + "  <tr><td>Max Connection Age:</td><td>");
        EncodingUtils.encodeHtml(maxConnectionAge==UNLIMITED_MAX_CONNECTION_AGE?"Unlimited":StringUtility.getDecimalTimeLengthString(maxConnectionAge), out);
        out.append("</td></tr>\n"
                + "  <tr><td>Is Closed:</td><td>").append(Boolean.toString(myIsClosed)).append("</td></tr>\n"
                + "</table>\n"
                + "<br /><br />\n"
                + "<table style='border:1px;' cellspacing='0' cellpadding='2'>\n"
                + "  <tr><th colspan='11'><span style='font-size:large;'>Connections</span></th></tr>\n"
                + "  <tr>\n"
                + "    <th>Connection #</th>\n"
                + "    <th>Is Connected</th>\n"
                + "    <th>Conn Age</th>\n"
                + "    <th>Conn Count</th>\n"
                + "    <th>Use Count</th>\n"
                + "    <th>Total Time</th>\n"
                + "    <th>% of Time</th>\n"
                + "    <th>State</th>\n"
                + "    <th>State Time</th>\n"
                + "    <th>Ave Trans Time</th>\n"
                + "    <th>Stack Trace</th>\n"
                + "  </tr>\n");

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
            out.append("  <tr>\n"
                    + "    <td>").append(Integer.toString(c+1)).append("</td>\n"
                    + "    <td>").append(isConnected?"Yes":"No").append("</td>\n"
                    + "    <td>");
            if(isConnected) EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(time-createTimes[c]), out);
            else out.append("&#160;");
            out.append("    <td>").append(Long.toString(connCount)).append("</td>\n"
                    + "    <td>").append(Long.toString(useCount)).append("</td>\n"
                    + "    <td>");
            EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(totalTime), out);
            out.append("</td>\n"
                    + "    <td>").append(Float.toString(totalTime*100/(float)timeLen)).append("%</td>\n"
                    + "    <td>").append(isBusy?"In Use":isConnected?"Idle":"Closed").append("</td>\n"
                    + "    <td>");
            EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(stateTime), out);
            out.append("</td>\n"
                    + "    <td>").append(Long.toString(totalTime*1000/useCount)).append("&#181;s</td>\n"
                    + "    <td>");
            Throwable T = allocateStackTraces[c];
            if(T == null) out.append("&#160;");
            else {
                out.append("      <a href='#' onclick='var elem = document.getElementById(\"stack_").append(Integer.toString(c)).append("\").style; elem.visibility=(elem.visibility==\"visible\" ? \"hidden\" : \"visible\"); return false;'>Stack Trace</a>\n"
                        + "      <span id='stack_").append(Integer.toString(c)).append("' style='text-align:left; white-space:nowrap; position:absolute; visibility: hidden; z-index:").append(Integer.toString(c+1)).append("'>\n"
                        + "        <pre style='text-align:left; background-color:white; border: 2px solid; border-color: black;'>\n");
                ErrorPrinter.printStackTraces(T, out);
                out.append("        </pre>\n"
                        + "      </span>\n");
            }
            out.append("</td>\n"
                    + "  </tr>\n");

            // Update totals
            if(isConnected) totalConnected++;
            totalConnects+=connCount;
            totalUses+=useCount;
            totalTotalTime+=totalTime;
            if(isBusy) totalBusy++;
        }
        out.append("  <tr>\n"
                + "    <td><b>Total</b></td>\n"
                + "    <td>").append(Integer.toString(totalConnected)).append("</td>\n"
                + "    <td>&#160;</td>\n"
                + "    <td>").append(Long.toString(totalConnects)).append("</td>\n"
                + "    <td>").append(Long.toString(totalUses)).append("</td>\n"
                + "    <td>");
        EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(totalTotalTime), out);
        out.append("</td>\n"
                + "    <td>").append(Float.toString(timeLen==0 ? 0 : (totalTotalTime*100/(float)timeLen))).append("%</td>\n"
                + "    <td>").append(Integer.toString(totalBusy)).append("</td>\n"
                + "    <td>");
        EncodingUtils.encodeHtml(StringUtility.getDecimalTimeLengthString(timeLen), out);
        out.append("</td>\n"
                + "    <td>").append(Long.toString(totalUses==0 ? 0 : (totalTotalTime*1000/totalUses))).append("&#181;s</td>\n"
                + "    <td>&#160;</td>\n"
                + "  </tr>\n"
                + "</table>\n");
    }

    /**
     * Releases the database <code>Connection</code> to the <code>Connection</code> pool.
     * It is safe to call this method more than once, but only the first call will
     * have any affect and the second release will log a warning.
     */
    final public void releaseConnection(C connection) throws E {
        // Find the associated PooledConnection
        PooledConnection<C> pooledConnection = null;
        List<PooledConnection<C>> threadConnections = currentThreadConnections.get();
        for(int c=threadConnections.size()-1; c>=0; c--) {
            PooledConnection<C> threadConnection = threadConnections.get(c);
            synchronized(threadConnection) {
                if(threadConnection.connection==connection) {
                    pooledConnection = threadConnection;
                    threadConnections.remove(c);
                    break;
                }
            }
        }
        if(pooledConnection==null) {
            logger.log(Level.WARNING, "PooledConnection not found during releaseConnection");
        } else {
            try {
                boolean closeConnection = false;
                boolean connIsClosed;
                try {
                    connIsClosed = isClosed(connection);
                } catch(Exception err) {
                    logger.log(Level.SEVERE, null, err);
                    connIsClosed = false;
                    closeConnection = true; // Force closure due to error on isClosed
                }
                if(connIsClosed) {
                    // Already closed
                    synchronized(pooledConnection) {
                        pooledConnection.connection = null;
                    }
                } else {
                    synchronized(pooledConnection) {
                        if(maxConnectionAge!=UNLIMITED_MAX_CONNECTION_AGE) {
                            long age = System.currentTimeMillis()-pooledConnection.createTime;
                            if(age<0 || age>=maxConnectionAge) closeConnection = true;
                        }
                    }
                    if(closeConnection) {
                        // Error on isClosed or max age reached, close the connection
                        try {
                            close(connection);
                        } catch(Exception err) {
                            logger.log(Level.SEVERE, null, err);
                        }
                        synchronized(pooledConnection) {
                            pooledConnection.connection = null;
                        }
                    } else {
                        // Reset connections as they are released
                        try {
                            resetConnection(connection);
                        } catch(Exception err) {
                            // Close the connection when error during reset
                            logger.log(Level.SEVERE, null, err);
                            try {
                                close(connection);
                            } catch(Exception err2) {
                                logger.log(Level.SEVERE, null, err2);
                            }
                            synchronized(pooledConnection) {
                                pooledConnection.connection = null;
                            }
                        }
                    }
                }
            } finally {
                // Unallocate the connection from the pool
                release(pooledConnection);
            }
        }
    }

    protected abstract void resetConnection(C conn) throws I, E;

    /**
     * The RefreshConnection thread polls every connection in the connection pool. If it
     * detects a connection is idle for more than the pre-defined MAX_IDLE_TIME, it closes
     * the connection.  It will stop when the pool is flagged as closed.
     */
    @Override
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
                    connsToClose = new ArrayList<C>(availableConnections.size());
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
                }
                // Close all of the connections
                for(C conn : connsToClose) {
                    try {
                        close(conn);
                    } catch(Exception err) {
                        logger.log(Level.WARNING, null, err);
                    }
                }
            } catch (ThreadDeath TD) {
                throw TD;
            } catch (Throwable T) {
                logger.logp(Level.SEVERE, AOPool.class.getName(), "run", null, T);
            }
        }
    }

    protected abstract E newException(String message, Throwable cause);

    protected abstract I newInterruptedException(String message, Throwable cause);

    final public Logger getLogger() {
        return logger;
    }
}