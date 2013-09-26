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
package com.aoindustries.io.buffer;

import com.aoindustries.encoding.MediaEncoder;
import java.io.IOException;
import java.io.Writer;

/**
 * The result from completion of a buffered writer.  Only available after a
 * buffered writer has been closed.
 * 
 * @see  AoBufferedWriter
 *
 * @author  AO Industries, Inc.
 */
public interface BufferResult {

    /**
     * Gets the number of characters in this view of the buffer.
     */
    long getLength() throws IOException;

    /**
     * Gets the captured data as a string.  For larger amounts of data, it is
	 * much more efficient to call the <code>writeTo</code> method.
     *
	 * TODO: Add contentEquals(String) method to avoid some uses of toString
	 *
     * @see  #writeTo(java.io.Writer)
	 * @see  #trim()
     */
    @Override
    String toString();

	/**
	 * Writes the captured body to the provided writer with the given encoding.
	 * 
	 * @param  encoder  if null, no encoding is performed - write through
	 */
    void writeTo(MediaEncoder encoder, Writer out) throws IOException;

	/**
     * Writes the captured body to the provided writer.
     */
    void writeTo(Writer out) throws IOException;

	/**
	 * Trims the contents of this result, returning the instance that represents this result trimmed.
	 */
	BufferResult trim() throws IOException;
}
