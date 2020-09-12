/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016, 2018, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.sql;

import com.aoindustries.io.AOPool;
import com.aoindustries.lang.AutoCloseables;
import com.aoindustries.lang.Throwables;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable connection pooling with dynamic flaming tiger feature.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Use Connection.isValid while allocating from pool and/or putting back into pool?
// TODO: Can isValid be used instead of forceful rollbackAndClose on SQLException?
// TODO: Or use isValid in background connection management?
// TODO: Warn in AOConnectionPool when max connections are higher than database supports
final public class AOConnectionPool extends AOPool<Connection,SQLException,SQLException> {

	/**
	 * The read-only state of connections while idle in the pool.
	 */
	public static final boolean IDLE_READ_ONLY = true;

	private final String driver;
	private final String url;
	private final String user;
	private final String password;

	public AOConnectionPool(String driver, String url, String user, String password, int numConnections, long maxConnectionAge, Logger logger) {
		super(AOConnectionPool.class.getName()+"?url=" + url+"&user="+user, numConnections, maxConnectionAge, logger);
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@SuppressWarnings("null")
	private Connection unwrap(Connection conn) throws SQLException {
		AOPoolConnectionWrapper wrapper;
		if(conn instanceof AOPoolConnectionWrapper) {
			wrapper = (AOPoolConnectionWrapper)conn;
		} else {
			wrapper = conn.unwrap(AOPoolConnectionWrapper.class);
		}
		if(wrapper.pool == this) {
			return wrapper.wrapped;
		} else {
			throw new SQLException("Connection from a different pool, cannot unwrap");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If the connection not already {@linkplain Connection#isClosed() closed}, and is not
	 * {@linkplain Connection#getAutoCommit() auto-commit}, the connection will be
	 * {@linkplain Connection#rollback() rolled back} and set back to auto-commit before closing.
	 * </p>
	 */
	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	protected void close(Connection conn) throws SQLException {
		Throwable t1 = null;
		// Unwrap
		try {
			conn = unwrap(conn);
		} catch(Throwable t) {
			t1 = Throwables.addSuppressed(t1, t);
		}
		// Close wrapped (or parameter "conn" when can't unwrap)
		try {
			if(!conn.isClosed() && !conn.getAutoCommit()) {
				conn.rollback();
				conn.setAutoCommit(true);
			}
		} catch(Throwable t) {
			t1 = Throwables.addSuppressed(t1, t);
		} finally {
			t1 = AutoCloseables.close(t1, conn);
		}
		if(t1 != null) {
			if(t1 instanceof Error) throw (Error)t1;
			if(t1 instanceof RuntimeException) throw (RuntimeException)t1;
			if(t1 instanceof SQLException) throw (SQLException)t1;
			throw new SQLException(t1);
		}
	}

	/**
	 * Gets a read/write connection to the database with a transaction level of
	 * {@link Connections#DEFAULT_TRANSACTION_ISOLATION},
	 * warning when a connection is already used by this thread.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 * <p>
	 * If all the connections in the pool are busy and the pool is at capacity, waits until a connection becomes
	 * available.
	 * </p>
	 * <p>
	 * Due to internal {@link ThreadLocal} optimizations, the connection returned must be released by the current
	 * thread, it should not be passed off to another thread before {@linkplain Connection#close() release}.
	 * </p>
	 *
	 * @return  The read/write connection to the database
	 *
	 * @throws  SQLException  when an error occurs, or when a thread attempts to allocate more than half the pool
	 *
	 * @see  #getConnection(int, boolean, int)
	 * @see  Connection#close()
	 */
	// Note: Matches AOPool.getConnection()
	// Note: Matches Database.getConnection()
	// Note: Matches DatabaseConnection.getConnection()
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(Connections.DEFAULT_TRANSACTION_ISOLATION, false, 1);
	}

	/**
	 * Gets a read/write connection to the database with a transaction level of
	 * {@link Connections#DEFAULT_TRANSACTION_ISOLATION}.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 * <p>
	 * If all the connections in the pool are busy and the pool is at capacity, waits until a connection becomes
	 * available.
	 * </p>
	 * <p>
	 * Due to internal {@link ThreadLocal} optimizations, the connection returned must be released by the current
	 * thread, it should not be passed off to another thread before {@linkplain Connection#close() release}.
	 * </p>
	 *
	 * @param  maxConnections  The maximum number of connections expected to be used by the current thread.
	 *                         This should normally be one to avoid potential deadlock.
	 *
	 * @return  The read/write connection to the database
	 *
	 * @throws  SQLException  when an error occurs, or when a thread attempts to allocate more than half the pool
	 *
	 * @see  #getConnection(int, boolean, int)
	 * @see  Connection#close()
	 */
	// Note: Matches AOPool.getConnection(int)
	// Note: Matches Database.getConnection(int)
	// Note: Matches DatabaseConnection.getConnection(int)
	@Override
	public Connection getConnection(int maxConnections) throws SQLException {
		return getConnection(Connections.DEFAULT_TRANSACTION_ISOLATION, false, maxConnections);
	}

	/**
	 * Gets a connection to the database with a transaction level of
	 * {@link Connections#DEFAULT_TRANSACTION_ISOLATION},
	 * warning when a connection is already used by this thread.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 * <p>
	 * If all the connections in the pool are busy and the pool is at capacity, waits until a connection becomes
	 * available.
	 * </p>
	 * <p>
	 * Due to internal {@link ThreadLocal} optimizations, the connection returned must be released by the current
	 * thread, it should not be passed off to another thread before {@linkplain Connection#close() release}.
	 * </p>
	 *
	 * @param  readOnly  The {@link Connection#setReadOnly(boolean) read-only flag}
	 *
	 * @return  The connection to the database
	 *
	 * @throws  SQLException  when an error occurs, or when a thread attempts to allocate more than half the pool
	 *
	 * @see  #getConnection(int, boolean, int)
	 * @see  Connection#close()
	 */
	// Note: Matches Database.getConnection(boolean)
	// Note: Matches DatabaseConnection.getConnection(boolean)
	public Connection getConnection(boolean readOnly) throws SQLException {
		return getConnection(Connections.DEFAULT_TRANSACTION_ISOLATION, readOnly, 1);
	}

	/**
	 * Gets a connection to the database,
	 * warning when a connection is already used by this thread.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 * <p>
	 * If all the connections in the pool are busy and the pool is at capacity, waits until a connection becomes
	 * available.
	 * </p>
	 * <p>
	 * Due to internal {@link ThreadLocal} optimizations, the connection returned must be released by the current
	 * thread, it should not be passed off to another thread before {@linkplain Connection#close() release}.
	 * </p>
	 *
	 * @param  isolationLevel  The {@link Connection#setTransactionIsolation(int) transaction isolation level}
	 *
	 * @param  readOnly        The {@link Connection#setReadOnly(boolean) read-only flag}
	 *
	 * @return  The connection to the database
	 *
	 * @throws  SQLException  when an error occurs, or when a thread attempts to allocate more than half the pool
	 *
	 * @see  #getConnection(int, boolean, int)
	 * @see  Connection#close()
	 */
	// Note: Matches Database.getConnection(int, boolean)
	// Note: Matches DatabaseConnection.getConnection(int, boolean)
	public Connection getConnection(int isolationLevel, boolean readOnly) throws SQLException {
		return getConnection(isolationLevel, readOnly, 1);
	}

	/**
	 * Gets a connection to the database.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 * <p>
	 * If all the connections in the pool are busy and the pool is at capacity, waits until a connection becomes
	 * available.
	 * </p>
	 * <p>
	 * Due to internal {@link ThreadLocal} optimizations, the connection returned must be released by the current
	 * thread, it should not be passed off to another thread before {@linkplain Connection#close() release}.
	 * </p>
	 *
	 * @param  isolationLevel  The {@link Connection#setTransactionIsolation(int) transaction isolation level}
	 *
	 * @param  readOnly        The {@link Connection#setReadOnly(boolean) read-only flag}
	 *
	 * @param  maxConnections  The maximum number of connections expected to be used by the current thread.
	 *                         This should normally be one to avoid potential deadlock.
	 *
	 * @return  The connection to the database
	 *
	 * @throws  SQLException  when an error occurs, or when a thread attempts to allocate more than half the pool
	 *
	 * @see  Connection#close()
	 */
	// Note: Matches Database.getConnection(int, boolean, int)
	// Note: Matches DatabaseConnection.getConnection(int, boolean, int)
	@SuppressWarnings({"UseSpecificCatch", "AssignmentToCatchBlockParameter"})
	public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws SQLException {
		Connection conn = null;
		try {
			conn = super.getConnection(maxConnections);
			assert conn.getAutoCommit();
			assert conn.isReadOnly() == IDLE_READ_ONLY : "Connection not reset";
			assert conn.getTransactionIsolation() == Connections.DEFAULT_TRANSACTION_ISOLATION : "Connection not reset";
			if(readOnly != IDLE_READ_ONLY) conn.setReadOnly(readOnly);
			if(isolationLevel != Connections.DEFAULT_TRANSACTION_ISOLATION) conn.setTransactionIsolation(isolationLevel);
			return conn;
		} catch(Throwable t) {
			try {
				release(conn);
			} catch(Throwable t2) {
				t = Throwables.addSuppressed(t, t2);
			}
			if(t instanceof Error) throw (Error)t;
			if(t instanceof RuntimeException) throw (RuntimeException)t;
			if(t instanceof SQLException) throw (SQLException)t;
			throw new SQLException(t);
		}
	}

	private static final ConcurrentMap<String,Object> driversLoaded = new ConcurrentHashMap<>();

	/**
	 * Loads a driver at most once.
	 */
	private static void loadDriver(String classname) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if(!driversLoaded.containsKey(classname)) {
			Object O = Class.forName(classname).getConstructor().newInstance();
			driversLoaded.putIfAbsent(classname, O);
		}
	}

	private static class AOPoolConnectionWrapper extends UncloseableConnectionWrapper {

		private final AOConnectionPool pool;
		private final Connection wrapped;

		private AOPoolConnectionWrapper(AOConnectionPool pool, Connection wrapped) {
			this.pool = pool;
			this.wrapped = wrapped;
		}

		@Override
		protected void onAbort(Executor executor) throws SQLException {
			Throwable t1 = null;
			try {
				wrapped.abort(executor);
			} catch(Throwable t) {
				t1 = Throwables.addSuppressed(t1, t);
			}
			try {
				pool.release(this);
			} catch(Throwable t) {
				t1 = Throwables.addSuppressed(t1, t);
			}
			if(t1 != null) {
				if(t1 instanceof Error) throw (Error)t1;
				if(t1 instanceof RuntimeException) throw (RuntimeException)t1;
				if(t1 instanceof SQLException) throw (SQLException)t1;
				throw new SQLException(t1);
			}
		}

		@Override
		protected void onClose() throws SQLException {
			pool.release(this);
		}

		@Override
		protected Connection getWrappedConnection() {
			return wrapped;
		}
	}

	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	protected AOPoolConnectionWrapper getConnectionObject() throws SQLException {
		try {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
			loadDriver(driver);
			Connection conn = DriverManager.getConnection(url, user, password);
			boolean successful = false;
			try {
				if(Thread.interrupted()) throw new SQLException("Thread interrupted"); // TODO: Make an InterruptedSQLException, with a static checkInterrupted() method?
				if(conn.getClass().getName().startsWith("org.postgresql.")) {
					// getTransactionIsolation causes a round-trip to the database, this wrapper caches the value and avoids unnecessary sets
					// to eliminate unnecessary round-trips and improve performance over high-latency links.
					conn = new PostgresqlConnectionWrapper(conn);
				}
				AOPoolConnectionWrapper wrapped = new AOPoolConnectionWrapper(this, conn);
				successful = true;
				return wrapped;
			} finally {
				if(!successful) conn.close();
			}
		} catch(ThreadDeath td) {
			throw td;
		} catch(Throwable t) {
			logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", t);
			if(t instanceof Error) throw (Error)t;
			if(t instanceof RuntimeException) throw (RuntimeException)t;
			if(t instanceof SQLException) throw (SQLException)t;
			throw new SQLException(t);
		}
	}

	@Override
	protected boolean isClosed(Connection conn) throws SQLException {
		return unwrap(conn).isClosed();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void printConnectionStats(Appendable out, boolean isXhtml) throws IOException {
		out.append("  <thead>\n"
				+ "    <tr><th colspan=\"2\"><span style=\"font-size:large\">JDBC Driver</span></th></tr>\n"
				+ "  </thead>\n");
		super.printConnectionStats(out, isXhtml);
		out.append("    <tr><td>Driver:</td><td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(driver, false, false, out, isXhtml);
		out.append("</td></tr>\n"
				+ "    <tr><td>URL:</td><td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(url, false, false, out, isXhtml);
		out.append("</td></tr>\n"
				+ "    <tr><td>User:</td><td>");
		com.aoindustries.util.EncodingUtils.encodeHtml(user, false, false, out, isXhtml);
		out.append("</td></tr>\n"
				+ "    <tr><td>Password:</td><td>");
		int len=password.length();
		for(int c=0;c<len;c++) {
			out.append('*');
		}
		out.append("</td></tr>\n");
	}

	/**
	 * Default implementation of {@link #logConnection(java.sql.Connection)}
	 *
	 * @see  #logConnection(java.sql.Connection)
	 */
	public static void defaultLogConnection(Connection conn, Logger logger) throws SQLException {
		if(logger.isLoggable(Level.WARNING)) {
			SQLWarning warning = conn.getWarnings();
			if(warning != null) logger.log(Level.WARNING, null, warning);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #defaultLogConnection(java.sql.Connection, java.util.logging.Logger)
	 */
	@Override
	protected void logConnection(Connection conn) throws SQLException {
		defaultLogConnection(conn, logger);
	}

	/**
	 * Default implementation of {@link #resetConnection(java.sql.Connection)}
	 * <ol>
	 * <li>{@linkplain Connection#clearWarnings() Warnings are cleared}</li>
	 * <li>Any {@linkplain Connection#getAutoCommit() transaction in-progress} is {@linkplain Connection#rollback() rolled-back}</li>
	 * <li>Auto-commit is enabled</li>
	 * <li>Read-only state is set to {@link #IDLE_READ_ONLY}</li>
	 * <li>Transaction isolation level set to {@link Connections#DEFAULT_TRANSACTION_ISOLATION}</li>
	 * </ol>
	 *
	 * @see  #resetConnection(java.sql.Connection)
	 */
	public static void defaultResetConnection(Connection conn) throws SQLException {
		if(Thread.interrupted()) throw new SQLException("Thread interrupted");
		conn.clearWarnings();

		// Autocommit will always be turned on, regardless what a previous transaction might have done
		if(!conn.getAutoCommit()) {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
			conn.rollback();
			conn.setAutoCommit(true);
		}
		// Restore the connection to the idle read-only state
		if(conn.isReadOnly() != IDLE_READ_ONLY) {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
			conn.setReadOnly(IDLE_READ_ONLY);
		}
		// Restore to default transaction level
		if(conn.getTransactionIsolation() != Connections.DEFAULT_TRANSACTION_ISOLATION) {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted"); // TODO: Should we do these types of interrupted checks more?
			conn.setTransactionIsolation(Connections.DEFAULT_TRANSACTION_ISOLATION);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #defaultResetConnection(java.sql.Connection)
	 */
	@Override
	protected void resetConnection(Connection conn) throws SQLException {
		defaultResetConnection(conn);
	}

	@Override
	protected SQLException newException(String message, Throwable cause) {
		if(cause instanceof SQLException) return (SQLException)cause;
		SQLException err = new SQLException(message);
		if(cause != null) err.initCause(cause);
		return err;
	}

	@Override
	protected SQLException newInterruptedException(String message, Throwable cause) {
		return newException(message, cause);
	}

	@Override
	public String toString() {
		return "AOConnectionPool(url=\""+url+"\", user=\""+user+"\")";
	}
}
