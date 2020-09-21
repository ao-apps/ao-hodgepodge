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
import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * Wraps a {@link CallableStatement}.
 *
 * @author  AO Industries, Inc.
 */
public class CallableStatementWrapper extends PreparedStatementWrapper implements ICallableStatementWrapper {

	public CallableStatementWrapper(ConnectionWrapper connectionWrapper, CallableStatement wrapped) {
		super(connectionWrapper, wrapped);
	}

	@Override
	public CallableStatement getWrapped() {
		return (CallableStatement)super.getWrapped();
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#newArrayWrapper(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(arrayWrapper.getStatementWrapper().orElse(null) == this) {
				return arrayWrapper;
			}
		}
		return getConnectionWrapper().newArrayWrapper(this, array);
	}

	@Override
    public ArrayWrapper getArray(int parameterIndex) throws SQLException {
		return wrapArray(getWrapped().getArray(parameterIndex));
	}

	@Override
    public ArrayWrapper getArray(String parameterName) throws SQLException {
		return wrapArray(getWrapped().getArray(parameterName));
	}
}
