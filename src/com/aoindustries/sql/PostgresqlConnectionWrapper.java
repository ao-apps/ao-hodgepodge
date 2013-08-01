/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011, 2013  AO Industries, Inc.
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

import com.aoindustries.lang.reflect.Methods;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Wraps a <code>Connection</code> and caches the transaction level, intended to avoid unnecessary round-trips imposed by
 * PostgreSQL getTransactionIsolation and setTransactionIsolation.
 *
 * @author  AO Industries, Inc.
 */
public class PostgresqlConnectionWrapper implements Connection {

    private final Connection conn;
    private int transactionIsolationLevel;

    PostgresqlConnectionWrapper(Connection conn) throws SQLException {
       this.conn = conn;
       this.transactionIsolationLevel = conn.getTransactionIsolation();
    }

	@Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return conn.unwrap(iface);
    }

	@Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return conn.isWrapperFor(iface);
    }

	@Override
    public Statement createStatement() throws SQLException {
        return conn.createStatement();
    }

	@Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

	@Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return conn.prepareCall(sql);
    }

	@Override
    public String nativeSQL(String sql) throws SQLException {
        return conn.nativeSQL(sql);
    }

	@Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        conn.setAutoCommit(autoCommit);
    }

	@Override
    public boolean getAutoCommit() throws SQLException {
        return conn.getAutoCommit();
    }

	@Override
    public void commit() throws SQLException {
        conn.commit();
    }

	@Override
    public void rollback() throws SQLException {
        conn.rollback();
    }

	@Override
    public void close() throws SQLException {
        conn.close();
    }

	@Override
    public boolean isClosed() throws SQLException {
        return conn.isClosed();
    }

	@Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return conn.getMetaData();
    }

	@Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        conn.setReadOnly(readOnly);
    }

	@Override
    public boolean isReadOnly() throws SQLException {
        return conn.isReadOnly();
    }

	@Override
    public void setCatalog(String catalog) throws SQLException {
        conn.setCatalog(catalog);
    }

	@Override
    public String getCatalog() throws SQLException {
        return conn.getCatalog();
    }

	@Override
    public void setTransactionIsolation(int level) throws SQLException {
        // We call on wrapped connection when isClosed to get the error from the wrapped driver.
        if(level!=this.transactionIsolationLevel || conn.isClosed()) {
            conn.setTransactionIsolation(level);
            this.transactionIsolationLevel = level;
        }
    }

	@Override
    public int getTransactionIsolation() throws SQLException {
        // We call on wrapped connection when isClosed to get the error from the wrapped driver.
        if(conn.isClosed()) return conn.getTransactionIsolation();
        return transactionIsolationLevel;
    }

	@Override
    public SQLWarning getWarnings() throws SQLException {
        return conn.getWarnings();
    }

	@Override
    public void clearWarnings() throws SQLException {
        conn.clearWarnings();
    }

	@Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return conn.createStatement(resultSetType, resultSetConcurrency);
    }

	@Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

	@Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

	@Override
    public Map<String,Class<?>> getTypeMap() throws SQLException {
        return conn.getTypeMap();
    }

	@Override
    public void setTypeMap(Map<String,Class<?>> map) throws SQLException {
        conn.setTypeMap(map);
    }

	@Override
    public void setHoldability(int holdability) throws SQLException {
        conn.setHoldability(holdability);
    }

	@Override
    public int getHoldability() throws SQLException {
        return conn.getHoldability();
    }

	@Override
    public Savepoint setSavepoint() throws SQLException {
        return conn.setSavepoint();
    }

	@Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return conn.setSavepoint(name);
    }

	@Override
    public void rollback(Savepoint savepoint) throws SQLException {
        conn.rollback(savepoint);
    }

	@Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        conn.releaseSavepoint(savepoint);
    }

	@Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

	@Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

	@Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

	@Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return conn.prepareStatement(sql, autoGeneratedKeys);
    }

	@Override
    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        return conn.prepareStatement(sql, columnIndexes);
    }

	@Override
    public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
        return conn.prepareStatement(sql, columnNames);
    }

	@Override
    public Clob createClob() throws SQLException {
        return conn.createClob();
    }

	@Override
    public Blob createBlob() throws SQLException {
        return conn.createBlob();
    }

	@Override
    public NClob createNClob() throws SQLException {
        return conn.createNClob();
    }

	@Override
    public SQLXML createSQLXML() throws SQLException {
        return conn.createSQLXML();
    }

	@Override
    public boolean isValid(int timeout) throws SQLException {
        return conn.isValid(timeout);
    }

	@Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        conn.setClientInfo(name, value);
    }
	
	@Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        conn.setClientInfo(properties);
    }

	@Override
    public String getClientInfo(String name) throws SQLException {
        return conn.getClientInfo(name);
    }

	@Override
    public Properties getClientInfo() throws SQLException {
        return conn.getClientInfo();
    }

	@Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return conn.createArrayOf(typeName, elements);
    }

	@Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return conn.createStruct(typeName, attributes);
    }

	// Java 1.7: @Override
	public int getNetworkTimeout() throws SQLException {
		return Methods.invoke(
			Integer.TYPE,
			conn,
			"getNetworkTimeout"
		);
		// Java 1.7: return conn.getNetworkTimeout();
	}

	// Java 1.7: @Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		Methods.invoke(
			Void.class,
			conn,
			"setNetworkTimeout",
			new Class[] {
				Executor.class,
				Integer.TYPE
			},
			new Object[] {
				executor,
				milliseconds
			}
		);
		// Java 1.7: conn.setNetworkTimeout(executor, milliseconds);
	}

	// Java 1.7: @Override
	public void setSchema(String schema) throws SQLException {
		Methods.invoke(
			Void.class,
			conn,
			"setSchema",
			String.class,
			schema
		);
		// Java 1.7: conn.setSchema(schema);
	}

	// Java 1.7: @Override
	public String getSchema() throws SQLException {
		return Methods.invoke(
			String.class,
			conn,
			"getSchema"
		);
		// Java 1.7: return conn.getSchema();
	}

	// Java 1.7: @Override
	public void abort(Executor executor) throws SQLException {
		Methods.invoke(
			Void.class,
			conn,
			"abort",
			Executor.class,
			executor
		);
		// Java 1.7: conn.abort(executor);
	}
}
