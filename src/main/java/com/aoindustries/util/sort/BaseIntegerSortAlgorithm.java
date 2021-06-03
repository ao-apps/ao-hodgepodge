/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.util.sort;

import com.aoindustries.collections.IntList;
import com.aoindustries.io.FileList;

/**
 * A sort implementation that sorts int[] primitives as was as integer representation of numeric objects.
 *
 * @author  AO Industries, Inc.
 */
abstract class BaseIntegerSortAlgorithm extends BaseSortAlgorithm<Number> implements IntegerSortAlgorithm {

	protected BaseIntegerSortAlgorithm() {
	}

	@Override
	public void sort(IntList list) {
		sort(list, null);
	}

	@Override
	public void sort(int[] array) {
		sort(array, null);
	}

	@Override
	public abstract void sort(IntList list, SortStatistics stats);

	@Override
	public abstract void sort(int[] array, SortStatistics stats);

	protected static int get(IntList list, int i, SortStatistics stats) {
		if(stats!=null) stats.sortGetting();
		return list.getInt(i);
	}

	protected static int get(int[] array, int i, SortStatistics stats) {
		if(stats!=null) stats.sortGetting();
		return array[i];
	}

	protected static void set(IntList list, int i, int value, SortStatistics stats) {
		if(stats!=null) stats.sortSetting();
		list.set(i, value);
	}

	protected static void set(int[] array, int i, int value, SortStatistics stats) {
		if(stats!=null) stats.sortSetting();
		array[i]=value;
	}

	protected static void swap(IntList list, int i, int j, SortStatistics stats) {
		if(stats!=null) stats.sortSwapping();

		if(list instanceof FileList<?>) ((FileList<?>)list).swap(i, j);
		else {
			int T=list.getInt(i);
			list.set(i, list.getInt(j));
			list.set(j, T);
		}
	}

	protected static void swap(int[] array, int i, int j, SortStatistics stats) {
		if(stats!=null) stats.sortSwapping();

		int T=array[i];
		array[i]=array[j];
		array[j]=T;
	}
}
