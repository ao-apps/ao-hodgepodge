package com.aoindustries.util.sort;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
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
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public <T> void sort(List<T> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(List<T>)", null);
        try {
            sortStatic(list);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(T[])", null);
        try {
            sortStatic(array);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(List<T>,SortStatistics)", null);
        try {
            sortStatic(list, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(T[],SortStatistics)", null);
        try {
            sortStatic(array, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(List<T>,Comparator<T>)", null);
        try {
           sortStatic(list, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(T[],Comparator<T>)", null);
        try {
           sortStatic(array, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(List<T>,Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(list, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, JavaSort.class, "sort(T[],Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(array, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(List<T>)", null);
        try {
            Collections.sort(list, null);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public static <T> void sortStatic(T[] array) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(T[])", null);
        try {
            Arrays.sort(array, null);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortStatic(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(List<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            Collections.sort(list, null);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortStatic(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(T[],SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            Arrays.sort(array, null);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(List<T>,Comparator<T>)", null);
        try {
            Collections.sort(list, comparator);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public static <T> void sortStatic(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(T[],Comparator<T>)", null);
        try {
            Arrays.sort(array, comparator);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(List<T>,Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            Collections.sort(list, comparator);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortStatic(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, JavaSort.class, "sortStatic(T[],Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            Arrays.sort(array, comparator);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}