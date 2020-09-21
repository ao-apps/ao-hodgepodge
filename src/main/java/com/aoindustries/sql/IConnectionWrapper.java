/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2020  AO Industries, Inc.
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

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Wraps a {@link Connection}.
 *
 * @author  AO Industries, Inc.
 */
public interface IConnectionWrapper extends Connection {

	/**
	 * Gets the connection that is wrapped.
	 */
	Connection getWrappedConnection();

	@Override
	default <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		Connection conn = getWrappedConnection();
		if(iface.isInstance(conn)) return iface.cast(conn);
		return conn.unwrap(iface);
	}

	@Override
	default boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		Connection conn = getWrappedConnection();
		return iface.isInstance(conn) || conn.isWrapperFor(iface);
	}

	@Override
	IStatementWrapper createStatement() throws SQLException;

	@Override
	IPreparedStatementWrapper prepareStatement(String sql) throws SQLException;

	@Override
	ICallableStatementWrapper prepareCall(String sql) throws SQLException;

	@Override
	default String nativeSQL(String sql) throws SQLException {
		return getWrappedConnection().nativeSQL(sql);
	}

	@Override
	default void setAutoCommit(boolean autoCommit) throws SQLException {
		getWrappedConnection().setAutoCommit(autoCommit);
	}

	@Override
	default boolean getAutoCommit() throws SQLException {
		return getWrappedConnection().getAutoCommit();
	}

	@Override
	default void commit() throws SQLException {
		getWrappedConnection().commit();
	}

	@Override
	default void rollback() throws SQLException {
		getWrappedConnection().rollback();
	}

	@Override
	default void close() throws SQLException {
		getWrappedConnection().close();
	}

	@Override
	default boolean isClosed() throws SQLException {
		return getWrappedConnection().isClosed();
	}

	@Override
	IDatabaseMetaDataWrapper getMetaData() throws SQLException;

	@Override
	default void setReadOnly(boolean readOnly) throws SQLException {
		getWrappedConnection().setReadOnly(readOnly);
	}

	@Override
	default boolean isReadOnly() throws SQLException {
		return getWrappedConnection().isReadOnly();
	}

	@Override
	default void setCatalog(String catalog) throws SQLException {
		getWrappedConnection().setCatalog(catalog);
	}

	@Override
	default String getCatalog() throws SQLException {
		return getWrappedConnection().getCatalog();
	}

	@Override
	default void setTransactionIsolation(int level) throws SQLException {
		getWrappedConnection().setTransactionIsolation(level);
	}

	@Override
	default int getTransactionIsolation() throws SQLException {
		return getWrappedConnection().getTransactionIsolation();
	}

	@Override
	default SQLWarning getWarnings() throws SQLException {
		return getWrappedConnection().getWarnings();
	}

	@Override
	default void clearWarnings() throws SQLException {
		getWrappedConnection().clearWarnings();
	}

	@Override
	IStatementWrapper createStatement(int resultSetType, int resultSetConcurrency) throws SQLException;

	@Override
	IPreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException;

	@Override
	ICallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException;

	@Override
	default Map<String,Class<?>> getTypeMap() throws SQLException {
		return getWrappedConnection().getTypeMap();
	}

	@Override
	default void setTypeMap(Map<String,Class<?>> map) throws SQLException {
		getWrappedConnection().setTypeMap(map);
	}

	@Override
	default void setHoldability(int holdability) throws SQLException {
		getWrappedConnection().setHoldability(holdability);
	}

	@Override
	default int getHoldability() throws SQLException {
		return getWrappedConnection().getHoldability();
	}

	@Override
	default Savepoint setSavepoint() throws SQLException {
		return getWrappedConnection().setSavepoint();
	}

	@Override
	default Savepoint setSavepoint(String name) throws SQLException {
		return getWrappedConnection().setSavepoint(name);
	}

	@Override
	default void rollback(Savepoint savepoint) throws SQLException {
		getWrappedConnection().rollback(savepoint);
	}

	@Override
	default void releaseSavepoint(Savepoint savepoint) throws SQLException {
		getWrappedConnection().releaseSavepoint(savepoint);
	}

	@Override
	IStatementWrapper createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException;

	@Override
	IPreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException;

	@Override
	ICallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException;

	@Override
	IPreparedStatementWrapper prepareStatement(String sql, int autoGeneratedKeys) throws SQLException;

	@Override
	IPreparedStatementWrapper prepareStatement(String sql, int columnIndexes[]) throws SQLException;

	@Override
	IPreparedStatementWrapper prepareStatement(String sql, String columnNames[]) throws SQLException;

	@Override
	default Clob createClob() throws SQLException {
		return getWrappedConnection().createClob();
	}

	@Override
	default Blob createBlob() throws SQLException {
		return getWrappedConnection().createBlob();
	}

	@Override
	default NClob createNClob() throws SQLException {
		return getWrappedConnection().createNClob();
	}

	@Override
	default SQLXML createSQLXML() throws SQLException {
		return getWrappedConnection().createSQLXML();
	}

	@Override
	default boolean isValid(int timeout) throws SQLException {
		return getWrappedConnection().isValid(timeout);
	}

	@Override
	default void setClientInfo(String name, String value) throws SQLClientInfoException {
		getWrappedConnection().setClientInfo(name, value);
	}

	@Override
	default void setClientInfo(Properties properties) throws SQLClientInfoException {
		getWrappedConnection().setClientInfo(properties);
	}

	@Override
	default String getClientInfo(String name) throws SQLException {
		return getWrappedConnection().getClientInfo(name);
	}

	@Override
	default Properties getClientInfo() throws SQLException {
		return getWrappedConnection().getClientInfo();
	}

	@Override
	IArrayWrapper createArrayOf(String typeName, Object[] elements) throws SQLException;

	@Override
	default Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return getWrappedConnection().createStruct(typeName, attributes);
	}

	@Override
	default int getNetworkTimeout() throws SQLException {
		return getWrappedConnection().getNetworkTimeout();
	}

	@Override
	default void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		getWrappedConnection().setNetworkTimeout(executor, milliseconds);
	}

	@Override
	default void setSchema(String schema) throws SQLException {
		getWrappedConnection().setSchema(schema);
	}

	@Override
	default String getSchema() throws SQLException {
		return getWrappedConnection().getSchema();
	}

	@Override
	default void abort(Executor executor) throws SQLException {
		getWrappedConnection().abort(executor);
	}
}
