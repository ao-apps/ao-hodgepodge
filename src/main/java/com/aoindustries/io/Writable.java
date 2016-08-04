/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015  AO Industries, Inc.
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
import java.io.Writer;

/**
 * Something that may be written to a Writer output instead of being
 * converted toString and then having the String written.
 *
 * @author  AO Industries, Inc.
 */
public interface Writable  {

	/**
	 * Checks if the writable will be able to be converted toString in an extremely efficient manner.
	 * This means without allocating any new buffer space or string copies.
	 * Callers should prefer toString over writeTo when ifFastToString returns true.
	 * <p>
	 * Note: As of Java 1.7.0_06, String.substring() now copies underlying buffers.
	 * </p>
	 */
	boolean isFastToString();

	/**
	 * Anything writable must have a <code>toString</code> consistent with what would be
	 * written by the <code>writeTo</code> methods.  For larger amounts of data, it is
	 * likely much more efficient to call the most appropriate <code>writeTo</code> method.
	 */
	@Override
	String toString();

	/**
	 * Writes a streamed version of the object's String representation.
	 * What is written must be the same as if <code>out.write(this.toString())</code>
	 * were called, but may be a much more efficient implementation.
	 */
	void writeTo(Writer out) throws IOException;
	
	/**
	 * Writes a streamed version of the object's String representation.
	 * What is written must be the same as if <code>out.write(this.toString(), off, len)</code>
	 * were called, but may be a much more efficient implementation.
	 */
	void writeTo(Writer out, long off, long len) throws IOException;

	/**
	 * Writes a streamed version of the object's String representation using the given encoder.
	 * What is written must be the same as if <code>encoder.write(this.toString(), out)</code>
	 * were called, but may be a much more efficient implementation.
	 * 
	 * @param  encoder  if null, no encoding is performed and will be the same as a call to <code>writeTo(Writer)</code>
	 */
	void writeTo(Encoder encoder, Writer out) throws IOException;

	/**
	 * Writes a streamed version of the object's String representation using the given encoder.
	 * What is written must be the same as if <code>encoder.write(this.toString(), off, len, out)</code>
	 * were called, but may be a much more efficient implementation.
	 * 
	 * @param  encoder  if null, no encoding is performed and will be the same as a call to <code>writeTo(Writer,off,len)</code>
	 */
	void writeTo(Encoder encoder, Writer out, long off, long len) throws IOException;
}
