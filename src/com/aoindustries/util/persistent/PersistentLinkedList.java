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

import com.aoindustries.io.BetterByteArrayInputStream;
import com.aoindustries.io.BetterByteArrayOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * Serializes and stores objects in a persistent buffer.  Unlike <code>FileList</code> which
 * is intended for efficient <code>RandomAccess</code>,
 * this is a linked list implementation and has the expected qualities and costs.
 * There are no size limits to the stored data.  Fragmentation may occur in the file
 * over time, but is minimized by the use of per-block size free space maps.
 * There is currently no compaction tool or conversion between block sizes.
 * </p>
 * <p>
 * This class is not thread-safe.  It is absolutely critical that external
 * synchronization be applied.
 * </p>
 * <p>
 * This class is intended for persistence, not for intra-process or intra-thread
 * shared data.  TODO: For performance, it maintains the most recently used entries in a
 * cache.
 * </p>
 * <p>
 * The file starts with a header:
 *     Offset   Type  Description
 *      0-15    ASCII LinkedFileList\n\0
 *     16-19    int   version
 *     20-27    long  position of the head or <code>TAIL_PTR (24)</code> if empty.
 *     28-35    long  position of the tail or <code>HEAD_PTR (20)</code> if empty.
 * </p>
 * <p>
 * Each entry consists of:
 *     Offset   Name        Type     Description
 *     +0       maxBits     byte     (0-31) the power of two that contains the data (0=1, 1=2, 2=4, 3=8, ..., 31=2^31).  Maximum number of bytes that may be stored in the
 *                                   data segment of this entry (used to determine block size).
 *     +1       next        long     position of next, <code>8</code> for last element, or <code>-1</code> for entry available.
 *     +9       prev        long     position of prev, <code>0</code> for first element, or <code>-1</code> for entry available.
 *     +17      compressed  boolean  flag indicating the data is gzip compressed.
 *     +18      dataSize    int      the size of the serialized (and optionally compressed) data, must always be &lt;= 2^maxBits, <code>-1</code> means null element
 *     +22      data        byte[]   the binary data.
 * </p>
 *
 * <pre>
 * TODO: Should we align on OS-level block sizes, or fixed block of 4096?
 *           This implies that blocks themselves must be 2^n in size, not 22+2^n like now.
 *           This would also make merging and splitting blocks pretty easy to better use free space on varying data sizes
 * TODO: In Java 1.6 support Deque interface instead of just Queue
 * TODO: Check for consistency in the constructor (all prevs and nexts match without loops), can set size there, too.
 * </pre>
 *
 * @author  AO Industries, Inc.
 */
public class PersistentLinkedList<E extends Serializable> extends AbstractSequentialList<E> implements List<E>, Queue<E> {

    private static final long serialVersionUID = 1L;

    private static final byte[] MAGIC={
        'P',
        'e',
        'r',
        's',
        'i',
        's',
        't',
        'e',
        'n',
        't',
        'L',
        'i',
        'n',
        'k',
        'e',
        'd',
        'L',
        'i',
        's',
        't',
        '\n'
    };

    private static final int VERSION = 1;

    /**
     * The constant location of the head pointer.
     */
    private static final long HEAD_PTR = MAGIC.length+4;

    /**
     * The constant location of the tail pointer.
     */
    private static final long TAIL_PTR = HEAD_PTR+8;

    /**
     * The total number of bytes in the header.
     */
    private static final int HEADER_SIZE = (int)(TAIL_PTR + 8);

    /**
     * Checks if the subrange of two byte arrays is equal.
     */
    private static boolean equals(byte[] b1, byte[] b2, int off, int len) {
        for(int end=off+len; off<end; off++) {
            if(b1[off]!=b2[off]) return false;
        }
        return true;
    }

    private final byte[] ioBuffer = new byte[22];

    final private PersistentBuffer pbuffer;
    final private boolean defaultGZIP;
    final private boolean useFsync;

