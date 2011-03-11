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
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * General-purpose collection utilities and constants.
 *
 * @author  AO Industries, Inc.
 */
public class AoCollections {

    private AoCollections() {
    }

    public static final SortedSet EMPTY_SORTED_SET = new EmptySortedSet();

    @SuppressWarnings("unchecked")
    public static final <T> SortedSet<T> emptySortedSet() {
        return (SortedSet<T>) EMPTY_SORTED_SET;
    }

    private static class EmptySortedSet extends AbstractSet<Object> implements SortedSet<Object>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
                @Override
                public Object next() {
                    throw new NoSuchElementException();
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public int size() {return 0;}

        @Override
        public boolean contains(Object obj) {
            return false;
        }

        private Object readResolve() {
            return EMPTY_SORTED_SET;
        }

        @Override
        public Comparator<? super Object> comparator() {
            return null;
        }

        @Override
        public SortedSet<Object> subSet(Object fromElement, Object toElement) {
            throw new IllegalArgumentException();
        }

        @Override
        public SortedSet<Object> headSet(Object toElement) {
            throw new IllegalArgumentException();
        }

        @Override
        public SortedSet<Object> tailSet(Object fromElement) {
            throw new IllegalArgumentException();
        }

        @Override
        public Object first() {
            throw new NoSuchElementException();
        }

        @Override
        public Object last() {
            throw new NoSuchElementException();
        }
    }

    public static <T> SortedSet<T> singletonSortedSet(T o) {
        return new SingletonSortedSet<T>(o);
    }

    private static class SingletonSortedSet<E> extends AbstractSet<E> implements SortedSet<E>, Serializable {

        private static final long serialVersionUID = 1L;

        final private E element;

        SingletonSortedSet(E e) {element = e;}

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private boolean hasNext = true;
                @Override
                public boolean hasNext() {
                    return hasNext;
                }
                @Override
                public E next() {
                    if (hasNext) {
                        hasNext = false;
                        return element;
                    }
                    throw new NoSuchElementException();
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean contains(Object o) {return StringUtility.equals(o, element);}

        @Override
        public Comparator<? super E> comparator() {
            return null;
        }

        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            if(StringUtility.equals(element, fromElement) && StringUtility.equals(element, toElement)) return emptySortedSet();
            throw new IllegalArgumentException();
        }

        @Override
        public SortedSet<E> headSet(E toElement) {
            if(StringUtility.equals(element, toElement)) return emptySortedSet();
            throw new IllegalArgumentException();
        }

        @Override
        public SortedSet<E> tailSet(E fromElement) {
            if(StringUtility.equals(element, fromElement)) return this;
            throw new IllegalArgumentException();
        }

        @Override
        public E first() {
            return element;
        }

        @Override
        public E last() {
            return element;
        }
    }

    /**
     * Gets the optimal implementation for unmodifiable collection.
     * If collection is empty, uses <code>Collections.emptyList</code>.
     * If collection has one element, uses <code>Collections.singletonList</code>.
     * Otherwise, wraps the collection with <code>Collections.unmodifiableCollection</code>.
     */
    public static <T> Collection<T> optimalUnmodifiableCollection(Collection<? extends T> collection) {
        int size = collection.size();
        if(size==0) return java.util.Collections.emptyList();
        if(size==1) return java.util.Collections.singletonList(collection.iterator().next());
        return java.util.Collections.unmodifiableCollection(collection);
    }

    /**
     * Gets the optimal implementation for unmodifiable list.
     * If list is empty, uses <code>Collections.emptyList</code>.
     * If list has one element, uses <code>Collections.singletonList</code>.
     * Otherwise, wraps the list with <code>Collections.unmodifiableList</code>.
     */
    public static <T> List<T> optimalUnmodifiableList(List<? extends T> list) {
        int size = list.size();
        if(size==0) return java.util.Collections.emptyList();
        if(size==1) return java.util.Collections.singletonList(list.get(0));
        return java.util.Collections.unmodifiableList(list);
    }

    /**
     * Gets the optimal implementation for unmodifiable set.
     * If set is empty, uses <code>Collections.emptySet</code>.
     * If set has one element, uses <code>Collections.singleton</code>.
     * Otherwise, wraps the set with <code>Collections.unmodifiableSet</code>.
     */
    public static <T> Set<T> optimalUnmodifiableSet(Set<? extends T> set) {
        int size = set.size();
        if(size==0) return java.util.Collections.emptySet();
        if(size==1) return java.util.Collections.singleton(set.iterator().next());
        return java.util.Collections.unmodifiableSet(set);
    }

    /**
     * Gets the optimal implementation for unmodifiable sorted set.
     * If sorted set is empty, uses <code>emptySortedSet</code>.
     * If sorted set has one element, uses <code>singletonSortedSet</code>.
     * Otherwise, wraps the sorted set with <code>Collections.unmodifiableSortedSet</code>.
     */
    public static <T> SortedSet<T> optimalUnmodifiableSortedSet(SortedSet<T> sortedSet) {
        int size = sortedSet.size();
        if(size==0) return emptySortedSet();
        if(size==1) return singletonSortedSet(sortedSet.first());
        return java.util.Collections.unmodifiableSortedSet(sortedSet);
    }

    /**
     * Gets the optimal implementation for unmodifiable map.
     * If map is empty, uses <code>Collections.emptyMap</code>.
     * If map has one element, uses <code>Collections.singletonMap</code>.
     * Otherwise, wraps the map with <code>Collections.unmodifiableMap</code>.
     */
    public static <K,V> Map<K,V> optimalUnmodifiableMap(Map<? extends K, ? extends V> map) {
        int size = map.size();
        if(size==0) return java.util.Collections.emptyMap();
        if(size==1) {
            Map.Entry<? extends K,? extends V> entry = map.entrySet().iterator().next();
            return java.util.Collections.singletonMap(entry.getKey(), entry.getValue());
        }
        return java.util.Collections.unmodifiableMap(map);
    }

    /**
     * Gets the optimal implementation for unmodifiable sorted map.
     * If sorted map is empty, uses <code>emptySortedMap</code>.
     * If sorted map has one element, uses <code>singletonSortedMap</code>.
     * Otherwise, wraps the sorted map with <code>Collections.unmodifiableSortedMap</code>.
     */
    public static <K,V> SortedMap<K,V> optimalUnmodifiableSortedMap(SortedMap<K, ? extends V> sortedMap) {
        // TODO: int size = sortedMap.size();
        // TODO: if(size==0) return emptySortedMap();
        // TODO: if(size==1) {
        // TODO:     K key = sortedMap.firstKey();
        // TODO:     return singletonSortedMap(key, sortedMap.get(key));
        // TODO: }
        return java.util.Collections.unmodifiableSortedMap(sortedMap);
    }

    /**
     * Gets an unmodifiable iterator for a single object.
     */
    public static <E> Iterator<E> singletonIterator(final E value) {
        return new Iterator<E>() {

            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public E next() {
                if(!hasNext) throw new NoSuchElementException();
                hasNext = false;
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
