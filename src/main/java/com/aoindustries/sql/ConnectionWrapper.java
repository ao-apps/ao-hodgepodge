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
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Wraps a {@link Connection}.
 *
 * @author  AO Industries, Inc.
 */
public abstract class ConnectionWrapper implements Connection {

	public ConnectionWrapper() {
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
		return wrapStatement(getWrappedConnection().createStatement()).orElseThrow(AssertionError::new);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql)).orElseThrow(AssertionError::new);
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql) throws SQLException {
		return wrapCallableStatement(getWrappedConnection().prepareCall(sql)).orElseThrow(AssertionError::new);
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
		return wrapStatement(getWrappedConnection().createStatement(resultSetType, resultSetConcurrency)).orElseThrow(AssertionError::new);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, resultSetType, resultSetConcurrency)).orElseThrow(AssertionError::new);
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapCallableStatement(getWrappedConnection().prepareCall(sql, resultSetType, resultSetConcurrency)).orElseThrow(AssertionError::new);
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
		return wrapStatement(getWrappedConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)).orElseThrow(AssertionError::new);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)).orElseThrow(AssertionError::new);
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapCallableStatement(getWrappedConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability)).orElseThrow(AssertionError::new);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, autoGeneratedKeys)).orElseThrow(AssertionError::new);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, columnIndexes)).orElseThrow(AssertionError::new);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, String columnNames[]) throws SQLException {
		return wrapPreparedStatement(getWrappedConnection().prepareStatement(sql, columnNames)).orElseThrow(AssertionError::new);
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

	protected Optional<? extends StatementWrapper> wrapStatement(Statement stmt) {
		if(stmt == null) {
			return Optional.empty();
		}
		if(stmt instanceof PreparedStatement) {
			return wrapPreparedStatement((PreparedStatement)stmt);
		}
		if(stmt instanceof StatementWrapper) {
			StatementWrapper stmtWrapper = (StatementWrapper)stmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return Optional.of(stmtWrapper);
			}
		}
		return Optional.of(
			new StatementWrapper() {
				@Override
				protected ConnectionWrapper getConnectionWrapper() {
					return ConnectionWrapper.this;
				}

				@Override
				protected Statement getWrappedStatement() {
					return stmt;
				}
			}
		);
	}

	protected Optional<? extends PreparedStatementWrapper> wrapPreparedStatement(PreparedStatement pstmt) {
		if(pstmt == null) {
			return Optional.empty();
		}
		if(pstmt instanceof CallableStatement) {
			return wrapCallableStatement((CallableStatement)pstmt);
		}
		if(pstmt instanceof PreparedStatementWrapper) {
			PreparedStatementWrapper stmtWrapper = (PreparedStatementWrapper)pstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return Optional.of(stmtWrapper);
			}
		}
		return Optional.of(
			new PreparedStatementWrapper() {
				@Override
				protected ConnectionWrapper getConnectionWrapper() {
					return ConnectionWrapper.this;
				}

				@Override
				protected PreparedStatement getWrappedStatement() {
					return pstmt;
				}
			}
		);
	}

	protected Optional<? extends CallableStatementWrapper> wrapCallableStatement(CallableStatement cstmt) {
		if(cstmt == null) {
			return Optional.empty();
		}
		if(cstmt instanceof CallableStatementWrapper) {
			CallableStatementWrapper stmtWrapper = (CallableStatementWrapper)cstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return Optional.of(stmtWrapper);
			}
		}
		return Optional.of(
			new CallableStatementWrapper() {
				@Override
				protected ConnectionWrapper getConnectionWrapper() {
					return ConnectionWrapper.this;
				}

				@Override
				protected CallableStatement getWrappedStatement() {
					return cstmt;
				}
			}
		);
	}

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
		return wrapStatement(results.getStatement())
			.map(stmtWrapper -> stmtWrapper.wrapResultSet(results))
			.orElseGet(() -> new ResultSetWrapper() {
				@Override
				protected ConnectionWrapper getConnectionWrapper() {
					return ConnectionWrapper.this;
				}

				@Override
				protected Optional<? extends StatementWrapper> getStatementWrapper() {
					return Optional.empty();
				}

				@Override
				protected ResultSet getWrappedResultSet() {
					return results;
				}
			});
	}

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
		return new ArrayWrapper() {
			@Override
			protected ConnectionWrapper getConnectionWrapper() {
				return ConnectionWrapper.this;
			}

			@Override
			protected Optional<? extends StatementWrapper> getStatementWrapper() {
				return Optional.empty();
			}

			@Override
			protected Array getWrappedArray() {
				return array;
			}
		};
	}

	protected DatabaseMetaDataWrapper wrapDatabaseMetaData(DatabaseMetaData metaData) {
		if(metaData instanceof DatabaseMetaDataWrapper) {
			DatabaseMetaDataWrapper metaDataWrapper = (DatabaseMetaDataWrapper)metaData;
			if(metaDataWrapper.getConnectionWrapper() == this) {
				return metaDataWrapper;
			}
		}
		return new DatabaseMetaDataWrapper() {
			@Override
			protected ConnectionWrapper getConnectionWrapper() {
				return ConnectionWrapper.this;
			}

			@Override
			protected DatabaseMetaData getWrappedDatabaseMetaData() {
				return metaData;
			}
		};
	}

	/**
	 * Gets the connection that is wrapped.
	 */
	protected abstract Connection getWrappedConnection();
}
