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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses a set of <code>MappedByteBuffer</code> for persistence.  Each buffer
 * covers a maximum of 2^30 bytes.  This handles mapping of up to
 * 2^30 * 2^31-2 bytes.
 *
 * @see  MappedPersistentBuffer
 *
 * @author  AO Industries, Inc.
 */
public class LargeMappedPersistentBuffer implements PersistentBuffer {

    private static final Logger logger = Logger.getLogger(LargeMappedPersistentBuffer.class.getName());

    // For testing, change this value to something smaller, like 12.  This will
    // allow the testing of the buffer boundary conditions.
    // 12 matches Linux page size of 4096 bytes and still performs well.
    // 10 performed less consistently, with frequently higher CPU usage.
    private static final int BUFFER_NUM_BIT_SHIFT = 30; // 12 for testing.
    private static final int BUFFER_SIZE = 1<<BUFFER_NUM_BIT_SHIFT;
    private static final int BUFFER_INDEX_MASK = BUFFER_SIZE-1;

    private final File tempFile;
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private final List<MappedByteBuffer> mappedBuffers = new ArrayList<MappedByteBuffer>();
    private final boolean readOnly;
    private boolean closed;

    /**
     * Creates a read-write buffer backed by a temporary file.  The temporary
     * file will be deleted when this buffer is closed or on JVM shutdown.
     */
    public LargeMappedPersistentBuffer() throws IOException {
        tempFile = File.createTempFile("RandomAccessFileBuffer", null);
        tempFile.deleteOnExit();
        raf = new RandomAccessFile(tempFile, "rw");
        channel = raf.getChannel();
        readOnly = false;
        // Lock the file
        channel.lock(0L, Long.MAX_VALUE, false);
        fillMappedBuffers();
    }

    /**
     * Creates a read-write buffer.
     */
    public LargeMappedPersistentBuffer(String name) throws IOException {
        this(new RandomAccessFile(name, "rw"), false);
    }

    /**
     * Creates a buffer.
     */
    public LargeMappedPersistentBuffer(String name, boolean readOnly) throws IOException {
        this(new RandomAccessFile(name, readOnly ? "r" : "rw"), readOnly);
    }

    /**
     * Creates a read-write buffer.
     */
    public LargeMappedPersistentBuffer(File file) throws IOException {
        this(new RandomAccessFile(file, "rw"), false);
    }

    /**
     * Creates a buffer.
     */
    public LargeMappedPersistentBuffer(File file, boolean readOnly) throws IOException {
        this(new RandomAccessFile(file, readOnly ? "r" : "rw"), readOnly);
    }

