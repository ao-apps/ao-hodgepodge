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
    private <T extends Number> void doTestPerformance(List<T> randomValues, int pass, int testSize) {
		if(GC_EACH_PASS) {
			System.gc();
			try {
				Thread.sleep(GC_SLEEP_TIME);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Time new radix sort
		List<T> newRadixResult = new ArrayList<T>(randomValues);
		long newRadixNanos;
		{
			long startNanos = System.nanoTime();
			IntegerRadixSortNew.getInstance().sort(newRadixResult);
			newRadixNanos = System.nanoTime() - startNanos;
			//System.out.println(pass+"/"+testSize+": IntegerRadixSortNew in "+BigDecimal.valueOf(newRadixNanos, 3)+" \u00B5s");
		}

		// Time radix sort
		List<T> radixResult = new ArrayList<T>(randomValues);
		long radixNanos;
		{
			long startNanos = System.nanoTime();
			IntegerRadixSort.getInstance().sort(radixResult);
			radixNanos = System.nanoTime() - startNanos;
			//System.out.println(pass+"/"+testSize+": IntegerRadixSort in "+BigDecimal.valueOf(radixNanos, 3)+" \u00B5s");
		}

		// Time Java sort
		List<T> javaResult = new ArrayList<T>(randomValues);
		long javaNanos;
		{
			long startNanos = System.nanoTime();
			Collections.sort((List)javaResult);
			javaNanos = System.nanoTime() - startNanos;
			//System.out.println(pass+"/"+testSize+": Collections.sort in "+BigDecimal.valueOf(javaNanos, 3)+" \u00B5s");
		}
		System.out.println(pass+"/"+testSize+": Speedup (Old/New): "+BigDecimal.valueOf(javaNanos * 1000 / radixNanos, 3) + " / " + BigDecimal.valueOf(javaNanos * 1000 / newRadixNanos, 3));

		// TODO: Display speedup

		assertEquals(javaResult, radixResult);
    }

    public void testPerformance() {
        final int numTests = 9;
        final int endTestSize = 1000000;
        List<Integer> randomValues = new ArrayList<Integer>(endTestSize);
        for(int testSize = 1; testSize<=endTestSize; testSize *= 10) {
            // Generate testSize random ints
            while(randomValues.size()<testSize) randomValues.add(random.nextInt());

			for(int pass=1; pass<=numTests; pass++) {
				doTestPerformance(randomValues, pass, testSize);
			}
        }
    }
}
