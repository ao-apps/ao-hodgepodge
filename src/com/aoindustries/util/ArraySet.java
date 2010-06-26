/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010  AO Industries, Inc.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;

/**
 * <p>
 * A compact <code>SortedSet</code> implementation that stores the elements in order.
 * The emphasis is to use as little heap space as possible - this is not a general-purpose
 * <code>Set</code> implementation as it has specific constraints about the order elements
 * may be added or removed.  To avoid the possibility of O(n^2) behavior, the elements must
 * already by sorted and be added in ascending order.  Also, only the last element may be
 * removed.
 * </p>
 * <p>
 * This set does not support null values.
 * </p>
 * <p>
 * This set is not thread safe.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class ArraySet<E> implements SortedSet<E>, Serializable {

    private static final long serialVersionUID = 1L;

    private final Comparator<? super E> comparator;
    private final ArrayList<E> elements;

    public ArraySet() {
        this.comparator = null;
        this.elements = new ArrayList<E>();
    }

    public ArraySet(int initialCapacity) {
        this.comparator = null;
        this.elements = new ArrayList<E>(initialCapacity);
    }

    public ArraySet(Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.elements = new ArrayList<E>();
    }

    public ArraySet(Comparator<? super E> comparator, int initialCapacity) {
        this.comparator = comparator;
        this.elements = new ArrayList<E>(initialCapacity);
    }

    public ArraySet(Collection<? extends E> c) {
        this.comparator = null;
        this.elements = new ArrayList<E>(c.size());
        addAll(c);
    }

    public ArraySet(SortedSet<E> s) {
        this.comparator = s.comparator();
        this.elements = new ArrayList<E>(s);
    }

    @SuppressWarnings("unchecked")
    private int binarySearch(E elem) {
        return
            comparator==null
            ? java.util.Collections.binarySearch((List)elements, elem)
            : java.util.Collections.binarySearch(elements, elem, comparator)
        ;
    }

    public void trimToSize() {
        elements.trimToSize();
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }

    @Override
    public E first() {
        if(elements.isEmpty()) throw new NoSuchElementException();
        return elements.get(0);
    }

    @Override
    public E last() {
        int size = elements.size();
        if(size==0) throw new NoSuchElementException();
        return elements.get(size-1);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        // TODO: How can we check if the passed-in object is of an unrelated, unexpected class
        // TODO: without passing around Class objects?
        return binarySearch((E)o)>=0;
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean add(E e) {
        int size = elements.size();
        if(size==0) {
            elements.add(e);
            return true;
        } else {
            int index = binarySearch(e);
            if(index>=0) {
                // Already in set
                return false;
            } else {
                index = -index - 1;
                // Must be last element to avoid O(n^2)
                if(index==size) {
                    elements.add(e);
                    return true;
                } else {
                    throw new UnsupportedOperationException("May only add the last element.");
                }
            }
        }
    }

    @Override
    public boolean remove(Object o) {
        int size = elements.size();
        if(size==0) return false;
        Object lastElem = elements.get(size-1);
        if(lastElem.equals(o)) {
            elements.remove(size-1);
            return true;
        } else {
            if(contains(o)) throw new UnsupportedOperationException("May only remove the last element.");
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o : c) if(!contains(o)) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for(E elem : c) if(add(elem)) modified = true;
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for(Object o : c) if(remove(o)) modified = true;
        return modified;
    }

    @Override
    public void clear() {
        elements.clear();
    }
}
