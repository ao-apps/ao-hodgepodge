/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013  AO Industries, Inc.
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

import java.text.Collator;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A Properties implementation that returns and writes its keys in alphabetical (English) order.
 * 
 * @author  AO Industries, Inc.
 */
public class SortedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    public SortedProperties() {
	super();
    }

    public SortedProperties(Properties defaults) {
	super(defaults);
    }

    @Override
    public Enumeration<Object> keys() {
        SortedSet<Object> sortedSet = new TreeSet<>(Collator.getInstance(Locale.ENGLISH));
        Enumeration<Object> e = super.keys();
        while(e.hasMoreElements()) sortedSet.add(e.nextElement());
        return Collections.enumeration(sortedSet);
    }
}