    private BetterByteArrayInputStream bufferIn; // Used during deserialization - could get directly from underlying stream, but would involve more I/Os.
    final private BetterByteArrayOutputStream bufferOut = new BetterByteArrayOutputStream(); // Used during serialization

    private long _head;
    private long _tail;
    private int _size;

    private final List<Set<Long>> freeSpaceMaps = new ArrayList<Set<Long>>(32);

    /**
     * Tracks free space on a per
     */

    // <editor-fold desc="Constructors">
    /**
     * Constructs a list backed by a temporary file.  The temporary file will be
     * deleted at JVM shutdown.
     * Operates in constant time.
     *
     * @see  RandomAccessFileBuffer#RandomAccessFileBuffer()
     */
    public PersistentLinkedList() throws IOException {
        pbuffer = new RandomAccessFileBuffer();
        defaultGZIP = false;
        useFsync = false;
        for(int c=0;c<32;c++) freeSpaceMaps.add(null);
        clear();
    }

    /**
     * Constructs a list with a temporary file containing all of the provided elements.
     * The temporary file will be deleted at JVM shutdown.
     * Operates in linear time.
     */
    public PersistentLinkedList(Collection<? extends E> c) throws IOException {
        this();
        addAll(c);
    }

    /**
     * Constructs a list backed by the provided persisent buffer.
     * Operates in linear time in order to cache the size.
     */
    public PersistentLinkedList(PersistentBuffer pbuffer, boolean defaultGZIP, boolean useFsync) throws IOException {
        this.pbuffer = pbuffer;
        this.defaultGZIP = defaultGZIP;
        this.useFsync = useFsync;
        for(int c=0;c<32;c++) freeSpaceMaps.add(null);
        // Read the head and tail to maintain in cache
        long len = pbuffer.capacity();
        if(len==0) clear();
        else if(len<HEADER_SIZE) throw new IOException("File does not have a complete header");
        else {
            pbuffer.get(0, ioBuffer, 0, MAGIC.length);
            if(!equals(ioBuffer, MAGIC, 0, MAGIC.length)) throw new IOException("File does not appear to be a LinkedFileList (MAGIC mismatch)");
            int version = pbuffer.getInt(MAGIC.length);
            if(version!=VERSION) throw new IOException("Unsupported file version: "+version);
            _head = pbuffer.getLong(MAGIC.length+4);
            _tail = pbuffer.getLong(MAGIC.length+12);
            assert _head==TAIL_PTR || isAllocated(_head);
            assert _tail==HEAD_PTR || isAllocated(_tail);
            int count = 0;
            long ptr = HEADER_SIZE;
            for(; ptr<len; ptr+=getEntrySize(getMaxBits(ptr))) {
                if(isAllocated(ptr)) count++;
                else addFreeSpaceMap(ptr);
            }
            if(ptr!=len) throw new IOException("ptr!=len: "+ptr+"!="+len);
            _size = count;
        }
    }
    // </editor-fold>

    // <editor-fold desc="Pointer Assertions">
    /**
     * Checks that the ptr is in the valid address range.
     */
    private boolean isValidRange(long ptr) throws IOException {
        return ptr>=HEADER_SIZE && ptr<pbuffer.capacity();
    }

    /**
     * Checks if the entry is allocated, also asserts isValidRange first.
     */
    private boolean isAllocated(long ptr) throws IOException {
        assert isValidRange(ptr) : "Invalid range: "+ptr;
        return pbuffer.getLong(ptr+1)!=-1 && pbuffer.getLong(ptr+9)!=-1;
    }
    // </editor-fold>

    // <editor-fold desc="Pointer Management">
    /**
     * Gets the head pointer or <code>TAIL_PTR</code> if the list is empty.
     */
    private long getHead() {
        return _head;
    }

    /**
     * Sets the head to the provided value.
     */
    private void setHead(long head) throws IOException {
        assert head==TAIL_PTR || isAllocated(head);
        pbuffer.putLong(HEAD_PTR, head);
        this._head = head;
    }

    private long getTail() {
        return _tail;
    }

