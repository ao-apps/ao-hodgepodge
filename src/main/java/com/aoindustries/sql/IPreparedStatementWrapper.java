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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.util.Calendar;

/**
 * Wraps a {@link PreparedStatement}.
 *
 * @author  AO Industries, Inc.
 */
public interface IPreparedStatementWrapper extends IStatementWrapper, PreparedStatement {

	/**
	 * Gets the prepared statement that is wrapped.
	 */
	@Override
	PreparedStatement getWrappedStatement();

	@Override
	IResultSetWrapper executeQuery() throws SQLException;

	@Override
	default int executeUpdate() throws SQLException {
		return getWrappedStatement().executeUpdate();
	}

	@Override
    default void setNull(int parameterIndex, int sqlType) throws SQLException {
		getWrappedStatement().setNull(parameterIndex, sqlType);
	}

	@Override
    default void setBoolean(int parameterIndex, boolean x) throws SQLException {
		getWrappedStatement().setBoolean(parameterIndex, x);
	}

	@Override
    default void setByte(int parameterIndex, byte x) throws SQLException {
		getWrappedStatement().setByte(parameterIndex, x);
	}

	@Override
    default void setShort(int parameterIndex, short x) throws SQLException {
		getWrappedStatement().setShort(parameterIndex, x);
	}

	@Override
    default void setInt(int parameterIndex, int x) throws SQLException {
		getWrappedStatement().setInt(parameterIndex, x);
	}

	@Override
    default void setLong(int parameterIndex, long x) throws SQLException {
		getWrappedStatement().setLong(parameterIndex, x);
	}

	@Override
    default void setFloat(int parameterIndex, float x) throws SQLException {
		getWrappedStatement().setFloat(parameterIndex, x);
	}

	@Override
    default void setDouble(int parameterIndex, double x) throws SQLException {
		getWrappedStatement().setDouble(parameterIndex, x);
	}

	@Override
    default void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		getWrappedStatement().setBigDecimal(parameterIndex, x);
	}

	@Override
    default void setString(int parameterIndex, String x) throws SQLException {
		getWrappedStatement().setString(parameterIndex, x);
	}

	@Override
    default void setBytes(int parameterIndex, byte x[]) throws SQLException {
		getWrappedStatement().setBytes(parameterIndex, x);
	}

	@Override
    default void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
		getWrappedStatement().setDate(parameterIndex, x);
	}

	@Override
    default void setTime(int parameterIndex, java.sql.Time x) throws SQLException {
		getWrappedStatement().setTime(parameterIndex, x);
	}

	@Override
    default void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {
		getWrappedStatement().setTimestamp(parameterIndex, x);
	}

	@Override
    default void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterIndex, x, length);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    default void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		getWrappedStatement().setUnicodeStream(parameterIndex, x, length);
	}

	@Override
    default void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterIndex, x, length);
	}

	@Override
    default void clearParameters() throws SQLException {
		getWrappedStatement().clearParameters();
	}

	@Override
    default void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		getWrappedStatement().setObject(parameterIndex, x, targetSqlType);
	}

	@Override
    default void setObject(int parameterIndex, Object x) throws SQLException {
		getWrappedStatement().setObject(parameterIndex, x);
	}

	@Override
    default boolean execute() throws SQLException {
		return getWrappedStatement().execute();
	}

	@Override
    default void addBatch() throws SQLException {
		getWrappedStatement().addBatch();
	}

	@Override
    default void setCharacterStream(int parameterIndex, java.io.Reader reader, int length) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterIndex, reader, length);
	}

	@Override
    default void setRef(int parameterIndex, Ref x) throws SQLException {
		getWrappedStatement().setRef(parameterIndex, x);
	}

	@Override
    default void setBlob(int parameterIndex, Blob x) throws SQLException {
		getWrappedStatement().setBlob(parameterIndex, x);
	}

	@Override
    default void setClob(int parameterIndex, Clob x) throws SQLException {
		getWrappedStatement().setClob(parameterIndex, x);
	}

	@Override
    default void setArray(int parameterIndex, Array x) throws SQLException {
		getWrappedStatement().setArray(parameterIndex, x);
	}

	// TODO: Wrap this, too?
	@Override
    default ResultSetMetaData getMetaData() throws SQLException {
		return getWrappedStatement().getMetaData();
	}

	@Override
    default void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException {
		getWrappedStatement().setDate(parameterIndex, x, cal);
	}

	@Override
    default void setTime(int parameterIndex, java.sql.Time x, Calendar cal) throws SQLException {
		getWrappedStatement().setTime(parameterIndex, x, cal);
	}

	@Override
    default void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) throws SQLException {
		getWrappedStatement().setTimestamp(parameterIndex, x, cal);
	}

	@Override
	default void setNull (int parameterIndex, int sqlType, String typeName) throws SQLException {
		getWrappedStatement().setNull(parameterIndex, sqlType, typeName);
	}

	@Override
    default void setURL(int parameterIndex, java.net.URL x) throws SQLException {
		getWrappedStatement().setURL(parameterIndex, x);
	}

	@Override
    default ParameterMetaData getParameterMetaData() throws SQLException {
		return getWrappedStatement().getParameterMetaData();
	}

	@Override
    default void setRowId(int parameterIndex, RowId x) throws SQLException {
		getWrappedStatement().setRowId(parameterIndex, x);
	}

	@Override
	default void setNString(int parameterIndex, String value) throws SQLException {
		getWrappedStatement().setNString(parameterIndex, value);
	}

	@Override
	default void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		getWrappedStatement().setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	default void setNClob(int parameterIndex, NClob value) throws SQLException {
		getWrappedStatement().setNClob(parameterIndex, value);
	}

	@Override
	default void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		getWrappedStatement().setClob(parameterIndex, reader, length);
	}

	@Override
	default void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		getWrappedStatement().setBlob(parameterIndex, inputStream, length);
	}

	@Override
	default void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		getWrappedStatement().setNClob(parameterIndex, reader, length);
	}

	@Override
	default void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		getWrappedStatement().setSQLXML(parameterIndex, xmlObject);
	}

	@Override
    default void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		getWrappedStatement().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
	default void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterIndex, x, length);
	}

	@Override
    default void setBinaryStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterIndex, x, length);
	}

	@Override
    default void setCharacterStream(int parameterIndex, java.io.Reader reader, long length) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterIndex, reader, length);
	}

	@Override
    default void setAsciiStream(int parameterIndex, java.io.InputStream x) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterIndex, x);
	}

	@Override
    default void setBinaryStream(int parameterIndex, java.io.InputStream x) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterIndex, x);
	}

	@Override
    default void setCharacterStream(int parameterIndex, java.io.Reader reader) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterIndex, reader);
	}

	@Override
	default void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		getWrappedStatement().setNCharacterStream(parameterIndex, value);
	}

	@Override
	default void setClob(int parameterIndex, Reader reader) throws SQLException {
		getWrappedStatement().setClob(parameterIndex, reader);
	}

	@Override
	default void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		getWrappedStatement().setBlob(parameterIndex, inputStream);
	}

	@Override
	default void setNClob(int parameterIndex, Reader reader) throws SQLException {
		getWrappedStatement().setNClob(parameterIndex, reader);
	}

	@Override
    default void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		getWrappedStatement().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	@Override
    default void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
		getWrappedStatement().setObject(parameterIndex, x, targetSqlType);
	}

	@Override
    default long executeLargeUpdate() throws SQLException {
		return getWrappedStatement().executeLargeUpdate();
	}
}
