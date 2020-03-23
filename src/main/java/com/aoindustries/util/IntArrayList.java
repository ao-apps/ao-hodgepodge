/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2020  AO Industries, Inc.
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
package com.aoindustries.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * An ArrayList that stores things using <code>int[]</code> instead of <code>Object[]</code>.  null values are not supported.
 *
 * @see  java.util.ArrayList
 */
public class IntArrayList extends AbstractList<Integer>
		implements IntList, RandomAccess, Cloneable, java.io.Serializable
{
	private static final long serialVersionUID = -1988646061548931562L;

	/**
	 * The array buffer into which the elements of the ArrayList are stored.
	 * The capacity of the ArrayList is the length of this array buffer.
	 */
	protected transient int[] elementData;

	/**
	 * The size of the ArrayList (the number of elements it contains).
	 *
	 * @serial
	 */
	protected int size;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param   initialCapacity   the initial capacity of the list.
	 * @exception IllegalArgumentException if the specified initial capacity
	 *            is negative
	 */
	public IntArrayList(int initialCapacity) {
		super();
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "+
											   initialCapacity);
		this.elementData = new int[initialCapacity];
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public IntArrayList() {
		this(10);
	}

	/**
	 * Constructs a list containing the elements of the specified
	 * collection, in the order they are returned by the collection's
	 * iterator.  The {@link IntArrayList} instance has an initial capacity of
	 * 110% the size of the specified collection.
	 *
	 * @param c the collection whose elements are to be placed into this list.
	 * @throws NullPointerException if the specified collection is null.
	 */
	public IntArrayList(Collection<? extends Number> c) {
		size = c.size();
		// Allow 10% room for growth
		elementData = new int[
					  (int)Math.min((size*110L)/100,Integer.MAX_VALUE)];
		Iterator<? extends Number> iter = c.iterator();
		int pos=0;
		while(iter.hasNext()) elementData[pos++]=iter.next().intValue();
	}

	public IntArrayList(int[] elements) {
		size = elements.length;
		// Allow 10% room for growth
		elementData = new int[
					  (int)Math.min((size*110L)/100,Integer.MAX_VALUE)];
		System.arraycopy(elements, 0, elementData, 0, size);
	}

	/**
	 * Trims the capacity of this {@link IntArrayList} instance to be the
	 * list's current size.  An application can use this operation to minimize
	 * the storage of an {@link IntArrayList} instance.
	 */
	public void trimToSize() {
		modCount++;
		int oldCapacity = elementData.length;
		if (size < oldCapacity) {
			int oldData[] = elementData;
			elementData = new int[size];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Increases the capacity of this {@link IntArrayList} instance, if
	 * necessary, to ensure  that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.
	 */
	public void ensureCapacity(int minCapacity) {
		modCount++;
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			int oldData[] = elementData;
			int newCapacity = (oldCapacity * 3)/2 + 1;
				if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = new int[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return  the number of elements in this list.
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Tests if this list has no elements.
	 *
	 * @return  {@code true} if this list has no elements;
	 *          {@code false} otherwise.
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns {@code true} if this list contains the specified element.
	 *
	 * @param elem element whose presence in this List is to be tested.
	 * @return  <code>true</code> if the specified element is present;
	 *                <code>false</code> otherwise.
	 */
	@Override
	public boolean contains(Object elem) {
		if (elem == null) return false;
		if (elem instanceof Number) return contains(((Number)elem).intValue());
		return false;
	}

	/**
	 * Returns {@code true} if this list contains the specified element.
	 *
	 * @param elem element whose presence in this List is to be tested.
	 * @return  <code>true</code> if the specified element is present;
	 *                <code>false</code> otherwise.
	 */
	@Override
	public boolean contains(int elem) {
		return indexOf(elem) >= 0;
	}

	/**
	 * Searches for the first occurrence of the given argument, testing
	 * for equality using the {@link #equals(java.lang.Object) equals} method.
	 *
	 * @param   elem   an object.
	 * @return  the index of the first occurrence of the argument in this
	 *          list; returns {@code -1} if the object is not found.
	 * @see     Object#equals(Object)
	 */
	@Override
	public int indexOf(Object elem) {
		if (elem == null) return -1;
		if (elem instanceof Number) return indexOf(((Number)elem).intValue());
		return -1;
	}

	/**
	 * Searches for the first occurrence of the given argument, testing
	 * for equality using the {@link #equals(java.lang.Object) equals} method.
	 *
	 * @param   elem   an object.
	 * @return  the index of the first occurrence of the argument in this
	 *          list; returns {@code -1} if the object is not found.
	 * @see     Object#equals(Object)
	 */
	@Override
	public int indexOf(int elem) {
		for (int i = 0; i < size; i++)
			if (elem==elementData[i])
				return i;
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in
	 * this list.
	 *
	 * @param   elem   the desired element.
	 * @return  the index of the last occurrence of the specified object in
	 *          this list; returns -1 if the object is not found.
	 */
	@Override
	public int lastIndexOf(Object elem) {
		if (elem == null) return -1;
		if (elem instanceof Number) return lastIndexOf(((Number)elem).intValue());
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in
	 * this list.
	 *
	 * @param   elem   the desired element.
	 * @return  the index of the last occurrence of the specified object in
	 *          this list; returns -1 if the object is not found.
	 */
	@Override
	public int lastIndexOf(int elem) {
		for (int i = size-1; i >= 0; i--)
			if (elem==elementData[i])
				return i;
		return -1;
	}

	/**
	 * Returns a shallow copy of this {@link IntArrayList} instance.  (The
	 * elements themselves are not copied.)
	 *
	 * @return  a clone of this {@link IntArrayList} instance.
	 */
	@Override
	public Object clone() {
		try {
			IntArrayList v = (IntArrayList) super.clone();
			v.elementData = new int[size];
			System.arraycopy(elementData, 0, v.elementData, 0, size);
			v.modCount = 0;
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Returns an array containing all of the elements in this list
	 * in the correct order.
	 *
	 * @return an array containing all of the elements in this list
	 *                in the correct order.
	 */
	@Override
	public Object[] toArray() {
		Object[] result = new Object[size];
		for(int c=0;c<size;c++) result[c] = elementData[c];
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list
	 * in the correct order.
	 *
	 * @return an array containing all of the elements in this list
	 *                in the correct order.
	 */
	@Override
	public int[] toArrayInt() {
		int[] result = new int[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in the
	 * correct order; the runtime type of the returned array is that of the
	 * specified array.  If the list fits in the specified array, it is
	 * returned therein.  Otherwise, a new array is allocated with the runtime
	 * type of the specified array and the size of this list.<p>
	 *
	 * If the list fits in the specified array with room to spare (i.e., the
	 * array has more elements than the list), the element in the array
	 * immediately following the end of the collection is set to
	 * {@code null}.  This is useful in determining the length of the list
	 * <i>only</i> if the caller knows that the list does not contain any
	 * {@code null} elements.
	 *
	 * @param a the array into which the elements of the list are to
	 *                be stored, if it is big enough; otherwise, a new array of the
	 *                 same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list.
	 * @throws ArrayStoreException if the runtime type of a is not a supertype
	 *         of the runtime type of every element in this list.
	 */
	@SuppressWarnings({"unchecked"})
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = (T[])java.lang.reflect.Array.
				newInstance(a.getClass().getComponentType(), size);
		System.arraycopy(elementData, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param  index index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws    IndexOutOfBoundsException if index is out of range {@code (index < 0 || index >= size())}.
	 */
	@Override
	public Integer get(int index) {
		RangeCheck(index);

		return elementData[index];
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param  index index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws    IndexOutOfBoundsException if index is out of range {@code (index < 0 || index >= size())}.
	 */
	@Override
	public int getInt(int index) {
		RangeCheck(index);

		return elementData[index];
	}

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 *
	 * @param index index of element to replace.
	 * @param element element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws    IndexOutOfBoundsException if index out of range
	 *                  {@code (index < 0 || index >= size())}.
	 */
	@Override
	public Integer set(int index, Integer element) {
		return set(index,element.intValue());
	}

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 *
	 * @param index index of element to replace.
	 * @param element element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws    IndexOutOfBoundsException if index out of range
	 *                  {@code (index < 0 || index >= size())}.
	 */
	@Override
	public int set(int index, int element) {
		RangeCheck(index);

		int oldValue = elementData[index];
		elementData[index] = element;
		return oldValue;
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param o element to be appended to this list.
	 * @return {@code true} (as per the general contract of Collection.add).
	 */
	@Override
	public boolean add(Integer o) {
		return add(o.intValue());
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param o element to be appended to this list.
	 * @return {@code true} (as per the general contract of Collection.add).
	 */
	@Override
	public boolean add(int o) {
		ensureCapacity(size + 1);  // Increments modCount!!
		elementData[size++] = o;
		return true;
	}

	/**
	 * Inserts the specified element at the specified position in this
	 * list. Shifts the element currently at that position (if any) and
	 * any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted.
	 * @param element element to be inserted.
	 * @throws    IndexOutOfBoundsException if index is out of range
	 *                  {@code (index < 0 || index > size())}.
	 */
	@Override
	public void add(int index, Integer element) {
		add(index, element.intValue());
	}

	/**
	 * Inserts the specified element at the specified position in this
	 * list. Shifts the element currently at that position (if any) and
	 * any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted.
	 * @param element element to be inserted.
	 * @throws    IndexOutOfBoundsException if index is out of range
	 *                  {@code (index < 0 || index > size())}.
	 */
	@Override
	public void add(int index, int element) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException(
				"Index: "+index+", Size: "+size);

		ensureCapacity(size+1);  // Increments modCount!!
		System.arraycopy(elementData, index, elementData, index + 1,
						 size - index);
		elementData[index] = element;
		size++;
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts one from their
	 * indices).
	 *
	 * @param index the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws    IndexOutOfBoundsException if index out of range {@code(index < 0 || index >= size())}.
	 */
	@Override
	public Integer remove(int index) {
		return removeAtIndex(index);
	}

	/**
	 * Removes the element at the specified position in this list.
	 * Shifts any subsequent elements to the left (subtracts one from their
	 * indices).
	 *
	 * @param index the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws    IndexOutOfBoundsException if index out of range {@code (index < 0 || index >= size())}.
	 */
	@Override
	public int removeAtIndex(int index) {
		RangeCheck(index);

		modCount++;
		int oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index+1, elementData, index,
							 numMoved);
		elementData[--size] = 0; // Let gc do its work

		return oldValue;
	}

	/**
	 * Removes a single instance of the specified element from this
	 * list, if it is present (optional operation).  More formally,
	 * removes an element {@code e} such that {@code (o==null ? e==null : o.equals(e))},
	 * if the list contains one or more such
	 * elements.  Returns {@code true} if the list contained the
	 * specified element (or equivalently, if the list changed as a
	 * result of the call).<p>
	 *
	 * @param o element to be removed from this list, if present.
	 * @return {@code true} if the list contained the specified element.
	 */
	@Override
	public boolean remove(Object o) {
		if (o != null && (o instanceof Number)) return removeByValue(((Number)o).intValue());
		return false;
	}

	/**
	 * Removes a single instance of the specified element from this
	 * list, if it is present (optional operation).  More formally,
	 * removes an element {@code e} such that {@code (o==null ? e==null : o.equals(e))},
	 * if the list contains one or more such
	 * elements.  Returns {@code true} if the list contained the
	 * specified element (or equivalently, if the list changed as a
	 * result of the call).<p>
	 *
	 * @param value element to be removed from this list, if present.
	 * @return {@code true} if the list contained the specified element.
	 */
	@Override
	public boolean removeByValue(int value) {
		for (int index = 0; index < size; index++)
			if (value==elementData[index]) {
				fastRemove(index);
				return true;
			}
		return false;
	}

	/*
	 * Private remove method that skips bounds checking and does not
	 * return the value removed.
	 */
	private void fastRemove(int index) {
		modCount++;
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index+1, elementData, index,
							 numMoved);
		elementData[--size] = 0; // Let gc do its work
	}

	/**
	 * Removes all of the elements from this list.  The list will
	 * be empty after this call returns.
	 */
	@Override
	public void clear() {
		modCount++;

		// Let gc do its work
		for (int i = 0; i < size; i++)
			elementData[i] = 0;

		size = 0;
	}

	/**
	 * Appends all of the elements in the specified Collection to the end of
	 * this list, in the order that they are returned by the
	 * specified Collection's Iterator.  The behavior of this operation is
	 * undefined if the specified Collection is modified while the operation
	 * is in progress.  (This implies that the behavior of this call is
	 * undefined if the specified Collection is this list, and this
	 * list is nonempty.)
	 *
	 * @param c the elements to be inserted into this list.
	 * @return {@code true} if this list changed as a result of the call.
	 * @throws    NullPointerException if the specified collection is null.
	 */
	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		int numNew = c.size();
		ensureCapacity(size + numNew);  // Increments modCount
		Iterator<? extends Integer> iter=c.iterator();
		while(iter.hasNext()) elementData[size++]=iter.next();
		return numNew != 0;
	}

	/**
	 * Inserts all of the elements in the specified Collection into this
	 * list, starting at the specified position.  Shifts the element
	 * currently at that position (if any) and any subsequent elements to
	 * the right (increases their indices).  The new elements will appear
	 * in the list in the order that they are returned by the
	 * specified Collection's iterator.
	 *
	 * @param index index at which to insert first element
	 *                    from the specified collection.
	 * @param c elements to be inserted into this list.
	 * @return {@code true} if this list changed as a result of the call.
	 * @throws    IndexOutOfBoundsException if index out of range {@code (index < 0 || index > size())}.
	 * @throws    NullPointerException if the specified Collection is null.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends Integer> c) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException(
				"Index: " + index + ", Size: " + size);

		int numNew = c.size();
		ensureCapacity(size + numNew);  // Increments modCount

		int numMoved = size - index;
		if (numMoved > 0)
			System.arraycopy(elementData, index, elementData, index + numNew,
							 numMoved);

		Iterator<? extends Integer> iter=c.iterator();
		int pos = index;
		while(iter.hasNext()) elementData[pos++]=iter.next();
		size += numNew;
		return numNew != 0;
	}

	/**
	 * Removes from this List all of the elements whose index is between
	 * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
	 * elements to the left (reduces their index).
	 * This call shortens the list by {@code (toIndex - fromIndex)} elements.
	 * (If {@code toIndex==fromIndex}, this operation has no effect.)
	 *
	 * @param fromIndex index of first element to be removed.
	 * @param toIndex index after last element to be removed.
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		modCount++;
		int numMoved = size - toIndex;
		System.arraycopy(elementData, toIndex, elementData, fromIndex,
						 numMoved);

		// Let gc do its work
		int newSize = size - (toIndex-fromIndex);
		while (size != newSize)
			elementData[--size] = 0;
	}

	/**
	 * Check if the given index is in range.  If not, throw an appropriate
	 * runtime exception.  This method does *not* check if the index is
	 * negative: It is always used immediately prior to an array access,
	 * which throws an ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void RangeCheck(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException(
				"Index: "+index+", Size: "+size);
	}

	/**
	 * Save the state of the {@link IntArrayList} instance to a stream (that
	 * is, serialize it).
	 *
	 * @serialData The length of the array backing the {@link IntArrayList}
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an {@link Object}) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
		throws java.io.IOException{
		int expectedModCount = modCount;
		// Write out element count, and any hidden stuff
		s.defaultWriteObject();

		// Write out array length
		s.writeInt(elementData.length);

		// Write out all elements in the proper order.
		for (int i=0; i<size; i++)
			s.writeInt(elementData[i]);

		 if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Reconstitute the {@link IntArrayList} instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s)
		throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();

		// Read in array length and allocate array
		int arrayLength = s.readInt();
		int[] a = elementData = new int[arrayLength];

		// Read in all elements in the proper order.
		for (int i=0; i<size; i++)
			a[i] = s.readInt();
	}
}
