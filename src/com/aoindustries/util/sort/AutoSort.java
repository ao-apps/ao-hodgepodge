package com.aoindustries.util.sort;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
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
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public <T> void sort(List<T> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(List<T>)", null);
        try {
            sortStatic(list);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(T[])", null);
        try {
            sortStatic(array);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(List<T>,SortStatistics)", null);
        try {
            sortStatic(list, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(T[],SortStatistics)", null);
        try {
            sortStatic(array, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(List<T>,Comparator<T>)", null);
        try {
           sortStatic(list, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(T[],Comparator<T>)", null);
        try {
           sortStatic(array, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(List<T>,Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(list, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sort(T[],Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(array, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> SortAlgorithm getRecommendedSortAlgorithm(List<T> list) {
        Profiler.startProfile(Profiler.FAST, AutoSort.class, "getRecommendedSortAlgorithm(List<T>)", null);
        try {
            if(list.size()>=10000) return new FastQSort();
            return new JavaSort();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> SortAlgorithm getRecommendedSortAlgorithm(T[] array) {
        Profiler.startProfile(Profiler.FAST, AutoSort.class, "getRecommendedSortAlgorithm(T[])", null);
        try {
            if(array.length>=10000) return new FastQSort();
            return new JavaSort();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortStatic(List<T> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(List<T>)", null);
        try {
            getRecommendedSortAlgorithm(list).sort(list);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public static <T> void sortStatic(T[] array) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(T[])", null);
        try {
            getRecommendedSortAlgorithm(array).sort(array);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(List<T>,SortStatistics)", null);
        try {
            getRecommendedSortAlgorithm(list).sort(list, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(T[],SortStatistics)", null);
        try {
            getRecommendedSortAlgorithm(array).sort(array, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(List<T>,Comparator<T>)", null);
        try {
            getRecommendedSortAlgorithm(list).sort(list, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public static <T> void sortStatic(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(T[],Comparator<T>)", null);
        try {
            getRecommendedSortAlgorithm(array).sort(array, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(List<T>,Comparator<T>,SortStatistics)", null);
        try {
            getRecommendedSortAlgorithm(list).sort(list, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, AutoSort.class, "sortStatic(T[],Comparator<T>,SortStatistics)", null);
        try {
            getRecommendedSortAlgorithm(array).sort(array, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
}