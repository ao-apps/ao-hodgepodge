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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

/**
 * Wraps a {@link PreparedStatement}.
 *
 * @author  AO Industries, Inc.
 */
public class PreparedStatementWrapper extends StatementWrapper implements IPreparedStatementWrapper {

	public PreparedStatementWrapper(ConnectionWrapper connectionWrapper, PreparedStatement wrapped) {
		super(connectionWrapper, wrapped);
	}

	@Override
	public PreparedStatement getWrapped() {
		return (PreparedStatement)super.getWrapped();
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
	 * Unwraps a {@link Blob}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapBlob(java.sql.Blob)
	 */
	protected Blob unwrapBlob(Blob blob) {
		return getConnectionWrapper().unwrapBlob(blob);
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
	 * Unwraps an {@link InputStream}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapInputStream(java.io.InputStream)
	 */
	protected InputStream unwrapInputStream(InputStream in) {
		return getConnectionWrapper().unwrapInputStream(in);
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
	 * Wraps a {@link ParameterMetaData}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapParameterMetaData(java.sql.ParameterMetaData)
	 */
	protected ParameterMetaDataWrapper wrapParameterMetaData(ParameterMetaData metaData) {
		return getConnectionWrapper().wrapParameterMetaData(metaData);
	}

	/**
	 * Unwraps a {@link Reader}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapReader(java.io.Reader)
	 */
	protected Reader unwrapReader(Reader in) {
		return getConnectionWrapper().unwrapReader(in);
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
	 * Unwraps a {@link RowId}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapRowId(java.sql.RowId)
	 */
	protected RowId unwrapRowId(RowId rowId) {
		return getConnectionWrapper().unwrapRowId(rowId);
	}

	/**
	 * Unwraps a {@link SQLXML}, if wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#unwrapSQLXML(java.sql.SQLXML)
	 */
	protected SQLXML unwrapSQLXML(SQLXML sqlXml) {
		return getConnectionWrapper().unwrapSQLXML(sqlXml);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapResultSet(java.sql.ResultSet)
	 */
	@Override
	public ResultSetWrapper executeQuery() throws SQLException {
		return wrapResultSet(getWrapped().executeQuery());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		getWrapped().setAsciiStream(parameterIndex, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	@Deprecated // Java 9: (since="1.2")
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		getWrapped().setUnicodeStream(parameterIndex, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		getWrapped().setBinaryStream(parameterIndex, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		getWrapped().setCharacterStream(parameterIndex, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapRef(java.sql.Ref)
	 */
	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		getWrapped().setRef(parameterIndex, unwrapRef(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapBlob(java.sql.Blob)
	 */
	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		getWrapped().setBlob(parameterIndex, unwrapBlob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapClob(java.sql.Clob)
	 */
	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		getWrapped().setClob(parameterIndex, unwrapClob(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapArray(java.sql.Array)
	 */
	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		getWrapped().setArray(parameterIndex, unwrapArray(x));
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
	 * @see  #wrapParameterMetaData(java.sql.ParameterMetaData)
	 */
	@Override
	public ParameterMetaDataWrapper getParameterMetaData() throws SQLException {
		return wrapParameterMetaData(getWrapped().getParameterMetaData());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapRowId(java.sql.RowId)
	 */
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		getWrapped().setRowId(parameterIndex, unwrapRowId(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		getWrapped().setNCharacterStream(parameterIndex, unwrapReader(value), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapNClob(java.sql.NClob)
	 */
	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		getWrapped().setNClob(parameterIndex, unwrapNClob(value));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		getWrapped().setClob(parameterIndex, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		getWrapped().setBlob(parameterIndex, unwrapInputStream(inputStream), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		getWrapped().setNClob(parameterIndex, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapSQLXML(java.sql.SQLXML)
	 */
	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		getWrapped().setSQLXML(parameterIndex, unwrapSQLXML(xmlObject));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		getWrapped().setAsciiStream(parameterIndex, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		getWrapped().setBinaryStream(parameterIndex, unwrapInputStream(x), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		getWrapped().setCharacterStream(parameterIndex, unwrapReader(reader), length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		getWrapped().setAsciiStream(parameterIndex, unwrapInputStream(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		getWrapped().setBinaryStream(parameterIndex, unwrapInputStream(x));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		getWrapped().setCharacterStream(parameterIndex, unwrapReader(reader));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		getWrapped().setNCharacterStream(parameterIndex, unwrapReader(value));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		getWrapped().setClob(parameterIndex, unwrapReader(reader));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapInputStream(java.io.InputStream)
	 */
	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		getWrapped().setBlob(parameterIndex, unwrapInputStream(inputStream));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapReader(java.io.Reader)
	 */
	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		getWrapped().setNClob(parameterIndex, unwrapReader(reader));
	}
}
