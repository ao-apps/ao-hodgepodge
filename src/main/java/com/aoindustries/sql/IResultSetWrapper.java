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
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
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
public interface IResultSetWrapper extends IWrapper, ResultSet {

	/**
	 * Gets the results that are wrapped.
	 */
	@Override
	ResultSet getWrapped();

	@Override
    default boolean next() throws SQLException {
		return getWrapped().next();
	}

	@Override
    default void close() throws SQLException {
		getWrapped().close();
	}

	@Override
    default boolean wasNull() throws SQLException {
		return getWrapped().wasNull();
	}

	@Override
    default String getString(int columnIndex) throws SQLException {
		return getWrapped().getString(columnIndex);
	}

	@Override
    default boolean getBoolean(int columnIndex) throws SQLException {
		return getWrapped().getBoolean(columnIndex);
	}

	@Override
    default byte getByte(int columnIndex) throws SQLException {
		return getWrapped().getByte(columnIndex);
	}

	@Override
    default short getShort(int columnIndex) throws SQLException {
		return getWrapped().getShort(columnIndex);
	}

	@Override
    default int getInt(int columnIndex) throws SQLException {
		return getWrapped().getInt(columnIndex);
	}

	@Override
    default long getLong(int columnIndex) throws SQLException {
		return getWrapped().getLong(columnIndex);
	}

	@Override
    default float getFloat(int columnIndex) throws SQLException {
		return getWrapped().getFloat(columnIndex);
	}

	@Override
    default double getDouble(int columnIndex) throws SQLException {
		return getWrapped().getDouble(columnIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return getWrapped().getBigDecimal(columnIndex, scale);
	}

	@Override
    default byte[] getBytes(int columnIndex) throws SQLException {
		return getWrapped().getBytes(columnIndex);
	}

	@Override
    default java.sql.Date getDate(int columnIndex) throws SQLException {
		return getWrapped().getDate(columnIndex);
	}

	@Override
    default java.sql.Time getTime(int columnIndex) throws SQLException {
		return getWrapped().getTime(columnIndex);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
		return getWrapped().getTimestamp(columnIndex);
	}

	@Override
    default java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
		return getWrapped().getAsciiStream(columnIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return getWrapped().getUnicodeStream(columnIndex);
	}

	@Override
    default java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
		return getWrapped().getBinaryStream(columnIndex);
	}

	@Override
    default String getString(String columnLabel) throws SQLException {
		return getWrapped().getString(columnLabel);
	}

	@Override
    default boolean getBoolean(String columnLabel) throws SQLException {
		return getWrapped().getBoolean(columnLabel);
	}

	@Override
    default byte getByte(String columnLabel) throws SQLException {
		return getWrapped().getByte(columnLabel);
	}

	@Override
    default short getShort(String columnLabel) throws SQLException {
		return getWrapped().getShort(columnLabel);
	}

	@Override
    default int getInt(String columnLabel) throws SQLException {
		return getWrapped().getInt(columnLabel);
	}

	@Override
    default long getLong(String columnLabel) throws SQLException {
		return getWrapped().getLong(columnLabel);
	}

	@Override
    default float getFloat(String columnLabel) throws SQLException {
		return getWrapped().getFloat(columnLabel);
	}

	@Override
    default double getDouble(String columnLabel) throws SQLException {
		return getWrapped().getDouble(columnLabel);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return getWrapped().getBigDecimal(columnLabel, scale);
	}

	@Override
    default byte[] getBytes(String columnLabel) throws SQLException {
		return getWrapped().getBytes(columnLabel);
	}

	@Override
    default java.sql.Date getDate(String columnLabel) throws SQLException {
		return getWrapped().getDate(columnLabel);
	}

	@Override
    default java.sql.Time getTime(String columnLabel) throws SQLException {
		return getWrapped().getTime(columnLabel);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String columnLabel) throws SQLException {
		return getWrapped().getTimestamp(columnLabel);
	}

