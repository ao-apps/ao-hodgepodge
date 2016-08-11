/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013, 2016  AO Industries, Inc.
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
package com.aoindustries.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Wraps a PrintStream to be an OutputStream.  Although PrintStream subclasses
 * OutputStream, it handles errors differently.  This calls checkError after
 * every write to properly detect and throw exceptions as expected for
 * an OutputStream.  Because checkError is documented as flushing the stream
 * this may have an adverse affect on performance.
 *
 * @author  AO Industries, Inc.
 */
public class PrintStreamOutputStream extends OutputStream {

	final private PrintStream out;

	public PrintStreamOutputStream(PrintStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
		if(out.checkError()) throw new IOException("Error in print stream");
	}

	@Override
	public void write(byte b[]) throws IOException {
		out.write(b);
		if(out.checkError()) throw new IOException("Error in print stream");
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		out.write(b, off, len);
		if(out.checkError()) throw new IOException("Error in print stream");
	}

	@Override
	public void flush() throws IOException {
		out.flush();
		if(out.checkError()) throw new IOException("Error in print stream");
	}

	@Override
	public void close() throws IOException {
		out.close();
		if(out.checkError()) throw new IOException("Error in print stream");
	}
}
