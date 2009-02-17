/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.util;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>BufferManager</code> manages a pool of <code>byte[]</code> and <code>char[]</code>
 * buffers that any <code>Thread</code> may use and then release.  This avoids the allocation
 * of memory for an operation that only needs a temporary buffer.
 *
 * @author  AO Industries, Inc.
 */
final public class BufferManager {

    /**
     * The maximum number of buffers to keep for reuse.
     */
    private static final int MAXIMUM_BUFFERS=1024;

    /**
     * The size of buffers that are returned.
     */
    public static final int BUFFER_SIZE=4096;

    private static final List<byte[]> bytes = new ArrayList<byte[]>();

    private static final List<char[]> chars = new ArrayList<char[]>();

    /**
     * Make no instances.
     */
    private BufferManager() {
    }

    /**
     * Various statistics
     */
    private static long
        bytesCreates=0,
        bytesUses=0,
        charsCreates=0,
        charsUses=0
    ;

    /**
     * Gets a <code>byte[]</code> of length <code>BUFFER_SIZE</code> that may
     * be temporarily used for any purpose.  Once done with the buffer,
     * <code>releaseBuffer</code> should be called, this is best accomplished
     * in a <code>finally</code> block.
     */
    public static byte[] getBytes() {
        synchronized(bytes) {
            bytesUses++;
            int len = bytes.size();
            if(len==0) {
                bytesCreates++;
                return new byte[BUFFER_SIZE];
            }
            return bytes.remove(len-1);
        }
    }

    /**
     * Gets a <code>char[]</code> of length <code>BUFFER_SIZE</code> that may
     * be temporarily used for any purpose.  Once done with the buffer,
     * <code>releaseBuffer</code> should be called, this is best accomplished
     * in a <code>finally</code> block.
     */
    public static char[] getChars() {
        synchronized(chars) {
            charsUses++;
            int len = chars.size();
            if(len==0) {
                charsCreates++;
                return new char[BUFFER_SIZE];
            }
            return chars.remove(len-1);
        }
    }

    /**
     * Releases a <code>byte[]</code> that was obtained by a call to
     * <code>getBytes</code>.
     *
     * @param  buffer  the <code>byte[]</code> to release
     */
    public static void release(byte[] buffer) {
        synchronized(bytes) {
            // Error of already in the buffer list
            assert !inList(buffer);
            if(bytes.size()<MAXIMUM_BUFFERS) bytes.add(buffer);
        }
    }
    private static boolean inList(byte[] buffer) {
        for(byte[] inList : bytes) if(inList==buffer) return true;
        return false;
    }

    /**
     * Releases a <code>char[]</code> that was obtained by a call to
     * <code>getChars</code>.
     *
     * @param  buffer  the <code>char[]</code> to release
     */
    public static void release(char[] buffer) {
        synchronized(chars) {
            // Error of already in the buffer list
            assert !inList(buffer);
            if(chars.size()<MAXIMUM_BUFFERS) chars.add(buffer);
        }
    }
    private static boolean inList(char[] buffer) {
        for(char[] inList : chars) if(inList==buffer) return true;
        return false;
    }

    public static long getByteBufferCreates() {
        synchronized(bytes) {
            return bytesCreates;
        }
    }

    public static long getByteBufferUses() {
        synchronized(bytes) {
            return bytesUses;
        }
    }

    public static long getCharBufferCreates() {
        synchronized(chars) {
            return charsCreates;
        }
    }

    public static long getCharBufferUses() {
        synchronized(chars) {
            return charsUses;
        }
    }
}
