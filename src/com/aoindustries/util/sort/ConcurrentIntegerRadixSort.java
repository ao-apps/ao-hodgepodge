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
				if(stats!=null) {
					// There will be only one get and one set for each element
					stats.sortGetting(size);
					stats.sortSetting(size);
				}

				// Determine size of division
				final int sizePerTask;
				{
					int spt = size / numTasks;
					if((spt*numTasks)<size) spt++; // Round-up instead of down
					sizePerTask = spt;
				}

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
					// TODO: Could perform the last one on the current thread, here and other places (and reduce size of executor service by one?)
					importStepFutures.add(
						executor.submit(
							new Callable<ImportStepResult>() {
								public ImportStepResult call() {
									int bitsSeen = 0; // Set of all bits seen for to skip bit ranges that won't sort
									int bitsNotSeen = 0;
									for(int i=finalTaskStart; i<finalTaskEnd; i++) {
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

				// Gather/scatter stage
				int lastShiftUsed = 0;
				for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
					// Skip this bit range when all values have equal bits.  For example
					// when going through the upper bits of lists of all smaller positive
					// or negative numbers.
					if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
						lastShiftUsed = shift;

						// Perform each concurrently
						/*
						final List<Future<?>> gatherScatterFutures = new ArrayList<Future<?>>(numTasks); // TODO: Use same list as below, don't allocate inside loop
						for(int taskStart=0; taskStart<PASS_SIZE; taskStart+=passSizePerTask) {
							final int finalShift = shift;
							final int finalTaskStart = taskStart;
							final int[][] finalFromQueues = fromQueues;
							final int[] finalFromQueueLengths = fromQueueLengths;
							final int[][] finalToQueues = toQueues;
							final int[] finalToQueueLengths = toQueueLengths;
							// TODO: Could perform the last one on the current thread, here and other places (and reduce size of executor service by one?)
							gatherScatterFutures.add(
								executor.submit(
									new Runnable() {
										public void run() {
											int taskEnd = finalTaskStart + passSizePerTask;
											if(taskEnd > PASS_SIZE) taskEnd = PASS_SIZE;
											for(int fromQueueNum=finalTaskStart; fromQueueNum<taskEnd; fromQueueNum++) {
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
						 */
						for(int fromQueueNum=0; fromQueueNum<PASS_SIZE; fromQueueNum++) {
							for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
								final int[][] taskFromQueues = fromQueues[fromTaskNum];
								int[] fromQueue = taskFromQueues[fromQueueNum];
								if(fromQueue!=null) {
									final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
									final int toTaskNum = 0; // TODO: Will be different during concurrency
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
				int fromQueueNum = fromQueueStart;
				if(USE_CONCURRENT_EXPORT) {
					// Use indexed strategy with balanced concurrency
					final int fromQueueLast = (fromQueueStart-1) & PASS_MASK;
					int exportTaskNum = 0;
					int taskFromQueueStart = fromQueueStart;
					int taskOutIndex = 0;
					int taskTotalLength = 0;
					final List<Future<?>> runnableFutures = new ArrayList<Future<?>>(numTasks);
					do {
						for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
							taskTotalLength += fromQueueLengths[fromTaskNum][fromQueueNum];
							if(
								taskTotalLength>0 // Skip no output, such as all handle in previous tasks
								&& (
									taskTotalLength >= sizePerTask // Found fair share (or more)
									|| fromQueueNum==fromQueueLast // or is last task
								)
							) {
								final int[][][] finalFromQueues = fromQueues;
								final int[][] finalFromQueueLengths = fromQueueLengths;
								final int finalTaskFromQueueStart = taskFromQueueStart;
								final int finalTaskOutIndex = taskOutIndex;
								final int taskFromQueueEnd = (fromQueueNum + 1) & PASS_MASK;
								// Queue concurrent
								runnableFutures.add(
									executor.submit(
										new Runnable() {
											@Override
											public void run() {
												int exportQueueNum = finalTaskFromQueueStart;
												int outIndex = finalTaskOutIndex;
												do {
													for(int exportTaskNum=0; exportTaskNum<numTasks; exportTaskNum++) {
														final int[][] taskFromQueues = finalFromQueues[exportTaskNum];
														int[] fromQueue = taskFromQueues[exportQueueNum];
														if(fromQueue!=null) {
															final int[] taskFromQueueLengths = finalFromQueueLengths[exportTaskNum];
															int length = taskFromQueueLengths[exportQueueNum];
															System.arraycopy(fromQueue, 0, array, outIndex, length);
															outIndex += length;
														}
													}
												} while(
													(exportQueueNum = (exportQueueNum + 1) & PASS_MASK)
													!= taskFromQueueEnd
												);
											}
										}
									)
								);

								// Reset to next task
								exportTaskNum++;
								taskFromQueueStart = taskFromQueueEnd;
								taskOutIndex += taskTotalLength;
								taskTotalLength = 0;
							}
						}
					} while(
						(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
						!= fromQueueStart
					);
					// Wait for each export task to complete
					for(Future<?> gatherScatterFuture : runnableFutures) {
						gatherScatterFuture.get();
					}
				} else {
					// Use indexed strategy
					int outIndex = 0;
					do {
						for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
							final int[][] taskFromQueues = fromQueues[fromTaskNum];
							int[] fromQueue = taskFromQueues[fromQueueNum];
							if(fromQueue!=null) {
								final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
								int length = taskFromQueueLengths[fromQueueNum];
								System.arraycopy(fromQueue, 0, array, outIndex, length);
								outIndex += length;
							}
						}
					} while(
						(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
						!= fromQueueStart
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
