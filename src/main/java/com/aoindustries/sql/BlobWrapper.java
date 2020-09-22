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
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Wraps a {@link Blob}.
 *
 * @author  AO Industries, Inc.
 */
public class BlobWrapper implements IBlobWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final Blob wrapped;

	public BlobWrapper(ConnectionWrapper connectionWrapper, Blob wrapped) {
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
	public Blob getWrapped() {
		return wrapped;
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
	 * Wraps an {@link InputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapInputStream(java.io.InputStream)
	 */
	protected InputStreamWrapper wrapInputStream(InputStream in) {
		return getConnectionWrapper().wrapInputStream(in);
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
	 * @see  #wrapInputStream(java.io.InputStream)
	 */
	@Override
	public InputStreamWrapper getBinaryStream(long pos, long length) throws SQLException {
		return wrapInputStream(getWrapped().getBinaryStream(pos, length));
	}
}
