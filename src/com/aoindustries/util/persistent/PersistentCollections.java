/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util.persistent;

import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * A set of static utility methods to help in the selection, creation, and management
 * of persistent collections.
 *
 * @author  AO Industries, Inc.
 */
public class PersistentCollections {

    private static final Logger logger = Logger.getLogger(PersistentCollections.class.getName());

    private PersistentCollections() {
    }

    // <editor-fold desc="Protected byte[] manipulation methods">
    static void charToBuffer(char ch, byte[] ioBuffer, int off) {
        ioBuffer[off] = (byte)(ch >>> 8);
        ioBuffer[off+1] = (byte)ch;
    }

    static char bufferToChar(byte[] ioBuffer, int off) {
        return
            (char)(
                ((ioBuffer[off+0]&255) << 8)
                + (ioBuffer[off+1]&255)
            )
        ;
    }

    static void shortToBuffer(short s, byte[] ioBuffer, int off) {
        ioBuffer[off] = (byte)(s >>> 8);
        ioBuffer[off+1] = (byte)s;
    }

    static short bufferToShort(byte[] ioBuffer, int off) {
        return
            (short)(
                ((ioBuffer[off+0]&255) << 8)
                + (ioBuffer[off+1]&255)
            )
        ;
    }

    static void intToBuffer(int i, byte[] ioBuffer, int off) {
        ioBuffer[off] = (byte)(i >>> 24);
        ioBuffer[off+1] = (byte)(i >>> 16);
        ioBuffer[off+2] = (byte)(i >>> 8);
        ioBuffer[off+3] = (byte)i;
    }

    static int bufferToInt(byte[] ioBuffer, int off) {
        return
              ((ioBuffer[off]&255) << 24)
            + ((ioBuffer[off+1]&255) << 16)
            + ((ioBuffer[off+2]&255) << 8)
            + (ioBuffer[off+3]&255)
        ;
    }

    static void longToBuffer(long l, byte[] ioBuffer, int off) {
        ioBuffer[off] = (byte)(l >>> 56);
        ioBuffer[off+1] = (byte)(l >>> 48);
        ioBuffer[off+2] = (byte)(l >>> 40);
        ioBuffer[off+3] = (byte)(l >>> 32);
        ioBuffer[off+4] = (byte)(l >>> 24);
        ioBuffer[off+5] = (byte)(l >>> 16);
        ioBuffer[off+6] = (byte)(l >>> 8);
        ioBuffer[off+7] = (byte)l;
    }

    static long bufferToLong(byte[] ioBuffer, int off) {
        return
              ((ioBuffer[off]&255L) << 56)
            + ((ioBuffer[off+1]&255L) << 48)
            + ((ioBuffer[off+2]&255L) << 40)
            + ((ioBuffer[off+3]&255L) << 32)
            + ((ioBuffer[off+4]&255L) << 24)
            + ((ioBuffer[off+5]&255L) << 16)
            + ((ioBuffer[off+6]&255L) << 8)
            + (ioBuffer[off+7]&255L)
        ;
    }

    private static final byte[] zeros = new byte[4096];

    /**
     * Writes the requested number of zeros to the provided output.
     */
    static void fillZeros(DataOutput out, long count) throws IOException {
        if(count<0) throw new IllegalArgumentException("count<0: "+count);
        while(count>4096) {
            out.write(zeros, 0, 4096);
            count -= 4096;
        }
        if(count>0) out.write(zeros, 0, (int)count);
    }

    /**
     * Writes the requested number of zeros to the provided buffer.
     */
    static void fillZeros(ByteBuffer buffer, long count) throws IOException {
        if(count<0) throw new IllegalArgumentException("count<0: "+count);
        while(count>4096) {
            buffer.put(zeros, 0, 4096);
            count -= 4096;
        }
        if(count>0) buffer.put(zeros, 0, (int)count);
    }

    /**
     * Fully reads a buffer.
     */
    static void readFully(InputStream in, byte[] buffer, int off, int len) throws IOException {
        while(len>0) {
            int count = in.read(buffer, off, len);
            if(count==-1) throw new EOFException();
            off += count;
            len -= count;
        }
    }

    /**
     * Checks if the subrange of two byte arrays is equal.
     */
    static boolean equals(byte[] b1, byte[] b2, int off, int len) {
        for(int end=off+len; off<end; off++) {
            if(b1[off]!=b2[off]) return false;
        }
        return true;
    }
    // </editor-fold>

    /**
     * Selects the most efficient temporary <code>PersistentBuffer</code> for the current
     * machine and the provided maximum buffer size.  The buffer will be backed by a temporary
     * file that will be deleted on buffer close or JVM shutdown.  The order of preference is:
     * <ol>
     *   <li><code>MappedPersistentBuffer</code></li>
     *   <li><code>LargeMappedPersistentBuffer</code></li>
     *   <li><code>RandomAccessFileBuffer</code></li>
     * </ol>
     *
     * @param maximumCapacity The maximum size of data that may be stored in the
     *                        buffer.  To ensure no limits, use <code>Long.MAX_VALUE</code>.
     */
    public static PersistentBuffer getPersistentBuffer(long maximumCapacity) throws IOException {
        // If < 1 GB, use mapped buffer
        if(maximumCapacity<(1L<<30)) {
            return new MappedPersistentBuffer();
        }
        // No mmap for 32-bit
        String arch = System.getProperty("os.arch");
        if(
            "i386".equals(arch)
        ) {
            return new RandomAccessFileBuffer();
        }
        // Use mmap for 64-bit
        if(
            !"x86_64".equals(arch)
        ) {
            logger.warning("Unexpected value for system property os.arch, assuming 64-bit virtual machine: os.arch="+arch);
        }
        return new LargeMappedPersistentBuffer();
    }

