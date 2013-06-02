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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 *
 * @author  AO Industries, Inc.
 */
final public class IntegerRadixSort extends IntegerSortAlgorithm {

	/*
	private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
	private static final int PASS_SIZE = 1 << BITS_PER_PASS;
	private static final int PASS_MASK = PASS_SIZE - 1;
	 */

	/**
	 * When sorting lists less than this size, will use a different algorithm.
	 */
	private static final int MAX_BUBBLE_SORT_SIZE = 4;
	private static final int MAX_JAVA_SORT_SIZE = 128;

	private static final int MINIMUM_START_QUEUE_LENGTH = 16;

	private static final IntegerRadixSort instance = new IntegerRadixSort();

    public static IntegerRadixSort getInstance() {
        return instance;
    }

    private IntegerRadixSort() {
    }

	@Override
    public <T extends Number> void sort(List<T> list, SortStatistics stats) {
		if(stats!=null) stats.sortStarting();
		final int size = list.size();
		if(size <= MAX_BUBBLE_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			QubbleSort.bsort(list, 0, size-1, null, stats);
		} else if(size <= MAX_JAVA_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			Collections.sort(list, null);
		} else {
			final boolean useRandomAccess = size<Integer.MAX_VALUE && (list instanceof RandomAccess);
			// Dynamically choose pass size
			final int BITS_PER_PASS;
			/* Small case now handled by bubble sort and Java sort
			if(size <= 0x80) {
				BITS_PER_PASS = 4;
			} else*/ if(size < 0x80000) {
				BITS_PER_PASS = 8;
			} else {
				BITS_PER_PASS = 16; // Must be power of two and less than or equal to 32
			}
			final int PASS_SIZE = 1 << BITS_PER_PASS;
			final int PASS_MASK = PASS_SIZE - 1;

			// Determine the start queue length
			int startQueueLength = size >>> (BITS_PER_PASS-1); // Double the average size to allow for somewhat uneven distribution before growing arrays
			if(startQueueLength<MINIMUM_START_QUEUE_LENGTH) startQueueLength = MINIMUM_START_QUEUE_LENGTH;
			if(startQueueLength>size) startQueueLength = size;

			@SuppressWarnings("unchecked")
			T[][] fromQueues = (T[][])new Number[PASS_SIZE][];
			int[] fromQueueLengths = new int[PASS_SIZE];
			@SuppressWarnings("unchecked")
			T[][] toQueues = (T[][])new Number[PASS_SIZE][];
			int[] toQueueLengths = new int[PASS_SIZE];
			// Initial population of elements into fromQueues
			int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
			int bitsNotSeen = 0;
			if(useRandomAccess) {
				for(int i=0;i<size;i++) {
					T number = list.get(i);
					int numInt = number.intValue();
					bitsSeen |= numInt;
					bitsNotSeen |= numInt ^ 0xffffffff;
					int fromQueueNum = numInt & PASS_MASK;
					T[] fromQueue = fromQueues[fromQueueNum];
					int fromQueueLength = fromQueueLengths[fromQueueNum];
					if(fromQueue==null) {
						@SuppressWarnings("unchecked")
						T[] newQueue = (T[])new Number[startQueueLength];
						fromQueues[fromQueueNum] = fromQueue = newQueue;
					} else if(fromQueueLength>=fromQueue.length) {
						// Grow queue
						@SuppressWarnings("unchecked")
						T[] newQueue = (T[])new Number[fromQueueLength<<1];
						System.arraycopy(fromQueue, 0, newQueue, 0, fromQueueLength);
						fromQueues[fromQueueNum] = fromQueue = newQueue;
					}
					fromQueue[fromQueueLength++] = number;
					fromQueueLengths[fromQueueNum] = fromQueueLength;
				}
			} else {
				for(T number : list) {
					int numInt = number.intValue();
					bitsSeen |= numInt;
					bitsNotSeen |= (numInt ^ 0xffffffff);
					int fromQueueNum = numInt & PASS_MASK;
					T[] fromQueue = fromQueues[fromQueueNum];
					int fromQueueLength = fromQueueLengths[fromQueueNum];
					if(fromQueue==null) {
						@SuppressWarnings("unchecked")
						T[] newQueue = (T[])new Number[startQueueLength];
						fromQueues[fromQueueNum] = fromQueue = newQueue;
					} else if(fromQueueLength>=fromQueue.length) {
						// Grow queue
						@SuppressWarnings("unchecked")
						T[] newQueue = (T[])new Number[fromQueueLength<<1];
						System.arraycopy(fromQueue, 0, newQueue, 0, fromQueueLength);
						fromQueues[fromQueueNum] = fromQueue = newQueue;
					}
					fromQueue[fromQueueLength++] = number;
					fromQueueLengths[fromQueueNum] = fromQueueLength;
				}
			}
			bitsNotSeen ^= 0xffffffff;

			int lastShiftUsed = 0;
			for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
				// Skip this bit range when all values have equal bits.  For example
				// when going through the upper bits of lists of all smaller positive
				// or negative numbers.
				if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
					lastShiftUsed = shift;
					for(int fromQueueNum=0; fromQueueNum<PASS_SIZE; fromQueueNum++) {
						T[] fromQueue = fromQueues[fromQueueNum];
						if(fromQueue!=null) {
							int length = fromQueueLengths[fromQueueNum];
							for(int j=0; j<length; j++) {
								T number = fromQueue[j];
								int toQueueNum = (number.intValue() >>> shift) & PASS_MASK;
								T[] toQueue = toQueues[toQueueNum];
								int toQueueLength = toQueueLengths[toQueueNum];
								if(toQueue==null) {
									@SuppressWarnings("unchecked")
									T[] newQueue = (T[])new Number[startQueueLength];
									toQueues[toQueueNum] = toQueue = newQueue;
								} else if(toQueueLength>=toQueue.length) {
									// Grow queue
									@SuppressWarnings("unchecked")
									T[] newQueue = (T[])new Number[toQueueLength<<1];
									System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
									toQueues[toQueueNum] = toQueue = newQueue;
								}
								toQueue[toQueueLength++] = number;
								toQueueLengths[toQueueNum] = toQueueLength;
							}
							fromQueueLengths[fromQueueNum] = 0;
						}
					}

					// Swap from and to
					T[][] temp = fromQueues;
					fromQueues = toQueues;
					toQueues = temp;
					int[] tempLengths = fromQueueLengths;
					fromQueueLengths = toQueueLengths;
					toQueueLengths = tempLengths;
				}
			}
			// Pick-up fromQueues and put into results, negative before positive to performed as signed integers
			int midPoint = (lastShiftUsed+BITS_PER_PASS)==32 ? (PASS_SIZE>>>1) : 0;
			if(useRandomAccess) {
				// Use indexed strategy
				int outIndex = 0;
				for(int fromQueueNum=midPoint; fromQueueNum<PASS_SIZE; fromQueueNum++) {
					T[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							list.set(outIndex++, fromQueue[j]);
						}
					}
				}
				for(int fromQueueNum=0; fromQueueNum<midPoint; fromQueueNum++) {
					T[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							list.set(outIndex++, fromQueue[j]);
						}
					}
				}
			} else {
				// Use iterator strategy
				ListIterator<T> iterator = list.listIterator();
				for(int fromQueueNum=midPoint; fromQueueNum<PASS_SIZE; fromQueueNum++) {
					T[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							iterator.next();
							iterator.set(fromQueue[j]);
						}
					}
				}
				for(int fromQueueNum=0; fromQueueNum<midPoint; fromQueueNum++) {
					T[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							iterator.next();
							iterator.set(fromQueue[j]);
						}
					}
				}
			}
		}
		if(stats!=null) stats.sortEnding();
    }

	@Override
    public <T extends Number> void sort(T[] array, SortStatistics stats) {
		if(stats!=null) stats.sortStarting();
		final int size = array.length;
		if(size <= MAX_BUBBLE_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			QubbleSort.bsort(array, 0, size-1, null, stats);
		} else if(size <= MAX_JAVA_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			Arrays.sort(array, null);
		} else {
			// Dynamically choose pass size
			final int BITS_PER_PASS;
			/* Small case now handled by bubble sort and Java sort
			if(size <= 0x80) {
				BITS_PER_PASS = 4;
			} else*/ if(size < 0x80000) {
				BITS_PER_PASS = 8;
			} else {
				BITS_PER_PASS = 16; // Must be power of two and less than or equal to 32
			}
			final int PASS_SIZE = 1 << BITS_PER_PASS;
			final int PASS_MASK = PASS_SIZE - 1;

			// Determine the start queue length
			int startQueueLength = size >>> (BITS_PER_PASS-1); // Double the average size to allow for somewhat uneven distribution before growing arrays
			if(startQueueLength<MINIMUM_START_QUEUE_LENGTH) startQueueLength = MINIMUM_START_QUEUE_LENGTH;
			if(startQueueLength>size) startQueueLength = size;

			@SuppressWarnings("unchecked")
			T[][] fromQueues = (T[][])new Number[PASS_SIZE][];
			int[] fromQueueLengths = new int[PASS_SIZE];
			@SuppressWarnings("unchecked")
			T[][] toQueues = (T[][])new Number[PASS_SIZE][];
			int[] toQueueLengths = new int[PASS_SIZE];
			// Initial population of elements into fromQueues
			int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
			int bitsNotSeen = 0;
			for(int i=0;i<size;i++) {
				T number = array[i];
				int numInt = number.intValue();
				bitsSeen |= numInt;
				bitsNotSeen |= numInt ^ 0xffffffff;
				int fromQueueNum = numInt & PASS_MASK;
				T[] fromQueue = fromQueues[fromQueueNum];
				int fromQueueLength = fromQueueLengths[fromQueueNum];
				if(fromQueue==null) {
					@SuppressWarnings("unchecked")
					T[] newQueue = (T[])new Number[startQueueLength];
					fromQueues[fromQueueNum] = fromQueue = newQueue;
				} else if(fromQueueLength>=fromQueue.length) {
					// Grow queue
					@SuppressWarnings("unchecked")
					T[] newQueue = (T[])new Number[fromQueueLength<<1];
					System.arraycopy(fromQueue, 0, newQueue, 0, fromQueueLength);
					fromQueues[fromQueueNum] = fromQueue = newQueue;
				}
				fromQueue[fromQueueLength++] = number;
				fromQueueLengths[fromQueueNum] = fromQueueLength;
			}
			bitsNotSeen ^= 0xffffffff;

			int lastShiftUsed = 0;
			for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
				// Skip this bit range when all values have equal bits.  For example
				// when going through the upper bits of lists of all smaller positive
				// or negative numbers.
				if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
					lastShiftUsed = shift;
					for(int fromQueueNum=0; fromQueueNum<PASS_SIZE; fromQueueNum++) {
						T[] fromQueue = fromQueues[fromQueueNum];
						if(fromQueue!=null) {
							int length = fromQueueLengths[fromQueueNum];
							for(int j=0; j<length; j++) {
								T number = fromQueue[j];
								int toQueueNum = (number.intValue() >>> shift) & PASS_MASK;
								T[] toQueue = toQueues[toQueueNum];
								int toQueueLength = toQueueLengths[toQueueNum];
								if(toQueue==null) {
									@SuppressWarnings("unchecked")
									T[] newQueue = (T[])new Number[startQueueLength];
									toQueues[toQueueNum] = toQueue = newQueue;
								} else if(toQueueLength>=toQueue.length) {
									// Grow queue
									@SuppressWarnings("unchecked")
									T[] newQueue = (T[])new Number[toQueueLength<<1];
									System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
									toQueues[toQueueNum] = toQueue = newQueue;
								}
								toQueue[toQueueLength++] = number;
								toQueueLengths[toQueueNum] = toQueueLength;
							}
							fromQueueLengths[fromQueueNum] = 0;
						}
					}

					// Swap from and to
					T[][] temp = fromQueues;
					fromQueues = toQueues;
					toQueues = temp;
					int[] tempLengths = fromQueueLengths;
					fromQueueLengths = toQueueLengths;
					toQueueLengths = tempLengths;
				}
			}
			// Pick-up fromQueues and put into results, negative before positive to performed as signed integers
			int midPoint = (lastShiftUsed+BITS_PER_PASS)==32 ? (PASS_SIZE>>>1) : 0;
			// Use indexed strategy
			int outIndex = 0;
			for(int fromQueueNum=midPoint; fromQueueNum<PASS_SIZE; fromQueueNum++) {
				T[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
			}
			for(int fromQueueNum=0; fromQueueNum<midPoint; fromQueueNum++) {
				T[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
			}
		}
		if(stats!=null) stats.sortEnding();
    }

	@Override
    public void sort(int[] array, SortStatistics stats) {
		if(stats!=null) stats.sortStarting();
		final int size = array.length;
		if(size <= MAX_JAVA_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			Arrays.sort(array);
		} else {
			// Dynamically choose pass size
			final int BITS_PER_PASS;
			/* Small case now handled by Java sort
			if(size <= 0x80) {
				BITS_PER_PASS = 4;
			} else*/ if(size < 0x80000) {
				BITS_PER_PASS = 8;
			} else {
				BITS_PER_PASS = 16; // Must be power of two and less than or equal to 32
			}
			final int PASS_SIZE = 1 << BITS_PER_PASS;
			final int PASS_MASK = PASS_SIZE - 1;

			// Determine the start queue length
			int startQueueLength = size >>> (BITS_PER_PASS-1); // Double the average size to allow for somewhat uneven distribution before growing arrays
			if(startQueueLength<MINIMUM_START_QUEUE_LENGTH) startQueueLength = MINIMUM_START_QUEUE_LENGTH;
			if(startQueueLength>size) startQueueLength = size;

			int[][] fromQueues = new int[PASS_SIZE][];
			int[] fromQueueLengths = new int[PASS_SIZE];
			int[][] toQueues = new int[PASS_SIZE][];
			int[] toQueueLengths = new int[PASS_SIZE];
			// Initial population of elements into fromQueues
			int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
			int bitsNotSeen = 0;
			for(int i=0;i<size;i++) {
				int number = array[i];
				bitsSeen |= number;
				bitsNotSeen |= number ^ 0xffffffff;
				int fromQueueNum = number & PASS_MASK;
				int[] fromQueue = fromQueues[fromQueueNum];
				int fromQueueLength = fromQueueLengths[fromQueueNum];
				if(fromQueue==null) {
					int[] newQueue = new int[startQueueLength];
					fromQueues[fromQueueNum] = fromQueue = newQueue;
				} else if(fromQueueLength>=fromQueue.length) {
					// Grow queue
					int[] newQueue = new int[fromQueueLength<<1];
					System.arraycopy(fromQueue, 0, newQueue, 0, fromQueueLength);
					fromQueues[fromQueueNum] = fromQueue = newQueue;
				}
				fromQueue[fromQueueLength++] = number;
				fromQueueLengths[fromQueueNum] = fromQueueLength;
			}
			bitsNotSeen ^= 0xffffffff;

			int lastShiftUsed = 0;
			for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
				// Skip this bit range when all values have equal bits.  For example
				// when going through the upper bits of lists of all smaller positive
				// or negative numbers.
				if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
					lastShiftUsed = shift;
					for(int fromQueueNum=0; fromQueueNum<PASS_SIZE; fromQueueNum++) {
						int[] fromQueue = fromQueues[fromQueueNum];
						if(fromQueue!=null) {
							int length = fromQueueLengths[fromQueueNum];
							for(int j=0; j<length; j++) {
								int number = fromQueue[j];
								int toQueueNum = (number >>> shift) & PASS_MASK;
								int[] toQueue = toQueues[toQueueNum];
								int toQueueLength = toQueueLengths[toQueueNum];
								if(toQueue==null) {
									int[] newQueue = new int[startQueueLength];
									toQueues[toQueueNum] = toQueue = newQueue;
								} else if(toQueueLength>=toQueue.length) {
									// Grow queue
									int[] newQueue = new int[toQueueLength<<1];
									System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
									toQueues[toQueueNum] = toQueue = newQueue;
								}
								toQueue[toQueueLength++] = number;
								toQueueLengths[toQueueNum] = toQueueLength;
							}
							fromQueueLengths[fromQueueNum] = 0;
						}
					}

					// Swap from and to
					int[][] temp = fromQueues;
					fromQueues = toQueues;
					toQueues = temp;
					int[] tempLengths = fromQueueLengths;
					fromQueueLengths = toQueueLengths;
					toQueueLengths = tempLengths;
				}
			}
			// Pick-up fromQueues and put into results, negative before positive to performed as signed integers
			int midPoint = (lastShiftUsed+BITS_PER_PASS)==32 ? (PASS_SIZE>>>1) : 0;
			// Use indexed strategy
			int outIndex = 0;
			for(int fromQueueNum=midPoint; fromQueueNum<PASS_SIZE; fromQueueNum++) {
				int[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
			}
			for(int fromQueueNum=0; fromQueueNum<midPoint; fromQueueNum++) {
				int[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
			}
		}
		if(stats!=null) stats.sortEnding();
    }
}
