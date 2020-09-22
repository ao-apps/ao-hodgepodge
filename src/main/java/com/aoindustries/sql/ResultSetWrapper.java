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
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
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
	 * Wraps a {@link Clob}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapClob(java.sql.Clob)
	 */
	protected ClobWrapper wrapClob(Clob clob) {
		return getConnectionWrapper().wrapClob(clob);
	}

	/**
	 * Unwraps a {@link Clob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapClob(java.sql.Clob)
	 */
	protected Clob unwrapClob(Clob clob) {
		return getConnectionWrapper().unwrapClob(clob);
	}

	/**
	 * Wraps a {@link NClob}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapNClob(java.sql.NClob)
	 */
	protected NClobWrapper wrapNClob(NClob nclob) {
		return getConnectionWrapper().wrapNClob(nclob);
	}

	/**
	 * Unwraps a {@link NClob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapNClob(java.sql.NClob)
	 */
	protected NClob unwrapNClob(NClob nclob) {
		return getConnectionWrapper().unwrapNClob(nclob);
	}

	/**
	 * Wraps a {@link Ref}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapRef(java.sql.Ref)
	 */
	protected RefWrapper wrapRef(Ref ref) {
		return getConnectionWrapper().wrapRef(ref);
	}

	/**
	 * Unwraps a {@link Ref}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapRef(java.sql.Ref)
	 */
	protected Ref unwrapRef(Ref ref) {
		return getConnectionWrapper().unwrapRef(ref);
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

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSetMetaData(java.sql.ResultSetMetaData)
	 */
	@Override
    public ResultSetMetaDataWrapper getMetaData() throws SQLException {
		return wrapResultSetMetaData(getWrapped().getMetaData());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapStatement(java.sql.Statement)
	 */
	@Override
    public StatementWrapper getStatement() throws SQLException {
		return wrapStatement(getWrapped().getStatement());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRef(java.sql.Ref)
	 */
	@Override
    public RefWrapper getRef(int columnIndex) throws SQLException {
		return wrapRef(getWrapped().getRef(columnIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
    public BlobWrapper getBlob(int columnIndex) throws SQLException {
		return wrapBlob(getWrapped().getBlob(columnIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
    public ClobWrapper getClob(int columnIndex) throws SQLException {
		return wrapClob(getWrapped().getClob(columnIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
    public ArrayWrapper getArray(int columnIndex) throws SQLException {
		return wrapArray(getWrapped().getArray(columnIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRef(java.sql.Ref)
	 */
	@Override
    public RefWrapper getRef(String columnLabel) throws SQLException {
		return wrapRef(getWrapped().getRef(columnLabel));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
    public BlobWrapper getBlob(String columnLabel) throws SQLException {
		return wrapBlob(getWrapped().getBlob(columnLabel));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
    public ClobWrapper getClob(String columnLabel) throws SQLException {
		return wrapClob(getWrapped().getClob(columnLabel));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
    public ArrayWrapper getArray(String columnLabel) throws SQLException {
		return wrapArray(getWrapped().getArray(columnLabel));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapRef(java.sql.Ref)
	 */
	@Override
    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
		getWrapped().updateRef(columnIndex, unwrapRef(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapRef(java.sql.Ref)
	 */
	@Override
    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
		getWrapped().updateRef(columnLabel, unwrapRef(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapBlob(java.sql.Blob)
	 */
	@Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
		getWrapped().updateBlob(columnIndex, unwrapBlob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapBlob(java.sql.Blob)
	 */
	@Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
		getWrapped().updateBlob(columnLabel, unwrapBlob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapClob(java.sql.Clob)
	 */
	@Override
    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
		getWrapped().updateClob(columnIndex, unwrapClob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapClob(java.sql.Clob)
	 */
	@Override
    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
		getWrapped().updateClob(columnLabel, unwrapClob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapArray(java.sql.Array)
	 */
	@Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
		getWrapped().updateArray(columnIndex, unwrapArray(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapArray(java.sql.Array)
	 */
	@Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
		getWrapped().updateArray(columnLabel, unwrapArray(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapNClob(java.sql.NClob) 
	 */
	@Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		getWrapped().updateNClob(columnIndex, unwrapNClob(nClob));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapNClob(java.sql.NClob) 
	 */
	@Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		getWrapped().updateNClob(columnLabel, unwrapNClob(nClob));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob) 
	 */
	@Override
    public NClobWrapper getNClob(int columnIndex) throws SQLException {
		return wrapNClob(getWrapped().getNClob(columnIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob) 
	 */
	@Override
    public NClobWrapper getNClob(String columnLabel) throws SQLException {
		return wrapNClob(getWrapped().getNClob(columnLabel));
	}
}
