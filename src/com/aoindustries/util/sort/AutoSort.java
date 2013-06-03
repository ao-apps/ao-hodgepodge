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

import java.util.Comparator;
import java.util.List;

/**
 * Attempts to automatically select the best sort algorithm based on information
 * available in the list.  It takes into account list length and list type.
 *
 * @author  AO Industries, Inc.
 */
final public class AutoSort extends ComparisonSortAlgorithm<Object> {

	private static final int FAST_QSORT_THRESHOLD = 10000;

	private static final AutoSort instance = new AutoSort();

	public static AutoSort getInstance() {
		return instance;
	}

	private AutoSort() {
	}

	@Override
	public boolean isStable() {
		return false;
	}

	@Override
	public <T> void sort(List<T> list) {
		getRecommendedSortAlgorithm(list).sort(list);
	}

	@Override
	public <T> void sort(T[] array) {
		getRecommendedSortAlgorithm(array).sort(array);
	}

	@Override
	public <T> void sort(List<T> list, SortStatistics stats) {
		getRecommendedSortAlgorithm(list).sort(list, stats);
	}

	@Override
	public <T> void sort(T[] array, SortStatistics stats) {
		getRecommendedSortAlgorithm(array).sort(array, stats);
	}

	@Override
	public <T> void sort(List<T> list, Comparator<? super T> comparator) {
		getRecommendedSortAlgorithm(list).sort(list, comparator);
	}

	@Override
	public <T> void sort(T[] array, Comparator<? super T> comparator) {
		getRecommendedSortAlgorithm(array).sort(array, comparator);
	}

	@Override
	public <T> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats) {
		getRecommendedSortAlgorithm(list).sort(list, comparator, stats);
	}

	@Override
	public <T> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats) {
		getRecommendedSortAlgorithm(array).sort(array, comparator, stats);
	}

	public static <T> ComparisonSortAlgorithm<? super T> getRecommendedSortAlgorithm(List<T> list) {
		if(list.size() >= FAST_QSORT_THRESHOLD) return FastQSort.getInstance();
		return JavaSort.getInstance();
	}

	public static <T> ComparisonSortAlgorithm<? super T> getRecommendedSortAlgorithm(T[] array) {
		if(array.length >= FAST_QSORT_THRESHOLD) return FastQSort.getInstance();
		return JavaSort.getInstance();
	}
}
