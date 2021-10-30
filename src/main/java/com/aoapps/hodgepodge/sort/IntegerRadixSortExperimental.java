/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2018, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.sort;

import com.aoapps.collections.IntList;
import com.aoapps.lang.RuntimeUtils;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.lang.util.AtomicSequence;
import com.aoapps.lang.util.Sequence;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 * TODO: Consider thread-local for the small sort space
 *
 * @author  AO Industries, Inc.
 */
public final class IntegerRadixSortExperimental extends BaseIntegerSortAlgorithm {

	private static final int MIN_RADIX_SORT_SIZE = 1 << 8;

	private static final int FIRST_BITS_PER_PASS = 4; // Must be power of two and less than or equal to 32
	//private static final int FIRST_PASS_SIZE = 1 << FIRST_BITS_PER_PASS;
	//private static final int FIRST_PASS_MASK = FIRST_PASS_SIZE - 1;

	private static final int R_BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
	//private static final int R_PASS_SIZE = 1 << R_BITS_PER_PASS;
	//private static final int R_PASS_MASK = R_PASS_SIZE - 1;

	private static final boolean ENABLE_CONCURRENCY = true; // TODO: Concurrent broken currently when test has many values in 0-255 range
	private static final int MIN_CONCURRENCY_SIZE = 1 << 9; // TODO: 1 << 16

	private static final ExecutorService executor = !ENABLE_CONCURRENCY ? null : Executors.newFixedThreadPool(
		RuntimeUtils.getAvailableProcessors(),
		new ThreadFactory() {
			private final Sequence idSequence = new AtomicSequence();
			@Override
			public Thread newThread(Runnable target) {
				return new Thread(target, IntegerRadixSortExperimental.class.getName()+".executor: id=" + idSequence.getNextSequenceValue());
			}
		}
	);

	private static final IntegerRadixSortExperimental instance = new IntegerRadixSortExperimental();

	public static IntegerRadixSortExperimental getInstance() {
		return instance;
	}

	private IntegerRadixSortExperimental() {
	}

	@Override
	public boolean isStable() {
		return true; // Since it only deals with primitives, this can be true, if ported to objects this may no longer be the case.
	}

	@Override
	public <N extends Number> void sort(List<N> list, SortStatistics stats) {
		IntegerRadixSort.getInstance().sort(list, stats);
	}

	@Override
	public <N extends Number> void sort(N[] array, SortStatistics stats) {
		IntegerRadixSort.getInstance().sort(array, stats);
	}

	@Override
	public void sort(IntList list, SortStatistics stats) {
		IntegerRadixSort.getInstance().sort(list, stats);
	}

	@Override
	public void sort(int[] array, SortStatistics stats) {
		if(stats != null) stats.sortStarting();
			if(array.length < MIN_RADIX_SORT_SIZE) {
				if(stats != null) stats.sortSwitchingAlgorithms();
				Arrays.sort(array);
			} else {
			if(ENABLE_CONCURRENCY) {
				Queue<Future<?>> futures = new ConcurrentLinkedQueue<>();
				sort(array, 0, array.length, 32-FIRST_BITS_PER_PASS, futures);
				try {
					while(!futures.isEmpty()) {
						futures.remove().get();
					}
				} catch(InterruptedException e) {
					// Restore the interrupted status
					Thread.currentThread().interrupt();
					throw new WrappedException(e);
				} catch(ExecutionException e) {
					throw new WrappedException(e);
				}
			} else {
				sort(array, 0, array.length, 32-R_BITS_PER_PASS, null);
			}
		}
		if(stats != null) stats.sortEnding();
	}

	private static final int UNSIGNED_OFFSET = 0x80000000;

	// From https://github.com/gorset/radix/blob/master/Radix.java
	public static void sort(final int[] array, int offset, int end, int shift, final Queue<Future<?>> futures) {
		final int BITS_PER_PASS;
		if(ENABLE_CONCURRENCY && (shift + R_BITS_PER_PASS)==32) {
			BITS_PER_PASS = FIRST_BITS_PER_PASS;
		} else {
			BITS_PER_PASS = R_BITS_PER_PASS;
		}
		final int PASS_SIZE = 1 << BITS_PER_PASS;
		final int PASS_MASK = PASS_SIZE - 1;

		int[] last = new int[PASS_SIZE];
		final int[] pointer = new int[PASS_SIZE];

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
			// TODO: Additional criteria
			shift -= BITS_PER_PASS;
			for (int x=0; x<PASS_SIZE; ++x) {
				final int size = x > 0 ? (pointer[x] - pointer[x-1]) : (pointer[0] - offset);
				if (size > 64) {
					final int newOffset = pointer[x] - size;
					final int newEnd = pointer[x];
					if(
						ENABLE_CONCURRENCY
						&& (newEnd-newOffset) >= MIN_CONCURRENCY_SIZE
					) {
						final int finalShift = shift;
						futures.add(
							executor.submit(
								() -> sort(array, newOffset, newEnd, finalShift, futures)
							)
						);
					} else {
						sort(array, newOffset, newEnd, shift, futures);
					}
				} else if (size > 1) {
					insertionSort(array, pointer[x] - size, pointer[x]);
					// Arrays.sort(array, pointer[x] - size, pointer[x]);
				}
			}
		}
	}

	// From https://github.com/gorset/radix/blob/master/Radix.java
	private static void insertionSort(int[] array, int offset, int end) {
		for (int x=offset; x<end; ++x) {
			for (int y=x; y>offset && array[y-1]>array[y]; y--) {
				int temp = array[y];
				array[y] = array[y-1];
				array[y-1] = temp;
			}
		}
	}
}
