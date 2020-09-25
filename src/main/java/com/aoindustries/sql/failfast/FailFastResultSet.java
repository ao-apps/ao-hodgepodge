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

import com.aoindustries.lang.Throwables;
import com.aoindustries.sql.wrapper.ResultSetWrapper;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @see  FailFastConnection
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastResultSet extends ResultSetWrapper {

	public FailFastResultSet(FailFastConnection failFastConnection, FailFastStatement failFastStmt, ResultSet wrapped) {
		super(failFastConnection, failFastStmt, wrapped);
	}

	@Override
	protected FailFastConnection getConnectionWrapper() {
		return (FailFastConnection)super.getConnectionWrapper();
	}

	@Override
	public boolean next() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.next();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void close() throws SQLException {
		try {
			super.close();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean wasNull() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.wasNull();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getString(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBoolean(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getByte(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getShort(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getInt(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getLong(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getFloat(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getDouble(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	@Deprecated // Java 9: (since="1.2")
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBigDecimal(columnIndex, scale);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBytes(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getDate(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTime(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTimestamp(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastInputStream getAsciiStream(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastInputStream)super.getAsciiStream(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	@Deprecated // Java 9: (since="1.2")
	public FailFastInputStream getUnicodeStream(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastInputStream)super.getUnicodeStream(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastInputStream getBinaryStream(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastInputStream)super.getBinaryStream(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getString(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBoolean(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getByte(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getShort(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getInt(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getLong(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getFloat(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getDouble(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	@Deprecated // Java 9: (since="1.2")
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBigDecimal(columnLabel, scale);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBytes(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getDate(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTime(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTimestamp(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastInputStream getAsciiStream(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastInputStream)super.getAsciiStream(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	@Deprecated // Java 9: (since="1.2")
	public FailFastInputStream getUnicodeStream(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastInputStream)super.getUnicodeStream(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastInputStream getBinaryStream(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastInputStream)super.getBinaryStream(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getWarnings();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void clearWarnings() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.clearWarnings();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getCursorName() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getCursorName();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastResultSetMetaData getMetaData() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastResultSetMetaData)super.getMetaData();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getObject(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getObject(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.findColumn(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastReader getCharacterStream(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastReader)super.getCharacterStream(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastReader getCharacterStream(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastReader)super.getCharacterStream(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBigDecimal(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getBigDecimal(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.isBeforeFirst();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.isAfterLast();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isFirst() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.isFirst();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isLast() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.isLast();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void beforeFirst() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.beforeFirst();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void afterLast() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.afterLast();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean first() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.first();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean last() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.last();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getRow() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getRow();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.absolute(row);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.relative(rows);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean previous() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.previous();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.setFetchDirection(direction);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getFetchDirection() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getFetchDirection();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.setFetchSize(rows);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getFetchSize() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getFetchSize();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getType() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getType();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getConcurrency() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getConcurrency();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.rowUpdated();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean rowInserted() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.rowInserted();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.rowDeleted();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNull(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBoolean(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateByte(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateShort(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateInt(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateLong(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateFloat(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateDouble(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBigDecimal(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateString(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBytes(int columnIndex, byte x[]) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBytes(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateDate(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateTime(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateTimestamp(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateAsciiStream(columnIndex, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBinaryStream(columnIndex, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateCharacterStream(columnIndex, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnIndex, x, scaleOrLength);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNull(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNull(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBoolean(String columnLabel, boolean x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBoolean(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateByte(String columnLabel, byte x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateByte(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateShort(String columnLabel, short x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateShort(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateInt(String columnLabel, int x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateInt(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateLong(String columnLabel, long x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateLong(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateFloat(String columnLabel, float x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateFloat(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateDouble(String columnLabel, double x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateDouble(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBigDecimal(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateString(String columnLabel, String x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateString(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBytes(String columnLabel, byte x[]) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBytes(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateDate(String columnLabel, Date x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateDate(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateTime(String columnLabel, Time x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateTime(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateTimestamp(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateAsciiStream(columnLabel, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBinaryStream(columnLabel, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateCharacterStream(columnLabel, reader, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnLabel, x, scaleOrLength);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(String columnLabel, Object x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void insertRow() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.insertRow();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateRow() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateRow();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void deleteRow() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.deleteRow();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void refreshRow() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.refreshRow();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.cancelRowUpdates();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.moveToInsertRow();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.moveToCurrentRow();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastStatement getStatement() throws SQLException {
		try {
			return (FailFastStatement)super.getStatement();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Object getObject(int columnIndex, Map<String,Class<?>> map) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getObject(columnIndex, map);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastRef getRef(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastRef)super.getRef(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastBlob getBlob(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastBlob)super.getBlob(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastClob getClob(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastClob)super.getClob(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastArray getArray(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastArray)super.getArray(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Object getObject(String columnLabel, Map<String,Class<?>> map) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getObject(columnLabel, map);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastRef getRef(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastRef)super.getRef(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastBlob getBlob(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastBlob)super.getBlob(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastClob getClob(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastClob)super.getClob(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastArray getArray(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastArray)super.getArray(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getDate(columnIndex, cal);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getDate(columnLabel, cal);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTime(columnIndex, cal);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTime(columnLabel, cal);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTimestamp(columnIndex, cal);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getTimestamp(columnLabel, cal);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getURL(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getURL(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateRef(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateRef(String columnLabel, Ref x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateRef(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBlob(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBlob(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateClob(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateClob(String columnLabel, Clob x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateClob(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateArray(int columnIndex, Array x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateArray(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateArray(String columnLabel, Array x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateArray(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastRowId getRowId(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastRowId)super.getRowId(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastRowId getRowId(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastRowId)super.getRowId(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateRowId(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateRowId(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public int getHoldability() throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getHoldability();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public boolean isClosed() throws SQLException {
		try {
			return super.isClosed();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNString(columnIndex, nString);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNString(columnLabel, nString);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNClob(columnIndex, nClob);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNClob(columnLabel, nClob);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastNClob getNClob(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastNClob)super.getNClob(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastNClob getNClob(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastNClob)super.getNClob(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastSQLXML getSQLXML(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastSQLXML)super.getSQLXML(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastSQLXML getSQLXML(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastSQLXML)super.getSQLXML(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateSQLXML(columnIndex, xmlObject);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateSQLXML(columnLabel, xmlObject);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getNString(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getNString(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastReader getNCharacterStream(int columnIndex) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastReader)super.getNCharacterStream(columnIndex);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public FailFastReader getNCharacterStream(String columnLabel) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return (FailFastReader)super.getNCharacterStream(columnLabel);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNCharacterStream(columnIndex, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNCharacterStream(columnLabel, reader, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateAsciiStream(columnIndex, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBinaryStream(columnIndex, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateCharacterStream(columnIndex, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateAsciiStream(columnLabel, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBinaryStream(columnLabel, x, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateCharacterStream(columnLabel, reader, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBlob(columnIndex, inputStream, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBlob(columnLabel, inputStream, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateClob(columnIndex, reader, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateClob(columnLabel, reader, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNClob(columnIndex, reader, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNClob(columnLabel, reader, length);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNCharacterStream(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNCharacterStream(columnLabel, reader);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateAsciiStream(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBinaryStream(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateCharacterStream(columnIndex, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateAsciiStream(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBinaryStream(columnLabel, x);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateCharacterStream(columnLabel, reader);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBlob(columnIndex, inputStream);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateBlob(columnLabel, inputStream);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateClob(columnIndex, reader);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateClob(columnLabel, reader);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNClob(columnIndex, reader);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateNClob(columnLabel, reader);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getObject(columnIndex, type);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			return super.getObject(columnLabel, type);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)  throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnIndex, x, targetSqlType);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}

	@Override
	public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastSQLException();
		try {
			super.updateObject(columnLabel, x, targetSqlType);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, SQLException.class, FailFastSQLException::new);
		}
	}
}
