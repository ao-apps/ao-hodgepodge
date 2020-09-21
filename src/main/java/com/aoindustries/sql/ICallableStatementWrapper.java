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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.util.Calendar;

/**
 * Wraps a {@link CallableStatement}.
 *
 * @author  AO Industries, Inc.
 */
public interface ICallableStatementWrapper extends IPreparedStatementWrapper, CallableStatement {

	/**
	 * Gets the callable statement that is wrapped.
	 */
	@Override
	CallableStatement getWrappedStatement();

	@Override
	default void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	default void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
    default boolean wasNull() throws SQLException {
		return getWrappedStatement().wasNull();
	}

	@Override
    default String getString(int parameterIndex) throws SQLException {
		return getWrappedStatement().getString(parameterIndex);
	}

	@Override
    default boolean getBoolean(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBoolean(parameterIndex);
	}

	@Override
    default byte getByte(int parameterIndex) throws SQLException {
		return getWrappedStatement().getByte(parameterIndex);
	}

	@Override
    default short getShort(int parameterIndex) throws SQLException {
		return getWrappedStatement().getShort(parameterIndex);
	}

	@Override
    default int getInt(int parameterIndex) throws SQLException {
		return getWrappedStatement().getInt(parameterIndex);
	}

	@Override
    default long getLong(int parameterIndex) throws SQLException {
		return getWrappedStatement().getLong(parameterIndex);
	}

	@Override
    default float getFloat(int parameterIndex) throws SQLException {
		return getWrappedStatement().getFloat(parameterIndex);
	}

	@Override
    default double getDouble(int parameterIndex) throws SQLException {
		return getWrappedStatement().getDouble(parameterIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return getWrappedStatement().getBigDecimal(parameterIndex, scale);
	}

	@Override
    default byte[] getBytes(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBytes(parameterIndex);
	}

	@Override
    default java.sql.Date getDate(int parameterIndex) throws SQLException {
		return getWrappedStatement().getDate(parameterIndex);
	}

	@Override
    default java.sql.Time getTime(int parameterIndex) throws SQLException {
		return getWrappedStatement().getTime(parameterIndex);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterIndex);
	}

	@Override
    default Object getObject(int parameterIndex) throws SQLException {
		return getWrappedStatement().getObject(parameterIndex);
	}

