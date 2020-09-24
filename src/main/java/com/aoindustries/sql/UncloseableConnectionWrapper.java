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

import com.aoindustries.sql.wrapper.ArrayWrapper;
import com.aoindustries.sql.wrapper.BlobWrapper;
import com.aoindustries.sql.wrapper.CallableStatementWrapper;
import com.aoindustries.sql.wrapper.ClobWrapper;
import com.aoindustries.sql.wrapper.ConnectionWrapper;
import com.aoindustries.sql.wrapper.DatabaseMetaDataWrapper;
import com.aoindustries.sql.wrapper.NClobWrapper;
import com.aoindustries.sql.wrapper.PreparedStatementWrapper;
import com.aoindustries.sql.wrapper.SQLXMLWrapper;
import com.aoindustries.sql.wrapper.SavepointWrapper;
import com.aoindustries.sql.wrapper.StatementWrapper;
import com.aoindustries.sql.wrapper.StructWrapper;
import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Wraps a {@link Connection} while tracking closed state; will only delegate methods to wrapped connection when not
 * closed.
 *
 * @author  AO Industries, Inc.
 */
public class UncloseableConnectionWrapper extends ConnectionWrapper implements IUncloseableConnectionWrapper {

	private final AtomicBoolean closed = new AtomicBoolean();

	public UncloseableConnectionWrapper(Connection wrapped) {
		super(wrapped);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation calls {@link Connection#abort(java.util.concurrent.Executor)}
	 * on the wrapped connection.
	 * </p>
	 *
	 * @see  #getWrappedConnection()
	 * @see  Connection#abort(java.util.concurrent.Executor)
	 */
	@Override
	public void onAbort(Executor executor) throws SQLException {
		getWrapped().abort(executor);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation does nothing.
	 * </p>
	 *
	 * @see  #getWrappedConnection()
	 * @see  Connection#close()
	 */
	@Override
	public void onClose() throws SQLException {
		// Do nothing
	}

	private <X extends Throwable> void checkNotClosed(Function<String,X> throwableSupplier) throws X {
		if(closed.get()) throw throwableSupplier.apply("Connection closed");
	}

	private void checkNotClosed() throws SQLException {
		checkNotClosed(message -> new SQLException(message));
	}

	@Override
	public StatementWrapper createStatement() throws SQLException {
		checkNotClosed();
		return super.createStatement();
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql) throws SQLException {
		checkNotClosed();
		return super.prepareStatement(sql);
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql) throws SQLException {
		checkNotClosed();
		return super.prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		checkNotClosed();
		return super.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		checkNotClosed();
		super.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		checkNotClosed();
		return super.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		checkNotClosed();
		super.commit();
	}

	@Override
	public void rollback() throws SQLException {
		checkNotClosed();
		super.rollback();
	}

	/**
	 * Blocks direct call to wrapped {@link Connection#close()}, instead setting closed flag and dispatching to
	 * {@link #onClose()}.  Will only call {@link #onClose()} once.
	 *
	 * @see #onClose()
	 */
	@Override
	public void close() throws SQLException {
		if(!closed.getAndSet(true)) {
			onClose();
		}
	}

	/**
	 * When already known to be closed, returns {@code true} without calling wrapped {@link Connection#isClosed()}.
	 * When a connection is discovered to be closed, calls {@link #onClose()}.  Will only call {@link #onClose()} once.
	 */
	@Override
	public boolean isClosed() throws SQLException {
		if(closed.get()) return true;
		boolean wrappedClosed = super.isClosed();
		if(wrappedClosed) {
			// Connection detected as closed, call onClose() now
			if(!closed.getAndSet(true)) {
				onClose();
			}
		}
		return wrappedClosed;
	}

	@Override
	public DatabaseMetaDataWrapper getMetaData() throws SQLException {
		checkNotClosed();
		return super.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		checkNotClosed();
		super.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		checkNotClosed();
		return super.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		checkNotClosed();
		super.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		checkNotClosed();
		return super.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		checkNotClosed();
		super.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		checkNotClosed();
		return super.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		checkNotClosed();
		return super.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		checkNotClosed();
		super.clearWarnings();
	}

	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		checkNotClosed();
		return super.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		checkNotClosed();
		return super.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		checkNotClosed();
		return super.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String,Class<?>> getTypeMap() throws SQLException {
		checkNotClosed();
		return super.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String,Class<?>> map) throws SQLException {
		checkNotClosed();
		super.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		checkNotClosed();
		super.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		checkNotClosed();
		return super.getHoldability();
	}

	@Override
	public SavepointWrapper setSavepoint() throws SQLException {
		checkNotClosed();
		return super.setSavepoint();
	}

	@Override
	public SavepointWrapper setSavepoint(String name) throws SQLException {
		checkNotClosed();
		return super.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		checkNotClosed();
		super.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		checkNotClosed();
		super.releaseSavepoint(savepoint);
	}

	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		checkNotClosed();
		return super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		checkNotClosed();
		return super.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		checkNotClosed();
		return super.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		checkNotClosed();
		return super.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		checkNotClosed();
		return super.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, String columnNames[]) throws SQLException {
		checkNotClosed();
		return super.prepareStatement(sql, columnNames);
	}

	@Override
	public ClobWrapper createClob() throws SQLException {
		checkNotClosed();
		return super.createClob();
	}

	@Override
	public BlobWrapper createBlob() throws SQLException {
		checkNotClosed();
		return super.createBlob();
	}

	@Override
	public NClobWrapper createNClob() throws SQLException {
		checkNotClosed();
		return super.createNClob();
	}

	@Override
	public SQLXMLWrapper createSQLXML() throws SQLException {
		checkNotClosed();
		return super.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		if(closed.get()) return false;
		return super.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		checkNotClosed(message -> new SQLClientInfoException(message, null));
		super.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		checkNotClosed(message -> new SQLClientInfoException(message, null));
		super.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		checkNotClosed();
		return super.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		checkNotClosed();
		return super.getClientInfo();
	}

	@Override
	public ArrayWrapper createArrayOf(String typeName, Object[] elements) throws SQLException {
		checkNotClosed();
		return super.createArrayOf(typeName, elements);
	}

	@Override
	public StructWrapper createStruct(String typeName, Object[] attributes) throws SQLException {
		checkNotClosed();
		return super.createStruct(typeName, attributes);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		checkNotClosed();
		return super.getNetworkTimeout();
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		checkNotClosed();
		super.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		checkNotClosed();
		super.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		checkNotClosed();
		return super.getSchema();
	}

	
	/**
	 * Blocks direct call to wrapped {@link Connection#abort(java.util.concurrent.Executor)}, instead setting closed
	 * flag and dispatching to {@link #onAbort(java.util.concurrent.Executor)}.  Will only call
	 * {@link #onAbort(java.util.concurrent.Executor)} once.
	 *
	 * @see #onAbort(java.util.concurrent.Executor)
	 */
	@Override
	public void abort(Executor executor) throws SQLException {
		if(!closed.getAndSet(true)) {
			onAbort(executor);
		}
	}
}
