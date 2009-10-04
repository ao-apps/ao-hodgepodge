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

import com.aoindustries.io.BetterByteArrayOutputStream;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * <p>
 * Serializes and stores objects in a persistent buffer.  Unlike <code>FileList</code> which
 * is intended for efficient <code>RandomAccess</code>,
 * this is a linked list implementation and has the expected benefits and costs.
 * There are no size limits to the stored data.
 * </p>
 * <p>
 * This class is not thread-safe.  It is absolutely critical that external
 * synchronization be applied.
 * </p>
 * <p>
 * The objects are serialized using the standard Java serialization, unless a
 * <code>Serializer</code> is provided.  If an object that is not <code>Serializable</code>
 * is to be stored, a <code>Serializer</code> must be provided.  <code>Serializer</code>s
 * may also provide a more efficient or more compact representation of an object.
 * </p>
 * <p>
 * This class is intended for persistence, not for intra-process or intra-thread
 * shared data.  TODO: For performance, it maintains the most recently used entries in a
 * cache.
 * </p>
 * <p>
 * The first block allocated is a header:
 *     Offset   Type  Description
 *      0- 3    ASCII "PLL\n"
 *      4- 7    int   version
 *      8-15    long  position of the head or <code>END_PTR</code> if empty.
 *     16-23    long  position of the tail or <code>END_PTR</code> if empty.
 * </p>
 * <p>
 * Each entry consists of:
 *     Offset   Name        Type     Description
 *       0- 7   next        long     block id of next, <code>END_PTR</code> for last element, or <code>NULL_PTR</code> for <code>null</code>
 *       8-15   prev        long     block id of prev, <code>END_PTR</code> for first element, or <code>NULL_PTR</code> for <code>null</code>
 *      16-23   dataSize    long     the size of the serialized data, <code>-1</code> means null element
 *      24+     data        data     the binary data
 * </p>
 *
 * <pre>
 * TODO: In Java 1.6 support Deque interface instead of just Queue
 * TODO: Check for consistency in the constructor (all prevs and nexts match without loops), can set size there, too.
 * TODO: Add corrupt flag, set of exceptions?  Cause immediate crash recovery?
 * </pre>
 *
 * @author  AO Industries, Inc.
 */
public class PersistentLinkedList<E> extends AbstractSequentialList<E> implements List<E>, Queue<E> {

    private static final byte[] MAGIC={'P', 'L', 'L', '\n'};

    private static final int VERSION = 3;

    /**
     * The value used to represent a <code>null</code> pointer.
     */
    private static final long NULL_PTR = -1;

    /**
     * The value used to represent an ending pointer.
     */
    private static final long END_PTR = -2;

    /**
     * The constant location of the head pointer.
     */
    private static final long HEAD_OFFSET = MAGIC.length+4;

    /**
     * The constant location of the tail pointer.
     */
    private static final long TAIL_OFFSET = HEAD_OFFSET+8;

    /**
     * The total number of bytes in the header.
     */
    private static final int HEADER_SIZE = (int)(TAIL_OFFSET + 8);

    /**
     * The block offset for <code>next</code>.
     */
    private static final int NEXT_OFFSET = 0;

    /**
     * The block offset for <code>prev</code>.
     */
    private static final int PREV_OFFSET = 8;

    /**
     * The block offset for <code>dataSize</code>.
     */
    private static final int DATA_SIZE_OFFSET = 16;

    /**
     * The block offset for the beginning of the data.
     */
    private static final int DATA_OFFSET = 24;

    /**
     * Value used to indicate <code>null</code> data.
     */
    private static final long DATA_SIZE_NULL = -1;

    final private Serializer<E> serializer;
    final private PersistentBlockBuffer blockBuffer;

    final private byte[] ioBuffer = new byte[Math.max(DATA_OFFSET, MAGIC.length)];

    // Cached for higher performance
    private long metaDataBlockId;
    private long _head;
    private long _tail;
    private long _size;

    // <editor-fold desc="Constructors">
    /**
     * Constructs a list backed by a temporary file using standard serialization.
     * The temporary file will be deleted at JVM shutdown.
     * Operates in constant time.
     *
     * @see  PersistentCollections#getPersistentBuffer(long)
     */
    public PersistentLinkedList(Class<E> type) throws IOException {
        serializer = PersistentCollections.getSerializer(type);
        blockBuffer = PersistentCollections.getPersistentBlockBuffer(
            serializer,
            PersistentCollections.getPersistentBuffer(Long.MAX_VALUE),
            Math.max(HEADER_SIZE, DATA_OFFSET)
        );
        clear();
    }

