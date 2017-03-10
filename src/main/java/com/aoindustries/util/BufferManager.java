/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.util;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * {@code BufferManager} manages a reusable pool of {@code byte[]} and {@code char[]} buffers.
 * This avoids the repetitive allocation of memory for an operation that only needs a temporary buffer.
 * The buffers are stored as {@code ThreadLocal} to maximize cache locality.
 * </p>
 * <p>
 * Do not use if intra-thread security is more important than performance.
 * </p>
 * <p>
 * The buffers are not necessarily cleared between invocations so the results of previous operations may be available
 * to additional callers.  On the scale of security versus performance, this is biased toward performance.
 * However, being thread local there remains some control over the visibility of the data.
 * </p>
 * <p>
 * Buffers should not be passed between threads.
 * Giving a thread a buffer you didn't get from it could result in a memory or information leak.
 * The number of buffers per thread is limited to avoid a complete runaway memory leak, but keeping
 * buffers to a single thread is optimal.
 * </p>
 * <p>
 * Under no circumstances should a buffer be released more than once.  This may result
 * in the buffer being allocated twice at the same time, with resulting data corruption.
 * </p>
 * <p>
 * The Java virtual machine has improved greatly over the years.  However, we still believe
 * this buffer management to be valuable to reduce garbage collection pressure.  If this ever
 * proves to not be the case, the implementation here can be simply changed to create new
 * arrays on each use.
 * </p>
 *
 * TODO: Java 1.7+: Implement as concurrent queue/deque instead of thread locals?
 *
 * @author  AO Industries, Inc.
 */
final public class BufferManager {

	/**
	 * The size of buffers that are returned.
	 */
	public static final int BUFFER_SIZE = 4096;

	/**
	 * The maximum number of retained buffers per thread.
	 */
	private static final int MAX_BUFFERS_PER_THREAD = 32;

	private static final ThreadLocal<Deque<byte[]>> bytes = new ThreadLocal<Deque<byte[]>>() {
		@Override
		public Deque<byte[]> initialValue() {
			return new ArrayDeque<byte[]>(MAX_BUFFERS_PER_THREAD);
		}
	};

	private static final ThreadLocal<Deque<char[]>> chars = new ThreadLocal<Deque<char[]>>() {
		@Override
		public Deque<char[]> initialValue() {
			return new ArrayDeque<char[]>(MAX_BUFFERS_PER_THREAD);
		}
	};

	/**
	 * Make no instances.
	 */
	private BufferManager() {
	}

	/**
	 * Various statistics
	 */
	private static final AtomicLong
		bytesCreates = new AtomicLong(),
		bytesUses = new AtomicLong(),
		bytesDiscards = new AtomicLong(),
		bytesZeroFills = new AtomicLong(),
		charsCreates = new AtomicLong(),
		charsUses = new AtomicLong(),
		charsDiscards = new AtomicLong(),
		charsZeroFills = new AtomicLong()
	;

	/**
	 * Gets a {@code byte[]] of length {@code BUFFER_SIZE} that may
	 * be temporarily used for any purpose.  Once done with the buffer,
	 * {@code release} should be called, this is best accomplished
	 * in a {@code finally} block.
	 * The buffer is not necessarily zero-filled and may contain data from a previous use.
	 */
	public static byte[] getBytes() {
		bytesUses.getAndIncrement();
		byte[] buffer = bytes.get().poll();
		if(buffer == null) {
			bytesCreates.getAndIncrement();
			buffer = new byte[BUFFER_SIZE];
		}
		return buffer;
	}

	/**
	 * Gets a {@code char[]} of length {@code BUFFER_SIZE} that may
	 * be temporarily used for any purpose.  Once done with the buffer,
	 * {@code release} should be called, this is best accomplished
	 * in a {@code finally} block.
	 * The buffer is not necessarily zero-filled and may contain data from a previous use.
	 */
	public static char[] getChars() {
		charsUses.getAndIncrement();
		char[] buffer = chars.get().poll();
		if(buffer == null) {
			charsCreates.getAndIncrement();
			buffer = new char[BUFFER_SIZE];
		}
		return buffer;
	}

