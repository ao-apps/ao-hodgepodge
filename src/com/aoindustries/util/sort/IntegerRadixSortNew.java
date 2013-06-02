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
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 *
 * @author  AO Industries, Inc.
 */
final public class IntegerRadixSortNew extends SortAlgorithm<Number> {

	/*
	private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
	private static final int PASS_SIZE = 1 << BITS_PER_PASS;
	private static final int PASS_MASK = PASS_SIZE - 1;
	 */

	private static final int MINIMUM_START_QUEUE_LENGTH = 16;

	private static final IntegerRadixSortNew instance = new IntegerRadixSortNew();

    public static IntegerRadixSortNew getInstance() {
        return instance;
    }

    private IntegerRadixSortNew() {
    }

	@Override
    public <T extends Number> void sort(List<T> list, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
		final int size = list.size();
		final boolean useRandomAccess = size<Integer.MAX_VALUE && (list instanceof RandomAccess);
		// Dynamically choose pass size
		final int BITS_PER_PASS;
		if(size <= 0x80) {
			BITS_PER_PASS = 4;
		} else if(size <= 0x20000) {
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
		//for(int i=0; i<PASS_SIZE; i++) {
		//	fromQueues[i] = new ArrayList<T>();
		//	toQueues[i] = new ArrayList<T>();
		//}
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
				if(fromQueue==null) fromQueues[fromQueueNum] = fromQueue = (T[])new Number[startQueueLength];
				else if(fromQueueLength>=fromQueue.length) {
					// Grow queue
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
				if(fromQueue==null) fromQueues[fromQueueNum] = fromQueue = (T[])new Number[startQueueLength];
				else if(fromQueueLength>=fromQueue.length) {
					// Grow queue
					T[] newQueue = (T[])new Number[fromQueueLength<<1];
					System.arraycopy(fromQueue, 0, newQueue, 0, fromQueueLength);
					fromQueues[fromQueueNum] = fromQueue = newQueue;
				}
				fromQueue[fromQueueLength++] = number;
				fromQueueLengths[fromQueueNum] = fromQueueLength;
			}
		}
		bitsNotSeen ^= 0xffffffff;
		//System.out.println("bitsSeen    = " + Integer.toString(bitsSeen, 16));
		//System.out.println("bitsNotSeen = " + Integer.toString(bitsNotSeen, 16));

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
							if(toQueue==null) toQueues[toQueueNum] = toQueue = (T[])new Number[startQueueLength];
							else if(toQueueLength>=toQueue.length) {
								// Grow queue
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
			} else {
				//System.err.println("Skipping bit range: shift="+shift);
			}
		}
		// Pick-up fromQueues and put into results, negative before positive to performed signed
		int midPoint = (lastShiftUsed+BITS_PER_PASS)==32 ? (PASS_SIZE>>>1) : 0;
		if(useRandomAccess) {
			// Use indexed strategy
			int outIndex = 0;
			for(int fromQueueNum=midPoint; fromQueueNum<PASS_SIZE; fromQueueNum++) {
				T[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					for(int j=0; j<length; j++) {
						T number = fromQueue[j];
						list.set(outIndex++, number);
					}
				}
			}
			for(int fromQueueNum=0; fromQueueNum<midPoint; fromQueueNum++) {
				T[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					for(int j=0; j<length; j++) {
						T number = fromQueue[j];
						list.set(outIndex++, number);
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
						T number = fromQueue[j];
						iterator.next();
						iterator.set(number);
					}
				}
			}
			for(int fromQueueNum=0; fromQueueNum<midPoint; fromQueueNum++) {
				T[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					for(int j=0; j<length; j++) {
						T number = fromQueue[j];
						iterator.next();
						iterator.set(number);
					}
				}
			}
		}
		if(stats!=null) stats.sortEnding();
    }

	@Override
    public <T extends Number> void sort(T[] array, SortStatistics stats) {
        if(stats!=null) stats.sortStarting();
		if(true) throw new NotImplementedException("TODO: Implement method");
        if(stats!=null) stats.sortEnding();
    }
}