    /**
     * Selects the most efficient <code>PersistentBuffer</code> for the current
     * machine and the provided maximum buffer size.  The order of preference is:
     * <ol>
     *   <li><code>MappedPersistentBuffer</code></li>
     *   <li><code>LargeMappedPersistentBuffer</code></li>
     *   <li><code>RandomAccessFileBuffer</code></li>
     * </ol>
     *
     * @param maximumCapacity The maximum size of data that may be stored in the
     *                        buffer.  If the random access file is larger than this value,
     *                        the length of the file is used instead.
     *                        To ensure no limits, use <code>Long.MAX_VALUE</code>.
     */
    public static PersistentBuffer getPersistentBuffer(RandomAccessFile raf, boolean readOnly, long maximumCapacity) throws IOException {
        if(maximumCapacity<(1L<<30)) {
            long len = raf.length();
            if(maximumCapacity<len) maximumCapacity = len;
        }
        // If < 1 GB, use mapped buffer
        if(maximumCapacity<(1L<<30)) {
            return new MappedPersistentBuffer(raf, readOnly);
        }
        // No mmap for 32-bit
        String arch = System.getProperty("os.arch");
        if(
            "i386".equals(arch)
        ) {
            return new RandomAccessFileBuffer(raf, readOnly);
        }
        // Use mmap for 64-bit
        if(
            !"x86_64".equals(arch)
        ) {
            logger.warning("Unexpected value for system property os.arch, assuming 64-bit virtual machine: os.arch="+arch);
        }
        return new LargeMappedPersistentBuffer(raf, readOnly);
    }

    /**
     * Selects the most efficient <code>Serializer</code> for the provided class.
     */
    public static <E> Serializer<E> getSerializer(Class<E> type) {
        if(type==Boolean.class) return (Serializer<E>)new BooleanSerializer();
        if(type==Byte.class) return (Serializer<E>)new ByteSerializer();
        if(type==Character.class) return (Serializer<E>)new CharacterSerializer();
        if(type==Double.class) return (Serializer<E>)new DoubleSerializer();
        if(type==Float.class) return (Serializer<E>)new FloatSerializer();
        if(type==Integer.class) return (Serializer<E>)new IntegerSerializer();
        if(type==Long.class) return (Serializer<E>)new LongSerializer();
        if(type==Short.class) return (Serializer<E>)new ShortSerializer();
        // Arrays
        Class<?> componentType = type.getComponentType();
        if(componentType!=null) {
            if(componentType==Byte.class) return (Serializer<E>)new ByteArraySerializer();
            if(componentType==Character.class) return (Serializer<E>)new CharArraySerializer();
        }
        // Default Java serialization
        return new ObjectSerializer<E>(type);
    }

    /**
     * Gets the most efficient <code>PersistentBlockBuffer</code> for the provided
     * provided <code>Serializer</code>.  If using fixed record sizes, the size of
     * the block buffer is rounded up to the nearest power of two, to help
     * alignment with system page tables.
     *
     * @param serializer            The <code>Serializer</code> that will be used to write to the blocks
     * @param pbuffer               The <code>PersistenceBuffer</code> that will be wrapped by the block buffer
     * @param additionalBlockSpace  The maximum additional space needed beyond the space used by the serializer.  This may be used
     *                              for linked list pointers, for example.
     */
    public static PersistentBlockBuffer getPersistentBlockBuffer(Serializer<?> serializer, PersistentBuffer pbuffer, long additionalBlockSpace) throws IOException {
        if(additionalBlockSpace<0) throw new IllegalArgumentException("additionalBlockSpace<0: "+additionalBlockSpace);
        // Use power-of-two fixed size blocks if possible
        if(serializer.isFixedSerializedSize()) return getRandomAccessPersistentBlockBuffer(serializer, pbuffer, additionalBlockSpace);
        // Then use dynamic sized blocks
        return new DynamicPersistentBlockBuffer(pbuffer);
    }

    /**
     * Gets the most efficient <code>RandomAccessPersistentBlockBuffer</code> for the provided
     * provided <code>Serializer</code>.  The serializer must be provide a fixed serializer size.
     * The size of the block buffer is rounded up to the nearest power of two, to help alignment
     * with system page tables.
     *
     * @param serializer            The <code>Serializer</code> that will be used to write to the blocks
     * @param pbuffer               The <code>PersistenceBuffer</code> that will be wrapped by the block buffer
     * @param additionalBlockSpace  The maximum additional space needed beyond the space used by the serializer.  This may be used
     *                              for linked list pointers, for example.
     */
    public static RandomAccessPersistentBlockBuffer getRandomAccessPersistentBlockBuffer(Serializer<?> serializer, PersistentBuffer pbuffer, long additionalBlockSpace) throws IOException {
        if(additionalBlockSpace<0) throw new IllegalArgumentException("additionalBlockSpace<0: "+additionalBlockSpace);
        // Use power-of-two fixed size blocks if possible
        if(!serializer.isFixedSerializedSize()) throw new IllegalArgumentException("serializer does not created fixed size output");
        long serSize = serializer.getSerializedSize(null);
        long minimumSize = serSize + additionalBlockSpace;
        if(minimumSize<0) throw new AssertionError("Long wraparound: "+serSize+"+"+minimumSize+"="+minimumSize);
        long highestOneBit = Long.highestOneBit(minimumSize);
        return new FixedPersistentBlockBuffer(
            pbuffer,
            highestOneBit==(1L<<62)
            ? minimumSize           // In range 2^62-2^63-1, cannot round up to next highest, use minimum size
            : minimumSize==highestOneBit
            ? minimumSize           // minimumSize is a power of two
            : (highestOneBit<<1)    // use next-highest power of two
        );
    }
}
