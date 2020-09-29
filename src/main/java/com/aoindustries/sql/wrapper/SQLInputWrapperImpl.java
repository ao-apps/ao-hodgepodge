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

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLXML;

/**
 * Wraps a {@link SQLInput}.
 *
 * @author  AO Industries, Inc.
 */
public class SQLInputWrapperImpl implements ISQLInputWrapper {

	private final ConnectionWrapperImpl connectionWrapper;
	private final SQLInput wrapped;

	public SQLInputWrapperImpl(ConnectionWrapperImpl connectionWrapper, SQLInput wrapped) {
		this.connectionWrapper = connectionWrapper;
		this.wrapped = wrapped;
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected ConnectionWrapperImpl getConnectionWrapper() {
		return connectionWrapper;
	}

	@Override
	public SQLInput getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapArray(com.aoindustries.sql.StatementWrapperImpl, java.sql.Array)
	 */
	protected ArrayWrapperImpl wrapArray(Array array) {
		return getConnectionWrapper().wrapArray(null, array);
	}

	/**
	 * Wraps a {@link Blob}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapBlob(java.sql.Blob)
	 */
	protected BlobWrapperImpl wrapBlob(Blob blob) {
		return getConnectionWrapper().wrapBlob(blob);
	}

	/**
	 * Wraps a {@link Clob}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapClob(java.sql.Clob)
	 */
	protected ClobWrapperImpl wrapClob(Clob clob) {
		return getConnectionWrapper().wrapClob(clob);
	}

	/**
	 * Wraps an {@link InputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapInputStream(java.io.InputStream)
	 */
	protected InputStreamWrapper wrapInputStream(InputStream in) {
		return getConnectionWrapper().wrapInputStream(in);
	}

	/**
	 * Wraps a {@link NClob}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapNClob(java.sql.NClob)
	 */
	protected NClobWrapperImpl wrapNClob(NClob nclob) {
		return getConnectionWrapper().wrapNClob(nclob);
	}

	/**
	 * Wraps a {@link Reader}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapReader(java.sql.Reader)
	 */
	protected ReaderWrapper wrapReader(Reader in) {
		return getConnectionWrapper().wrapReader(in);
	}

	/**
	 * Wraps a {@link Ref}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapRef(java.sql.Ref)
	 */
	protected RefWrapperImpl wrapRef(Ref ref) {
		return getConnectionWrapper().wrapRef(ref);
	}

	/**
	 * Wraps a {@link RowId}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapRowId(RowId)
	 */
	protected RowIdWrapperImpl wrapRowId(RowId rowId) {
		return getConnectionWrapper().wrapRowId(rowId);
	}

	/**
	 * Wraps a {@link SQLXML}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXMLWrapperImpl wrapSQLXML(SQLXML sqlXml) {
		return getConnectionWrapper().wrapSQLXML(sqlXml);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	@Override
	public ReaderWrapper readCharacterStream() throws SQLException {
		return wrapReader(getWrapped().readCharacterStream());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapInputStream(java.io.InputStream)
	 */
	@Override
	public InputStreamWrapper readAsciiStream() throws SQLException {
		return wrapInputStream(getWrapped().readAsciiStream());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapInputStream(java.io.InputStream)
	 */
	@Override
	public InputStreamWrapper readBinaryStream() throws SQLException {
		return wrapInputStream(getWrapped().readBinaryStream());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRef(java.sql.Ref)
	 */
	@Override
	public RefWrapperImpl readRef() throws SQLException {
		return wrapRef(getWrapped().readRef());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
	public BlobWrapperImpl readBlob() throws SQLException {
		return wrapBlob(getWrapped().readBlob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
	public ClobWrapperImpl readClob() throws SQLException {
		return wrapClob(getWrapped().readClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
	public ArrayWrapperImpl readArray() throws SQLException {
		return wrapArray(getWrapped().readArray());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	@Override
	public NClobWrapperImpl readNClob() throws SQLException {
		return wrapNClob(getWrapped().readNClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public SQLXMLWrapperImpl readSQLXML() throws SQLException {
		return wrapSQLXML(getWrapped().readSQLXML());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRowId(java.sql.RowId)
	 */
	@Override
	public RowIdWrapperImpl readRowId() throws SQLException {
		return wrapRowId(getWrapped().readRowId());
	}
}
