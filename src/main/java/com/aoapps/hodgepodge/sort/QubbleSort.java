/*
 * @(#)QubbleSortAlgorithm.java 1.0 95/06/26 Jim Boritz
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

package com.aoapps.hodgepodge.sort;

import java.util.Comparator;
import java.util.List;

/**
 * An quick sort with buble sort speedup  demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author Jim Boritz
 * @version 1.6, 26 Jun 1995
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
public final class QubbleSort extends BaseComparisonSortAlgorithm<Object> {

  private static final QubbleSort instance = new QubbleSort();

  public static QubbleSort getInstance() {
    return instance;
  }

  private QubbleSort() {
    // Do nothing
  }

  @Override
  public <T> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats) {
    if (stats != null) {
      stats.sortStarting();
    }
    sort(list, 0, list.size()-1, comparator, stats);
    if (stats != null) {
      stats.sortEnding();
    }
  }

  @Override
  public <T> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats) {
    if (stats != null) {
      stats.sortStarting();
    }
    sort(array, 0, array.length-1, comparator, stats);
    if (stats != null) {
      stats.sortEnding();
    }
  }

  @Override
  public boolean isStable() {
    return false; // Not really sure since based on quicksort, safer to say not stable.
  }

  private static <T> void sort(List<T> list, int lo0, int hi0, Comparator<? super T> comparator, SortStatistics stats) {
    int lo = lo0;
    int hi = hi0;

    /*
     *  Bubble sort if the number of elements is less than 6
     */
    if ((hi-lo) <= 6) {
      if (stats != null) {
        stats.sortRecursing();
      }
      bsort(list, lo, hi, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
      return;
    }

    /*
     *  Pick a pivot and move it out of the way
     */
    T pivot = get(list, (lo+hi)/2, stats);
    set(list, (lo+hi)/2, get(list, hi, stats), stats);
    set(list, hi, pivot, stats);

    while (lo<hi) {
      /*
       *  Search forward from a[lo] until an element is found that
       *  is greater than the pivot or lo >= hi
       */
      while (
        compare(get(list, lo, stats), pivot, comparator, stats) <= 0
        && lo < hi
      ) {
        lo++;
      }

      /*
       *  Search backward from a[hi] until element is found that
       *  is less than the pivot, or hi <= lo
       */
      while (
        compare(pivot, get(list, hi, stats), comparator, stats) <= 0
        && lo < hi
      ) {
        hi--;
      }

      /*
       *  Swap elements a[lo] and a[hi]
       */
      if ( lo < hi ) {
        swap(list, hi, lo, stats);
      }
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
    if (stats != null) {
      stats.sortRecursing();
    }
    sort(list, lo0, lo-1, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }

    if (stats != null) {
      stats.sortRecursing();
    }
    sort(list, hi+1, hi0, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }
  }

  private static <T> void sort(T[] array, int lo0, int hi0, Comparator<? super T> comparator, SortStatistics stats) {
    int lo = lo0;
    int hi = hi0;

    /*
     *  Bubble sort if the number of elements is less than 6
     */
    if ((hi-lo) <= 6) {
      if (stats != null) {
        stats.sortRecursing();
      }
      bsort(array, lo, hi, comparator, stats);
      if (stats != null) {
        stats.sortUnrecursing();
      }
      return;
    }

    /*
     *  Pick a pivot and move it out of the way
     */
    T pivot = get(array, (lo+hi)/2, stats);
    set(array, (lo+hi)/2, get(array, hi, stats), stats);
    set(array, hi, pivot, stats);

    while (lo<hi) {
      /*
       *  Search forward from a[lo] until an element is found that
       *  is greater than the pivot or lo >= hi
       */
      while (
        compare(get(array, lo, stats), pivot, comparator, stats) <= 0
        && lo < hi
      ) {
        lo++;
      }

      /*
       *  Search backward from a[hi] until element is found that
       *  is less than the pivot, or hi <= lo
       */
      while (
        compare(pivot, get(array, hi, stats), comparator, stats) <= 0
        && lo < hi
      ) {
        hi--;
      }

      /*
       *  Swap elements a[lo] and a[hi]
       */
      if ( lo < hi ) {
        swap(array, hi, lo, stats);
      }
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
    if (stats != null) {
      stats.sortRecursing();
    }
    sort(array, lo0, lo-1, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }

    if (stats != null) {
      stats.sortRecursing();
    }
    sort(array, hi+1, hi0, comparator, stats);
    if (stats != null) {
      stats.sortUnrecursing();
    }
  }

  static <T> void bsort(List<T> list, int lo, int hi, Comparator<? super T> comparator, SortStatistics stats) {
    for (int j = hi; j > lo; j--) {
      for (int i = lo; i < j; i++) {
        T o1 = get(list, i, stats);
        T o2 = get(list, i + 1, stats);
        if (compare(o1, o2, comparator, stats) > 0) {
          set(list, i + 1, o1, stats);
          set(list, i, o2, stats);
        }
      }
    }
  }

  static <T> void bsort(T[] array, int lo, int hi, Comparator<? super T> comparator, SortStatistics stats) {
    for (int j = hi; j > lo; j--) {
      for (int i = lo; i < j; i++) {
        T o1 = get(array, i, stats);
        T o2 = get(array, i + 1, stats);
        if (compare(o1, o2, comparator, stats) > 0) {
          set(array, i + 1, o1, stats);
          set(array, i, o2, stats);
        }
      }
    }
  }
}
