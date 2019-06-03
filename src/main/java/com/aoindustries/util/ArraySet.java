/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013, 2016, 2019  AO Industries, Inc.
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
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
 * This set will generally operate at O(log n) due to binary search.  In general, it will
 * not be as fast as the O(1) behavior of HashSet.  Here we give up speed to save space.
 * </p>
 * <p>
 * This set is not thread safe.
 * </p>
 *
 * @see  HashCodeComparator to properly sort objects before adding to the set
 *
 * @author  AO Industries, Inc.
 */
public class ArraySet<E> extends AbstractSet<E> implements Serializable {

	private static final long serialVersionUID = 7396605502356021412L;

	/**
	 * May more forcefully disable asserts for benchmarking.
	 */
	private static final boolean ASSERTIONS_ENABLED = true;

	/**
	 * The number of elements at which a linear search switches to a binary search.
	 */
	private static final int BINARY_SEARCH_THRESHOLD = 13; // The point where the O(n) line and O(log n) curves intersect.

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
			ArraySet<Integer> set = new ArraySet<Integer>(values);
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

	private final ArrayList<E> elements;

	/**
	 * @deprecated  This is for convention only, please provide a previously sorted ArrayList for best performance.
	 */
	@Deprecated
	public ArraySet() {
		this.elements = new ArrayList<>();
	}

	/**
	 * @deprecated  This is for convention only, please provide a previously sorted ArrayList for best performance.
	 */
	@Deprecated
	public ArraySet(int initialCapacity) {
		this.elements = new ArrayList<>(initialCapacity);
	}

	/*
	@Complexity(
		best=GrowthFunction.LINEAR,
		bestConditions={GrowthCondition.GOOD_HASH_CODE},
		average=GrowthFunction.LINEAR,
		averageConditions={GrowthCondition.GOOD_HASH_CODE},
		worst=GrowthFunction.QUADRATIC
	)
	 */
	/**
	 * @deprecated  This is for convention only, please provide a previously sorted ArrayList for best performance.
	 */
	@Deprecated
	public ArraySet(Collection<? extends E> c) {
		this.elements = new ArrayList<>(c.size());
		addAll(c);
	}

	private boolean assertInOrderAndUnique(ArrayList<E> elements) {
		// Make sure all elements are in hashCode order and unique
		int size = elements.size();
		if(size>1) {
			E prev = elements.get(0);
			int prevHash = prev.hashCode();
			for(int index=1; index<size; index++) {
				E elem = elements.get(index);
				int elemHash = elem.hashCode();
				if(elemHash<prevHash) throw new AssertionError("elements not sorted by hashCode: "+elemHash+"<"+prevHash+": "+elem+"<"+prev);
				if(elemHash==prevHash) {
					// Make sure not equal to prev
					if(elem.equals(prev)) throw new AssertionError("Element not unique: "+elem);
					// Look backward until different hashCode
					for(int i=index-2; i>=0; i--) {
						E morePrev = elements.get(i);
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
	 * Uses the provided elements list without copying, which must already
	 * be sorted in hashCode order and unique.
	 *
	 * The sort order and uniqueness is only checked with assertions enabled.
	 *
	 * @see  HashCodeComparator to properly sort objects before adding to the set
	 */
	/*
	@Complexity(
		best=GrowthFunction.CONSTANT,
		average=GrowthFunction.CONSTANT,
		worst=GrowthFunction.CONSTANT
	)
	 */
	public ArraySet(ArrayList<E> elements) {
		if(ASSERTIONS_ENABLED) assert assertInOrderAndUnique(elements);
		this.elements = elements;
	}

	/*
	@Complexity(
		best=GrowthFunction.LOGARITHMIC,
		average=GrowthFunction.LOGARITHMIC,
		worst=GrowthFunction.LOGARITHMIC
	)
	 */
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
	/*
	@Complexity(
		best=GrowthFunction.LOGARITHMIC,
		bestConditions={GrowthCondition.GOOD_HASH_CODE},
		average=GrowthFunction.LOGARITHMIC,
		averageConditions={GrowthCondition.GOOD_HASH_CODE},
		worst=GrowthFunction.LINEAR
	)
	 */
	public boolean contains(Object o) {
		int size = elements.size();
		if(size==0 || o==null) return false;
		if(size<BINARY_SEARCH_THRESHOLD) {
			// Simple search
			for(int i=0;i<size;i++) if(elements.get(i).equals(o)) return true;
		} else {
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
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return elements.iterator();
	}

	@Override
	/*
	@Complexity(
		best=GrowthFunction.LINEAR,
		average=GrowthFunction.LINEAR,
		worst=GrowthFunction.LINEAR
	)
	 */
	public Object[] toArray() {
		return elements.toArray();
	}

	@Override
	/*
	@Complexity(
		best=GrowthFunction.LINEAR,
		average=GrowthFunction.LINEAR,
		worst=GrowthFunction.LINEAR
	)*/
	public <T> T[] toArray(T[] a) {
		return elements.toArray(a);
	}

	@Override
	/*
	@Complexity(
		best=GrowthFunction.CONSTANT,
		bestConditions={GrowthCondition.GOOD_HASH_CODE},
		average=GrowthFunction.CONSTANT,
		averageConditions={GrowthCondition.GOOD_HASH_CODE},
		worst=GrowthFunction.LINEAR
	)*/
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
	/*
	@Complexity(
		best=GrowthFunction.CONSTANT,
		bestConditions={GrowthCondition.GOOD_HASH_CODE},
		average=GrowthFunction.CONSTANT,
		averageConditions={GrowthCondition.GOOD_HASH_CODE},
		worst=GrowthFunction.LINEAR
	)*/
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
	/*
	@Complexity(
		best=GrowthFunction.LINEAR,
		bestConditions={GrowthCondition.GOOD_HASH_CODE},
		average=GrowthFunction.LINEAR,
		averageConditions={GrowthCondition.GOOD_HASH_CODE},
		worst=GrowthFunction.QUADRATIC
	)*/
	public boolean containsAll(Collection<?> c) {
		for(Object o : c) if(!contains(o)) return false;
		return true;
	}

	@Override
	/*
	@Complexity(
		best=GrowthFunction.LINEAR,
		bestConditions={GrowthCondition.GOOD_HASH_CODE},
		average=GrowthFunction.LINEAR,
		averageConditions={GrowthCondition.GOOD_HASH_CODE},
		worst=GrowthFunction.QUADRATIC
	)*/
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
	/*
	@Complexity(
		best=GrowthFunction.LINEAR,
		bestConditions={GrowthCondition.GOOD_HASH_CODE},
		average=GrowthFunction.LINEAR,
		averageConditions={GrowthCondition.GOOD_HASH_CODE},
		worst=GrowthFunction.QUADRATIC
	)*/
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for(Object o : c) if(remove(o)) modified = true;
		return modified;
	}

	@Override
	/*
	@Complexity(
		best=GrowthFunction.LINEAR,
		average=GrowthFunction.LINEAR,
		worst=GrowthFunction.LINEAR
	)*/
	public void clear() {
		elements.clear();
	}
}
