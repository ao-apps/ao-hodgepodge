/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013  AO Industries, Inc.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <code>BufferManager</code> manages a reusable pool of <code>byte[]</code> and <code>char[]</code>
 * buffers.  The buffers are stored as <code>ThreadLocal</code> to avoid overhead in NUMA architectures.
 * This avoids the repetitive allocation of memory for an operation that only needs a temporary buffer.
 *
 * The buffers are not cleared between invocations so the results of previous operations may be available
 * to additional callers.  On the scale of security versus performance, this is biased toward performance.
 * However, being thread local there remains some control over the visibility of the data.
 * 
 * Do not use if intra-thread security is more important than performance.
 *
 * @author  AO Industries, Inc.
 */
final public class BufferManager {

    /**
     * The maximum number of buffers to keep for reuse, per thread.
     */
    private static final int MAXIMUM_BUFFERS_PER_THREAD = 16;

    /**
     * The size of buffers that are returned.
     */
    public static final int BUFFER_SIZE = 4096;

    private static final ThreadLocal<List<byte[]>> bytes = new ThreadLocal<List<byte[]>>() {
        @Override
        public List<byte[]> initialValue() {
            return new ArrayList<byte[]>(MAXIMUM_BUFFERS_PER_THREAD);
        }
    };

    private static final ThreadLocal<List<char[]>> chars = new ThreadLocal<List<char[]>>() {
        @Override
        public List<char[]> initialValue() {
            return new ArrayList<char[]>(MAXIMUM_BUFFERS_PER_THREAD);
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
    private static AtomicLong
        bytesCreates = new AtomicLong(),
        bytesUses = new AtomicLong(),
        charsCreates = new AtomicLong(),
        charsUses = new AtomicLong()
    ;

    /**
     * Gets a <code>byte[]</code> of length <code>BUFFER_SIZE</code> that may
     * be temporarily used for any purpose.  Once done with the buffer,
     * <code>releaseBuffer</code> should be called, this is best accomplished
     * in a <code>finally</code> block.
	 * The buffer is not necessarily zero-filled and may contain data from a previous use.
     */
    public static byte[] getBytes() {
        bytesUses.getAndIncrement();
        List<byte[]> myBytes = bytes.get();
        int len = myBytes.size();
        if(len==0) {
            bytesCreates.getAndIncrement();
            return new byte[BUFFER_SIZE];
        }
        return myBytes.remove(len-1);
    }

    /**
     * Gets a <code>char[]</code> of length <code>BUFFER_SIZE</code> that may
     * be temporarily used for any purpose.  Once done with the buffer,
     * <code>releaseBuffer</code> should be called, this is best accomplished
     * in a <code>finally</code> block.
	 * The buffer is not necessarily zero-filled and may contain data from a previous use.
     */
    public static char[] getChars() {
        charsUses.getAndIncrement();
        List<char[]> myChars = chars.get();
        int len = myChars.size();
        if(len==0) {
            charsCreates.getAndIncrement();
            return new char[BUFFER_SIZE];
        }
        return myChars.remove(len-1);
    }

	/**
	 * @deprecated  May obtain greater performance by avoiding zero fill on non-sensitive data.
	 */
	@Deprecated
    public static void release(byte[] buffer) {
		release(buffer, true);
	}

	/**
     * Releases a <code>byte[]</code> that was obtained by a call to
     * <code>getBytes</code>.
     *
     * @param  buffer  the <code>byte[]</code> to release
	 * @param  zeroFill  if the data in the buffer may be sensitive, it is best to zero-fill the buffer on release.
     */
    public static void release(byte[] buffer, boolean zeroFill) {
        List<byte[]> myBytes = bytes.get();
        assert buffer.length==BUFFER_SIZE;
        assert !inList(myBytes, buffer); // Error if already in the buffer list
        if(myBytes.size()<MAXIMUM_BUFFERS_PER_THREAD) {
			if(zeroFill) Arrays.fill(buffer, 0, BUFFER_SIZE, (byte)0);
			myBytes.add(buffer);
		}
    }
    private static boolean inList(List<byte[]> myBytes, byte[] buffer) {
        for(byte[] inList : myBytes) if(inList==buffer) return true;
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
     * Releases a <code>char[]</code> that was obtained by a call to
     * <code>getChars</code>.
     *
     * @param  buffer  the <code>char[]</code> to release
	 * @param  zeroFill  if the data in the buffer may be sensitive, it is best to zero-fill the buffer on release.
     */
    public static void release(char[] buffer, boolean zeroFill) {
        List<char[]> myChars = chars.get();
        assert buffer.length==BUFFER_SIZE;
        assert !inList(myChars, buffer); // Error if already in the buffer list
        if(myChars.size()<MAXIMUM_BUFFERS_PER_THREAD) {
			if(zeroFill) Arrays.fill(buffer, 0, BUFFER_SIZE, (char)0);
			myChars.add(buffer);
		}
    }
    private static boolean inList(List<char[]> myChars, char[] buffer) {
        for(char[] inList : myChars) if(inList==buffer) return true;
        return false;
    }

    public static long getByteBufferCreates() {
        return bytesCreates.get();
    }

    public static long getByteBufferUses() {
        return bytesUses.get();
    }

    public static long getCharBufferCreates() {
        return charsCreates.get();
    }

    public static long getCharBufferUses() {
        return charsUses.get();
    }
}