	@Override
    default BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBigDecimal(parameterIndex);
	}

	@Override
    default Object getObject(int parameterIndex, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedStatement().getObject(parameterIndex, map);
	}

	@Override
    default Ref getRef(int parameterIndex) throws SQLException {
		return getWrappedStatement().getRef(parameterIndex);
	}

	@Override
    default Blob getBlob(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBlob(parameterIndex);
	}

	@Override
    default Clob getClob(int parameterIndex) throws SQLException {
		return getWrappedStatement().getClob(parameterIndex);
	}

	@Override
    IArrayWrapper getArray(int parameterIndex) throws SQLException;

	@Override
    default java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return getWrappedStatement().getDate(parameterIndex, cal);
	}

	@Override
    default java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return getWrappedStatement().getTime(parameterIndex, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterIndex, cal);
	}

	@Override
    default void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
    default void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType);
	}

	@Override
    default void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
    default void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
    default java.net.URL getURL(int parameterIndex) throws SQLException {
		return getWrappedStatement().getURL(parameterIndex);
	}

	@Override
    default void setURL(String parameterName, java.net.URL val) throws SQLException {
		getWrappedStatement().setURL(parameterName, val);
	}

	@Override
    default void setNull(String parameterName, int sqlType) throws SQLException {
		getWrappedStatement().setNull(parameterName, sqlType);
	}

	@Override
    default void setBoolean(String parameterName, boolean x) throws SQLException {
		getWrappedStatement().setBoolean(parameterName, x);
	}

	@Override
    default void setByte(String parameterName, byte x) throws SQLException {
		getWrappedStatement().setByte(parameterName, x);
	}

	@Override
    default void setShort(String parameterName, short x) throws SQLException {
		getWrappedStatement().setShort(parameterName, x);
	}

	@Override
    default void setInt(String parameterName, int x) throws SQLException {
		getWrappedStatement().setInt(parameterName, x);
	}

	@Override
    default void setLong(String parameterName, long x) throws SQLException {
		getWrappedStatement().setLong(parameterName, x);
	}

	@Override
    default void setFloat(String parameterName, float x) throws SQLException {
		getWrappedStatement().setFloat(parameterName, x);
	}

	@Override
    default void setDouble(String parameterName, double x) throws SQLException {
		getWrappedStatement().setDouble(parameterName, x);
	}

	@Override
    default void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		getWrappedStatement().setBigDecimal(parameterName, x);
	}

	@Override
    default void setString(String parameterName, String x) throws SQLException {
		getWrappedStatement().setString(parameterName, x);
	}

	@Override
    default void setBytes(String parameterName, byte x[]) throws SQLException {
		getWrappedStatement().setBytes(parameterName, x);
	}

	@Override
    default void setDate(String parameterName, java.sql.Date x) throws SQLException {
		getWrappedStatement().setDate(parameterName, x);
	}

	@Override
    default void setTime(String parameterName, java.sql.Time x)throws SQLException {
		getWrappedStatement().setTime(parameterName, x);
	}

	@Override
    default void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
		getWrappedStatement().setTimestamp(parameterName, x);
	}

	@Override
    default void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterName, x, length);
	}

	@Override
    default void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterName, x, length);
	}

	@Override
    default void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType, scale);
	}

	@Override
    default void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType);
	}

	@Override
    default void setObject(String parameterName, Object x) throws SQLException {
		getWrappedStatement().setObject(parameterName, x);
	}

	@Override
    default void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterName, reader, length);
	}

	@Override
    default void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
		getWrappedStatement().setDate(parameterName, x, cal);
	}

	@Override
    default void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
		getWrappedStatement().setTime(parameterName, x, cal);
	}

	@Override
    default void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
		getWrappedStatement().setTimestamp(parameterName, x, cal);
	}

	@Override
    default void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		getWrappedStatement().setNull(parameterName, sqlType, typeName);
	}

	@Override
    default String getString(String parameterName) throws SQLException {
		return getWrappedStatement().getString(parameterName);
	}

	@Override
    default boolean getBoolean(String parameterName) throws SQLException {
		return getWrappedStatement().getBoolean(parameterName);
	}

	@Override
    default byte getByte(String parameterName) throws SQLException {
		return getWrappedStatement().getByte(parameterName);
	}

	@Override
    default short getShort(String parameterName) throws SQLException {
		return getWrappedStatement().getShort(parameterName);
	}

	@Override
    default int getInt(String parameterName) throws SQLException {
		return getWrappedStatement().getInt(parameterName);
	}

	@Override
    default long getLong(String parameterName) throws SQLException {
		return getWrappedStatement().getLong(parameterName);
	}

	@Override
    default float getFloat(String parameterName) throws SQLException {
		return getWrappedStatement().getFloat(parameterName);
	}

	@Override
    default double getDouble(String parameterName) throws SQLException {
		return getWrappedStatement().getDouble(parameterName);
	}

	@Override
    default byte[] getBytes(String parameterName) throws SQLException {
		return getWrappedStatement().getBytes(parameterName);
	}

	@Override
    default java.sql.Date getDate(String parameterName) throws SQLException {
		return getWrappedStatement().getDate(parameterName);
	}

	@Override
    default java.sql.Time getTime(String parameterName) throws SQLException {
		return getWrappedStatement().getTime(parameterName);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterName);
	}

	@Override
    default Object getObject(String parameterName) throws SQLException {
		return getWrappedStatement().getObject(parameterName);
	}

	@Override
    default BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return getWrappedStatement().getBigDecimal(parameterName);
	}

	@Override
    default Object getObject(String parameterName, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedStatement().getObject(parameterName, map);
	}

	@Override
    default Ref getRef(String parameterName) throws SQLException {
		return getWrappedStatement().getRef(parameterName);
	}

	@Override
    default Blob getBlob(String parameterName) throws SQLException {
		return getWrappedStatement().getBlob(parameterName);
	}

	@Override
    default Clob getClob(String parameterName) throws SQLException {
		return getWrappedStatement().getClob(parameterName);
	}

	@Override
    IArrayWrapper getArray(String parameterName) throws SQLException;

	@Override
    default java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
		return getWrappedStatement().getDate(parameterName, cal);
	}

	@Override
    default java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
		return getWrappedStatement().getTime(parameterName, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterName, cal);
	}

	@Override
    default java.net.URL getURL(String parameterName) throws SQLException {
		return getWrappedStatement().getURL(parameterName);
	}

	@Override
    default RowId getRowId(int parameterIndex) throws SQLException {
		return getWrappedStatement().getRowId(parameterIndex);
	}

	@Override
    default RowId getRowId(String parameterName) throws SQLException {
		return getWrappedStatement().getRowId(parameterName);
	}

	@Override
    default void setRowId(String parameterName, RowId x) throws SQLException {
		getWrappedStatement().setRowId(parameterName, x);
	}

	@Override
    default void setNString(String parameterName, String value) throws SQLException {
		getWrappedStatement().setNString(parameterName, value);
	}

	@Override
    default void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		getWrappedStatement().setNCharacterStream(parameterName, value, length);
	}

	@Override
	default void setNClob(String parameterName, NClob value) throws SQLException {
		getWrappedStatement().setNClob(parameterName, value);
	}

	@Override
	default void setClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrappedStatement().setClob(parameterName, reader, length);
	}

	@Override
	default void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		getWrappedStatement().setBlob(parameterName, inputStream, length);
	}

	@Override
	default void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrappedStatement().setNClob(parameterName, reader, length);
	}

	@Override
    default NClob getNClob(int parameterIndex) throws SQLException {
		return getWrappedStatement().getNClob(parameterIndex);
	}

	@Override
    default NClob getNClob(String parameterName) throws SQLException {
		return getWrappedStatement().getNClob(parameterName);
	}

	@Override
    default void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		getWrappedStatement().setSQLXML(parameterName, xmlObject);
	}

	@Override
    default SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return getWrappedStatement().getSQLXML(parameterIndex);
	}

	@Override
    default SQLXML getSQLXML(String parameterName) throws SQLException {
		return getWrappedStatement().getSQLXML(parameterName);
	}

	@Override
    default String getNString(int parameterIndex) throws SQLException {
		return getWrappedStatement().getNString(parameterIndex);
	}

	@Override
    default String getNString(String parameterName) throws SQLException {
		return getWrappedStatement().getNString(parameterName);
	}

	@Override
    default java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return getWrappedStatement().getNCharacterStream(parameterIndex);
	}

	@Override
    default java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
		return getWrappedStatement().getNCharacterStream(parameterName);
	}

	@Override
    default java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
		return getWrappedStatement().getCharacterStream(parameterIndex);
	}

	@Override
    default java.io.Reader getCharacterStream(String parameterName) throws SQLException {
		return getWrappedStatement().getCharacterStream(parameterName);
	}

	@Override
    default void setBlob(String parameterName, Blob x) throws SQLException {
		getWrappedStatement().setBlob(parameterName, x);
	}

	@Override
    default void setClob(String parameterName, Clob x) throws SQLException {
		getWrappedStatement().setClob(parameterName, x);
	}

	@Override
    default void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterName, x, length);
	}

	@Override
    default void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterName, x, length);
	}

	@Override
    default void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterName, reader, length);
	}

	@Override
    default void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterName, x);
	}

	@Override
    default void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterName, x);
	}

	@Override
    default void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterName, reader);
	}

	@Override
	default void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		getWrappedStatement().setNCharacterStream(parameterName, value);
	}

	@Override
	default void setClob(String parameterName, Reader reader) throws SQLException {
		getWrappedStatement().setClob(parameterName, reader);
	}

	@Override
	default void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		getWrappedStatement().setBlob(parameterName, inputStream);
	}

	@Override
	default void setNClob(String parameterName, Reader reader) throws SQLException {
		getWrappedStatement().setNClob(parameterName, reader);
	}

	@Override
	default <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return getWrappedStatement().getObject(parameterIndex, type);
	}

	@Override
	default <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return getWrappedStatement().getObject(parameterName, type);
	}

	@Override
	default void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType, scaleOrLength);
	}

	@Override
	default void setObject(String parameterName, Object x, SQLType targetSqlType)throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType);
	}

	@Override
    default void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType);
	}

	@Override
    default void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
    default void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
    default void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType);
	}

	@Override
    default void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
    default void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, typeName);
	}
}
