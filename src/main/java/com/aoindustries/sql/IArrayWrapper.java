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

import java.sql.Array;
import java.sql.SQLException;

/**
 * Wraps an {@link Array}.
 *
 * @author  AO Industries, Inc.
 */
public interface IArrayWrapper extends Array {

	/**
	 * Gets the array that is wrapped.
	 */
	Array getWrappedArray();

	@Override
	default String getBaseTypeName() throws SQLException {
		return getWrappedArray().getBaseTypeName();
	}

	@Override
	default int getBaseType() throws SQLException {
		return getWrappedArray().getBaseType();
	}

	@Override
	default Object getArray() throws SQLException {
		return getWrappedArray().getArray();
	}

	@Override
	default Object getArray(java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedArray().getArray(map);
	}

	@Override
	default Object getArray(long index, int count) throws SQLException {
		return getWrappedArray().getArray(index, count);
	}

	@Override
	default Object getArray(long index, int count, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedArray().getArray(index, count, map);
	}

	@Override
	IResultSetWrapper getResultSet() throws SQLException;

	@Override
	IResultSetWrapper getResultSet(java.util.Map<String,Class<?>> map) throws SQLException;

	@Override
	IResultSetWrapper getResultSet(long index, int count) throws SQLException;

	@Override
	IResultSetWrapper getResultSet(long index, int count, java.util.Map<String,Class<?>> map) throws SQLException;

	@Override
	default void free() throws SQLException {
		getWrappedArray().free();
	}
}