    /**
     * Sets the tail to the provided value.
     */
    private void setTail(long tail) throws IOException {
        assert tail==HEAD_PTR || isAllocated(tail);
        pbuffer.putLong(TAIL_PTR, tail);
        this._tail = tail;
    }

    /**
     * Gets the next pointer for the entry at the provided location.
     * The entry must be allocated.
     */
    private long getNext(long ptr) throws IOException {
        assert isAllocated(ptr);
        return pbuffer.getLong(ptr+1);
    }

    /**
     * Sets the next pointer for the entry at the provided location.
     * The entry must be allocated.
     */
    private void setNext(long ptr, long next) throws IOException {
        assert isAllocated(ptr);
        assert next==TAIL_PTR || isAllocated(next);
        pbuffer.putLong(ptr+1, next);
    }

    /**
     * Gets the prev pointer for the entry at the provided location.
     * The entry must be allocated.
     */
    private long getPrev(long ptr) throws IOException {
        assert isAllocated(ptr);
        return pbuffer.getLong(ptr+9);
    }

    /**
     * Sets the prev pointer for the entry at the provided location.
     * The entry must be allocated.
     */
    private void setPrev(long ptr, long prev) throws IOException {
        assert isAllocated(ptr);
        assert prev==HEAD_PTR || isAllocated(prev);
        pbuffer.putLong(ptr+9, prev);
    }

    /**
     * Gets the maximum amount of data that may be stored in the entry.
     */
    private int getMaxBits(long ptr) throws IOException {
        assert isValidRange(ptr);
        int maxBits = pbuffer.get(ptr);
        assert maxBits>=0 && maxBits<=31;
        return maxBits;
    }

    /**
     * Gets the compressed flag for the entry.
     * The entry must be allocated.
     */
    private boolean isCompressed(long ptr) throws IOException {
        assert isAllocated(ptr);
        return pbuffer.getBoolean(ptr+17);
    }

    /**
     * Gets the size of the data for the entry at the provided location.
     * This does not include pointers or flags.
     * The entry must be allocated.
     */
    private int getDataSize(long ptr) throws IOException {
        assert isAllocated(ptr);
        return pbuffer.getInt(ptr+18);
    }

    /**
     * Checks if the provided element is null.
     * The entry must be allocated.
     */
    private boolean isNull(long ptr) throws IOException {
        return getDataSize(ptr)==-1;
    }

    /**
     * Gets the element for the entry at the provided location.
     * The entry must be allocated.
     */
    @SuppressWarnings("unchecked")
    private E getElement(long ptr) throws IOException {
        assert isAllocated(ptr);
        pbuffer.get(ptr+17, ioBuffer, 0, 5);
        boolean isCompressed = ioBuffer[0]!=0;
        int dataSize =
              ((ioBuffer[1]&255) << 24)
            + ((ioBuffer[2]&255) << 16)
            + ((ioBuffer[3]&255) << 8)
            + (ioBuffer[4]&255)
        ;
        if(dataSize==-1) return null;
        assert ((ptr+22)+dataSize)<=pbuffer.capacity(); // Must not extend past end of file

        // assert dataSize>=0 && ((long)dataSize)<=(1L << getMaxBits(ptr)); // Must not exceed maximum size
        // Only Required for previous assertion: raf.seek(ptr+22);

        // Create or grow the buffer
        if(bufferIn==null || bufferIn.getInternalByteArray().length<dataSize) bufferIn = new BetterByteArrayInputStream(new byte[dataSize]);
        // Fill the buffer
        bufferIn.readFrom(pbuffer, ptr+22, dataSize);
        // Read the object
        ObjectInputStream oin = new ObjectInputStream(isCompressed ? new GZIPInputStream(bufferIn) : bufferIn);
        try {
            return (E)oin.readObject();
        } catch(ClassNotFoundException err) {
            IOException ioErr = new IOException();
            ioErr.initCause(err);
            throw ioErr;
        } finally {
            oin.close();
        }
    }

    /**
     * Gets the overall size of an entry given its maxBits.
     * This includes the pointers and flags.
     */
    private static long getEntrySize(int maxBits) {
        assert maxBits>=0 && maxBits<=31;
        return 22L + (1L << maxBits);
    }

