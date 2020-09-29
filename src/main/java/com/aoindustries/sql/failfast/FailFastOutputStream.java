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

import com.aoindustries.lang.Throwables;
import com.aoindustries.sql.wrapper.OutputStreamWrapper;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @see  FailFastConnectionImpl
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
public class FailFastOutputStream extends OutputStreamWrapper {

	public FailFastOutputStream(FailFastConnectionImpl failFastConnection, OutputStream wrapped) {
		super(failFastConnection, wrapped);
	}

	@Override
	protected FailFastConnectionImpl getConnectionWrapper() {
		return (FailFastConnectionImpl)super.getConnectionWrapper();
	}

	@Override
	public void write(int b) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			super.write(b);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public void write(byte b[]) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			super.write(b);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			super.write(b, off, len);
		} catch(Throwable t) {
			ffConn.addFailFastCause(t);
			throw Throwables.wrap(t, IOException.class, IOException::new);
		}
	}

	@Override
	public void flush() throws IOException {
		FailFastConnectionImpl ffConn = getConnectionWrapper();
		ffConn.failFastIOException();
		try {
			super.flush();
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
}
