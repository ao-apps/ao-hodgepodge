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
import com.aoindustries.lang.RuntimeUtils;
import com.aoindustries.util.AtomicSequence;
import com.aoindustries.util.IntList;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.concurrent.ConcurrentUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
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
 * TODO: For concurrent implementation: Might get better performance (due to cache
 * locality of reference) by flattening the two-dimensional fixed dimensions of
 * the arrays into a single dimension.
 *
 * TODO: For concurrent implementation: Might also consider changing the row/column
 * order of the multi-dimensional arrays to help cache interaction.  Might get
 * better throughput when hit the cache wall where performance drops considerably.
 *
 * TODO: Integrate concurrent implementation into this codebase.
 *
 * @author  AO Industries, Inc.
 */
final public class NewIntegerRadixSort extends IntegerSortAlgorithm {

	private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
	private static final int PASS_SIZE = 1 << BITS_PER_PASS;
	private static final int PASS_MASK = PASS_SIZE - 1;

	/**
	 * When sorting lists less than this size, will use a different algorithm.
	 */
	private static final int MIN_RADIX_SORT_SIZE = 1 << 11;

	/**
	 * The minimum starting queue length (unless the size of the passed-in list is smaller)
	 */
	private static final int MINIMUM_START_QUEUE_LENGTH = 16;

	// Concurrency debug settings
	private static final boolean USE_CONCURRENT_IMPORT = true;
	private static final boolean USE_CONCURRENT_GATHER_SCATTER = true;
	private static final boolean USE_CONCURRENT_EXPORT = true;

	/**
	 * When there are fewer than MIN_CONCURRENCY_SIZE elements,
	 * the single-threaded implementation is used.
	 */
	private static final int MIN_CONCURRENCY_SIZE = 1 << 16; // This is the break-even point on a Core i7-2600k (shows as 8 processors, but has 4 cores), might depend on the number of processors

	/**
	 * Where there are fewer than MIN_CONCURRENCY_PROCESSORS available processors,
	 * the single-threaded implementation is used.
	 */
	private static final int MIN_CONCURRENCY_PROCESSORS = 2;

	/**
	 * The number of tasks that will be submitted to the thread pool per processor.
	 */
	private static final int TASKS_PER_PROCESSOR = 2;

	private static final ExecutorService defaultExecutor = Executors.newCachedThreadPool(
		new ThreadFactory() {
			private final Sequence idSequence = new AtomicSequence();
			public Thread newThread(Runnable target) {
				long id = idSequence.getNextSequenceValue();
				return new Thread(target, NewIntegerRadixSort.class.getName()+".defaultExecutor: id=" + id);
			}
		}
	);

	private static final NewIntegerRadixSort defaultInstance = new NewIntegerRadixSort(defaultExecutor);
	private static final NewIntegerRadixSort singleThreadedInstance = new NewIntegerRadixSort(null);

	/**
	 * Gets the default IntegerRadixSort using the default executor service.
	 * This will use concurrency where appropriate (long lists/arrays on
	 * multi-core systems).
	 */
    public static NewIntegerRadixSort getInstance() {
        return defaultInstance;
    }

	/**
	 * Gets a single-threaded instance of IntegerRadixSort, that will not ever
	 * sort concurrently.  As the determination of when to use concurrency should
	 * avoid any potential downfalls, it is recommended to use the default instance
	 * from <code>getInstance</code> for most scenarios.
	 *
	 * @see  #getInstance()
	 */
	public static NewIntegerRadixSort getSingleThreadedInstance() {
		return singleThreadedInstance;
	}

	/**
	 * Gets a IntegerRadixSort that uses the provided ExecutorService.
	 * If the executor service is <code>null</code>, concurrency is disabled.
	 */
	public static NewIntegerRadixSort getInstance(ExecutorService executor) {
		return executor==null ? singleThreadedInstance : new NewIntegerRadixSort(executor);
	}

	private final ExecutorService executor;

	private NewIntegerRadixSort(ExecutorService executor) {
		this.executor = executor;
    }

	@Override
	public boolean isStable() {
		return true;
	}

	// <editor-fold defaultstate="collapsed" desc="Sorter">
	abstract static class Sorter {

		final int size;

		/**
		 * The number of tasks to divide work between.
		 * One for single-threaded.
		 */
		final int numTasks;

