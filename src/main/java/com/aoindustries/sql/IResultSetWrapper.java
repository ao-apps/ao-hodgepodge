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
import java.util.Calendar;

/**
 * Wraps a {@link ResultSet}.
 *
 * @author  AO Industries, Inc.
 */
public interface IResultSetWrapper extends ResultSet {

	/**
	 * Gets the results that are wrapped.
	 */
	ResultSet getWrappedResultSet();

	@Override
	default <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		ResultSet results = getWrappedResultSet();
		if(iface.isInstance(results)) return iface.cast(results);
		return results.unwrap(iface);
	}

	@Override
	default boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		ResultSet results = getWrappedResultSet();
		return iface.isInstance(results) || results.isWrapperFor(iface);
	}

	@Override
    default boolean next() throws SQLException {
		return getWrappedResultSet().next();
	}

	@Override
    default void close() throws SQLException {
		getWrappedResultSet().close();
	}

	@Override
    default boolean wasNull() throws SQLException {
		return getWrappedResultSet().wasNull();
	}

	@Override
    default String getString(int columnIndex) throws SQLException {
		return getWrappedResultSet().getString(columnIndex);
	}

	@Override
    default boolean getBoolean(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBoolean(columnIndex);
	}

	@Override
    default byte getByte(int columnIndex) throws SQLException {
		return getWrappedResultSet().getByte(columnIndex);
	}

	@Override
    default short getShort(int columnIndex) throws SQLException {
		return getWrappedResultSet().getShort(columnIndex);
	}

	@Override
    default int getInt(int columnIndex) throws SQLException {
		return getWrappedResultSet().getInt(columnIndex);
	}

	@Override
    default long getLong(int columnIndex) throws SQLException {
		return getWrappedResultSet().getLong(columnIndex);
	}

	@Override
    default float getFloat(int columnIndex) throws SQLException {
		return getWrappedResultSet().getFloat(columnIndex);
	}

	@Override
    default double getDouble(int columnIndex) throws SQLException {
		return getWrappedResultSet().getDouble(columnIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnIndex, scale);
	}

	@Override
    default byte[] getBytes(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBytes(columnIndex);
	}

	@Override
    default java.sql.Date getDate(int columnIndex) throws SQLException {
		return getWrappedResultSet().getDate(columnIndex);
	}

	@Override
    default java.sql.Time getTime(int columnIndex) throws SQLException {
		return getWrappedResultSet().getTime(columnIndex);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnIndex);
	}

	@Override
    default java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getAsciiStream(columnIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getUnicodeStream(columnIndex);
	}

	@Override
    default java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBinaryStream(columnIndex);
	}

	@Override
    default String getString(String columnLabel) throws SQLException {
		return getWrappedResultSet().getString(columnLabel);
	}

	@Override
    default boolean getBoolean(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBoolean(columnLabel);
	}

	@Override
    default byte getByte(String columnLabel) throws SQLException {
		return getWrappedResultSet().getByte(columnLabel);
	}

	@Override
    default short getShort(String columnLabel) throws SQLException {
		return getWrappedResultSet().getShort(columnLabel);
	}

	@Override
    default int getInt(String columnLabel) throws SQLException {
		return getWrappedResultSet().getInt(columnLabel);
	}

	@Override
    default long getLong(String columnLabel) throws SQLException {
		return getWrappedResultSet().getLong(columnLabel);
	}

	@Override
    default float getFloat(String columnLabel) throws SQLException {
		return getWrappedResultSet().getFloat(columnLabel);
	}

	@Override
    default double getDouble(String columnLabel) throws SQLException {
		return getWrappedResultSet().getDouble(columnLabel);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnLabel, scale);
	}

	@Override
    default byte[] getBytes(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBytes(columnLabel);
	}

	@Override
    default java.sql.Date getDate(String columnLabel) throws SQLException {
		return getWrappedResultSet().getDate(columnLabel);
	}

	@Override
    default java.sql.Time getTime(String columnLabel) throws SQLException {
		return getWrappedResultSet().getTime(columnLabel);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String columnLabel) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnLabel);
	}

	@Override
    default java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getAsciiStream(columnLabel);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getUnicodeStream(columnLabel);
	}

	@Override
    default java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBinaryStream(columnLabel);
	}

	@Override
    default SQLWarning getWarnings() throws SQLException {
		return getWrappedResultSet().getWarnings();
	}

	@Override
    default void clearWarnings() throws SQLException {
		getWrappedResultSet().clearWarnings();
	}

	@Override
    default String getCursorName() throws SQLException {
		return getWrappedResultSet().getCursorName();
	}

	@Override
    default ResultSetMetaData getMetaData() throws SQLException {
		return getWrappedResultSet().getMetaData();
	}

	@Override
    default Object getObject(int columnIndex) throws SQLException {
		return getWrappedResultSet().getObject(columnIndex);
	}

	@Override
    default Object getObject(String columnLabel) throws SQLException {
		return getWrappedResultSet().getObject(columnLabel);
	}

	@Override
    default int findColumn(String columnLabel) throws SQLException {
		return getWrappedResultSet().findColumn(columnLabel);
	}

	@Override
    default java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getCharacterStream(columnIndex);
	}

	@Override
    default java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getCharacterStream(columnLabel);
	}

	@Override
    default BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnIndex);
	}

	@Override
    default BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBigDecimal(columnLabel);
	}

	@Override
    default boolean isBeforeFirst() throws SQLException {
		return getWrappedResultSet().isBeforeFirst();
	}

	@Override
    default boolean isAfterLast() throws SQLException {
		return getWrappedResultSet().isAfterLast();
	}

	@Override
    default boolean isFirst() throws SQLException {
		return getWrappedResultSet().isFirst();
	}

	@Override
    default boolean isLast() throws SQLException {
		return getWrappedResultSet().isLast();
	}

	@Override
    default void beforeFirst() throws SQLException {
		getWrappedResultSet().beforeFirst();
	}

	@Override
    default void afterLast() throws SQLException {
		getWrappedResultSet().afterLast();
	}

	@Override
    default boolean first() throws SQLException {
		return getWrappedResultSet().first();
	}

	@Override
    default boolean last() throws SQLException {
		return getWrappedResultSet().last();
	}

	@Override
    default int getRow() throws SQLException {
		return getWrappedResultSet().getRow();
	}

	@Override
    default boolean absolute(int row) throws SQLException {
		return getWrappedResultSet().absolute(row);
	}

	@Override
    default boolean relative(int rows) throws SQLException {
		return getWrappedResultSet().relative(rows);
	}

	@Override
    default boolean previous() throws SQLException {
		return getWrappedResultSet().previous();
	}

	@Override
    default void setFetchDirection(int direction) throws SQLException {
		getWrappedResultSet().setFetchDirection(direction);
	}

	@Override
    default int getFetchDirection() throws SQLException {
		return getWrappedResultSet().getFetchDirection();
	}

	@Override
    default void setFetchSize(int rows) throws SQLException {
		getWrappedResultSet().setFetchSize(rows);
	}

	@Override
    default int getFetchSize() throws SQLException {
		return getWrappedResultSet().getFetchSize();
	}

	@Override
    default int getType() throws SQLException {
		return getWrappedResultSet().getType();
	}

	@Override
    default int getConcurrency() throws SQLException {
		return getWrappedResultSet().getConcurrency();
	}

	@Override
    default boolean rowUpdated() throws SQLException {
		return getWrappedResultSet().rowUpdated();
	}

	@Override
    default boolean rowInserted() throws SQLException {
		return getWrappedResultSet().rowInserted();
	}

	@Override
    default boolean rowDeleted() throws SQLException {
		return getWrappedResultSet().rowDeleted();
	}

	@Override
    default void updateNull(int columnIndex) throws SQLException {
		getWrappedResultSet().updateNull(columnIndex);
	}

	@Override
    default void updateBoolean(int columnIndex, boolean x) throws SQLException {
		getWrappedResultSet().updateBoolean(columnIndex, x);
	}

	@Override
    default void updateByte(int columnIndex, byte x) throws SQLException {
		getWrappedResultSet().updateByte(columnIndex, x);
	}

	@Override
    default void updateShort(int columnIndex, short x) throws SQLException {
		getWrappedResultSet().updateShort(columnIndex, x);
	}

	@Override
    default void updateInt(int columnIndex, int x) throws SQLException {
		getWrappedResultSet().updateInt(columnIndex, x);
	}

	@Override
    default void updateLong(int columnIndex, long x) throws SQLException {
		getWrappedResultSet().updateLong(columnIndex, x);
	}

	@Override
    default void updateFloat(int columnIndex, float x) throws SQLException {
		getWrappedResultSet().updateFloat(columnIndex, x);
	}

	@Override
    default void updateDouble(int columnIndex, double x) throws SQLException {
		getWrappedResultSet().updateDouble(columnIndex, x);
	}

	@Override
    default void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		getWrappedResultSet().updateBigDecimal(columnIndex, x);
	}

	@Override
    default void updateString(int columnIndex, String x) throws SQLException {
		getWrappedResultSet().updateString(columnIndex, x);
	}

	@Override
    default void updateBytes(int columnIndex, byte x[]) throws SQLException {
		getWrappedResultSet().updateBytes(columnIndex, x);
	}

	@Override
    default void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
		getWrappedResultSet().updateDate(columnIndex, x);
	}

	@Override
    default void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
		getWrappedResultSet().updateTime(columnIndex, x);
	}

	@Override
    default void updateTimestamp(int columnIndex, java.sql.Timestamp x) throws SQLException {
		getWrappedResultSet().updateTimestamp(columnIndex, x);
	}

	@Override
    default void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnIndex, x, length);
	}

	@Override
    default void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnIndex, x, length);
	}

	@Override
    default void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnIndex, x, length);
	}

	@Override
    default void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
    default void updateObject(int columnIndex, Object x) throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x);
	}

	@Override
    default void updateNull(String columnLabel) throws SQLException {
		getWrappedResultSet().updateNull(columnLabel);
	}

	@Override
    default void updateBoolean(String columnLabel, boolean x) throws SQLException {
		getWrappedResultSet().updateBoolean(columnLabel, x);
	}

	@Override
    default void updateByte(String columnLabel, byte x) throws SQLException {
		getWrappedResultSet().updateByte(columnLabel, x);
	}

	@Override
    default void updateShort(String columnLabel, short x) throws SQLException {
		getWrappedResultSet().updateShort(columnLabel, x);
	}

	@Override
    default void updateInt(String columnLabel, int x) throws SQLException {
		getWrappedResultSet().updateInt(columnLabel, x);
	}

	@Override
    default void updateLong(String columnLabel, long x) throws SQLException {
		getWrappedResultSet().updateLong(columnLabel, x);
	}

	@Override
    default void updateFloat(String columnLabel, float x) throws SQLException {
		getWrappedResultSet().updateFloat(columnLabel, x);
	}

	@Override
    default void updateDouble(String columnLabel, double x) throws SQLException {
		getWrappedResultSet().updateDouble(columnLabel, x);
	}

	@Override
    default void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		getWrappedResultSet().updateBigDecimal(columnLabel, x);
	}

	@Override
    default void updateString(String columnLabel, String x) throws SQLException {
		getWrappedResultSet().updateString(columnLabel, x);
	}

	@Override
    default void updateBytes(String columnLabel, byte x[]) throws SQLException {
		getWrappedResultSet().updateBytes(columnLabel, x);
	}

	@Override
    default void updateDate(String columnLabel, java.sql.Date x) throws SQLException {
		getWrappedResultSet().updateDate(columnLabel, x);
	}

	@Override
    default void updateTime(String columnLabel, java.sql.Time x) throws SQLException {
		getWrappedResultSet().updateTime(columnLabel, x);
	}

	@Override
    default void updateTimestamp(String columnLabel, java.sql.Timestamp x) throws SQLException {
		getWrappedResultSet().updateTimestamp(columnLabel, x);
	}

	@Override
    default void updateAsciiStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnLabel, x, length);
	}

	@Override
    default void updateBinaryStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnLabel, x, length);
	}

	@Override
    default void updateCharacterStream(String columnLabel, java.io.Reader reader, int length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnLabel, reader, length);
	}

	@Override
    default void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x, scaleOrLength);
	}

	@Override
    default void updateObject(String columnLabel, Object x) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x);
	}

	@Override
    default void insertRow() throws SQLException {
		getWrappedResultSet().insertRow();
	}

	@Override
    default void updateRow() throws SQLException {
		getWrappedResultSet().updateRow();
	}

	@Override
    default void deleteRow() throws SQLException {
		getWrappedResultSet().deleteRow();
	}

	@Override
    default void refreshRow() throws SQLException {
		getWrappedResultSet().refreshRow();
	}

	@Override
    default void cancelRowUpdates() throws SQLException {
		getWrappedResultSet().cancelRowUpdates();
	}

	@Override
    default void moveToInsertRow() throws SQLException {
		getWrappedResultSet().moveToInsertRow();
	}

	@Override
    default void moveToCurrentRow() throws SQLException {
		getWrappedResultSet().moveToCurrentRow();
	}

	@Override
    IStatementWrapper getStatement() throws SQLException;

	@Override
    default Object getObject(int columnIndex, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedResultSet().getObject(columnIndex, map);
	}

	@Override
    default Ref getRef(int columnIndex) throws SQLException {
		return getWrappedResultSet().getRef(columnIndex);
	}

	@Override
    default Blob getBlob(int columnIndex) throws SQLException {
		return getWrappedResultSet().getBlob(columnIndex);
	}

	@Override
    default Clob getClob(int columnIndex) throws SQLException {
		return getWrappedResultSet().getClob(columnIndex);
	}

	@Override
    IArrayWrapper getArray(int columnIndex) throws SQLException;

	@Override
    default Object getObject(String columnLabel, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedResultSet().getObject(columnLabel, map);
	}

	@Override
    default Ref getRef(String columnLabel) throws SQLException {
		return getWrappedResultSet().getRef(columnLabel);
	}

	@Override
    default Blob getBlob(String columnLabel) throws SQLException {
		return getWrappedResultSet().getBlob(columnLabel);
	}

	@Override
    default Clob getClob(String columnLabel) throws SQLException {
		return getWrappedResultSet().getClob(columnLabel);
	}

	@Override
    IArrayWrapper getArray(String columnLabel) throws SQLException;

	@Override
    default java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return getWrappedResultSet().getDate(columnIndex, cal);
	}

	@Override
    default java.sql.Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return getWrappedResultSet().getDate(columnLabel, cal);
	}

	@Override
    default java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTime(columnIndex, cal);
	}

	@Override
    default java.sql.Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTime(columnLabel, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnIndex, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return getWrappedResultSet().getTimestamp(columnLabel, cal);
	}

	@Override
    default java.net.URL getURL(int columnIndex) throws SQLException {
		return getWrappedResultSet().getURL(columnIndex);
	}

	@Override
    default java.net.URL getURL(String columnLabel) throws SQLException {
		return getWrappedResultSet().getURL(columnLabel);
	}

	@Override
    default void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
		getWrappedResultSet().updateRef(columnIndex, x);
	}

	@Override
    default void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
		getWrappedResultSet().updateRef(columnLabel, x);
	}

	@Override
    default void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
		getWrappedResultSet().updateBlob(columnIndex, x);
	}

	@Override
    default void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
		getWrappedResultSet().updateBlob(columnLabel, x);
	}

	@Override
    default void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
		getWrappedResultSet().updateClob(columnIndex, x);
	}

	@Override
    default void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
		getWrappedResultSet().updateClob(columnLabel, x);
	}

	@Override
    default void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
		getWrappedResultSet().updateArray(columnIndex, x);
	}

	@Override
    default void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
		getWrappedResultSet().updateArray(columnLabel, x);
	}

	@Override
    default RowId getRowId(int columnIndex) throws SQLException {
		return getWrappedResultSet().getRowId(columnIndex);
	}

	@Override
    default RowId getRowId(String columnLabel) throws SQLException {
		return getWrappedResultSet().getRowId(columnLabel);
	}

	@Override
    default void updateRowId(int columnIndex, RowId x) throws SQLException {
		getWrappedResultSet().updateRowId(columnIndex, x);
	}

	@Override
    default void updateRowId(String columnLabel, RowId x) throws SQLException {
		getWrappedResultSet().updateRowId(columnLabel, x);
	}

	@Override
    default int getHoldability() throws SQLException {
		return getWrappedResultSet().getHoldability();
	}

	@Override
    default boolean isClosed() throws SQLException {
		return getWrappedResultSet().isClosed();
	}

	@Override
    default void updateNString(int columnIndex, String nString) throws SQLException {
		getWrappedResultSet().updateNString(columnIndex, nString);
	}

	@Override
    default void updateNString(String columnLabel, String nString) throws SQLException {
		getWrappedResultSet().updateNString(columnLabel, nString);
	}

	@Override
    default void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		getWrappedResultSet().updateNClob(columnIndex, nClob);
	}

	@Override
    default void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		getWrappedResultSet().updateNClob(columnLabel, nClob);
	}

	@Override
    default NClob getNClob(int columnIndex) throws SQLException {
		return getWrappedResultSet().getNClob(columnIndex);
	}

	@Override
    default NClob getNClob(String columnLabel) throws SQLException {
		return getWrappedResultSet().getNClob(columnLabel);
	}

	@Override
    default SQLXML getSQLXML(int columnIndex) throws SQLException {
		return getWrappedResultSet().getSQLXML(columnIndex);
	}

	@Override
    default SQLXML getSQLXML(String columnLabel) throws SQLException {
		return getWrappedResultSet().getSQLXML(columnLabel);
	}

	@Override
    default void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		getWrappedResultSet().updateSQLXML(columnIndex, xmlObject);
	}

	@Override
    default void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		getWrappedResultSet().updateSQLXML(columnLabel, xmlObject);
	}

	@Override
    default String getNString(int columnIndex) throws SQLException {
		return getWrappedResultSet().getNString(columnIndex);
	}

	@Override
    default String getNString(String columnLabel) throws SQLException {
		return getWrappedResultSet().getNString(columnLabel);
	}

	@Override
    default java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
		return getWrappedResultSet().getNCharacterStream(columnIndex);
	}

	@Override
    default java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
		return getWrappedResultSet().getNCharacterStream(columnLabel);
	}

	@Override
    default void updateNCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnIndex, x, length);
	}

	@Override
    default void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnLabel, reader, length);
	}

	@Override
    default void updateAsciiStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnIndex, x, length);
	}

	@Override
    default void updateBinaryStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnIndex, x, length);
	}

	@Override
    default void updateCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnIndex, x, length);
	}

	@Override
    default void updateAsciiStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnLabel, x, length);
	}

	@Override
    default void updateBinaryStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnLabel, x, length);
	}

	@Override
    default void updateCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnLabel, reader, length);
	}

	@Override
    default void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		getWrappedResultSet().updateBlob(columnIndex, inputStream, length);
	}

	@Override
    default void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		getWrappedResultSet().updateBlob(columnLabel, inputStream, length);
	}

	@Override
    default void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateClob(columnIndex, reader, length);
	}

	@Override
    default void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateClob(columnLabel, reader, length);
	}

	@Override
    default void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateNClob(columnIndex, reader, length);
	}

	@Override
    default void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		getWrappedResultSet().updateNClob(columnLabel, reader, length);
	}

	@Override
    default void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnIndex, x);
	}

	@Override
    default void updateNCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
		getWrappedResultSet().updateNCharacterStream(columnLabel, reader);
	}

	@Override
    default void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnIndex, x);
	}

	@Override
    default void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnIndex, x);
	}

	@Override
    default void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnIndex, x);
	}

	@Override
    default void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateAsciiStream(columnLabel, x);
	}

	@Override
    default void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
		getWrappedResultSet().updateBinaryStream(columnLabel, x);
	}

	@Override
    default void updateCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
		getWrappedResultSet().updateCharacterStream(columnLabel, reader);
	}

	@Override
    default void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		getWrappedResultSet().updateBlob(columnIndex, inputStream);
	}

	@Override
    default void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		getWrappedResultSet().updateBlob(columnLabel, inputStream);
	}

	@Override
    default void updateClob(int columnIndex, Reader reader) throws SQLException {
		getWrappedResultSet().updateClob(columnIndex, reader);
	}

	@Override
    default void updateClob(String columnLabel, Reader reader) throws SQLException {
		getWrappedResultSet().updateClob(columnLabel, reader);
	}

	@Override
    default void updateNClob(int columnIndex, Reader reader) throws SQLException {
		getWrappedResultSet().updateNClob(columnIndex, reader);
	}

	@Override
    default void updateNClob(String columnLabel, Reader reader) throws SQLException {
		getWrappedResultSet().updateNClob(columnLabel, reader);
	}

	@Override
	default <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return getWrappedResultSet().getObject(columnIndex, type);
	}

	@Override
	default <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return getWrappedResultSet().getObject(columnLabel, type);
	}

	@Override
	default void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)  throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
    default void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x, targetSqlType, scaleOrLength);
	}

	@Override
    default void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
		getWrappedResultSet().updateObject(columnIndex, x, targetSqlType);
	}

	@Override
    default void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
		getWrappedResultSet().updateObject(columnLabel, x, targetSqlType);
	}
}
