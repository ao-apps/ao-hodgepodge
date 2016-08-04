/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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
package com.aoindustries.lang;

/**
 * Utilities that help when working with Enums.
 *
 * @author  AO Industries, Inc.
 */
public final class EnumUtils {

    /**
     * Make no instances.
     */
    private EnumUtils() {
    }

	/**
	 * Gets the greater of two enums.
	 */
	public static <E extends Enum<E>> E max(E e1, E e2) {
		return e1.compareTo(e2) >= 0 ? e1 : e2;
	}

	/**
	 * Gets the lesser of two enums.
	 */
	public static <E extends Enum<E>> E min(E e1, E e2) {
		return e1.compareTo(e2) <= 0 ? e1 : e2;
	}
}
