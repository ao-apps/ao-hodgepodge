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
import java.sql.SQLException;
import java.sql.SQLXML;

/**
 * Wraps a {@link SQLXML}.
 *
 * @author  AO Industries, Inc.
 */
public class SQLXMLWrapper implements ISQLXMLWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final SQLXML wrapped;

	public SQLXMLWrapper(ConnectionWrapper connectionWrapper, SQLXML wrapped) {
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
	public SQLXML getWrapped() {
		return wrapped;
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
	 * Wraps an {@link OutputStream}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapOutputStream(java.io.OutputStream)
	 */
	protected OutputStreamWrapper wrapOutputStream(OutputStream out) {
		return getConnectionWrapper().wrapOutputStream(out);
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
	 * Wraps a {@link Writer}, if not already wrapped by this wrapper.
	 *
	 * @see  ConnectionWrapper#wrapWriter(java.io.Writer)
	 */
	protected WriterWrapper wrapWriter(Writer out) {
		return getConnectionWrapper().wrapWriter(out);
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
	 * @see  #wrapOutputStream(java.io.OutputStream)
	 */
	@Override
	public OutputStreamWrapper setBinaryStream() throws SQLException {
		return wrapOutputStream(getWrapped().setBinaryStream());
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
	 * @see  #wrapWriter(Writer)
	 */
	@Override
	public WriterWrapper setCharacterStream() throws SQLException {
		return wrapWriter(getWrapped().setCharacterStream());
	}

}
