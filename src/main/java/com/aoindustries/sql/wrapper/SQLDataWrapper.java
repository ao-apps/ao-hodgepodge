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
public class SQLDataWrapper implements ISQLDataWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final SQLData wrapped;

	public SQLDataWrapper(ConnectionWrapper connectionWrapper, SQLData wrapped) {
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
	public SQLData getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps a {@link SQLInput}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapSQLInput(java.sql.SQLInput)
	 */
	protected SQLInputWrapper wrapSQLInput(SQLInput sqlInput) {
		return getConnectionWrapper().wrapSQLInput(sqlInput);
	}

	/**
	 * Wraps a {@link SQLOutput}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapSQLOutput(java.sql.SQLOutput)
	 */
	protected SQLOutputWrapper wrapSQLOutput(SQLOutput sqlOutput) {
		return getConnectionWrapper().wrapSQLOutput(sqlOutput);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  SQLInputWrapper#close()
	 */
	@Override
	public void readSQL(SQLInput stream, String typeName) throws SQLException {
		try (SQLInputWrapper streamWrapper = wrapSQLInput(stream)) {
			getWrapped().readSQL(streamWrapper, typeName);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  SQLOutputWrapper#close()
	 */
	@Override
	public void writeSQL(SQLOutput stream) throws SQLException {
		try (SQLOutputWrapper streamWrapper = wrapSQLOutput(stream)) {
			getWrapped().writeSQL(streamWrapper);
		}
	}
}
