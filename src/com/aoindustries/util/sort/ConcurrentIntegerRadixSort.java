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

import com.aoindustries.util.AtomicSequence;
import com.aoindustries.util.Sequence;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 *
 * Although a very different implementation, this topic is discussed at
 * {@link http://erik.gorset.no/2011/04/radix-sort-is-faster-than-quicksort.html}
 * with source provided at {@link https://github.com/gorset/radix/blob/master/Radix.java}
 *
 * @author  AO Industries, Inc.
 */
final public class ConcurrentIntegerRadixSort extends IntegerSortAlgorithm {

	/*
	private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
	private static final int PASS_SIZE = 1 << BITS_PER_PASS;
	private static final int PASS_MASK = PASS_SIZE - 1;
	 */

	/**
	 * Concurrency controls.
	 */
	private static final int MIN_CONCURRENCY_SIZE = 1 << 5; // TODO: 1 << 16;
	private static final int THREADS_PER_PROCESSOR = 2;
	private static final int TASKS_PER_THREAD = 2;

	private static final int MINIMUM_START_QUEUE_LENGTH = 16;

	private static final ExecutorService executor = Executors.newFixedThreadPool(
		Runtime.getRuntime().availableProcessors() * THREADS_PER_PROCESSOR,
		new ThreadFactory() {
			private final Sequence idSequence = new AtomicSequence();
			public Thread newThread(Runnable target) {
				return new Thread(target, ConcurrentIntegerRadixSort.class.getName()+".executor: id=" + idSequence.getNextSequenceValue());
			}
		}
	);

	private static final ConcurrentIntegerRadixSort instance = new ConcurrentIntegerRadixSort();

    public static ConcurrentIntegerRadixSort getInstance() {
        return instance;
    }

    private ConcurrentIntegerRadixSort() {
    }

	@Override
    public <T extends Number> void sort(List<T> list, SortStatistics stats) {
		if(stats!=null) stats.sortSwitchingAlgorithms();
		IntegerRadixSort.getInstance().sort(list, stats);
    }

	@Override
    public <T extends Number> void sort(T[] array, SortStatistics stats) {
		if(stats!=null) stats.sortSwitchingAlgorithms();
		IntegerRadixSort.getInstance().sort(array, stats);
    }

	private static final class ImportStepResult {
		private final int bitsSeen;
		private final int bitsNotSeen;
		private ImportStepResult(int bitsSeen, int bitsNotSeen) {
			this.bitsSeen = bitsSeen;
			this.bitsNotSeen = bitsNotSeen;
		}
	}

	@Override
    public void sort(final int[] array, SortStatistics stats) {
		final int size = array.length;
		if(size<MIN_CONCURRENCY_SIZE) {
			if(stats!=null) stats.sortSwitchingAlgorithms();
			IntegerRadixSort.getInstance().sort(array, stats);
		} else {
			try {
				// Use concurrency
				if(stats!=null) stats.sortStarting();
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

				final Object[] queueLocks = new Object[PASS_SIZE];
				for(int i=0; i<PASS_SIZE; i++) queueLocks[i] = new Object();
				int[][] fromQueues = new int[PASS_SIZE][startQueueLength];
				int[] fromQueueLengths = new int[PASS_SIZE];
				int[][] toQueues = new int[PASS_SIZE][startQueueLength];
				int[] toQueueLengths = new int[PASS_SIZE];

				// Initial population of elements into fromQueues
				if(stats!=null) {
					// One get and one set for each element
					stats.sortGetting(size);
					stats.sortSetting(size);
				}

				// Determine size of division
				final int numTasks = Runtime.getRuntime().availableProcessors() * THREADS_PER_PROCESSOR * TASKS_PER_THREAD;
				final int sizePerThread;
				{
					int spt = size / numTasks;
					if((spt*numTasks)<size) spt++; // Round-up instead of down
					sizePerThread = spt;
				}

				// Perform each concurrently
				final List<Future<ImportStepResult>> importStepFutures = new ArrayList<Future<ImportStepResult>>(numTasks);
				for(int threadStart=0; threadStart<size; threadStart+=sizePerThread) {
					final int finalThreadStart = threadStart;
					final int[][] finalFromQueues = fromQueues;
					final int[] finalFromQueueLengths = fromQueueLengths;
					// TODO: Could perform the last one on the current thread, here and other places
					importStepFutures.add(
						executor.submit(
							new Callable<ImportStepResult>() {
								public ImportStepResult call() {
									int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
									int bitsNotSeen = 0;
									int threadEnd = finalThreadStart + sizePerThread;
									if(threadEnd > size) threadEnd = size;
									for(int i=finalThreadStart; i<threadEnd; i++) {
										int number = array[i];
										bitsSeen |= number;
										bitsNotSeen |= number ^ 0xffffffff;
										int fromQueueNum = number & PASS_MASK;
										synchronized(queueLocks[fromQueueNum]) {
											int[] fromQueue = finalFromQueues[fromQueueNum];
											int fromQueueLength = finalFromQueueLengths[fromQueueNum];
											if(fromQueueLength>=fromQueue.length) {
												// Grow queue
												int[] newQueue = new int[fromQueueLength<<1];
												System.arraycopy(fromQueue, 0, newQueue, 0, fromQueueLength);
												finalFromQueues[fromQueueNum] = fromQueue = newQueue;
											}
											fromQueue[fromQueueLength++] = number;
											finalFromQueueLengths[fromQueueNum] = fromQueueLength;
										}
									}
									return new ImportStepResult(
										bitsSeen,
										bitsNotSeen
									);
								}
							}
						)
					);
				}

				// Combine results
				int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
				int bitsNotSeen = 0;
				for(Future<ImportStepResult> importStepFuture : importStepFutures) {
					ImportStepResult result = importStepFuture.get();
					bitsSeen |= result.bitsSeen;
					bitsNotSeen |= result.bitsNotSeen;
				}
				bitsNotSeen ^= 0xffffffff;

				// Determine size of division of per-pass steps
				final int passSizePerThread;
				{
					int pspt = PASS_SIZE / numTasks;
					if((pspt*numTasks)<PASS_SIZE) pspt++; // Round-up instead of down
					passSizePerThread = pspt;
				}

				// Gather/scatter stage
				int lastShiftUsed = 0;
				for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
					// Skip this bit range when all values have equal bits.  For example
					// when going through the upper bits of lists of all smaller positive
					// or negative numbers.
					if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
						lastShiftUsed = shift;
						// Perform each concurrently
						final List<Future<?>> gatherScatterFutures = new ArrayList<Future<?>>(numTasks);
						for(int threadStart=0; threadStart<PASS_SIZE; threadStart+=passSizePerThread) {
							final int finalShift = shift;
							final int finalThreadStart = threadStart;
							final int[][] finalFromQueues = fromQueues;
							final int[] finalFromQueueLengths = fromQueueLengths;
							final int[][] finalToQueues = toQueues;
							final int[] finalToQueueLengths = toQueueLengths;
							// TODO: Could perform the last one on the current thread, here and other places
							gatherScatterFutures.add(
								executor.submit(
									new Runnable() {
										public void run() {
											int threadEnd = finalThreadStart + passSizePerThread;
											if(threadEnd > PASS_SIZE) threadEnd = PASS_SIZE;
											for(int fromQueueNum=finalThreadStart; fromQueueNum<threadEnd; fromQueueNum++) {
												int[] fromQueue = finalFromQueues[fromQueueNum];
												int length = finalFromQueueLengths[fromQueueNum];
												for(int j=0; j<length; j++) {
													int number = fromQueue[j];
													int toQueueNum = (number >>> finalShift) & PASS_MASK;
													synchronized(queueLocks[toQueueNum]) {
														int[] toQueue = finalToQueues[toQueueNum];
														int toQueueLength = finalToQueueLengths[toQueueNum];
														if(toQueueLength>=toQueue.length) {
															// Grow queue
															int[] newQueue = new int[toQueueLength<<1];
															System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
															finalToQueues[toQueueNum] = toQueue = newQueue;
														}
														toQueue[toQueueLength++] = number;
														finalToQueueLengths[toQueueNum] = toQueueLength;
													}
												}
												finalFromQueueLengths[fromQueueNum] = 0;
											}
										}
									}
								)
							);
						}
						// Wait for each gather/scatter task to complete
						for(Future<?> gatherScatterFuture : gatherScatterFutures) {
							gatherScatterFuture.get();
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
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
				for(int fromQueueNum=0; fromQueueNum<midPoint; fromQueueNum++) {
					int[] fromQueue = fromQueues[fromQueueNum];
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
				if(stats!=null) stats.sortEnding();
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			} catch(ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
    }
}
