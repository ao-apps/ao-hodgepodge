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
public abstract class ArrayWrapper implements Array {

	public ArrayWrapper() {
	}

	@Override
	public String getBaseTypeName() throws SQLException {
		return getWrappedArray().getBaseTypeName();
	}

	@Override
	public int getBaseType() throws SQLException {
		return getWrappedArray().getBaseType();
	}

	@Override
	public Object getArray() throws SQLException {
		return getWrappedArray().getArray();
	}

	@Override
	public Object getArray(java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedArray().getArray(map);
	}

	@Override
	public Object getArray(long index, int count) throws SQLException {
		return getWrappedArray().getArray(index, count);
	}

	@Override
	public Object getArray(long index, int count, java.util.Map<String,Class<?>> map) throws SQLException {
		return getWrappedArray().getArray(index, count, map);
	}

	@Override
	public ResultSetWrapper getResultSet() throws SQLException {
		return wrapResultSet(getWrappedArray().getResultSet());
	}

	@Override
	public ResultSetWrapper getResultSet(java.util.Map<String,Class<?>> map) throws SQLException {
		return wrapResultSet(getWrappedArray().getResultSet(map));
	}

	@Override
	public ResultSetWrapper getResultSet(long index, int count) throws SQLException {
		return wrapResultSet(getWrappedArray().getResultSet(index, count));
	}

	@Override
	public ResultSetWrapper getResultSet(long index, int count, java.util.Map<String,Class<?>> map) throws SQLException {
		return wrapResultSet(getWrappedArray().getResultSet(index, count, map));
	}

	@Override
	public void free() throws SQLException {
		getWrappedArray().free();
	}

	protected ResultSetWrapper wrapResultSet(ResultSet results) throws SQLException {
		if(results == null) {
			return null;
		}
		ConnectionWrapper connectionWrapper = getConnectionWrapper();
		Optional<? extends StatementWrapper> arrayStmtWrapper = getStatementWrapper();
		if(results instanceof ResultSetWrapper) {
			ResultSetWrapper resultsWrapper = (ResultSetWrapper)results;
			Optional<? extends StatementWrapper> rsStmtWrapper = resultsWrapper.getStatementWrapper();
			if(
				rsStmtWrapper.isPresent()
				&& rsStmtWrapper.get().getConnectionWrapper() == connectionWrapper
				&& (
					rsStmtWrapper.get() == arrayStmtWrapper.orElse(null)
					|| rsStmtWrapper.get().getWrappedStatement() == arrayStmtWrapper.orElse(null)
				)
			) {
				return resultsWrapper;
			}
		}
		Statement stmt = results.getStatement();
		if(
			arrayStmtWrapper.isPresent()
			&& arrayStmtWrapper.get().getWrappedStatement() == stmt
		) {
			return arrayStmtWrapper.get().wrapResultSet(results);
		} else {
			// Java 9: Use Optional.or

			Optional<? extends StatementWrapper> newStmtWrapper = connectionWrapper.wrapStatement(stmt);
			if(newStmtWrapper.isPresent()) {
				return newStmtWrapper.get().wrapResultSet(results);
			} else {
				return connectionWrapper.wrapResultSet(results);
			}

			//return connectionWrapper.wrapStatement(stmt)
			//	.map(newStmtWrapper -> newStmtWrapper.wrapResultSet(results))
			//	.orElseGet(() -> connectionWrapper.wrapResultSet(results));
		}
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected abstract ConnectionWrapper getConnectionWrapper();

	/**
	 * Gets the statement wrapper.
	 */
	protected abstract Optional<? extends StatementWrapper> getStatementWrapper();

	/**
	 * Gets the array that is wrapped.
	 */
	protected abstract Array getWrappedArray();
}
