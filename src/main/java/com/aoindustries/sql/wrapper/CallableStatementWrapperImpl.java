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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

/**
 * Wraps a {@link CallableStatement}.
 *
 * @author  AO Industries, Inc.
 */
public class CallableStatementWrapperImpl extends PreparedStatementWrapperImpl implements ICallableStatementWrapper {

	public CallableStatementWrapperImpl(ConnectionWrapperImpl connectionWrapper, CallableStatement wrapped) {
		super(connectionWrapper, wrapped);
	}

	@Override
	public CallableStatement getWrapped() {
		return (CallableStatement)super.getWrapped();
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapArray(com.aoindustries.sql.StatementWrapperImpl, java.sql.Array)
	 */
	protected ArrayWrapperImpl wrapArray(Array array) {
		return getConnectionWrapper().wrapArray(this, array);
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
	 * @see  #wrapRef(java.sql.Ref)
	 */
	@Override
	public RefWrapperImpl getRef(int parameterIndex) throws SQLException {
		return wrapRef(getWrapped().getRef(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
	public BlobWrapperImpl getBlob(int parameterIndex) throws SQLException {
		return wrapBlob(getWrapped().getBlob(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
	public ClobWrapperImpl getClob(int parameterIndex) throws SQLException {
		return wrapClob(getWrapped().getClob(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
	public ArrayWrapperImpl getArray(int parameterIndex) throws SQLException {
		return wrapArray(getWrapped().getArray(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		getWrapped().setAsciiStream(parameterName, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		getWrapped().setBinaryStream(parameterName, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		getWrapped().setCharacterStream(parameterName, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRef(java.sql.Ref)
	 */
	@Override
	public RefWrapperImpl getRef(String parameterName) throws SQLException {
		return wrapRef(getWrapped().getRef(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
	public BlobWrapperImpl getBlob(String parameterName) throws SQLException {
		return wrapBlob(getWrapped().getBlob(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
	public ClobWrapperImpl getClob(String parameterName) throws SQLException {
		return wrapClob(getWrapped().getClob(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
	public ArrayWrapperImpl getArray(String parameterName) throws SQLException {
		return wrapArray(getWrapped().getArray(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRowId(java.sql.RowId)
	 */
	@Override
	public RowIdWrapperImpl getRowId(int parameterIndex) throws SQLException {
		return wrapRowId(getWrapped().getRowId(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapRowId(java.sql.RowId)
	 */
	@Override
	public RowIdWrapperImpl getRowId(String parameterName) throws SQLException {
		return wrapRowId(getWrapped().getRowId(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapRowId(java.sql.RowId) 
	 */
	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		getWrapped().setRowId(parameterName, unwrapRowId(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		getWrapped().setNCharacterStream(parameterName, unwrapReader(value), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapNClob(java.sql.NClob) 
	 */
	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		getWrapped().setNClob(parameterName, unwrapNClob(value));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrapped().setClob(parameterName, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		getWrapped().setBlob(parameterName, unwrapInputStream(inputStream), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		getWrapped().setNClob(parameterName, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	@Override
	public NClobWrapperImpl getNClob(int parameterIndex) throws SQLException {
		return wrapNClob(getWrapped().getNClob(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapNClob(java.sql.NClob)
	 */
	@Override
	public NClobWrapperImpl getNClob(String parameterName) throws SQLException {
		return wrapNClob(getWrapped().getNClob(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapSQLXML(java.sql.SQLXML) 
	 */
	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		getWrapped().setSQLXML(parameterName, unwrapSQLXML(xmlObject));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public SQLXMLWrapperImpl getSQLXML(int parameterIndex) throws SQLException {
		return wrapSQLXML(getWrapped().getSQLXML(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public SQLXMLWrapperImpl getSQLXML(String parameterName) throws SQLException {
		return wrapSQLXML(getWrapped().getSQLXML(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	@Override
	public ReaderWrapper getNCharacterStream(int parameterIndex) throws SQLException {
		return wrapReader(getWrapped().getNCharacterStream(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	@Override
	public ReaderWrapper getNCharacterStream(String parameterName) throws SQLException {
		return wrapReader(getWrapped().getNCharacterStream(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	@Override
	public ReaderWrapper getCharacterStream(int parameterIndex) throws SQLException {
		return wrapReader(getWrapped().getCharacterStream(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	@Override
	public ReaderWrapper getCharacterStream(String parameterName) throws SQLException {
		return wrapReader(getWrapped().getCharacterStream(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapBlob(java.sql.Blob)
	 */
	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		getWrapped().setBlob(parameterName, unwrapBlob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapClob(java.sql.Clob)
	 */
	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		getWrapped().setClob(parameterName, unwrapClob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		getWrapped().setAsciiStream(parameterName, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		getWrapped().setBinaryStream(parameterName, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		getWrapped().setCharacterStream(parameterName, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		getWrapped().setAsciiStream(parameterName, unwrapInputStream(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		getWrapped().setBinaryStream(parameterName, unwrapInputStream(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		getWrapped().setCharacterStream(parameterName, unwrapReader(reader));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		getWrapped().setNCharacterStream(parameterName, unwrapReader(value));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		getWrapped().setClob(parameterName, unwrapReader(reader));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		getWrapped().setBlob(parameterName, unwrapInputStream(inputStream));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		getWrapped().setNClob(parameterName, unwrapReader(reader));
	}
}
