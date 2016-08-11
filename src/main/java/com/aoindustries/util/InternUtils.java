/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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
 * @author  AO Industries, Inc.
 */
public final class InternUtils {

	/**
	 * No instances
	 */
	private InternUtils() {
	}

	/**
	 * Interns the object, return null when null.
	 */
	public static <T extends Internable<T>> T intern(T value) {
		if(value==null) return null;
		return value.intern();
	}

	/**
	 * Null-safe intern: interns a String if it is not null, returns null if parameter is null.
	 */
	public static String intern(String S) {
		if(S==null) return null;
		return S.intern();
	}
}
