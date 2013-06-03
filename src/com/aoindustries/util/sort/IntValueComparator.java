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
package com.aoindustries.util.sort;

import java.util.Comparator;

/**
 * Orders numbers by their integer representation.
 *
 * @author  AO Industries, Inc.
 */
final public class IntValueComparator implements Comparator<Number> {

	private static final IntValueComparator instance = new IntValueComparator();

	public static IntValueComparator getInstance() {
		return instance;
	}

    private IntValueComparator() {
    }

	public int compare(Number o1, Number o2) {
		int i1 = o1.intValue();
		int i2 = o2.intValue();
		if(i1<i2) return -1;
		if(i1>i2) return 1;
		return 0;
	}
}
