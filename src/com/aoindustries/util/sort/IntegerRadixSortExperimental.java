/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

import com.aoindustries.lang.NotImplementedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 * TODO: Consider thread-local for the small sort space
 *
 * TODO: Consider http://erik.gorset.no/2011/04/radix-sort-is-faster-than-quicksort.html
 *                https://github.com/gorset/radix/blob/master/Radix.java
 *
 * @author  AO Industries, Inc.
 */
final public class IntegerRadixSortExperimental extends IntegerSortAlgorithm {

	private static final int LARGE_THRESHOLD = 16384; // TODO: Tune: 65536;

	private static final boolean USE_SMALL_THREAD_LOCAL = true;

	private static final boolean USE_LARGE_DELAYED_ALLOCATE = true;

	private static final boolean FORCE_LIST_ITERATOR = false;

	private static final boolean USE_TO_ARRAY = true;

	private static final int BITS_PER_PASS = 8; // TODO: Tune: 8 // Must be power of two and less than or equal to 32
	private static final int PASS_SIZE = 1 << BITS_PER_PASS;
	private static final int PASS_MASK = PASS_SIZE - 1;

	private static final IntegerRadixSortExperimental instance = new IntegerRadixSortExperimental();

    public static IntegerRadixSortExperimental getInstance() {
        return instance;
    }

    private IntegerRadixSortExperimental() {
    }

	@Override
    public <T extends Number> void sort(List<T> list, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
		final int size = list.size();
		if(list.size()<LARGE_THRESHOLD) sortSmall(list, stats);
		else sortLarge(list, stats);
		if(stats!=null) stats.sortEnding();
    }

	// TODO: null-out when done, or just store indexes?
	// TODO: Could be short[] for very small case
	private static final ThreadLocal<int[][]> fromQueues = /* TODO !USE_SMALL_THREAD_LOCAL ? null :*/ new ThreadLocal<int[][]>() {
		@Override
		protected int[][] initialValue() {
			return new int[PASS_SIZE][LARGE_THRESHOLD];
		}
	};
	private static final ThreadLocal<int[][]> toQueues = /* TODO !USE_SMALL_THREAD_LOCAL ? null :*/ new ThreadLocal<int[][]>() {
		@Override
		protected int[][] initialValue() {
			return new int[PASS_SIZE][LARGE_THRESHOLD];
		}
	};

