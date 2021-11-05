/*
 * @(#)QSortAlgorithm.java	1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted.
 * Please refer to the file http://www.javasoft.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://www.javasoft.com/licensing.html for further important
 * licensing information for the Java (tm) Technology.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */
package com.aoapps.hodgepodge.sort;

import java.util.Comparator;
import java.util.List;

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version 	\@(#)QSortAlgorithm.java	1.3, 29 Feb 1996
 * extended with TriMedian and InsertionSort by Denis Ahrens
 * with all the tips from Robert Sedgewick (Algorithms in C++).
 * It uses TriMedian and InsertionSort for lists shorts than 4.
 * &lt;fuhrmann@cs.tu-berlin.de&gt;
 * <p>
 * Adapted from <a href='http://www.cs.ubc.ca/spider/harrison/Java/FastQSortAlgorithm.java.html'>Denis Ahrens' FastQSortAlgorithm</a>,
 * which was derived from Sun's example QSortAlgorithm.
 * <p>
 * 2003-11-06 - Dan Armstrong - To avoid worst-case scenarios, if the quickSort recursion depth exceeds <code>(int)(10*Math.log(list.size()))</code>,
 *                              the algorithm will quit and a HeapSort will be performed.
 */
public final class FastQSort extends BaseComparisonSortAlgorithm<Object> {

	private static final FastQSort instance = new FastQSort();

	public static FastQSort getInstance() {
		return instance;
	}

	private FastQSort() {
		// Do nothing
	}

	@Override
	public boolean isStable() {
		return false; // Not really sure since based on quicksort, safer to say not stable.
	}

	@Override
	public <T> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats) {
		if(stats != null) stats.sortStarting();
		int length=list.size();
		if(quickSort(list, 0, length-1, comparator, stats, 1, (int)(10*Math.log(length)))) {
			insertionSort(list, 0, length-1, comparator, stats);
		} else {
			// If quickSort fails, do a more constant-time HeapSort on the remaining data
			if(stats != null) stats.sortSwitchingAlgorithms();
			HeapSort.heapSort(list, comparator, stats);
		}
		if(stats != null) stats.sortEnding();
	}

	@Override
	public <T> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats) {
		if(stats != null) stats.sortStarting();
		int length=array.length;
		if(quickSort(array, 0, length-1, comparator, stats, 1, (int)(10*Math.log(length)))) {
			insertionSort(array, 0, length-1, comparator, stats);
		} else {
			// If quickSort fails, do a more constant-time HeapSort on the remaining data
			if(stats != null) stats.sortSwitchingAlgorithms();
			HeapSort.heapSort(array, comparator, stats);
		}
		if(stats != null) stats.sortEnding();
	}

	/**
	 * This is a generic version of C.A.R Hoare's Quick Sort
	 * algorithm. This will handle arrays that are already
	 * sorted, and arrays with duplicate keys.<br />
	 *
	 * If you think of a one dimensional array as going from
	 * the lowest index on the left to the highest index on the right
	 * then the parameters to this function are lowest index or
	 * left and highest index or right. The first time you call
	 * this function it will be with the parameters 0, a.length - 1.
	 *
	 * @param a an integer array
	 * @param lo0 left boundary of array partition
	 * @param hi0 right boundary of array partition
	 *
	 * @param true if the algorithm completed correctly, false if maximum recursion was exceeded
	 */
	private static <T> boolean quickSort(List<T> list, int l, int r, Comparator<? super T> comparator, SortStatistics stats, int currentRecursion, int maxRecursion) {
		final int M = 4;

		if((r-l)>M) {
			int i=(r+l)/2;

			if(compare(list, l, i, comparator, stats)>0) swap(list, l, i, stats);  // Tri-Median Methode!
			if(compare(list, l, r, comparator, stats)>0) swap(list, l, r, stats);
			if(compare(list, i, r, comparator, stats)>0) swap(list, i, r, stats);

			int j=r-1;
			swap(list, i, j, stats);
			i=l;
			T v=get(list, j, stats);
			for(;;) {
				while(compare(get(list, ++i, stats), v, comparator, stats)<0) {
					// Empty while
				}
				while(compare(get(list, --j, stats), v, comparator, stats)>0) {
					// Empty while
				}
				if(j<i) break;
				swap(list, i, j, stats);
			}
			swap(list, i, r-1, stats);

			int newRecursion=currentRecursion+1;
			if(newRecursion>maxRecursion) return false;
			if(stats != null) stats.sortRecursing();
			if(!quickSort(list, l, j, comparator, stats, newRecursion, maxRecursion)) return false;
			if(stats != null) stats.sortUnrecursing();

			if(stats != null) stats.sortRecursing();
			if(!quickSort(list, i+1, r, comparator, stats, newRecursion, maxRecursion)) return false;
			if(stats != null) stats.sortUnrecursing();
		}
		return true;
	}

	/**
	 * This is a generic version of C.A.R Hoare's Quick Sort
	 * algorithm. This will handle arrays that are already
	 * sorted, and arrays with duplicate keys.<br />
	 *
	 * If you think of a one dimensional array as going from
	 * the lowest index on the left to the highest index on the right
	 * then the parameters to this function are lowest index or
	 * left and highest index or right. The first time you call
	 * this function it will be with the parameters 0, a.length - 1.
	 *
	 * @param a an integer array
	 * @param lo0 left boundary of array partition
	 * @param hi0 right boundary of array partition
	 *
	 * @param true if the algorithm completed correctly, false if maximum recursion was exceeded
	 */
	private static <T> boolean quickSort(T[] array, int l, int r, Comparator<? super T> comparator, SortStatistics stats, int currentRecursion, int maxRecursion) {
		final int M = 4;

		if((r-l)>M) {
			int i=(r+l)/2;

			if(compare(array, l, i, comparator, stats)>0) swap(array, l, i, stats);  // Tri-Median Methode!
			if(compare(array, l, r, comparator, stats)>0) swap(array, l, r, stats);
			if(compare(array, i, r, comparator, stats)>0) swap(array, i, r, stats);

			int j=r-1;
			swap(array, i, j, stats);
			i=l;
			T v=get(array, j, stats);
			for(;;) {
				while(compare(get(array, ++i, stats), v, comparator, stats)<0) {
					// Empty while
				}
				while(compare(get(array, --j, stats), v, comparator, stats)>0) {
					// Empty while
				}
				if(j<i) break;
				swap(array, i, j, stats);
			}
			swap(array, i, r-1, stats);

			int newRecursion=currentRecursion+1;
			if(newRecursion>maxRecursion) return false;
			if(stats != null) stats.sortRecursing();
			if(!quickSort(array, l, j, comparator, stats, newRecursion, maxRecursion)) return false;
			if(stats != null) stats.sortUnrecursing();

			if(stats != null) stats.sortRecursing();
			if(!quickSort(array, i+1, r, comparator, stats, newRecursion, maxRecursion)) return false;
			if(stats != null) stats.sortUnrecursing();
		}
		return true;
	}

	static <T> void insertionSort(List<T> list, int lo0, int hi0, Comparator<? super T> comparator, SortStatistics stats) {
		for(int i=lo0+1;i<=hi0;i++) {
			T v=get(list, i, stats);
			int j=i;
			T t;
			while(
				j>lo0
				&& compare(t=get(list, j-1, stats), v, comparator, stats)>0
			) {
				set(list, j, t, stats);
				j--;
			}
			set(list, j, v, stats);
		}
	}

	static <T> void insertionSort(T[] array, int lo0, int hi0, Comparator<? super T> comparator, SortStatistics stats) {
		for(int i=lo0+1;i<=hi0;i++) {
			T v=get(array, i, stats);
			int j=i;
			T t;
			while(
				j>lo0
				&& compare(t=get(array, j-1, stats), v, comparator, stats)>0
			) {
				set(array, j, t, stats);
				j--;
			}
			set(array, j, v, stats);
		}
	}
}
