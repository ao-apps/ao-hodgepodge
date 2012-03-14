/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
package com.aoindustries.io;

import com.aoindustries.util.BufferManager;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * I/O utilities.
 */
final public class IoUtils {

    /**
     * Make no instances.
     */
    private IoUtils() {}

    /**
     * copies without flush.
     *
     * @see #copy(java.io.InputStream, java.io.OutputStream, boolean)
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        return copy(in, out, false);
    }

    /**
     * Copies all information from one stream to another.  Internally reuses thread-local
     * buffers to avoid initial buffer zeroing cost and later garbage collection overhead.
     *
     * @return  the number of bytes copied
     *
     * @see  BufferManager#getBytes()
     */
    public static long copy(InputStream in, OutputStream out, boolean flush) throws IOException {
        byte[] buff = BufferManager.getBytes();
        try {
            long totalBytes = 0;
            int numBytes;
            while((numBytes = in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                out.write(buff, 0, numBytes);
                if(flush) out.flush();
                totalBytes += numBytes;
            }
            return totalBytes;
        } finally {
            BufferManager.release(buff);
        }
    }

    /**
     * Copies all information from one stream to another.  Internally reuses thread-local
     * buffers to avoid initial buffer zeroing cost and later garbage collection overhead.
     *
     * @return  the number of bytes copied
     *
     * @see  BufferManager#getChars()
     */
    public static long copy(Reader in, Writer out) throws IOException {
        char[] buff = BufferManager.getChars();
        try {
            long totalChars = 0;
            int numChars;
            while((numChars = in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                out.write(buff, 0, numChars);
                totalChars += numChars;
            }
            return totalChars;
        } finally {
            BufferManager.release(buff);
        }
    }

    /**
     * Copies all information from one stream to another.  Internally reuses thread-local
     * buffers to avoid initial buffer zeroing cost and later garbage collection overhead.
     *
     * @return  the number of bytes copied
     *
     * @see  BufferManager#getChars()
     */
    public static long copy(Reader in, StringBuilder out) throws IOException {
        char[] buff = BufferManager.getChars();
        try {
            long totalChars = 0;
            int numChars;
            while((numChars = in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                out.append(buff, 0, numChars);
                totalChars += numChars;
            }
            return totalChars;
        } finally {
            BufferManager.release(buff);
        }
    }

    /**
     * Copies all information from one stream to another.  Internally reuses thread-local
     * buffers to avoid initial buffer zeroing cost and later garbage collection overhead.
     *
     * @return  the number of bytes copied
     *
     * @see  BufferManager#getChars()
     */
    public static long copy(Reader in, ChainWriter out) throws IOException {
        char[] buff = BufferManager.getChars();
        try {
            long totalChars = 0;
            int numChars;
            while((numChars = in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                out.write(buff, 0, numChars);
                totalChars += numChars;
            }
            return totalChars;
        } finally {
            BufferManager.release(buff);
        }
    }

    /**
     * readFully for any stream.
     */
    // @ThreadSafe
    public static void readFully(InputStream in, byte[] buffer) throws IOException {
        readFully(in, buffer, 0, buffer.length);
    }

    /**
     * readFully for any stream.
     */
    // @ThreadSafe
    public static void readFully(InputStream in, byte[] buffer, int off, int len) throws IOException {
        while(len>0) {
            int count = in.read(buffer, off, len);
            if(count==-1) throw new EOFException();
            off += count;
            len -= count;
        }
    }
}
