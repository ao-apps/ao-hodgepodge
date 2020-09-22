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

import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Wraps a {@link Clob}.
 *
 * @author  AO Industries, Inc.
 */
public interface IClobWrapper extends IWrapper, Clob, AutoCloseable {

	/**
	 * Gets the clob that is wrapped.
	 */
	@Override
	Clob getWrapped();

	/**
	 * Calls {@link #free()}
	 *
	 * @see  #free()
	 */
	@Override
	default void close() throws SQLException {
		free();
	}

	@Override
	default long length() throws SQLException {
		return getWrapped().length();
	}

	@Override
	default String getSubString(long pos, int length) throws SQLException {
		return getWrapped().getSubString(pos, length);
	}

	@Override
	ReaderWrapper getCharacterStream() throws SQLException;

	@Override
	InputStreamWrapper getAsciiStream() throws SQLException;

	@Override
	default long position(String searchstr, long start) throws SQLException {
		return getWrapped().position(searchstr, start);
	}

	@Override
	long position(Clob searchstr, long start) throws SQLException;

	@Override
	default int setString(long pos, String str) throws SQLException {
		return getWrapped().setString(pos, str);
	}

	@Override
	default int setString(long pos, String str, int offset, int len) throws SQLException {
		return getWrapped().setString(pos, str, offset, len);
	}

	@Override
	OutputStreamWrapper setAsciiStream(long pos) throws SQLException;

	@Override
	default Writer setCharacterStream(long pos) throws SQLException {
		return getWrapped().setCharacterStream(pos);
	}

	@Override
	default void truncate(long len) throws SQLException {
		getWrapped().truncate(len);
	}

	@Override
	default void free() throws SQLException {
		getWrapped().free();
	}

	@Override
	ReaderWrapper getCharacterStream(long pos, long length) throws SQLException;
}
