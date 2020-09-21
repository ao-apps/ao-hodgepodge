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

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * Wraps a {@link Statement}.
 *
 * @author  AO Industries, Inc.
 */
public interface IStatementWrapper extends Statement {

	/**
	 * Gets the statement that is wrapped.
	 */
	Statement getWrappedStatement();

	@Override
	default <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(this)) return iface.cast(this);
		Statement stmt = getWrappedStatement();
		if(iface.isInstance(stmt)) return iface.cast(stmt);
		return stmt.unwrap(iface);
	}

	@Override
	default boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(this)) return true;
		Statement stmt = getWrappedStatement();
		return iface.isInstance(stmt) || stmt.isWrapperFor(iface);
	}

	@Override
	IResultSetWrapper executeQuery(String sql) throws SQLException;

    @Override
	default int executeUpdate(String sql) throws SQLException {
		return getWrappedStatement().executeUpdate(sql);
	}

	@Override
	default void close() throws SQLException {
		getWrappedStatement().close();
	}

    @Override
	default int getMaxFieldSize() throws SQLException {
		return getWrappedStatement().getMaxFieldSize();
	}

    @Override
	default void setMaxFieldSize(int max) throws SQLException {
		getWrappedStatement().setMaxFieldSize(max);
	}

    @Override
	default int getMaxRows() throws SQLException {
		return getWrappedStatement().getMaxRows();
	}

    @Override
	default void setMaxRows(int max) throws SQLException {
		getWrappedStatement().setMaxRows(max);
	}

    @Override
	default void setEscapeProcessing(boolean enable) throws SQLException {
		getWrappedStatement().setEscapeProcessing(enable);
	}

    @Override
	default int getQueryTimeout() throws SQLException {
		return getWrappedStatement().getQueryTimeout();
	}

    @Override
	default void setQueryTimeout(int seconds) throws SQLException {
		getWrappedStatement().setQueryTimeout(seconds);
	}

    @Override
	default void cancel() throws SQLException {
		getWrappedStatement().cancel();
	}

    @Override
	default SQLWarning getWarnings() throws SQLException {
		return getWrappedStatement().getWarnings();
	}

    @Override
	default void clearWarnings() throws SQLException {
		getWrappedStatement().clearWarnings();
	}

    @Override
	default void setCursorName(String name) throws SQLException {
		getWrappedStatement().setCursorName(name);
	}

    @Override
	default boolean execute(String sql) throws SQLException {
		return getWrappedStatement().execute(sql);
	}

	@Override
	IResultSetWrapper getResultSet() throws SQLException;

    @Override
	default int getUpdateCount() throws SQLException {
		return getWrappedStatement().getUpdateCount();
	}

    @Override
	default boolean getMoreResults() throws SQLException {
		return getWrappedStatement().getMoreResults();
	}

    @Override
	default void setFetchDirection(int direction) throws SQLException {
		getWrappedStatement().setFetchDirection(direction);
	}

    @Override
	default int getFetchDirection() throws SQLException {
		return getWrappedStatement().getFetchDirection();
	}

    @Override
	default void setFetchSize(int rows) throws SQLException {
		getWrappedStatement().setFetchSize(rows);
	}

    @Override
	default int getFetchSize() throws SQLException {
		return getWrappedStatement().getFetchSize();
	}

    @Override
	default int getResultSetConcurrency() throws SQLException {
		return getWrappedStatement().getResultSetConcurrency();
	}

    @Override
	default int getResultSetType() throws SQLException {
		return getWrappedStatement().getResultSetType();
	}

    @Override
	default void addBatch(String sql) throws SQLException {
		getWrappedStatement().addBatch(sql);
	}

    @Override
	default void clearBatch() throws SQLException {
		getWrappedStatement().clearBatch();
	}

    @Override
	default int[] executeBatch() throws SQLException {
		return getWrappedStatement().executeBatch();
	}

	@Override
	IConnectionWrapper getConnection() throws SQLException;

    @Override
	default boolean getMoreResults(int current) throws SQLException {
		return getWrappedStatement().getMoreResults(current);
	}

	@Override
	IResultSetWrapper getGeneratedKeys() throws SQLException;

    @Override
	default int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return getWrappedStatement().executeUpdate(sql, autoGeneratedKeys);
	}

    @Override
	default int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
		return getWrappedStatement().executeUpdate(sql, columnIndexes);
	}

    @Override
	default int executeUpdate(String sql, String columnNames[]) throws SQLException {
		return getWrappedStatement().executeUpdate(sql, columnNames);
	}

    @Override
	default boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return getWrappedStatement().execute(sql, autoGeneratedKeys);
	}

    @Override
	default boolean execute(String sql, int columnIndexes[]) throws SQLException {
		return getWrappedStatement().execute(sql, columnIndexes);
	}

    @Override
	default boolean execute(String sql, String columnNames[]) throws SQLException {
		return getWrappedStatement().execute(sql, columnNames);
	}

    @Override
	default int getResultSetHoldability() throws SQLException {
		return getWrappedStatement().getResultSetHoldability();
	}

    @Override
	default boolean isClosed() throws SQLException {
		return getWrappedStatement().isClosed();
	}

	@Override
	default void setPoolable(boolean poolable) throws SQLException {
		getWrappedStatement().setPoolable(poolable);
	}

	@Override
	default boolean isPoolable() throws SQLException {
		return getWrappedStatement().isPoolable();
	}

    @Override
	default void closeOnCompletion() throws SQLException {
		getWrappedStatement().closeOnCompletion();
	}

    @Override
	default boolean isCloseOnCompletion() throws SQLException {
		return getWrappedStatement().isCloseOnCompletion();
	}

    @Override
	default long getLargeUpdateCount() throws SQLException {
		return getWrappedStatement().getLargeUpdateCount();
	}

    @Override
	default void setLargeMaxRows(long max) throws SQLException {
		getWrappedStatement().setLargeMaxRows(max);
	}

	@Override
	default long getLargeMaxRows() throws SQLException {
		return getWrappedStatement().getLargeMaxRows();
	}

    @Override
	default long[] executeLargeBatch() throws SQLException {
		return getWrappedStatement().executeLargeBatch();
	}

    @Override
	default long executeLargeUpdate(String sql) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql);
	}

    @Override
	default long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql, autoGeneratedKeys);
	}

    @Override
	default long executeLargeUpdate(String sql, int columnIndexes[]) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql, columnIndexes);
	}

    @Override
	default long executeLargeUpdate(String sql, String columnNames[]) throws SQLException {
		return getWrappedStatement().executeLargeUpdate(sql, columnNames);
	}

	// Java 9: String enquoteLiteral(String val)  throws SQLException;
	// Java 9: String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException
	// Java 9: boolean isSimpleIdentifier(String identifier) throws SQLException
	// Java 9: String enquoteNCharLiteral(String val)  throws SQLException
}
