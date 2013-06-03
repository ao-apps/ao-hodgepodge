/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2013  AO Industries, Inc.
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

import com.aoindustries.io.FileList;
import java.util.List;

/**
 * Generalized structure for sort algorithms.
 *
 * TODO: Make SortAlgorithm an interface
 *
 * @author  AO Industries, Inc.
 */
abstract public class SortAlgorithm<E> {

	protected SortAlgorithm() {
	}

	/**
	 * Checks if this is a stable sort.  A stable sort will keep elements with
	 * equal values in their same relative order.
	 */
	public abstract boolean isStable();

	public <T extends E> void sort(List<T> list) {
		sort(list, null);
	}

    public <T extends E> void sort(T[] array) {
        sort(array, null);
    }

	public abstract <T extends E> void sort(List<T> list, SortStatistics stats);

    public abstract <T extends E> void sort(T[] array, SortStatistics stats);

	protected static <T> T get(List<T> list, int i, SortStatistics stats) {
		if(stats!=null) stats.sortGetting();
		return list.get(i);
	}

	protected static <T> T get(T[] array, int i, SortStatistics stats) {
		if(stats!=null) stats.sortGetting();
		return array[i];
	}

	protected static <T> void set(List<T> list, int i, T O, SortStatistics stats) {
		if(stats!=null) stats.sortSetting();
		list.set(i, O);
	}

	protected static <T> void set(T[] array, int i, T O, SortStatistics stats) {
		if(stats!=null) stats.sortSetting();
		array[i]=O;
	}

	protected static <T> void swap(List<T> list, int i, int j, SortStatistics stats) {
		if(stats!=null) stats.sortSwapping();

		if(list instanceof FileList<?>) ((FileList<?>)list).swap(i, j);
		else {
			T T=list.get(i);
			list.set(i, list.get(j));
			list.set(j, T);
		}
	}

	protected static <T> void swap(T[] array, int i, int j, SortStatistics stats) {
		if(stats!=null) stats.sortSwapping();

		T T=array[i];
		array[i]=array[j];
		array[j]=T;
	}
}
