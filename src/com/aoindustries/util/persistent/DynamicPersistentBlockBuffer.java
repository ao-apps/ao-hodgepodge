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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Treats a <code>PersistentBuffer</code> as a set of allocatable blocks.
 * Each block is stored in a 2^n area of the buffer, where the usable
 * space is 2^n-1 (the first byte of that area of the buffer indicates
 * the block size and allocated status).
 * </p>
 * <p>
 * Free space maps are generated upon instantiation.  This means that startup
 * costs can be fairly high.  This class is designed for long-lifetime situations.
 * </p>
 * <p>
 * Blocks that are allocated take no space in memory, while blocks that are allocated consume space.
 * Blocks may be merged and split as needed to manage free space.
 * </p>
 * <p>
 * Fragmentation may occur in the file over time, but is minimized by the use of
 * per-block size free space maps.  There is currently no compaction tool or
 * conversion between block sizes.
 * </p>
 * <p>
 * Each entry has a one-byte header:
 *     bits 0-5: maxBits     (0-63) the power of two of the size of this block (max data size is <code>(2^maxBits)-1</code>).
 *     bit  6:   reserved, should be 0
 *     bit  7:   allocated flag
 * </p>
 * <p>
 * This class is not thread-safe.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class DynamicPersistentBlockBuffer extends AbstractPersistentBlockBuffer {

    /**
     * Tracks free space on a per power-of-two basis.
     */
    private final List<Set<Long>> freeSpaceMaps = new ArrayList<Set<Long>>(64);

    public DynamicPersistentBlockBuffer(PersistentBuffer pbuffer) {
        super(pbuffer);
        for(int c=0;c<64;c++) freeSpaceMaps.add(null);
        // Read the head and tail to maintain in cache
        /*
        long len = oldpbuffer.capacity();
        if(len==0) clear();
        clear();
        long ptr = HEADER_SIZE;
        for(; ptr<len; ptr+=getEntrySize(getMaxBits(ptr))) {
            if(isAllocated(ptr)) count++;
            else addFreeSpaceMap(ptr);
        }
        if(ptr!=len) throw new IOException("ptr!=len: "+ptr+"!="+len);
         */
    }

    /*
    private boolean isAllocated(long ptr) throws IOException {
        assert isValidRange(ptr) : "Invalid range: "+ptr;
        return pbuffer.getLong(ptr+NEXT_OFFSET)!=-1 && pbuffer.getLong(ptr+PREV_OFFSET)!=-1;
    }*/

    public Iterator<Long> iterateBlockIds() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long allocate(long minimumSize) throws IOException {
        //assert (ptr+DATA_OFFSET+dataSize)<=pbuffer.capacity(); // Must not extend past end of file
        // assert dataSize>=0 && ((long)dataSize)<=(1L << getMaxBits(ptr)); // Must not exceed maximum size
        // Only Required for previous assertion: raf.seek(ptr+DATA_OFFSET);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deallocate(long id) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Checks if the provided block is allocated.  This is for debugging only
     * to be used in assertions.  It is not a reliable mechanism to use because
     * block combining and splitting can cause this method to check arbitrary
     * garbage.
     */
    private boolean isAllocated(long id) throws IOException {
        return (pbuffer.get(id) & 128)!=0;
    }

    /**
     * Gets the maximum amount of data that may be stored in the entry.  This
     * is the underlying power-of-two block size minus one.  May only check
     * the block size of allocated blocks.
     */
    public long getBlockSize(long id) throws IOException {
        assert isAllocated(id);
        int maxBits = pbuffer.get(id) & 63;
        return (1<<maxBits) - 1;
    }

    /*public void clear() {
        try {
            pbuffer.setCapacity(HEADER_SIZE);
            for(Set<Long> fsm : freeSpaceMaps) {
                if(fsm!=null) fsm.clear();
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }*/

    /**
     * Gets the overall size of an entry given its maxBits.
     * This includes the pointers and flags.
     */
    /*private static long getEntrySize(int maxBits) {
        assert maxBits>=0 && maxBits<=31;
        return DATA_OFFSET + (1L << maxBits);
    }*/

    /**
     * Deallocates the entry at the provided location.
     * The entry must have non-null next and prev.
     */
    /*private void deallocate(long ptr) throws IOException {
        deallocate(ptr, getMaxBits(ptr));
    }*/

    /**
     * Deallocates the entry at the provided location.
     * The entry must have non-null next and prev.
     */
    /*private void deallocate(long ptr, int maxBits) throws IOException {
        addFreeSpaceMap(ptr, maxBits);
    }*/

    /**
     * Adds the block at the provided location to the free space maps.
     * The entry must have non-null next and prev.
     */
    /*private void addFreeSpaceMap(long ptr) throws IOException {
        addFreeSpaceMap(ptr, getMaxBits(ptr));
    }*/

    /**
     * Adds the block at the provided location to the free space maps.
     * The entry must have non-null next and prev.
     */
    /*private void addFreeSpaceMap(long ptr, int maxBits) throws IOException {
        assert !hasNonNullNextPrev(ptr);
        Set<Long> fsm = freeSpaceMaps.get(maxBits);
        if(fsm==null) freeSpaceMaps.set(maxBits, fsm = new TreeSet<Long>());
        if(!fsm.add(ptr)) throw new AssertionError("Free space map already contains entry: "+ptr);
    }*/

    /**
     * Allocates the first free space available that can hold the requested amount of
     * data.  The amount of data should not include the pointer space.
     *
     * Operates in logarithic complexity on the amount of free entries.
     */
    /*private long allocateEntry(long next, long prev, long dataSize, byte[] data) throws IOException {
        assert next==TAIL_PTR || hasNonNullNextPrev(next);
        assert prev==HEAD_PTR || hasNonNullNextPrev(prev);
        assert (dataSize==-1 && data==null) || (dataSize>=0 && data!=null);
        if(_size==Long.MAX_VALUE) throw new IOException("List is full: _size==Long.MAX_VALUE");
        // Determine the maxBits value
        int maxBits = 0;
        while((1L<<maxBits)<dataSize) maxBits++;
        // Look in the free space maps
        Set<Long> fsm = freeSpaceMaps.get(maxBits);
        if(fsm!=null && !fsm.isEmpty()) {
            Iterator<Long> iter = fsm.iterator();
            long ptr = iter.next();
            iter.remove();
            // Update existing entry
            PersistentCollections.longToBuffer(next, ioBuffer, NEXT_OFFSET-NEXT_OFFSET);
            PersistentCollections.longToBuffer(prev, ioBuffer, PREV_OFFSET-NEXT_OFFSET);
            PersistentCollections.longToBuffer(dataSize, ioBuffer, DATA_SIZE_OFFSET-NEXT_OFFSET);
            pbuffer.put(ptr+NEXT_OFFSET, ioBuffer, 0, 20);
            if(dataSize>0) pbuffer.put(ptr+DATA_OFFSET, data, 0, dataSize);
            _size++;
            return ptr;
        }
        // Allocate more space at the end of the file
        long ptr = pbuffer.capacity();
        long newLen = ptr + DATA_OFFSET + (1L<<(long)maxBits);
        pbuffer.setCapacity(newLen);
        ioBuffer[MAX_BITS_OFFSET] = (byte)maxBits;
        PersistentCollections.longToBuffer(next, ioBuffer, NEXT_OFFSET);
        PersistentCollections.longToBuffer(prev, ioBuffer, PREV_OFFSET);
        PersistentCollections.longToBuffer(dataSize, ioBuffer, DATA_SIZE_OFFSET);
        pbuffer.put(ptr, ioBuffer, 0, DATA_OFFSET);
        if(dataSize>0) pbuffer.put(ptr+DATA_OFFSET, data, 0, dataSize);
        _size++;
        return ptr;
    }*/

    protected long getBlockAddress(long id) {
        return id;
    }

    @Override
    protected void ensureCapacity(long capacity) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
