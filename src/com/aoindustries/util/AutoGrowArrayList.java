package com.aoindustries.util;

/*
 * Copyright 2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.ArrayList;
import java.util.Collection;

/**
 * Automatically extends the size of the list instead of throwing exceptions on set, add, and addAll.
 *
 * @author  AO Industries, Inc.
 */
public class AutoGrowArrayList<E> extends ArrayList<E> {

    public AutoGrowArrayList() {
        super();
    }

    public AutoGrowArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public AutoGrowArrayList(Collection<E> c) {
        super(c);
    }

    public E set(int index, E element) {
        int minSize = index+1;
        ensureCapacity(minSize);
        while(size()<minSize) add(null);
        return super.set(index, element);
    }

    public void add(int index, E element) {
        ensureCapacity(index+1);
        while(size()<index) add(null);
        super.add(index, element);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        ensureCapacity(index+c.size());
        while(size()<index) add(null);
        return super.addAll(index, c);
    }
}
