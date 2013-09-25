/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaEncoder;
import java.io.IOException;
import java.io.Writer;

/**
 * A buffered writer that may be trimmed, converted to String, and written to another
 * writer.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AoBufferedWriter extends Writer {

	protected AoBufferedWriter() {
    }

    /**
     * Gets the number of characters in this writer.
	 * If this is a substring of the writer, returns the number of characters in the substring.
	 * Once closed, this length will not be modified.
     */
    abstract public long getLength();

    /**
     * Gets the captured data as a string.  For larger amounts of data, it is
	 * much more efficient to call the <code>writeTo</code> method.
     *
	 * Calling toString before closed will return a short message (like type and length).
	 * Once closed, toString will return all the buffered data.
	 *
     * @see  #writeTo(java.io.Writer)
	 * @see  #trim()
     */
    @Override
    abstract public String toString();

	/**
	 * Writes the captured body to the provided writer with the given encoding.
	 * 
	 * @param  encoder  if null, no encoding is performed - write through
	 * 
	 * @exception  IllegalStateException if not closed
	 */
    abstract public void writeTo(MediaEncoder encoder, Writer out) throws IllegalStateException, IOException;

	/**
     * Writes the captured body to the provided writer.
	 * 
	 * @exception  IllegalStateException if not closed
     */
    abstract public void writeTo(Writer out) throws IllegalStateException, IOException;

	/**
	 * Trims the contents of this writer, returning the instance that represents this writer trimmed.
	 *
	 * The buffer must be closed.
	 * 
	 * @exception  IllegalStateException  if not closed
	 */
	abstract public AoBufferedWriter trim() throws IllegalStateException, IOException;
}
