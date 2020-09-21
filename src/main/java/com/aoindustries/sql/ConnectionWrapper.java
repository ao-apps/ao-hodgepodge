/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2020  AO Industries, Inc.
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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wraps a {@link Connection}.
 *
 * @author  AO Industries, Inc.
 */
public class ConnectionWrapper implements IConnectionWrapper {

	private final Connection wrapped;

	public ConnectionWrapper(Connection wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Connection getWrapped() {
		return wrapped;
	}

	/**
	 * Creates a new {@link ArrayWrapper}.
	 *
	 * @see  CallableStatementWrapper#wrapArray(java.sql.Array)
	 * @see  #wrapArray(java.sql.Array)
	 * @see  ResultSetWrapper#wrapArray(java.sql.Array)
	 */
	protected ArrayWrapper newArrayWrapper(StatementWrapper stmtWrapper, Array array) {
		return new ArrayWrapper(this, stmtWrapper, array);
	}

	/**
	 * Creates a new {@link CallableStatementWrapper}.
	 *
	 * @see  #wrapCallableStatement(java.sql.CallableStatement)
	 */
	protected CallableStatementWrapper newCallableStatementWrapper(CallableStatement cstmt) {
		return new CallableStatementWrapper(this, cstmt);
	}

	/**
	 * Creates a new {@link DatabaseMetaDataWrapper}.
	 *
	 * @see  #wrapDatabaseMetaData(java.sql.DatabaseMetaData)
	 */
	protected DatabaseMetaDataWrapper newDatabaseMetaDataWrapper(DatabaseMetaData metaData) {
		return new DatabaseMetaDataWrapper(this, metaData);
	}

	/**
	 * Creates a new {@link PreparedStatementWrapper}.
	 *
	 * @see  #wrapPreparedStatement(java.sql.PreparedStatement)
	 */
	protected PreparedStatementWrapper newPreparedStatementWrapper(PreparedStatement pstmt) {
		return new PreparedStatementWrapper(this, pstmt);
	}

	/**
	 * Creates a new {@link ResultSetWrapper}.
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 * @see  StatementWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper newResultSetWrapper(StatementWrapper stmtWrapper, ResultSet results) {
		return new ResultSetWrapper(this, stmtWrapper, results);
	}

	/**
	 * Creates a new {@link ResultSetMetaDataWrapper}.
	 *
	 * @see  PreparedStatementWrapper#wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 * @see  ResultSetWrapper#wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapper newResultSetMetaDataWrapper(ResultSetMetaData metaData) {
		return new ResultSetMetaDataWrapper(this, metaData);
	}

	/**
	 * Creates a new {@link StatementWrapper}.
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper newStatementWrapper(Statement stmt) {
		return new StatementWrapper(this, stmt);
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  #newArrayWrapper(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 * @see  #unwrapArray(java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		if(array == null) {
			return null;
		}
		if(array instanceof ArrayWrapper) {
			ArrayWrapper arrayWrapper = (ArrayWrapper)array;
			if(
				arrayWrapper.getConnectionWrapper() == this
				&& !arrayWrapper.getStatementWrapper().isPresent()
			) {
				return arrayWrapper;
			}
		}
		return newArrayWrapper(null, array);
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
			if(arrayWrapper.getConnectionWrapper() == this) {
				return arrayWrapper.getWrapped();
			}
		}
		return array;
	}

	/**
	 * Wraps a {@link CallableStatement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newCallableStatementWrapper(java.sql.CallableStatement)
	 */
	protected CallableStatementWrapper wrapCallableStatement(CallableStatement cstmt) {
		if(cstmt == null) {
			return null;
		}
		if(cstmt instanceof CallableStatementWrapper) {
			CallableStatementWrapper stmtWrapper = (CallableStatementWrapper)cstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newCallableStatementWrapper(cstmt);
	}

	/**
	 * Wraps a {@link DatabaseMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  #newDatabaseMetaDataWrapper(java.sql.DatabaseMetaData)
	 */
	protected DatabaseMetaDataWrapper wrapDatabaseMetaData(DatabaseMetaData metaData) {
		if(metaData instanceof DatabaseMetaDataWrapper) {
			DatabaseMetaDataWrapper metaDataWrapper = (DatabaseMetaDataWrapper)metaData;
			if(metaDataWrapper.getConnectionWrapper() == this) {
				return metaDataWrapper;
			}
		}
		return newDatabaseMetaDataWrapper(metaData);
	}

	/**
	 * Wraps a {@link PreparedStatement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newPreparedStatementWrapper(java.sql.PreparedStatement)
	 */
	protected PreparedStatementWrapper wrapPreparedStatement(PreparedStatement pstmt) {
		if(pstmt == null) {
			return null;
		}
		if(pstmt instanceof CallableStatement) {
			return wrapCallableStatement((CallableStatement)pstmt);
		}
		if(pstmt instanceof PreparedStatementWrapper) {
			PreparedStatementWrapper stmtWrapper = (PreparedStatementWrapper)pstmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newPreparedStatementWrapper(pstmt);
	}

	/**
	 * Wraps a {@link ResultSet}, if not already wrapped by this wrapper.
	 *
	 * @see  #newResultSetWrapper(com.aoindustries.sql.StatementWrapper, java.sql.ResultSet)
	 * @see  #wrapStatement(java.sql.Statement)
	 * @see  ArrayWrapper#wrapResultSet(java.sql.ResultSet)
	 * @see  DatabaseMetaDataWrapper#wrapResultSet(java.sql.ResultSet)
	 */
	protected ResultSetWrapper wrapResultSet(ResultSet results) throws SQLException {
		if(results == null) {
			return null;
		}
		if(results instanceof ResultSetWrapper) {
			ResultSetWrapper resultsWrapper = (ResultSetWrapper)results;
			if(
				resultsWrapper.getConnectionWrapper() == this
				&& !resultsWrapper.getStatementWrapper().isPresent()
			) {
				return resultsWrapper;
			}
		}
		StatementWrapper stmtWrapper = wrapStatement(results.getStatement());
		if(stmtWrapper != null) {
			return stmtWrapper.wrapResultSet(results);
		} else {
			return newResultSetWrapper(null, results);
		}
	}

	/**
	 * Wraps a {@link Statement}, if not already wrapped by this wrapper.
	 *
	 * @see  #newStatementWrapper(java.sql.Statement)
	 * @see  ResultSetWrapper#wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper wrapStatement(Statement stmt) {
		if(stmt == null) {
			return null;
		}
		if(stmt instanceof PreparedStatement) {
			return wrapPreparedStatement((PreparedStatement)stmt);
		}
		if(stmt instanceof StatementWrapper) {
			StatementWrapper stmtWrapper = (StatementWrapper)stmt;
			if(stmtWrapper.getConnectionWrapper() == this) {
				return stmtWrapper;
			}
		}
		return newStatementWrapper(stmt);
	}

	@Override
	public StatementWrapper createStatement() throws SQLException {
		return wrapStatement(getWrapped().createStatement());
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql));
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql));
	}

	@Override
	public DatabaseMetaDataWrapper getMetaData() throws SQLException {
		return wrapDatabaseMetaData(getWrapped().getMetaData());
	}

	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapStatement(getWrapped().createStatement(resultSetType, resultSetConcurrency));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql, resultSetType, resultSetConcurrency));
	}

	@Override
	public StatementWrapper createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapStatement(getWrapped().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public CallableStatementWrapper prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return wrapCallableStatement(getWrapped().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, autoGeneratedKeys));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, int columnIndexes[]) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, columnIndexes));
	}

	@Override
	public PreparedStatementWrapper prepareStatement(String sql, String columnNames[]) throws SQLException {
		return wrapPreparedStatement(getWrapped().prepareStatement(sql, columnNames));
	}

	@Override
	public ArrayWrapper createArrayOf(String typeName, Object[] elements) throws SQLException {
		return wrapArray(getWrapped().createArrayOf(typeName, elements));
	}
}
