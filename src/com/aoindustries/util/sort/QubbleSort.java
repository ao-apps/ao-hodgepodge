package com.aoindustries.util.sort;

/*
 * @(#)QubbleSortAlgorithm.java	1.0 95/06/26 Jim Boritz
 *
 * Copyright (c) 1995 UBC Microsystems, Inc. All Rights Reserved.
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
 * An quick sort with buble sort speedup  demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author Jim Boritz
 * @version 	1.6, 26 Jun 1995
 * <p>
 * 19 Feb 1996: Fixed to avoid infinite loop discoved by Paul Haberli.
 *              Misbehaviour expressed when the pivot element was not unique.
 *              -Jason Harrison
 *
 * 21 Jun 1996: Modified code based on comments from Paul Haeberli, and
 *              Peter Schweizer (Peter.Schweizer@mni.fh-giessen.de).  
 *              Used Daeron Meyer's (daeron@geom.umn.edu) code for the
 *              new pivoting code. - Jason Harrison
 *
 * 09 Jan 1998: Another set of bug fixes by Thomas Everth (everth@wave.co.nz)
 *              and John Brzustowski (jbrzusto@gpu.srv.ualberta.ca).
 * <p>
 * Adapted from <a href='http://www.cs.ubc.ca/spider/harrison/Java/QubbleSortAlgorithm.java.html'>Jim Boritz' QubbleSortAlgorithm</a>.
 */
public class QubbleSort extends SortAlgorithm {

