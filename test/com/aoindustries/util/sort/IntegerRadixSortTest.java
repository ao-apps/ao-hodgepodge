/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
package com.aoindustries.util.sort;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class IntegerRadixSortTest extends TestCase {

	private static final boolean GC_EACH_PASS = true;
	private static final long GC_SLEEP_TIME = 100;

    public IntegerRadixSortTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(IntegerRadixSortTest.class);
    }

    private static final Random random = new SecureRandom();

	@SuppressWarnings("unchecked")
    private <T extends Number> void doTestPerformance(
		List<T> randomValues,
		int pass,
		int testSize,
		long[] totalOld,
		long[] totalNew,
		long[] totalExp,
		long[] totalJava
	) {
		if(GC_EACH_PASS) {
			System.gc();
			try {
				Thread.sleep(GC_SLEEP_TIME);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Time experimental radix sort
		List<T> expRadixResult = new ArrayList<T>(randomValues);
		long expRadixNanos;
		{
			long startNanos = System.nanoTime();
			IntegerRadixSortExperimental.getInstance().sort(expRadixResult);
			expRadixNanos = System.nanoTime() - startNanos;
			totalExp[0] += expRadixNanos;
			//System.out.println(pass+"/"+testSize+": IntegerRadixSortExperimental in "+BigDecimal.valueOf(expRadixNanos, 3)+" \u00B5s");
		}

		// Time new radix sort
		List<T> newRadixResult = new ArrayList<T>(randomValues);
		long newRadixNanos;
		{
			long startNanos = System.nanoTime();
			IntegerRadixSortNew.getInstance().sort(newRadixResult);
			newRadixNanos = System.nanoTime() - startNanos;
			totalNew[0] += newRadixNanos;
			//System.out.println(pass+"/"+testSize+": IntegerRadixSortNew in "+BigDecimal.valueOf(newRadixNanos, 3)+" \u00B5s");
		}

		// Time radix sort
		List<T> oldRadixResult = new ArrayList<T>(randomValues);
		long oldRadixNanos;
		{
			long startNanos = System.nanoTime();
			IntegerRadixSort.getInstance().sort(oldRadixResult);
			oldRadixNanos = System.nanoTime() - startNanos;
			totalOld[0] += oldRadixNanos;
			//System.out.println(pass+"/"+testSize+": IntegerRadixSort in "+BigDecimal.valueOf(radixNanos, 3)+" \u00B5s");
		}

		// Time Java sort
		List<T> javaResult = new ArrayList<T>(randomValues);
		long javaNanos;
		{
			long startNanos = System.nanoTime();
			Collections.sort((List)javaResult);
			javaNanos = System.nanoTime() - startNanos;
			totalJava[0] += javaNanos;
			//System.out.println(pass+"/"+testSize+": Collections.sort in "+BigDecimal.valueOf(javaNanos, 3)+" \u00B5s");
		}

		// Display speedup
		System.out.println(
			pass
			+ "/"
			+ testSize
			+ ": Speedup (Old/New/Experimental): "
			+ BigDecimal.valueOf(javaNanos * 1000 / oldRadixNanos, 3)
			+ " / "
			+ BigDecimal.valueOf(javaNanos * 1000 / newRadixNanos, 3)
			+ " / "
			+ BigDecimal.valueOf(javaNanos * 1000 / expRadixNanos, 3)
		);

		assertEquals(javaResult, oldRadixResult);
    }

    public void testPerformance() {
        final int numTests = 9;
        final int endTestSize = 1000000;
		long[] totalOld = new long[1];
		long[] totalNew = new long[1];
		long[] totalExp = new long[1];
		long[] totalJava = new long[1];
        List<Integer> randomValues = new ArrayList<Integer>(endTestSize);
        for(int testSize = 1; testSize<=endTestSize; testSize *= 10) {
            // Generate testSize random ints
            while(randomValues.size()<testSize) randomValues.add(random.nextInt());

			for(int pass=1; pass<=numTests; pass++) {
				doTestPerformance(randomValues, pass, testSize, totalOld, totalNew, totalExp, totalJava);
			}
        }
		// Display total speedup
		System.out.println(
			"Total Speedup (Old/New/Experimental): "
			+ BigDecimal.valueOf(totalJava[0] * 1000 / totalOld[0], 3)
			+ " / "
			+ BigDecimal.valueOf(totalJava[0] * 1000 / totalNew[0], 3)
			+ " / "
			+ BigDecimal.valueOf(totalJava[0] * 1000 / totalExp[0], 3)
		);
    }
}