	private static <T extends Number> void sortSmall(List<T> list, SortStatistics stats) {
		final int size = list.size();
		final T[] array = USE_TO_ARRAY ? (T[])list.toArray(new Number[size]) : null;
		if(stats!=null) {
			// Each element is get once and set once
			stats.sortGetting(size);
			stats.sortSetting(size);
		}
		final boolean useRandomAccess = (list instanceof RandomAccess);
		@SuppressWarnings("unchecked")
		int[][] fromQueues = (USE_SMALL_THREAD_LOCAL ? IntegerRadixSortExperimental.fromQueues.get() : new int[PASS_SIZE][size]);
		int[] fromQueueLengths = new int[PASS_SIZE];
		@SuppressWarnings("unchecked")
		int[][] toQueues = (USE_SMALL_THREAD_LOCAL ? IntegerRadixSortExperimental.toQueues.get() : new int[PASS_SIZE][size]);
		int[] toQueueLengths = new int[PASS_SIZE];
		// Initial population of elements into fromQueues
		if(USE_TO_ARRAY) {
			for(int i=0;i<size;i++) {
				int queueNum = array[i].intValue() & PASS_MASK;
				fromQueues[queueNum][fromQueueLengths[queueNum]++] = i;
			}
		} else if (useRandomAccess) {
			for(int i=0;i<size;i++) {
				int queueNum = list.get(i).intValue() & PASS_MASK;
				fromQueues[queueNum][fromQueueLengths[queueNum]++] = i;
			}
		} else {
			int i = 0;
			for(T number : list) {
				int queueNum = number.intValue() & PASS_MASK;
				fromQueues[queueNum][fromQueueLengths[queueNum]++] = i++;
			}
		}
		for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
			for(int i=0; i<PASS_SIZE; i++) {
				int[] fromQueue = fromQueues[i];
				int length = fromQueueLengths[i];
				for(int j=0; j<length; j++) {
					int index = fromQueue[j];
					T number = USE_TO_ARRAY ? array[index] : list.get(index);
					int queueNum = (number.intValue() >>> shift) & PASS_MASK;
					toQueues[queueNum][toQueueLengths[queueNum]++] = index;
				}
				fromQueueLengths[i] = 0;
			}

			// Swap from and to
			int[][] tempQueues = fromQueues;
			fromQueues = toQueues;
			toQueues = tempQueues;
			int[] tempLengths = fromQueueLengths;
			fromQueueLengths = toQueueLengths;
			toQueueLengths = tempLengths;
		}
		// Pick-up fromQueues and put into results, negative before positive to performed signed
		final int midPoint = PASS_SIZE>>>1;
		if(!FORCE_LIST_ITERATOR && useRandomAccess) {
			// Use indexed strategy
			int outIndex = 0;
			for(int i=midPoint; i<PASS_SIZE; i++) {
				int[] fromQueue = fromQueues[i];
				int length = fromQueueLengths[i];
				for(int j=0; j<length; j++) {
					int index = fromQueue[j];
					T number = USE_TO_ARRAY ? array[index] : list.get(index);
					list.set(outIndex++, number);
				}
			}
			for(int i=0; i<midPoint; i++) {
				int[] fromQueue = fromQueues[i];
				int length = fromQueueLengths[i];
				for(int j=0; j<length; j++) {
					int index = fromQueue[j];
					T number = USE_TO_ARRAY ? array[index] : list.get(index);
					list.set(outIndex++, number);
				}
			}
		} else {
			// Use iterator strategy
			ListIterator<T> iterator = list.listIterator();
			for(int i=midPoint; i<PASS_SIZE; i++) {
				int[] fromQueue = fromQueues[i];
				int length = fromQueueLengths[i];
				for(int j=0; j<length; j++) {
					int index = fromQueue[j];
					T number = USE_TO_ARRAY ? array[index] : list.get(index);
					iterator.next();
					iterator.set(number);
				}
			}
			for(int i=0; i<midPoint; i++) {
				int[] fromQueue = fromQueues[i];
				int length = fromQueueLengths[i];
				for(int j=0; j<length; j++) {
					int index = fromQueue[j];
					T number = USE_TO_ARRAY ? array[index] : list.get(index);
					iterator.next();
					iterator.set(number);
				}
			}
		}
    }

	private <T extends Number> void sortLarge(List<T> list, SortStatistics stats) {
		final int size = list.size();
		final boolean useRandomAccess = size<Integer.MAX_VALUE && (list instanceof RandomAccess);
		@SuppressWarnings("unchecked")
		List<T>[] fromQueues = (List<T>[])new List<?>[PASS_SIZE];
		@SuppressWarnings("unchecked")
		List<T>[] toQueues = (List<T>[])new List<?>[PASS_SIZE];
		if(!USE_LARGE_DELAYED_ALLOCATE) {
			for(int i=0; i<PASS_SIZE; i++) {
				fromQueues[i] = new ArrayList<T>();
				toQueues[i] = new ArrayList<T>();
			}
		}
		// Initial population of elements into fromQueues
		if(useRandomAccess) {
			// Each element is get and set once
			if(stats!=null) {
				stats.sortGetting(size);
				stats.sortSetting(size);
			}
			for(int i=0;i<size;i++) {
				T number = list.get(i);
				int queueNum = number.intValue() & PASS_MASK;
				List<T> fromQueue = fromQueues[queueNum];
				if(USE_LARGE_DELAYED_ALLOCATE && fromQueue==null) fromQueues[queueNum] = fromQueue = new ArrayList<T>();
				fromQueue.add(number);
			}
		} else {
			for(T number : list) {
				if(stats!=null) {
					stats.sortGetting();
					stats.sortSetting();
				}
				int queueNum = number.intValue() & PASS_MASK;
				List<T> fromQueue = fromQueues[queueNum];
				if(USE_LARGE_DELAYED_ALLOCATE && fromQueue==null) fromQueues[queueNum] = fromQueue = new ArrayList<T>();
				fromQueue.add(number);
			}
		}
		for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
			for(int i=0; i<PASS_SIZE; i++) {
				List<T> fromQueue = fromQueues[i];
				if(!USE_LARGE_DELAYED_ALLOCATE || fromQueue!=null) {
					for(T number : fromQueue) {
						int queueNum = (number.intValue() >>> shift) & PASS_MASK;
						List<T> toQueue = toQueues[queueNum];
						if(USE_LARGE_DELAYED_ALLOCATE && toQueue==null) toQueues[queueNum] = toQueue = new ArrayList<T>();
						toQueue.add(number);
					}
					fromQueue.clear(); // TODO: Faster if leave elements inside (no null referencesm, just change count field)?
				}
			}

			// Swap from and to
			List<T>[] temp = fromQueues;
			fromQueues = toQueues;
			toQueues = temp;
		}
		// Pick-up fromQueues and put into results, negative before positive to performed signed
		final int midPoint = PASS_SIZE>>>1;
		if(!FORCE_LIST_ITERATOR && useRandomAccess) {
			// Use indexed strategy
			int outIndex = 0;
			for(int i=midPoint; i<PASS_SIZE; i++) {
				List<T> fromQueue = fromQueues[i];
				if(!USE_LARGE_DELAYED_ALLOCATE || fromQueue!=null) {
					for(T number : fromQueue) {
						list.set(outIndex++, number);
					}
				}
			}
			for(int i=0; i<midPoint; i++) {
				List<T> fromQueue = fromQueues[i];
				if(!USE_LARGE_DELAYED_ALLOCATE || fromQueue!=null) {
					for(T number : fromQueue) {
						list.set(outIndex++, number);
					}
				}
			}
		} else {
			// Use iterator strategy
			ListIterator<T> iterator = list.listIterator();
			for(int i=midPoint; i<PASS_SIZE; i++) {
				List<T> fromQueue = fromQueues[i];
				if(!USE_LARGE_DELAYED_ALLOCATE || fromQueue!=null) {
					for(T number : fromQueue) {
						iterator.next();
						iterator.set(number);
					}
				}
			}
			for(int i=0; i<midPoint; i++) {
				List<T> fromQueue = fromQueues[i];
				if(!USE_LARGE_DELAYED_ALLOCATE || fromQueue!=null) {
					for(T number : fromQueue) {
						iterator.next();
						iterator.set(number);
					}
				}
			}
		}
    }

	@Override
    public <T extends Number> void sort(T[] array, SortStatistics stats) {
		IntegerRadixSort.getInstance().sort(array, stats);
    }

	@Override
    public void sort(int[] array, SortStatistics stats) {
		IntegerRadixSort.getInstance().sort(array, stats);
    }
}
