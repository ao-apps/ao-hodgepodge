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
import java.io.OutputStream;

/**
 * Wraps an {@link OutputStream}.
 *
 * @author  AO Industries, Inc.
 */
public class OutputStreamWrapper extends OutputStream implements IWrapper {

	private final ConnectionWrapper connectionWrapper;
	private final OutputStream wrapped;

	public OutputStreamWrapper(ConnectionWrapper connectionWrapper, OutputStream wrapped) {
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
	 * Gets the output stream that is wrapped.
	 */
	@Override
	public OutputStream getWrapped() {
		return wrapped;
	}

	@Override
	public void write(int b) throws IOException {
		getWrapped().write(b);
	}

	@Override
	public void write(byte b[]) throws IOException {
		getWrapped().write(b);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		getWrapped().write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		getWrapped().flush();
	}

	@Override
	public void close() throws IOException {
		getWrapped().close();
	}
}
