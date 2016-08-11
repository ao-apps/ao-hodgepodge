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
package com.aoindustries.util.sort;

import java.util.Comparator;
import java.util.List;

/**
 * Generalized structure for sort algorithms that are based on comparisons.
 *
 * @author  AO Industries, Inc.
 */
public interface ComparisonSortAlgorithm<E> extends SortAlgorithm<E> {

	<T extends E> void sort(List<T> list, Comparator<? super T> comparator);

	<T extends E> void sort(T[] array, Comparator<? super T> comparator);

	<T extends E> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats);

	<T extends E> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats);
}
