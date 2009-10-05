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

import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

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
 * Blocks that are allocated take no space in memory, while blocks that are deallocated
 * consume space.  Adjacent blocks are automatically merged into a larger free block of
 * twice the size.  Blocks are also split into smaller blocks before allocating additional
 * space.
 * </p>
 * <p>
 * Fragmentation may occur in the file over time, but is minimized by the use of
 * per-block size free space maps along with block merging and splitting.  There
 * is currently no compaction tool.
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

    private static boolean isAllocated(byte header) {
        return (header&0x80)!=0;
    }

    private static long getBlockSize(byte header) {
        return 1L<<(header&0x3f);
    }

    /**
     * Tracks free space on a per power-of-two basis.
     */
    private final List<SortedSet<Long>> freeSpaceMaps = new ArrayList<SortedSet<Long>>(64);

    public DynamicPersistentBlockBuffer(PersistentBuffer pbuffer) throws IOException {
        super(pbuffer);
        for(int c=0;c<64;c++) freeSpaceMaps.add(null);
        // Read the head and tail to maintain in cache
        long capacity = pbuffer.capacity();
        long ptr = 0;
        while(ptr<capacity) {
            byte header = pbuffer.get(ptr);
            long blockSize = getBlockSize(header);
            // Auto-expand if underlying buffer doesn't end on a block boundary
            long blockEnd = ptr + blockSize;
            if(blockEnd>capacity) capacity = expandCapacity(blockEnd);
            if(!isAllocated(header)) addFreeSpaceMap(ptr, header&0x3f, capacity);
            ptr = blockEnd;
        }
        assert ptr==capacity : "ptr!=len: "+ptr+"!="+capacity;
    }

    /*
    private boolean isAllocated(long ptr) throws IOException {
        assert isValidRange(ptr) : "Invalid range: "+ptr;
        return pbuffer.getLong(ptr+NEXT_OFFSET)!=-1 && pbuffer.getLong(ptr+PREV_OFFSET)!=-1;
    }*/

    public Iterator<Long> iterateBlockIds() throws IOException {
        return new Iterator<Long>() {
            // TODO: Add modCount
            long nextPtr = 0;
            public boolean hasNext() {
                try {
                    long capacity = pbuffer.capacity();
                    while(nextPtr<capacity) {
                        byte header = pbuffer.get(nextPtr);
                        if(isAllocated(header)) return true;
                        nextPtr += getBlockSize(header);
                    }
                    return false;
                } catch(IOException err) {
                    throw new WrappedException(err);
                }
            }
            public Long next() {
                try {
                    long capacity = pbuffer.capacity();
                    while(nextPtr<capacity) {
                        byte header = pbuffer.get(nextPtr);
                        long ptr = nextPtr;
                        nextPtr += getBlockSize(header);
                        if(isAllocated(header)) return ptr;
                    }
                    throw new NoSuchElementException();
                } catch(IOException err) {
                    throw new WrappedException(err);
                }
            }
            public void remove() {
                throw new UnsupportedOperationException("TODO: Not supported yet.");
            }
        };
    }

    private long splitAllocate(int maxBits, long capacity) throws IOException {
        SortedSet<Long> fsm = freeSpaceMaps.get(maxBits);
        if(fsm!=null && !fsm.isEmpty()) {
            // No split needed
            Long ptr = fsm.first();
            fsm.remove(ptr);
            return ptr;
        } else {
            // End recursion
            if(maxBits==63) return -1;
            long blockSize = 1L << maxBits;
            if(blockSize>capacity) return -1;
            // Try split
            long biggerPtr = splitAllocate(maxBits+1, capacity);
            // Unsplittable
            if(biggerPtr==-1) return -1;
            // Split the bigger one
            pbuffer.put(biggerPtr, (byte)maxBits);
            long nextPtr = biggerPtr+blockSize;
            pbuffer.put(nextPtr, (byte)maxBits);
            if(fsm==null) freeSpaceMaps.set(maxBits, fsm = new TreeSet<Long>());
            fsm.add(nextPtr);
            return biggerPtr;
        }
    }

    public long allocate(long minimumSize) throws IOException {
        // Determine the min block size for the provided input
        int maxBits = 64 - Long.numberOfLeadingZeros(minimumSize);
        long capacity = pbuffer.capacity();
        long ptr = splitAllocate(maxBits, capacity);
        if(ptr==-1) {
            long blockSize = 1L << maxBits;
            long blockMask = blockSize - 1;
            // Allocate space at end, aligned with block size
            long blockStart = capacity;
            long blockOffset = blockStart & blockMask;
            if(blockOffset!=0) {
                // TODO: Faster way, combine into a single setCapacity call with that below
                long expandBytes = blockSize - blockOffset;
                long newCapacity = capacity+expandBytes;
                pbuffer.setCapacity(newCapacity);
                for(long pos=capacity; pos<newCapacity; pos++) {
                    addFreeSpaceMap(pos, 0, newCapacity);
                }
                capacity = newCapacity;
            }
            if(blockSize<4096) {
                pbuffer.setCapacity(blockStart + 4096);
                pbuffer.put(blockStart, (byte)(0x80 | maxBits));
                // TODO: Faster way
                for(long pos=blockStart+blockSize, end=blockStart+4096; pos<end; pos++) {
                    addFreeSpaceMap(pos, 0, end);
                }
            } else {
                pbuffer.setCapacity(blockStart+blockSize);
                pbuffer.put(blockStart, (byte)(0x80 | maxBits));
            }
            ptr = blockStart;
        }
        return ptr;
    }

    public void deallocate(long id) throws IOException, IllegalStateException {
        byte header = pbuffer.get(id);
        if((header&0x80)==0) throw new AssertionError("Block not allocated");
        pbuffer.put(id, (byte)(header&0x7f));
        addFreeSpaceMap(id, header&0x3f, pbuffer.capacity());
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

    /**
     * Adds the block at the provided location to the free space maps.
     */
    private void addFreeSpaceMap(long ptr, int maxBits, long capacity) throws IOException {
        assert !isAllocated(ptr);
        // Group as much as possible within the same power-of-two block
        boolean writeMaxBits = false;
        for(int bits = maxBits; bits<63; bits++) {
            long blockSize = 1L<<bits;
            long biggerBlockSize = blockSize<<1;
            long biggerBlockMask = biggerBlockSize-1;
            long prevPtr = ptr - blockSize;
            long ptrBiggerBlockMask = ptr&biggerBlockMask;
            if((prevPtr&biggerBlockMask)==ptrBiggerBlockMask) {
                // In the same bigger block as the block to the left
                if(isAllocated(prevPtr)) {
                    // Block to the left is allocated, stop grouping
                    break;
                } else {
                    ptr = prevPtr;
                    maxBits = bits+1;
                    writeMaxBits = true;
                    // Remove prev from FSM
                    SortedSet<Long> fsm = freeSpaceMaps.get(bits);
                    if(fsm!=null) fsm.remove(prevPtr);
                }
            } else {
                // Only group right if the pbuffer has room for the bigger parent
                if((ptr+biggerBlockSize)>capacity) {
                    // Stop grouping
                    break;
                } else {
                    long nextPtr = ptr + blockSize;
                    if((nextPtr&biggerBlockMask)==ptrBiggerBlockMask) {
                        // In the same bigger block as the block to the right
                        if(isAllocated(nextPtr)) {
                            // Block to the right is allocated, stop grouping
                            break;
                        } else {
                            maxBits = bits+1;
                            writeMaxBits = true;
                            // Remove next from FSM
                            SortedSet<Long> fsm = freeSpaceMaps.get(bits);
                            if(fsm!=null) fsm.remove(nextPtr);
                        }
                    }
                }
            }
        }
        if(writeMaxBits) pbuffer.put(ptr, (byte)maxBits);
        SortedSet<Long> fsm = freeSpaceMaps.get(maxBits);
        if(fsm==null) freeSpaceMaps.set(maxBits, fsm = new TreeSet<Long>());
        if(!fsm.add(ptr)) throw new AssertionError("Free space map already contains entry: "+ptr);
    }

    protected long getBlockAddress(long id) {
        return id;
    }

    protected long expandCapacity(long newCapacity) throws IOException {
        if((newCapacity&0xfff)!=0) newCapacity = (newCapacity & 0xfffffffffffff000L)+4096L;
        pbuffer.setCapacity(newCapacity);
        return newCapacity;
    }

    @Override
    protected long ensureCapacity(long capacity) throws IOException {
        long curCapacity = pbuffer.capacity();
        if(curCapacity<capacity) pbuffer.setCapacity(capacity);
        return curCapacity;
    }
}
