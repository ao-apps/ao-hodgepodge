/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016  AO Industries, Inc.
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
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
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

	@Override
	protected void close(Connection conn) throws SQLException {
		conn.close();
	}

	/**
	 * Gets a read/write connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
	 * @return
	 * @throws SQLException
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(Connection.TRANSACTION_READ_COMMITTED, false, 1);
	}

	/**
	 * Gets a connection to the database with a transaction level of Connection.TRANSACTION_READ_COMMITTED and a maximum connections of 1.
	 * @param readOnly
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(boolean readOnly) throws SQLException {
		return getConnection(Connection.TRANSACTION_READ_COMMITTED, readOnly, 1);
	}

	/**
	 * Gets a connection to the database with a maximum connections of 1.
	 * @param isolationLevel
	 * @param readOnly
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection(int isolationLevel, boolean readOnly) throws SQLException {
		return getConnection(isolationLevel, readOnly, 1);
	}

	public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws SQLException {
		Connection conn=null;
		try {
			while(conn==null) {
				conn=super.getConnection(maxConnections);
				boolean isReadOnly = conn.isReadOnly();
				if(isReadOnly!=readOnly) conn.setReadOnly(readOnly);
			}
			int currentIsolationLevel = conn.getTransactionIsolation();
			if(currentIsolationLevel!=isolationLevel) conn.setTransactionIsolation(isolationLevel);
			return conn;
		} catch(SQLException err) {
			if(conn!=null) {
				try {
					releaseConnection(conn);
				} catch(Exception err2) {
					// Will throw original error instead
				}
			}
			throw err;
		} catch(Exception err) {
			if(conn!=null) {
				try {
					releaseConnection(conn);
				} catch(Exception err2) {
					// Will throw original error instead
				}
			}
			throw new SQLException(err);
		}
	}

	private static final ConcurrentMap<String,Object> driversLoaded = new ConcurrentHashMap<String,Object>();

	/**
	 * Loads a driver at most once.
	 */
	private static void loadDriver(String classname) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(!driversLoaded.containsKey(classname)) {
			Object O = Class.forName(classname).newInstance();
			driversLoaded.putIfAbsent(classname, O);
		}
	}

	@Override
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
		} catch(SQLException err) {
			logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", err);
			throw err;
		} catch (ClassNotFoundException err) {
			SQLException sqlErr=new SQLException();
			sqlErr.initCause(err);
			logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", sqlErr);
			throw sqlErr;
		} catch (InstantiationException err) {
			SQLException sqlErr=new SQLException();
			sqlErr.initCause(err);
			logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", sqlErr);
			throw sqlErr;
		} catch (IllegalAccessException err) {
			SQLException sqlErr=new SQLException();
			sqlErr.initCause(err);
			logger.logp(Level.SEVERE, AOConnectionPool.class.getName(), "getConnectionObject", "url="+url+"&user="+user+"&password=XXXXXXXX", sqlErr);
			throw sqlErr;
		}
	}

	@Override
	protected boolean isClosed(Connection conn) throws SQLException {
		return conn.isClosed();
	}

	@Override
	protected void printConnectionStats(Appendable out) throws IOException {
		out.append("  <tr><th colspan='2'><span style='font-size:large;'>JDBC Driver</span></th></tr>\n"
				+ "  <tr><td>Driver:</td><td>");
		EncodingUtils.encodeHtml(driver, false, false, out);
		out.append("</td></tr>\n"
				+ "  <tr><td>URL:</td><td>");
		EncodingUtils.encodeHtml(url, false, false, out);
		out.append("</td></tr>\n"
				+ "  <tr><td>User:</td><td>");
		EncodingUtils.encodeHtml(user, false, false, out);
		out.append("</td></tr>\n"
				+ "  <tr><td>Password:</td><td>");
		int len=password.length();
		for(int c=0;c<len;c++) {
			out.append('*');
		}
		out.append("</td></tr>\n");
	}

	@Override
	protected void resetConnection(Connection conn) throws SQLException {
		if(Thread.interrupted()) throw new SQLException("Thread interrupted");
		// Dump all warnings to System.err and clear warnings
		SQLWarning warning=conn.getWarnings();
		if(warning!=null) {
			logger.logp(Level.WARNING, AOConnectionPool.class.getName(), "resetConnection", null, warning);
		}
		conn.clearWarnings();

		// Autocommit will always be turned on, regardless what a previous transaction might have done
		if(!conn.getAutoCommit()) {
			if(Thread.interrupted()) throw new SQLException("Thread interrupted");
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
		SQLException err=new SQLException(message);
		if(cause!=null) err.initCause(cause);
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
