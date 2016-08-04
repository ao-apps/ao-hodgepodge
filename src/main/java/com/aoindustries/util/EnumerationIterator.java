/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Bridges the gap between Enumeration and Iterator in the opposite direction of Collections.enumeration(Collection);
 *
 * @see  Collections#enumeration(java.util.Collection)
 *
 * @author  AO Industries, Inc.
 */
public class EnumerationIterator<E> implements Iterator<E> {

    private final Enumeration<E> enumerator;

    public EnumerationIterator(Enumeration<E> enumerator) {
        this.enumerator = enumerator;
    }

    @Override
    public boolean hasNext() {
        return enumerator.hasMoreElements();
    }

    @Override
    public E next() {
        return enumerator.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}