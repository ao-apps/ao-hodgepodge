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
import java.sql.Blob;
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
	 * @see  ConnectionWrapper#wrapArray(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		return getConnectionWrapper().wrapArray(getStatementWrapper().orElse(null), array);
	}

	/**
	 * Unwraps an {@link Array}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapArray(java.sql.Array)
	 */
	protected Array unwrapArray(Array array) {
		return getConnectionWrapper().unwrapArray(array);
	}

	/**
	 * Wraps a {@link Blob}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapBlob(java.sql.Blob)
	 */
	protected BlobWrapper wrapBlob(Blob blob) {
		return getConnectionWrapper().wrapBlob(blob);
	}

	/**
	 * Unwraps a {@link Blob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapBlob(java.sql.Blob)
	 */
	protected Blob unwrapBlob(Blob blob) {
		return getConnectionWrapper().unwrapBlob(blob);
	}

	/**
	 * Wraps a {@link ResultSetMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 */
	protected ResultSetMetaDataWrapper wrapResultSetMetaData(ResultSetMetaData metaData) {
		return getConnectionWrapper().wrapResultSetMetaData(metaData);
	}

	/**
	 * Wraps a {@link Statement}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapStatement(java.sql.Statement)
	 */
	protected StatementWrapper wrapStatement(Statement stmt) {
		return getConnectionWrapper().wrapStatement(stmt);
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
    public BlobWrapper getBlob(int columnIndex) throws SQLException {
		return wrapBlob(getWrapped().getBlob(columnIndex));
	}

	@Override
    public ArrayWrapper getArray(int columnIndex) throws SQLException {
		return wrapArray(getWrapped().getArray(columnIndex));
	}

	@Override
    public BlobWrapper getBlob(String columnLabel) throws SQLException {
		return wrapBlob(getWrapped().getBlob(columnLabel));
	}

	@Override
    public ArrayWrapper getArray(String columnLabel) throws SQLException {
		return wrapArray(getWrapped().getArray(columnLabel));
	}

	@Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
		getWrapped().updateBlob(columnIndex, unwrapBlob(x));
	}

	@Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
		getWrapped().updateBlob(columnLabel, unwrapBlob(x));
	}

	@Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
		getWrapped().updateArray(columnIndex, unwrapArray(x));
	}

	@Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
		getWrapped().updateArray(columnLabel, unwrapArray(x));
	}
}
