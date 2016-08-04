/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2016  AO Industries, Inc.
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
import java.util.Collections;
import java.util.List;

/**
 * MinimalList provides a set of static methods to dynamically choose the most
 * efficient List implementation.  The implementation of List is changed as needed.
 * MinimalList is most suited for building list-based data structures that use less
 * heap space than a pure ArrayList-based solution.
 * <p>
 * size=0: null<br/>
 * size=1: Collections.singletonList<br/>
 * size=2: ArrayList
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class MinimalList {

	private MinimalList() {
	}

	/**
	 * Adds a new element to a list, returning the (possibly new) list.
	 */
	public static <E> List<E> add(List<E> list, E elem) {
		if(list == null) {
			// The first element is always a singletonList
			list = Collections.singletonList(elem);
		} else if(list.size()==1) {
			// Is a singleton list
			List<E> newList = new ArrayList<>(8);
			newList.add(list.get(0));
			newList.add(elem);
			list = newList;
		} else {
			// Is an ArrayList
			list.add(elem);
		}
		return list;
	}

	/**
	 * Gets an element from a list.
	 */
	public static <E> E get(List<E> list, int index) throws IndexOutOfBoundsException {
		if(list==null) throw new IndexOutOfBoundsException();
		return list.get(index);
	}

	/**
	 * Performs a shallow copy of a list.  The list is assumed to have been
	 * created by MinimalList and to be used through MinimalList.
	 */
	public static <E> List<E> copy(List<E> list) {
		if(list==null) {
			// Empty
			return null;
		}
		if(list.size()==1) {
			// Is a singletonList (unmodifiable) - safe to share instance.
			return list;
		}
		// Create copy of list
		return new ArrayList<>(list);
	}
}
