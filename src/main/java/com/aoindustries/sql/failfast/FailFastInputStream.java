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
package com.aoindustries.sql.failfast;

import com.aoindustries.exception.WrappedException;
import com.aoindustries.lang.Throwables;
import com.aoindustries.sql.wrapper.InputStreamWrapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * @see  FailFastConnection
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastInputStream extends InputStreamWrapper {

	public FailFastInputStream(FailFastConnection failFastConnection, InputStream wrapped) {
		super(failFastConnection, wrapped);
	}

	@Override
	protected FailFastConnection getConnectionWrapper() {
		return (FailFastConnection)super.getConnectionWrapper();
	}

	@Override
	public int read() throws IOException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.read();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public int read(byte b[]) throws IOException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.read(b);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.read(b, off, len);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	// Java 9: byte[] readAllBytes() throws IOException;
	// Java 11: byte[] readNBytes(int len) throws IOException;
	// Java 9: int readNBytes(byte[] b, int off, int len) throws IOException;

	@Override
	public long skip(long n) throws IOException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.skip(n);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public int available() throws IOException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.available();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} catch(Throwable t) {
			getConnectionWrapper().addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public void mark(int readlimit) {
		FailFastConnection ffConn = getConnectionWrapper();
		if(ffConn.getFailFastCause() == null) {
			try {
				super.mark(readlimit);
			} catch(Throwable t) {
				ffConn.addFailFastCause(t);
				throw Throwables.wrap(t, WrappedException.class, WrappedException::new);
			}
		}
	}

	@Override
	public void reset() throws IOException {
		FailFastConnection ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			super.reset();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public boolean markSupported() {
		FailFastConnection ffConn = getConnectionWrapper();
		if(ffConn.getFailFastCause() == null) {
			try {
				return super.markSupported();
			} catch(Throwable t) {
				ffConn.addFailFastCause(t);
				throw Throwables.wrap(t, WrappedException.class, WrappedException::new);
			}
		} else {
			return false;
		}
	}

	// Java 9: long transferTo(OutputStream out) throws IOException;
}
