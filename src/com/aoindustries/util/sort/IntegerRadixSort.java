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

import com.aoindustries.util.IntList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * A radix sort implementation for numeric data, sorting by its integer representation.
 *
 * Although a very different implementation, this topic is discussed at
 * {@link http://erik.gorset.no/2011/04/radix-sort-is-faster-than-quicksort.html}
 * with source provided at {@link https://github.com/gorset/radix/blob/master/Radix.java}
 *
 * TODO: Integrate concurrent implementation into this codebase.
 *
 * @author  AO Industries, Inc.
 */
final public class IntegerRadixSort extends IntegerSortAlgorithm {

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

	private static final IntegerRadixSort instance = new IntegerRadixSort();

    public static IntegerRadixSort getInstance() {
        return instance;
    }

    private IntegerRadixSort() {
    }

	@Override
	public boolean isStable() {
		return true;
	}

	// <editor-fold defaultstate="collapsed" desc="Sorter">
	abstract static class Sorter {

		protected final int size;

		// Must be power of two and less than or equal to 32
		/*
		protected final int BITS_PER_PASS;
		protected final int PASS_SIZE;
		protected final int PASS_MASK;
		 */

		protected final int startQueueLength;

		protected int[] fromQueueLengths;
		protected int[] toQueueLengths;

		/**
		 * Set of all bits seen to skip bit ranges that won't sort.
		 */
		protected int bitsSeen;

		/**
		 * Set of all bits not seen to skip bit ranges that won't sort.
		 */
		protected int bitsNotSeen;

		Sorter(int size) {
			this.size = size;

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
				int sql = size >>> (BITS_PER_PASS-1); // Double the average size to allow for somewhat uneven distribution before growing arrays
				if(sql<MINIMUM_START_QUEUE_LENGTH) sql = MINIMUM_START_QUEUE_LENGTH;
				if(sql>size) sql = size;
				this.startQueueLength = sql;
			}

			this.fromQueueLengths = new int[PASS_SIZE];
			this.toQueueLengths = new int[PASS_SIZE];
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
		void swapQueues() {
			int[] tempLengths = fromQueueLengths;
			fromQueueLengths = toQueueLengths;
			toQueueLengths = tempLengths;
		}

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

	// <editor-fold defaultstate="collapsed" desc="NumberSorter">
	abstract static class NumberSorter<N extends Number> extends Sorter {

		protected N[][] fromQueues;
		protected N[][] toQueues;

		@SuppressWarnings("unchecked")
		NumberSorter(int size) {
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

	// <editor-fold defaultstate="collapsed" desc="List<N>">
	static class NumberListSorter<N extends Number> extends NumberSorter<N> {

		private final List<N> list;
		private final boolean useRandomAccess;

		NumberListSorter(List<N> list) {
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
				new NumberListSorter<N>(list).sort();
			}
			if(stats!=null) stats.sortEnding();
		}
    }
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="N[]">
	static class NumberArraySorter<N extends Number> extends NumberSorter<N> {

		private final N[] array;

		NumberArraySorter(N[] array) {
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
			new NumberArraySorter<N>(array).sort();
		}
		if(stats!=null) stats.sortEnding();
    }
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="IntSorter">
	abstract static class IntSorter extends Sorter {

		protected int[][] fromQueues;
		protected int[][] toQueues;

		IntSorter(int size) {
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

	// <editor-fold defaultstate="collapsed" desc="IntList">
	static class IntListSorter extends IntSorter {

		private final IntList list;
		private final boolean useRandomAccess;

		IntListSorter(IntList list) {
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
			new IntListSorter(list).sort();
		}
		if(stats!=null) stats.sortEnding();
    }
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="int[]">
	static class IntArraySorter extends IntSorter {

		private final int[] array;

		IntArraySorter(int[] array) {
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
			new IntArraySorter(array).sort();
		}
		if(stats!=null) stats.sortEnding();
    }
	// </editor-fold>
}
