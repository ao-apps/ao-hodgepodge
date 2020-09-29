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
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.SQLXML;
import java.sql.Struct;

/**
 * Wraps a {@link SQLOutput}.
 *
 * @author  AO Industries, Inc.
 */
public class SQLOutputWrapperImpl implements ISQLOutputWrapper {

	private final ConnectionWrapperImpl connectionWrapper;
	private final SQLOutput wrapped;

	public SQLOutputWrapperImpl(ConnectionWrapperImpl connectionWrapper, SQLOutput wrapped) {
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
	public SQLOutput getWrapped() {
		return wrapped;
	}

	/**
	 * Unwraps an {@link Array}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapArray(java.sql.Array)
	 */
	protected Array unwrapArray(Array array) {
		return getConnectionWrapper().unwrapArray(array);
	}

	/**
	 * Unwraps a {@link Blob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapBlob(java.sql.Blob)
	 */
	protected Blob unwrapBlob(Blob blob) {
		return getConnectionWrapper().unwrapBlob(blob);
	}

	/**
	 * Unwraps a {@link Clob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapClob(java.sql.Clob)
	 */
	protected Clob unwrapClob(Clob clob) {
		return getConnectionWrapper().unwrapClob(clob);
	}

	/**
	 * Unwraps an {@link InputStream}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapInputStream(java.io.InputStream)
	 */
	protected InputStream unwrapInputStream(InputStream in) {
		return getConnectionWrapper().unwrapInputStream(in);
	}

	/**
	 * Unwraps a {@link NClob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapNClob(java.sql.NClob)
	 */
	protected NClob unwrapNClob(NClob nclob) {
		return getConnectionWrapper().unwrapNClob(nclob);
	}

	/**
	 * Unwraps a {@link Reader}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapReader(java.io.Reader)
	 */
	protected Reader unwrapReader(Reader in) {
		return getConnectionWrapper().unwrapReader(in);
	}

	/**
	 * Unwraps a {@link Ref}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapRef(java.sql.Ref)
	 */
	protected Ref unwrapRef(Ref ref) {
		return getConnectionWrapper().unwrapRef(ref);
	}

	/**
	 * Unwraps a {@link RowId}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapRowId(java.sql.RowId)
	 */
	protected RowId unwrapRowId(RowId rowId) {
		return getConnectionWrapper().unwrapRowId(rowId);
	}

	/**
	 * Wraps a {@link SQLData}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapSQLData(java.sql.SQLData)
	 */
	protected SQLDataWrapperImpl wrapSQLData(SQLData sqlData) {
		return getConnectionWrapper().wrapSQLData(sqlData);
	}

	/**
	 * Unwraps a {@link SQLXML}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXML unwrapSQLXML(SQLXML sqlXml) {
		return getConnectionWrapper().unwrapSQLXML(sqlXml);
	}

	/**
	 * Unwraps a {@link Struct}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#unwrapStruct(java.sql.Struct)
	 */
	protected Struct unwrapStruct(Struct struct) {
		return getConnectionWrapper().unwrapStruct(struct);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void writeCharacterStream(Reader x) throws SQLException {
		getWrapped().writeCharacterStream(unwrapReader(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void writeAsciiStream(InputStream x) throws SQLException {
		getWrapped().writeAsciiStream(unwrapInputStream(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void writeBinaryStream(InputStream x) throws SQLException {
		getWrapped().writeBinaryStream(unwrapInputStream(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSQLData(java.sql.SQLData)
	 */
	@Override
	public void writeObject(SQLData x) throws SQLException {
		try (SQLDataWrapperImpl xWrapper = wrapSQLData(x)) {
			getWrapped().writeObject(xWrapper);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapRef(java.sql.Ref)
	 */
	@Override
	public void writeRef(Ref x) throws SQLException {
		getWrapped().writeRef(unwrapRef(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapBlob(java.sql.Blob)
	 */
	@Override
	public void writeBlob(Blob x) throws SQLException {
		getWrapped().writeBlob(unwrapBlob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapClob(java.sql.Clob)
	 */
	@Override
	public void writeClob(Clob x) throws SQLException {
		getWrapped().writeClob(unwrapClob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapStruct(java.sql.Struct)
	 */
	@Override
	public void writeStruct(Struct x) throws SQLException {
		getWrapped().writeStruct(unwrapStruct(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapArray(java.sql.Array)
	 */
	@Override
	public void writeArray(Array x) throws SQLException {
		getWrapped().writeArray(unwrapArray(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapNClob(java.sql.NClob)
	 */
	@Override
	public void writeNClob(NClob x) throws SQLException {
		getWrapped().writeNClob(unwrapNClob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapRowId(java.sql.RowId)
	 */
	@Override
	public void writeRowId(RowId x) throws SQLException {
		getWrapped().writeRowId(unwrapRowId(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public void writeSQLXML(SQLXML x) throws SQLException {
		getWrapped().writeSQLXML(unwrapSQLXML(x));
	}
}