	/**
	 * @deprecated  May obtain greater performance by avoiding zero fill on non-sensitive data.
	 */
	@Deprecated
	public static void release(byte[] buffer) {
		release(buffer, true);
	}

	/**
	 * Releases a {@code byte[]} that was obtained by a call to
	 * {@code getBytes}.  A buffer must not be released more than once.
	 *
	 * @param  buffer  the {@code byte[]} to release
	 * @param  zeroFill  if the data in the buffer may be sensitive, it is best to zero-fill the buffer on release.
	 */
	public static void release(byte[] buffer, boolean zeroFill) {
		Deque<byte[]> myBytes = bytes.get();
		if(buffer.length != BUFFER_SIZE) throw new IllegalArgumentException();
		assert !inQueue(myBytes, buffer); // Error if already in the buffer list
		if(myBytes.size() >= MAX_BUFFERS_PER_THREAD) {
			bytesDiscards.getAndIncrement();
		} else {
			if(zeroFill) {
				bytesZeroFills.getAndIncrement();
				Arrays.fill(buffer, 0, BUFFER_SIZE, (byte)0);
			}
			myBytes.add(buffer);
		}
	}
	private static boolean inQueue(Iterable<byte[]> myBytes, byte[] buffer) {
		for(byte[] inQueue : myBytes) if(inQueue==buffer) return true;
		return false;
	}

	/**
	 * @deprecated  May obtain greater performance by avoiding zero fill on non-sensitive data.
	 */
	@Deprecated
	public static void release(char[] buffer) {
		release(buffer, true);
	}

	/**
	 * Releases a {@code char[]} that was obtained by a call to
	 * {@code getChars}.  A buffer must not be released more than once.
	 *
	 * @param  buffer  the {@code char[]} to release
	 * @param  zeroFill  if the data in the buffer may be sensitive, it is best to zero-fill the buffer on release.
	 */
	public static void release(char[] buffer, boolean zeroFill) {
		Deque<char[]> myChars = chars.get();
		if(buffer.length != BUFFER_SIZE) throw new IllegalArgumentException();
		assert !inQueue(myChars, buffer); // Error if already in the buffer list
		if(myChars.size() >= MAX_BUFFERS_PER_THREAD) {
			charsDiscards.getAndIncrement();
		} else {
			if(zeroFill) {
				charsZeroFills.getAndIncrement();
				Arrays.fill(buffer, 0, BUFFER_SIZE, (char)0);
			}
			myChars.add(buffer);
		}
	}
	private static boolean inQueue(Iterable<char[]> myChars, char[] buffer) {
		for(char[] inQueue : myChars) if(inQueue == buffer) return true;
		return false;
	}

	/**
	 * Gets the number of {@code byte[]} buffers instantiated.
	 */
	public static long getByteBufferCreates() {
		return bytesCreates.get();
	}

	/**
	 * Gets the number of time {@code byte[]} buffers have been used.
	 */
	public static long getByteBufferUses() {
		return bytesUses.get();
	}

	/**
	 * Gets the number of time {@code byte[]} buffers have been discarded on release.
	 */
	public static long getByteBufferDiscards() {
		return bytesDiscards.get();
	}

	/**
	 * Gets the number of time {@code byte[]} buffers have been zero-filled on release.
	 */
	public static long getByteBufferZeroFills() {
		return bytesZeroFills.get();
	}

	/**
	 * Gets the number of {@code char[]} buffers instantiated.
	 */
	public static long getCharBufferCreates() {
		return charsCreates.get();
	}

	/**
	 * Gets the number of time {@code char[]} buffers have been used.
	 */
	public static long getCharBufferUses() {
		return charsUses.get();
	}

	/**
	 * Gets the number of time {@code char[]} buffers have been discarded on release.
	 */
	public static long getCharBufferDiscards() {
		return charsDiscards.get();
	}

	/**
	 * Gets the number of time {@code char[]} buffers have been zero-filled on release.
	 */
	public static long getCharBufferZeroFills() {
		return charsZeroFills.get();
	}
}