		// Must be power of two and less than or equal to 32
		/*
		final int BITS_PER_PASS;
		final int PASS_SIZE;
		final int PASS_MASK;
		 */

		final int startQueueLength;

		/**
		 * Set of all bits seen to skip bit ranges that won't sort.
		 */
		int bitsSeen;

		/**
		 * Set of all bits not seen to skip bit ranges that won't sort.
		 */
		int bitsNotSeen;

		Sorter(int size, int numTasks) {
			this.size = size;
			this.numTasks = numTasks;

			// Dynamically choose pass size
			/*
			if(size < 0x80000) {
				this.BITS_PER_PASS = 8;
			} else {
				this.BITS_PER_PASS = 16;
			}
			this.PASS_SIZE = 1 << BITS_PER_PASS;
			this.PASS_MASK = PASS_SIZE - 1;
			 */

			// Determine the start queue length
			{
				int sql = (size >>> (BITS_PER_PASS-1)) / numTasks; // Double the average size to allow for somewhat uneven distribution before growing arrays
				if(sql<MINIMUM_START_QUEUE_LENGTH) sql = MINIMUM_START_QUEUE_LENGTH;
				if(sql>size) sql = size;
				this.startQueueLength = sql;
			}
		}

		final void sort() {
			// Import from source into toQueues, updating bitsSeen and bitsNotSeen
			bitsSeen = 0;
			bitsNotSeen = 0;
			importData();
			bitsNotSeen ^= 0xffffffff;

			// Swap toQueues and fromQueues
			swapQueues();

			// Perform gather/scatter iterations
			int lastShiftUsed = gatherScatter();

			// Negative before positive to perform as signed integers
			int fromQueueStart = (lastShiftUsed+BITS_PER_PASS)==32 ? (PASS_SIZE>>>1) : 0;

			// Put results back into source
			exportData(fromQueueStart);
		}

		/**
		 * Swaps the from and to queues.
		 */
		abstract void swapQueues();

		/**
		 * Initial population of elements into toQueues,
		 * updating bitsSeen and bitsNotSeen.
		 */
		abstract void importData();

		/**
		 * Performs the gather/scatter stage of the sort
		 *
		 * @return  lastShiftUsed  The last shift value performed in a pass
		 */
		abstract int gatherScatter();

		/**
		 * Pick-up fromQueues and put into results, started at the provided queue.
		 */
		abstract void exportData(int fromQueueStart);
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="SingleThreadedSorter">
	abstract static class SingleThreadedSorter extends Sorter {

		int[] fromQueueLengths;
		int[] toQueueLengths;

		SingleThreadedSorter(int size) {
			super(size, 1);
			this.fromQueueLengths = new int[PASS_SIZE];
			this.toQueueLengths   = new int[PASS_SIZE];
		}

		@Override
		void swapQueues() {
			int[] tempLengths = fromQueueLengths;
			fromQueueLengths = toQueueLengths;
			toQueueLengths = tempLengths;
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="ConcurrentSorter">
	abstract static class ConcurrentSorter extends Sorter {

		final ExecutorService executor;

		// The same futures list is used by multiple stages below
		final List<Future<?>> runnableFutures;

		int[][] fromQueueLengths;
		int[][] toQueueLengths;

		/**
		 * The size of division of work (number of elements per task).
		 */
		final int sizePerTask;

		ConcurrentSorter(int size, ExecutorService executor, int numProcessors) {
			super(size, numProcessors * TASKS_PER_PROCESSOR);
			this.executor         = executor;
			this.runnableFutures  = new ArrayList<Future<?>>(numTasks);
			this.fromQueueLengths = new int[numTasks][PASS_SIZE];
			this.toQueueLengths   = new int[numTasks][PASS_SIZE];
			{
				int spt = size / numTasks;
				if((spt*numTasks)<size) spt++; // Round-up instead of down
				this.sizePerTask = spt;
			}
		}

		@Override
		void swapQueues() {
			int[][] tempLengths = fromQueueLengths;
			fromQueueLengths = toQueueLengths;
			toQueueLengths = tempLengths;
		}

		@Override
		final int gatherScatter() {
			try {
				int lastShiftUsed = 0;
				for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
					// Skip this bit range when all values have equal bits.  For example
					// when going through the upper bits of lists of all smaller positive
					// or negative numbers.
					if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
						lastShiftUsed = shift;

						if(USE_CONCURRENT_GATHER_SCATTER) {
							// Get some final values for anonymous inner class
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
									shift,
									fromQueueNum,
									0 // Task #0 for non concurrent gather/scatter
								);
							}
						}

						// Swap from and to
						swapQueues();
					}
				}
				return lastShiftUsed;
			} catch(InterruptedException e) {
				throw new WrappedException(e);
			} catch(ExecutionException e) {
				throw new WrappedException(e);
			}
		}

