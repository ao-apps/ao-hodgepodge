package com.aoindustries.util.sort;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.*;

/**
 * Attempts to automatically select the best sort algorithm based on information
 * available in the list.  It takes into account list length and list type.
 *
 * @version  1.0
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