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
package com.aoindustries.util.sort;

import java.util.Comparator;
import java.util.List;

/**
 * A heap sort demonstration algorithm
 * SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 *
 * @author Jason Harrison@cs.ubc.ca
 * @version 	1.0, 23 Jun 1995
 * <p>
 * Adapted from <a href='http://www.cs.ubc.ca/spider/harrison/Java/HeapSortAlgorithm.java.html'>Jason Harrison's HeapSortAlgorithm</a>.
 */
final public class HeapSort extends BaseComparisonSortAlgorithm<Object> {

	private static final HeapSort instance = new HeapSort();

	public static HeapSort getInstance() {
		return instance;
	}

    private HeapSort() {
    }

	@Override
	public boolean isStable() {
		return false;
	}

	@Override
    public <T> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
        heapSort(list, comparator, stats);
        if(stats!=null) stats.sortEnding();
    }

	@Override
    public <T> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
        heapSort(array, comparator, stats);
        if(stats!=null) stats.sortEnding();
    }

    static <T> void heapSort(List<T> list, Comparator<? super T> comparator, SortStatistics stats) {
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
    }

    static <T> void heapSort(T[] array, Comparator<? super T> comparator, SortStatistics stats) {
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
    }

    private static <T> void downheap(List<T> list, int k, int N, Comparator<? super T> comparator, SortStatistics stats) {
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
        set(list, k-1, temp, stats);
    }

    private static <T> void downheap(T[] array, int k, int N, Comparator<? super T> comparator, SortStatistics stats) {
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
        set(array, k-1, temp, stats);
    }
}
