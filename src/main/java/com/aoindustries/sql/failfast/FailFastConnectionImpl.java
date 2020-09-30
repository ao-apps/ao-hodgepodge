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
package com.aoindustries.sql.failfast;

import com.aoindustries.collections.AoCollections;
import com.aoindustries.lang.Throwables;
import com.aoindustries.sql.wrapper.ConnectionWrapperImpl;
import com.aoindustries.sql.wrapper.SQLDataWrapperImpl;
import com.aoindustries.sql.wrapper.StatementWrapperImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.ClientInfoStatus;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLClientInfoException;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Makes a {@link Connection} perform in a fail-fast manner.  All access to the connection will fail once a
 * {@link Throwable} has been thrown by the underlying driver, with this state only being cleared by rollback.
 *
 * @author  AO Industries, Inc.
 */
// Note: Comment matches FailFastConnection
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastConnectionImpl extends ConnectionWrapperImpl implements FailFastConnection {

	private static class FailFastLock {}
	private final FailFastLock failFastLock = new FailFastLock();

	/**
	 * The current fail-fast cause.  Any read-only operation may access it without synchronization.  All updates must
	 * be synchronized on {@link #failFastLock}, since updates are multi-step operations for managing suppressed
	 * throwables.
	 */
	private volatile Throwable failFastCause;

	public FailFastConnectionImpl(FailFastDriver driver, Connection wrapped) {
		super(driver, wrapped);
	}

	public FailFastConnectionImpl(Connection wrapped) {
		super(wrapped);
	}

	@Override
	public void addFailFastCause(Throwable cause) {
		if(cause != null) {
			if(
				cause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
				|| cause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
			) throw new IllegalArgumentException("Private markers must be set directly without merge");
			synchronized(failFastLock) {
				if(
					// Don't merge if already in terminal fail-fast state
					failFastCause != ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
					&& failFastCause != AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
					// Don't replace if is same
					&& cause != failFastCause
				) {
					if(failFastCause == null) {
						failFastCause = cause;
					} else {
						State currentState = State.getState(failFastCause);
						State newState = State.getState(cause);
						int diff = currentState.compareTo(newState);
						if(diff > 0) {
							if(!ThrowableUtil.isSuppressed(failFastCause, cause)) failFastCause.addSuppressed(cause);
						} else if(diff < 0) {
							if(!ThrowableUtil.isSuppressed(cause, failFastCause)) cause.addSuppressed(failFastCause);
							failFastCause = cause;
						} else {
							failFastCause = Throwables.addSuppressed(failFastCause, cause);
						}
					}
				}
			}
		}
	}

	@Override
	public Throwable getFailFastCause() {
		return failFastCause;
	}

	@Override
	public Throwable clearFailFast() throws TerminalSQLException {
		synchronized(failFastLock) {
			Throwable cause = failFastCause;
			// Compare to the constants to distinguish from TerminalSQLException thrown by wrapped connections
			if(cause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new ClosedSQLException();
			if(cause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new AbortedSQLException();
			failFastCause = null;
			return cause;
		}
	}

	@Override
	public ApplicationSQLException clearApplicationFailFast() throws TerminalSQLException, SQLException {
		synchronized(failFastLock) {
			Throwable cause = failFastCause;
			// Compare to the constants to distinguish from TerminalSQLException thrown by wrapped connections
			if(cause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new ClosedSQLException();
			if(cause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new AbortedSQLException();
			if(!(cause instanceof ApplicationSQLException)) throw ThrowableUtil.newFailFastSQLException(cause);
			failFastCause = null;
			return (ApplicationSQLException)cause;
		}
	}

	/**
	 * @throws  SQLException  if currently in a fail-fast state
	 *
	 * @see  ThrowableUtil#newFailFastSQLException(java.lang.Throwable)
	 */
	protected void failFastSQLException() throws SQLException {
		Throwable cause = failFastCause;
		if(cause != null) throw ThrowableUtil.newFailFastSQLException(cause);
	}

	/**
	 * @throws  SQLClientInfoException  if currently in a fail-fast state
	 *
	 * @see  ThrowableUtil#newSQLClientInfoException(java.util.function.Supplier, java.lang.Throwable)
	 */
	protected void failFastSQLClientInfoException(Supplier<? extends Map<String,ClientInfoStatus>> failedPropertiesSupplier) throws SQLClientInfoException {
		Throwable cause = failFastCause;
		if(cause != null) throw ThrowableUtil.newSQLClientInfoException(failedPropertiesSupplier, cause);
	}

	/**
	 * @throws  IOException  if currently in a fail-fast state
	 *
	 * @see  ThrowableUtil#newIOException(java.lang.Throwable)
	 */
	protected void failFastIOException() throws IOException {
		Throwable cause = failFastCause;
		if(cause != null) throw ThrowableUtil.newIOException(cause);
	}

	@Override
	protected FailFastArrayImpl newArrayWrapper(StatementWrapperImpl stmtWrapper, Array array) {
		return new FailFastArrayImpl(this, (FailFastStatementImpl)stmtWrapper, array);
	}

	@Override
	protected FailFastBlobImpl newBlobWrapper(Blob blob) {
		return new FailFastBlobImpl(this, blob);
	}

	@Override
	protected FailFastCallableStatementImpl newCallableStatementWrapper(CallableStatement cstmt) {
		return new FailFastCallableStatementImpl(this, cstmt);
	}

	@Override
	protected FailFastClobImpl newClobWrapper(Clob clob) {
		return new FailFastClobImpl(this, clob);
	}

	@Override
	protected FailFastDatabaseMetaDataImpl newDatabaseMetaDataWrapper(DatabaseMetaData metaData) {
		return new FailFastDatabaseMetaDataImpl(this, metaData);
	}

	@Override
	protected FailFastInputStream newInputStreamWrapper(InputStream in) {
		return new FailFastInputStream(this, in);
	}

	@Override
	protected FailFastNClobImpl newNClobWrapper(NClob nclob) {
		return new FailFastNClobImpl(this, nclob);
	}

	@Override
	protected FailFastOutputStream newOutputStreamWrapper(OutputStream out) {
		return new FailFastOutputStream(this, out);
	}

	@Override
	protected FailFastParameterMetaDataImpl newParameterMetaDataWrapper(ParameterMetaData metaData) {
		return new FailFastParameterMetaDataImpl(this, metaData);
	}

	@Override
	protected FailFastPreparedStatementImpl newPreparedStatementWrapper(PreparedStatement pstmt) {
		return new FailFastPreparedStatementImpl(this, pstmt);
	}

	@Override
	protected FailFastReader newReaderWrapper(Reader in) {
		return new FailFastReader(this, in);
	}

	@Override
	protected FailFastRefImpl newRefWrapper(Ref ref) {
		return new FailFastRefImpl(this, ref);
	}

	@Override
	protected FailFastResultSetImpl newResultSetWrapper(StatementWrapperImpl stmtWrapper, ResultSet results) {
		return new FailFastResultSetImpl(this, (FailFastStatementImpl)stmtWrapper, results);
	}

	@Override
	protected FailFastResultSetMetaDataImpl newResultSetMetaDataWrapper(ResultSetMetaData metaData) {
		return new FailFastResultSetMetaDataImpl(this, metaData);
	}

	@Override
	protected FailFastRowIdImpl newRowIdWrapper(RowId rowId) {
		return new FailFastRowIdImpl(this, rowId);
	}

	@Override
	protected SQLDataWrapperImpl newSQLDataWrapper(SQLData sqlData) {
		return new FailFastSQLDataImpl(this, sqlData);
	}

	@Override
	protected FailFastSQLInputImpl newSQLInputWrapper(SQLInput sqlInput) {
		return new FailFastSQLInputImpl(this, sqlInput);
	}

	@Override
	protected FailFastSQLOutputImpl newSQLOutputWrapper(SQLOutput sqlOutput) {
		return new FailFastSQLOutputImpl(this, sqlOutput);
	}

	@Override
	protected FailFastSQLXMLImpl newSQLXMLWrapper(SQLXML sqlXml) {
		return new FailFastSQLXMLImpl(this, sqlXml);
	}

	@Override
	protected FailFastSavepointImpl newSavepointWrapper(Savepoint savepoint) {
		return new FailFastSavepointImpl(this, savepoint);
	}

	@Override
	protected FailFastStatementImpl newStatementWrapper(Statement stmt) {
		return new FailFastStatementImpl(this, stmt);
	}

	@Override
	protected FailFastStructImpl newStructWrapper(Struct struct) {
		return new FailFastStructImpl(this, struct);
	}

	@Override
	protected FailFastWriter newWriterWrapper(Writer out) {
		return new FailFastWriter(this, out);
	}

	@Override
	public FailFastStatementImpl createStatement() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStatementImpl)super.createStatement();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatementImpl prepareStatement(String sql) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatementImpl)super.prepareStatement(sql);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastCallableStatementImpl prepareCall(String sql) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastCallableStatementImpl)super.prepareCall(sql);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		failFastSQLException();
		try {
			return super.nativeSQL(sql);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		failFastSQLException();
		try {
			super.setAutoCommit(autoCommit);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		failFastSQLException();
		try {
			return super.getAutoCommit();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void commit() throws SQLException {
		failFastSQLException();
		try {
			super.commit();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	@SuppressWarnings("ThrowableResultIgnored")
	public void rollback() throws TerminalSQLException, SQLException {
		synchronized(failFastLock) {
			if(failFastCause != null) {
				if(failFastCause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new ClosedSQLException();
				if(failFastCause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new AbortedSQLException();
				try {
					super.rollback();
					clearFailFast();
					return;
				} catch(Throwable t) {
					addFailFastCause(t);
					throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
				}
			}
			// Continue outside synchronized block
		}
		try {
			super.rollback();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void close() throws SQLException {
		Throwable cause;
		boolean doClose;
		synchronized(failFastLock) {
			cause = failFastCause;
			if(
				cause != ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
				&& cause != AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
			) {
				failFastCause = ClosedSQLException.FAST_MARKER_KEEP_PRIVATE;
				doClose = true;
			} else {
				doClose = false;
			}
		}
		if(doClose) doClose(cause);
	}

	@Override
	public boolean isClosed() throws SQLException {
		Throwable cause = failFastCause;
		if(
			cause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
			|| cause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
		) {
			return true;
		}
		try {
			return super.isClosed();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastDatabaseMetaDataImpl getMetaData() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastDatabaseMetaDataImpl)super.getMetaData();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		failFastSQLException();
		try {
			super.setReadOnly(readOnly);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		failFastSQLException();
		try {
			return super.isReadOnly();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		failFastSQLException();
		try {
			super.setCatalog(catalog);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getCatalog() throws SQLException {
		failFastSQLException();
		try {
			return super.getCatalog();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		failFastSQLException();
		try {
			super.setTransactionIsolation(level);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		failFastSQLException();
		try {
			return super.getTransactionIsolation();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		failFastSQLException();
		try {
			return super.getWarnings();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void clearWarnings() throws SQLException {
		failFastSQLException();
		try {
			super.clearWarnings();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastStatementImpl createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStatementImpl)super.createStatement(resultSetType, resultSetConcurrency);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatementImpl prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatementImpl)super.prepareStatement(sql, resultSetType, resultSetConcurrency);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastCallableStatementImpl prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastCallableStatementImpl)super.prepareCall(sql, resultSetType, resultSetConcurrency);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Map<String,Class<?>> getTypeMap() throws SQLException {
		failFastSQLException();
		try {
			return super.getTypeMap();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setTypeMap(Map<String,Class<?>> map) throws SQLException {
		failFastSQLException();
		try {
			super.setTypeMap(map);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		failFastSQLException();
		try {
			super.setHoldability(holdability);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getHoldability() throws SQLException {
		failFastSQLException();
		try {
			return super.getHoldability();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastSavepointImpl setSavepoint() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastSavepointImpl)super.setSavepoint();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastSavepointImpl setSavepoint(String name) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastSavepointImpl)super.setSavepoint(name);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	@SuppressWarnings("ThrowableResultIgnored")
	public void rollback(Savepoint savepoint) throws SQLException {
		synchronized(failFastLock) {
			if(failFastCause != null) {
				if(failFastCause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new ClosedSQLException();
				if(failFastCause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new AbortedSQLException();
				try {
					super.rollback(savepoint);
					clearFailFast();
					return;
				} catch(Throwable t) {
					addFailFastCause(t);
					throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
				}
			}
			// Continue outside synchronized block
		}
		try {
			super.rollback(savepoint);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		try {
			super.releaseSavepoint(savepoint);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastStatementImpl createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStatementImpl)super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatementImpl prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatementImpl)super.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastCallableStatementImpl prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastCallableStatementImpl)super.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatementImpl prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatementImpl)super.prepareStatement(sql, autoGeneratedKeys);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatementImpl prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatementImpl)super.prepareStatement(sql, columnIndexes);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatementImpl prepareStatement(String sql, String columnNames[]) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatementImpl)super.prepareStatement(sql, columnNames);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastClobImpl createClob() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastClobImpl)super.createClob();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastBlobImpl createBlob() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastBlobImpl)super.createBlob();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastNClobImpl createNClob() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastNClobImpl)super.createNClob();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastSQLXMLImpl createSQLXML() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastSQLXMLImpl)super.createSQLXML();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		Throwable cause = failFastCause;
		if(
			cause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
			|| cause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
		) {
			return false;
		}
		try {
			return super.isValid(timeout);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		failFastSQLClientInfoException(() -> Collections.singletonMap(name, ClientInfoStatus.REASON_UNKNOWN));
		try {
			super.setClientInfo(name, value);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(
				t,
				SQLClientInfoException.class,
				cause -> new SQLClientInfoException(
					Collections.singletonMap(name, ClientInfoStatus.REASON_UNKNOWN),
					cause
				)
			);
		}
	}

	private static Map<String,ClientInfoStatus> toClientInfoMap(Properties props) {
		Map<String,ClientInfoStatus> map = AoCollections.newHashMap(props.size());
		for(Object key : props.keySet()) {
			if(key instanceof String) map.put((String)key, ClientInfoStatus.REASON_UNKNOWN);
		}
		return map;
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		failFastSQLClientInfoException(() -> toClientInfoMap(properties));
		try {
			super.setClientInfo(properties);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(
				t,
				SQLClientInfoException.class,
				cause -> new SQLClientInfoException(
					toClientInfoMap(properties),
					cause
				)
			);
		}
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		failFastSQLException();
		try {
			return super.getClientInfo(name);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		failFastSQLException();
		try {
			return super.getClientInfo();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastArrayImpl createArrayOf(String typeName, Object[] elements) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastArrayImpl)super.createArrayOf(typeName, elements);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastStructImpl createStruct(String typeName, Object[] attributes) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStructImpl)super.createStruct(typeName, attributes);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		failFastSQLException();
		try {
			return super.getNetworkTimeout();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		failFastSQLException();
		try {
			super.setNetworkTimeout(executor, milliseconds);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		failFastSQLException();
		try {
			super.setSchema(schema);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getSchema() throws SQLException {
		failFastSQLException();
		try {
			return super.getSchema();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		Throwable cause;
		boolean doAbort;
		synchronized(failFastLock) {
			cause = failFastCause;
			if(
				cause != ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
				&& cause != AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
			) {
				failFastCause = AbortedSQLException.FAST_MARKER_KEEP_PRIVATE;
				doAbort = true;
			} else {
				doAbort = false;
			}
		}
		if(doAbort) doAbort(failFastCause, executor);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Either this method or {@link #doAbort(java.lang.Throwable, java.util.concurrent.Executor)} is called, and at most
	 * once.  Once either is called, additional close/abort requests are ignored.
	 * </p>
	 * <p>
	 * This default implementation calls {@code super.close()}.
	 * </p>
	 * <p>
	 * TODO: If already in a non-terminal fail-fast state, {@linkplain Connection#isValid(int) validates the connection},
	 * then calls {@link #abort(java.util.concurrent.Executor)} instead when the connection is invalid.  This allows
	 * underlying pools to know the connection should not be reused.
	 * </p>
	 *
	 * @param  failFastCause  The fail-fast state before close.
	 */
	protected void doClose(Throwable failFastCause) throws SQLException {
		super.close();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Either this method or {@link #doClose(java.lang.Throwable)} is called, and at most once.  Once either is called,
	 * additional close/abort requests are ignored.
	 * </p>
	 * <p>
	 * This default implementation calls {@code super.abort(executor)}.
	 * </p>
	 *
	 * @param  failFastCause  The fail-fast state before close.
	 */
	protected void doAbort(Throwable failFastCause, Executor executor) throws SQLException {
		super.abort(executor);
	}
}