    /**
     * Deallocates the entry at the provided location.
     * The entry must be allocated.
     */
    private void deallocate(long ptr) throws IOException {
        deallocate(ptr, getMaxBits(ptr));
    }

    /**
     * Deallocates the entry at the provided location.
     * The entry must be allocated.
     */
    private void deallocate(long ptr, int maxBits) throws IOException {
        assert isAllocated(ptr);
        assert _size>0;
        pbuffer.putLong(ptr+1, -1);
        pbuffer.putLong(ptr+9, -1);
        _size--;
        addFreeSpaceMap(ptr, maxBits);
    }
    // </editor-fold>

    // <editor-fold desc="Data Structure Management">
    /**
     * Will perform fsync only if fsync is enabled.
     */
    private void fsync() throws IOException {
        if(useFsync) pbuffer.force();
    }

    /**
     * Adds the block at the provided location to the free space maps.
     * The entry must not be allocated.
     */
    private void addFreeSpaceMap(long ptr) throws IOException {
        addFreeSpaceMap(ptr, getMaxBits(ptr));
    }

    /**
     * Adds the block at the provided location to the free space maps.
     * The entry must not be allocated.
     */
    private void addFreeSpaceMap(long ptr, int maxBits) throws IOException {
        assert !isAllocated(ptr);
        Set<Long> fsm = freeSpaceMaps.get(maxBits);
        if(fsm==null) freeSpaceMaps.set(maxBits, fsm = new TreeSet<Long>());
        if(!fsm.add(ptr)) throw new AssertionError("Free space map already contains entry: "+ptr);
    }

    /**
     * Removes the entry at the provided location and restores it to an unallocated state.
     * Operates in constant time.
     * The entry must be allocated.
     */
    private void remove(long ptr) throws IOException {
        assert isAllocated(ptr);
        long prev = getPrev(ptr);
        long next = getNext(ptr);
        if(prev==HEAD_PTR) setHead(next);
        else setNext(prev, next);
        if(next==TAIL_PTR) setTail(prev);
        else setPrev(next, prev);
        deallocate(ptr);
    }

    /**
     * Serializes the object to bufferOut.
     * Operates in constant time.
     */
    private void serialize(E element, boolean compress) throws IOException {
        bufferOut.reset();
        ObjectOutputStream oout = new ObjectOutputStream(compress ? new GZIPOutputStream(bufferOut) : bufferOut);
        try {
            oout.writeObject(element);
        } finally {
            oout.close();
        }
    }

