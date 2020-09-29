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
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Wraps a {@link Clob}.
 *
 * @author  AO Industries, Inc.
 */
public class ClobWrapperImpl implements IClobWrapper {

	private final ConnectionWrapperImpl connectionWrapper;
	private final Clob wrapped;

	public ClobWrapperImpl(ConnectionWrapperImpl connectionWrapper, Clob wrapped) {
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
	public Clob getWrapped() {
		return wrapped;
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
	 * Wraps a {@link Reader}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapReader(java.sql.Reader)
	 */
	protected ReaderWrapper wrapReader(Reader in) {
		return getConnectionWrapper().wrapReader(in);
	}

	/**
	 * Wraps a {@link Writer}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapperImpl#wrapWriter(java.io.Writer)
	 */
	protected WriterWrapper wrapWriter(Writer out) {
		return getConnectionWrapper().wrapWriter(out);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	@Override
	public ReaderWrapper getCharacterStream() throws SQLException {
		return wrapReader(getWrapped().getCharacterStream());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapInputStream(java.io.InputStream)
	 */
	@Override
	public InputStreamWrapper getAsciiStream() throws SQLException {
		return wrapInputStream(getWrapped().getAsciiStream());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #unwrapClob(java.sql.Clob)
	 */
	@Override
	public long position(Clob pattern, long start) throws SQLException {
		return getWrapped().position(unwrapClob(pattern), start);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapOutputStream(java.io.OutputStream)
	 */
	@Override
	public OutputStreamWrapper setAsciiStream(long pos) throws SQLException {
		return wrapOutputStream(getWrapped().setAsciiStream(pos));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapWriter(Writer)
	 */
	@Override
	public WriterWrapper setCharacterStream(long pos) throws SQLException {
		return wrapWriter(getWrapped().setCharacterStream(pos));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  #wrapReader(java.io.Reader)
	 */
	@Override
	public ReaderWrapper getCharacterStream(long pos, long length) throws SQLException {
		return wrapReader(getWrapped().getCharacterStream(pos, length));
	}
}
