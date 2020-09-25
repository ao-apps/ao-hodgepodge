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
import com.aoindustries.sql.wrapper.ConnectionWrapper;
import com.aoindustries.sql.wrapper.StatementWrapper;
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
// Note: Comment matches IFailFastConnection
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastConnection extends ConnectionWrapper implements IFailFastConnection {

	private static class FailFastLock {}
	private final FailFastLock failFastLock = new FailFastLock();

	/**
	 * The current fail-fast cause.  Any read-only operation may access it without synchronization.  All updates must
	 * be synchronized on {@link #failFastLock}, since updates are multi-step operations for managing suppressed
	 * throwables.
	 */
	private volatile Throwable failFastCause;

	public FailFastConnection(FailFastDriver driver, Connection wrapped) {
		super(driver, wrapped);
	}

	public FailFastConnection(Connection wrapped) {
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
					if(
						failFastCause == null
					) {
						failFastCause = cause;
					} else if(failFastCause instanceof TerminalSQLException) {
						if(!ThrowableUtil.isSuppressed(failFastCause, cause)) failFastCause.addSuppressed(cause);
					} else if(cause instanceof TerminalSQLException) {
						if(!ThrowableUtil.isSuppressed(cause, failFastCause)) cause.addSuppressed(failFastCause);
						failFastCause = cause;
					} else {
						failFastCause = Throwables.addSuppressed(failFastCause, cause);
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
	public Throwable clearFailFastCause() throws TerminalSQLException {
		synchronized(failFastLock) {
			Throwable cause = failFastCause;
			// Compare to the constants to distinguish from TerminalSQLException thrown by wrapped connections
			if(cause == ClosedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new ClosedSQLException();
			if(cause == AbortedSQLException.FAST_MARKER_KEEP_PRIVATE) throw new AbortedSQLException();
			failFastCause = null;
			return cause;
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
	protected FailFastArray newArrayWrapper(StatementWrapper stmtWrapper, Array array) {
		return new FailFastArray(this, (FailFastStatement)stmtWrapper, array);
	}

	@Override
	protected FailFastBlob newBlobWrapper(Blob blob) {
		return new FailFastBlob(this, blob);
	}

	@Override
	protected FailFastCallableStatement newCallableStatementWrapper(CallableStatement cstmt) {
		return new FailFastCallableStatement(this, cstmt);
	}

	@Override
	protected FailFastClob newClobWrapper(Clob clob) {
		return new FailFastClob(this, clob);
	}

	@Override
	protected FailFastDatabaseMetaData newDatabaseMetaDataWrapper(DatabaseMetaData metaData) {
		return new FailFastDatabaseMetaData(this, metaData);
	}

	@Override
	protected FailFastInputStream newInputStreamWrapper(InputStream in) {
		return new FailFastInputStream(this, in);
	}

	@Override
	protected FailFastNClob newNClobWrapper(NClob nclob) {
		return new FailFastNClob(this, nclob);
	}

	@Override
	protected FailFastOutputStream newOutputStreamWrapper(OutputStream out) {
		return new FailFastOutputStream(this, out);
	}

	@Override
	protected FailFastParameterMetaData newParameterMetaDataWrapper(ParameterMetaData metaData) {
		return new FailFastParameterMetaData(this, metaData);
	}

	@Override
	protected FailFastPreparedStatement newPreparedStatementWrapper(PreparedStatement pstmt) {
		return new FailFastPreparedStatement(this, pstmt);
	}

	@Override
	protected FailFastReader newReaderWrapper(Reader in) {
		return new FailFastReader(this, in);
	}

	@Override
	protected FailFastRef newRefWrapper(Ref ref) {
		return new FailFastRef(this, ref);
	}

	@Override
	protected FailFastResultSet newResultSetWrapper(StatementWrapper stmtWrapper, ResultSet results) {
		return new FailFastResultSet(this, (FailFastStatement)stmtWrapper, results);
	}

	@Override
	protected FailFastResultSetMetaData newResultSetMetaDataWrapper(ResultSetMetaData metaData) {
		return new FailFastResultSetMetaData(this, metaData);
	}

	@Override
	protected FailFastRowId newRowIdWrapper(RowId rowId) {
		return new FailFastRowId(this, rowId);
	}

	@Override
	protected FailFastSQLInput newSQLInputWrapper(SQLInput sqlInput) {
		return new FailFastSQLInput(this, sqlInput);
	}

	@Override
	protected FailFastSQLOutput newSQLOutputWrapper(SQLOutput sqlOutput) {
		return new FailFastSQLOutput(this, sqlOutput);
	}

	@Override
	protected FailFastSQLXML newSQLXMLWrapper(SQLXML sqlXml) {
		return new FailFastSQLXML(this, sqlXml);
	}

	@Override
	protected FailFastSavepoint newSavepointWrapper(Savepoint savepoint) {
		return new FailFastSavepoint(this, savepoint);
	}

	@Override
	protected FailFastStatement newStatementWrapper(Statement stmt) {
		return new FailFastStatement(this, stmt);
	}

	@Override
	protected FailFastStruct newStructWrapper(Struct struct) {
		return new FailFastStruct(this, struct);
	}

	@Override
	protected FailFastWriter newWriterWrapper(Writer out) {
		return new FailFastWriter(this, out);
	}

	@Override
	public FailFastStatement createStatement() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStatement)super.createStatement();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatement prepareStatement(String sql) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatement)super.prepareStatement(sql);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastCallableStatement prepareCall(String sql) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastCallableStatement)super.prepareCall(sql);
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
					clearFailFastCause();
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
		synchronized(failFastLock) {
			if(
				failFastCause != ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
				&& failFastCause != AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
			) {
				failFastCause = ClosedSQLException.FAST_MARKER_KEEP_PRIVATE;
			}
		}
		super.close();
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
	public FailFastDatabaseMetaData getMetaData() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastDatabaseMetaData)super.getMetaData();
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
	public FailFastStatement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStatement)super.createStatement(resultSetType, resultSetConcurrency);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatement)super.prepareStatement(sql, resultSetType, resultSetConcurrency);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastCallableStatement)super.prepareCall(sql, resultSetType, resultSetConcurrency);
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
	public FailFastSavepoint setSavepoint() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastSavepoint)super.setSavepoint();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastSavepoint setSavepoint(String name) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastSavepoint)super.setSavepoint(name);
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
					clearFailFastCause();
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
	public FailFastStatement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStatement)super.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatement)super.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastCallableStatement)super.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatement)super.prepareStatement(sql, autoGeneratedKeys);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatement)super.prepareStatement(sql, columnIndexes);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastPreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastPreparedStatement)super.prepareStatement(sql, columnNames);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastClob createClob() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastClob)super.createClob();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastBlob createBlob() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastBlob)super.createBlob();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastNClob createNClob() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastNClob)super.createNClob();
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastSQLXML createSQLXML() throws SQLException {
		failFastSQLException();
		try {
			return (FailFastSQLXML)super.createSQLXML();
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

	private static Map<String,ClientInfoStatus> asMap(Properties props) {
		Map<String,ClientInfoStatus> map = AoCollections.newHashMap(props.size());
		for(Object key : props.keySet()) {
			if(key instanceof String) map.put((String)key, ClientInfoStatus.REASON_UNKNOWN);
		}
		return map;
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		failFastSQLClientInfoException(() -> asMap(properties));
		try {
			super.setClientInfo(properties);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(
				t,
				SQLClientInfoException.class,
				cause -> new SQLClientInfoException(
					asMap(properties),
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
	public FailFastArray createArrayOf(String typeName, Object[] elements) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastArray)super.createArrayOf(typeName, elements);
		} catch(Throwable t) {
			addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastStruct createStruct(String typeName, Object[] attributes) throws SQLException {
		failFastSQLException();
		try {
			return (FailFastStruct)super.createStruct(typeName, attributes);
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
		synchronized(failFastLock) {
			if(
				failFastCause != ClosedSQLException.FAST_MARKER_KEEP_PRIVATE
				&& failFastCause != AbortedSQLException.FAST_MARKER_KEEP_PRIVATE
			) {
				failFastCause = AbortedSQLException.FAST_MARKER_KEEP_PRIVATE;
			}
		}
		super.abort(executor);
	}
}
