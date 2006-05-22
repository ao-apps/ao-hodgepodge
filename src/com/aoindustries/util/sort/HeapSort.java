package com.aoindustries.util.sort;

/*
 * @(#)HeapSortAlgorithm.java	1.0 95/06/23 Jason Harrison
 *
 * Copyright (c) 1995 University of British Columbia
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * UBC MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. UBC SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import com.aoindustries.profiler.*;
import java.util.*;

/**
 * A heap sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author Jason Harrison@cs.ubc.ca
 * @version 	1.0, 23 Jun 1995
 * <p>
 * Adapted from <a href='http://www.cs.ubc.ca/spider/harrison/Java/HeapSortAlgorithm.java.html'>Jason Harrison's HeapSortAlgorithm</a>.
 */
public class HeapSort extends SortAlgorithm {

    protected HeapSort() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public <T> void sort(List<T> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(List<T>)", null);
        try {
            sortStatic(list);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(T[])", null);
        try {
            sortStatic(array);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(List<T>,SortStatistics)", null);
        try {
            sortStatic(list, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(T[],SortStatistics)", null);
        try {
            sortStatic(array, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(List<T>,Comparator<T>)", null);
        try {
           sortStatic(list, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(T[],Comparator<T>)", null);
        try {
           sortStatic(array, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(List<T>,Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(list, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sort(T[],Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(array, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sortStatic(List<T>)", null);
        try {
            sortStatic(list, null, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public static <T> void sortStatic(T[] array) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sortStatic(T[])", null);
        try {
            sortStatic(array, null, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sortStatic(List<T>,SortStatistics)", null);
        try {
            sortStatic(list, null, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sortStatic(T[],SortStatistics)", null);
        try {
            sortStatic(array, null, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sortStatic(List<T>,Comparator<T>)", null);
        try {
            sortStatic(list, comparator, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public static <T> void sortStatic(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, HeapSort.class, "sortStatic(T[],Comparator<T>)", null);
        try {
            sortStatic(array, comparator, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, HeapSort.class, "sortStatic(List<T>,Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            sortStatic0(list, comparator, stats);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortStatic(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, HeapSort.class, "sortStatic(T[],Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            sortStatic0(array, comparator, stats);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    static <T> void sortStatic0(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.UNKNOWN, HeapSort.class, "sortStatic0(List<T>,Comparator<T>,SortStatistics)", null);
        try {
            int N=list.size();
            for (int k = N/2; k > 0; k--) {
                if(stats!=null) stats.sortRecursing();
                downheap(list, k, N, comparator, stats);
                if(stats!=null) stats.sortUnrecursing();
            }
            do {
                swap(list, 0, N-1, stats);
                N -= 1;
                if(stats!=null) stats.sortRecursing();
                downheap(list, 1, N, comparator, stats);
                if(stats!=null) stats.sortUnrecursing();
            } while (N > 1);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    static <T> void sortStatic0(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.UNKNOWN, HeapSort.class, "sortStatic0(T[],Comparator<T>,SortStatistics)", null);
        try {
            int N=array.length;
            for (int k = N/2; k > 0; k--) {
                if(stats!=null) stats.sortRecursing();
                downheap(array, k, N, comparator, stats);
                if(stats!=null) stats.sortUnrecursing();
            }
            do {
                swap(array, 0, N-1, stats);
                N -= 1;
                if(stats!=null) stats.sortRecursing();
                downheap(array, 1, N, comparator, stats);
                if(stats!=null) stats.sortUnrecursing();
            } while (N > 1);
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private static <T> void downheap(List<T> list, int k, int N, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, HeapSort.class, "downheap(List<T>,int,int,Comparator<T>,SortStatistics)", null);
        try {
            T temp=get(list, k - 1, stats);
            while (k <= N/2) {
                int j = k + k;
                if(
                    (j < N)
                    && compare(list, j-1, j, comparator, stats) < 0
                ) {
                    j++;
                }
                if (compare(temp, get(list, j-1, stats), comparator, stats)>=0) {
                    break;
                } else {
                    set(list, k-1, get(list, j-1, stats), stats);
                    k = j;
                }
            }
            set(list, k-1, temp, stats);;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private static <T> void downheap(T[] array, int k, int N, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, HeapSort.class, "downheap(T[],int,int,Comparator<T>,SortStatistics)", null);
        try {
            T temp=get(array, k - 1, stats);
            while (k <= N/2) {
                int j = k + k;
                if(
                    (j < N)
                    && compare(array, j-1, j, comparator, stats) < 0
                ) {
                    j++;
                }
                if (compare(temp, get(array, j-1, stats), comparator, stats)>=0) {
                    break;
                } else {
                    set(array, k-1, get(array, j-1, stats), stats);
                    k = j;
                }
            }
            set(array, k-1, temp, stats);;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}