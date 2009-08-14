/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009  AO Industries, Inc.
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
