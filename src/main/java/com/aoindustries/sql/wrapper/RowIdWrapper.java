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

import java.sql.RowId;

/**
 * Wraps a {@link RowId}.
 *
 * @author  AO Industries, Inc.
 */
public class RowIdWrapper implements IRowIdWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final RowId wrapped;

	public RowIdWrapper(ConnectionWrapper connectionWrapper, RowId wrapped) {
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
	public RowId getWrapped() {
		return wrapped;
	}

	/**
	 * Unwraps a {@link RowId}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapRowId(java.sql.RowId)
	 */
	protected RowId unwrapRowId(RowId rowId) {
		return getConnectionWrapper().unwrapRowId(rowId);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RowId) {
			obj = unwrapRowId((RowId)obj);
		}
		return getWrapped().equals(obj);
	}

	@Override
	public String toString() {
		return getWrapped().toString();
	}

	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}
}
