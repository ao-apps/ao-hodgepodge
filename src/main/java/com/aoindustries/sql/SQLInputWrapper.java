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
public class SQLInputWrapper implements ISQLInputWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final SQLInput wrapped;

	public SQLInputWrapper(ConnectionWrapper connectionWrapper, SQLInput wrapped) {
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
	public SQLInput getWrapped() {
		return wrapped;
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapArray(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		return getConnectionWrapper().wrapArray(null, array);
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
	 * Wraps a {@link Clob}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapClob(java.sql.Clob)
	 */
	protected ClobWrapper wrapClob(Clob clob) {
		return getConnectionWrapper().wrapClob(clob);
	}

	/**
	 * Wraps an {@link InputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapInputStream(java.io.InputStream)
	 */
	protected InputStreamWrapper wrapInputStream(InputStream in) {
		return getConnectionWrapper().wrapInputStream(in);
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
	 * Wraps a {@link Reader}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapReader(java.sql.Reader)
	 */
	protected ReaderWrapper wrapReader(Reader in) {
		return getConnectionWrapper().wrapReader(in);
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
	 * Wraps a {@link RowId}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapRowId(RowId)
	 */
	protected RowIdWrapper wrapRowId(RowId rowId) {
		return getConnectionWrapper().wrapRowId(rowId);
	}

	/**
	 * Wraps a {@link SQLXML}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXMLWrapper wrapSQLXML(SQLXML sqlXml) {
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
	public RefWrapper readRef() throws SQLException {
		return wrapRef(getWrapped().readRef());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
	public BlobWrapper readBlob() throws SQLException {
		return wrapBlob(getWrapped().readBlob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
	public ClobWrapper readClob() throws SQLException {
		return wrapClob(getWrapped().readClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
	public ArrayWrapper readArray() throws SQLException {
		return wrapArray(getWrapped().readArray());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	@Override
	public NClobWrapper readNClob() throws SQLException {
		return wrapNClob(getWrapped().readNClob());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public SQLXMLWrapper readSQLXML() throws SQLException {
		return wrapSQLXML(getWrapped().readSQLXML());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRowId(java.sql.RowId)
	 */
	@Override
	public RowIdWrapper readRowId() throws SQLException {
		return wrapRowId(getWrapped().readRowId());
	}
}
