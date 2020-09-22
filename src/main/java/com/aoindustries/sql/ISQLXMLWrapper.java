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
import java.sql.SQLException;
import java.sql.SQLXML;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

/**
 * Wraps a {@link SQLXML}.
 *
 * @author  AO Industries, Inc.
 */
public interface ISQLXMLWrapper extends IWrapper, SQLXML, AutoCloseable {

	/**
	 * Gets the SQL XML that is wrapped.
	 */
	@Override
	SQLXML getWrapped();

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
	default void free() throws SQLException {
		getWrapped().free();
	}

	@Override
	InputStreamWrapper getBinaryStream() throws SQLException;

	@Override
	OutputStreamWrapper setBinaryStream() throws SQLException;

	@Override
	ReaderWrapper getCharacterStream() throws SQLException;

	@Override
	default Writer setCharacterStream() throws SQLException {
		return getWrapped().setCharacterStream();
	}

	@Override
	default String getString() throws SQLException {
		return getWrapped().getString();
	}

	@Override
	default void setString(String value) throws SQLException {
		getWrapped().setString(value);
	}

	@Override
	default <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {
		return getWrapped().getSource(sourceClass);
	}

	@Override
	default <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
		return getWrapped().setResult(resultClass);
	}
}
