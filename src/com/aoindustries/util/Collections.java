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
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

/**
 * General-purpose collection utilities and constants.
 *
 * @author  AO Industries, Inc.
 */
public class Collections {

    private Collections() {
    }

    public static final SortedSet EMPTY_SORTED_SET = new EmptySortedSet();

    @SuppressWarnings("unchecked")
    public static final <T> SortedSet<T> emptySortedSet() {
        return (SortedSet<T>) EMPTY_SORTED_SET;
    }

    private static class EmptySortedSet extends AbstractSet<Object> implements SortedSet<Object>, Serializable {

        private static final long serialVersionUID = 1L;

        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                public boolean hasNext() {
                    return false;
                }
                public Object next() {
                    throw new NoSuchElementException();
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public int size() {return 0;}

        @Override
        public boolean contains(Object obj) {
            return false;
        }

        private Object readResolve() {
            return EMPTY_SORTED_SET;
        }

        public Comparator<? super Object> comparator() {
            return null;
        }

        public SortedSet<Object> subSet(Object fromElement, Object toElement) {
            throw new IllegalArgumentException();
        }

        public SortedSet<Object> headSet(Object toElement) {
            throw new IllegalArgumentException();
        }

        public SortedSet<Object> tailSet(Object fromElement) {
            throw new IllegalArgumentException();
        }

        public Object first() {
            throw new NoSuchElementException();
        }

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

        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private boolean hasNext = true;
                public boolean hasNext() {
                    return hasNext;
                }
                public E next() {
                    if (hasNext) {
                        hasNext = false;
                        return element;
                    }
                    throw new NoSuchElementException();
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public int size() {
            return 1;
        }

        @Override
        public boolean contains(Object o) {return StringUtility.equals(o, element);}

        public Comparator<? super E> comparator() {
            return null;
        }

        public SortedSet<E> subSet(E fromElement, E toElement) {
            if(StringUtility.equals(element, fromElement) && StringUtility.equals(element, toElement)) return emptySortedSet();
            throw new IllegalArgumentException();
        }

        public SortedSet<E> headSet(E toElement) {
            if(StringUtility.equals(element, toElement)) return emptySortedSet();
            throw new IllegalArgumentException();
        }

        public SortedSet<E> tailSet(E fromElement) {
            if(StringUtility.equals(element, fromElement)) return this;
            throw new IllegalArgumentException();
        }

        public E first() {
            return element;
        }

        public E last() {
            return element;
        }
    }
}
