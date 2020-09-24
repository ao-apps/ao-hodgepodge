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
package com.aoindustries.sql.wrapper;

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
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Wraps a {@link SQLOutput}.
 *
 * @author  AO Industries, Inc.
 */
public interface ISQLOutputWrapper extends IWrapper, SQLOutput, AutoCloseable {

	/**
	 * Gets the SQL output that is wrapped.
	 */
	@Override
	SQLOutput getWrapped();

	/**
	 * Releases resources associated with this wrapper.
	 */
	@Override
	default void close() throws SQLException {
		// Do nothing by default
	}

	@Override
	default void writeString(String x) throws SQLException {
		getWrapped().writeString(x);
	}

	@Override
	default void writeBoolean(boolean x) throws SQLException {
		getWrapped().writeBoolean(x);
	}

	@Override
	default void writeByte(byte x) throws SQLException {
		getWrapped().writeByte(x);
	}

	@Override
	default void writeShort(short x) throws SQLException {
		getWrapped().writeShort(x);
	}

	@Override
	default void writeInt(int x) throws SQLException {
		getWrapped().writeInt(x);
	}

	@Override
	default void writeLong(long x) throws SQLException {
		getWrapped().writeLong(x);
	}

	@Override
	default void writeFloat(float x) throws SQLException {
		getWrapped().writeFloat(x);
	}

	@Override
	default void writeDouble(double x) throws SQLException {
		getWrapped().writeDouble(x);
	}

	@Override
	default void writeBigDecimal(BigDecimal x) throws SQLException {
		getWrapped().writeBigDecimal(x);
	}

	@Override
	default void writeBytes(byte[] x) throws SQLException {
		getWrapped().writeBytes(x);
	}

	@Override
	default void writeDate(Date x) throws SQLException {
		getWrapped().writeDate(x);
	}

	@Override
	default void writeTime(Time x) throws SQLException {
		getWrapped().writeTime(x);
	}

	@Override
	default void writeTimestamp(Timestamp x) throws SQLException {
		getWrapped().writeTimestamp(x);
	}

	@Override
	void writeCharacterStream(Reader x) throws SQLException;

	@Override
	void writeAsciiStream(InputStream x) throws SQLException;

	@Override
	void writeBinaryStream(InputStream x) throws SQLException;

	@Override
	default void writeObject(SQLData x) throws SQLException {
		getWrapped().writeObject(x);
	}

	@Override
	void writeRef(Ref x) throws SQLException;

	@Override
	void writeBlob(Blob x) throws SQLException;

	@Override
	void writeClob(Clob x) throws SQLException;

	@Override
	void writeStruct(Struct x) throws SQLException;

	@Override
	void writeArray(Array x) throws SQLException;

	@Override
	default void writeURL(URL x) throws SQLException {
		getWrapped().writeURL(x);
	}

	@Override
	default void writeNString(String x) throws SQLException {
		getWrapped().writeNString(x);
	}

	@Override
	void writeNClob(NClob x) throws SQLException;

	@Override
	void writeRowId(RowId x) throws SQLException;

	@Override
	void writeSQLXML(SQLXML x) throws SQLException;

	@Override
	default void writeObject(Object x, SQLType targetSqlType) throws SQLException {
		getWrapped().writeObject(x, targetSqlType);
	}
}
