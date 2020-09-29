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

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Wraps a {@link SQLInput}.
 *
 * @author  AO Industries, Inc.
 */
public interface ISQLInputWrapper extends IWrapper, SQLInput, AutoCloseable {

	/**
	 * Gets the SQL input that is wrapped.
	 */
	@Override
	SQLInput getWrapped();

	/**
	 * Releases resources associated with this wrapper.
	 *
	 * @see  SQLDataWrapperImpl#readSQL(java.sql.SQLInput, java.lang.String)
	 */
	@Override
	default void close() throws SQLException {
		// Do nothing by default
	}

	@Override
	default String readString() throws SQLException {
		return getWrapped().readString();
	}

	@Override
	default boolean readBoolean() throws SQLException {
		return getWrapped().readBoolean();
	}

	@Override
	default byte readByte() throws SQLException {
		return getWrapped().readByte();
	}

	@Override
	default short readShort() throws SQLException {
		return getWrapped().readShort();
	}

	@Override
	default int readInt() throws SQLException {
		return getWrapped().readInt();
	}

	@Override
	default long readLong() throws SQLException {
		return getWrapped().readLong();
	}

	@Override
	default float readFloat() throws SQLException {
		return getWrapped().readFloat();
	}

	@Override
	default double readDouble() throws SQLException {
		return getWrapped().readDouble();
	}

	@Override
	default BigDecimal readBigDecimal() throws SQLException {
		return getWrapped().readBigDecimal();
	}

	@Override
	default byte[] readBytes() throws SQLException {
		return getWrapped().readBytes();
	}

	@Override
	default Date readDate() throws SQLException {
		return getWrapped().readDate();
	}

	@Override
	default Time readTime() throws SQLException {
		return getWrapped().readTime();
	}

	@Override
	default Timestamp readTimestamp() throws SQLException {
		return getWrapped().readTimestamp();
	}

	@Override
	ReaderWrapper readCharacterStream() throws SQLException;

	@Override
	InputStreamWrapper readAsciiStream() throws SQLException;

	@Override
	InputStreamWrapper readBinaryStream() throws SQLException;

	@Override
	default Object readObject() throws SQLException {
		return getWrapped().readObject();
	}

	@Override
	IRefWrapper readRef() throws SQLException;

	@Override
	IBlobWrapper readBlob() throws SQLException;

	@Override
	IClobWrapper readClob() throws SQLException;

	@Override
	IArrayWrapper readArray() throws SQLException;

	@Override
	default boolean wasNull() throws SQLException {
		return getWrapped().wasNull();
	}

	@Override
	default URL readURL() throws SQLException {
		return getWrapped().readURL();
	}

	@Override
	INClobWrapper readNClob() throws SQLException;

	@Override
	default String readNString() throws SQLException {
		return getWrapped().readNString();
	}

	@Override
	ISQLXMLWrapper readSQLXML() throws SQLException;

	@Override
	IRowIdWrapper readRowId() throws SQLException;

	@Override
	default <T> T readObject(Class<T> type) throws SQLException {
		return getWrapped().readObject(type);
	}
}
