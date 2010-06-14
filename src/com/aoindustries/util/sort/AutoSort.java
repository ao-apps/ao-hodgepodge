/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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

import java.util.*;

/**
 * Attempts to automatically select the best sort algorithm based on information
 * available in the list.  It takes into account list length and list type.
 *
 * @author  AO Industries, Inc.
 */
public class AutoSort extends SortAlgorithm {

    protected AutoSort() {
    }

    public <T> void sort(List<T> list) {
        sortStatic(list);
    }
    
    public <T> void sort(T[] array) {
        sortStatic(array);
    }

    public <T> void sort(List<T> list, SortStatistics stats) {
        sortStatic(list, stats);
    }

    public <T> void sort(T[] array, SortStatistics stats) {
        sortStatic(array, stats);
    }

    public <T> void sort(List<T> list, Comparator<T> comparator) {
       sortStatic(list, comparator);
    }
    
    public <T> void sort(T[] array, Comparator<T> comparator) {
       sortStatic(array, comparator);
    }

    public <T> void sort(List<T> list, Comparator<T> comparator, SortStatistics stats) {
       sortStatic(list, comparator, stats);
    }

    public <T> void sort(T[] array, Comparator<T> comparator, SortStatistics stats) {
       sortStatic(array, comparator, stats);
    }

    public static <T> SortAlgorithm getRecommendedSortAlgorithm(List<T> list) {
        if(list.size()>=10000) return new FastQSort();
        return new JavaSort();
    }

    public static <T> SortAlgorithm getRecommendedSortAlgorithm(T[] array) {
        if(array.length>=10000) return new FastQSort();
        return new JavaSort();
    }

    public static <T> void sortStatic(List<T> list) {
        getRecommendedSortAlgorithm(list).sort(list);
    }
    
    public static <T> void sortStatic(T[] array) {
        getRecommendedSortAlgorithm(array).sort(array);
    }

    public static <T> void sortStatic(List<T> list, SortStatistics stats) {
        getRecommendedSortAlgorithm(list).sort(list, stats);
    }

    public static <T> void sortStatic(T[] array, SortStatistics stats) {
        getRecommendedSortAlgorithm(array).sort(array, stats);
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator) {
        getRecommendedSortAlgorithm(list).sort(list, comparator);
    }
    
    public static <T> void sortStatic(T[] array, Comparator<T> comparator) {
        getRecommendedSortAlgorithm(array).sort(array, comparator);
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        getRecommendedSortAlgorithm(list).sort(list, comparator, stats);
    }

    public static <T> void sortStatic(T[] array, Comparator<T> comparator, SortStatistics stats) {
        getRecommendedSortAlgorithm(array).sort(array, comparator, stats);
    }
}