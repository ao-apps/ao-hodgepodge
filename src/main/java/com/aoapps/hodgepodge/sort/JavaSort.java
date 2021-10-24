/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2013, 2016, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A sort algorithm using the standard Java sort methods.
 *
 * @author  AO Industries, Inc.
 */
public final class JavaSort extends BaseComparisonSortAlgorithm<Object> {

	private static final JavaSort instance = new JavaSort();

	public static JavaSort getInstance() {
		return instance;
	}

	private JavaSort() {
	}

	@Override
	public boolean isStable() {
		return true;
	}

	@Override
	public <T> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats) {
		if(stats != null) stats.sortStarting();
		Collections.sort(list, comparator);
		if(stats != null) stats.sortEnding();
	}

	@Override
	public <T> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats) {
		if(stats != null) stats.sortStarting();
		Arrays.sort(array, comparator);
		if(stats != null) stats.sortEnding();
	}
}
