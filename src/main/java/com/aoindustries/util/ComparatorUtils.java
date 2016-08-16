/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2016  AO Industries, Inc.
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
package com.aoindustries.util;

/**
 * Utilities that help when working with comparators.
 *
 * @author  AO Industries, Inc.
 */
public final class ComparatorUtils {

	/**
	 * Make no instances.
	 */
	private ComparatorUtils() {
	}

	/**
	 * Compares two integers.
	 * 
	 * @see Integer#compare(int,int) as of Java 1.7
	 * 
	 * Java 1.7: deprecated  use java.lang.Integer#compare(int,int) as of Java 1.7
	 */
	// Java 1.7: @Deprecated
	public static int compare(int i1, int i2) {
		// Java 1.7: return Integer.compare(i1, i2);
		return (i1 < i2) ? -1 : ((i1 == i2) ? 0 : 1);
	}

	/**
	 * Compares two booleans.
	 * 
	 * @see Boolean#compare(boolean,boolean) as of Java 1.7
	 * 
	 * Java 1.7: deprecated  use java.lang.Boolean#compare(boolean,boolean) as of Java 1.7
	 */
	// Java 1.7: @Deprecated
	public static int compare(boolean b1, boolean b2) {
		// Java 1.7: return Boolean.compare(b1, b2);
		return (b1 == b2) ? 0 : (b1 ? 1 : -1);
	}
}
