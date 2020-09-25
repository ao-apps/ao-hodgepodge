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
package com.aoindustries.sql.failfast;

import com.aoindustries.exception.WrappedException;
import com.aoindustries.lang.Throwables;
import com.aoindustries.sql.wrapper.RowIdWrapper;
import java.sql.RowId;

/**
 * @see  FailFastConnection
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastRowId extends RowIdWrapper {

	public FailFastRowId(FailFastConnection failFastConnection, RowId wrapped) {
		super(failFastConnection, wrapped);
	}

	@Override
	protected FailFastConnection getConnectionWrapper() {
		return (FailFastConnection)super.getConnectionWrapper();
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		try {
			return super.equals(obj);
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, WrappedException.class, WrappedException::new);
		}
	}

	@Override
	public byte[] getBytes() {
		try {
			return super.getBytes();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, WrappedException.class, WrappedException::new);
		}
	}

	@Override
	public String toString() {
		try {
			return super.toString();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, WrappedException.class, WrappedException::new);
		}
	}

	@Override
	public int hashCode() {
		try {
			return super.hashCode();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, WrappedException.class, WrappedException::new);
		}
	}
}