    /**
     * Allocates the first free space available that can hold the requested amount of
     * data.  The amount of data should not include the pointer space.
     *
     * Operates in logarithic complexity on the amount of free entries.
     */
    private long allocateEntry(long next, long prev, boolean compress, int dataSize, byte[] data) throws IOException {
        assert next==TAIL_PTR || isAllocated(next);
        assert prev==HEAD_PTR || isAllocated(prev);
        assert (dataSize==-1 && data==null) || (dataSize>=0 && data!=null);
        if(_size==Integer.MAX_VALUE) throw new IOException("List is full: _size==Integer.MAX_VALUE");
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
            Utils.longToBuffer(next, ioBuffer, 0);                 // 0-7
            Utils.longToBuffer(prev, ioBuffer, 8);                 // 8-15
            ioBuffer[16] = compress ? (byte)1 : (byte)0;     // 16
            Utils.intToBuffer(dataSize, ioBuffer, 17);             // 17-20
            pbuffer.put(ptr+1, ioBuffer, 0, 21);
            if(dataSize>0) pbuffer.put(ptr+22, data, 0, dataSize);
            _size++;
            return ptr;
        }
        // Allocate more space at the end of the file
        long ptr = pbuffer.capacity();
        long newLen = ptr + 22 + (1L<<(long)maxBits);
        pbuffer.setCapacity(newLen);
        ioBuffer[0] = (byte)maxBits;                     // 0
        Utils.longToBuffer(next, ioBuffer, 1);                 // 1-8
        Utils.longToBuffer(prev, ioBuffer, 9);                 // 9-16
        ioBuffer[17] = compress ? (byte)1 : (byte)0;     // 17
        Utils.intToBuffer(dataSize, ioBuffer, 18);             // 18-21
        pbuffer.put(ptr, ioBuffer, 0, 22);
        if(dataSize>0) pbuffer.put(ptr+22, data, 0, dataSize);
        _size++;
        return ptr;
    }

    /**
     * Adds the first entry to the list.
     * Operates in constant time.
     */
    private void addFirstEntry(final E element, final boolean compress) throws IOException {
        assert getHead()==TAIL_PTR;
        assert getTail()==HEAD_PTR;
        int dataSize;
        byte[] data;
        if(element==null) {
            dataSize=-1;
            data = null;
        }
        else {
            serialize(element, compress);
            dataSize = bufferOut.size();
            data = bufferOut.getInternalByteArray();
        }
        long newPtr = allocateEntry(TAIL_PTR, HEAD_PTR, compress, dataSize, data);
        setHead(newPtr);
        setTail(newPtr);
    }

    /**
     * Adds the provided element before the element at the provided location.
     * Operates in log time for free space.
     */
    private void addBefore(final E element, final long ptr, final boolean compress) throws IOException {
        assert isAllocated(ptr);
        int dataSize;
        byte[] data;
        if(element==null) {
            dataSize=-1;
            data = null;
        }
        else {
            serialize(element, compress);
            dataSize = bufferOut.size();
            data = bufferOut.getInternalByteArray();
        }
        long prev = getPrev(ptr);
        long newPtr = allocateEntry(ptr, prev, compress, dataSize, data);
        if(prev==HEAD_PTR) setHead(newPtr);
        else setNext(prev, newPtr);
        setPrev(ptr, newPtr);
    }

    /**
     * Adds the provided element after the element at the provided location.
     * Operates in log time for free space.
     */
    private void addAfter(final E element, final long ptr, final boolean compress) throws IOException {
        assert isAllocated(ptr);
        int dataSize;
        byte[] data;
        if(element==null) {
            dataSize=-1;
            data = null;
        }
        else {
            serialize(element, compress);
            dataSize = bufferOut.size();
            data = bufferOut.getInternalByteArray();
        }
        long next = getNext(ptr);
        long newPtr = allocateEntry(next, ptr, compress, dataSize, data);
        if(next==TAIL_PTR) setTail(newPtr);
        else setPrev(next, newPtr);
        setNext(ptr, newPtr);
    }
    // </editor-fold>

    // <editor-fold desc="Queue/Deque Implementation">
    /**
     * Returns the first element in this list.
     * Operates in constant time.
     *
     * @return the first element in this list
     * @throws NoSuchElementException if this list is empty
     */
    public E getFirst() {
        long head=getHead();
        if(head==TAIL_PTR) throw new NoSuchElementException();
        try {
            return getElement(head);
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Returns the last element in this list.
     * Operates in constant time.
     *
     * @return the last element in this list
     * @throws NoSuchElementException if this list is empty
     */
    public E getLast()  {
        long tail=getTail();
        if(tail==HEAD_PTR) throw new NoSuchElementException();
        try {
            return getElement(tail);
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Removes and returns the first element from this list.
     * Operates in constant time.
     *
     * @return the first element from this list
     * @throws NoSuchElementException if this list is empty
     */
    public E removeFirst() {
        long head = getHead();
        if(head==TAIL_PTR) throw new NoSuchElementException();
        try {
            modCount++;
            E element = getElement(head);
            remove(head);
            fsync();
            return element;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Removes and returns the last element from this list.
     * Operates in constant time.
     *
     * @return the last element from this list
     * @throws NoSuchElementException if this list is empty
     */
    public E removeLast() {
        long tail = getTail();
        if(tail==HEAD_PTR) throw new NoSuchElementException();
        try {
            modCount++;
            E element = getElement(tail);
            remove(tail);
            fsync();
            return element;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Inserts the specified element at the beginning of this list.
     * Operates in log time for free space.
     *
     * @param e the element to add
     */
    public void addFirst(E element) {
        try {
            modCount++;
            long head = getHead();
            if(head==TAIL_PTR) addFirstEntry(element, defaultGZIP);
            else {
                addBefore(element, head, defaultGZIP);
            }
            fsync();
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Appends the specified element to the end of this list.
     * Operates in log time for free space.
     *
     * <p>This method is equivalent to {@link #add}.
     *
     * @param e the element to add
     */
    public void addLast(E element) {
        try {
            modCount++;
            long tail = getTail();
            if(tail==HEAD_PTR) addFirstEntry(element, defaultGZIP);
            else {
                addAfter(element, tail, defaultGZIP);
            }
            fsync();
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Gets the pointer for the provided index.
     *
     * This runs in linear time.
     */
    private long getPointerForIndex(int index) throws IOException {
        if(index<(_size<<1)) {
            long ptr = getHead();
            for(int i=0;i<index;i++) ptr = getNext(ptr);
            return ptr;
        } else {
            // Search backwards
            long ptr = getTail();
            for(int i=index;i>index;i--) ptr = getPrev(ptr);
            return ptr;
        }
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If this list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns <tt>true</tt> if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        try {
            if(o==null) {
                for(long ptr = getHead(); ptr!=TAIL_PTR; ptr = getNext(ptr)) {
                    if(isNull(ptr)) {
                        modCount++;
                        remove(ptr);
                        fsync();
                        return true;
                    }
                }
            } else {
                for(long ptr = getHead(); ptr!=TAIL_PTR; ptr = getNext(ptr)) {
                    if(o.equals(getElement(ptr))) {
                        modCount++;
                        remove(ptr);
                        fsync();
                        return true;
                    }
                }
            }
            return false;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator.  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in
     * progress.  (Note that this will occur if the specified collection is
     * this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if(c.isEmpty()) return false;
        modCount++;
        for(E element : c) addLast(element);
        return true;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element
     *              from the specified collection
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if(index==_size) return addAll(c);
        if(c.isEmpty()) return false;
        if (index < 0 || index > _size) throw new IndexOutOfBoundsException("Index: "+index+", Size: "+_size);
    	modCount++;
        try {
            long ptr = getPointerForIndex(index);
            for(E element : c) addBefore(element, ptr, defaultGZIP);
            fsync();
            return true;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets the number of elements in this list.
     * Operates in constant time.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return _size;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }
    // </editor-fold>

    /**
     * Clears the list.
     */
    @Override
    public void clear() {
        try {
            modCount++;
            pbuffer.setCapacity(HEADER_SIZE);
            pbuffer.put(0, MAGIC, 0, MAGIC.length);
            pbuffer.putInt(MAGIC.length, VERSION);
            setHead(TAIL_PTR);
            setTail(HEAD_PTR);
            _size = 0;
            for(Set<Long> fsm : freeSpaceMaps) {
                if(fsm!=null) fsm.clear();
            }
            fsync();
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E get(int index) {
        try {
            return getElement(getPointerForIndex(index));
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * TODO: First try to replace at the current position.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    private void setElement(long ptr, E element) {
        modCount++;
        try {
            long prev = getPrev(ptr);
            remove(ptr);
            if(prev==HEAD_PTR) addFirst(element);
            else {
                addAfter(element, prev, defaultGZIP);
                fsync();
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        try {
            long ptr = getPointerForIndex(index);
            E oldElement = getElement(ptr);
            setElement(ptr, element);
            return oldElement;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        modCount++;
        try {
            long ptr = getPointerForIndex(index);
            long prev = getPrev(ptr);
            if(prev==HEAD_PTR) addFirst(element);
            else {
                addAfter(element, prev, defaultGZIP);
                fsync();
            }
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public E remove(int index) {
        modCount++;
        try {
            long ptr = getPointerForIndex(index);
            E oldElement = getElement(ptr);
            remove(ptr);
            fsync();
            return oldElement;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    @Override
    public int indexOf(Object o) {
        try {
            int index = 0;
            if(o==null) {
                for(long ptr = getHead(); ptr!=TAIL_PTR; ptr = getNext(ptr)) {
                    if(isNull(ptr)) return index;
                    index++;
                }
            } else {
                for(long ptr = getHead(); ptr!=TAIL_PTR; ptr = getNext(ptr)) {
                    if(o.equals(getElement(ptr))) return index;
                    index++;
                }
            }
            return -1;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    @Override
    public int lastIndexOf(Object o) {
        try {
            int index = _size;
            if(o==null) {
                for(long ptr = getTail(); ptr!=HEAD_PTR; ptr = getPrev(ptr)) {
                    --index;
                    if(isNull(ptr)) return index;
                }
            } else {
                for(long ptr = getTail(); ptr!=HEAD_PTR; ptr = getPrev(ptr)) {
                    --index;
                    if(o.equals(getElement(ptr))) return index;
                }
            }
            return -1;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    public E peek() {
        if(_size==0) return null;
        return getFirst();
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list
     * @return the head of this list, or <tt>null</tt> if this list is empty
     * @since 1.5
     */
    public E poll() {
        if(_size==0) return null;
        return removeFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     *
     * @param e the element to add
     * @return <tt>true</tt> (as specified by {@link Queue#offer})
     * @since 1.5
     */
    public boolean offer(E e) {
        return add(e);
    }

    /**
     * Inserts the specified element at the front of this list.
     *
     * @param e the element to insert
     * @return <tt>true</tt> (as specified by {@link Deque#offerFirst})
     * @since 1.6
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this list.
     *
     * @param e the element to insert
     * @return <tt>true</tt> (as specified by {@link Deque#offerLast})
     * @since 1.6
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves, but does not remove, the first element of this list,
     * or returns <tt>null</tt> if this list is empty.
     *
     * @return the first element of this list, or <tt>null</tt>
     *         if this list is empty
     * @since 1.6
     */
    public E peekFirst() {
        if(_size==0)
            return null;
        return getFirst();
    }

    /**
     * Retrieves, but does not remove, the last element of this list,
     * or returns <tt>null</tt> if this list is empty.
     *
     * @return the last element of this list, or <tt>null</tt>
     *         if this list is empty
     * @since 1.6
     */
    public E peekLast() {
        if(_size==0)
            return null;
        return getLast();
    }

    /**
     * Retrieves and removes the first element of this list,
     * or returns <tt>null</tt> if this list is empty.
     *
     * @return the first element of this list, or <tt>null</tt> if
     *     this list is empty
     * @since 1.6
     */
    public E pollFirst() {
        if(_size==0)
            return null;
        return removeFirst();
    }

    /**
     * Retrieves and removes the last element of this list,
     * or returns <tt>null</tt> if this list is empty.
     *
     * @return the last element of this list, or <tt>null</tt> if
     *     this list is empty
     * @since 1.6
     */
    public E pollLast() {
        if (_size==0)
            return null;
        return removeLast();
    }

    /**
     * Pushes an element onto the stack represented by this list.  In other
     * words, inserts the element at the front of this list.
     *
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @since 1.6
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this list.  In other
     * words, removes and returns the first element of this list.
     *
     * <p>This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this list (which is the top
     *         of the stack represented by this list)
     * @throws NoSuchElementException if this list is empty
     * @since 1.6
     */
    public E pop() {
        return removeFirst();
    }

    /**
     * Removes the first occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if the list contained the specified element
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * Removes the last occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if the list contained the specified element
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        try {
            if(o==null) {
                for(long ptr = getTail(); ptr!=HEAD_PTR; ptr = getPrev(ptr)) {
                    if(isNull(ptr)) {
                        modCount++;
                        remove(ptr);
                        fsync();
                        return true;
                    }
                }
            } else {
                for(long ptr = getTail(); ptr!=HEAD_PTR; ptr = getPrev(ptr)) {
                    if(o.equals(getElement(ptr))) {
                        modCount++;
                        remove(ptr);
                        fsync();
                        return true;
                    }
                }
            }
            return false;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * Obeys the general contract of <tt>List.listIterator(int)</tt>.<p>
     *
     * The list-iterator is <i>fail-fast</i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own <tt>remove</tt> or <tt>add</tt>
     * methods, the list-iterator will throw a
     * <tt>ConcurrentModificationException</tt>.  Thus, in the face of
     * concurrent modification, the iterator fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future.
     *
     * @param index index of the first element to be returned from the
     *              list-iterator (by a call to <tt>next</tt>)
     * @return a ListIterator of the elements in this list (in proper
     *         sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private long lastReturned = TAIL_PTR;
        private long nextPtr;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            if (index < 0 || index > _size)
            throw new IndexOutOfBoundsException("Index: "+index+
                                ", Size: "+_size);
            try {
                if (index < (_size >> 1)) {
                    nextPtr = getHead();
                    for (nextIndex=0; nextIndex<index; nextIndex++) nextPtr = getNext(nextPtr);
                } else {
                    nextPtr = getTail();
                    if(nextPtr==HEAD_PTR) {
                        // List empty
                        nextIndex = 0;
                    } else {
                        for (nextIndex=_size-1; nextIndex>index; nextIndex--) nextPtr = getPrev(nextPtr);
                    }
                }
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }

        public boolean hasNext() {
            return nextIndex != _size;
        }

        public E next() {
                checkForComodification();
            if (nextIndex == _size)
            throw new NoSuchElementException();

            try {
                lastReturned = nextPtr;
                nextPtr = getNext(nextPtr);
                nextIndex++;
                return getElement(lastReturned);
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }

        public boolean hasPrevious() {
            return nextIndex != 0;
        }

        public E previous() {
            if (nextIndex == 0)
            throw new NoSuchElementException();

            try {
                lastReturned = nextPtr = getPrev(nextPtr);
                nextIndex--;
                checkForComodification();
                return getElement(lastReturned);
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex-1;
        }

        public void remove() {
            checkForComodification();
            try {
                long lastNext = getNext(lastReturned);
                try {
                    PersistentLinkedList.this.remove(lastReturned);
                    fsync();
                } catch (NoSuchElementException e) {
                    throw new IllegalStateException();
                }
                if(nextPtr==lastReturned) nextPtr = lastNext;
                else nextIndex--;
                lastReturned = TAIL_PTR;
                expectedModCount++;
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }

        public void set(E e) {
            if (lastReturned == TAIL_PTR)
            throw new IllegalStateException();
            checkForComodification();
            setElement(lastReturned, e);
        }

        public void add(E e) {
            checkForComodification();
            try {
                lastReturned = TAIL_PTR;
                modCount++;
                addBefore(e, nextPtr, defaultGZIP);
                fsync();
                nextIndex++;
                expectedModCount++;
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
        }
    }

    /**
     * @since 1.6
     */
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /** Adapter to provide descending iterators via ListItr.previous */
    private class DescendingIterator implements Iterator<E> {
        final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
                return itr.previous();
            }
        public void remove() {
            itr.remove();
        }
    }

    @Override
    public Object[] toArray() {
        try {
            Object[] result = new Object[_size];
            int i = 0;
            for (long ptr = getHead(); ptr != TAIL_PTR; ptr = getNext(ptr)) result[i++] = getElement(ptr);
            return result;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        try {
            if (a.length < _size)
                a = (T[])java.lang.reflect.Array.newInstance(
                                    a.getClass().getComponentType(), _size);
            int i = 0;
            Object[] result = a;
            for (long ptr = getHead(); ptr != TAIL_PTR; ptr = getNext(ptr)) result[i++] = getElement(ptr);
            if (a.length > _size)
            a[_size] = null;

            return a;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    @Override
    public void finalize() throws IOException {
        close();
    }

    /**
     * Closes the random access file backing this list.
     */
    public void close() throws IOException {
        pbuffer.close();
    }
}
