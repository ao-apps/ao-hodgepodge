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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wraps a {@link Statement}.
 *
 * @author  AO Industries, Inc.
 */
public class StatementWrapper implements IStatementWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final Statement wrapped;

	public StatementWrapper(ConnectionWrapper connectionWrapper, Statement wrapped) {
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
	public Statement getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapResultSet(com.aoindustries.sql.StatementWrapper, java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(ResultSet results) throws SQLException {
		return getConnectionWrapper().wrapResultSet(this, results);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper executeQuery(String sql) throws SQLException {
		return wrapResultSet(getWrapped().executeQuery(sql));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
    @Override
	public ResultSetWrapper getResultSet() throws SQLException {
		return wrapResultSet(getWrapped().getResultSet());
	}

    @Override
	public ConnectionWrapper getConnection() throws SQLException {
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		assert getWrapped().getConnection() == _connectionWrapper.getWrapped();
		return _connectionWrapper;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
    @Override
	public ResultSetWrapper getGeneratedKeys() throws SQLException {
		return wrapResultSet(getWrapped().getGeneratedKeys());
	}
}