    protected QubbleSort() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public <T> void sort(List<T> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(List<T>)", null);
        try {
            sortStatic(list);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(T[])", null);
        try {
            sortStatic(array);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(List<T>,SortStatistics)", null);
        try {
            sortStatic(list, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(T[],SortStatistics)", null);
        try {
            sortStatic(array, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(List<T>,Comparator<T>)", null);
        try {
           sortStatic(list, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public <T> void sort(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(T[],Comparator<T>)", null);
        try {
           sortStatic(array, comparator);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(List<T>,Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(list, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public <T> void sort(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sort(T[],Comparator<T>,SortStatistics)", null);
        try {
           sortStatic(array, comparator, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sortStatic(List<T>)", null);
        try {
            sortStatic(list, null, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public static <T> void sortStatic(T[] array) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sortStatic(T[])", null);
        try {
            sortStatic(array, null, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sortStatic(List<T>,SortStatistics)", null);
        try {
            sortStatic(list, null, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(T[] array, SortStatistics stats) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sortStatic(T[],SortStatistics)", null);
        try {
            sortStatic(array, null, stats);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sortStatic(List<T>,Comparator<T>)", null);
        try {
            sortStatic(list, comparator, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public static <T> void sortStatic(T[] array, Comparator<T> comparator) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, QubbleSort.class, "sortStatic(T[],Comparator<T>)", null);
        try {
            sortStatic(array, comparator, null);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public static <T> void sortStatic(List<T> list, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.UNKNOWN, QubbleSort.class, "sortStatic(List<T>,Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            sort(list, 0, list.size()-1, comparator, stats);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    public static <T> void sortStatic(T[] array, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.UNKNOWN, QubbleSort.class, "sortStatic(T[],Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortStarting();
            sort(array, 0, array.length-1, comparator, stats);
            if(stats!=null) stats.sortEnding();
        } finally {
            Profiler.endProfile(Profiler.UNKNOWN);
        }
    }

    private static <T> void sort(List<T> list, int lo0, int hi0, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, QubbleSort.class, "sort(List<T>,int,int,Comparator<T>,SortStatistics)", null);
        try {
            int lo = lo0;
            int hi = hi0;

            /*
             *  Bubble sort if the number of elements is less than 6 
             */
            if ((hi-lo) <= 6) {
                if(stats!=null) stats.sortRecursing();
                bsort(list, lo, hi, comparator, stats);
                if(stats!=null) stats.sortUnrecursing();
                return;
            }

            /*
             *  Pick a pivot and move it out of the way
             */
            T pivot = get(list, (lo+hi)/2, stats);
            set(list, (lo+hi)/2, get(list, hi, stats), stats);
            set(list, hi, pivot, stats);

            while(lo<hi) {
                /*
                 *  Search forward from a[lo] until an element is found that
                 *  is greater than the pivot or lo >= hi 
                 */
                while(
                    compare(get(list, lo, stats), pivot, comparator, stats) <= 0
                    && lo < hi
                ) {
                    lo++;
                }

                /*
                 *  Search backward from a[hi] until element is found that
                 *  is less than the pivot, or hi <= lo 
                 */
                while(
                    compare(pivot, get(list, hi, stats), comparator, stats) <= 0
                    && lo < hi 
                ) {
                    hi--;
                }

                /*
                 *  Swap elements a[lo] and a[hi]
                 */
                if( lo < hi ) swap(list, hi, lo, stats);
            }

            /*
             *  Put the median in the "center" of the list
             */
            set(list, hi0, get(list, hi, stats), stats);
            set(list, hi, pivot, stats);

            /*
             *  Recursive calls, elements a[lo0] to a[lo-1] are less than or
             *  equal to pivot, elements a[hi+1] to a[hi0] are greater than
             *  pivot.
             */
            if(stats!=null) stats.sortRecursing();
            sort(list, lo0, lo-1, comparator, stats);
            if(stats!=null) stats.sortUnrecursing();

            if(stats!=null) stats.sortRecursing();
            sort(list, hi+1, hi0, comparator, stats);
            if(stats!=null) stats.sortUnrecursing();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private static <T> void sort(T[] array, int lo0, int hi0, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, QubbleSort.class, "sort(T[],int,int,Comparator<T>,SortStatistics)", null);
        try {
            int lo = lo0;
            int hi = hi0;

            /*
             *  Bubble sort if the number of elements is less than 6 
             */
            if ((hi-lo) <= 6) {
                if(stats!=null) stats.sortRecursing();
                bsort(array, lo, hi, comparator, stats);
                if(stats!=null) stats.sortUnrecursing();
                return;
            }

            /*
             *  Pick a pivot and move it out of the way
             */
            T pivot = get(array, (lo+hi)/2, stats);
            set(array, (lo+hi)/2, get(array, hi, stats), stats);
            set(array, hi, pivot, stats);

            while(lo<hi) {
                /*
                 *  Search forward from a[lo] until an element is found that
                 *  is greater than the pivot or lo >= hi 
                 */
                while(
                    compare(get(array, lo, stats), pivot, comparator, stats) <= 0
                    && lo < hi
                ) {
                    lo++;
                }

                /*
                 *  Search backward from a[hi] until element is found that
                 *  is less than the pivot, or hi <= lo 
                 */
                while(
                    compare(pivot, get(array, hi, stats), comparator, stats) <= 0
                    && lo < hi 
                ) {
                    hi--;
                }

                /*
                 *  Swap elements a[lo] and a[hi]
                 */
                if( lo < hi ) swap(array, hi, lo, stats);
            }

            /*
             *  Put the median in the "center" of the array
             */
            set(array, hi0, get(array, hi, stats), stats);
            set(array, hi, pivot, stats);

            /*
             *  Recursive calls, elements a[lo0] to a[lo-1] are less than or
             *  equal to pivot, elements a[hi+1] to a[hi0] are greater than
             *  pivot.
             */
            if(stats!=null) stats.sortRecursing();
            sort(array, lo0, lo-1, comparator, stats);
            if(stats!=null) stats.sortUnrecursing();

            if(stats!=null) stats.sortRecursing();
            sort(array, hi+1, hi0, comparator, stats);
            if(stats!=null) stats.sortUnrecursing();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private static <T> void bsort(List<T> list, int lo, int hi, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, QubbleSort.class, "bsort(List<T>,int,intComparator<T>,SortStatistics)", null);
        try {
            for (int j=hi; j > lo; j--) {
                for (int i=lo; i < j; i++) {
                    T O1=get(list, i, stats);
                    T O2=get(list, i+1, stats);
                    if(compare(O1, O2, comparator, stats)>0) {
                        set(list, i+1, O1, stats);
                        set(list, i, O2, stats);
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private static <T> void bsort(T[] array, int lo, int hi, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, QubbleSort.class, "bsort(T[],int,int,Comparator<T>,SortStatistics)", null);
        try {
            for (int j=hi; j > lo; j--) {
                for (int i=lo; i < j; i++) {
                    T O1=get(array, i, stats);
                    T O2=get(array, i+1, stats);
                    if(compare(O1, O2, comparator, stats)>0) {
                        set(array, i+1, O1, stats);
                        set(array, i, O2, stats);
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}