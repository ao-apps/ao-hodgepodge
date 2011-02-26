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
package com.aoindustries.util.logging;

import java.util.Comparator;
import java.util.logging.Level;

/**
 * Level in at least JDK1.4-1.6 is not Comparable.
 * 
 * @see Level
 * 
 * @author  AO Industries, Inc.
 */
final public class LevelComparator implements Comparator<Level> {

    private static final LevelComparator instance = new LevelComparator();
    public static LevelComparator getInstance() {
        return instance;
    }

    private LevelComparator() {
    }

    private Object readResolve() {
        return getInstance();
    }

    @Override
    public int compare(Level l1, Level l2) {
        int i1 = l1.intValue();
        int i2 = l2.intValue();
        if(i1<i2) return -1;
        if(i1>i2) return 1;
        return 0;
    }
}
