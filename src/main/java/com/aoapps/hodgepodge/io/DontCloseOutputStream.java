/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.io;

import com.aoapps.lang.io.NoClose;
import com.aoapps.lang.io.NoCloseOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps an OutputStream to protect it from close calls.  This is useful for at least
 * GZIPOutputStream where the native resources of the GZIPOutputStream need to be released
 * using the close call while the underlying stream is left intact.
 *
 * @deprecated  Please use {@link NoCloseOutputStream} instead
 *
 * @author  AO Industries, Inc.
 */
@Deprecated/* Java 9: (forRemoval = true) */
public class DontCloseOutputStream extends FilterOutputStream implements NoClose {

	public DontCloseOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	/**
	 * Does nothing on close to protect the wrapped OutputStream.
	 */
	@Override
	public void close() {
		// Do nothing
	}
}
