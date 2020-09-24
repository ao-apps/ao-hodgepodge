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

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an {@link InputStream}.
 *
 * @author  AO Industries, Inc.
 */
public class InputStreamWrapper extends InputStream implements IWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final InputStream wrapped;

	public InputStreamWrapper(ConnectionWrapper connectionWrapper, InputStream wrapped) {
		this.connectionWrapper = connectionWrapper;
		this.wrapped = wrapped;
	}

	/**
	 * Gets the connection wrapper.
	 */
	protected ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}

	/**
	 * Gets the input stream that is wrapped.
	 */
	@Override
	public InputStream getWrapped() {
		return wrapped;
	}

	@Override
	public int read() throws IOException {
		return getWrapped().read();
	}

	@Override
	public int read(byte b[]) throws IOException {
		return getWrapped().read(b);
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		return getWrapped().read(b, off, len);
	}

	// Java 9: byte[] readAllBytes() throws IOException;
	// Java 11: byte[] readNBytes(int len) throws IOException;
	// Java 9: int readNBytes(byte[] b, int off, int len) throws IOException;

	@Override
	public long skip(long n) throws IOException {
		return getWrapped().skip(n);
	}

	@Override
	public int available() throws IOException {
		return getWrapped().available();
	}

	@Override
	public void close() throws IOException {
		getWrapped().close();
	}

	@Override
	public void mark(int readlimit) {
		getWrapped().mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		getWrapped().reset();
	}

	@Override
	public boolean markSupported() {
		return getWrapped().markSupported();
	}

	// Java 9: long transferTo(OutputStream out) throws IOException;
}
