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
import java.sql.Wrapper;

/**
 * Wraps an {@link Object}.
 *
 * @author  AO Industries, Inc.
 */
public interface IWrapper extends Wrapper {

	/**
	 * Gets the wrapper that is wrapped.
	 * <p>
	 * Note: This does not return {@link Wrapper} because we use {@link IWrapper} on some objects that are not
	 * themselves {@link Wrapper}, such as {@link Array}.
	 * </p>
	 */
	Object getWrapped();

	@Override
	default <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		Object wrapped = getWrapped();
		if(iface.isInstance(wrapped)) return iface.cast(wrapped);
		if(wrapped instanceof Wrapper) {
			return ((Wrapper)wrapped).unwrap(iface);
		} else {
			throw new SQLException("Nothing to unwrap for " + iface.getName());
		}
	}

	@Override
	default boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		Object wrapped = getWrapped();
		if(iface.isInstance(wrapped)) return true;
		if(wrapped instanceof Wrapper) {
			return ((Wrapper)wrapped).isWrapperFor(iface);
		} else {
			return false;
		}
	}
}
