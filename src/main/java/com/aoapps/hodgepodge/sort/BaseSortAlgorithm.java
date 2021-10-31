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
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.sort;

import com.aoapps.hodgepodge.io.FileList;
import java.util.List;

/**
 * Generalized structure for sort algorithms.
 *
 * @author  AO Industries, Inc.
 */
abstract class BaseSortAlgorithm<E> implements SortAlgorithm<E>{

	protected BaseSortAlgorithm() {
	}

	@Override
	public <T extends E> void sort(List<T> list) {
		sort(list, null);
	}

	@Override
	public <T extends E> void sort(T[] array) {
		sort(array, null);
	}

	@Override
	public abstract <T extends E> void sort(List<T> list, SortStatistics stats);

	@Override
	public abstract <T extends E> void sort(T[] array, SortStatistics stats);

	protected static <T> T get(List<T> list, int i, SortStatistics stats) {
		if(stats != null) stats.sortGetting();
		return list.get(i);
	}

	protected static <T> T get(T[] array, int i, SortStatistics stats) {
		if(stats != null) stats.sortGetting();
		return array[i];
	}

	protected static <T> void set(List<T> list, int i, T o, SortStatistics stats) {
		if(stats != null) stats.sortSetting();
		list.set(i, o);
	}

	protected static <T> void set(T[] array, int i, T o, SortStatistics stats) {
		if(stats != null) stats.sortSetting();
		array[i] = o;
	}

	protected static <T> void swap(List<T> list, int i, int j, SortStatistics stats) {
		if(stats != null) stats.sortSwapping();

		if(list instanceof FileList<?>) ((FileList<?>)list).swap(i, j);
		else {
			assert list != null;
			T t = list.get(i);
			list.set(i, list.get(j));
			list.set(j, t);
		}
	}

	protected static <T> void swap(T[] array, int i, int j, SortStatistics stats) {
		if(stats != null) stats.sortSwapping();

		T t = array[i];
		array[i] = array[j];
		array[j] = t;
	}
}
