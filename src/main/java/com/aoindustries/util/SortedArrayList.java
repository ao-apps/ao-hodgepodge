/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2020  AO Industries, Inc.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A <code>SortedArrayList</code> stores its elements in hashCode order and provides means of quickly
 * locating objects.
 *
 * @author  AO Industries, Inc.
 */
public class SortedArrayList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param   initialCapacity   the initial capacity of the list.
	 * @exception IllegalArgumentException if the specified initial capacity
	 *            is negative
	 */
	public SortedArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public SortedArrayList() {
		super();
	}

	/**
	 * Performs a binary search on hashCode values only.
	 * It will return any matching element, not necessarily
	 * the first or the last.
	 */
	protected int binarySearchHashCode(int elemHash) {
		int left = 0;
		int right = size()-1;
		while(left <= right) {
			int mid = (left + right)>>1;
			int midHash = get(mid).hashCode();
			if(elemHash==midHash) return mid;
			if(elemHash<midHash) right = mid-1;
			else left = mid+1;
		}
		return -(left+1);
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
		int elemHash=elem.hashCode();

		// Find the location to insert the object at
		int elemHashPos=binarySearchHashCode(elemHash);

		// Not found
		if(elemHashPos<0) return -1;

		// Try backwards until found or different hashCode
		int pos=elemHashPos;
		while(pos>=0) {
			E T=get(pos);
			if(T.hashCode()!=elemHash) break;
			if(T.equals(elem)) {
				// Found one, iterate backwards to the first one
				while(pos>0 && get(pos-1).equals(elem)) pos--;
				return pos;
			}
			pos--;
		}

		// Try forwards until found or different hashCode
		pos=elemHashPos+1;
		int size=size();
		while(pos<size) {
			E T=get(pos);
			if(T.hashCode()!=elemHash) break;
			if(T.equals(elem)) return pos;
			pos++;
		}

		// Not found
		return -1;
	}

	/**
	 * Finds the first index where the object has the provided hashCode
	 */
	public int indexOf(int hashCode) {
		int pos=binarySearchHashCode(hashCode);
		if(pos<0) return -1;

		// Try backwards until different hashCode
		while(pos>0 && get(pos-1).hashCode()==hashCode) pos--;
		return pos;
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
		int elemHash=elem.hashCode();

		// Find the location to insert the object at
		int elemHashPos=binarySearchHashCode(elemHash);

		// Not found
		if(elemHashPos<0) return -1;

		// Try forwards until found or different hashCode
		int pos=elemHashPos;
		int size=size();
		while(pos<size) {
			E T=get(pos);
			if(T.hashCode()!=elemHash) break;
			if(T.equals(elem)) {
				// Found one, iterate backwards to the first one
				while(pos<(size-1) && get(pos+1).equals(elem)) pos++;
				return pos;
			}
			pos++;
		}

		// Try backwards until found or different hashCode
		pos=elemHashPos-1;
		while(pos>=0) {
			E T=get(pos);
			if(T.hashCode()!=elemHash) break;
			if(T.equals(elem)) return pos;
			pos--;
		}

		// Not found
		return -1;
	}

	/**
	 * Not allowed to set specific indexes.
	 */
	@Override
	public E set(int index, E element) {
		throw new RuntimeException("Not allowed to set specific indexes");
	}

	/**
	 * Adds the specified element in sorted position within this list.  When
	 * two elements have the same hashCode, the new item is added at the end
	 * of the list of matching hashCodes.
	 *
	 * @param o element to be appended to this list.
	 * @return {@code true} (as per the general contract of Collection.add).
	 */
	@Override
	public boolean add(E o) {
		// Shortcut for empty
		int size=size();
		if(size==0) {
			super.add(o);
		} else {
			int Ohash=o.hashCode();

			// Shortcut for adding to end (makes imports of already-sorted data operate at constant-time instead of logarithmic complexity)
			if(Ohash>=get(size-1).hashCode()) {
				super.add(o);
			} else {
				int index=binarySearchHashCode(Ohash);
				if(index<0) {
					// Not found in list
					super.add(-(index+1), o);
				} else {
					// Add after the last item with matching hashCodes
					while(index<(size-1) && get(index+1).hashCode()==Ohash) index++;
					super.add(index+1, o);
				}
			}
		}

		return true;
	}

	/**
	 * Not allowed to add to specific indexes.
	 */
	@Override
	public void add(int index, E element) {
		throw new RuntimeException("Not allowed to add to specific indexes");
	}

	/**
	 * Removes a single instance of the specified element from this
	 * list, if it is present (optional operation).
	 * If the list contains one or more such
	 * elements.  Returns {@code true} if the list contained the
	 * specified element (or equivalently, if the list changed as a
	 * result of the call).<p>
	 *
	 * @param o element to be removed from this list, if present.
	 * @return {@code true} if the list contained the specified element.
	 */
	@Override
	public boolean remove(Object o) {
		int index=lastIndexOf(o);
		if(index==-1) return false;
		remove(index);
		return true;
	}

	/**
	 * Adds all of the elements in the specified Collection and sorts during
	 * the add.  This may operate slowly as it is the same as individual
	 * calls to the add method.
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		Iterator<? extends E>iter=c.iterator();
		boolean didOne=false;
		while(iter.hasNext()) {
			add(iter.next());
			didOne=true;
		}
		return didOne;
	}

	/**
	 * Not allowed to add to a specific index.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new RuntimeException("Not allowed to add to a specific index");
	}
}