		/**
		 * Gather/scatter for a single task.
		 */
		abstract void gatherScatter(
			final int shift,
			final int fromQueueNum,
			final int toTaskNum
		);
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="SingleThreadedNumberSorter">
	abstract static class SingleThreadedNumberSorter<N extends Number> extends SingleThreadedSorter {

		N[][] fromQueues;
		N[][] toQueues;

		@SuppressWarnings("unchecked")
		SingleThreadedNumberSorter(int size) {
			super(size);
			this.fromQueues = (N[][])new Number[PASS_SIZE][];
			this.toQueues = (N[][])new Number[PASS_SIZE][];
		}

		/**
		 * Adds a number to the toQueue.
		 *
		 * @param  number  The number to add
		 * @return  the <code>int</code> value of the number added
		 */
		final int addToQueue(int shift, N number) {
			int numInt = number.intValue();
			int toQueueNum = (numInt >>> shift) & PASS_MASK;
			N[] toQueue = toQueues[toQueueNum];
			int toQueueLength = toQueueLengths[toQueueNum];
			if(toQueue==null) {
				@SuppressWarnings("unchecked")
				N[] newQueue = (N[])new Number[startQueueLength];
				toQueues[toQueueNum] = toQueue = newQueue;
			} else if(toQueueLength>=toQueue.length) {
				// Grow queue
				@SuppressWarnings("unchecked")
				N[] newQueue = (N[])new Number[toQueueLength<<1];
				System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
				toQueues[toQueueNum] = toQueue = newQueue;
			}
			toQueue[toQueueLength++] = number;
			toQueueLengths[toQueueNum] = toQueueLength;
			return numInt;
		}

		@Override
		final void swapQueues() {
			super.swapQueues();
			N[][] temp = fromQueues;
			fromQueues = toQueues;
			toQueues = temp;
		}

