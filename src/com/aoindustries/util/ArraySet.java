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
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * A compact <code>Set</code> implementation that stores the elements in hashCode order.
 * The emphasis is to use as little heap space as possible - this is not a general-purpose
 * <code>Set</code> implementation as it has specific constraints about the order elements
 * may be added or removed.  To avoid the possibility of O(n^2) behavior, the elements must
 * already be sorted and be added in ascending order.  Also, only the last element may be
 * removed.
 * </p>
 * <p>
 * This set does not support null values.
 * </p>
 * <p>
 * This set is not thread safe.
 * </p>
 *
 * @see  HashCodeComparator to properly sort objects before adding to the set
 *
 * @author  AO Industries, Inc.
 */
public class ArraySet<E> implements Set<E>, Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<E> elements;

    public ArraySet() {
        this.elements = new ArrayList<E>();
    }

    public ArraySet(int initialCapacity) {
        this.elements = new ArrayList<E>(initialCapacity);
    }

    public ArraySet(Collection<? extends E> c) {
        this.elements = new ArrayList<E>(c.size());
        addAll(c);
    }

    /**
     * Uses the provided elements list without copying, which must already
     * be sorted in hashCode order.
     *
     * @see  HashCodeComparator to properly sort objects before adding to the set
     */
    public ArraySet(ArrayList<E> elements) {
        this.elements = elements;
    }

    @SuppressWarnings("unchecked")
    private int binarySearch(E elem) {
        return java.util.Collections.binarySearch(elements, elem, HashCodeComparator.getInstance());
    }

    public void trimToSize() {
        elements.trimToSize();
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
        int size = elements.size();
        if(size==0) return false;
        int index = binarySearch((E)o);
        if(index<0) return false;
        // Matches at index?
        E elem = elements.get(index);
        if(elem.equals(o)) return true;
        // Look forward until different hashCode
        int oHash = o.hashCode();
        for(int i=index+1; i<size; i++) {
            elem = elements.get(i);
            if(elem.hashCode()!=oHash) break;
            if(elem.equals(o)) return true;
        }
        // Look backward until different hashCode
        for(int i=index-1; i>=0; i--) {
            elem = elements.get(i);
            if(elem.hashCode()!=oHash) break;
            if(elem.equals(o)) return true;
        }
        return false;
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
            // Shortcut for adding last element
            int eHash = e.hashCode();
            E last = elements.get(size-1);
            int lastHash = last.hashCode();
            if(eHash>lastHash) {
                elements.add(e);
                return true;
            } else if(eHash==lastHash) {
                if(last.equals(e)) {
                    // Already in set
                    return false;
                }
                // Look backward until different hashCode
                for(int i=size-2; i>=0; i--) {
                    E elem = elements.get(i);
                    if(elem.hashCode()!=eHash) break;
                    if(elem.equals(e)) {
                        // Already in set
                        return false;
                    }
                }
                elements.add(e);
                return true;
            } else {
                if(contains(e)) {
                    // Already in set
                    return false;
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
        // Shortcut for removing last element
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
