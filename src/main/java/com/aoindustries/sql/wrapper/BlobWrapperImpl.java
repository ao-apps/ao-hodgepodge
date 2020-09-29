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
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Wraps a {@link Blob}.
 *
 * @author  AO Industries, Inc.
 */
public class BlobWrapperImpl implements IBlobWrapper {

	private final ConnectionWrapperImpl connectionWrapper;
	private final Blob wrapped;

	public BlobWrapperImpl(ConnectionWrapperImpl connectionWrapper, Blob wrapped) {
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
	public Blob getWrapped() {
		return wrapped;
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
	 * Wraps an {@link InputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapInputStream(java.io.InputStream)
	 */
	protected InputStreamWrapper wrapInputStream(InputStream in) {
		return getConnectionWrapper().wrapInputStream(in);
	}

	/**
	 * Wraps an {@link OutputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapOutputStream(java.io.OutputStream)
	 */
	protected OutputStreamWrapper wrapOutputStream(OutputStream out) {
		return getConnectionWrapper().wrapOutputStream(out);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapInputStream(java.io.InputStream)
	 */
	@Override
	public InputStreamWrapper getBinaryStream() throws SQLException {
		return wrapInputStream(getWrapped().getBinaryStream());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapBlob(java.sql.Blob)
	 */
	@Override
	public long position(Blob pattern, long start) throws SQLException {
		return getWrapped().position(unwrapBlob(pattern), start);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapOutputStream(java.io.OutputStream)
	 */
	@Override
	public OutputStreamWrapper setBinaryStream(long pos) throws SQLException {
		return wrapOutputStream(getWrapped().setBinaryStream(pos));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapInputStream(java.io.InputStream)
	 */
	@Override
	public InputStreamWrapper getBinaryStream(long pos, long length) throws SQLException {
		return wrapInputStream(getWrapped().getBinaryStream(pos, length));
	}
}
