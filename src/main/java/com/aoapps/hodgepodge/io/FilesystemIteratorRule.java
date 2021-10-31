/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2019, 2021  AO Industries, Inc.
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

import java.io.IOException;

/**
 * Controls the behavior of a <code>FilesystemIterator</code>.  Each rule
 * has is mapped in via either a filename or a file prefix.  If a rule is provided
 * with an empty filename for regular rules (not prefix-matched), it will be the
 * default for all file system roots.
 *
 * @author  AO Industries, Inc.
 */
@FunctionalInterface
public interface FilesystemIteratorRule {

	/**
	 * Gets if this item should be included.
	 */
	boolean isIncluded(String filename) throws IOException;

	/**
	 * A rule that will always be backed-up.
	 */
	FilesystemIteratorRule OK = filename -> true;

	/**
	 * A rule that will not be backed-up.
	 */
	FilesystemIteratorRule SKIP = filename -> false;
}
