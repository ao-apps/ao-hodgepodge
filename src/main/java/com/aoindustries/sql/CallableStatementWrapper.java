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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Wraps a {@link CallableStatement}.
 *
 * @author  AO Industries, Inc.
 */
public class CallableStatementWrapper extends PreparedStatementWrapper implements ICallableStatementWrapper {

	public CallableStatementWrapper(ConnectionWrapper connectionWrapper, CallableStatement wrapped) {
		super(connectionWrapper, wrapped);
	}

	@Override
	public CallableStatement getWrapped() {
		return (CallableStatement)super.getWrapped();
	}

	/**
	 * Wraps an {@link Array}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapArray(com.aoindustries.sql.StatementWrapper, java.sql.Array)
	 */
	protected ArrayWrapper wrapArray(Array array) {
		return getConnectionWrapper().wrapArray(this, array);
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
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
    public BlobWrapper getBlob(int parameterIndex) throws SQLException {
		return wrapBlob(getWrapped().getBlob(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
    public ClobWrapper getClob(int parameterIndex) throws SQLException {
		return wrapClob(getWrapped().getClob(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
    public ArrayWrapper getArray(int parameterIndex) throws SQLException {
		return wrapArray(getWrapped().getArray(parameterIndex));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapBlob(java.sql.Blob)
	 */
	@Override
    public BlobWrapper getBlob(String parameterName) throws SQLException {
		return wrapBlob(getWrapped().getBlob(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapClob(java.sql.Clob)
	 */
	@Override
    public ClobWrapper getClob(String parameterName) throws SQLException {
		return wrapClob(getWrapped().getClob(parameterName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapArray(java.sql.Array)
	 */
	@Override
    public ArrayWrapper getArray(String parameterName) throws SQLException {
		return wrapArray(getWrapped().getArray(parameterName));
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
}
