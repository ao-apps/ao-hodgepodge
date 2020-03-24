/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013, 2016, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.collections.HashCodeComparator;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class ArraySetTest extends TestCase {

	public ArraySetTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(ArraySetTest.class);
	}

	private static final Random random = new SecureRandom();

	private void doTestPerformance() {
		final int endTestSize = 1000000;
		Set<Integer> randomValues = new HashSet<>(endTestSize);
		for(int testSize = 1; testSize<=endTestSize; testSize *= 10) {
			// Generate testSize random ints
			while(randomValues.size()<testSize) randomValues.add(random.nextInt());
			List<Integer> randomList = new ArrayList<>(randomValues);
			// Time new
			long startNanos = System.nanoTime();
			HashSet<Integer> hashSet = new HashSet<>(randomList);
			long timeNanos = System.nanoTime() - startNanos;
			System.out.println(testSize+": Created HashSet in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");
			startNanos = System.nanoTime();
			ArrayList<Integer> list = new ArrayList<>(randomList);
			java.util.Collections.sort(list, HashCodeComparator.getInstance());
			ArraySet<Integer> arraySet = new ArraySet<>(list);
			timeNanos = System.nanoTime() - startNanos;
			System.out.println(testSize+": Created ArraySet in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");
			// Test contains
			startNanos = System.nanoTime();
			for(Integer value : randomList) {
				if(!hashSet.contains(value)) throw new AssertionError();
			}
			timeNanos = System.nanoTime() - startNanos;
			System.out.println(testSize+": HashSet contains in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");
			startNanos = System.nanoTime();
			for(Integer value : randomList) {
				if(!arraySet.contains(value)) throw new AssertionError();
			}
			timeNanos = System.nanoTime() - startNanos;
			System.out.println(testSize+": ArraySet contains in "+BigDecimal.valueOf(timeNanos/1000, 3)+" ms");

		}
	}

	public void testPerformance() {
		final int numTests = 1;
		for(int c=0;c<numTests;c++) doTestPerformance();
	}
}
