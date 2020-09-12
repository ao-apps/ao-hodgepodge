/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2020  AO Industries, Inc.
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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 * Wraps a {@link Connection}.
 *
 * @author  AO Industries, Inc.
 */
public class ConnectionWrapper implements IConnectionWrapper {

	private final Connection wrapped;

	public ConnectionWrapper(Connection wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Connection getWrappedConnection() {
		return wrapped;
	}

	/**
	 * Creates a new {@link ArrayWrapper}.
	 *
	 * @see  CallableStatementWrapper#wrapArray(java.sql.Array)
	 * @see  #wrapArray(java.sql.Array)
	 * @see  ResultSetWrapper#wrapArray(java.sql.Array)
	 */
	protected ArrayWrapper newArrayWrapper(StatementWrapper stmtWrapper, Array array) {
		return new ArrayWrapper(this, stmtWrapper, array);
	}

	/**
	 * Creates a new {@link CallableStatementWrapper}.
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	protected CallableStatementWrapper newCallableStatementWrapper(CallableStatement cstmt) {
		return new CallableStatementWrapper(this, cstmt);
	}

	/**
	 * Creates a new {@link DatabaseMetaDataWrapper}.
	 *
	 * @see  #wrapDatabaseMetaData(java.sql.DatabaseMetaData)
	 */
	protected DatabaseMetaDataWrapper newDatabaseMetaDataWrapper(DatabaseMetaData metaData) {
		return new DatabaseMetaDataWrapper(this, metaData);
	}

	/**
	 * Creates a new {@link PreparedStatementWrapper}.
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	protected PreparedStatementWrapper newPreparedStatementWrapper(PreparedStatement pstmt) {
		return new PreparedStatementWrapper(this, pstmt);
	}

	/**
	 * Creates a new {@link ResultSetWrapper}.
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 * @see  StatementWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper newResultSetWrapper(StatementWrapper stmtWrapper, ResultSet results) {
		return new ResultSetWrapper(this, stmtWrapper, results);
	}

	/**
	 * Creates a new {@link StatementWrapper}.
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper newStatementWrapper(Statement stmt) {
		return new StatementWrapper(this, stmt);
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  #newArrayWrapper(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 * @see  #unwrapArray(java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(
				arrayWrapper.getConnectionWrapper() == this
				&& !arrayWrapper.getStatementWrapper().isPresent()
			) {
				return arrayWrapper;
			}
		}
		return newArrayWrapper(null, array);
	}

	/**
	 * Unwraps an {@link Array}, if wrapped by this wrapper.
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	protected Array unwrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(arrayWrapper.getConnectionWrapper() == this) {
				return arrayWrapper.getWrappedArray();
			}
		}
		return array;
	}

	/**
	 * Wraps a {@link CallableStatement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newCallableStatementWrapper(java.sql.CallableStatement)
	 */
	protected CallableStatementWrapper wrapCallableStatement(CallableStatement cstmt) {
		if(cstmt == null) {
			return null;
		}
		if(cstmt instanceof CallableStatementWrapper) {
			CallableStatementWrapper stmtWrapper = (CallableStatementWrapper)cstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newCallableStatementWrapper(cstmt);
	}

	/**
	 * Wraps a {@link DatabaseMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  #newDatabaseMetaDataWrapper(java.sql.DatabaseMetaData)
	 */
	protected DatabaseMetaDataWrapper wrapDatabaseMetaData(DatabaseMetaData metaData) {
		if(metaData instanceof DatabaseMetaDataWrapper) {
			DatabaseMetaDataWrapper metaDataWrapper = (DatabaseMetaDataWrapper)metaData;
			if(metaDataWrapper.getConnectionWrapper() == this) {
				return metaDataWrapper;
			}
		}
		return newDatabaseMetaDataWrapper(metaData);
	}

	/**
	 * Wraps a {@link PreparedStatement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newPreparedStatementWrapper(java.sql.PreparedStatement)
	 */
	protected PreparedStatementWrapper wrapPreparedStatement(PreparedStatement pstmt) {
		if(pstmt == null) {
			return null;
		}
		if(pstmt instanceof CallableStatement) {
			return wrapCallableStatement((CallableStatement)pstmt);
		}
		if(pstmt instanceof PreparedStatementWrapper) {
			PreparedStatementWrapper stmtWrapper = (PreparedStatementWrapper)pstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newPreparedStatementWrapper(pstmt);
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  #newResultSetWrapper(com.aoindustries.sql.StatementWrapper, java.sql.ResultSet)
	 * @see  #wrapStatement(java.sql.Statement)
	 * @see  ArrayWrapper#wrapResultSet(java.sql.ResultSet)
	 * @see  DatabaseMetaDataWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(ResultSet results) throws SQLException {
		if(results == null) {
			return null;
		}
		if(results instanceof ResultSetWrapper) {
			ResultSetWrapper resultsWrapper = (ResultSetWrapper)results;
			if(
				resultsWrapper.getConnectionWrapper() == this
				&& !resultsWrapper.getStatementWrapper().isPresent()
			) {
				return resultsWrapper;
			}
		}
		StatementWrapper stmtWrapper = wrapStatement(results.getStatement());
		if(stmtWrapper != null) {
			return stmtWrapper.wrapResultSet(results);
		} else {
			return newResultSetWrapper(null, results);
		}
	}

	/**
	 * Wraps a {@link Statement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newStatementWrapper(java.sql.Statement)
	 * @see  ResultSetWrapper#wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper wrapStatement(Statement stmt) {
		if(stmt == null) {
			return null;
		}
		if(stmt instanceof PreparedStatement) {
			return wrapPreparedStatement((PreparedStatement)stmt);
		}
		if(stmt instanceof StatementWrapper) {
			StatementWrapper stmtWrapper = (StatementWrapper)stmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newStatementWrapper(stmt);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		Connection conn = getWrappedConnection();
		if(iface.isInstance(conn)) return iface.cast(conn);
		return conn.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		Connection conn = getWrappedConnection();
		return iface.isInstance(conn) || conn.isWrapperFor(iface);
	}

	@Override
	public StatementWrapper createStatement() throws SQLException {
		return wrapStatement(getWrappedConnection().createStatement());
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql));
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql) throws SQLException {
		return wrapCallableStatement(getWrappedConnection().prepareCall(sql));
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return getWrappedConnection().nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		getWrappedConnection().setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return getWrappedConnection().getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		getWrappedConnection().commit();
	}

	@Override
	public void rollback() throws SQLException {
		getWrappedConnection().rollback();
	}

	@Override
	public void close() throws SQLException {
		getWrappedConnection().close();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return getWrappedConnection().isClosed();
	}

	@Override
	public DatabaseMetaDataWrapper getMetaData() throws SQLException {
		return wrapDatabaseMetaData(getWrappedConnection().getMetaData());
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		getWrappedConnection().setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return getWrappedConnection().isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		getWrappedConnection().setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return getWrappedConnection().getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		getWrappedConnection().setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return getWrappedConnection().getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return getWrappedConnection().getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		getWrappedConnection().clearWarnings();
	}

	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapStatement(getWrappedConnection().createStatement(resultSetType, resultSetConcurrency));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapCallableStatement(getWrappedConnection().prepareCall(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public Map<String,Class<?>> getTypeMap() throws SQLException {
		return getWrappedConnection().getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String,Class<?>> map) throws SQLException {
		getWrappedConnection().setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		getWrappedConnection().setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return getWrappedConnection().getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return getWrappedConnection().setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return getWrappedConnection().setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		getWrappedConnection().rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		getWrappedConnection().releaseSavepoint(savepoint);
	}

	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapStatement(getWrappedConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapCallableStatement(getWrappedConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, autoGeneratedKeys));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, columnIndexes));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, String columnNames[]) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, columnNames));
	}

	@Override
	public Clob createClob() throws SQLException {
		return getWrappedConnection().createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return getWrappedConnection().createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return getWrappedConnection().createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return getWrappedConnection().createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return getWrappedConnection().isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		getWrappedConnection().setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		getWrappedConnection().setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return getWrappedConnection().getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return getWrappedConnection().getClientInfo();
	}

	@Override
	public ArrayWrapper createArrayOf(String typeName, Object[] elements) throws SQLException {
		return wrapArray(getWrappedConnection().createArrayOf(typeName, elements));
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return getWrappedConnection().createStruct(typeName, attributes);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return getWrappedConnection().getNetworkTimeout();
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		getWrappedConnection().setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		getWrappedConnection().setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return getWrappedConnection().getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		getWrappedConnection().abort(executor);
	}
}
