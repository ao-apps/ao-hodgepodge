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
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses <code>RandomAccessFile</code> for persistence.  Obtains a shared lock
 * on the channel for read-only mode or an exclusive lock for write mode.  The
 * lock is held until the buffer is closed.
 *
 * @author  AO Industries, Inc.
 */
public class RandomAccessFileBuffer extends AbstractPersistentBuffer {

    private static final Logger logger = Logger.getLogger(RandomAccessFileBuffer.class.getName());

    private final File tempFile;
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private final boolean readOnly;
    private boolean closed;

    /**
     * Creates a read-write buffer backed by a temporary file.  The temporary
     * file will be deleted when this buffer is closed or on JVM shutdown.
     */
    public RandomAccessFileBuffer() throws IOException {
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
    public RandomAccessFileBuffer(String name) throws IOException {
        this(new RandomAccessFile(name, "rw"), false);
    }

    /**
     * Creates a buffer.
     */
    public RandomAccessFileBuffer(String name, boolean readOnly) throws IOException {
        this(new RandomAccessFile(name, readOnly ? "r" : "rw"), readOnly);
    }

    /**
     * Creates a read-write buffer.
     */
    public RandomAccessFileBuffer(File file) throws IOException {
        this(new RandomAccessFile(file, "rw"), false);
    }

    /**
     * Creates a buffer.
     */
    public RandomAccessFileBuffer(File file, boolean readOnly) throws IOException {
        this(new RandomAccessFile(file, readOnly ? "r" : "rw"), readOnly);
    }

    /**
     * Creates a buffer using the provided <code>RandomAccessFile</code>.
     */
    public RandomAccessFileBuffer(RandomAccessFile raf, boolean readOnly) throws IOException {
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

    public void setCapacity(long newLength) throws IOException {
        long oldLength = capacity();
        raf.setLength(newLength);
        if(newLength>oldLength) {
            // Ensure zero-filled
            raf.seek(oldLength);
            Utils.fillZeros(raf, newLength - oldLength);
        }
    }

    public int getSome(long position, byte[] buff, int off, int len) throws IOException {
        raf.seek(position);
        int count = raf.read(buff, off, len);
        if(count<0) throw new BufferUnderflowException();
        return count;
    }

    @Override
    public byte get(long position) throws IOException {
        raf.seek(position);
        return raf.readByte();
    }

    @Override
    public void put(long position, byte value) throws IOException {
        if(position>=capacity()) throw new BufferOverflowException();
        raf.seek(position);
        raf.write(value);
    }

    public void put(long position, byte[] buff, int off, int len) throws IOException {
        if((position+len)>capacity()) throw new BufferOverflowException();
        raf.seek(position);
        raf.write(buff, off, len);
    }

    public void force() throws IOException {
        channel.force(true);
    }
}