    /**
     * Constructs a list with a temporary file using standard serialization containing all of the provided elements.
     * The temporary file will be deleted at JVM shutdown.
     * Operates in linear time.
     */
    public PersistentLinkedList(Class<E> type, Collection<? extends E> c) throws IOException {
        this(type);
        addAll(c);
    }

    /**
     * Constructs a list backed by the provided persistent buffer using the most efficient serialization
     * for the provided type.
     * Operates in linear time in order to cache the size.
     *
     * @see  PersistentCollections#getSerializer(java.lang.Class)
     */
    public PersistentLinkedList(PersistentBuffer pbuffer, Class<E> type) throws IOException {
        this(pbuffer, PersistentCollections.getSerializer(type));
    }

    /**
     * Constructs a list backed by the provided persistent buffer.
     * Operates in linear time in order to cache the size.
     */
    public PersistentLinkedList(PersistentBuffer pbuffer, Serializer<E> serializer) throws IOException {
        this.serializer = serializer;
        blockBuffer = PersistentCollections.getPersistentBlockBuffer(
            serializer,
            pbuffer,
            Math.max(HEADER_SIZE, DATA_OFFSET)
        );
        // Get the meta data block
        Iterator<Long> ids = blockBuffer.iterateBlockIds();
        if(ids.hasNext()) {
            metaDataBlockId = ids.next();
            blockBuffer.get(metaDataBlockId, 0, ioBuffer, 0, MAGIC.length);
            if(!PersistentCollections.equals(ioBuffer, MAGIC, 0, MAGIC.length)) throw new IOException("File does not appear to be a PersistentLinkedList (MAGIC mismatch)");
            int version = blockBuffer.getInt(metaDataBlockId, MAGIC.length);
            if(version!=VERSION) throw new IOException("Unsupported file version: "+version);
            _head = blockBuffer.getLong(metaDataBlockId, HEAD_OFFSET);
            _tail = blockBuffer.getLong(metaDataBlockId, TAIL_OFFSET);
            assert _head==END_PTR || hasNonNullNextPrev(_head);
            assert _tail==END_PTR || hasNonNullNextPrev(_tail);
            long count = 0;
            while(ids.hasNext()) {
                long id = ids.next();
                // TODO: Perform crash recovery here
                count++;
            }
            _size = count;
        } else {
            clear();
        }
    }
    // </editor-fold>

    // <editor-fold desc="Pointer Assertions">
    /**
     * Checks that the ptr is in the valid address range.  It must be >=0 and
     * not the metadata block pointer.
     */
    private boolean isValidRange(long ptr) throws IOException {
        return ptr>=0 && ptr!=metaDataBlockId;
    }

    /**
     * Checks if the entry has both next and previous pointers, also asserts isValidRange first.
     */
    private boolean hasNonNullNextPrev(long ptr) throws IOException {
        assert isValidRange(ptr) : "Invalid range: "+ptr;
        return
            blockBuffer.getLong(ptr, NEXT_OFFSET)!=NULL_PTR
            && blockBuffer.getLong(ptr, PREV_OFFSET)!=NULL_PTR
        ;
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
        assert head==END_PTR || hasNonNullNextPrev(head);
        blockBuffer.putLong(metaDataBlockId, HEAD_OFFSET, head);
        this._head = head;
    }

    private long getTail() {
        return _tail;
    }

    /**
     * Sets the tail to the provided value.
     */
    private void setTail(long tail) throws IOException {
        assert tail==END_PTR || hasNonNullNextPrev(tail);
        blockBuffer.putLong(metaDataBlockId, TAIL_OFFSET, tail);
        this._tail = tail;
    }

    /**
     * Gets the next pointer for the entry at the provided location.
     * The entry must have non-null next and prev.
     */
    private long getNext(long ptr) throws IOException {
        assert hasNonNullNextPrev(ptr);
        return blockBuffer.getLong(ptr, NEXT_OFFSET);
    }

    /**
     * Sets the next pointer for the entry at the provided location.
     * The entry must have non-null next and prev.
     */
    private void setNext(long ptr, long next) throws IOException {
        assert hasNonNullNextPrev(ptr);
        assert next==END_PTR || hasNonNullNextPrev(next);
        blockBuffer.putLong(ptr, NEXT_OFFSET, next);
    }