		@Override
		final int gatherScatter() {
			int lastShiftUsed = 0;
			for(int shift=BITS_PER_PASS; shift<32; shift += BITS_PER_PASS) {
				// Skip this bit range when all values have equal bits.  For example
				// when going through the upper bits of lists of all smaller positive
				// or negative numbers.
				if(((bitsSeen>>>shift)&PASS_MASK) != ((bitsNotSeen>>>shift)&PASS_MASK) ) {
					lastShiftUsed = shift;
					for(int fromQueueNum=0; fromQueueNum<PASS_SIZE; fromQueueNum++) {
						N[] fromQueue = fromQueues[fromQueueNum];
						if(fromQueue!=null) {
							int length = fromQueueLengths[fromQueueNum];
							for(int j=0; j<length; j++) {
								addToQueue(shift, fromQueue[j]);
							}
							fromQueueLengths[fromQueueNum] = 0;
						}
					}

					// Swap from and to
					swapQueues();
				}
			}
			return lastShiftUsed;
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="ConcurrentNumberSorter">
	abstract static class ConcurrentNumberSorter<N extends Number> extends ConcurrentSorter {

		N[][][] fromQueues;
		N[][][] toQueues;

		@SuppressWarnings("unchecked")
		ConcurrentNumberSorter(int size, ExecutorService executor, int numProcessors) {
			super(size, executor, numProcessors);
			this.fromQueues = (N[][][])new Number[numTasks][PASS_SIZE][];
			this.toQueues   = (N[][][])new Number[numTasks][PASS_SIZE][];
		}

		/**
		 * Adds a number to the toQueue.
		 *
		 * @param  number  The number to add
		 *
		 * @return  the <code>int</code> value of the number added
		 */
		final int addToQueue(int shift, N number, N[][] taskToQueues, int[] taskToQueueLengths) {
			int numInt = number.intValue();
			int toQueueNum = (numInt >>> shift) & PASS_MASK;
			N[] toQueue = taskToQueues[toQueueNum];
			int toQueueLength = taskToQueueLengths[toQueueNum];
			if(toQueue==null) {
				@SuppressWarnings("unchecked")
				N[] newQueue = (N[])new Number[startQueueLength];
				taskToQueues[toQueueNum] = toQueue = newQueue;
			} else if(toQueueLength>=toQueue.length) {
				// Grow queue
				@SuppressWarnings("unchecked")
				N[] newQueue = (N[])new Number[toQueueLength<<1];
				System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
				taskToQueues[toQueueNum] = toQueue = newQueue;
			}
			toQueue[toQueueLength++] = number;
			taskToQueueLengths[toQueueNum] = toQueueLength;
			return numInt;
		}

		@Override
		final void swapQueues() {
			super.swapQueues();
			N[][][] temp = fromQueues;
			fromQueues = toQueues;
			toQueues = temp;
		}

		@Override
		void gatherScatter(
			int shift,
			int fromQueueNum,
			int toTaskNum
		) {
			for(int fromTaskNum=0; fromTaskNum<numTasks; fromTaskNum++) {
				final N[][] taskFromQueues = fromQueues[fromTaskNum];
				N[] fromQueue = taskFromQueues[fromQueueNum];
				if(fromQueue!=null) {
					final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
					final N[][] taskToQueues = toQueues[toTaskNum];
					final int[] taskToQueueLengths = toQueueLengths[toTaskNum];
					int length = taskFromQueueLengths[fromQueueNum];
					for(int j=0; j<length; j++) {
						addToQueue(shift, fromQueue[j], taskToQueues, taskToQueueLengths);
					}
					taskFromQueueLengths[fromQueueNum] = 0;
				}
			}
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="List<N>">
	static class SingleThreadedNumberListSorter<N extends Number> extends SingleThreadedNumberSorter<N> {

		private final List<N> list;
		private final boolean useRandomAccess;

		SingleThreadedNumberListSorter(List<N> list) {
			super(list.size());
			this.list = list;
			this.useRandomAccess = size<Integer.MAX_VALUE && (list instanceof RandomAccess);
		}

		@Override
		final void importData() {
			if(useRandomAccess) {
				for(int i=0;i<size;i++) {
					int numInt = addToQueue(0, list.get(i));
					bitsSeen |= numInt;
					bitsNotSeen |= numInt ^ 0xffffffff;
				}
			} else {
				for(N number : list) {
					int numInt = addToQueue(0, number);
					bitsSeen |= numInt;
					bitsNotSeen |= (numInt ^ 0xffffffff);
				}
			}
		}

		@Override
		final void exportData(final int fromQueueStart) {
			int fromQueueNum = fromQueueStart;
			if(useRandomAccess) {
				// Use indexed strategy
				int outIndex = 0;
				do {
					N[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							list.set(outIndex++, fromQueue[j]);
						}
					}
				} while(
					(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
					!= fromQueueStart
				);
			} else {
				// Use iterator strategy
				ListIterator<N> iterator = list.listIterator();
				do {
					N[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							iterator.next();
							iterator.set(fromQueue[j]);
						}
					}
				} while(
					(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
					!= fromQueueStart
				);
			}
		}
	}

	@Override
    public <N extends Number> void sort(List<N> list, SortStatistics stats) {
		if(list instanceof IntList) {
			sort((IntList)list);
		} else {
			if(stats!=null) stats.sortStarting();
			final int size = list.size();
			if(size < MIN_RADIX_SORT_SIZE) {
				if(stats!=null) stats.sortSwitchingAlgorithms();
				Collections.sort(list, IntValueComparator.getInstance());
			} else {
				if(stats!=null) {
					// One get and one set for each element
					stats.sortGetting(size);
					stats.sortSetting(size);
				}
				final int numProcessors;
				if(
					executor==null
					|| size < MIN_CONCURRENCY_SIZE
					|| (numProcessors = RuntimeUtils.getAvailableProcessors())<MIN_CONCURRENCY_PROCESSORS
				) {
					new SingleThreadedNumberListSorter<N>(list).sort();
				} else {
					// TODO: new ConcurrentNumberListSorter<N>(list, numProcessors).sort();
					throw new NotImplementedException("TODO: Implement concurrent version");
				}
			}
			if(stats!=null) stats.sortEnding();
		}
    }
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="N[]">
	static class SingleThreadedNumberArraySorter<N extends Number> extends SingleThreadedNumberSorter<N> {

		private final N[] array;

		SingleThreadedNumberArraySorter(N[] array) {
			super(array.length);
			this.array = array;
		}

		@Override
		final void importData() {
			for(int i=0;i<size;i++) {
				int numInt = addToQueue(0, array[i]);
				bitsSeen |= numInt;
				bitsNotSeen |= numInt ^ 0xffffffff;
			}
		}

		@Override
		final void exportData(final int fromQueueStart) {
			int fromQueueNum = fromQueueStart;
			// Use indexed strategy
			int outIndex = 0;
			do {
				N[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
			} while(
				(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
				!= fromQueueStart
			);
		}
	}

	@Override
    public <N extends Number> void sort(N[] array, SortStatistics stats) {
		if(stats!=null) stats.sortStarting();
		final int size = array.length;
		if(size < MIN_RADIX_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			Arrays.sort(array, IntValueComparator.getInstance());
		} else {
			if(stats!=null) {
				// One get and one set for each element
				stats.sortGetting(size);
				stats.sortSetting(size);
			}
			final int numProcessors;
			if(
				executor==null
				|| size < MIN_CONCURRENCY_SIZE
				|| (numProcessors = RuntimeUtils.getAvailableProcessors())<MIN_CONCURRENCY_PROCESSORS
			) {
				new SingleThreadedNumberArraySorter<N>(array).sort();
			} else {
				// TODO: new ConcurrentNumberArraySorter<N>(array, numProcessors).sort();
				throw new NotImplementedException("TODO: Implement concurrent version");
			}
		}
		if(stats!=null) stats.sortEnding();
    }
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="SingleThreadedIntSorter">
	abstract static class SingleThreadedIntSorter extends SingleThreadedSorter {

		int[][] fromQueues;
		int[][] toQueues;

		SingleThreadedIntSorter(int size) {
			super(size);
			this.fromQueues = new int[PASS_SIZE][];
			this.toQueues = new int[PASS_SIZE][];
		}

		/**
		 * Adds a number to the toQueue.
		 *
		 * @param  number  The number to add
		 * @return  the <code>int</code> value of the number added
		 */
		final int addToQueue(int shift, int number) {
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
			return number;
		}

		@Override
		final void swapQueues() {
			super.swapQueues();
			int[][] temp = fromQueues;
			fromQueues = toQueues;
			toQueues = temp;
		}

		@Override
		final int gatherScatter() {
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
								addToQueue(shift, fromQueue[j]);
							}
							fromQueueLengths[fromQueueNum] = 0;
						}
					}

					// Swap from and to
					swapQueues();
				}
			}
			return lastShiftUsed;
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="ConcurrentIntSorter">
	abstract static class ConcurrentIntSorter extends ConcurrentSorter {

		int[][][] fromQueues;
		int[][][] toQueues;

		@SuppressWarnings("unchecked")
		ConcurrentIntSorter(int size, ExecutorService executor, int numProcessors) {
			super(size, executor, numProcessors);
			this.fromQueues = new int[numTasks][PASS_SIZE][];
			this.toQueues   = new int[numTasks][PASS_SIZE][];
		}

		/**
		 * Adds a number to the toQueue.
		 *
		 * @param  number  The number to add
		 *
		 * @return  the <code>int</code> value of the number added
		 */
		final int addToQueue(int shift, int number, int[][] taskToQueues, int[] taskToQueueLengths) {
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
			return number;
		}

		@Override
		final void swapQueues() {
			super.swapQueues();
			int[][][] temp = fromQueues;
			fromQueues = toQueues;
			toQueues = temp;
		}

		@Override
		void gatherScatter(
			int shift,
			int fromQueueNum,
			int toTaskNum
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
						addToQueue(shift, fromQueue[j], taskToQueues, taskToQueueLengths);
					}
					taskFromQueueLengths[fromQueueNum] = 0;
				}
			}
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="IntList">
	static class SingleThreadedIntListSorter extends SingleThreadedIntSorter {

		private final IntList list;
		private final boolean useRandomAccess;

		SingleThreadedIntListSorter(IntList list) {
			super(list.size());
			this.list = list;
			this.useRandomAccess = size<Integer.MAX_VALUE && (list instanceof RandomAccess);
		}

		@Override
		final void importData() {
			if(useRandomAccess) {
				for(int i=0;i<size;i++) {
					int numInt = addToQueue(0, list.getInt(i));
					bitsSeen |= numInt;
					bitsNotSeen |= numInt ^ 0xffffffff;
				}
			} else {
				for(Integer number : list) {
					int numInt = addToQueue(0, number);
					bitsSeen |= numInt;
					bitsNotSeen |= (numInt ^ 0xffffffff);
				}
			}
		}

		@Override
		final void exportData(final int fromQueueStart) {
			int fromQueueNum = fromQueueStart;
			if(useRandomAccess) {
				// Use indexed strategy
				int outIndex = 0;
				do {
					int[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							list.set(outIndex++, fromQueue[j]);
						}
					}
				} while(
					(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
					!= fromQueueStart
				);
			} else {
				// Use iterator strategy
				ListIterator<Integer> iterator = list.listIterator();
				do {
					int[] fromQueue = fromQueues[fromQueueNum];
					if(fromQueue!=null) {
						int length = fromQueueLengths[fromQueueNum];
						for(int j=0; j<length; j++) {
							iterator.next();
							iterator.set(fromQueue[j]);
						}
					}
				} while(
					(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
					!= fromQueueStart
				);
			}
		}
	}

	@Override
    public void sort(IntList list, SortStatistics stats) {
		if(stats!=null) stats.sortStarting();
		final int size = list.size();
		if(size < MIN_RADIX_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			Collections.sort(list, IntValueComparator.getInstance());
		} else {
			if(stats!=null) {
				// One get and one set for each element
				stats.sortGetting(size);
				stats.sortSetting(size);
			}
			final int numProcessors;
			if(
				executor==null
				|| size < MIN_CONCURRENCY_SIZE
				|| (numProcessors = RuntimeUtils.getAvailableProcessors())<MIN_CONCURRENCY_PROCESSORS
			) {
				new SingleThreadedIntListSorter(list).sort();
			} else {
				// TODO: new ConcurrentIntListSorter(list, numProcessors).sort();
				throw new NotImplementedException("TODO: Implement concurrent version");
			}
		}
		if(stats!=null) stats.sortEnding();
    }
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="int[]">
	static class SingleThreadedIntArraySorter extends SingleThreadedIntSorter {

		private final int[] array;

		SingleThreadedIntArraySorter(int[] array) {
			super(array.length);
			this.array = array;
		}

		@Override
		final void importData() {
			for(int i=0;i<size;i++) {
				int number = addToQueue(0, array[i]);
				bitsSeen |= number;
				bitsNotSeen |= number ^ 0xffffffff;
			}
		}

		@Override
		final void exportData(final int fromQueueStart) {
			int fromQueueNum = fromQueueStart;
			// Use indexed strategy
			int outIndex = 0;
			do {
				int[] fromQueue = fromQueues[fromQueueNum];
				if(fromQueue!=null) {
					int length = fromQueueLengths[fromQueueNum];
					System.arraycopy(fromQueue, 0, array, outIndex, length);
					outIndex += length;
				}
			} while(
				(fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
				!= fromQueueStart
			);
		}
	}

	@Override
    public void sort(int[] array, SortStatistics stats) {
		if(stats!=null) stats.sortStarting();
		final int size = array.length;
		if(size < MIN_RADIX_SORT_SIZE) {
            if(stats!=null) stats.sortSwitchingAlgorithms();
			Arrays.sort(array);
		} else {
			if(stats!=null) {
				// One get and one set for each element
				stats.sortGetting(size);
				stats.sortSetting(size);
			}
			final int numProcessors;
			if(
				executor==null
				|| size < MIN_CONCURRENCY_SIZE
				|| (numProcessors = RuntimeUtils.getAvailableProcessors())<MIN_CONCURRENCY_PROCESSORS
			) {
				new SingleThreadedIntArraySorter(array).sort();
			} else {
				// TODO: new ConcurrentIntArraySorter(array, numProcessors).sort();
				throw new NotImplementedException("TODO: Implement concurrent version");
			}
		}
		if(stats!=null) stats.sortEnding();
    }
	// </editor-fold>
}
