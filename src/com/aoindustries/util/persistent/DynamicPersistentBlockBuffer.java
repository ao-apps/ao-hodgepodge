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
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(DynamicPersistentBlockBuffer.class.getName());

    /**
     * Tracks free space on a per power-of-two basis.
     */
    private final List<SortedSet<Long>> freeSpaceMaps = new ArrayList<SortedSet<Long>>(64);

    public DynamicPersistentBlockBuffer(PersistentBuffer pbuffer) throws IOException {
        super(pbuffer);
        for(int c=0;c<64;c++) freeSpaceMaps.add(null);
        // Build the free space maps and expand to end on an even block size and page size
        long capacity = pbuffer.capacity();
        long id = 0;
        while(id<capacity) {
            byte header = pbuffer.get(id);
            int blockSizeBits = getBlockSizeBits(header);
            if(!isBlockAligned(id, blockSizeBits)) throw new IOException("Block not aligned: "+id);
            // Auto-expand if underlying buffer doesn't end on a block boundary, this may be the result of a partial increase in size
            long blockEnd = id + getBlockSize(blockSizeBits);
            if(blockEnd>capacity) {
                logger.warning("Expanding capacity to match block end");
                // Round up to the nearest PAGE_SIZE
                pbuffer.setCapacity(capacity = getNearestPage(blockEnd));
                // Any extra space is zero-filled, which means unallocated and size 1 (2^0),
                // which will be grouped by the addFreeSpaceMap algorithm.  Not worried
                // about the performance of this recovery process
            }
            if(!isAllocated(header)) addFreeSpaceMap(id, blockSizeBits, capacity);
            id = blockEnd;
        }
        assert id==capacity : "id!=capacity: "+id+"!="+capacity;
    }

    // <editor-fold desc="Bit Manipulation">
    /**
     * Space will always be allocated to align with this page size.
     */
    private static final long PAGE_SIZE = 0x1000L; // Must be a power of two.
    private static final long PAGE_OFFSET_MASK = PAGE_SIZE-1;
    private static final long PAGE_MASK = -PAGE_SIZE;

    private static boolean isAllocated(byte header) {
        return (header&0x80)!=0;
    }

    private static int getBlockSizeBits(byte header) {
        return header&0x3f;
    }

    /**
     * Gets the block size given the block size bits.
     */
    private static long getBlockSize(int blockSizeBits) {
        return 1L<<blockSizeBits;
    }

    /**
     * Gets the offset of an id within a page.
     */
    private static long getPageOffset(long id) {
        return id&PAGE_OFFSET_MASK;
    }

    /**
     * Gets the nearest page boundary, rounding up if necessary.
     */
    private static long getNearestPage(long id) {
        if(getPageOffset(id)!=0) id = (id & PAGE_MASK)+PAGE_SIZE;
        return id;
    }
    // </editor-fold>

    // <editor-fold desc="Assertions and Data Consistency">
    /**
     * Checks that the is a valid blockSizeBits.
     */
    private static boolean isValidBlockSizeBits(int blockSizeBits) {
        return blockSizeBits>=0 && blockSizeBits<=0x3f;
    }

    /**
     * Makes sure the id is in the valid range: <code>0 &lt;= id &lt; capacity</code>
     */
    private boolean isValidRange(long id) throws IOException {
        return id>=0 && id<pbuffer.capacity();
    }

    /**
     * Each block should always be aligned based on its size.  This means that
     * all bits for its location less than its size should be zero.
     */
    private boolean isBlockAligned(long id, int blockSizeBits) throws IOException {
        assert isValidRange(id);
        assert isValidBlockSizeBits(blockSizeBits);
        return ((getBlockSize(blockSizeBits)-1)&id)==0;
    }

    /**
     * Each block should always be aligned based on its size.  This means that
     * all bits for its location less than its size should be zero.
     * Makes sure a block is complete: <code>(id + blockSize) &lt;= capacity</code>
     */
    private boolean isBlockAlignedAndComplete(long id, int blockSizeBits) throws IOException {
        assert isValidRange(id);
        assert isValidBlockSizeBits(blockSizeBits);
        long blockSize = getBlockSize(blockSizeBits);
        return
            ((blockSize-1)&id)==0                   // Aligned
            && (id+blockSize)<=pbuffer.capacity()   // Complete
        ;
    }

    /**
     * Checks if the provided block is allocated.  This is for debugging only
     * to be used in assertions.  It is not a reliable mechanism to use because
     * block combining and splitting can cause this method to check arbitrary
     * garbage.
     */
    private boolean isAllocated(long id) throws IOException {
        assert isValidRange(id);
        byte header = pbuffer.get(id);
        assert isBlockAlignedAndComplete(id, getBlockSizeBits(header));
        return isAllocated(header);
    }
    // </editor-fold>

    // <editor-fold desc="Allocation and Deallocation">
    /**
     * Adds the block at the provided location to the free space maps.
     */
    private void addFreeSpaceMap(long ptr, int blockSizeBits, long capacity) throws IOException {
        assert blockSizeBits>=0 && blockSizeBits<=0x3f;
        assert !isAllocated(ptr);
        // Group as much as possible within the same power-of-two block
        boolean writeMaxBits = false;
        for(int bits = blockSizeBits; bits<0x3f; bits++) {
            long blockSize = getBlockSize(bits);
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
                    blockSizeBits = bits+1;
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
                            blockSizeBits = bits+1;
                            writeMaxBits = true;
                            // Remove next from FSM
                            SortedSet<Long> fsm = freeSpaceMaps.get(bits);
                            if(fsm!=null) fsm.remove(nextPtr);
                        }
                    }
                }
            }
        }
        if(writeMaxBits) pbuffer.put(ptr, (byte)blockSizeBits);
        SortedSet<Long> fsm = freeSpaceMaps.get(blockSizeBits);
        if(fsm==null) freeSpaceMaps.set(blockSizeBits, fsm = new TreeSet<Long>());
        if(!fsm.add(ptr)) throw new AssertionError("Free space map already contains entry: "+ptr);
    }

    private long splitAllocate(int blockSizeBits, long capacity) throws IOException {
        assert isValidBlockSizeBits(blockSizeBits);
        assert capacity>=0;
        // TODO: Assertions from here
        SortedSet<Long> fsm = freeSpaceMaps.get(blockSizeBits);
        if(fsm!=null && !fsm.isEmpty()) {
            // No split needed
            Long ptr = fsm.first();
            fsm.remove(ptr);
            return ptr;
        } else {
            // End recursion
            if(blockSizeBits==0x3f) return -1;
            long blockSize = getBlockSize(blockSizeBits);
            if(blockSize>capacity) return -1;
            // Try split
            long biggerPtr = splitAllocate(blockSizeBits+1, capacity);
            // Unsplittable
            if(biggerPtr==-1) return -1;
            // Split the bigger one
            pbuffer.put(biggerPtr, (byte)blockSizeBits);
            long nextPtr = biggerPtr+blockSize;
            pbuffer.put(nextPtr, (byte)blockSizeBits);
            if(fsm==null) freeSpaceMaps.set(blockSizeBits, fsm = new TreeSet<Long>());
            fsm.add(nextPtr);
            return biggerPtr;
        }
    }

    public long allocate(long minimumSize) throws IOException {
        // Determine the min block size for the provided input
        int blockSizeBits = 64 - Long.numberOfLeadingZeros(minimumSize);
        long capacity = pbuffer.capacity();
        long ptr = splitAllocate(blockSizeBits, capacity);
        if(ptr==-1) {
            long blockSize = getBlockSize(blockSizeBits);
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
            if(blockSize<PAGE_SIZE) {
                pbuffer.setCapacity(blockStart + PAGE_SIZE);
                pbuffer.put(blockStart, (byte)(0x80 | blockSizeBits));
                // TODO: Faster way
                for(long pos=blockStart+blockSize, end=blockStart+PAGE_SIZE; pos<end; pos++) {
                    addFreeSpaceMap(pos, 0, end);
                }
            } else {
                pbuffer.setCapacity(blockStart+blockSize);
                pbuffer.put(blockStart, (byte)(0x80 | blockSizeBits));
            }
            ptr = blockStart;
        }
        return ptr;
    }

    public void deallocate(long id) throws IOException, IllegalStateException {
        byte header = pbuffer.get(id);
        int blockSizeBits = getBlockSizeBits(header);
        assert isBlockAlignedAndComplete(id, blockSizeBits);
        if(!isAllocated(header)) throw new AssertionError("Block not allocated");
        pbuffer.put(id, (byte)(header&0x7f));
        addFreeSpaceMap(id, blockSizeBits, pbuffer.capacity());
    }
    // </editor-fold>

    // <editor-fold desc="PersistentBlockBuffer Implementation">
    public Iterator<Long> iterateBlockIds() throws IOException {
        return new Iterator<Long>() {
            // TODO: Add modCount
            long nextPtr = 0;
            public boolean hasNext() {
                try {
                    long capacity = pbuffer.capacity();
                    while(nextPtr<capacity) {
                        byte header = pbuffer.get(nextPtr);
                        int blockSizeBits = getBlockSizeBits(header);
                        assert isBlockAlignedAndComplete(nextPtr, blockSizeBits);
                        if(isAllocated(header)) return true;
                        nextPtr += getBlockSize(blockSizeBits);
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
                        int blockSizeBits = getBlockSizeBits(header);
                        assert isBlockAlignedAndComplete(nextPtr, blockSizeBits);
                        long ptr = nextPtr;
                        nextPtr += getBlockSize(blockSizeBits);
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

    /**
     * Gets the maximum amount of data that may be stored in the entry.  This
     * is the underlying power-of-two block size minus one.  May only check
     * the block size of allocated blocks.
     */
    public long getBlockSize(long id) throws IOException {
        assert isValidRange(id);
        byte header = pbuffer.get(id);
        int blockSizeBits = getBlockSizeBits(header);
        assert isBlockAlignedAndComplete(id, blockSizeBits);
        if(!isAllocated(header)) throw new IOException("Block not allocated: "+id);
        return getBlockSize(blockSizeBits) - 1;
    }

    protected long getBlockAddress(long id) {
        return id;
    }

    /**
     * The capacity should always be enough because the capacity ensured here is
     * constrained to a single block, and blocks are always allocated fully.
     * This merely asserts this fact.
     */
    protected void ensureCapacity(long capacity) throws IOException {
        assert pbuffer.capacity()>=capacity: "pbuffer.capacity()<capacity";
    }
    // </editor-fold>
}
