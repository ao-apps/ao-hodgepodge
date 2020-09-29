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
import com.aoindustries.sql.wrapper.ReaderWrapper;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * @see  FailFastConnectionImpl
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastReader extends ReaderWrapper {

	public FailFastReader(FailFastConnectionImpl failFastConnection, Reader wrapped) {
		super(failFastConnection, wrapped);
	}

	@Override
	protected FailFastConnectionImpl getConnectionWrapper() {
		return (FailFastConnectionImpl)super.getConnectionWrapper();
	}

	@Override
	public int read(CharBuffer target) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.read(target);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public int read() throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.read();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public int read(char cbuf[]) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.read(cbuf);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public int read(char cbuf[], int off, int len) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.read(cbuf, off, len);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public long skip(long n) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.skip(n);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public boolean ready() throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			return super.ready();
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public boolean markSupported() {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
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

	@Override
	public void mark(int readAheadLimit) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			super.mark(readAheadLimit);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public void reset() throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			super.reset();
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

	// Java 10: public long transferTo(Writer out) throws IOException;
}
