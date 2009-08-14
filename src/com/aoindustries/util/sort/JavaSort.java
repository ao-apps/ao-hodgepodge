/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sorting utilities.
 *
 * @author  AO Industries, Inc.
 */
public class JavaSort extends SortAlgorithm {

    protected JavaSort() {
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

    public static <T> void sortStatic(List<T> list) {
        Collections.sort(list, null);
    }
    
    public static <T> void sortStatic(T[] array) {
        Arrays.sort(array, null);
    }

    public static <T> void sortStatic(List<T> list, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
        Collections.sort(list, null);
        if(stats!=null) stats.sortEnding();
    }

    public static <T> void sortStatic(T[] array, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
        Arrays.sort(array, null);
        if(stats!=null) stats.sortEnding();
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator) {
        Collections.sort(list, comparator);
    }
    
    public static <T> void sortStatic(T[] array, Comparator<T> comparator) {
        Arrays.sort(array, comparator);
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
        Collections.sort(list, comparator);
        if(stats!=null) stats.sortEnding();
    }

    public static <T> void sortStatic(T[] array, Comparator<T> comparator, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
        Arrays.sort(array, comparator);
        if(stats!=null) stats.sortEnding();
    }
}