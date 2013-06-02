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

import java.util.List;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 * TODO: Consider thread-local for the small sort space
 *
 * @author  AO Industries, Inc.
 */
final public class IntegerRadixSortExperimental extends IntegerSortAlgorithm {

	private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
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
		IntegerRadixSort.getInstance().sort(list, stats);
    }

	@Override
    public <T extends Number> void sort(T[] array, SortStatistics stats) {
		IntegerRadixSort.getInstance().sort(array, stats);
    }

	@Override
    public void sort(int[] array, SortStatistics stats) {
		if(stats!=null) stats.sortStarting();
		sort(array, 0, array.length, 32-BITS_PER_PASS);
		if(stats!=null) stats.sortEnding();
    }

	private static final int UNSIGNED_OFFSET = 0x80000000;

	// From https://github.com/gorset/radix/blob/master/Radix.java
	public static void sort(int[] array, int offset, int end, int shift) {
		int[] last = new int[PASS_SIZE];
		int[] pointer = new int[PASS_SIZE];

		for (int x=offset; x<end; ++x) {
			++last[((array[x]+UNSIGNED_OFFSET) >> shift) & PASS_MASK];
		}

		last[0] += offset;
		pointer[0] = offset;
		for (int x=1; x<PASS_SIZE; ++x) {
			pointer[x] = last[x-1];
			last[x] += last[x-1];
		}

		for (int x=0; x<PASS_SIZE; ++x) {
			while (pointer[x] != last[x]) {
				int value = array[pointer[x]];
				int y = ((value+UNSIGNED_OFFSET) >> shift) & PASS_MASK;
				while (x != y) {
					int temp = array[pointer[y]];
					array[pointer[y]++] = value;
					value = temp;
					y = ((value+UNSIGNED_OFFSET) >> shift) & PASS_MASK;
				}
				array[pointer[x]++] = value;
			}
		}
		if (shift > 0) {
			shift -= BITS_PER_PASS;
			for (int x=0; x<PASS_SIZE; ++x) {
				int size = x > 0 ? pointer[x] - pointer[x-1] : pointer[0] - offset;
				if (size > 64) {
					sort(array, pointer[x] - size, pointer[x], shift);
				} else if (size > 1) {
					insertionSort(array, pointer[x] - size, pointer[x]);
					// Arrays.sort(array, pointer[x] - size, pointer[x]);
				}
			}
		}
	}

	// From https://github.com/gorset/radix/blob/master/Radix.java
	private static void insertionSort(int array[], int offset, int end) {
		for (int x=offset; x<end; ++x) {
			for (int y=x; y>offset && array[y-1]>array[y]; y--) {
				int temp = array[y];
				array[y] = array[y-1];
				array[y-1] = temp;
			}
		}
	}
}
