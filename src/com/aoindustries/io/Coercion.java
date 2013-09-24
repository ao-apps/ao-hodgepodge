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
 * Coerces objects to String compatible with JSTL (JSP EL).
 *
 * @author  AO Industries, Inc.
 */
public final class Coercion  {

	public static String toString(Object value) {
		// If A is a string, then the result is A.
		if(value instanceof String) return (String)value;
		// Otherwise, if A is null, then the result is "".
		if(value == null) return "";
		// Otherwise, if A.toString() throws an exception, then raise an error
		String str = value.toString();
		// Otherwise, the result is A.toString();
		return str;
	}

	/**
	 * Coerces an object to a String representation, supporting streaming for specialized types.
	 */
	public static void toString(Object value, Writer out) throws IOException {
		if(value instanceof String) {
			// If A is a string, then the result is A.
			out.write((String)value);
		} else if(value == null) {
			// Otherwise, if A is null, then the result is "".
			// Write nothing
		} else if(value instanceof AutoTempFileWriter) {
			// Avoid intermediate String from AutoTempFileWriter
			((AutoTempFileWriter)value).writeTo(out);
		} else {
			// Otherwise, if A.toString() throws an exception, then raise an error
			String str = value.toString();
			// Otherwise, the result is A.toString();
			out.write(str);
		}
	}

	/**
	 * Coerces an object to a String representation, supporting streaming for specialized types.
	 * 
	 * @param  encoder  if null, no encoding is performed - write through
	 */
	public static void toString(Object value, MediaEncoder encoder, Writer out) throws IOException {
		if(encoder==null) {
			toString(value, out);
		} else {
			if(value instanceof String) {
				// If A is a string, then the result is A.
				encoder.write((String)value, out);
			} else if(value == null) {
				// Otherwise, if A is null, then the result is "".
				// Write nothing
			} else if(value instanceof AutoTempFileWriter) {
				// Avoid intermediate String from AutoTempFileWriter
				((AutoTempFileWriter)value).writeTo(encoder, out);
			} else {
				// Otherwise, if A.toString() throws an exception, then raise an error
				String str = value.toString();
				// Otherwise, the result is A.toString();
				encoder.write(str, out);
			}
		}
	}

	/**
     * Make no instances.
     */
    private Coercion() {
    }
}
