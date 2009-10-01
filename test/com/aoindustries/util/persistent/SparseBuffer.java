/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009  AO Industries, Inc.
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
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ReadOnlyBufferException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This buffer allows very large address spaces for testing purposes.  It is backed by
 * a map of 4096-byte buffers in heap.  Each buffer will only be created when first written to.
 * The buffer will not be removed, even if it becomes all zeros again.
 *
 * @author  AO Industries, Inc.
 */
public class SparseBuffer extends AbstractPersistentBuffer {

    private boolean isClosed = false;
    private long capacity = 0L;
    private Map<Long,byte[]> buffers = new HashMap<Long,byte[]>();

    /**
     * Creates a read-write test buffer with protection level <code>NONE</code>.
     */
    public SparseBuffer() {
        this(ProtectionLevel.NONE);
    }

    public SparseBuffer(ProtectionLevel protectionLevel) {
        super(protectionLevel);
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void close() throws IOException {
        isClosed = true;
    }

    public long capacity() throws IOException {
        return capacity;
    }

    public void setCapacity(long newCapacity) throws IOException {
        if(newCapacity<0) throw new IllegalArgumentException("capacity<0: "+capacity);
        if(protectionLevel==ProtectionLevel.READ_ONLY) throw new ReadOnlyBufferException();
        if(newCapacity>capacity) {
            // TODO: Zero the partial part of the last page when growing
        }
        this.capacity = newCapacity;
        // Discard any pages above new capacity
        long highestPage = newCapacity>>>12;
        if((newCapacity&0x7ff)!=0) highestPage++;
        Iterator<Long> keyIter = buffers.keySet().iterator();
        while(keyIter.hasNext()) {
            long key = keyIter.next();
            if(key>highestPage) keyIter.remove();
        }
    }

    public int getSome(long position, byte[] buff, int off, int len) throws IOException {
        get(position, buff, off, len);
        return len;
    }

    @Override
    public void get(long position, byte[] buff, int off, int len) throws IOException {
        if((position+len)>capacity) throw new BufferUnderflowException();
        // TODO: More efficient algorithm using blocks calling System.arraycopy.
        long lastBufferNum = -1;
        byte[] lastBuffer = null;
        while(len>0) {
            long blockNum = position >>> 12;
            if(blockNum!=lastBufferNum) lastBuffer = buffers.get(lastBufferNum = blockNum);
            buff[off] = lastBuffer==null ? 0 : lastBuffer[(int)(position & 0xfffL)];
            position++;
            off++;
            len--;
        }
    }

    public void put(long position, byte[] buff, int off, int len) throws IOException {
        if(protectionLevel==ProtectionLevel.READ_ONLY) throw new ReadOnlyBufferException();
        if((position+len)>capacity) throw new BufferOverflowException();
        // TODO: More efficient algorithm using blocks calling System.arraycopy.
        long lastBufferNum = -1;
        byte[] lastBuffer = null;
        while(len>0) {
            long blockNum = position >>> 12;
            if(blockNum!=lastBufferNum) lastBuffer = buffers.get(lastBufferNum = blockNum);
            byte value = buff[off];
            // Only create the buffer when a non-zero value is being added
            if(lastBuffer==null && value!=0) buffers.put(lastBufferNum, lastBuffer = new byte[4096]);
            if(lastBuffer!=null) lastBuffer[(int)(position & 0xfffL)] = value;
            position++;
            off++;
            len--;
        }
    }

    public void barrier(boolean force) throws IOException {
    }
}
