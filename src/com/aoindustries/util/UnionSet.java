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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * In order to efficiently provide a union of fewer, larger sets, this provides a
 * set view on top of other sets.  Any set that is added to this union set via
 * <code>addAll(Set)</code> must not be modified after being added.  For
 * performance purposes, defensive copying is not performed.
 * </p>
 * <pre>
 * DependecyTest:
 *   Before:
 *     testGetDependencies:   244.443 s
 *     testGetDependentObjects: 0.622 s
 *   After:
 *     testGetDependencies:    12.675 s
 *     testGetDependentObjects: 0.748 s
 *
 * @author  AO Industries, Inc.
 */
public class UnionSet<E> implements Set<E> {

    /**
     * Any set added with fewer or equal to this many items will just be added to
     * the internal combined set.
     *
     * This value is arbitrary.  Other implementations.
     */
    private static final int MAXIMUM_COMBINE_SIZE = 10;

    private final Set<E> combined;

    /**
     * Will never contain any empty sets.
     */
    private final List<Set<? extends E>> added = new ArrayList<Set<? extends E>>();

    public UnionSet() {
        combined = new HashSet<E>();
    }

    public UnionSet(Collection<? extends E> c) {
        combined = new HashSet<E>(c.size()*4/3+1);
    	addAll(c);
    }

    public UnionSet(Set<? extends E> set) {
        combined = new HashSet<E>(set.size()*4/3+1);
    	addAll(set);
    }

    private void combine() {
        for(Set<? extends E> set : added) combined.addAll(set);
        added.clear();
    }

    /**
     * Triggers combining.
     */
    @Override
    public int size() {
        combine();
        return combined.size();
    }

    @Override
    public boolean isEmpty() {
        return combined.isEmpty() && added.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if(combined.contains(o)) return true;
        for(Set<? extends E> set : added) if(set.contains(o)) return true;
        return false;
    }

    /**
     * Triggers combining.
     */
    @Override
    public Iterator<E> iterator() {
        combine();
        return combined.iterator();
    }

    /**
     * Triggers combining.
     */
    @Override
    public Object[] toArray() {
        combine();
        return combined.toArray();
    }

    /**
     * Triggers combining.
     */
    @Override
    public <T> T[] toArray(T[] a) {
        combine();
        return combined.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return combined.add(e);
    }

    /**
     * Triggers combining.
     */
    @Override
    public boolean remove(Object o) {
        combine();
        return combined.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o : c) if(!contains(o)) return false;
        return true;
    }

    /**
     * Triggers combining.
     *
     * @see  #addAll(java.util.Set)
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        combine();
        return combined.addAll(c);
    }

    /**
     * If the set has size > MAXIMUM_COMBINE_SIZE, the set will be added to the
     * <code>added</code> list, which may then be later combined only when needed.
     * Because of this potentially delayed combining, any set added should not be
     * subsequently altered.
     */
    public void addAll(Set<? extends E> set) {
        if(set.size()<=MAXIMUM_COMBINE_SIZE) combined.addAll(set);
        else added.add(set);
    }

    /**
     * Triggers combining.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        combine();
        return combined.retainAll(c);
    }

    /**
     * Triggers combining.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        combine();
        return combined.removeAll(c);
    }

    @Override
    public void clear() {
        combined.clear();
        added.clear();
    }
}
