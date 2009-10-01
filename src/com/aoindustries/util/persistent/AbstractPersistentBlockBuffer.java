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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

/**
 * Base class for any implementation that treats a <code>PersistentBuffer</code>
 * as a set of allocatable blocks.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractPersistentBlockBuffer implements PersistentBlockBuffer {

    protected final PersistentBuffer pbuffer;

    public AbstractPersistentBlockBuffer(PersistentBuffer pbuffer) {
        this.pbuffer = pbuffer;
    }

    public boolean isClosed() {
        return pbuffer.isClosed();
    }

    public void close() throws IOException {
        pbuffer.close();
    }

    public ProtectionLevel getProtectionLevel() {
        return pbuffer.getProtectionLevel();
    }

    public void barrier(boolean force) throws IOException {
        pbuffer.barrier(force);
    }

    public void get(long id, long offset, byte[] buff, int off, int len) throws IOException, BufferUnderflowException {
        if((offset+len)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+len);
        pbuffer.get(startAddress, buff, off, len);
    }

    public int getInt(long id, long offset) throws IOException, BufferUnderflowException {
        if((offset+4)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+4);
        return pbuffer.getInt(startAddress);
    }

    public long getLong(long id, long offset) throws IOException, BufferUnderflowException {
        if((offset+8)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+8);
        return pbuffer.getLong(startAddress);
    }

    public InputStream getInputStream(long id, long offset, long length) throws IOException, BufferUnderflowException {
        if((offset+length)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+length);
        return pbuffer.getInputStream(startAddress, length);
    }

    public void put(long id, long offset, byte[] buff, int off, int len) throws IOException, BufferOverflowException {
        if((offset+len)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+len);
        pbuffer.put(startAddress, buff, off, len);
    }

    public void putInt(long id, long offset, int value) throws IOException, BufferOverflowException {
        if((offset+4)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+4);
        pbuffer.putInt(startAddress, value);
    }

    public void putLong(long id, long offset, long value) throws IOException, BufferOverflowException {
        if((offset+8)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+8);
        pbuffer.putLong(startAddress, value);
    }

    public OutputStream getOutputStream(long id, long offset, long length) throws IOException, BufferOverflowException {
        if((offset+length)>getBlockSize(id)) throw new BufferOverflowException();
        long startAddress = getBlockAddress(id)+offset;
        ensureCapacity(startAddress+length);
        return pbuffer.getOutputStream(startAddress, length);
    }

    /**
     * Gets the address of the block in the underlying persistent buffer.
     */
    abstract protected long getBlockAddress(long id);

    /**
     * Ensures the underlying persistent buffer is of adequate capacity.  Grows the
     * underlying storage if needed.
     */
    abstract protected void ensureCapacity(long capacity) throws IOException;
}
