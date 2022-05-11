/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015, 2016, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.sort;

import com.aoapps.collections.IntList;
import com.aoapps.lang.NullArgumentException;
import com.aoapps.lang.RuntimeUtils;
import com.aoapps.lang.exception.WrappedException;
import com.aoapps.lang.util.AtomicSequence;
import com.aoapps.lang.util.Sequence;
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
 * <p>
 * Although a very different implementation, this topic is discussed at
 * <a href="http://erik.gorset.no/2011/04/radix-sort-is-faster-than-quicksort.html">http://erik.gorset.no/2011/04/radix-sort-is-faster-than-quicksort.html</a>
 * with source provided at <a href="https://github.com/gorset/radix/blob/master/Radix.java">https://github.com/gorset/radix/blob/master/Radix.java</a>.
 * </p>
 * <p>
 * TODO: For concurrent implementation: Might get better performance (due to cache
 * locality of reference) by flattening the two-dimensional fixed dimensions of
 * the arrays into a single dimension.
 * </p>
 * <p>
 * TODO: For concurrent implementation: Might also consider changing the row/column
 * order of the multi-dimensional arrays to help cache interaction.  Might get
 * better throughput when hit the cache wall where performance drops considerably.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public final class IntegerRadixSort extends BaseIntegerSortAlgorithm {

  private static final int BITS_PER_PASS = 8; // Must be power of two and less than or equal to 32
  private static final int PASS_SIZE     = 1 << BITS_PER_PASS;
  private static final int PASS_MASK     = PASS_SIZE - 1;

  /**
   * When sorting lists less than this size, will use a different algorithm.
   */
  private static final int MIN_RADIX_SORT_SIZE = 1 << 11;

  /**
   * The minimum starting queue length (unless the size of the passed-in list is smaller).
   */
  private static final int MINIMUM_START_QUEUE_LENGTH = 16;

  /**
   * When there are fewer than MIN_CONCURRENCY_SIZE elements,
   * the single-threaded implementation is used.
   */
  private static final int MIN_CONCURRENCY_SIZE = 1 << 16; // This is the break-even point on a Core i7-2600k (shows as 8 processors, but has 4 cores), might depend on the number of processors

  /**
   * When there are fewer than MIN_CONCURRENCY_PROCESSORS available processors,
   * the single-threaded implementation is used.
   */
  private static final int MIN_CONCURRENCY_PROCESSORS = 2;

  /**
   * The number of tasks that will be submitted to the thread pool per processor.
   */
  private static final int TASKS_PER_PROCESSOR = 2;

  // TODO: Allow threads to shut down, like in ao-concurrent
  private static final ExecutorService defaultExecutor = Executors.newCachedThreadPool(
      new ThreadFactory() {
        private final Sequence idSequence = new AtomicSequence();
        @Override
        public Thread newThread(Runnable target) {
          long id = idSequence.getNextSequenceValue();
          return new Thread(target, IntegerRadixSort.class.getName() + ".defaultExecutor: id=" + id);
        }
      }
  );

  private static final IntegerRadixSort defaultInstance = new IntegerRadixSort(defaultExecutor);
  private static final IntegerRadixSort singleThreadedInstance = new IntegerRadixSort(null);

  /**
   * Gets the default IntegerRadixSort using the default executor service.
   * This will use concurrency where appropriate (long lists/arrays on
   * multi-core systems).
   */
  public static IntegerRadixSort getInstance() {
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
  public static IntegerRadixSort getSingleThreadedInstance() {
    return singleThreadedInstance;
  }

  /**
   * Gets a IntegerRadixSort that uses the provided ExecutorService.
   * If the executor service is <code>null</code>, concurrency is disabled.
   */
  public static IntegerRadixSort getInstance(ExecutorService executor) {
    return executor == null ? singleThreadedInstance : new IntegerRadixSort(executor);
  }

  /**
   * Waits for all futures to complete, discarding any results.
   * <p>
   * Note: This method is cloned from ConcurrentUtils.java to avoid package dependency.
   * </p>
   */
  private static void waitForAll(Iterable<? extends Future<?>> futures) throws InterruptedException, ExecutionException {
    for (Future<?> future : futures) {
      future.get();
    }
  }

  private final ExecutorService executor;

  private IntegerRadixSort(ExecutorService executor) {
    this.executor = executor;
  }

  @Override
  public boolean isStable() {
    return true;
  }

  // <editor-fold defaultstate="collapsed" desc="RadixTable">
  abstract static class RadixTable {

    /**
     * The number of tasks to divide work between.
     * One for single-threaded.
     */
    final int numTasks;

    final int startQueueLength;

    RadixTable(int size, int numTasks) {
      this.numTasks = numTasks;
      // Determine the start queue length
      int sql = (size >>> (BITS_PER_PASS - 1)) / numTasks; // Double the average size to allow for somewhat uneven distribution before growing arrays
      if (sql < MINIMUM_START_QUEUE_LENGTH) {
        sql = MINIMUM_START_QUEUE_LENGTH;
      }
      if (sql > size) {
        sql = size;
      }
      this.startQueueLength = sql;
    }

    /**
     * Swaps the from and to queues.
     */
    abstract void swapQueues();

    /**
     * Gather/scatter for a single task.
     */
    abstract void gatherScatter(int shift, int fromQueueNum, int toTaskNum);

    /**
     * Gets the number of elements in the fromQueue.
     */
    abstract int getFromQueueLength(int fromTaskNum, int fromQueueNum);
  }

  abstract static class NumberRadixTable<N extends Number> extends RadixTable {

    NumberRadixTable(int size, int numTasks) {
      super(size, numTasks);
    }

    /**
     * Gets the elements in the fromQueue.
     */
    abstract N[] getFromQueue(int fromTaskNum, int fromQueueNum);

    /**
     * Adds a number to the toQueue.
     *
     * @param  number  The number to add
     * @return  the <code>int</code> value of the number added
     */
    abstract int addToQueue(int shift, N number, int toTaskNum);
  }

  abstract static class IntRadixTable extends RadixTable {

    IntRadixTable(int size, int numTasks) {
      super(size, numTasks);
    }

    /**
     * Gets the elements in the fromQueue.
     */
    abstract int[] getFromQueue(int fromTaskNum, int fromQueueNum);

    /**
     * Adds a number to the toQueue.
     *
     * @param  number  The number to add
     * @return  the <code>int</code> value of the number added
     */
    abstract int addToQueue(int shift, int number, int toTaskNum);
  }

  static class SingleTaskNumberRadixTable<N extends Number> extends NumberRadixTable<N> {

    private N[][] fromQueues;
    private int[] fromQueueLengths;
    private N[][] toQueues;
    private int[] toQueueLengths;

    @SuppressWarnings("unchecked")
    SingleTaskNumberRadixTable(int size) {
      super(size, 1);
      this.fromQueues       = (N[][]) new Number[PASS_SIZE][];
      this.fromQueueLengths = new int[PASS_SIZE];
      this.toQueues         = (N[][]) new Number[PASS_SIZE][];
      this.toQueueLengths   = new int[PASS_SIZE];
    }

    @Override
    final void swapQueues() {
      N[][] temp = fromQueues;
      fromQueues = toQueues;
      toQueues = temp;
      int[] tempLengths = fromQueueLengths;
      fromQueueLengths = toQueueLengths;
      toQueueLengths = tempLengths;
    }

    @Override
    final void gatherScatter(int shift, int fromQueueNum, int toTaskNum) {
      assert toTaskNum == 0;
      N[] fromQueue = fromQueues[fromQueueNum];
      if (fromQueue != null) {
        int length = fromQueueLengths[fromQueueNum];
        for (int j = 0; j < length; j++) {
          addToQueue(shift, fromQueue[j], 0);
        }
        fromQueueLengths[fromQueueNum] = 0;
      }
    }

    @Override
    final int getFromQueueLength(int fromTaskNum, int fromQueueNum) {
      assert fromTaskNum == 0;
      return fromQueueLengths[fromQueueNum];
    }

    @Override
    final N[] getFromQueue(int fromTaskNum, int fromQueueNum) {
      assert fromTaskNum == 0;
      return fromQueues[fromQueueNum];
    }

    @Override
    final int addToQueue(int shift, N number, int toTaskNum) {
      assert toTaskNum == 0;
      int numInt = number.intValue();
      int toQueueNum = (numInt >>> shift) & PASS_MASK;
      N[] toQueue = toQueues[toQueueNum];
      int toQueueLength = toQueueLengths[toQueueNum];
      if (toQueue == null) {
        @SuppressWarnings("unchecked")
        N[] newQueue = (N[]) new Number[startQueueLength];
        toQueues[toQueueNum] = toQueue = newQueue;
      } else if (toQueueLength >= toQueue.length) {
        // Grow queue
        @SuppressWarnings("unchecked")
        N[] newQueue = (N[]) new Number[toQueueLength << 1];
        System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
        toQueues[toQueueNum] = toQueue = newQueue;
      }
      toQueue[toQueueLength++] = number;
      toQueueLengths[toQueueNum] = toQueueLength;
      return numInt;
    }
  }

  static class MultiTaskNumberRadixTable<N extends Number> extends NumberRadixTable<N> {

    private N[][][] fromQueues;
    private int[][] fromQueueLengths;
    private N[][][] toQueues;
    private int[][] toQueueLengths;

    @SuppressWarnings("unchecked")
    MultiTaskNumberRadixTable(int size, int numTasks) {
      super(size, numTasks);
      this.fromQueues       = (N[][][]) new Number[numTasks][PASS_SIZE][];
      this.fromQueueLengths = new int[numTasks][PASS_SIZE];
      this.toQueues         = (N[][][]) new Number[numTasks][PASS_SIZE][];
      this.toQueueLengths   = new int[numTasks][PASS_SIZE];
    }

    @Override
    final void swapQueues() {
      N[][][] temp = fromQueues;
      fromQueues = toQueues;
      toQueues = temp;
      int[][] tempLengths = fromQueueLengths;
      fromQueueLengths = toQueueLengths;
      toQueueLengths = tempLengths;
    }

    @Override
    final void gatherScatter(int shift, int fromQueueNum, int toTaskNum) {
      for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
        final N[][] taskFromQueues = fromQueues[fromTaskNum];
        N[] fromQueue = taskFromQueues[fromQueueNum];
        if (fromQueue != null) {
          final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
          final N[][] taskToQueues = toQueues[toTaskNum];
          final int[] taskToQueueLengths = toQueueLengths[toTaskNum];
          int length = taskFromQueueLengths[fromQueueNum];
          for (int j = 0; j < length; j++) {
            addToQueue(shift, fromQueue[j], taskToQueues, taskToQueueLengths);
          }
          taskFromQueueLengths[fromQueueNum] = 0;
        }
      }
    }

    @Override
    final int getFromQueueLength(int fromTaskNum, int fromQueueNum) {
      return fromQueueLengths[fromTaskNum][fromQueueNum];
    }

    @Override
    final N[] getFromQueue(int fromTaskNum, int fromQueueNum) {
      return fromQueues[fromTaskNum][fromQueueNum];
    }

    private int addToQueue(int shift, N number, N[][] taskToQueues, int[] taskToQueueLengths) {
      int numInt = number.intValue();
      int toQueueNum = (numInt >>> shift) & PASS_MASK;
      N[] toQueue = taskToQueues[toQueueNum];
      int toQueueLength = taskToQueueLengths[toQueueNum];
      if (toQueue == null) {
        @SuppressWarnings("unchecked")
        N[] newQueue = (N[]) new Number[startQueueLength];
        taskToQueues[toQueueNum] = toQueue = newQueue;
      } else if (toQueueLength >= toQueue.length) {
        // Grow queue
        @SuppressWarnings("unchecked")
        N[] newQueue = (N[]) new Number[toQueueLength << 1];
        System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
        taskToQueues[toQueueNum] = toQueue = newQueue;
      }
      toQueue[toQueueLength++] = number;
      taskToQueueLengths[toQueueNum] = toQueueLength;
      return numInt;
    }

    @Override
    final int addToQueue(int shift, N number, int toTaskNum) {
      return addToQueue(
          shift,
          number,
          toQueues[toTaskNum],
          toQueueLengths[toTaskNum]
      );
    }
  }

  static class SingleTaskIntRadixTable extends IntRadixTable {

    private int[][] fromQueues;
    private int[] fromQueueLengths;
    private int[][] toQueues;
    private int[] toQueueLengths;

    SingleTaskIntRadixTable(int size) {
      super(size, 1);
      this.fromQueues       = new int[PASS_SIZE][];
      this.fromQueueLengths = new int[PASS_SIZE];
      this.toQueues         = new int[PASS_SIZE][];
      this.toQueueLengths   = new int[PASS_SIZE];
    }

    @Override
    final void swapQueues() {
      int[][] temp = fromQueues;
      fromQueues = toQueues;
      toQueues = temp;
      int[] tempLengths = fromQueueLengths;
      fromQueueLengths = toQueueLengths;
      toQueueLengths = tempLengths;
    }

    @Override
    final void gatherScatter(int shift, int fromQueueNum, int toTaskNum) {
      assert toTaskNum == 0;
      int[] fromQueue = fromQueues[fromQueueNum];
      if (fromQueue != null) {
        int length = fromQueueLengths[fromQueueNum];
        for (int j = 0; j < length; j++) {
          addToQueue(shift, fromQueue[j], 0);
        }
        fromQueueLengths[fromQueueNum] = 0;
      }
    }

    @Override
    final int getFromQueueLength(int fromTaskNum, int fromQueueNum) {
      assert fromTaskNum == 0;
      return fromQueueLengths[fromQueueNum];
    }

    @Override
    final int[] getFromQueue(int fromTaskNum, int fromQueueNum) {
      assert fromTaskNum == 0;
      return fromQueues[fromQueueNum];
    }

    @Override
    final int addToQueue(int shift, int number, int toTaskNum) {
      assert toTaskNum == 0;
      int toQueueNum = (number >>> shift) & PASS_MASK;
      int[] toQueue = toQueues[toQueueNum];
      int toQueueLength = toQueueLengths[toQueueNum];
      if (toQueue == null) {
        int[] newQueue = new int[startQueueLength];
        toQueues[toQueueNum] = toQueue = newQueue;
      } else if (toQueueLength >= toQueue.length) {
        // Grow queue
        int[] newQueue = new int[toQueueLength << 1];
        System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
        toQueues[toQueueNum] = toQueue = newQueue;
      }
      toQueue[toQueueLength++] = number;
      toQueueLengths[toQueueNum] = toQueueLength;
      return number;
    }
  }

  static class MultiTaskIntRadixTable extends IntRadixTable {

    private int[][][] fromQueues;
    private int[][] fromQueueLengths;
    private int[][][] toQueues;
    private int[][] toQueueLengths;

    MultiTaskIntRadixTable(int size, int numTasks) {
      super(size, numTasks);
      this.fromQueues       = new int[numTasks][PASS_SIZE][];
      this.fromQueueLengths = new int[numTasks][PASS_SIZE];
      this.toQueues         = new int[numTasks][PASS_SIZE][];
      this.toQueueLengths   = new int[numTasks][PASS_SIZE];
    }

    @Override
    final void swapQueues() {
      int[][][] temp = fromQueues;
      fromQueues = toQueues;
      toQueues = temp;
      int[][] tempLengths = fromQueueLengths;
      fromQueueLengths = toQueueLengths;
      toQueueLengths = tempLengths;
    }

    private int addToQueue(int shift, int number, int[][] taskToQueues, int[] taskToQueueLengths) {
      int toQueueNum = (number >>> shift) & PASS_MASK;
      int[] toQueue = taskToQueues[toQueueNum];
      int toQueueLength = taskToQueueLengths[toQueueNum];
      if (toQueue == null) {
        int[] newQueue = new int[startQueueLength];
        taskToQueues[toQueueNum] = toQueue = newQueue;
      } else if (toQueueLength >= toQueue.length) {
        // Grow queue
        int[] newQueue = new int[toQueueLength << 1];
        System.arraycopy(toQueue, 0, newQueue, 0, toQueueLength);
        taskToQueues[toQueueNum] = toQueue = newQueue;
      }
      toQueue[toQueueLength++] = number;
      taskToQueueLengths[toQueueNum] = toQueueLength;
      return number;
    }

    @Override
    final void gatherScatter(int shift, int fromQueueNum, int toTaskNum) {
      for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
        final int[][] taskFromQueues = fromQueues[fromTaskNum];
        int[] fromQueue = taskFromQueues[fromQueueNum];
        if (fromQueue != null) {
          final int[] taskFromQueueLengths = fromQueueLengths[fromTaskNum];
          final int[][] taskToQueues = toQueues[toTaskNum];
          final int[] taskToQueueLengths = toQueueLengths[toTaskNum];
          int length = taskFromQueueLengths[fromQueueNum];
          for (int j = 0; j < length; j++) {
            addToQueue(shift, fromQueue[j], taskToQueues, taskToQueueLengths);
          }
          taskFromQueueLengths[fromQueueNum] = 0;
        }
      }
    }

    @Override
    final int getFromQueueLength(int fromTaskNum, int fromQueueNum) {
      return fromQueueLengths[fromTaskNum][fromQueueNum];
    }

    @Override
    final int[] getFromQueue(int fromTaskNum, int fromQueueNum) {
      return fromQueues[fromTaskNum][fromQueueNum];
    }

    @Override
    final int addToQueue(int shift, int number, int toTaskNum) {
      return addToQueue(
          shift,
          number,
          toQueues[toTaskNum],
          toQueueLengths[toTaskNum]
      );
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Source">
  abstract static class Source<T extends RadixTable> {

    Source() {
      // Do nothing
    }

    /**
     * Checks if this data source supports random access.
     */
    abstract boolean useRandomAccess();

    static final class ImportDataResult {
      final int bitsSeen;
      final int bitsNotSeen;

      ImportDataResult(int bitsSeen, int bitsNotSeen) {
        this.bitsSeen = bitsSeen;
        this.bitsNotSeen = bitsNotSeen;
      }
    }

    /**
     * Imports one range of the data to the provided task in the table.
     */
    abstract ImportDataResult importData(T table, int start, int end, int toTaskNum);

    /**
     * Pick-up fromQueues and put into results, started at the provided queue.
     */
    abstract void exportData(T table, int fromQueueStart, int fromQueueEnd, int start);
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="radixSort">
  static <T extends RadixTable> void radixSort(
      final int size,
      final T table,
      final Source<? super T> source,
      final ExecutorService executor
  ) {
    try {
      final int numTasks = table.numTasks;

      // Dynamically choose pass size
      // Must be power of two and less than or equal to 32
      // final int BITS_PER_PASS;
      // if (size < 0x80000) {
      //   BITS_PER_PASS = 8;
      // } else {
      //   BITS_PER_PASS = 16;
      // }
      // final int PASS_SIZE = 1 << BITS_PER_PASS;
      // final int PASS_MASK = PASS_SIZE - 1;

      // The same futures list is used by multiple stages below
      final List<Future<?>> runnableFutures;

      // The size of division of work (number of elements per task).
      final int sizePerTask;

      if (executor == null) {
        assert numTasks == 1 : "Must have an executor when numTasks != 1";
        runnableFutures = null;
        sizePerTask = size;
      } else {
        assert numTasks >= 2 : "Must not have an executor when numTasks < 2";
        runnableFutures = new ArrayList<>(numTasks);
        int spt = size / numTasks;
        if ((spt * numTasks) < size) {
          spt++; // Round-up instead of down
        }
        sizePerTask = spt;
      }

      // Set of all bits seen to skip bit ranges that won't sort.
      int bitsSeen = 0;

      // Set of all bits not seen to skip bit ranges that won't sort.
      int bitsNotSeen = 0;

      // May only use concurrent import for random access sources
      if (executor != null && source.useRandomAccess()) {
        // Perform concurrent import
        final List<Future<Source.ImportDataResult>> importStepFutures = new ArrayList<>(numTasks);
        for (int taskStart = 0, toTaskNum = 0;
            taskStart < size;
            taskStart += sizePerTask, toTaskNum++
        ) {
          int taskEnd = taskStart + sizePerTask;
          if (taskEnd > size) {
            taskEnd = size;
          }

          final int finalTaskStart = taskStart;
          final int finalTaskEnd   = taskEnd;
          final int finalToTaskNum = toTaskNum;
          importStepFutures.add(
              executor.submit(
                  () -> source.importData(table, finalTaskStart, finalTaskEnd, finalToTaskNum)
              )
          );
        }

        // Combine results
        for (Future<Source.ImportDataResult> importStepFuture : importStepFutures) {
          Source.ImportDataResult result = importStepFuture.get();
          bitsSeen |= result.bitsSeen;
          bitsNotSeen |= result.bitsNotSeen;
        }
      } else {
        // Single-threaded import
        Source.ImportDataResult result = source.importData(table, 0, size, 0);
        bitsSeen |= result.bitsSeen;
        bitsNotSeen |= result.bitsNotSeen;
      }
      bitsNotSeen ^= 0xffffffff;

      // Swap toQueues and fromQueues
      table.swapQueues();

      // Perform gather/scatter iterations
      int lastShiftUsed = 0;
      for (int shift = BITS_PER_PASS; shift < 32; shift += BITS_PER_PASS) {
        // Skip this bit range when all values have equal bits.  For example
        // when going through the upper bits of lists of all smaller positive
        // or negative numbers.
        if (((bitsSeen >>> shift) & PASS_MASK) != ((bitsNotSeen >>> shift) & PASS_MASK)) {
          lastShiftUsed = shift;
          if (executor != null) {
            // Get some final values for anonymous inner class
            final int finalShift = shift;
            // Perform each concurrently with balanced concurrency
            int toTaskNum = 0;
            int taskFromQueueStart = 0;
            int taskTotalLength = 0;
            for (int fromQueueNum = 0; fromQueueNum < PASS_SIZE; fromQueueNum++) {
              for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
                taskTotalLength += table.getFromQueueLength(fromTaskNum, fromQueueNum);
              }
              if (
                  taskTotalLength > 0 // Skip no output, such as all handle in previous tasks
                      && (
                      taskTotalLength >= sizePerTask // Found fair share (or more)
                          || fromQueueNum == PASS_MASK// or is last task
                  )
              ) {
                final int finalToTaskNum = toTaskNum;
                final int finalTaskFromQueueStart = taskFromQueueStart;
                final int taskFromQueueEnd = fromQueueNum + 1;
                // Gather/scatter concurrent
                assert runnableFutures != null;
                runnableFutures.add(
                    executor.submit(
                        () -> {
                          for (int myFromQueueNum = finalTaskFromQueueStart; myFromQueueNum < taskFromQueueEnd; myFromQueueNum++) {
                            table.gatherScatter(
                                finalShift,
                                myFromQueueNum,
                                finalToTaskNum
                            );
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
            assert runnableFutures != null;
            waitForAll(runnableFutures);
            runnableFutures.clear();
          } else {
            for (int fromQueueNum = 0; fromQueueNum < PASS_SIZE; fromQueueNum++) {
              table.gatherScatter(shift, fromQueueNum, 0);
            }
          }

          // Swap from and to
          table.swapQueues();
        }
      }

      // Negative before positive to perform as signed integers
      int fromQueueStart = (lastShiftUsed + BITS_PER_PASS) == 32 ? (PASS_SIZE >>> 1) : 0;

      // May only use concurrent export for random access sources
      if (executor != null && source.useRandomAccess()) {
        // Use indexed strategy with balanced concurrency
        final int fromQueueLast = (fromQueueStart - 1) & PASS_MASK;
        int taskFromQueueStart = fromQueueStart;
        int taskOutIndex = 0;
        int taskTotalLength = 0;
        int fromQueueNum = fromQueueStart;
        do {
          for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
            taskTotalLength += table.getFromQueueLength(fromTaskNum, fromQueueNum);
          }
          if (
              taskTotalLength > 0 // Skip no output, such as all handle in previous tasks
                  && (
                  taskTotalLength >= sizePerTask // Found fair share (or more)
                      || fromQueueNum == fromQueueLast// or is last task
              )
          ) {
            final int finalTaskFromQueueStart = taskFromQueueStart;
            final int finalTaskOutIndex = taskOutIndex;
            final int finalTaskFromQueueEnd = (fromQueueNum + 1) & PASS_MASK;
            // Queue concurrent
            assert runnableFutures != null;
            runnableFutures.add(
                executor.submit(
                    () -> source.exportData(
                        table,
                        finalTaskFromQueueStart,
                        finalTaskFromQueueEnd,
                        finalTaskOutIndex
                    )
                )
            );

            // Reset to next task
            taskFromQueueStart = finalTaskFromQueueEnd;
            taskOutIndex      += taskTotalLength;
            taskTotalLength    = 0;
          }
        } while (
            (fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
                != fromQueueStart
        );
        // Wait for each export task to complete
        waitForAll(runnableFutures);
        // This is the last stage, not needed: runnableFutures.clear()
      } else {
        // Use indexed strategy, single-threaded
        source.exportData(
            table,
            fromQueueStart,
            fromQueueStart,
            0
        );
      }
    } catch (InterruptedException e) {
      // Restore the interrupted status
      Thread.currentThread().interrupt();
      throw new WrappedException(e);
    } catch (ExecutionException e) {
      throw new WrappedException(e);
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="List<N>">
  static class NumberListSource<N extends Number> extends Source<NumberRadixTable<N>> {

    private final int size;
    private final List<N> list;
    private final boolean useRandomAccess;

    NumberListSource(int size, List<N> list) {
      this.size            = size;
      this.list            = list;
      this.useRandomAccess = size < Integer.MAX_VALUE && (list instanceof RandomAccess);
    }

    @Override
    final boolean useRandomAccess() {
      return useRandomAccess;
    }

    @Override
    final ImportDataResult importData(NumberRadixTable<N> table, int start, int end, int toTaskNum) {
      int bitsSeen = 0;
      int bitsNotSeen = 0;
      if (useRandomAccess) {
        for (int i = start; i < end; i++) {
          int numInt = table.addToQueue(0, list.get(i), toTaskNum);
          bitsSeen |= numInt;
          bitsNotSeen |= numInt ^ 0xffffffff;
        }
      } else {
        assert start == 0 && end == size : "Must import all in a single pass for iterator method";
        for (N number : list) {
          int numInt = table.addToQueue(0, number, toTaskNum);
          bitsSeen |= numInt;
          bitsNotSeen |= numInt ^ 0xffffffff;
        }
      }
      return new ImportDataResult(bitsSeen, bitsNotSeen);
    }

    @Override
    final void exportData(
        final NumberRadixTable<N> table,
        final int fromQueueStart,
        final int fromQueueEnd,
        final int start
    ) {
      final int numTasks = table.numTasks;
      int fromQueueNum = fromQueueStart;
      if (useRandomAccess) {
        // Use indexed strategy
        int outIndex = start;
        do {
          for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
            N[] fromQueue = table.getFromQueue(fromTaskNum, fromQueueNum);
            if (fromQueue != null) {
              int length = table.getFromQueueLength(fromTaskNum, fromQueueNum);
              for (int j = 0; j < length; j++) {
                list.set(outIndex++, fromQueue[j]);
              }
            }
          }
        } while (
            (fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
                != fromQueueEnd
        );
      } else {
        // Use iterator strategy
        assert start == 0 : "Must import all in a single pass for iterator method";
        ListIterator<N> iterator = list.listIterator();
        do {
          for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
            N[] fromQueue = table.getFromQueue(fromTaskNum, fromQueueNum);
            if (fromQueue != null) {
              int length = table.getFromQueueLength(fromTaskNum, fromQueueNum);
              for (int j = 0; j < length; j++) {
                iterator.next();
                iterator.set(fromQueue[j]);
              }
            }
          }
        } while (
            (fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
                != fromQueueEnd
        );
      }
    }
  }

  @Override
  public <N extends Number> void sort(List<N> list, SortStatistics stats) {
    if (list == null) {
      throw new NullArgumentException("list");
    }
    if (list instanceof IntList) {
      sort((IntList) list);
    } else {
      if (stats != null) {
        stats.sortStarting();
      }
      final int size = list.size();
      if (size < MIN_RADIX_SORT_SIZE) {
        if (stats != null) {
          stats.sortSwitchingAlgorithms();
        }
        Collections.sort(list, IntValueComparator.getInstance());
      } else {
        if (stats != null) {
          // One get and one set for each element
          stats.sortGetting(size);
          stats.sortSetting(size);
        }
        final int numProcessors;
        if (
            executor == null
                || size < MIN_CONCURRENCY_SIZE
                || (numProcessors = RuntimeUtils.getAvailableProcessors()) < MIN_CONCURRENCY_PROCESSORS
        ) {
          radixSort(
              size,
              new SingleTaskNumberRadixTable<>(size),
              new NumberListSource<>(size, list),
              null
          );
        } else {
          radixSort(
              size,
              new MultiTaskNumberRadixTable<>(size, numProcessors * TASKS_PER_PROCESSOR),
              new NumberListSource<>(size, list),
              executor
          );
        }
      }
      if (stats != null) {
        stats.sortEnding();
      }
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="N[]">
  static class NumberArraySource<N extends Number> extends Source<NumberRadixTable<N>> {

    // private final int size;
    private final N[] array;

    NumberArraySource(int size, N[] array) {
      // this.size  = size;
      this.array = array;
    }

    @Override
    final boolean useRandomAccess() {
      return true;
    }

    @Override
    final ImportDataResult importData(NumberRadixTable<N> table, int start, int end, int toTaskNum) {
      int bitsSeen = 0;
      int bitsNotSeen = 0;
      for (int i = start; i < end; i++) {
        int numInt = table.addToQueue(0, array[i], toTaskNum);
        bitsSeen |= numInt;
        bitsNotSeen |= numInt ^ 0xffffffff;
      }
      return new ImportDataResult(bitsSeen, bitsNotSeen);
    }

    @Override
    final void exportData(
        final NumberRadixTable<N> table,
        final int fromQueueStart,
        final int fromQueueEnd,
        final int start
    ) {
      final int numTasks = table.numTasks;
      int fromQueueNum = fromQueueStart;
      // Use indexed strategy
      int outIndex = start;
      do {
        for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
          N[] fromQueue = table.getFromQueue(fromTaskNum, fromQueueNum);
          if (fromQueue != null) {
            int length = table.getFromQueueLength(fromTaskNum, fromQueueNum);
            System.arraycopy(fromQueue, 0, array, outIndex, length);
            outIndex += length;
          }
        }
      } while (
          (fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
              != fromQueueEnd
      );
    }
  }

  @Override
  public <N extends Number> void sort(N[] array, SortStatistics stats) {
    if (stats != null) {
      stats.sortStarting();
    }
    final int size = array.length;
    if (size < MIN_RADIX_SORT_SIZE) {
      if (stats != null) {
        stats.sortSwitchingAlgorithms();
      }
      Arrays.sort(array, IntValueComparator.getInstance());
    } else {
      if (stats != null) {
        // One get and one set for each element
        stats.sortGetting(size);
        stats.sortSetting(size);
      }
      final int numProcessors;
      if (
          executor == null
              || size < MIN_CONCURRENCY_SIZE
              || (numProcessors = RuntimeUtils.getAvailableProcessors()) < MIN_CONCURRENCY_PROCESSORS
      ) {
        radixSort(
            size,
            new SingleTaskNumberRadixTable<>(size),
            new NumberArraySource<>(size, array),
            null
        );
      } else {
        radixSort(
            size,
            new MultiTaskNumberRadixTable<>(size, numProcessors * TASKS_PER_PROCESSOR),
            new NumberArraySource<>(size, array),
            executor
        );
      }
    }
    if (stats != null) {
      stats.sortEnding();
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="IntList">
  static class IntListSource extends Source<IntRadixTable> {

    private final int size;
    private final IntList list;
    private final boolean useRandomAccess;

    IntListSource(int size, IntList list) {
      this.size            = size;
      this.list            = list;
      this.useRandomAccess = size < Integer.MAX_VALUE && (list instanceof RandomAccess);
    }

    @Override
    final boolean useRandomAccess() {
      return useRandomAccess;
    }

    @Override
    final ImportDataResult importData(IntRadixTable table, int start, int end, int toTaskNum) {
      int bitsSeen = 0;
      int bitsNotSeen = 0;
      if (useRandomAccess) {
        for (int i = start; i < end; i++) {
          int numInt = table.addToQueue(0, list.getInt(i), toTaskNum);
          bitsSeen |= numInt;
          bitsNotSeen |= numInt ^ 0xffffffff;
        }
      } else {
        assert start == 0 && end == size : "Must import all in a single pass for iterator method";
        for (int number : list) {
          int numInt = table.addToQueue(0, number, toTaskNum);
          bitsSeen |= numInt;
          bitsNotSeen |= numInt ^ 0xffffffff;
        }
      }
      return new ImportDataResult(bitsSeen, bitsNotSeen);
    }

    @Override
    final void exportData(
        final IntRadixTable table,
        final int fromQueueStart,
        final int fromQueueEnd,
        final int start
    ) {
      final int numTasks = table.numTasks;
      int fromQueueNum = fromQueueStart;
      if (useRandomAccess) {
        // Use indexed strategy
        int outIndex = start;
        do {
          for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
            int[] fromQueue = table.getFromQueue(fromTaskNum, fromQueueNum);
            if (fromQueue != null) {
              int length = table.getFromQueueLength(fromTaskNum, fromQueueNum);
              for (int j = 0; j < length; j++) {
                list.set(outIndex++, fromQueue[j]);
              }
            }
          }
        } while (
            (fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
                != fromQueueEnd
        );
      } else {
        // Use iterator strategy
        assert start == 0 : "Must import all in a single pass for iterator method";
        ListIterator<Integer> iterator = list.listIterator();
        do {
          for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
            int[] fromQueue = table.getFromQueue(fromTaskNum, fromQueueNum);
            if (fromQueue != null) {
              int length = table.getFromQueueLength(fromTaskNum, fromQueueNum);
              for (int j = 0; j < length; j++) {
                iterator.next();
                iterator.set(fromQueue[j]);
              }
            }
          }
        } while (
            (fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
                != fromQueueEnd
        );
      }
    }
  }

  @Override
  public void sort(IntList list, SortStatistics stats) {
    if (stats != null) {
      stats.sortStarting();
    }
    final int size = list.size();
    if (size < MIN_RADIX_SORT_SIZE) {
      if (stats != null) {
        stats.sortSwitchingAlgorithms();
      }
      Collections.sort(list, IntValueComparator.getInstance());
    } else {
      if (stats != null) {
        // One get and one set for each element
        stats.sortGetting(size);
        stats.sortSetting(size);
      }
      final int numProcessors;
      if (
          executor == null
              || size < MIN_CONCURRENCY_SIZE
              || (numProcessors = RuntimeUtils.getAvailableProcessors()) < MIN_CONCURRENCY_PROCESSORS
      ) {
        radixSort(
            size,
            new SingleTaskIntRadixTable(size),
            new IntListSource(size, list),
            null
        );
      } else {
        radixSort(
            size,
            new MultiTaskIntRadixTable(size, numProcessors * TASKS_PER_PROCESSOR),
            new IntListSource(size, list),
            executor
        );
      }
    }
    if (stats != null) {
      stats.sortEnding();
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="int[]">
  static class IntArraySource extends Source<IntRadixTable> {

    // private final int size;
    private final int[] array;

    IntArraySource(int size, int[] array) {
      // this.size  = size;
      this.array = array;
    }

    @Override
    final boolean useRandomAccess() {
      return true;
    }

    @Override
    final ImportDataResult importData(IntRadixTable table, int start, int end, int toTaskNum) {
      int bitsSeen = 0;
      int bitsNotSeen = 0;
      for (int i = start; i < end; i++) {
        int numInt = table.addToQueue(0, array[i], toTaskNum);
        bitsSeen |= numInt;
        bitsNotSeen |= numInt ^ 0xffffffff;
      }
      return new ImportDataResult(bitsSeen, bitsNotSeen);
    }

    @Override
    final void exportData(
        final IntRadixTable table,
        final int fromQueueStart,
        final int fromQueueEnd,
        final int start
    ) {
      final int numTasks = table.numTasks;
      int fromQueueNum = fromQueueStart;
      // Use indexed strategy
      int outIndex = start;
      do {
        for (int fromTaskNum = 0; fromTaskNum < numTasks; fromTaskNum++) {
          int[] fromQueue = table.getFromQueue(fromTaskNum, fromQueueNum);
          if (fromQueue != null) {
            int length = table.getFromQueueLength(fromTaskNum, fromQueueNum);
            System.arraycopy(fromQueue, 0, array, outIndex, length);
            outIndex += length;
          }
        }
      } while (
          (fromQueueNum = (fromQueueNum + 1) & PASS_MASK)
              != fromQueueEnd
      );
    }
  }

  @Override
  public void sort(int[] array, SortStatistics stats) {
    if (stats != null) {
      stats.sortStarting();
    }
    final int size = array.length;
    if (size < MIN_RADIX_SORT_SIZE) {
      if (stats != null) {
        stats.sortSwitchingAlgorithms();
      }
      Arrays.sort(array);
    } else {
      if (stats != null) {
        // One get and one set for each element
        stats.sortGetting(size);
        stats.sortSetting(size);
      }
      final int numProcessors;
      if (
          executor == null
              || size < MIN_CONCURRENCY_SIZE
              || (numProcessors = RuntimeUtils.getAvailableProcessors()) < MIN_CONCURRENCY_PROCESSORS
      ) {
        radixSort(
            size,
            new SingleTaskIntRadixTable(size),
            new IntArraySource(size, array),
            null
        );
      } else {
        radixSort(
            size,
            new MultiTaskIntRadixTable(size, numProcessors * TASKS_PER_PROCESSOR),
            new IntArraySource(size, array),
            executor
        );
      }
    }
    if (stats != null) {
      stats.sortEnding();
    }
  }
  // </editor-fold>
}