    /**
     * Gets the prev pointer for the entry at the provided location.
     * The entry must have non-null next and prev.
     */
    private long getPrev(long ptr) throws IOException {
        assert hasNonNullNextPrev(ptr);
        return blockBuffer.getLong(ptr, PREV_OFFSET);
    }

    /**
     * Sets the prev pointer for the entry at the provided location.
     * The entry must have non-null next and prev.
     */
    private void setPrev(long ptr, long prev) throws IOException {
        assert hasNonNullNextPrev(ptr);
        assert prev==END_PTR || hasNonNullNextPrev(prev);
        blockBuffer.putLong(ptr, PREV_OFFSET, prev);
    }

    /**
     * Gets the size of the data for the entry at the provided location.
     * This does not include the block header.
     * The entry must have non-null next and prev.
     */
    private long getDataSize(long ptr) throws IOException {
        assert hasNonNullNextPrev(ptr);
        return blockBuffer.getLong(ptr, DATA_SIZE_OFFSET);
    }

    /**
     * Checks if the provided element is null.
     * The entry must have non-null next and prev.
     */
    private boolean isNull(long ptr) throws IOException {
        return getDataSize(ptr)==DATA_SIZE_NULL;
    }

    /**
     * Gets the element for the entry at the provided location.
     * The entry must have non-null next and prev.
     */
    private E getElement(long ptr) throws IOException {
        assert hasNonNullNextPrev(ptr);
        long dataSize = getDataSize(ptr);
        if(dataSize==DATA_SIZE_NULL) return null;

        InputStream in = blockBuffer.getInputStream(ptr, DATA_OFFSET, dataSize);
        try {
            // Read the object
            return serializer.deserialize(in);
        } finally {
            in.close();
        }
    }
    // </editor-fold>

    // <editor-fold desc="Data Structure Management">
    private void barrier(boolean force) throws IOException {
        blockBuffer.barrier(force);
    }

    /**
     * Removes the entry at the provided location and restores it to an unallocated state.
     * Operates in constant time.
     * The entry must have non-null next and prev.
     */
    private void remove(long ptr) throws IOException {
        assert hasNonNullNextPrev(ptr);
        assert _size>0;
        long prev = getPrev(ptr);
        long next = getNext(ptr);
        if(prev==END_PTR) setHead(next);
        else setNext(prev, next);
        if(next==END_PTR) setTail(prev);
        else setPrev(next, prev);
        // Barrier, to make sure always pointing to complete data
        barrier(false);
        blockBuffer.putLong(ptr, NEXT_OFFSET, NULL_PTR);
        blockBuffer.putLong(ptr, PREV_OFFSET, NULL_PTR);
        blockBuffer.deallocate(ptr);
        // Barrier, to make sure links are correct
        barrier(true);
        _size--;
    }

    // TODO: Use direct streams
    private BetterByteArrayOutputStream bufferOut;

