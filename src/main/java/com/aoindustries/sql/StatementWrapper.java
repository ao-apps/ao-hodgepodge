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
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Optional;

/**
 * Wraps a {@link Statement}.
 *
 * @author  AO Industries, Inc.
 */
public abstract class StatementWrapper implements Statement {

	public StatementWrapper() {
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		Statement stmt = getWrappedStatement();
		if(iface.isInstance(stmt)) return iface.cast(stmt);
		return stmt.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		Statement stmt = getWrappedStatement();
		return iface.isInstance(stmt) || stmt.isWrapperFor(iface);
	}

	@Override
	public ResultSetWrapper executeQuery(String sql) throws SQLException {
		return wrapResultSet(getWrappedStatement().executeQuery(sql));
	}

    @Override
	public int executeUpdate(String sql) throws SQLException {
		return getWrappedStatement().executeUpdate(sql);
	}

	@Override
	public void close() throws SQLException {
		getWrappedStatement().close();
	}

    @Override
	public int getMaxFieldSize() throws SQLException {
		return getWrappedStatement().getMaxFieldSize();
	}

    @Override
	public void setMaxFieldSize(int max) throws SQLException {
		getWrappedStatement().setMaxFieldSize(max);
	}

    @Override
	public int getMaxRows() throws SQLException {
		return getWrappedStatement().getMaxRows();
	}

    @Override
	public void setMaxRows(int max) throws SQLException {
		getWrappedStatement().setMaxRows(max);
	}

    @Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		getWrappedStatement().setEscapeProcessing(enable);
	}

    @Override
	public int getQueryTimeout() throws SQLException {
		return getWrappedStatement().getQueryTimeout();
	}

    @Override
	public void setQueryTimeout(int seconds) throws SQLException {
		getWrappedStatement().setQueryTimeout(seconds);
	}

    @Override
	public void cancel() throws SQLException {
		getWrappedStatement().cancel();
	}

    @Override
	public SQLWarning getWarnings() throws SQLException {
		return getWrappedStatement().getWarnings();
	}

    @Override
	public void clearWarnings() throws SQLException {
		getWrappedStatement().clearWarnings();
	}

    @Override
	public void setCursorName(String name) throws SQLException {
		getWrappedStatement().setCursorName(name);
	}

    @Override
	public boolean execute(String sql) throws SQLException {
		return getWrappedStatement().execute(sql);
	}

    @Override
	public ResultSetWrapper getResultSet() throws SQLException {
		return wrapResultSet(getWrappedStatement().getResultSet());
	}

    @Override
	public int getUpdateCount() throws SQLException {
		return getWrappedStatement().getUpdateCount();
	}

    @Override
	public boolean getMoreResults() throws SQLException {
		return getWrappedStatement().getMoreResults();
	}

    @Override
	public void setFetchDirection(int direction) throws SQLException {
		getWrappedStatement().setFetchDirection(direction);
	}

    @Override
	public int getFetchDirection() throws SQLException {
		return getWrappedStatement().getFetchDirection();
	}

    @Override
	public void setFetchSize(int rows) throws SQLException {
		getWrappedStatement().setFetchSize(rows);
	}

    @Override
	public int getFetchSize() throws SQLException {
		return getWrappedStatement().getFetchSize();
	}

    @Override
	public int getResultSetConcurrency() throws SQLException {
		return getWrappedStatement().getResultSetConcurrency();
	}

    @Override
	public int getResultSetType()  throws SQLException {
		return getWrappedStatement().getResultSetType();
	}

    @Override
	public void addBatch(String sql) throws SQLException {
		getWrappedStatement().addBatch(sql);
	}

    @Override
	public void clearBatch() throws SQLException {
		getWrappedStatement().clearBatch();
	}

    @Override
	public int[] executeBatch() throws SQLException {
		return getWrappedStatement().executeBatch();
	}

    @Override
	public ConnectionWrapper getConnection()  throws SQLException {
		ConnectionWrapper connectionWrapper = getConnectionWrapper();
		assert getWrappedStatement().getConnection() == connectionWrapper.getWrappedConnection();
		return connectionWrapper;
	}

    @Override
	public boolean getMoreResults(int current) throws SQLException {
		return getWrappedStatement().getMoreResults(current);
	}

    @Override
	public ResultSetWrapper getGeneratedKeys() throws SQLException {
		return wrapResultSet(getWrappedStatement().getGeneratedKeys());
	}

    @Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return getWrappedStatement().executeUpdate(sql, autoGeneratedKeys);
	}

    @Override
	public int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
		return getWrappedStatement().executeUpdate(sql, columnIndexes);
	}

    @Override
	public int executeUpdate(String sql, String columnNames[]) throws SQLException {
		return getWrappedStatement().executeUpdate(sql, columnNames);
	}

    @Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return getWrappedStatement().execute(sql, autoGeneratedKeys);
	}

    @Override
	public boolean execute(String sql, int columnIndexes[]) throws SQLException {
		return getWrappedStatement().execute(sql, columnIndexes);
	}

    @Override
	public boolean execute(String sql, String columnNames[]) throws SQLException {
		return getWrappedStatement().execute(sql, columnNames);
	}

    @Override
	public int getResultSetHoldability() throws SQLException {
		return getWrappedStatement().getResultSetHoldability();
	}

    @Override
	public boolean isClosed() throws SQLException {
		return getWrappedStatement().isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		getWrappedStatement().setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return getWrappedStatement().isPoolable();
	}

    @Override
	public void closeOnCompletion() throws SQLException {
		getWrappedStatement().closeOnCompletion();
	}

    @Override
	public boolean isCloseOnCompletion() throws SQLException {
		return getWrappedStatement().isCloseOnCompletion();
	}

    @Override
	public long getLargeUpdateCount() throws SQLException {
		return getWrappedStatement().getLargeUpdateCount();
	}

    @Override
	public void setLargeMaxRows(long max) throws SQLException {
		getWrappedStatement().setLargeMaxRows(max);
	}

	@Override
	public long getLargeMaxRows() throws SQLException {
		return getWrappedStatement().getLargeMaxRows();
	}

    @Override
	public long[] executeLargeBatch() throws SQLException {
		return getWrappedStatement().executeLargeBatch();
	}

    @Override
	public long executeLargeUpdate(String sql) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql);
	}

    @Override
	public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql, autoGeneratedKeys);
	}

    @Override
	public long executeLargeUpdate(String sql, int columnIndexes[]) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql, columnIndexes);
	}

    @Override
	public long executeLargeUpdate(String sql, String columnNames[]) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql, columnNames);
	}

	// Java 9: String enquoteLiteral(String val)  throws SQLException;
	// Java 9: String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException
	// Java 9: boolean isSimpleIdentifier(String identifier) throws SQLException
	// Java 9: String enquoteNCharLiteral(String val)  throws SQLException
	
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
		return new ResultSetWrapper() {
			@Override
			protected ConnectionWrapper getConnectionWrapper() {
				return StatementWrapper.this.getConnectionWrapper();
			}

			@Override
			protected Optional<? extends StatementWrapper> getStatementWrapper() {
				return Optional.of(StatementWrapper.this);
			}

			@Override
			protected ResultSet getWrappedResultSet() {
				return results;
			}
		};
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected abstract ConnectionWrapper getConnectionWrapper();

	/**
	 * Gets the statement that is wrapped.
	 */
	protected abstract Statement getWrappedStatement();
}
