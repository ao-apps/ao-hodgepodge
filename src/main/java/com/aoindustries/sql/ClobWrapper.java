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

import java.sql.Clob;
import java.sql.SQLException;

/**
 * Wraps a {@link Clob}.
 *
 * @author  AO Industries, Inc.
 */
public class ClobWrapper implements IClobWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final Clob wrapped;

	public ClobWrapper(ConnectionWrapper connectionWrapper, Clob wrapped) {
		this.connectionWrapper = connectionWrapper;
		this.wrapped = wrapped;
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}

	@Override
	public Clob getWrapped() {
		return wrapped;
	}

	/**
	 * Unwraps a {@link Clob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapClob(java.sql.Clob)
	 */
	protected Clob unwrapClob(Clob clob) {
		return getConnectionWrapper().unwrapClob(clob);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapClob(java.sql.Clob)
	 */
	@Override
	public long position(Clob pattern, long start) throws SQLException {
		return getWrapped().position(unwrapClob(pattern), start);
	}
}
