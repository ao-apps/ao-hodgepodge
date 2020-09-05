/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2020  AO Industries, Inc.
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

import java.io.Writer;

/**
 * Converts native line endings to Unix format ('\n') while writing.
 *
 * @author  AO Industries, Inc.
 */
final public class NativeToUnixWriter {

	/**
	 * Make no instances.
	 */
	private NativeToUnixWriter() {
	}

	/**
	 * The end of line character for Unix.
	 */
	static final String UNIX_EOL = "\n";

	/**
	 * The end of line character for the current Java virtual machine.
	 */
	private static final String EOL = System.lineSeparator();

	/**
	 * Gets an instance of the Writer that performs the conversion.
	 * The implementation may be optimized for common platforms.
	 */
	public static Writer getInstance(Writer out) {
		// Already in Unix format, no conversion necessary
		if(UNIX_EOL.equals(EOL)) return out;
		// Use FindReplaceWriter
		return new FindReplaceWriter(out, EOL, UNIX_EOL);
	}
}
