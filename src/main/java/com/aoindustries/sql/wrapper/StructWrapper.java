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

import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

/**
 * Wraps a {@link Struct}.
 *
 * @author  AO Industries, Inc.
 */
public interface StructWrapper extends Wrapper, Struct, AutoCloseable {

	/**
	 * Gets the struct that is wrapped.
	 */
	@Override
	Struct getWrapped();

	/**
	 * Releases resources associated with this wrapper.
	 */
	@Override
	default void close() throws SQLException {
		// Do nothing by default
	}

	@Override
	default String getSQLTypeName() throws SQLException {
		return getWrapped().getSQLTypeName();
	}

	@Override
	default Object[] getAttributes() throws SQLException {
		return getWrapped().getAttributes();
	}

	@Override
	default Object[] getAttributes(Map<String,Class<?>> map) throws SQLException {
		// TODO: How can we wrap SQLData on UDT maps?
		return getWrapped().getAttributes(map);
	}
}
