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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Wraps an {@link Array}.
 *
 * @author  AO Industries, Inc.
 */
public class ArrayWrapper implements IArrayWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final StatementWrapper stmtWrapper;
	private final Array wrapped;

	public ArrayWrapper(ConnectionWrapper connectionWrapper, StatementWrapper stmtWrapper, Array wrapped) {
		this.connectionWrapper = connectionWrapper;
		this.stmtWrapper = stmtWrapper;
		this.wrapped = wrapped;
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}

	/**
	 * Gets the statement wrapper.
	 */
	protected Optional<? extends StatementWrapper> getStatementWrapper() {
		return Optional.ofNullable(stmtWrapper);
	}

	@Override
	public Array getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapResultSet(java.sql.ResultSet)
	 * @see  StatementWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(ResultSet results) throws SQLException {
		if(results == null) {
			return null;
		}
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		StatementWrapper _stmtWrapper = getStatementWrapper().orElse(null);
		if(results instanceof ResultSetWrapper) {
			ResultSetWrapper resultsWrapper = (ResultSetWrapper)results;
			Optional<? extends StatementWrapper> rsStmtWrapper = resultsWrapper.getStatementWrapper();
			if(
				rsStmtWrapper.isPresent()
				&& rsStmtWrapper.get().getConnectionWrapper() == _connectionWrapper
				&& (
					rsStmtWrapper.get() == _stmtWrapper
					|| rsStmtWrapper.get().getWrapped() == _stmtWrapper
				)
			) {
				return resultsWrapper;
			}
		}
		Statement stmt = results.getStatement();
		if(
			_stmtWrapper != null
			&& _stmtWrapper.getWrapped() == stmt
		) {
			return _stmtWrapper.wrapResultSet(results);
		} else {
			StatementWrapper newStmtWrapper = _connectionWrapper.wrapStatement(stmt);
			if(newStmtWrapper != null) {
				return newStmtWrapper.wrapResultSet(results);
			} else {
				return _connectionWrapper.wrapResultSet(results);
			}
		}
	}

	@Override
	public ResultSetWrapper getResultSet() throws SQLException {
		return wrapResultSet(getWrapped().getResultSet());
	}

	@Override
	public ResultSetWrapper getResultSet(java.util.Map<String,Class<?>> map) throws SQLException {
		return wrapResultSet(getWrapped().getResultSet(map));
	}

	@Override
	public ResultSetWrapper getResultSet(long index, int count) throws SQLException {
		return wrapResultSet(getWrapped().getResultSet(index, count));
	}

	@Override
	public ResultSetWrapper getResultSet(long index, int count, java.util.Map<String,Class<?>> map) throws SQLException {
		return wrapResultSet(getWrapped().getResultSet(index, count, map));
	}
}
