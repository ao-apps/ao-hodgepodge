package com.aoindustries.util;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;

/**
 * <code>BufferManager</code> manages a pool of <code>byte[]</code> and <code>char[]</code>
 * buffers that any <code>Thread</code> may use and then release.  This avoids the allocation
 * of memory for an operation that only needs a temporary buffer.  It is important that any
 * method using these buffers release the buffers in a <code>finally</code> block, otherwise
 * memory will build upon each request.
 *
 * @author  AO Industries, Inc.
 */
final public class BufferManager {

    /**
     * The number of buffers to initially allocate.
     */
    private static final int INITIAL_BUFFERS=32;

    /**
     * The size of buffers that are returned.
     */
    public static final int BUFFER_SIZE=4096;

    private static final Object
        bytesLock=new Object(),
        charsLock=new Object()
    ;

    /**
     * Keeps track of which buffers are currently in use.
     */
    private static boolean[] activeBytes=new boolean[INITIAL_BUFFERS];

    /**
     * Keeps track of which buffers are currently in use.
     */
    private static boolean[] activeChars=new boolean[INITIAL_BUFFERS];

    /**
     * Keeps a reference to all buffers that have been allocated.
     */
    private static byte[][] bytes=new byte[INITIAL_BUFFERS][];

    /**
     * Keeps a reference to all buffers that have been allocated.
     */
    private static char[][] chars=new char[INITIAL_BUFFERS][];

    /**
     * Make no instances.
     */
    private BufferManager() {
    }

    /**
     * Various statistics
     */
    private static long
        bytesUses=0,
        charsUses=0
    ;

    /**
     * Gets a <code>byte[]</code> of length <code>BUFFER_SIZE</code> that may
     * be temporarily used for any purpose.  Once done with the buffer,
     * <code>releaseBuffer</code> should be called, this is best accomplished
     * in a <code>finally</code> block.
     */
    public static byte[] getBytes() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getBytes()", null);
        try {
            synchronized(bytesLock) {
                bytesUses++;
                int len=activeBytes.length;
                for(int c=0;c<len;c++) {
                    if(!activeBytes[c]) {
                        if(bytes[c]==null) bytes[c]=new byte[BUFFER_SIZE];
                        activeBytes[c]=true;
                        return bytes[c];
                    }
                }
                // Make the number of buffers greater
                int newLen=len<<1;
                boolean[] newActiveBuffers=new boolean[newLen];
                System.arraycopy(activeBytes, 0, newActiveBuffers, 0, len);
                byte[][] newBuffers=new byte[newLen][];
                System.arraycopy(bytes, 0, newBuffers, 0, len);
                activeBytes=newActiveBuffers;
                bytes=newBuffers;

                bytes[len]=new byte[BUFFER_SIZE];
                activeBytes[len]=true;
                return bytes[len];
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets a <code>char[]</code> of length <code>BUFFER_SIZE</code> that may
     * be temporarily used for any purpose.  Once done with the buffer,
     * <code>releaseBuffer</code> should be called, this is best accomplished
     * in a <code>finally</code> block.
     */
    public static char[] getChars() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getChars()", null);
        try {
            synchronized(charsLock) {
                charsUses++;
                int len=activeChars.length;
                for(int c=0;c<len;c++) {
                    if(!activeChars[c]) {
                        if(chars[c]==null) chars[c]=new char[BUFFER_SIZE];
                        activeChars[c]=true;
                        return chars[c];
                    }
                }
                // Make the number of buffers greater
                int newLen=len<<1;
                boolean[] newActiveBuffers=new boolean[newLen];
                System.arraycopy(activeChars, 0, newActiveBuffers, 0, len);
                char[][] newBuffers=new char[newLen][];
                System.arraycopy(chars, 0, newBuffers, 0, len);
                activeChars=newActiveBuffers;
                chars=newBuffers;

                chars[len]=new char[BUFFER_SIZE];
                activeChars[len]=true;
                return chars[len];
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Releases a <code>byte[]</code> that was obtained by a call to
     * <code>getBytes</code>.
     *
     * @param  buffer  the <code>byte[]</code> to release
     *
     * @exception  IllegalArgumentException  if <code>buffer</code> is not active or was
     *             not returned by a call to <code>getBytes</code>
     */
    public static void release(byte[] buffer) {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "release(byte[])", null);
        try {
            synchronized(bytesLock) {
                int len=activeBytes.length;
                for(int c=0;c<len;c++) {
                    if(activeBytes[c] && bytes[c]==buffer) {
                        activeBytes[c]=false;
                        return;
                    }
                }
            }
            throw new IllegalArgumentException("buffer is not active or was not returned by a call to getBytes");
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Releases a <code>char[]</code> that was obtained by a call to
     * <code>getChars</code>.
     *
     * @param  buffer  the <code>char[]</code> to release
     *
     * @exception  IllegalArgumentException  if <code>buffer</code> is not active or was
     *             not returned by a call to <code>getChars</code>
     */
    public static void release(char[] buffer) {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "release(char[])", null);
        try {
            synchronized(charsLock) {
                int len=activeChars.length;
                for(int c=0;c<len;c++) {
                    if(activeChars[c] && chars[c]==buffer) {
                        activeChars[c]=false;
                        return;
                    }
                }
            }
            throw new IllegalArgumentException("buffer is not active or was not returned by a call to getChars");
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public static int getByteBufferCount() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getByteBufferCount()", null);
        try {
            synchronized(bytesLock) {
                int len=bytes.length;
                int pos=len-1;
                for(;pos>=0;pos--) {
                    if(bytes[pos]!=null) break;
                }
                return pos+1;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static long getByteBufferUses() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getByteBufferUses()", null);
        try {
            synchronized(bytesLock) {
                return bytesUses;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static int getByteBufferUsedCount() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getByteBufferUsedCount()", null);
        try {
            synchronized(bytesLock) {
                int len=activeBytes.length;
                int count=0;
                for(int c=0;c<len;c++) if(activeBytes[c]) count++;
                return count;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static int getCharBufferCount() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getCharBufferCount()", null);
        try {
            synchronized(charsLock) {
                int len=chars.length;
                int pos=len-1;
                for(;pos>=0;pos--) {
                    if(chars[pos]!=null) break;
                }
                return pos+1;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static long getCharBufferUses() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getCharBufferUses()", null);
        try {
            synchronized(charsLock) {
                return charsUses;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static int getCharBufferUsedCount() {
        Profiler.startProfile(Profiler.FAST, BufferManager.class, "getCharBufferUsedCount()", null);
        try {
            synchronized(charsLock) {
                int len=activeChars.length;
                int count=0;
                for(int c=0;c<len;c++) if(activeChars[c]) count++;
                return count;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}