/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013, 2016, 2019, 2020  AO Industries, Inc.
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
 *
 * @author  AO Industries, Inc.
 */
public class UnionSet<E> extends AbstractSet<E> {

	/**
	 * Any set added with fewer or equal to this many items will just be added to
	 * the internal combined set.
	 *
	 * TODO: This value is arbitrary.  Benchmark other values.
	 */
	private static final int MAXIMUM_COMBINE_SIZE = 10;

	private Set<E> combined;

	/**
	 * Will never contain any empty sets.
	 */
	private final List<Set<? extends E>> added = new ArrayList<>();

	public UnionSet() {
	}

	public UnionSet(Collection<? extends E> c) {
		combined = new HashSet<>(c.size()*4/3+1);
		addAll(c);
	}

	public UnionSet(Set<? extends E> set) {
		addAll(set);
	}

	private void combine() {
		if(!added.isEmpty()) {
			if(combined==null) {
				// Avoid rehash at the expense of possibly allocating more than needed when there are duplicates
				int totalSize = 0;
				for(Set<? extends E> set : added) totalSize += set.size();
				combined = new HashSet<>(totalSize*4/3+1);
			}
			for(Set<? extends E> set : added) combined.addAll(set);
			added.clear();
		}
	}

	/**
	 * Triggers combining.
	 */
	@Override
	public int size() {
		combine();
		return combined==null ? 0 : combined.size();
	}

	@Override
	public boolean isEmpty() {
		return (combined==null || combined.isEmpty()) && added.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if(combined!=null && combined.contains(o)) return true;
		for(Set<? extends E> set : added) if(set.contains(o)) return true;
		return false;
	}

	/**
	 * Triggers combining.
	 *
	 * TODO: Iterate without combining - benchmark speed versus complexity
	 */
	@Override
	public Iterator<E> iterator() {
		combine();
		if(combined==null) {
			Set<E> emptySet = java.util.Collections.emptySet();
			return emptySet.iterator();
		}
		return combined.iterator();
	}

	/**
	 * Triggers combining.
	 */
	@Override
	public Object[] toArray() {
		combine();
		if(combined==null) {
			Set<E> emptySet = java.util.Collections.emptySet();
			return emptySet.toArray();
		}
		return combined.toArray();
	}

	/**
	 * Triggers combining.
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		combine();
		if(combined==null) {
			Set<E> emptySet = java.util.Collections.emptySet();
			return emptySet.toArray(a);
		}
		return combined.toArray(a);
	}

	@Override
	public boolean add(E e) {
		if(combined==null) combined = new HashSet<>();
		return combined.add(e);
	}

	/**
	 * Triggers combining.
	 */
	@Override
	public boolean remove(Object o) {
		combine();
		return combined!=null && combined.remove(o);
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
		if(c.isEmpty()) return false;
		combine();
		if(combined==null) {
			combined = new HashSet<>(c);
			return true;
		} else {
			return combined.addAll(c);
		}
	}

	/**
	 * If the set has size &gt; MAXIMUM_COMBINE_SIZE, the set will be added to the
	 * <code>added</code> list, which may then be later combined only when needed.
	 * Because of this potentially delayed combining, any set added should not be
	 * subsequently altered.
	 */
	public void addAll(Set<? extends E> set) {
		if(set.size()<=MAXIMUM_COMBINE_SIZE) {
			if(combined==null) combined = new HashSet<>(set);
			else combined.addAll(set);
		} else {
			added.add(set);
		}
	}

	/**
	 * Triggers combining.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		combine();
		if(combined==null) combined = new HashSet<>();
		return combined.retainAll(c);
	}

	/**
	 * Triggers combining.
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		combine();
		if(combined==null) combined = new HashSet<>();
		return combined.removeAll(c);
	}

	@Override
	public void clear() {
		if(combined!=null) combined.clear();
		added.clear();
	}
}
