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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>
 * An unmodifiable compact <code>Set</code> implementation that stores the elements in hashCode order.
 * The emphasis is to use as little heap space as possible - this is not a general-purpose
 * <code>Set</code> implementation.
 * </p>
 * <p>
 * This set does not support null values.
 * </p>
 * <p>
 * This set will generally operate at O(log n) due to binary search.  In general, it will
 * not be as fast as the O(1) behavior or HashSet.  Here we give up speed to save space.
 * </p>
 * <p>
 * This set is not thread safe.
 * </p>
 *
 * @see  HashCodeComparator to properly sort objects before adding to the set
 *
 * @author  AO Industries, Inc.
 */
public class UnmodifiableArraySet<E> implements Set<E>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * May more forcefully disable asserts for benchmarking.
     */
    private static final boolean ASSERTIONS_ENABLED = true;

    /**
     * The number of elements at which a linear search switches to a binary search.
     */
    private static final int BINARY_SEARCH_THRESHOLD = 22; // The point where the O(n) line and O(log n) curves intersect.

    // Used to find BINARY_SEARCH_THRESHOLD
    /*
    private static void test() {
        final int numSearches = 10000;
        final int searchPasses = 100;
        Integer[] searches = new Integer[numSearches];
        Random random = new Random();
        for(int size=0;size<=32;size++) {
            ArrayList<Integer> values = new ArrayList<Integer>(size);
            int range = 0;
            for(int i=0; i<size; i++) {
                values.add(range);
                range += 1 + random.nextInt(10);
            }
            UnmodifiableArraySet<Integer> set = new UnmodifiableArraySet<Integer>(values);
            for(int i=0;i<numSearches;i++) searches[i] = range==0 ? 0 : random.nextInt(range);
            long startTime = System.currentTimeMillis();
            for(int pass=0; pass<searchPasses; pass++) {
                for(int i=0;i<numSearches;i++) set.contains(searches[i]);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(size+":"+(endTime-startTime));
        }
    }

    public static void main(String[] args) {
        for(int c=0;c<10;c++) test();
    }
     //*/

    final E[] elements;

    private boolean assertInOrderAndUnique(E[] elements) {
        // Make sure all elements are in hashCode order and unique
        int size = elements.length;
        if(size>1) {
            E prev = elements[0];
            int prevHash = prev.hashCode();
            for(int index=1; index<size; index++) {
                E elem = elements[index];
                int elemHash = elem.hashCode();
                if(elemHash<prevHash) throw new AssertionError("elements not sorted by hashCode: "+elemHash+"<"+prevHash+": "+elem+"<"+prev);
                if(elemHash==prevHash) {
                    // Make sure not equal to prev
                    if(elem.equals(prev)) throw new AssertionError("Element not unique: "+elem);
                    // Look backward until different hashCode
                    for(int i=index-2; i>=0; i--) {
                        E morePrev = elements[i];
                        if(morePrev.hashCode()!=elemHash) break;
                        if(elem.equals(morePrev)) throw new AssertionError("Element not unique: "+elem);
                    }
                }
                prev = elem;
                prevHash = elemHash;
            }
        }
        return true;
    }

    /**
     * Uses the provided elements, which must already be sorted in hashCode order and unique.
     *
     * The sort order and uniqueness is only checked with assertions enabled.
     *
     * @see  HashCodeComparator to properly sort objects before adding to the set
     */
    public UnmodifiableArraySet(E... elements) {
        if(ASSERTIONS_ENABLED) assert assertInOrderAndUnique(elements);
        this.elements = elements;
    }

    /**
     * Uses the provided elements collection, which must already be sorted in hashCode order and unique.
     * A defensive copy is made.
     *
     * The sort order and uniqueness is only checked with assertions enabled.
     *
     * @see  HashCodeComparator to properly sort objects before adding to the set
     */
    @SuppressWarnings("unchecked")
    public UnmodifiableArraySet(Collection<E> elements) {
        this((E[])elements.toArray());
    }

    private int binarySearch(int oHash) {
        return binarySearch0(0, elements.length, oHash);
    }
    private int binarySearch0(int fromIndex, int toIndex, int oHash) {
        int low = fromIndex;
        int high = toIndex - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midHash = elements[mid].hashCode();
            if (midHash < oHash) low = mid + 1;
            else if (midHash > oHash) high = mid - 1;
            else return mid;
        }
        return -(low + 1);
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public boolean isEmpty() {
        return elements.length==0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        int size = elements.length;
        if(size==0 || o==null) return false;
        if(size<BINARY_SEARCH_THRESHOLD) {
            // Simple search
            for(int i=0;i<size;i++) if(elements[i].equals(o)) return true;
        } else {
            int index = binarySearch(o.hashCode());
            if(index<0) return false;
            // Matches at index?
            E elem = elements[index];
            if(elem.equals(o)) return true;
            // Look forward until different hashCode
            int oHash = o.hashCode();
            for(int i=index+1; i<size; i++) {
                elem = elements[i];
                if(elem.hashCode()!=oHash) break;
                if(elem.equals(o)) return true;
            }
            // Look backward until different hashCode
            for(int i=index-1; i>=0; i--) {
                elem = elements[i];
                if(elem.hashCode()!=oHash) break;
                if(elem.equals(o)) return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index<elements.length;
            }
            @Override
            public E next() {
                if(index<elements.length) return elements[index++];
                else throw new NoSuchElementException();
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, elements.length);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = elements.length;
        if (a.length < size) return (T[]) Arrays.copyOf(elements, size, a.getClass());
    	System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // Could do a nifty merge if the other collection is also an UnmodifiableArraySet
        for(Object o : c) if(!contains(o)) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
