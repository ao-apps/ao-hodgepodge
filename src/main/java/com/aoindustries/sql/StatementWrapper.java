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
	public Statement getWrappedStatement() {
		return wrapped;
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#newResultSetWrapper(com.aoindustries.sql.StatementWrapper, java.sql.ResultSet)
	 * @see  ArrayWrapper#wrapResultSet(java.sql.ResultSet)
	 * @see  DatabaseMetaDataWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(ResultSet results) {
		if(results == null) {
			return null;
		}
		if(results instanceof ResultSetWrapper) {
			ResultSetWrapper resultsWrapper = (ResultSetWrapper)results;
			if(resultsWrapper.getStatementWrapper().orElse(null) == this) {
				return resultsWrapper;
			}
		}
		return getConnectionWrapper().newResultSetWrapper(this, results);
	}

	@Override
	public ResultSetWrapper executeQuery(String sql) throws SQLException {
		return wrapResultSet(getWrappedStatement().executeQuery(sql));
	}

    @Override
	public ResultSetWrapper getResultSet() throws SQLException {
		return wrapResultSet(getWrappedStatement().getResultSet());
	}

    @Override
	public ConnectionWrapper getConnection() throws SQLException {
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		assert getWrappedStatement().getConnection() == _connectionWrapper.getWrappedConnection();
		return _connectionWrapper;
	}

    @Override
	public ResultSetWrapper getGeneratedKeys() throws SQLException {
		return wrapResultSet(getWrappedStatement().getGeneratedKeys());
	}
}
