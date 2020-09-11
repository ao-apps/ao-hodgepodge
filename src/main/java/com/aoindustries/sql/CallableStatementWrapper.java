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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.util.Calendar;
import java.util.Optional;

/**
 * Wraps a {@link CallableStatement}.
 *
 * @author  AO Industries, Inc.
 */
public abstract class CallableStatementWrapper extends PreparedStatementWrapper implements CallableStatement {

	public CallableStatementWrapper() {
	}

	@Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
    public boolean wasNull() throws SQLException {
		return getWrappedStatement().wasNull();
	}

	@Override
    public String getString(int parameterIndex) throws SQLException {
		return getWrappedStatement().getString(parameterIndex);
	}

	@Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBoolean(parameterIndex);
	}

	@Override
    public byte getByte(int parameterIndex) throws SQLException {
		return getWrappedStatement().getByte(parameterIndex);
	}

	@Override
    public short getShort(int parameterIndex) throws SQLException {
		return getWrappedStatement().getShort(parameterIndex);
	}

	@Override
    public int getInt(int parameterIndex) throws SQLException {
		return getWrappedStatement().getInt(parameterIndex);
	}

	@Override
    public long getLong(int parameterIndex) throws SQLException {
		return getWrappedStatement().getLong(parameterIndex);
	}

	@Override
    public float getFloat(int parameterIndex) throws SQLException {
		return getWrappedStatement().getFloat(parameterIndex);
	}

	@Override
    public double getDouble(int parameterIndex) throws SQLException {
		return getWrappedStatement().getDouble(parameterIndex);
	}

	@Override
    @Deprecated // Java 9: (since="1.2")
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return getWrappedStatement().getBigDecimal(parameterIndex, scale);
	}

	@Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBytes(parameterIndex);
	}

	@Override
    public java.sql.Date getDate(int parameterIndex) throws SQLException {
		return getWrappedStatement().getDate(parameterIndex);
	}

	@Override
    public java.sql.Time getTime(int parameterIndex) throws SQLException {
		return getWrappedStatement().getTime(parameterIndex);
	}

	@Override
    public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterIndex);
	}

	@Override
    public Object getObject(int parameterIndex) throws SQLException {
		return getWrappedStatement().getObject(parameterIndex);
	}

	@Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBigDecimal(parameterIndex);
	}

	@Override
    public Object getObject(int parameterIndex, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedStatement().getObject(parameterIndex, map);
	}

	@Override
    public Ref getRef(int parameterIndex) throws SQLException {
		return getWrappedStatement().getRef(parameterIndex);
	}

	@Override
    public Blob getBlob(int parameterIndex) throws SQLException {
		return getWrappedStatement().getBlob(parameterIndex);
	}

	@Override
    public Clob getClob(int parameterIndex) throws SQLException {
		return getWrappedStatement().getClob(parameterIndex);
	}

	@Override
    public ArrayWrapper getArray(int parameterIndex) throws SQLException {
		return wrapArray(getWrappedStatement().getArray(parameterIndex));
	}

	@Override
    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return getWrappedStatement().getDate(parameterIndex, cal);
	}

	@Override
    public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return getWrappedStatement().getTime(parameterIndex, cal);
	}

	@Override
    public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterIndex, cal);
	}

	@Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType);
	}

	@Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
    public java.net.URL getURL(int parameterIndex) throws SQLException {
		return getWrappedStatement().getURL(parameterIndex);
	}

	@Override
    public void setURL(String parameterName, java.net.URL val) throws SQLException {
		getWrappedStatement().setURL(parameterName, val);
	}

	@Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
		getWrappedStatement().setNull(parameterName, sqlType);
	}

	@Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
		getWrappedStatement().setBoolean(parameterName, x);
	}

	@Override
    public void setByte(String parameterName, byte x) throws SQLException {
		getWrappedStatement().setByte(parameterName, x);
	}

	@Override
    public void setShort(String parameterName, short x) throws SQLException {
		getWrappedStatement().setShort(parameterName, x);
	}

	@Override
    public void setInt(String parameterName, int x) throws SQLException {
		getWrappedStatement().setInt(parameterName, x);
	}

	@Override
    public void setLong(String parameterName, long x) throws SQLException {
		getWrappedStatement().setLong(parameterName, x);
	}

	@Override
    public void setFloat(String parameterName, float x) throws SQLException {
		getWrappedStatement().setFloat(parameterName, x);
	}

	@Override
    public void setDouble(String parameterName, double x) throws SQLException {
		getWrappedStatement().setDouble(parameterName, x);
	}

	@Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		getWrappedStatement().setBigDecimal(parameterName, x);
	}

	@Override
    public void setString(String parameterName, String x) throws SQLException {
		getWrappedStatement().setString(parameterName, x);
	}

	@Override
    public void setBytes(String parameterName, byte x[]) throws SQLException {
		getWrappedStatement().setBytes(parameterName, x);
	}

	@Override
    public void setDate(String parameterName, java.sql.Date x) throws SQLException {
		getWrappedStatement().setDate(parameterName, x);
	}

	@Override
    public void setTime(String parameterName, java.sql.Time x)throws SQLException {
		getWrappedStatement().setTime(parameterName, x);
	}

	@Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
		getWrappedStatement().setTimestamp(parameterName, x);
	}

	@Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterName, x, length);
	}

	@Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterName, x, length);
	}

	@Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType, scale);
	}

	@Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType);
	}

	@Override
    public void setObject(String parameterName, Object x) throws SQLException {
		getWrappedStatement().setObject(parameterName, x);
	}

	@Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterName, reader, length);
	}

	@Override
    public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
		getWrappedStatement().setDate(parameterName, x, cal);
	}

	@Override
    public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
		getWrappedStatement().setTime(parameterName, x, cal);
	}

	@Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
		getWrappedStatement().setTimestamp(parameterName, x, cal);
	}

	@Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		getWrappedStatement().setNull(parameterName, sqlType, typeName);
	}

	@Override
    public String getString(String parameterName) throws SQLException {
		return getWrappedStatement().getString(parameterName);
	}

	@Override
    public boolean getBoolean(String parameterName) throws SQLException {
		return getWrappedStatement().getBoolean(parameterName);
	}

	@Override
    public byte getByte(String parameterName) throws SQLException {
		return getWrappedStatement().getByte(parameterName);
	}

	@Override
    public short getShort(String parameterName) throws SQLException {
		return getWrappedStatement().getShort(parameterName);
	}

	@Override
    public int getInt(String parameterName) throws SQLException {
		return getWrappedStatement().getInt(parameterName);
	}

	@Override
    public long getLong(String parameterName) throws SQLException {
		return getWrappedStatement().getLong(parameterName);
	}

	@Override
    public float getFloat(String parameterName) throws SQLException {
		return getWrappedStatement().getFloat(parameterName);
	}

	@Override
    public double getDouble(String parameterName) throws SQLException {
		return getWrappedStatement().getDouble(parameterName);
	}

	@Override
    public byte[] getBytes(String parameterName) throws SQLException {
		return getWrappedStatement().getBytes(parameterName);
	}

	@Override
    public java.sql.Date getDate(String parameterName) throws SQLException {
		return getWrappedStatement().getDate(parameterName);
	}

	@Override
    public java.sql.Time getTime(String parameterName) throws SQLException {
		return getWrappedStatement().getTime(parameterName);
	}

	@Override
    public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterName);
	}

	@Override
    public Object getObject(String parameterName) throws SQLException {
		return getWrappedStatement().getObject(parameterName);
	}

	@Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return getWrappedStatement().getBigDecimal(parameterName);
	}

	@Override
    public Object getObject(String parameterName, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedStatement().getObject(parameterName, map);
	}

	@Override
    public Ref getRef(String parameterName) throws SQLException {
		return getWrappedStatement().getRef(parameterName);
	}

	@Override
    public Blob getBlob(String parameterName) throws SQLException {
		return getWrappedStatement().getBlob(parameterName);
	}

	@Override
    public Clob getClob(String parameterName) throws SQLException {
		return getWrappedStatement().getClob(parameterName);
	}

	@Override
    public ArrayWrapper getArray(String parameterName) throws SQLException {
		return wrapArray(getWrappedStatement().getArray(parameterName));
	}

	@Override
    public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
		return getWrappedStatement().getDate(parameterName, cal);
	}

	@Override
    public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
		return getWrappedStatement().getTime(parameterName, cal);
	}

	@Override
    public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return getWrappedStatement().getTimestamp(parameterName, cal);
	}

	@Override
    public java.net.URL getURL(String parameterName) throws SQLException {
		return getWrappedStatement().getURL(parameterName);
	}

	@Override
    public RowId getRowId(int parameterIndex) throws SQLException {
		return getWrappedStatement().getRowId(parameterIndex);
	}

	@Override
    public RowId getRowId(String parameterName) throws SQLException {
		return getWrappedStatement().getRowId(parameterName);
	}

	@Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
		getWrappedStatement().setRowId(parameterName, x);
	}

	@Override
    public void setNString(String parameterName, String value) throws SQLException {
		getWrappedStatement().setNString(parameterName, value);
	}

	@Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		getWrappedStatement().setNCharacterStream(parameterName, value, length);
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		getWrappedStatement().setNClob(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrappedStatement().setClob(parameterName, reader, length);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		getWrappedStatement().setBlob(parameterName, inputStream, length);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrappedStatement().setNClob(parameterName, reader, length);
	}

	@Override
    public NClob getNClob(int parameterIndex) throws SQLException {
		return getWrappedStatement().getNClob(parameterIndex);
	}

	@Override
    public NClob getNClob(String parameterName) throws SQLException {
		return getWrappedStatement().getNClob(parameterName);
	}

	@Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		getWrappedStatement().setSQLXML(parameterName, xmlObject);
	}

	@Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return getWrappedStatement().getSQLXML(parameterIndex);
	}

	@Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
		return getWrappedStatement().getSQLXML(parameterName);
	}

	@Override
    public String getNString(int parameterIndex) throws SQLException {
		return getWrappedStatement().getNString(parameterIndex);
	}

	@Override
    public String getNString(String parameterName) throws SQLException {
		return getWrappedStatement().getNString(parameterName);
	}

	@Override
    public java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return getWrappedStatement().getNCharacterStream(parameterIndex);
	}

	@Override
    public java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
		return getWrappedStatement().getNCharacterStream(parameterName);
	}

	@Override
    public java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
		return getWrappedStatement().getCharacterStream(parameterIndex);
	}

	@Override
    public java.io.Reader getCharacterStream(String parameterName) throws SQLException {
		return getWrappedStatement().getCharacterStream(parameterName);
	}

	@Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
		getWrappedStatement().setBlob(parameterName, x);
	}

	@Override
    public void setClob(String parameterName, Clob x) throws SQLException {
		getWrappedStatement().setClob(parameterName, x);
	}

	@Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterName, x, length);
	}

	@Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterName, x, length);
	}

	@Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterName, reader, length);
	}

	@Override
    public void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
		getWrappedStatement().setAsciiStream(parameterName, x);
	}

	@Override
    public void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
		getWrappedStatement().setBinaryStream(parameterName, x);
	}

	@Override
    public void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
		getWrappedStatement().setCharacterStream(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		getWrappedStatement().setNCharacterStream(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		getWrappedStatement().setClob(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		getWrappedStatement().setBlob(parameterName, inputStream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		getWrappedStatement().setNClob(parameterName, reader);
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return getWrappedStatement().getObject(parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return getWrappedStatement().getObject(parameterName, type);
	}

	@Override
	public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType, scaleOrLength);
	}

	@Override
	public void setObject(String parameterName, Object x, SQLType targetSqlType)throws SQLException {
		getWrappedStatement().setObject(parameterName, x, targetSqlType);
	}

	@Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType);
	}

	@Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
    public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType);
	}

	@Override
    public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
    public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
		getWrappedStatement().registerOutParameter(parameterName, sqlType, typeName);
	}

	protected ArrayWrapper wrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(arrayWrapper.getStatementWrapper().orElse(null) == this) {
				return arrayWrapper;
			}
		}
		return new ArrayWrapper() {
			@Override
			protected ConnectionWrapper getConnectionWrapper() {
				return CallableStatementWrapper.this.getConnectionWrapper();
			}

			@Override
			protected Optional<? extends StatementWrapper> getStatementWrapper() {
				return Optional.of(CallableStatementWrapper.this);
			}

			@Override
			protected Array getWrappedArray() {
				return array;
			}
		};
	}

	/**
	 * Gets the callable statement that is wrapped.
	 */
	@Override
	protected abstract CallableStatement getWrappedStatement();
}
