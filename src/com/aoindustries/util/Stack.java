package com.aoindustries.util;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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

    private static final long serialVersionUID = 1L;

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
