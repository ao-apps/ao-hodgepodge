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

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

/**
 * Wraps a {@link SQLData}.
 *
 * @author  AO Industries, Inc.
 */
public class SQLDataWrapperImpl implements ISQLDataWrapper {

	private final ConnectionWrapperImpl connectionWrapper;
	private final SQLData wrapped;

	public SQLDataWrapperImpl(ConnectionWrapperImpl connectionWrapper, SQLData wrapped) {
		this.connectionWrapper = connectionWrapper;
		this.wrapped = wrapped;
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected ConnectionWrapperImpl getConnectionWrapper() {
		return connectionWrapper;
	}

	@Override
	public SQLData getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps a {@link SQLInput}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapSQLInput(java.sql.SQLInput)
	 */
	protected SQLInputWrapperImpl wrapSQLInput(SQLInput sqlInput) {
		return getConnectionWrapper().wrapSQLInput(sqlInput);
	}

	/**
	 * Wraps a {@link SQLOutput}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapSQLOutput(java.sql.SQLOutput)
	 */
	protected SQLOutputWrapperImpl wrapSQLOutput(SQLOutput sqlOutput) {
		return getConnectionWrapper().wrapSQLOutput(sqlOutput);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  SQLInputWrapperImpl#close()
	 */
	@Override
	public void readSQL(SQLInput stream, String typeName) throws SQLException {
		try (SQLInputWrapperImpl streamWrapper = wrapSQLInput(stream)) {
			getWrapped().readSQL(streamWrapper, typeName);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  SQLOutputWrapperImpl#close()
	 */
	@Override
	public void writeSQL(SQLOutput stream) throws SQLException {
		try (SQLOutputWrapperImpl streamWrapper = wrapSQLOutput(stream)) {
			getWrapped().writeSQL(streamWrapper);
		}
	}
}
