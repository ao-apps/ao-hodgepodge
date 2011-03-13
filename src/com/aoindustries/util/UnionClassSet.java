/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A union set that assumes objects of different classes are not equal.  It never
 * does any combining.
 *
 * The following assumptions are made:
 * <ol>
 *   <li>All elements of the added sets are of the same exact class</li>
 *   <li>Objects of different classes are not equal.</li>
 *   <li>No set will contain <code>null</code></li>
 *   <li>A set for a specific class may only be added once.</li>
 *   <li>Sets that have been added do not change after being added.</li>
 *   <li>Only sets will be added.</li>
 *   <li>Nothing will be removed (except clear is supported).</li>
 * </ol>
 *
 * @author  AO Industries, Inc.
 */
public class UnionClassSet<E> extends AbstractSet<E> {

    /**
     * May disable assertions more completely for benchmarking
     */
    private static final boolean ENABLE_ASSERTIONS = true;

    private int size = 0;

    /**
     * Will never contain any empty sets.
     */
    private final Map<Class<?>, Set<? extends E>> added = new LinkedHashMap<Class<?>,Set<? extends E>>();

    public UnionClassSet() {
    }

    public UnionClassSet(Collection<? extends E> c) {
    	addAll(c);
    }

    public UnionClassSet(Set<? extends E> set) {
    	addAll(set);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object o) {
        if(o==null) return false;
        Set<? extends E> set = added.get(o.getClass());
        return set==null ? false : set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<Set<? extends E>> setIter = added.values().iterator();
        return new Iterator<E>() {
            private Iterator<? extends E> valIter = null;

            @Override
            public boolean hasNext() {
                if(valIter==null) {
                    if(setIter.hasNext()) {
                        valIter = setIter.next().iterator();
                        return true; // The sets are never empty
                    } else {
                        return false;
                    }
                } else {
                    return true; // valIter is null as soon as not has next
                }
            }

            @Override
            public E next() {
                if(valIter==null) {
                    if(setIter.hasNext()) {
                        valIter = setIter.next().iterator();
                    } else {
                        throw new NoSuchElementException();
                    }
                }
                E val = valIter.next();
                if(!valIter.hasNext()) valIter = null;
                return val;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    /**
     * Must be a set.
     *
     * @see  #addAll(java.util.Set)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Collection<? extends E> c) {
        if(c.isEmpty()) return false;
        if(c instanceof Set) return addAll((Set<? extends E>)c);
        else throw new UnsupportedOperationException("May only add sets");
    }

    private static boolean allSameClass(Class<?> clazz, Iterator<?> iter) {
        while(iter.hasNext()) if(iter.next().getClass()!=clazz) return false;
        return true;
    }

    public boolean addAll(Set<? extends E> set) {
        int setSize = set.size();
        if(setSize==0) return false;
        Iterator<? extends E> iter = set.iterator();
        Class<?> clazz = iter.next().getClass();
        if(ENABLE_ASSERTIONS) assert allSameClass(clazz, iter) : "Not all objects are of the same exact class";
        if(added.containsKey(clazz)) throw new IllegalArgumentException("Set already added for class: "+clazz);
        size += setSize;
        added.put(clazz, set);
        return true;
    }

    @Override
    public void clear() {
        size = 0;
        added.clear();
    }
}
