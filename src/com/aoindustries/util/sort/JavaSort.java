package com.aoindustries.util.sort;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Sorting utilities.
 *
 * @version  1.0
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