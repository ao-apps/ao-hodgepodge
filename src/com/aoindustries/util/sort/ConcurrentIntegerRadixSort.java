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
import com.aoindustries.util.concurrent.ConcurrentUtils;
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

	private static final boolean USE_CONCURRENT_IMPORT = true;
	private static final boolean USE_CONCURRENT_GATHER_SCATTER = true;
	private static final boolean USE_CONCURRENT_EXPORT = true;

	private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
	private static final int PASS_SIZE = 1 << BITS_PER_PASS;
	private static final int PASS_MASK = PASS_SIZE - 1;

	/**
	 * When there are fewer than MIN_CONCURRENCY_SIZE elements,
	 * the single-threaded implementation is used.
	 */
	private static final int MIN_CONCURRENCY_SIZE = 1 << 16; // TODO: Find break-even point, might also depend on the number of processors

	/**
	 * Where there are fewer than MIN_CONCURRENCY_PROCESSORS available processors,
	 * the single-threaded implementation is used.
	 */
	private static final int MIN_CONCURRENCY_PROCESSORS = 2;

	/**
	 * The number of threads that will be in the thread pool per available processor.
	 */
	private static final int THREADS_PER_PROCESSOR = 1;

	/**
	 * The number of tasks that will be submitted to the thread pool per thread.
	 */
	private static final int TASKS_PER_THREAD = 2;

	/**
	 * The minimum starting queue length (unless the size of the passed-in list is smaller)
	 */
	private static final int MINIMUM_START_QUEUE_LENGTH = 16;

	/**
	 * <p>
	 * The call to Runtime.availableProcessors() is particularly expensive.  This simple
	 * approach assumes that the number of available processors will not change over the
	 * life span of the Java virtual machine.  This assumption, however, is not accurate
	 * in all cases.  This should probably be replaced by a polling strategy to occasionally
	 * check the new number of processors and adjust the executor service accordingly.
	 * </p>
	 * </p>
	 * However, the sort should not be affected too adversely if it gets the number of processors
	 * wrong.  If the number of processors is reduced, it just adds a few more context switches
	 * due to extra threads.  If the number of processors is increased, the additional processors
	 * will simply not be used.
	 * </p>
	 */
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

	private static final ExecutorService defaultExecutor = Executors.newFixedThreadPool(
		AVAILABLE_PROCESSORS * THREADS_PER_PROCESSOR,
		new ThreadFactory() {
			private final Sequence idSequence = new AtomicSequence();
			public Thread newThread(Runnable target) {
				long id = idSequence.getNextSequenceValue();
				return new Thread(target, ConcurrentIntegerRadixSort.class.getName()+".defaultExecutor: id=" + id);
			}
		}
	);

	private static final ConcurrentIntegerRadixSort defaultInstance = new ConcurrentIntegerRadixSort(defaultExecutor);

	/**
	 * Gets the default ConcurrentIntegerRadixSort using the default executor
	 * service.  The default executor services scales the number of threads to
	 * be in proportion to the number of available processors.
	 */
    public static ConcurrentIntegerRadixSort getInstance() {
        return defaultInstance;
    }

	/**
	 * Gets a ConcurrentIntegerRadixSort that uses the provided ExecutorService.
	 */
	public static ConcurrentIntegerRadixSort getInstance(ExecutorService executor) {
		return new ConcurrentIntegerRadixSort(executor);
	}

	private final ExecutorService executor;
    private ConcurrentIntegerRadixSort(ExecutorService executor) {
		this.executor = executor;
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

	private static ImportStepResult importStep(
		final int PASS_MASK,
		final int startQueueLength,
		final int[][] taskFromQueues,
		final int[] taskFromQueueLengths,
		final int[] array,
		final int start,
		final int end
	) {
		int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
		int bitsNotSeen = 0;
		for(int i=start; i<end; i++) {
			int number = array[i];
			bitsSeen |= number;
			bitsNotSeen |= number ^ 0xffffffff;
			int fromQueueNum = number & PASS_MASK;
			int[] fromQueue = taskFromQueues[fromQueueNum];
			int fromQueueLength = taskFromQueueLengths[fromQueueNum];
			if(fromQueue==null) {
				int[] newQueue = new int[startQueueLength];
				taskFromQueues[fromQueueNum] = fromQueue = newQueue;
			} else if(fromQueueLength>=fromQueue.length) {
				// Grow queue
				int[] newQueue = new int[fromQueueLength<<1];
				System.arraycopy(fromQueue, 0, newQueue, 0, fromQueueLength);
				taskFromQueues[fromQueueNum] = fromQueue = newQueue;
			}
			fromQueue[fromQueueLength++] = number;
			taskFromQueueLengths[fromQueueNum] = fromQueueLength;
		}
		return new ImportStepResult(
			bitsSeen,
			bitsNotSeen
		);
	}

	/**
	 * Gather/scatter shared by both concurrent and single-threaded implementations
	 */
	private static void gatherScatter(
		final int numTasks,
		final int PASS_MASK,
		final int startQueueLength,
		final int[][][] fromQueues,
		final int[][] fromQueueLengths,
		final int[][][] toQueues,
		final int[][] toQueueLengths,
		final int shift,
		final int fromQueueNum,
		final int toTaskNum
	) {
		for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
			final int[][] taskFromQueues = fromQueues[fromTaskNum];
			int[] fromQueue = taskFromQueues[fromQueueNum];
			if(fromQueue!=null) {
				final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
				final int[][] taskToQueues = toQueues[toTaskNum];
				final int[] taskToQueueLengths = toQueueLengths[toTaskNum];
				int length = taskFromQueueLengths[fromQueueNum];
				for(int j=0; j<length; j++) {
					int number = fromQueue[j];
					int toQueueNum = (number >>> shift) & PASS_MASK;
					int[] toQueue = taskToQueues[toQueueNum];
					int toQueueLength = taskToQueueLengths[toQueueNum];
					if(toQueue==null) {
						int[] newQueue = new int[startQueueLength];
						taskToQueues[toQueueNum] = toQueue = newQueue;
					} else if(toQueueLength>=toQueue.length) {
						// Grow queue
						int[] newQueue = new int[toQueueLength<<1];
						System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
						taskToQueues[toQueueNum] = toQueue = newQueue;
					}
					toQueue[toQueueLength++] = number;
					taskToQueueLengths[toQueueNum] = toQueueLength;
				}
				taskFromQueueLengths[fromQueueNum] = 0;
			}
		}
	}

	/**
	 * Export shared by both concurrent and single-threaded implementations
	 */
	private static void export(
		final int numTasks,
		final int PASS_MASK,
		final int[][][] fromQueues,
		final int[][] fromQueueLengths,
		final int fromQueueStart,
		final int fromQueueEnd,
		final int[] outArray,
		int outIndex
	) {
		int fromQueueNum = fromQueueStart;
		do {
			for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
				final int[][] taskFromQueues = fromQueues[fromTaskNum];
				int[] fromQueue = taskFromQueues[fromQueueNum];
				if(fromQueue!=null) {
					final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
					int length = taskFromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, outArray, outIndex, length);
					outIndex += length;
				}
			}
		} while(
			(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
			!= fromQueueEnd
		);
	}

	@Override
    public void sort(final int[] array, SortStatistics stats) {
		final int size = array.length;
		final int numProcessors = AVAILABLE_PROCESSORS;
		if(size<MIN_CONCURRENCY_SIZE || numProcessors<MIN_CONCURRENCY_PROCESSORS) {
			if(stats!=null) stats.sortSwitchingAlgorithms();
			IntegerRadixSort.getInstance().sort(array, stats);
		} else {
			try {
				// Use concurrency
				if(stats!=null) stats.sortStarting();

				// Determine the number of tasks to divide work between
				final int numTasks = numProcessors * THREADS_PER_PROCESSOR * TASKS_PER_THREAD;

				// Determine size of division
				final int sizePerTask;
				{
					int spt = size / numTasks;
					if((spt*numTasks)<size) spt++; // Round-up instead of down
					sizePerTask = spt;
				}

				/*
				// Dynamically choose pass size
				final int BITS_PER_PASS;
				 */
				/* Small case now handled by bubble sort and Java sort
				if(sizePerTask <= 0x80) {
					BITS_PER_PASS = 4;
				} else*/ /*if(sizePerTask < 0x80000) {
					BITS_PER_PASS = 8;
				} else {
					BITS_PER_PASS = 16; // Must be power of two and less than or equal to 32
				}
				 */
				/*
				final int PASS_SIZE = 1 << BITS_PER_PASS;
				final int PASS_MASK = PASS_SIZE - 1;
				 */

				// Determine the start queue length
				final int startQueueLength;
				{
					int sql = (size >>> (BITS_PER_PASS-1)) / numTasks; // Double the average size to allow for somewhat uneven distribution before growing arrays
					if(sql<MINIMUM_START_QUEUE_LENGTH) sql = MINIMUM_START_QUEUE_LENGTH;
					if(sql>size) sql = size;
					startQueueLength = sql;
				}

				int[][][] fromQueues       = new int[numTasks][PASS_SIZE][];
				int[][]   fromQueueLengths = new int[numTasks][PASS_SIZE];
				int[][][] toQueues         = new int[numTasks][PASS_SIZE][];
				int[][]   toQueueLengths   = new int[numTasks][PASS_SIZE];

				// Initial population of elements into fromQueues
				int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
				int bitsNotSeen = 0;
				if(stats!=null) {
					// There will be only one get and one set for each element
					stats.sortGetting(size);
					stats.sortSetting(size);
				}
				if(USE_CONCURRENT_IMPORT) {
					// Perform each concurrently
					final List<Future<ImportStepResult>> importStepFutures = new ArrayList<Future<ImportStepResult>>(numTasks);
					for(
						int taskStart=0, fromTaskNum=0;
						taskStart<size;
						taskStart+=sizePerTask, fromTaskNum++
					) {
						int taskEnd = taskStart + sizePerTask;
						if(taskEnd > size) taskEnd = size;
						final int finalTaskStart = taskStart;
						final int finalTaskEnd = taskEnd;
						final int[][] taskFromQueues = fromQueues[fromTaskNum];
						final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
						importStepFutures.add(
							executor.submit(
								new Callable<ImportStepResult>() {
									public ImportStepResult call() {
										return importStep(
											PASS_MASK,
											startQueueLength,
											taskFromQueues,
											taskFromQueueLengths,
											array,
											finalTaskStart,
											finalTaskEnd
										);
									}
								}
							)
						);
					}

					// Combine results
					for(Future<ImportStepResult> importStepFuture : importStepFutures) {
						ImportStepResult result = importStepFuture.get();
						bitsSeen |= result.bitsSeen;
						bitsNotSeen |= result.bitsNotSeen;
					}
				} else {
					final int fromTaskNum = 0; // Task #0 for non concurrent import
					ImportStepResult result = importStep(
						PASS_MASK,
						startQueueLength,
						fromQueues[fromTaskNum],
						fromQueueLengths[fromTaskNum],
						array,
						0,
						size
					);
					bitsSeen |= result.bitsSeen;
					bitsNotSeen |= result.bitsNotSeen;
				}
				bitsNotSeen ^= 0xffffffff;

				// The same futures list is used by multiple stages below
				final List<Future<?>> runnableFutures = new ArrayList<Future<?>>(numTasks);

				// Gather/scatter stage
				int lastShiftUsed = 0;
				for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
					// Skip this bit range when all values have equal bits.  For example
					// when going through the upper bits of lists of all smaller positive
					// or negative numbers.
					if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
						lastShiftUsed = shift;

						if(USE_CONCURRENT_GATHER_SCATTER) {
							// Get some final references for anonymous inner class
							final int[][][] finalFromQueues = fromQueues;
							final int[][] finalFromQueueLengths = fromQueueLengths;
							final int[][][] finalToQueues = toQueues;
							final int[][] finalToQueueLengths = toQueueLengths;
							final int finalShift = shift;
							// Perform each concurrently with balanced concurrency
							int toTaskNum = 0;
							int taskFromQueueStart = 0;
							int taskTotalLength = 0;
							for(int fromQueueNum=0; fromQueueNum<PASS_SIZE; fromQueueNum++) {
								for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
									taskTotalLength += fromQueueLengths[fromTaskNum][fromQueueNum];
								}
								if(
									taskTotalLength>0 // Skip no output, such as all handle in previous tasks
									&& (
										taskTotalLength >= sizePerTask // Found fair share (or more)
										|| fromQueueNum==PASS_MASK // or is last task
									)
								) {
									final int finalToTaskNum = toTaskNum;
									final int finalTaskFromQueueStart = taskFromQueueStart;
									final int taskFromQueueEnd = fromQueueNum + 1;
									// Gather/scatter concurrent
									runnableFutures.add(
										executor.submit(
											new Runnable() {
												@Override
												public void run() {
													for(int fromQueueNum=finalTaskFromQueueStart; fromQueueNum<taskFromQueueEnd; fromQueueNum++) {
														gatherScatter(
															numTasks,
															PASS_MASK,
															startQueueLength,
															finalFromQueues,
															finalFromQueueLengths,
															finalToQueues,
															finalToQueueLengths,
															finalShift,
															fromQueueNum,
															finalToTaskNum
														);
													}
												}
											}
										)
									);

									// Reset to next task
									toTaskNum++;
									taskFromQueueStart = taskFromQueueEnd;
									taskTotalLength = 0;
								}
							}
							// Wait for each gather/scatter task to complete
							ConcurrentUtils.waitForAll(runnableFutures);
							if(USE_CONCURRENT_EXPORT) runnableFutures.clear(); // Clear when not last concurrent step
						} else {
							for(int fromQueueNum=0; fromQueueNum<PASS_SIZE; fromQueueNum++) {
								gatherScatter(
									numTasks,
									PASS_MASK,
									startQueueLength,
									fromQueues,
									fromQueueLengths,
									toQueues,
									toQueueLengths,
									shift,
									fromQueueNum,
									0 // Task #0 for non concurrent gather/scatter
								);
							}
						}

						// Swap from and to
						int[][][] temp = fromQueues;
						fromQueues = toQueues;
						toQueues = temp;
						int[][] tempLengths = fromQueueLengths;
						fromQueueLengths = toQueueLengths;
						toQueueLengths = tempLengths;
					}
				}
				// Pick-up fromQueues and put into results, negative before positive to performed as signed integers
				// TODO: Concurrently put into results, computing beginning positions for each thread by adding up lengths
				final int fromQueueStart = (lastShiftUsed+BITS_PER_PASS)==32 ? (PASS_SIZE>>>1) : 0;
				if(USE_CONCURRENT_EXPORT) {
					// Get some final references for anonymous inner class
					final int[][][] finalFromQueues = fromQueues;
					final int[][] finalFromQueueLengths = fromQueueLengths;
					// Use indexed strategy with balanced concurrency
					final int fromQueueLast = (fromQueueStart-1) & PASS_MASK;
					int taskFromQueueStart = fromQueueStart;
					int taskOutIndex = 0;
					int taskTotalLength = 0;
					int fromQueueNum = fromQueueStart;
					do {
						for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
							taskTotalLength += fromQueueLengths[fromTaskNum][fromQueueNum];
						}
						if(
							taskTotalLength>0 // Skip no output, such as all handle in previous tasks
							&& (
								taskTotalLength >= sizePerTask // Found fair share (or more)
								|| fromQueueNum==fromQueueLast // or is last task
							)
						) {
							final int finalTaskFromQueueStart = taskFromQueueStart;
							final int finalTaskOutIndex = taskOutIndex;
							final int taskFromQueueEnd = (fromQueueNum + 1) & PASS_MASK;
							// Queue concurrent
							runnableFutures.add(
								executor.submit(
									new Runnable() {
										@Override
										public void run() {
											export(
												numTasks,
												PASS_MASK,
												finalFromQueues,
												finalFromQueueLengths,
												finalTaskFromQueueStart,
												taskFromQueueEnd,
												array,
												finalTaskOutIndex
											);
										}
									}
								)
							);

							// Reset to next task
							taskFromQueueStart = taskFromQueueEnd;
							taskOutIndex += taskTotalLength;
							taskTotalLength = 0;
						}
					} while(
						(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
						!= fromQueueStart
					);
					// Wait for each export task to complete
					ConcurrentUtils.waitForAll(runnableFutures);
					// This is the last stage, not needed: runnableFutures.clear()
				} else {
					// Use indexed strategy, single-threaded
					export(
						numTasks,
						PASS_MASK,
						fromQueues,
						fromQueueLengths,
						fromQueueStart,
						fromQueueStart,
						array,
						0
					);
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
