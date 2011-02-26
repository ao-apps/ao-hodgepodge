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

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class ArraySortedSetTest extends TestCase {

    public ArraySortedSetTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ArraySortedSetTest.class);
    }

    private static final Random random = new SecureRandom();

    private void doTestPerformance() {
        final int endTestSize = 1000000;
        SortedSet<Integer> randomValues = new TreeSet<Integer>();
        for(int testSize = 1; testSize<=endTestSize; testSize *= 10) {
            // Generate testSize random ints
            while(randomValues.size()<testSize) randomValues.add(random.nextInt());
            List<Integer> randomList = new ArrayList<Integer>(randomValues);
            // Time new
            long startNanos = System.nanoTime();
            TreeSet<Integer> treeSet = new TreeSet<Integer>(randomValues);
            long timeNanos = System.nanoTime() - startNanos;
            System.out.println(testSize+": Created TreeSet in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");
            startNanos = System.nanoTime();
            ArraySortedSet<Integer> arraySortedSet = new ArraySortedSet<Integer>(randomValues);
            timeNanos = System.nanoTime() - startNanos;
            System.out.println(testSize+": Created ArraySortedSet in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");
            // Test contains
            startNanos = System.nanoTime();
            for(Integer value : randomList) {
                if(!treeSet.contains(value)) throw new AssertionError();
            }
            timeNanos = System.nanoTime() - startNanos;
            System.out.println(testSize+": TreeSet contains in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");
            startNanos = System.nanoTime();
            for(Integer value : randomList) {
                if(!arraySortedSet.contains(value)) throw new AssertionError();
            }
            timeNanos = System.nanoTime() - startNanos;
            System.out.println(testSize+": ArraySortedSet contains in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");

        }
    }

    public void testPerformance() {
        final int numTests = 1;
        for(int c=0;c<numTests;c++) doTestPerformance();
    }
}
