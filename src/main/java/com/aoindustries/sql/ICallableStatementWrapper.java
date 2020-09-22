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
	CallableStatement getWrapped();

	@Override
	default void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		getWrapped().registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	default void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		getWrapped().registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
    default boolean wasNull() throws SQLException {
		return getWrapped().wasNull();
	}

	@Override
    default String getString(int parameterIndex) throws SQLException {
		return getWrapped().getString(parameterIndex);
	}

	@Override
    default boolean getBoolean(int parameterIndex) throws SQLException {
		return getWrapped().getBoolean(parameterIndex);
	}

	@Override
    default byte getByte(int parameterIndex) throws SQLException {
		return getWrapped().getByte(parameterIndex);
	}

	@Override
    default short getShort(int parameterIndex) throws SQLException {
		return getWrapped().getShort(parameterIndex);
	}

	@Override
    default int getInt(int parameterIndex) throws SQLException {
		return getWrapped().getInt(parameterIndex);
	}

	@Override
    default long getLong(int parameterIndex) throws SQLException {
		return getWrapped().getLong(parameterIndex);
	}

	@Override
    default float getFloat(int parameterIndex) throws SQLException {
		return getWrapped().getFloat(parameterIndex);
	}

	@Override
    default double getDouble(int parameterIndex) throws SQLException {
		return getWrapped().getDouble(parameterIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return getWrapped().getBigDecimal(parameterIndex, scale);
	}

	@Override
    default byte[] getBytes(int parameterIndex) throws SQLException {
		return getWrapped().getBytes(parameterIndex);
	}

	@Override
    default java.sql.Date getDate(int parameterIndex) throws SQLException {
		return getWrapped().getDate(parameterIndex);
	}

	@Override
    default java.sql.Time getTime(int parameterIndex) throws SQLException {
		return getWrapped().getTime(parameterIndex);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return getWrapped().getTimestamp(parameterIndex);
	}

	@Override
    default Object getObject(int parameterIndex) throws SQLException {
		return getWrapped().getObject(parameterIndex);
	}

	@Override
    default BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return getWrapped().getBigDecimal(parameterIndex);
	}

	@Override
    default Object getObject(int parameterIndex, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrapped().getObject(parameterIndex, map);
	}

	@Override
    IRefWrapper getRef(int parameterIndex) throws SQLException;

	@Override
    IBlobWrapper getBlob(int parameterIndex) throws SQLException;

	@Override
    IClobWrapper getClob(int parameterIndex) throws SQLException;

	@Override
    IArrayWrapper getArray(int parameterIndex) throws SQLException;

	@Override
    default java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return getWrapped().getDate(parameterIndex, cal);
	}

	@Override
    default java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return getWrapped().getTime(parameterIndex, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return getWrapped().getTimestamp(parameterIndex, cal);
	}

	@Override
    default void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		getWrapped().registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
    default void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		getWrapped().registerOutParameter(parameterName, sqlType);
	}

	@Override
    default void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		getWrapped().registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
    default void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		getWrapped().registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
    default java.net.URL getURL(int parameterIndex) throws SQLException {
		return getWrapped().getURL(parameterIndex);
	}

	@Override
    default void setURL(String parameterName, java.net.URL val) throws SQLException {
		getWrapped().setURL(parameterName, val);
	}

	@Override
    default void setNull(String parameterName, int sqlType) throws SQLException {
		getWrapped().setNull(parameterName, sqlType);
	}

	@Override
    default void setBoolean(String parameterName, boolean x) throws SQLException {
		getWrapped().setBoolean(parameterName, x);
	}

	@Override
    default void setByte(String parameterName, byte x) throws SQLException {
		getWrapped().setByte(parameterName, x);
	}

	@Override
    default void setShort(String parameterName, short x) throws SQLException {
		getWrapped().setShort(parameterName, x);
	}

	@Override
    default void setInt(String parameterName, int x) throws SQLException {
		getWrapped().setInt(parameterName, x);
	}

	@Override
    default void setLong(String parameterName, long x) throws SQLException {
		getWrapped().setLong(parameterName, x);
	}

	@Override
    default void setFloat(String parameterName, float x) throws SQLException {
		getWrapped().setFloat(parameterName, x);
	}

	@Override
    default void setDouble(String parameterName, double x) throws SQLException {
		getWrapped().setDouble(parameterName, x);
	}

	@Override
    default void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		getWrapped().setBigDecimal(parameterName, x);
	}

	@Override
    default void setString(String parameterName, String x) throws SQLException {
		getWrapped().setString(parameterName, x);
	}

	@Override
    default void setBytes(String parameterName, byte x[]) throws SQLException {
		getWrapped().setBytes(parameterName, x);
	}

	@Override
    default void setDate(String parameterName, java.sql.Date x) throws SQLException {
		getWrapped().setDate(parameterName, x);
	}

	@Override
    default void setTime(String parameterName, java.sql.Time x)throws SQLException {
		getWrapped().setTime(parameterName, x);
	}

	@Override
    default void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
		getWrapped().setTimestamp(parameterName, x);
	}

	@Override
    default void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
		getWrapped().setAsciiStream(parameterName, x, length);
	}

	@Override
    default void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
		getWrapped().setBinaryStream(parameterName, x, length);
	}

	@Override
    default void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		getWrapped().setObject(parameterName, x, targetSqlType, scale);
	}

	@Override
    default void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		getWrapped().setObject(parameterName, x, targetSqlType);
	}

	@Override
    default void setObject(String parameterName, Object x) throws SQLException {
		getWrapped().setObject(parameterName, x);
	}

	@Override
    default void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
		getWrapped().setCharacterStream(parameterName, reader, length);
	}

	@Override
    default void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
		getWrapped().setDate(parameterName, x, cal);
	}

	@Override
    default void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
		getWrapped().setTime(parameterName, x, cal);
	}

	@Override
    default void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
		getWrapped().setTimestamp(parameterName, x, cal);
	}

	@Override
    default void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		getWrapped().setNull(parameterName, sqlType, typeName);
	}

	@Override
    default String getString(String parameterName) throws SQLException {
		return getWrapped().getString(parameterName);
	}

	@Override
    default boolean getBoolean(String parameterName) throws SQLException {
		return getWrapped().getBoolean(parameterName);
	}

	@Override
    default byte getByte(String parameterName) throws SQLException {
		return getWrapped().getByte(parameterName);
	}

	@Override
    default short getShort(String parameterName) throws SQLException {
		return getWrapped().getShort(parameterName);
	}

	@Override
    default int getInt(String parameterName) throws SQLException {
		return getWrapped().getInt(parameterName);
	}

	@Override
    default long getLong(String parameterName) throws SQLException {
		return getWrapped().getLong(parameterName);
	}

	@Override
    default float getFloat(String parameterName) throws SQLException {
		return getWrapped().getFloat(parameterName);
	}

	@Override
    default double getDouble(String parameterName) throws SQLException {
		return getWrapped().getDouble(parameterName);
	}

	@Override
    default byte[] getBytes(String parameterName) throws SQLException {
		return getWrapped().getBytes(parameterName);
	}

	@Override
    default java.sql.Date getDate(String parameterName) throws SQLException {
		return getWrapped().getDate(parameterName);
	}

	@Override
    default java.sql.Time getTime(String parameterName) throws SQLException {
		return getWrapped().getTime(parameterName);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
		return getWrapped().getTimestamp(parameterName);
	}

	@Override
    default Object getObject(String parameterName) throws SQLException {
		return getWrapped().getObject(parameterName);
	}

	@Override
    default BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return getWrapped().getBigDecimal(parameterName);
	}

	@Override
    default Object getObject(String parameterName, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrapped().getObject(parameterName, map);
	}

	@Override
    IRefWrapper getRef(String parameterName) throws SQLException;

	@Override
    IBlobWrapper getBlob(String parameterName) throws SQLException;

	@Override
    IClobWrapper getClob(String parameterName) throws SQLException;

	@Override
    IArrayWrapper getArray(String parameterName) throws SQLException;

	@Override
    default java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
		return getWrapped().getDate(parameterName, cal);
	}

	@Override
    default java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
		return getWrapped().getTime(parameterName, cal);
	}

	@Override
    default java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return getWrapped().getTimestamp(parameterName, cal);
	}

	@Override
    default java.net.URL getURL(String parameterName) throws SQLException {
		return getWrapped().getURL(parameterName);
	}

	@Override
    IRowIdWrapper getRowId(int parameterIndex) throws SQLException;

	@Override
    IRowIdWrapper getRowId(String parameterName) throws SQLException;

	@Override
    void setRowId(String parameterName, RowId x) throws SQLException;

	@Override
    default void setNString(String parameterName, String value) throws SQLException {
		getWrapped().setNString(parameterName, value);
	}

	@Override
    default void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		getWrapped().setNCharacterStream(parameterName, value, length);
	}

	@Override
	void setNClob(String parameterName, NClob value) throws SQLException;

	@Override
	default void setClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrapped().setClob(parameterName, reader, length);
	}

	@Override
	default void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		getWrapped().setBlob(parameterName, inputStream, length);
	}

	@Override
	default void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrapped().setNClob(parameterName, reader, length);
	}

	@Override
    INClobWrapper getNClob(int parameterIndex) throws SQLException;

	@Override
    INClobWrapper getNClob(String parameterName) throws SQLException;

	@Override
    void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException;

	@Override
    ISQLXMLWrapper getSQLXML(int parameterIndex) throws SQLException;

	@Override
    ISQLXMLWrapper getSQLXML(String parameterName) throws SQLException;

	@Override
    default String getNString(int parameterIndex) throws SQLException {
		return getWrapped().getNString(parameterIndex);
	}

	@Override
    default String getNString(String parameterName) throws SQLException {
		return getWrapped().getNString(parameterName);
	}

	@Override
    default java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return getWrapped().getNCharacterStream(parameterIndex);
	}

	@Override
    default java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
		return getWrapped().getNCharacterStream(parameterName);
	}

	@Override
    default java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
		return getWrapped().getCharacterStream(parameterIndex);
	}

	@Override
    default java.io.Reader getCharacterStream(String parameterName) throws SQLException {
		return getWrapped().getCharacterStream(parameterName);
	}

	@Override
    void setBlob(String parameterName, Blob x) throws SQLException;

	@Override
    void setClob(String parameterName, Clob x) throws SQLException;

	@Override
    default void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
		getWrapped().setAsciiStream(parameterName, x, length);
	}

	@Override
    default void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
		getWrapped().setBinaryStream(parameterName, x, length);
	}

	@Override
    default void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
		getWrapped().setCharacterStream(parameterName, reader, length);
	}

	@Override
    default void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
		getWrapped().setAsciiStream(parameterName, x);
	}

	@Override
    default void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
		getWrapped().setBinaryStream(parameterName, x);
	}

	@Override
    default void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
		getWrapped().setCharacterStream(parameterName, reader);
	}

	@Override
	default void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		getWrapped().setNCharacterStream(parameterName, value);
	}

	@Override
	default void setClob(String parameterName, Reader reader) throws SQLException {
		getWrapped().setClob(parameterName, reader);
	}

	@Override
	default void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		getWrapped().setBlob(parameterName, inputStream);
	}

	@Override
	default void setNClob(String parameterName, Reader reader) throws SQLException {
		getWrapped().setNClob(parameterName, reader);
	}

	@Override
	default <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return getWrapped().getObject(parameterIndex, type);
	}

	@Override
	default <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return getWrapped().getObject(parameterName, type);
	}

	@Override
	default void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		getWrapped().setObject(parameterName, x, targetSqlType, scaleOrLength);
	}

	@Override
	default void setObject(String parameterName, Object x, SQLType targetSqlType)throws SQLException {
		getWrapped().setObject(parameterName, x, targetSqlType);
	}

	@Override
    default void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
		getWrapped().registerOutParameter(parameterIndex, sqlType);
	}

	@Override
    default void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
		getWrapped().registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
    default void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
		getWrapped().registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
    default void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
		getWrapped().registerOutParameter(parameterName, sqlType);
	}

	@Override
    default void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
		getWrapped().registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
    default void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
		getWrapped().registerOutParameter(parameterName, sqlType, typeName);
	}
}