	@Override
    default java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
		return getWrapped().getAsciiStream(columnLabel);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return getWrapped().getUnicodeStream(columnLabel);
	}

	@Override
    default java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
		return getWrapped().getBinaryStream(columnLabel);
	}

	@Override
    default SQLWarning getWarnings() throws SQLException {
		return getWrapped().getWarnings();
	}

	@Override
    default void clearWarnings() throws SQLException {
		getWrapped().clearWarnings();
	}

	@Override
    default String getCursorName() throws SQLException {
		return getWrapped().getCursorName();
	}

	@Override
    IResultSetMetaDataWrapper getMetaData() throws SQLException;

	@Override
    default Object getObject(int columnIndex) throws SQLException {
		return getWrapped().getObject(columnIndex);
	}

	@Override
    default Object getObject(String columnLabel) throws SQLException {
		return getWrapped().getObject(columnLabel);
	}

	@Override
    default int findColumn(String columnLabel) throws SQLException {
		return getWrapped().findColumn(columnLabel);
	}

	@Override
    default java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
		return getWrapped().getCharacterStream(columnIndex);
	}

	@Override
    default java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
		return getWrapped().getCharacterStream(columnLabel);
	}

	@Override
    default BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return getWrapped().getBigDecimal(columnIndex);
	}

	@Override
    default BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return getWrapped().getBigDecimal(columnLabel);
	}

	@Override
    default boolean isBeforeFirst() throws SQLException {
		return getWrapped().isBeforeFirst();
	}

	@Override
    default boolean isAfterLast() throws SQLException {
		return getWrapped().isAfterLast();
	}

	@Override
    default boolean isFirst() throws SQLException {
		return getWrapped().isFirst();
	}

	@Override
    default boolean isLast() throws SQLException {
		return getWrapped().isLast();
	}

	@Override
    default void beforeFirst() throws SQLException {
		getWrapped().beforeFirst();
	}

	@Override
    default void afterLast() throws SQLException {
		getWrapped().afterLast();
	}

	@Override
    default boolean first() throws SQLException {
		return getWrapped().first();
	}

	@Override
    default boolean last() throws SQLException {
		return getWrapped().last();
	}

	@Override
    default int getRow() throws SQLException {
		return getWrapped().getRow();
	}

	@Override
    default boolean absolute(int row) throws SQLException {
		return getWrapped().absolute(row);
	}

	@Override
    default boolean relative(int rows) throws SQLException {
		return getWrapped().relative(rows);
	}

	@Override
    default boolean previous() throws SQLException {
		return getWrapped().previous();
	}

	@Override
    default void setFetchDirection(int direction) throws SQLException {
		getWrapped().setFetchDirection(direction);
	}

	@Override
    default int getFetchDirection() throws SQLException {
		return getWrapped().getFetchDirection();
	}

	@Override
    default void setFetchSize(int rows) throws SQLException {
		getWrapped().setFetchSize(rows);
	}

	@Override
    default int getFetchSize() throws SQLException {
		return getWrapped().getFetchSize();
	}

	@Override
    default int getType() throws SQLException {
		return getWrapped().getType();
	}

	@Override
    default int getConcurrency() throws SQLException {
		return getWrapped().getConcurrency();
	}

	@Override
    default boolean rowUpdated() throws SQLException {
		return getWrapped().rowUpdated();
	}

	@Override
    default boolean rowInserted() throws SQLException {
		return getWrapped().rowInserted();
	}

	@Override
    default boolean rowDeleted() throws SQLException {
		return getWrapped().rowDeleted();
	}

	@Override
    default void updateNull(int columnIndex) throws SQLException {
		getWrapped().updateNull(columnIndex);
	}

	@Override
    default void updateBoolean(int columnIndex, boolean x) throws SQLException {
		getWrapped().updateBoolean(columnIndex, x);
	}

	@Override
    default void updateByte(int columnIndex, byte x) throws SQLException {
		getWrapped().updateByte(columnIndex, x);
	}

	@Override
    default void updateShort(int columnIndex, short x) throws SQLException {
		getWrapped().updateShort(columnIndex, x);
	}

	@Override
    default void updateInt(int columnIndex, int x) throws SQLException {
		getWrapped().updateInt(columnIndex, x);
	}

	@Override
    default void updateLong(int columnIndex, long x) throws SQLException {
		getWrapped().updateLong(columnIndex, x);
	}

	@Override
    default void updateFloat(int columnIndex, float x) throws SQLException {
		getWrapped().updateFloat(columnIndex, x);
	}

	@Override
    default void updateDouble(int columnIndex, double x) throws SQLException {
		getWrapped().updateDouble(columnIndex, x);
	}

	@Override
    default void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		getWrapped().updateBigDecimal(columnIndex, x);
	}

	@Override
    default void updateString(int columnIndex, String x) throws SQLException {
		getWrapped().updateString(columnIndex, x);
	}

	@Override
    default void updateBytes(int columnIndex, byte x[]) throws SQLException {
		getWrapped().updateBytes(columnIndex, x);
	}

	@Override
    default void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
		getWrapped().updateDate(columnIndex, x);
	}

	@Override
    default void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
		getWrapped().updateTime(columnIndex, x);
	}

	@Override
    default void updateTimestamp(int columnIndex, java.sql.Timestamp x) throws SQLException {
		getWrapped().updateTimestamp(columnIndex, x);
	}

	@Override
    default void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
		getWrapped().updateAsciiStream(columnIndex, x, length);
	}

	@Override
    default void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
		getWrapped().updateBinaryStream(columnIndex, x, length);
	}

	@Override
    default void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
		getWrapped().updateCharacterStream(columnIndex, x, length);
	}

	@Override
    default void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		getWrapped().updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
    default void updateObject(int columnIndex, Object x) throws SQLException {
		getWrapped().updateObject(columnIndex, x);
	}

	@Override
    default void updateNull(String columnLabel) throws SQLException {
		getWrapped().updateNull(columnLabel);
	}

	@Override
    default void updateBoolean(String columnLabel, boolean x) throws SQLException {
		getWrapped().updateBoolean(columnLabel, x);
	}

	@Override
    default void updateByte(String columnLabel, byte x) throws SQLException {
		getWrapped().updateByte(columnLabel, x);
	}

	@Override
    default void updateShort(String columnLabel, short x) throws SQLException {
		getWrapped().updateShort(columnLabel, x);
	}

	@Override
    default void updateInt(String columnLabel, int x) throws SQLException {
		getWrapped().updateInt(columnLabel, x);
	}

	@Override
    default void updateLong(String columnLabel, long x) throws SQLException {
		getWrapped().updateLong(columnLabel, x);
	}

	@Override
    default void updateFloat(String columnLabel, float x) throws SQLException {
		getWrapped().updateFloat(columnLabel, x);
	}

	@Override
    default void updateDouble(String columnLabel, double x) throws SQLException {
		getWrapped().updateDouble(columnLabel, x);
	}

	@Override
    default void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		getWrapped().updateBigDecimal(columnLabel, x);
	}

	@Override
    default void updateString(String columnLabel, String x) throws SQLException {
		getWrapped().updateString(columnLabel, x);
	}

	@Override
    default void updateBytes(String columnLabel, byte x[]) throws SQLException {
		getWrapped().updateBytes(columnLabel, x);
	}

	@Override
    default void updateDate(String columnLabel, java.sql.Date x) throws SQLException {
		getWrapped().updateDate(columnLabel, x);
	}

	@Override
    default void updateTime(String columnLabel, java.sql.Time x) throws SQLException {
		getWrapped().updateTime(columnLabel, x);
	}

	@Override
    default void updateTimestamp(String columnLabel, java.sql.Timestamp x) throws SQLException {
		getWrapped().updateTimestamp(columnLabel, x);
	}

	@Override
    default void updateAsciiStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
		getWrapped().updateAsciiStream(columnLabel, x, length);
	}

	@Override
    default void updateBinaryStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
		getWrapped().updateBinaryStream(columnLabel, x, length);
	}

	@Override
    default void updateCharacterStream(String columnLabel, java.io.Reader reader, int length) throws SQLException {
		getWrapped().updateCharacterStream(columnLabel, reader, length);
	}

	@Override
    default void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		getWrapped().updateObject(columnLabel, x, scaleOrLength);
	}

	@Override
    default void updateObject(String columnLabel, Object x) throws SQLException {
		getWrapped().updateObject(columnLabel, x);
	}

	@Override
    default void insertRow() throws SQLException {
		getWrapped().insertRow();
	}

	@Override
    default void updateRow() throws SQLException {
		getWrapped().updateRow();
	}

	@Override
    default void deleteRow() throws SQLException {
		getWrapped().deleteRow();
	}

	@Override
    default void refreshRow() throws SQLException {
		getWrapped().refreshRow();
	}

	@Override
    default void cancelRowUpdates() throws SQLException {
		getWrapped().cancelRowUpdates();
	}

	@Override
    default void moveToInsertRow() throws SQLException {
		getWrapped().moveToInsertRow();
	}

	@Override
    default void moveToCurrentRow() throws SQLException {
		getWrapped().moveToCurrentRow();
	}

	@Override
    IStatementWrapper getStatement() throws SQLException;

	@Override
    default Object getObject(int columnIndex, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrapped().getObject(columnIndex, map);
	}

	@Override
    default Ref getRef(int columnIndex) throws SQLException {
		return getWrapped().getRef(columnIndex);
	}

	@Override
    IBlobWrapper getBlob(int columnIndex) throws SQLException;

	@Override
    default Clob getClob(int columnIndex) throws SQLException {
		return getWrapped().getClob(columnIndex);
	}

	@Override
    IArrayWrapper getArray(int columnIndex) throws SQLException;

	@Override
    default Object getObject(String columnLabel, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrapped().getObject(columnLabel, map);
	}

	@Override
    default Ref getRef(String columnLabel) throws SQLException {
		return getWrapped().getRef(columnLabel);
	}

	@Override
    IBlobWrapper getBlob(String columnLabel) throws SQLException;

	@Override
    default Clob getClob(String columnLabel) throws SQLException {
		return getWrapped().getClob(columnLabel);
	}

	@Override
    IArrayWrapper getArray(String columnLabel) throws SQLException;

	@Override
    default java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return getWrapped().getDate(columnIndex, cal);
	}

	@Override
    default java.sql.Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return getWrapped().getDate(columnLabel, cal);
	}

	@Override
    default java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return getWrapped().getTime(columnIndex, cal);
	}

	@Override
    default java.sql.Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return getWrapped().getTime(columnLabel, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return getWrapped().getTimestamp(columnIndex, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return getWrapped().getTimestamp(columnLabel, cal);
	}

	@Override
    default java.net.URL getURL(int columnIndex) throws SQLException {
		return getWrapped().getURL(columnIndex);
	}

	@Override
    default java.net.URL getURL(String columnLabel) throws SQLException {
		return getWrapped().getURL(columnLabel);
	}

	@Override
    default void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
		getWrapped().updateRef(columnIndex, x);
	}

	@Override
    default void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
		getWrapped().updateRef(columnLabel, x);
	}

	@Override
    void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException;

	@Override
    void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException;

	@Override
    default void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
		getWrapped().updateClob(columnIndex, x);
	}

	@Override
    default void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
		getWrapped().updateClob(columnLabel, x);
	}

	@Override
    void updateArray(int columnIndex, java.sql.Array x) throws SQLException;

	@Override
    void updateArray(String columnLabel, java.sql.Array x) throws SQLException;

	@Override
    default RowId getRowId(int columnIndex) throws SQLException {
		return getWrapped().getRowId(columnIndex);
	}

	@Override
    default RowId getRowId(String columnLabel) throws SQLException {
		return getWrapped().getRowId(columnLabel);
	}

	@Override
    default void updateRowId(int columnIndex, RowId x) throws SQLException {
		getWrapped().updateRowId(columnIndex, x);
	}

	@Override
    default void updateRowId(String columnLabel, RowId x) throws SQLException {
		getWrapped().updateRowId(columnLabel, x);
	}

	@Override
    default int getHoldability() throws SQLException {
		return getWrapped().getHoldability();
	}

	@Override
    default boolean isClosed() throws SQLException {
		return getWrapped().isClosed();
	}

	@Override
    default void updateNString(int columnIndex, String nString) throws SQLException {
		getWrapped().updateNString(columnIndex, nString);
	}

	@Override
    default void updateNString(String columnLabel, String nString) throws SQLException {
		getWrapped().updateNString(columnLabel, nString);
	}

	@Override
    default void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		getWrapped().updateNClob(columnIndex, nClob);
	}

	@Override
    default void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		getWrapped().updateNClob(columnLabel, nClob);
	}

	@Override
    default NClob getNClob(int columnIndex) throws SQLException {
		return getWrapped().getNClob(columnIndex);
	}

	@Override
    default NClob getNClob(String columnLabel) throws SQLException {
		return getWrapped().getNClob(columnLabel);
	}

	@Override
    default SQLXML getSQLXML(int columnIndex) throws SQLException {
		return getWrapped().getSQLXML(columnIndex);
	}

	@Override
    default SQLXML getSQLXML(String columnLabel) throws SQLException {
		return getWrapped().getSQLXML(columnLabel);
	}

	@Override
    default void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		getWrapped().updateSQLXML(columnIndex, xmlObject);
	}

	@Override
    default void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		getWrapped().updateSQLXML(columnLabel, xmlObject);
	}

	@Override
    default String getNString(int columnIndex) throws SQLException {
		return getWrapped().getNString(columnIndex);
	}

	@Override
    default String getNString(String columnLabel) throws SQLException {
		return getWrapped().getNString(columnLabel);
	}

	@Override
    default java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
		return getWrapped().getNCharacterStream(columnIndex);
	}

	@Override
    default java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
		return getWrapped().getNCharacterStream(columnLabel);
	}

	@Override
    default void updateNCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
		getWrapped().updateNCharacterStream(columnIndex, x, length);
	}

	@Override
    default void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
		getWrapped().updateNCharacterStream(columnLabel, reader, length);
	}

	@Override
    default void updateAsciiStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
		getWrapped().updateAsciiStream(columnIndex, x, length);
	}

	@Override
    default void updateBinaryStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
		getWrapped().updateBinaryStream(columnIndex, x, length);
	}

	@Override
    default void updateCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
		getWrapped().updateCharacterStream(columnIndex, x, length);
	}

	@Override
    default void updateAsciiStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
		getWrapped().updateAsciiStream(columnLabel, x, length);
	}

	@Override
    default void updateBinaryStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
		getWrapped().updateBinaryStream(columnLabel, x, length);
	}

	@Override
    default void updateCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
		getWrapped().updateCharacterStream(columnLabel, reader, length);
	}

	@Override
    default void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		getWrapped().updateBlob(columnIndex, inputStream, length);
	}

	@Override
    default void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		getWrapped().updateBlob(columnLabel, inputStream, length);
	}

	@Override
    default void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		getWrapped().updateClob(columnIndex, reader, length);
	}

	@Override
    default void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		getWrapped().updateClob(columnLabel, reader, length);
	}

	@Override
    default void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		getWrapped().updateNClob(columnIndex, reader, length);
	}

	@Override
    default void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		getWrapped().updateNClob(columnLabel, reader, length);
	}

	@Override
    default void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
		getWrapped().updateNCharacterStream(columnIndex, x);
	}

	@Override
    default void updateNCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
		getWrapped().updateNCharacterStream(columnLabel, reader);
	}

	@Override
    default void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
		getWrapped().updateAsciiStream(columnIndex, x);
	}

	@Override
    default void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
		getWrapped().updateBinaryStream(columnIndex, x);
	}

	@Override
    default void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
		getWrapped().updateCharacterStream(columnIndex, x);
	}

	@Override
    default void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
		getWrapped().updateAsciiStream(columnLabel, x);
	}

	@Override
    default void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
		getWrapped().updateBinaryStream(columnLabel, x);
	}

	@Override
    default void updateCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
		getWrapped().updateCharacterStream(columnLabel, reader);
	}

	@Override
    default void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		getWrapped().updateBlob(columnIndex, inputStream);
	}

	@Override
    default void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		getWrapped().updateBlob(columnLabel, inputStream);
	}

	@Override
    default void updateClob(int columnIndex, Reader reader) throws SQLException {
		getWrapped().updateClob(columnIndex, reader);
	}

	@Override
    default void updateClob(String columnLabel, Reader reader) throws SQLException {
		getWrapped().updateClob(columnLabel, reader);
	}

	@Override
    default void updateNClob(int columnIndex, Reader reader) throws SQLException {
		getWrapped().updateNClob(columnIndex, reader);
	}

	@Override
    default void updateNClob(String columnLabel, Reader reader) throws SQLException {
		getWrapped().updateNClob(columnLabel, reader);
	}

	@Override
	default <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return getWrapped().getObject(columnIndex, type);
	}

	@Override
	default <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return getWrapped().getObject(columnLabel, type);
	}

	@Override
	default void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)  throws SQLException {
		getWrapped().updateObject(columnIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
    default void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		getWrapped().updateObject(columnLabel, x, targetSqlType, scaleOrLength);
	}

	@Override
    default void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
		getWrapped().updateObject(columnIndex, x, targetSqlType);
	}

	@Override
    default void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
		getWrapped().updateObject(columnLabel, x, targetSqlType);
	}
}