    /**
     * Creates a buffer using the provided <code>RandomAccessFile</code>.
     */
    public LargeMappedPersistentBuffer(RandomAccessFile raf, boolean readOnly) throws IOException {
        this.tempFile = null;
        this.raf = raf;
        channel = raf.getChannel();
        this.readOnly = readOnly;
        // Lock the file
        channel.lock(0L, Long.MAX_VALUE, readOnly);
        fillMappedBuffers();
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void finalize() {
        try {
            close();
        } catch(IOException err) {
            logger.log(Level.WARNING, null, err);
        }
    }

    public void close() throws IOException {
        closed = true;
        raf.close();
        if(tempFile!=null && tempFile.exists() && !tempFile.delete()) throw new IOException("Unable to delete temp file: "+tempFile);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public long capacity() throws IOException {
        return raf.length();
    }

    /**
     * Fills the buffers to cover the entire file length.
     */
    private void fillMappedBuffers() throws IOException {
        long len = raf.length();
        long maxBuffNum = len>>>BUFFER_NUM_BIT_SHIFT;
        if(maxBuffNum>=Integer.MAX_VALUE) throw new IOException("file too large for LargeMappedPersistentBuffer: "+len);
        int buffNumInt = (int)maxBuffNum;
        // Expand list
        while(mappedBuffers.size()<=buffNumInt) {
            long mapStart = ((long)mappedBuffers.size())<<BUFFER_NUM_BIT_SHIFT;
            long size = raf.length() - mapStart;
            if(size>BUFFER_SIZE) size = BUFFER_SIZE;
            mappedBuffers.add(channel.map(readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, mapStart, size));
        }
    }

    private int getBufferNum(long position) throws IOException {
        if(position<0) throw new IllegalArgumentException("position<0: "+position);
        long buffNum = position>>>BUFFER_NUM_BIT_SHIFT;
        if(buffNum>=Integer.MAX_VALUE) throw new IOException("position too large for LargeMappedPersistentBuffer: "+position);
        return (int)buffNum;
    }

    /**
     * Gets the position as an integer or throws IOException if too big for a mapped buffer.
     */
    private int getIndex(long position) throws IOException {
        return (int)(position&BUFFER_INDEX_MASK);
    }

    private void fillZeros(long position, long len) throws IOException {
        while(len>0) {
            long bufferStart = position & BUFFER_INDEX_MASK;
            long bufferSize = BUFFER_SIZE - bufferStart;
            if(bufferSize > len) bufferSize = len;
            MappedByteBuffer mappedBuffer = mappedBuffers.get(getBufferNum(position));
            mappedBuffer.position((int)bufferStart);
            Utils.fillZeros(mappedBuffer, bufferSize);
            position += bufferSize;
            len -= bufferSize;
        }
    }

    public void setCapacity(long newLength) throws IOException {
        long oldLength = capacity();
        if(oldLength!=newLength) {
            // Remove any buffers that could be affected
            long affectedFrom = getBufferNum(Math.min(oldLength, newLength));
            while(mappedBuffers.size()>affectedFrom) mappedBuffers.remove(mappedBuffers.size()-1);
            raf.setLength(newLength);
            fillMappedBuffers();
            if(newLength>oldLength) {
                // Ensure zero-filled
                fillZeros(oldLength, newLength-oldLength);
            }
        }
    }

    public void get(long position, byte[] buff, int off, int len) throws IOException {
        if(len>0) {
            int startBufferNum = getBufferNum(position);
            int endBufferNum = getBufferNum(position+len-1);
            if(startBufferNum==endBufferNum) {
                MappedByteBuffer mappedBuffer = mappedBuffers.get(startBufferNum);
                mappedBuffer.position(getIndex(position));
                mappedBuffer.get(buff, off, len);
            } else {
                do {
                    int bufferStart = (int)(position & BUFFER_INDEX_MASK);
                    int bufferSize = BUFFER_SIZE - bufferStart;
                    if(bufferSize > len) bufferSize = len;
                    MappedByteBuffer mappedBuffer = mappedBuffers.get(getBufferNum(position));
                    mappedBuffer.position(bufferStart);
                    mappedBuffer.get(buff, off, bufferSize);
                    position += bufferSize;
                    off += bufferSize;
                    len -= bufferSize;
                } while(len>0);
            }
        }
    }

    public int getSome(long position, byte[] buff, int off, int len) throws IOException {
        get(position, buff, off, len);
        return len;
    }

    public byte get(long position) throws IOException {
        return mappedBuffers.get(getBufferNum(position)).get(getIndex(position));
    }

    public void put(long position, byte value) throws IOException {
        mappedBuffers.get(getBufferNum(position)).put(getIndex(position), value);
    }

    public void put(long position, byte[] buff, int off, int len) throws IOException {
        if(len>0) {
            int startBufferNum = getBufferNum(position);
            int endBufferNum = getBufferNum(position+len-1);
            if(startBufferNum==endBufferNum) {
                MappedByteBuffer mappedBuffer = mappedBuffers.get(startBufferNum);
                mappedBuffer.position(getIndex(position));
                mappedBuffer.put(buff, off, len);
            } else {
                do {
                    int bufferStart = (int)(position & BUFFER_INDEX_MASK);
                    int bufferSize = BUFFER_SIZE - bufferStart;
                    if(bufferSize > len) bufferSize = len;
                    MappedByteBuffer mappedBuffer = mappedBuffers.get(getBufferNum(position));
                    mappedBuffer.position(bufferStart);
                    mappedBuffer.put(buff, off, bufferSize);
                    position += bufferSize;
                    off += bufferSize;
                    len -= bufferSize;
                } while(len>0);
            }
        }
    }

    public void force() throws IOException {
        for(MappedByteBuffer mappedBuffer : mappedBuffers) mappedBuffer.force();
        channel.force(true);
    }

    public boolean getBoolean(long position) throws IOException {
        return get(position)!=0;
    }

    public int getInt(long position) throws IOException {
        int startBufferNum = getBufferNum(position);
        int endBufferNum = getBufferNum(position+3);
        if(startBufferNum==endBufferNum) {
            return mappedBuffers.get(startBufferNum).getInt(getIndex(position));
        } else {
            return
                  ((get(position)&255) << 24)
                + ((get(position+1)&255) << 16)
                + ((get(position+2)&255) << 8)
                + (get(position+3)&255)
            ;
        }
    }

    public long getLong(long position) throws IOException {
        int startBufferNum = getBufferNum(position);
        int endBufferNum = getBufferNum(position+7);
        if(startBufferNum==endBufferNum) {
            return mappedBuffers.get(startBufferNum).getLong(getIndex(position));
        } else {
            return
                  ((get(position)&255L) << 56)
                + ((get(position+1)&255L) << 48)
                + ((get(position+2)&255L) << 40)
                + ((get(position+3)&255L) << 32)
                + ((get(position+4)&255L) << 24)
                + ((get(position+5)&255L) << 16)
                + ((get(position+6)&255L) << 8)
                + (get(position+7)&255L)
            ;
        }
    }

    public void putInt(long position, int value) throws IOException {
        int startBufferNum = getBufferNum(position);
        int endBufferNum = getBufferNum(position+3);
        if(startBufferNum==endBufferNum) {
            mappedBuffers.get(startBufferNum).putInt(getIndex(position), value);
        } else {
            put(position, (byte)(value >>> 24));
            put(position+1, (byte)(value >>> 16));
            put(position+2, (byte)(value >>> 8));
            put(position+3, (byte)value);
        }
    }

    public void putLong(long position, long value) throws IOException {
        int startBufferNum = getBufferNum(position);
        int endBufferNum = getBufferNum(position+7);
        if(startBufferNum==endBufferNum) {
            mappedBuffers.get(startBufferNum).putLong(getIndex(position), value);
        } else {
            put(position, (byte)(value >>> 56));
            put(position+1, (byte)(value >>> 48));
            put(position+2, (byte)(value >>> 40));
            put(position+3, (byte)(value >>> 32));
            put(position+4, (byte)(value >>> 24));
            put(position+5, (byte)(value >>> 16));
            put(position+6, (byte)(value >>> 8));
            put(position+7, (byte)value);
        }
    }
}
