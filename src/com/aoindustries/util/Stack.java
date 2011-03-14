/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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
import java.util.EmptyStackException;

/**
 * This is a source-compatible, unsynchronized version of <code>java.util.Stack</code>
 * based on <code>java.util.ArrayList</code>.
 * 
 * @see java.util.Stack
 *
 * @author  AO Industries, Inc.
 */
public class Stack<E> extends ArrayList<E> {

    private static final long serialVersionUID = -3967561912087026100L;

    /**
     * @see java.util.Stack#Stack
     */
    public Stack() {
        super();
    }

    /**
     * @see java.util.Stack#push
     */
    public E push(E item) {
	add(item);
	return item;
    }

    /**
     * @see java.util.Stack#pop
     */
    public E pop() {
	int len = size();
	if (len == 0) throw new EmptyStackException();
	return remove(len - 1);
    }

    /**
     * @see java.util.Stack#peek
     */
    public E peek() {
	int len = size();
	if (len == 0) throw new EmptyStackException();
	return get(len - 1);
    }

    /**
     * @see java.util.Stack#empty
     *
     * @deprecated  This exists only for source compatibility
     *              with <code>java.util.Stack</code>, please use <code>isEmpty</code>
     *              instead.
     */
    @Deprecated
    public boolean empty() {
	return isEmpty();
    }

    /**
     * @see java.util.Stack#search
     */
    public int search(Object o) {
	int i = lastIndexOf(o);
	if (i >= 0) return size() - i;
	return -1;
    }
}
