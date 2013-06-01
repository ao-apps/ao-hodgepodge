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
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 *
 * @author  AO Industries, Inc.
 */
final public class IntegerRadixSort extends SortAlgorithm<Number> {

	private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
	private static final int PASS_SIZE = 1 << BITS_PER_PASS;
	private static final int PASS_MASK = PASS_SIZE - 1;

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
		final boolean useRandomAccess = size<Integer.MAX_VALUE && (list instanceof RandomAccess);
		@SuppressWarnings("unchecked")
		List<T>[] fromQueues = (List<T>[])new List<?>[PASS_SIZE];
		@SuppressWarnings("unchecked")
		List<T>[] toQueues = (List<T>[])new List<?>[PASS_SIZE];
		//for(int i=0; i<PASS_SIZE; i++) {
		//	fromQueues[i] = new ArrayList<T>();
		//	toQueues[i] = new ArrayList<T>();
		//}
		// Initial population of elements into fromQueues
		if(useRandomAccess) {
			for(int i=0;i<size;i++) {
				T number = list.get(i);
				int queueNum = number.intValue() & PASS_MASK;
				List<T> fromQueue = fromQueues[queueNum];
				if(fromQueue==null) fromQueues[queueNum] = fromQueue = new ArrayList<T>();
				fromQueue.add(number);
			}
		} else {
			for(T number : list) {
				int queueNum = number.intValue() & PASS_MASK;
				List<T> fromQueue = fromQueues[queueNum];
				if(fromQueue==null) fromQueues[queueNum] = fromQueue = new ArrayList<T>();
				fromQueue.add(number);
			}
		}
		for(
			int shift=BITS_PER_PASS, mask=PASS_MASK<<BITS_PER_PASS;
			shift<32;
			shift += BITS_PER_PASS, mask<<=BITS_PER_PASS
		) {
			for(int i=0; i<PASS_SIZE; i++) {
				List<T> fromQueue = fromQueues[i];
				if(fromQueue!=null) {
					for(T number : fromQueue) {
						int queueNum = (number.intValue() & mask) >>> shift;
						List<T> toQueue = toQueues[queueNum];
						if(toQueue==null) toQueues[queueNum] = toQueue = new ArrayList<T>();
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
		if(useRandomAccess) {
			// Use indexed strategy
			int outIndex = 0;
			for(int i=midPoint; i<PASS_SIZE; i++) {
				List<T> fromQueue = fromQueues[i];
				if(fromQueue!=null) {
					for(T number : fromQueue) {
						list.set(outIndex++, number);
					}
				}
			}
			for(int i=0; i<midPoint; i++) {
				List<T> fromQueue = fromQueues[i];
				if(fromQueue!=null) {
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
				if(fromQueue!=null) {
					for(T number : fromQueue) {
						iterator.next();
						iterator.set(number);
					}
				}
			}
			for(int i=0; i<midPoint; i++) {
				List<T> fromQueue = fromQueues[i];
				if(fromQueue!=null) {
					for(T number : fromQueue) {
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
