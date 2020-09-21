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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Wraps a {@link ResultSet}.
 *
 * @author  AO Industries, Inc.
 */
public class ResultSetWrapper implements IResultSetWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final StatementWrapper stmtWrapper;
	private final ResultSet wrapped;

	public ResultSetWrapper(ConnectionWrapper connectionWrapper, StatementWrapper stmtWrapper, ResultSet wrapped) {
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
	public ResultSet getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#newArrayWrapper(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 * @see  #unwrapArray(java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		if(array == null) {
			return null;
		}
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		StatementWrapper resultsStmtWrapper = getStatementWrapper().orElse(null);
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(
				arrayWrapper.getConnectionWrapper() == _connectionWrapper
				&& arrayWrapper.getStatementWrapper().orElse(null) == resultsStmtWrapper
			) {
				return arrayWrapper;
			}
		}
		return _connectionWrapper.newArrayWrapper(resultsStmtWrapper, array);
	}

	/**
	 * Unwraps an {@link Array}, if wrapped by this wrapper.
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	protected Array unwrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(arrayWrapper.getConnectionWrapper() == getConnectionWrapper()) {
				return arrayWrapper.getWrapped();
			}
		}
		return array;
	}

	/**
	 * Wraps a {@link ResultSetMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#newResultSetMetaDataWrapper(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapper wrapResultSetMetaData(ResultSetMetaData metaData) {
		if(metaData == null) {
			return null;
		}
		ConnectionWrapper _connectionWrapper = getConnectionWrapper();
		if(metaData instanceof ResultSetMetaDataWrapper) {
			ResultSetMetaDataWrapper metaDataWrapper = (ResultSetMetaDataWrapper)metaData;
			if(metaDataWrapper.getConnectionWrapper() == _connectionWrapper) {
				return metaDataWrapper;
			}
		}
		return _connectionWrapper.newResultSetMetaDataWrapper(metaData);
	}

	/**
	 * Wraps a {@link Statement}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper wrapStatement(Statement stmt) {
		if(stmt == null) {
			return null;
		}
		StatementWrapper _stmtWrapper = getStatementWrapper().orElse(null);
		if(
			_stmtWrapper != null
			&& (
		 		_stmtWrapper == stmt
		 		|| _stmtWrapper.getWrapped() == stmt
			)
		) {
			return _stmtWrapper;
		} else {
			return getConnectionWrapper().wrapStatement(stmt);
		}
	}

	@Override
    public ResultSetMetaDataWrapper getMetaData() throws SQLException {
		return wrapResultSetMetaData(getWrapped().getMetaData());
	}

	@Override
    public StatementWrapper getStatement() throws SQLException {
		return wrapStatement(getWrapped().getStatement());
	}

	@Override
    public ArrayWrapper getArray(int columnIndex) throws SQLException {
		return wrapArray(getWrapped().getArray(columnIndex));
	}

	@Override
    public ArrayWrapper getArray(String columnLabel) throws SQLException {
		return wrapArray(getWrapped().getArray(columnLabel));
	}

	@Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
		IResultSetWrapper.super.updateArray(columnIndex, unwrapArray(x));
	}

	@Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
		IResultSetWrapper.super.updateArray(columnLabel, unwrapArray(x));
	}
}
