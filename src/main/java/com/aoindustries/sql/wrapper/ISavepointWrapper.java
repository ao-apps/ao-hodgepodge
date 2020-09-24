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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Wraps a {@link Savepoint}.
 *
 * @author  AO Industries, Inc.
 */
public interface ISavepointWrapper extends IWrapper, Savepoint, AutoCloseable {

	/**
	 * Gets the savepoint that is wrapped.
	 */
	@Override
	Savepoint getWrapped();

	/**
	 * Calls {@link Connection#releaseSavepoint(java.sql.Savepoint)}
	 *
	 * @see  Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	@Override
	void close() throws SQLException;

	@Override
	default int getSavepointId() throws SQLException {
		return getWrapped().getSavepointId();
	}

	@Override
	default String getSavepointName() throws SQLException {
		return getWrapped().getSavepointName();
	}
}