    /**
     * Adds an entry.  Allocates, writes the header and data, barrier, link-in, barrier, _size++
     * If the serializer is fixed size, will preallocate and serialize directly
     * the the block.  Otherwise, it serializes to a buffer, and then allocates the
     * appropriate amount of space.
     * Operates in constant time.
     */
    private long addEntry(long next, long prev, E element) throws IOException {
        assert next==END_PTR || hasNonNullNextPrev(next);
        assert prev==END_PTR || hasNonNullNextPrev(prev);
        if(_size==Long.MAX_VALUE) throw new IOException("List is full: _size==Long.MAX_VALUE");

        // Allocate and write new entry
        long dataSize;
        long newPtr;
        if(element==null) {
            dataSize = DATA_SIZE_NULL;
            newPtr = blockBuffer.allocate(0);
            PersistentCollections.longToBuffer(next, ioBuffer, NEXT_OFFSET);
            PersistentCollections.longToBuffer(prev, ioBuffer, PREV_OFFSET);
            PersistentCollections.longToBuffer(DATA_SIZE_NULL, ioBuffer, DATA_SIZE_OFFSET);
            blockBuffer.put(newPtr, 0, ioBuffer, 0, DATA_OFFSET);
        } else if(serializer.isFixedSerializedSize()) {
            dataSize = serializer.getSerializedSize(null);
            newPtr = blockBuffer.allocate(dataSize);
            PersistentCollections.longToBuffer(next, ioBuffer, NEXT_OFFSET);
            PersistentCollections.longToBuffer(prev, ioBuffer, PREV_OFFSET);
            PersistentCollections.longToBuffer(dataSize, ioBuffer, DATA_SIZE_OFFSET);
            blockBuffer.put(newPtr, 0, ioBuffer, 0, DATA_OFFSET);
            OutputStream out = blockBuffer.getOutputStream(newPtr, DATA_OFFSET, dataSize);
            try {
                serializer.serialize(element, out);
            } finally {
                out.close();
            }
        } else {
            dataSize = serializer.getSerializedSize(element);
            if(bufferOut==null) bufferOut = new BetterByteArrayOutputStream(dataSize>Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)dataSize);
            else bufferOut.reset();
            serializer.serialize(element, bufferOut);
            if(bufferOut.size()!=dataSize) throw new AssertionError("bufferSize!=dataSize");
            byte[] data = bufferOut.getInternalByteArray();
            newPtr = blockBuffer.allocate(data.length);
            PersistentCollections.longToBuffer(next, ioBuffer, NEXT_OFFSET);
            PersistentCollections.longToBuffer(prev, ioBuffer, PREV_OFFSET);
            PersistentCollections.longToBuffer(data.length, ioBuffer, DATA_SIZE_OFFSET);
            blockBuffer.put(newPtr, 0, ioBuffer, 0, DATA_OFFSET);
            blockBuffer.put(newPtr, DATA_OFFSET, data, 0, data.length);
        }
        // Barrier, to make sure always pointing to complete data
        barrier(false);
        // Update pointers
        if(prev==END_PTR) {
            assert _head==next;
            setHead(newPtr);
        } else {
            assert getNext(prev)==next;
            setNext(prev, newPtr);
        }
        if(next==END_PTR) {
            assert _tail==prev;
            setTail(newPtr);
        } else {
            assert getPrev(next)==prev;
            setPrev(next, newPtr);
        }
        // Barrier, to make sure links are correct
        barrier(true);
        // Increment size
        _size++;
        return newPtr;
    }

    /**
     * Adds the first entry to the list.
     */
    private void addFirstEntry(final E element) throws IOException {
        assert getHead()==END_PTR;
        assert getTail()==END_PTR;
        addEntry(END_PTR, END_PTR, element);
    }

    /**
     * Adds the provided element before the element at the provided location.
     */
    private void addBefore(final E element, final long ptr) throws IOException {
        assert hasNonNullNextPrev(ptr);
        long prev = getPrev(ptr);
        addEntry(ptr, prev, element);
    }

    /**
     * Adds the provided element after the element at the provided location.
     */
    private void addAfter(final E element, final long ptr) throws IOException {
        assert hasNonNullNextPrev(ptr);
        long next = getNext(ptr);
        addEntry(next, ptr, element);
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
        if(head==END_PTR) throw new NoSuchElementException();
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
        if(tail==END_PTR) throw new NoSuchElementException();
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
        if(head==END_PTR) throw new NoSuchElementException();
        try {
            modCount++;
            E element = getElement(head);
            remove(head);
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
        if(tail==END_PTR) throw new NoSuchElementException();
        try {
            modCount++;
            E element = getElement(tail);
            remove(tail);
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
            if(head==END_PTR) addFirstEntry(element);
            else addBefore(element, head);
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
            if(tail==END_PTR) addFirstEntry(element);
            else addAfter(element, tail);
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
        if(index<(_size >> 1)) {
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
                for(long ptr = getHead(); ptr!=END_PTR; ptr = getNext(ptr)) {
                    if(isNull(ptr)) {
                        modCount++;
                        remove(ptr);
                        return true;
                    }
                }
            } else {
                for(long ptr = getHead(); ptr!=END_PTR; ptr = getNext(ptr)) {
                    if(o.equals(getElement(ptr))) {
                        modCount++;
                        remove(ptr);
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
            for(E element : c) addBefore(element, ptr);
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
        return _size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)_size;
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
            Iterator<Long> ids = blockBuffer.iterateBlockIds();
            if(ids.hasNext()) {
                metaDataBlockId = ids.next();
                setHead(END_PTR);
                setTail(END_PTR);
                barrier(false);
                // Deallocate all except first block
                while(ids.hasNext()) {
                    ids.next();
                    ids.remove();
                }
                barrier(true);
            } else {
                metaDataBlockId = blockBuffer.allocate(HEADER_SIZE);
                blockBuffer.put(metaDataBlockId, 0, MAGIC, 0, MAGIC.length);
                blockBuffer.putInt(metaDataBlockId, MAGIC.length, VERSION);
                setHead(END_PTR);
                setTail(END_PTR);
                barrier(true);
            }
            _size = 0;
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
            if(prev==END_PTR) addFirst(element);
            else addAfter(element, prev);
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
            if(prev==END_PTR) addFirst(element);
            else addAfter(element, prev);
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
                for(long ptr = getHead(); ptr!=END_PTR; ptr = getNext(ptr)) {
                    if(isNull(ptr)) return index;
                    index++;
                }
            } else {
                for(long ptr = getHead(); ptr!=END_PTR; ptr = getNext(ptr)) {
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
            long index = _size;
            if(o==null) {
                for(long ptr = getTail(); ptr!=END_PTR; ptr = getPrev(ptr)) {
                    --index;
                    if(isNull(ptr)) {
                        if(index>Integer.MAX_VALUE) throw new RuntimeException("Index too high to return from lastIndexOf: "+index);
                        return (int)index;
                    }
                }
            } else {
                for(long ptr = getTail(); ptr!=END_PTR; ptr = getPrev(ptr)) {
                    --index;
                    if(o.equals(getElement(ptr))) {
                        if(index>Integer.MAX_VALUE) throw new RuntimeException("Index too high to return from lastIndexOf: "+index);
                        return (int)index;
                    }
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
                for(long ptr = getTail(); ptr!=END_PTR; ptr = getPrev(ptr)) {
                    if(isNull(ptr)) {
                        modCount++;
                        remove(ptr);
                        return true;
                    }
                }
            } else {
                for(long ptr = getTail(); ptr!=END_PTR; ptr = getPrev(ptr)) {
                    if(o.equals(getElement(ptr))) {
                        modCount++;
                        remove(ptr);
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
        private long lastReturned = END_PTR;
        private long nextPtr;
        private long nextIndex;
        private int expectedModCount = modCount;

        ListItr(long index) {
            if (index < 0 || index > _size)
            throw new IndexOutOfBoundsException("Index: "+index+
                                ", Size: "+_size);
            try {
                if (index < (_size >> 1)) {
                    nextPtr = getHead();
                    for (nextIndex=0; nextIndex<index; nextIndex++) nextPtr = getNext(nextPtr);
                } else {
                    nextPtr = getTail();
                    if(nextPtr==END_PTR) {
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
            if(nextIndex>Integer.MAX_VALUE) throw new RuntimeException("Index too high to return from nextIndex: "+nextIndex);
            return (int)nextIndex;
        }

        public int previousIndex() {
            long prevIndex = nextIndex-1;
            if(prevIndex>Integer.MAX_VALUE) throw new RuntimeException("Index too high to return from previousIndex: "+prevIndex);
            return (int)prevIndex;
        }

        public void remove() {
            checkForComodification();
            try {
                long lastNext = getNext(lastReturned);
                try {
                    PersistentLinkedList.this.remove(lastReturned);
                } catch (NoSuchElementException e) {
                    throw new IllegalStateException();
                }
                if(nextPtr==lastReturned) nextPtr = lastNext;
                else nextIndex--;
                lastReturned = END_PTR;
                expectedModCount++;
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }

        public void set(E e) {
            if (lastReturned == END_PTR)
            throw new IllegalStateException();
            checkForComodification();
            setElement(lastReturned, e);
        }

        public void add(E e) {
            checkForComodification();
            try {
                lastReturned = END_PTR;
                modCount++;
                addBefore(e, nextPtr);
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
            if(_size>Integer.MAX_VALUE) throw new RuntimeException("Too many elements in list to create Object[]: "+_size);
            Object[] result = new Object[(int)_size];
            int i = 0;
            for (long ptr = getHead(); ptr != END_PTR; ptr = getNext(ptr)) result[i++] = getElement(ptr);
            return result;
        } catch(IOException err) {
            throw new WrappedException(err);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if(_size>Integer.MAX_VALUE) throw new RuntimeException("Too many elements in list to fill or create array: "+_size);
        try {
            if (a.length < _size)
                a = (T[])java.lang.reflect.Array.newInstance(
                                    a.getClass().getComponentType(), (int)_size);
            int i = 0;
            Object[] result = a;
            for (long ptr = getHead(); ptr != END_PTR; ptr = getNext(ptr)) result[i++] = getElement(ptr);
            if (a.length > _size)
            a[(int)_size] = null;

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
        blockBuffer.close();
    }
}
