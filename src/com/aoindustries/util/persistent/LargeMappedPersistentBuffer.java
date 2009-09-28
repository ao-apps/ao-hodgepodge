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
 * 2^30 * 2^31-1 bytes.
 *
 * @see  MappedPersistentBuffer
 *
 * @author  AO Industries, Inc.
 */
public class LargeMappedPersistentBuffer implements PersistentBuffer {

    private static final Logger logger = Logger.getLogger(LargeMappedPersistentBuffer.class.getName());

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

    private MappedByteBuffer getMappedBuffer(long position) throws IOException {
        if(position<0) throw new IllegalArgumentException("position<0: "+position);
        long buffNum = position>>>30;
        if(buffNum>Integer.MAX_VALUE) throw new IOException("position too large for LargeMappedPersistentBuffer: "+position);
        int buffNumInt = (int)buffNum;
        // Expand list
        while(mappedBuffers.size()<=buffNumInt) mappedBuffers.add(null);
        // Create map if missing
        MappedByteBuffer mappedBuffer = mappedBuffers.get(buffNumInt);
        if(mappedBuffer==null) {
            long mapStart = buffNum<<30;
            long size = raf.length() - mapStart;
            if(size>0x40000000) size = 0x40000000;
            mappedBuffers.set(buffNumInt, mappedBuffer = channel.map(readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, mapStart, size));
        }
        return mappedBuffer;
    }

    /**
     * Gets the position as an integer or throws IOException if too big for a mapped buffer.
     */
    private int getIndex(long position) throws IOException {
        return (int)(position&0x3fffffff);
    }

    public void setCapacity(long newLength) throws IOException {
        long oldLength = capacity();
        if(oldLength!=newLength) {
            // Remove any buffers that could be affected
            long affectedFrom = Math.min(oldLength, newLength) >>> 30;
            while(mappedBuffers.size()>affectedFrom) mappedBuffers.remove(mappedBuffers.size()-1);
            raf.setLength(newLength);
            if(newLength>oldLength) {
                // Ensure zero-filled
                raf.seek(oldLength);
                Utils.fillZeros(raf, newLength - oldLength);
            }
        }
    }

    public void get(long position, byte[] buff, int off, int len) throws IOException {
        MappedByteBuffer mappedBuffer = getMappedBuffer(position);
        mappedBuffer.position(getIndex(position));
        mappedBuffer.get(buff, off, len);
    }

    public int getSome(long position, byte[] buff, int off, int len) throws IOException {
        MappedByteBuffer mappedBuffer = getMappedBuffer(position);
        mappedBuffer.position(getIndex(position));
        mappedBuffer.get(buff, off, len);
        return len;
    }

    public byte get(long position) throws IOException {
        return getMappedBuffer(position).get(getIndex(position));
    }

    public void put(long position, byte[] buff, int off, int len) throws IOException {
        MappedByteBuffer mappedBuffer = getMappedBuffer(position);
        mappedBuffer.position(getIndex(position));
        mappedBuffer.put(buff, off, len);
    }

    public void force() throws IOException {
        for(MappedByteBuffer mappedBuffer : mappedBuffers) {
            if(mappedBuffer!=null) mappedBuffer.force();
        }
        channel.force(true);
    }

    public boolean getBoolean(long position) throws IOException {
        return getMappedBuffer(position).get(getIndex(position))!=0;
    }

    public int getInt(long position) throws IOException {
        return getMappedBuffer(position).getInt(getIndex(position));
    }

    public long getLong(long position) throws IOException {
        return getMappedBuffer(position).getLong(getIndex(position));
    }

    public void putInt(long position, int value) throws IOException {
        getMappedBuffer(position).putInt(getIndex(position), value);
    }

    public void putLong(long position, long value) throws IOException {
        getMappedBuffer(position).putLong(getIndex(position), value);
    }
}
