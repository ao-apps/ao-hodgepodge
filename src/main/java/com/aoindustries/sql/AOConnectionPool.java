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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
final public class AOConnectionPool extends AOPool<Connection,SQLException,SQLException> {

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
		try {
			if(!conn.isClosed() && !conn.getAutoCommit()) {
				conn.rollback();
				conn.setAutoCommit(true);
			}
		} catch(ThreadDeath td) {
			throw td;
		} catch(Throwable t) {
			t1 = t;
		} finally {
			try {
				conn.close();
			} catch(ThreadDeath td) {
				throw td;
			} catch(Throwable t) {
				if(t1 == null) {
					t1 = t;
				} else {
					t1.addSuppressed(t);
				}
			}
		}
		if(t1 != null) {
			if(t1 instanceof Error) throw (Error)t1;
			if(t1 instanceof RuntimeException) throw (RuntimeException)t1;
			if(t1 instanceof SQLException) throw (SQLException)t1;
			throw new SQLException(t1);
		}
	}

	/**
	 * Gets a read/write connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 *
	 * @return The read/write connection to the database
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(Connection.TRANSACTION_READ_COMMITTED, false, 1);
	}

	/**
	 * Gets a connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 *
	 * @param readOnly The {@link Connection#setReadOnly(boolean) read-only flag}
	 * @return The connection to the database
	 */
	public Connection getConnection(boolean readOnly) throws SQLException {
		return getConnection(Connection.TRANSACTION_READ_COMMITTED, readOnly, 1);
	}

	/**
	 * Gets a connection to the database with a maximum connections of 1.
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 *
	 * @param isolationLevel The {@link Connection#setTransactionIsolation(int) transaction isolation level}
	 * @param readOnly The {@link Connection#setReadOnly(boolean) read-only flag}
	 *
	 * @return The connection to the database
	 */
	public Connection getConnection(int isolationLevel, boolean readOnly) throws SQLException {
		return getConnection(isolationLevel, readOnly, 1);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The connection will be in auto-commit mode, as configured by {@link #resetConnection(java.sql.Connection)}
	 * </p>
	 */
	@SuppressWarnings("UseSpecificCatch")
	public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws SQLException {
		Connection conn = null;
		try {
			conn = super.getConnection(maxConnections);
			assert conn.getAutoCommit();
			if(conn.isReadOnly() != readOnly) conn.setReadOnly(readOnly);
			if(conn.getTransactionIsolation() != isolationLevel) conn.setTransactionIsolation(isolationLevel);
			return conn;
		} catch(ThreadDeath td) {
			throw td;
		} catch(Throwable t) {
			try {
				releaseConnection(conn);
			} catch(ThreadDeath td) {
				throw td;
			} catch(Throwable t2) {
				t.addSuppressed(t2);
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

	@Override
	@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
	protected Connection getConnectionObject() throws SQLException {
		try {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
			loadDriver(driver);
			Connection conn = DriverManager.getConnection(url, user, password);
			boolean successful = false;
			try {
				if(Thread.interrupted()) throw new SQLException("Thread interrupted");
				if(conn.getClass().getName().startsWith("org.postgresql.")) {
					// getTransactionIsolation causes a round-trip to the database, this wrapper caches the value and avoids unnecessary sets
					// to eliminate unnecessary round-trips and improve performance over high-latency links.
					conn = new PostgresqlConnectionWrapper(conn);
				}
				successful = true;
				return conn;
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
		return conn.isClosed();
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

	@Override
	protected void logConnection(Connection conn) throws SQLException {
		if(logger.isLoggable(Level.WARNING)) {
			SQLWarning warning = conn.getWarnings();
			if(warning != null) logger.log(Level.WARNING, null, warning);
		}
	}

	@Override
	protected void resetConnection(Connection conn) throws SQLException {
		if(Thread.interrupted()) throw new SQLException("Thread interrupted");
		conn.clearWarnings();

		// Autocommit will always be turned on, regardless what a previous transaction might have done
		if(!conn.getAutoCommit()) {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
			conn.rollback();
			conn.setAutoCommit(true);
		}

		// Restore to default transaction level
		if(conn.getTransactionIsolation()!=Connection.TRANSACTION_READ_COMMITTED) {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		}

		// Restore the connection to a read-only state
		if(!conn.isReadOnly()) {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
			conn.setReadOnly(true);
		}
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
