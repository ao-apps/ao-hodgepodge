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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses <code>MappedByteBuffer</code> for persistence.  It maps the entire file
 * at once into a single buffer.  For this reason, it is limited to a maximum
 * buffer size of 2^31-1.  To use memory mapping for larger buffers, at a slight
 * performance cost, use <code>LargeMappedPersistentBuffer</code>.
 *
 * @see  LargeMappedPersistentBuffer
 *
 * @author  AO Industries, Inc.
 */
public class MappedPersistentBuffer implements PersistentBuffer {

    private static final Logger logger = Logger.getLogger(MappedPersistentBuffer.class.getName());

    private final File tempFile;
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private MappedByteBuffer mappedBuffer;
    private boolean modified;
    private final boolean readOnly;
    private boolean closed;

    /**
     * Creates a read-write buffer backed by a temporary file.  The temporary
     * file will be deleted when this buffer is closed or on JVM shutdown.
     */
    public MappedPersistentBuffer() throws IOException {
        tempFile = File.createTempFile("RandomAccessFileBuffer", null);
        tempFile.deleteOnExit();
        raf = new RandomAccessFile(tempFile, "rw");
        channel = raf.getChannel();
        mappedBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 0);
        readOnly = false;
        // Lock the file
        channel.lock(0L, Long.MAX_VALUE, false);
    }

    /**
     * Creates a read-write buffer.
     */
    public MappedPersistentBuffer(String name) throws IOException {
        this(new RandomAccessFile(name, "rw"), false);
    }

    /**
     * Creates a buffer.
     */
    public MappedPersistentBuffer(String name, boolean readOnly) throws IOException {
        this(new RandomAccessFile(name, readOnly ? "r" : "rw"), readOnly);
    }

    /**
     * Creates a read-write buffer.
     */
    public MappedPersistentBuffer(File file) throws IOException {
        this(new RandomAccessFile(file, "rw"), false);
    }

    /**
     * Creates a buffer.
     */
    public MappedPersistentBuffer(File file, boolean readOnly) throws IOException {
        this(new RandomAccessFile(file, readOnly ? "r" : "rw"), readOnly);
    }

    /**
     * Creates a buffer using the provided <code>RandomAccessFile</code>.
     */
    public MappedPersistentBuffer(RandomAccessFile raf, boolean readOnly) throws IOException {
        this.tempFile = null;
        this.raf = raf;
        channel = raf.getChannel();
        mappedBuffer = channel.map(readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, 0, raf.length());
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

    /**
     * Gets the position as an integer or throws IOException if too big for a mapped buffer.
     */
    private int getIndex(long position) throws IOException {
        if(position<0) throw new IllegalArgumentException("position<0: "+position);
        if(position>Integer.MAX_VALUE) throw new IOException("position too large for MappedPersistentBuffer: "+position);
        return (int)position;
    }

    public void setCapacity(long newLength) throws IOException {
        long oldLength = capacity();
        if(oldLength!=newLength) {
            if(newLength<oldLength) {
                if(modified) {
                    mappedBuffer.force();
                    modified = false;
                }
                mappedBuffer = channel.map(readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, 0, newLength);
            }
            raf.setLength(newLength);
            if(newLength>oldLength) {
                if(modified) {
                    mappedBuffer.force();
                    modified = false;
                }
                mappedBuffer = channel.map(readOnly ? FileChannel.MapMode.READ_ONLY : FileChannel.MapMode.READ_WRITE, 0, newLength);
                // Ensure zero-filled
                mappedBuffer.position(getIndex(oldLength));
                PersistentCollections.fillZeros(mappedBuffer, newLength - oldLength);
                modified = true;
            }
        }
    }

    public void get(long position, byte[] buff, int off, int len) throws IOException {
        mappedBuffer.position(getIndex(position));
        mappedBuffer.get(buff, off, len);
    }

    public int getSome(long position, byte[] buff, int off, int len) throws IOException {
        mappedBuffer.position(getIndex(position));
        mappedBuffer.get(buff, off, len);
        return len;
    }

    public byte get(long position) throws IOException {
        return mappedBuffer.get(getIndex(position));
    }

    public void put(long position, byte value) throws IOException {
        mappedBuffer.put(getIndex(position), value);
        modified = true;
    }

    public void put(long position, byte[] buff, int off, int len) throws IOException {
        mappedBuffer.position(getIndex(position));
        mappedBuffer.put(buff, off, len);
        modified = true;
    }

    /**
     * There is not currently a way to provide a barrier without using <code>force</code>.
     * This just uses force for each case.
     */
    public void barrier(boolean force) throws IOException {
        if(modified) {
            mappedBuffer.force();
            modified = false;
        }
        // channel.force(true);
    }

    public boolean getBoolean(long position) throws IOException {
        return mappedBuffer.get(getIndex(position))!=0;
    }

    public int getInt(long position) throws IOException {
        return mappedBuffer.getInt(getIndex(position));
    }

    public long getLong(long position) throws IOException {
        return mappedBuffer.getLong(getIndex(position));
    }

    public void putInt(long position, int value) throws IOException {
        mappedBuffer.putInt(getIndex(position), value);
        modified = true;
    }

    public void putLong(long position, long value) throws IOException {
        mappedBuffer.putLong(getIndex(position), value);
        modified = true;
    }
}
