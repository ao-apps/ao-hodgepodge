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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Optional;

/**
 * Wraps a {@link ResultSet}.
 *
 * @author  AO Industries, Inc.
 */
public class ResultSetWrapper implements IResultSetWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final StatementWrapper stmtWrapper;
	private final ResultSet wrapped;

	public ResultSetWrapper(ConnectionWrapper connectionWrapper, StatementWrapper stmtWrapper, ResultSet wrapped) {
		this.connectionWrapper = connectionWrapper;
		this.stmtWrapper = stmtWrapper;
		this.wrapped = wrapped;
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}

	/**
	 * Gets the statement wrapper.
	 */
	protected Optional<? extends StatementWrapper> getStatementWrapper() {
		return Optional.ofNullable(stmtWrapper);
	}

	@Override
	public ResultSet getWrappedResultSet() {
		return wrapped;
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#newArrayWrapper(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 * @see  #unwrapArray(java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		if(array == null) {
			return null;
		}
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		StatementWrapper resultsStmtWrapper = getStatementWrapper().orElse(null);
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(
				arrayWrapper.getConnectionWrapper() == _connectionWrapper
				&& arrayWrapper.getStatementWrapper().orElse(null) == resultsStmtWrapper
			) {
				return arrayWrapper;
			}
		}
		return _connectionWrapper.newArrayWrapper(resultsStmtWrapper, array);
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
			if(arrayWrapper.getConnectionWrapper() == getConnectionWrapper()) {
				return arrayWrapper.getWrappedArray();
			}
		}
		return array;
	}

	/**
	 * Wraps a {@link Statement}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper wrapStatement(Statement stmt) {
		if(stmt == null) {
			return null;
		}
		StatementWrapper _stmtWrapper = getStatementWrapper().orElse(null);
		if(
			_stmtWrapper != null
			&& (
		 		_stmtWrapper == stmt
		 		|| _stmtWrapper.getWrappedStatement() == stmt
			)
		) {
			return _stmtWrapper;
		} else {
			return getConnectionWrapper().wrapStatement(stmt);
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		ResultSet results = getWrappedResultSet();
		if(iface.isInstance(results)) return iface.cast(results);
		return results.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		ResultSet results = getWrappedResultSet();
		return iface.isInstance(results) || results.isWrapperFor(iface);
	}

	@Override
    public boolean next() throws SQLException {
		return getWrappedResultSet().next();
	}

	@Override
    public void close() throws SQLException {
		getWrappedResultSet().close();
	}

	@Override
    public boolean wasNull() throws SQLException {
		return getWrappedResultSet().wasNull();
	}

	@Override
    public String getString(int columnIndex) throws SQLException {
		return getWrappedResultSet().getString(columnIndex);
	}

	@Override
    public boolean getBoolean(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBoolean(columnIndex);
	}

	@Override
    public byte getByte(int columnIndex) throws SQLException {
		return getWrappedResultSet().getByte(columnIndex);
	}

	@Override
    public short getShort(int columnIndex) throws SQLException {
		return getWrappedResultSet().getShort(columnIndex);
	}

	@Override
    public int getInt(int columnIndex) throws SQLException {
		return getWrappedResultSet().getInt(columnIndex);
	}

	@Override
    public long getLong(int columnIndex) throws SQLException {
		return getWrappedResultSet().getLong(columnIndex);
	}

	@Override
    public float getFloat(int columnIndex) throws SQLException {
		return getWrappedResultSet().getFloat(columnIndex);
	}

	@Override
    public double getDouble(int columnIndex) throws SQLException {
		return getWrappedResultSet().getDouble(columnIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnIndex, scale);
	}

	@Override
    public byte[] getBytes(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBytes(columnIndex);
	}

	@Override
    public java.sql.Date getDate(int columnIndex) throws SQLException {
		return getWrappedResultSet().getDate(columnIndex);
	}

	@Override
    public java.sql.Time getTime(int columnIndex) throws SQLException {
		return getWrappedResultSet().getTime(columnIndex);
	}

	@Override
    public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnIndex);
	}

	@Override
    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getAsciiStream(columnIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getUnicodeStream(columnIndex);
	}

	@Override
    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBinaryStream(columnIndex);
	}

	@Override
    public String getString(String columnLabel) throws SQLException {
		return getWrappedResultSet().getString(columnLabel);
	}

	@Override
    public boolean getBoolean(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBoolean(columnLabel);
	}

	@Override
    public byte getByte(String columnLabel) throws SQLException {
		return getWrappedResultSet().getByte(columnLabel);
	}

	@Override
    public short getShort(String columnLabel) throws SQLException {
		return getWrappedResultSet().getShort(columnLabel);
	}

	@Override
    public int getInt(String columnLabel) throws SQLException {
		return getWrappedResultSet().getInt(columnLabel);
	}

	@Override
    public long getLong(String columnLabel) throws SQLException {
		return getWrappedResultSet().getLong(columnLabel);
	}

	@Override
    public float getFloat(String columnLabel) throws SQLException {
		return getWrappedResultSet().getFloat(columnLabel);
	}

	@Override
    public double getDouble(String columnLabel) throws SQLException {
		return getWrappedResultSet().getDouble(columnLabel);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnLabel, scale);
	}

	@Override
    public byte[] getBytes(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBytes(columnLabel);
	}

	@Override
    public java.sql.Date getDate(String columnLabel) throws SQLException {
		return getWrappedResultSet().getDate(columnLabel);
	}

	@Override
    public java.sql.Time getTime(String columnLabel) throws SQLException {
		return getWrappedResultSet().getTime(columnLabel);
	}

	@Override
    public java.sql.Timestamp getTimestamp(String columnLabel) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnLabel);
	}

	@Override
    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getAsciiStream(columnLabel);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getUnicodeStream(columnLabel);
	}

	@Override
    public java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBinaryStream(columnLabel);
	}

	@Override
    public SQLWarning getWarnings() throws SQLException {
		return getWrappedResultSet().getWarnings();
	}

	@Override
    public void clearWarnings() throws SQLException {
		getWrappedResultSet().clearWarnings();
	}

	@Override
    public String getCursorName() throws SQLException {
		return getWrappedResultSet().getCursorName();
	}

	@Override
    public ResultSetMetaData getMetaData() throws SQLException {
		return getWrappedResultSet().getMetaData();
	}

	@Override
    public Object getObject(int columnIndex) throws SQLException {
		return getWrappedResultSet().getObject(columnIndex);
	}

	@Override
    public Object getObject(String columnLabel) throws SQLException {
		return getWrappedResultSet().getObject(columnLabel);
	}

	@Override
    public int findColumn(String columnLabel) throws SQLException {
		return getWrappedResultSet().findColumn(columnLabel);
	}

	@Override
    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getCharacterStream(columnIndex);
	}

	@Override
    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getCharacterStream(columnLabel);
	}

	@Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnIndex);
	}

	@Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnLabel);
	}

	@Override
    public boolean isBeforeFirst() throws SQLException {
		return getWrappedResultSet().isBeforeFirst();
	}

	@Override
    public boolean isAfterLast() throws SQLException {
		return getWrappedResultSet().isAfterLast();
	}

	@Override
    public boolean isFirst() throws SQLException {
		return getWrappedResultSet().isFirst();
	}

	@Override
    public boolean isLast() throws SQLException {
		return getWrappedResultSet().isLast();
	}

	@Override
    public void beforeFirst() throws SQLException {
		getWrappedResultSet().beforeFirst();
	}

	@Override
    public void afterLast() throws SQLException {
		getWrappedResultSet().afterLast();
	}

	@Override
    public boolean first() throws SQLException {
		return getWrappedResultSet().first();
	}

	@Override
    public boolean last() throws SQLException {
		return getWrappedResultSet().last();
	}

	@Override
    public int getRow() throws SQLException {
		return getWrappedResultSet().getRow();
	}

	@Override
    public boolean absolute(int row) throws SQLException {
		return getWrappedResultSet().absolute(row);
	}

	@Override
    public boolean relative(int rows) throws SQLException {
		return getWrappedResultSet().relative(rows);
	}

	@Override
    public boolean previous() throws SQLException {
		return getWrappedResultSet().previous();
	}

	@Override
    public void setFetchDirection(int direction) throws SQLException {
		getWrappedResultSet().setFetchDirection(direction);
	}

	@Override
    public int getFetchDirection() throws SQLException {
		return getWrappedResultSet().getFetchDirection();
	}

	@Override
    public void setFetchSize(int rows) throws SQLException {
		getWrappedResultSet().setFetchSize(rows);
	}

	@Override
    public int getFetchSize() throws SQLException {
		return getWrappedResultSet().getFetchSize();
	}

	@Override
    public int getType() throws SQLException {
		return getWrappedResultSet().getType();
	}

	@Override
    public int getConcurrency() throws SQLException {
		return getWrappedResultSet().getConcurrency();
	}

	@Override
    public boolean rowUpdated() throws SQLException {
		return getWrappedResultSet().rowUpdated();
	}

	@Override
    public boolean rowInserted() throws SQLException {
		return getWrappedResultSet().rowInserted();
	}

	@Override
    public boolean rowDeleted() throws SQLException {
		return getWrappedResultSet().rowDeleted();
	}

	@Override
    public void updateNull(int columnIndex) throws SQLException {
		getWrappedResultSet().updateNull(columnIndex);
	}

	@Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		getWrappedResultSet().updateBoolean(columnIndex, x);
	}

	@Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
		getWrappedResultSet().updateByte(columnIndex, x);
	}

	@Override
    public void updateShort(int columnIndex, short x) throws SQLException {
		getWrappedResultSet().updateShort(columnIndex, x);
	}

	@Override
    public void updateInt(int columnIndex, int x) throws SQLException {
		getWrappedResultSet().updateInt(columnIndex, x);
	}

	@Override
    public void updateLong(int columnIndex, long x) throws SQLException {
		getWrappedResultSet().updateLong(columnIndex, x);
	}

	@Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
		getWrappedResultSet().updateFloat(columnIndex, x);
	}

	@Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
		getWrappedResultSet().updateDouble(columnIndex, x);
	}

	@Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		getWrappedResultSet().updateBigDecimal(columnIndex, x);
	}

	@Override
    public void updateString(int columnIndex, String x) throws SQLException {
		getWrappedResultSet().updateString(columnIndex, x);
	}

	@Override
    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
		getWrappedResultSet().updateBytes(columnIndex, x);
	}

	@Override
    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
		getWrappedResultSet().updateDate(columnIndex, x);
	}

	@Override
    public void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
		getWrappedResultSet().updateTime(columnIndex, x);
	}

	@Override
    public void updateTimestamp(int columnIndex, java.sql.Timestamp x) throws SQLException {
		getWrappedResultSet().updateTimestamp(columnIndex, x);
	}

	@Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnIndex, x, length);
	}

	@Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnIndex, x, length);
	}

	@Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnIndex, x, length);
	}

	@Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x);
	}

	@Override
    public void updateNull(String columnLabel) throws SQLException {
		getWrappedResultSet().updateNull(columnLabel);
	}

	@Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		getWrappedResultSet().updateBoolean(columnLabel, x);
	}

	@Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
		getWrappedResultSet().updateByte(columnLabel, x);
	}

	@Override
    public void updateShort(String columnLabel, short x) throws SQLException {
		getWrappedResultSet().updateShort(columnLabel, x);
	}

	@Override
    public void updateInt(String columnLabel, int x) throws SQLException {
		getWrappedResultSet().updateInt(columnLabel, x);
	}

	@Override
    public void updateLong(String columnLabel, long x) throws SQLException {
		getWrappedResultSet().updateLong(columnLabel, x);
	}

	@Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
		getWrappedResultSet().updateFloat(columnLabel, x);
	}

	@Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
		getWrappedResultSet().updateDouble(columnLabel, x);
	}

	@Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		getWrappedResultSet().updateBigDecimal(columnLabel, x);
	}

	@Override
    public void updateString(String columnLabel, String x) throws SQLException {
		getWrappedResultSet().updateString(columnLabel, x);
	}

	@Override
    public void updateBytes(String columnLabel, byte x[]) throws SQLException {
		getWrappedResultSet().updateBytes(columnLabel, x);
	}

	@Override
    public void updateDate(String columnLabel, java.sql.Date x) throws SQLException {
		getWrappedResultSet().updateDate(columnLabel, x);
	}

	@Override
    public void updateTime(String columnLabel, java.sql.Time x) throws SQLException {
		getWrappedResultSet().updateTime(columnLabel, x);
	}

	@Override
    public void updateTimestamp(String columnLabel, java.sql.Timestamp x) throws SQLException {
		getWrappedResultSet().updateTimestamp(columnLabel, x);
	}

	@Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnLabel, x, length);
	}

	@Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnLabel, x, length);
	}

	@Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnLabel, reader, length);
	}

	@Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x, scaleOrLength);
	}

	@Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x);
	}

	@Override
    public void insertRow() throws SQLException {
		getWrappedResultSet().insertRow();
	}

	@Override
    public void updateRow() throws SQLException {
		getWrappedResultSet().updateRow();
	}

	@Override
    public void deleteRow() throws SQLException {
		getWrappedResultSet().deleteRow();
	}

	@Override
    public void refreshRow() throws SQLException {
		getWrappedResultSet().refreshRow();
	}

	@Override
    public void cancelRowUpdates() throws SQLException {
		getWrappedResultSet().cancelRowUpdates();
	}

	@Override
    public void moveToInsertRow() throws SQLException {
		getWrappedResultSet().moveToInsertRow();
	}

	@Override
    public void moveToCurrentRow() throws SQLException {
		getWrappedResultSet().moveToCurrentRow();
	}

	@Override
    public StatementWrapper getStatement() throws SQLException {
		return wrapStatement(getWrappedResultSet().getStatement());
	}

	@Override
    public Object getObject(int columnIndex, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedResultSet().getObject(columnIndex, map);
	}

	@Override
    public Ref getRef(int columnIndex) throws SQLException {
		return getWrappedResultSet().getRef(columnIndex);
	}

	@Override
    public Blob getBlob(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBlob(columnIndex);
	}

	@Override
    public Clob getClob(int columnIndex) throws SQLException {
		return getWrappedResultSet().getClob(columnIndex);
	}

	@Override
    public ArrayWrapper getArray(int columnIndex) throws SQLException {
		return wrapArray(getWrappedResultSet().getArray(columnIndex));
	}

	@Override
    public Object getObject(String columnLabel, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedResultSet().getObject(columnLabel, map);
	}

	@Override
    public Ref getRef(String columnLabel) throws SQLException {
		return getWrappedResultSet().getRef(columnLabel);
	}

	@Override
    public Blob getBlob(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBlob(columnLabel);
	}

	@Override
    public Clob getClob(String columnLabel) throws SQLException {
		return getWrappedResultSet().getClob(columnLabel);
	}

	@Override
    public ArrayWrapper getArray(String columnLabel) throws SQLException {
		return wrapArray(getWrappedResultSet().getArray(columnLabel));
	}

	@Override
    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return getWrappedResultSet().getDate(columnIndex, cal);
	}

	@Override
    public java.sql.Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return getWrappedResultSet().getDate(columnLabel, cal);
	}

	@Override
    public java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTime(columnIndex, cal);
	}

	@Override
    public java.sql.Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTime(columnLabel, cal);
	}

	@Override
    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnIndex, cal);
	}

	@Override
    public java.sql.Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnLabel, cal);
	}

	@Override
    public java.net.URL getURL(int columnIndex) throws SQLException {
		return getWrappedResultSet().getURL(columnIndex);
	}

	@Override
    public java.net.URL getURL(String columnLabel) throws SQLException {
		return getWrappedResultSet().getURL(columnLabel);
	}

	@Override
    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
		getWrappedResultSet().updateRef(columnIndex, x);
	}

	@Override
    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
		getWrappedResultSet().updateRef(columnLabel, x);
	}

	@Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
		getWrappedResultSet().updateBlob(columnIndex, x);
	}

	@Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
		getWrappedResultSet().updateBlob(columnLabel, x);
	}

	@Override
    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
		getWrappedResultSet().updateClob(columnIndex, x);
	}

	@Override
    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
		getWrappedResultSet().updateClob(columnLabel, x);
	}

	@Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
		getWrappedResultSet().updateArray(columnIndex, unwrapArray(x));
	}

	@Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
		getWrappedResultSet().updateArray(columnLabel, unwrapArray(x));
	}

	@Override
    public RowId getRowId(int columnIndex) throws SQLException {
		return getWrappedResultSet().getRowId(columnIndex);
	}

	@Override
    public RowId getRowId(String columnLabel) throws SQLException {
		return getWrappedResultSet().getRowId(columnLabel);
	}

	@Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
		getWrappedResultSet().updateRowId(columnIndex, x);
	}

	@Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
		getWrappedResultSet().updateRowId(columnLabel, x);
	}

	@Override
    public int getHoldability() throws SQLException {
		return getWrappedResultSet().getHoldability();
	}

	@Override
    public boolean isClosed() throws SQLException {
		return getWrappedResultSet().isClosed();
	}

	@Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
		getWrappedResultSet().updateNString(columnIndex, nString);
	}

	@Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
		getWrappedResultSet().updateNString(columnLabel, nString);
	}

	@Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		getWrappedResultSet().updateNClob(columnIndex, nClob);
	}

	@Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		getWrappedResultSet().updateNClob(columnLabel, nClob);
	}

	@Override
    public NClob getNClob(int columnIndex) throws SQLException {
		return getWrappedResultSet().getNClob(columnIndex);
	}

	@Override
    public NClob getNClob(String columnLabel) throws SQLException {
		return getWrappedResultSet().getNClob(columnLabel);
	}

	@Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return getWrappedResultSet().getSQLXML(columnIndex);
	}

	@Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return getWrappedResultSet().getSQLXML(columnLabel);
	}

	@Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		getWrappedResultSet().updateSQLXML(columnIndex, xmlObject);
	}

	@Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		getWrappedResultSet().updateSQLXML(columnLabel, xmlObject);
	}

	@Override
    public String getNString(int columnIndex) throws SQLException {
		return getWrappedResultSet().getNString(columnIndex);
	}

	@Override
    public String getNString(String columnLabel) throws SQLException {
		return getWrappedResultSet().getNString(columnLabel);
	}

	@Override
    public java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getNCharacterStream(columnIndex);
	}

	@Override
    public java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getNCharacterStream(columnLabel);
	}

	@Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnIndex, x, length);
	}

	@Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnLabel, reader, length);
	}

	@Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnIndex, x, length);
	}

	@Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnIndex, x, length);
	}

	@Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnIndex, x, length);
	}

	@Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnLabel, x, length);
	}

	@Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnLabel, x, length);
	}

	@Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnLabel, reader, length);
	}

	@Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		getWrappedResultSet().updateBlob(columnIndex, inputStream, length);
	}

	@Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		getWrappedResultSet().updateBlob(columnLabel, inputStream, length);
	}

	@Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateClob(columnIndex, reader, length);
	}

	@Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateClob(columnLabel, reader, length);
	}

	@Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateNClob(columnIndex, reader, length);
	}

	@Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateNClob(columnLabel, reader, length);
	}

	@Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnIndex, x);
	}

	@Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnLabel, reader);
	}

	@Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnIndex, x);
	}

	@Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnIndex, x);
	}

	@Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnIndex, x);
	}

	@Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnLabel, x);
	}

	@Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnLabel, x);
	}

	@Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnLabel, reader);
	}

	@Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		getWrappedResultSet().updateBlob(columnIndex, inputStream);
	}

	@Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		getWrappedResultSet().updateBlob(columnLabel, inputStream);
	}

	@Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
		getWrappedResultSet().updateClob(columnIndex, reader);
	}

	@Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
		getWrappedResultSet().updateClob(columnLabel, reader);
	}

	@Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		getWrappedResultSet().updateNClob(columnIndex, reader);
	}

	@Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		getWrappedResultSet().updateNClob(columnLabel, reader);
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return getWrappedResultSet().getObject(columnIndex, type);
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return getWrappedResultSet().getObject(columnLabel, type);
	}

	@Override
	public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)  throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x, targetSqlType, scaleOrLength);
	}

	@Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x, targetSqlType);
	}

	@Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x, targetSqlType);
	}
}
