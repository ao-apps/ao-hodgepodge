/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2018, 2019, 2020, 2021, 2022, 2025  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.sort;

import com.aoapps.collections.IntArrayList;
import com.aoapps.collections.IntList;
import com.aoapps.lang.io.IoUtils;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
@SuppressWarnings({"SleepWhileInLoop", "UseOfSystemOutOrSystemErr"})
public class IntegerRadixSortTest extends TestCase {

  private static final boolean GC_EACH_PASS = false;
  private static final long GC_SLEEP_TIME = 100;

  private static final int START_TEST_SIZE = 1; // << 8;
  private static final int END_TEST_SIZE = 1 << 16 /*20*/; // << 26;
  private static final int MAX_PASSES = (1 << 16/*20*/) / START_TEST_SIZE;

  private static final boolean USE_SORTED = false;

  private static final boolean RANDOM_FULL = true;
  private static final boolean RANDOM_NEGATIVE = false;
  private static final int RANDOM_RANGE = 0x100;
  private static final int RANDOM_MULTIPLIER = 1; // 0x10000;

  public IntegerRadixSortTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(IntegerRadixSortTest.class);
  }

  /**
   * A fast pseudo-random number generator for non-cryptographic purposes.
   */
  private static final Random fastRandom = new Random(IoUtils.bufferToLong(new SecureRandom().generateSeed(Long.BYTES)));

  private static int getRandomValue() {
    return
        RANDOM_FULL ? fastRandom.nextInt()
            : RANDOM_NEGATIVE ? (fastRandom.nextInt(RANDOM_RANGE) * RANDOM_MULTIPLIER * -1 - 1)
            : (fastRandom.nextInt(RANDOM_RANGE) * RANDOM_MULTIPLIER);
  }

  @SuppressWarnings("unchecked")
  private <N extends Number> void doTestListPerformance(
      List<N> randomValues,
      int testSize,
      int passes,
      long[] totalOld,
      // long[] totalNew,
      long[] totalExp,
      long[] totalJava,
      long[] avgSumOld,
      // long[] avgSumNew,
      long[] avgSumExp
  ) {
    long expRadixNanos = 0;
    // long newRadixNanos = 0;
    long javaNanos = 0;
    long oldRadixNanos = 0;
    // Iteration 0 is the warm-up and is not counted
    for (int iteration = 0; iteration <= passes; iteration++) {
      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time experimental radix sort
      List<N> expRadixResult = new ArrayList<>(randomValues);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSortExperimental.getInstance().sort(expRadixResult);
        if (iteration > 0) {
          expRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSortExperimental in "+BigDecimal.valueOf(expRadixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time new radix sort
      /*
      List<N> newRadixResult = new ArrayList<>(randomValues);
        {
          long startNanos = System.nanoTime();
          NewIntegerRadixSort.getInstance().sort(newRadixResult);
          if (iteration>0) {
            newRadixNanos += System.nanoTime() - startNanos;
            // System.out.println(pass+"/"+testSize+": NewIntegerRadixSort in "+BigDecimal.valueOf(newRadixNanos, 3)+" µs");
          }
        }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }
       */

      // Time radix sort
      List<N> oldRadixResult = new ArrayList<>(randomValues);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSort.getInstance().sort(oldRadixResult);
        if (iteration > 0) {
          oldRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSort in "+BigDecimal.valueOf(radixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time Java sort
      List<N> javaResult = new ArrayList<>(randomValues);
      {
        long startNanos = System.nanoTime();
        Collections.sort(javaResult, null);
        if (iteration > 0) {
          javaNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": Collections.sort in "+BigDecimal.valueOf(javaNanos, 3)+" µs");
        }
      }

      // Check results
      assertEquals(javaResult, oldRadixResult);
      // assertEquals(javaResult, newRadixResult);
      assertEquals(javaResult, expRadixResult);
    }

    // Update total times
    totalExp[0] += expRadixNanos;
    // totalNew[0] += newRadixNanos;
    totalOld[0] += oldRadixNanos;
    totalJava[0] += javaNanos;
    // Calculate scaled values
    long scaledOld = javaNanos * 1000 / oldRadixNanos;
    // long scaledNew = javaNanos * 1000 / newRadixNanos;
    long scaledExp = javaNanos * 1000 / expRadixNanos;
    // Update average sums
    avgSumOld[0] += scaledOld;
    // avgSumNew[0] += scaledNew;
    avgSumExp[0] += scaledExp;
    // Display speedup
    System.out.println(
        testSize
            + ": Speedup (Old/Experimental): "
            + BigDecimal.valueOf(scaledOld, 3)
            // + " / "
            // + BigDecimal.valueOf(scaledNew, 3)
            + " / "
            + BigDecimal.valueOf(scaledExp, 3)
    );
  }

  public void testListPerformance() {
    System.out.println("testListPerformance");
    long[] totalOld = new long[1];
    // long[] totalNew = new long[1];
    long[] totalExp = new long[1];
    long[] totalJava = new long[1];
    long[] avgSumOld = new long[1];
    // long[] avgSumNew = new long[1];
    long[] avgSumExp = new long[1];
    List<Integer> randomValues = new ArrayList<>(END_TEST_SIZE);
    int tests = 0;
    for (int testSize = START_TEST_SIZE, passes = MAX_PASSES;
        testSize <= END_TEST_SIZE;
        testSize *= 2, passes /= 2
    ) {
      // Generate testSize random ints
      while (randomValues.size() > testSize) {
        randomValues.remove(randomValues.size() - 1);
      }
      while (randomValues.size() < testSize) {
        randomValues.add(USE_SORTED ? randomValues.size() : getRandomValue());
      }

      tests++;
      doTestListPerformance(
          randomValues,
          testSize,
          passes < 1 ? 1 : passes,
          totalOld,
          // totalNew,
          totalExp,
          totalJava,
          avgSumOld,
          // avgSumNew,
          avgSumExp
      );
    }
    // Display total speedup
    System.out.println(
        "Total Speedup (Old/Experimental): "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalOld[0], 3)
            // + " / "
            // + BigDecimal.valueOf(totalJava[0] * 1000 / totalNew[0], 3)
            + " / "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalExp[0], 3)
    );
    System.out.println(
        "Average Speedup (Old/Experimental): "
            + BigDecimal.valueOf(avgSumOld[0] / tests, 3)
            // + " / "
            // + BigDecimal.valueOf(avgSumNew[0] / tests, 3)
            + " / "
            + BigDecimal.valueOf(avgSumExp[0] / tests, 3)
    );
  }

  @SuppressWarnings("unchecked")
  private <N extends Number> void doTestArrayPerformance(
      N[] randomValues,
      int testSize,
      int passes,
      long[] totalOld,
      // long[] totalNew,
      long[] totalExp,
      long[] totalJava,
      long[] avgSumOld,
      // long[] avgSumNew,
      long[] avgSumExp
  ) {
    long expRadixNanos = 0;
    // long newRadixNanos = 0;
    long javaNanos = 0;
    long oldRadixNanos = 0;
    // Iteration 0 is the warm-up and is not counted
    for (int iteration = 0; iteration <= passes; iteration++) {
      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time experimental radix sort
      N[] expRadixResult = (N[]) new Number[randomValues.length];
      System.arraycopy(randomValues, 0, expRadixResult, 0, randomValues.length);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSortExperimental.getInstance().sort(expRadixResult);
        if (iteration > 0) {
          expRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSortExperimental in "+BigDecimal.valueOf(expRadixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time new radix sort
      /*
      N[] newRadixResult = (N[])new Number[randomValues.length];
      System.arraycopy(randomValues, 0, newRadixResult, 0, randomValues.length);
        {
          long startNanos = System.nanoTime();
          NewIntegerRadixSort.getInstance().sort(newRadixResult);
          if (iteration>0) {
            newRadixNanos += System.nanoTime() - startNanos;
            // System.out.println(pass+"/"+testSize+": NewIntegerRadixSort in "+BigDecimal.valueOf(newRadixNanos, 3)+" µs");
          }
        }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }
       */

      // Time radix sort
      N[] oldRadixResult = (N[]) new Number[randomValues.length];
      System.arraycopy(randomValues, 0, oldRadixResult, 0, randomValues.length);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSort.getInstance().sort(oldRadixResult);
        if (iteration > 0) {
          oldRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSort in "+BigDecimal.valueOf(radixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time Java sort
      N[] javaResult = (N[]) new Number[randomValues.length];
      System.arraycopy(randomValues, 0, javaResult, 0, randomValues.length);
      {
        long startNanos = System.nanoTime();
        Arrays.sort(javaResult);
        if (iteration > 0) {
          javaNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": Collections.sort in "+BigDecimal.valueOf(javaNanos, 3)+" µs");
        }
      }

      // Check results
      List<N> javaResultList = Arrays.asList(javaResult);
      List<N> oldRadixResultList = Arrays.asList(oldRadixResult);
      // List<N> newRadixResultList = Arrays.asList(newRadixResult);
      List<N> expRadixResultList = Arrays.asList(expRadixResult);
      assertEquals(javaResultList, oldRadixResultList);
      // assertEquals(javaResultList, newRadixResultList);
      assertEquals(javaResultList, expRadixResultList);
    }

    // Update total times
    totalExp[0] += expRadixNanos;
    // totalNew[0] += newRadixNanos;
    totalOld[0] += oldRadixNanos;
    totalJava[0] += javaNanos;
    // Calculate scaled values
    long scaledOld = javaNanos * 1000 / oldRadixNanos;
    // long scaledNew = javaNanos * 1000 / newRadixNanos;
    long scaledExp = javaNanos * 1000 / expRadixNanos;
    // Update average sums
    avgSumOld[0] += scaledOld;
    // avgSumNew[0] += scaledNew;
    avgSumExp[0] += scaledExp;
    // Display speedup
    System.out.println(
        testSize
            + ": Speedup (Old/Experimental): "
            + BigDecimal.valueOf(scaledOld, 3)
            // + " / "
            // + BigDecimal.valueOf(scaledNew, 3)
            + " / "
            + BigDecimal.valueOf(scaledExp, 3)
    );
  }

  public void testArrayPerformance() {
    System.out.println("testArrayPerformance");
    long[] totalOld = new long[1];
    // long[] totalNew = new long[1];
    long[] totalExp = new long[1];
    long[] totalJava = new long[1];
    long[] avgSumOld = new long[1];
    // long[] avgSumNew = new long[1];
    long[] avgSumExp = new long[1];
    int tests = 0;
    for (int testSize = START_TEST_SIZE, passes = MAX_PASSES;
        testSize <= END_TEST_SIZE;
        testSize *= 2, passes /= 2
    ) {
      // Generate testSize random ints
      Integer[] randomValues = new Integer[testSize];
      for (int i = 0; i < testSize; i++) {
        randomValues[i] = USE_SORTED ? i : getRandomValue();
      }

      tests++;
      doTestArrayPerformance(
          randomValues,
          testSize,
          passes < 1 ? 1 : passes,
          totalOld,
          // totalNew,
          totalExp,
          totalJava,
          avgSumOld,
          // avgSumNew,
          avgSumExp
      );
    }
    // Display total speedup
    System.out.println(
        "Total Speedup (Old/Experimental): "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalOld[0], 3)
            // + " / "
            // + BigDecimal.valueOf(totalJava[0] * 1000 / totalNew[0], 3)
            + " / "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalExp[0], 3)
    );
    System.out.println(
        "Average Speedup (Old/Experimental): "
            + BigDecimal.valueOf(avgSumOld[0] / tests, 3)
            // + " / "
            // + BigDecimal.valueOf(avgSumNew[0] / tests, 3)
            + " / "
            + BigDecimal.valueOf(avgSumExp[0] / tests, 3)
    );
  }

  public void doTestIntListPerformance(
      IntList randomValues,
      int testSize,
      int passes,
      long[] totalOld,
      // long[] totalNew,
      long[] totalExp,
      long[] totalJava,
      long[] avgSumOld,
      // long[] avgSumNew,
      long[] avgSumExp
  ) {
    long expRadixNanos = 0;
    // long newRadixNanos = 0;
    long javaNanos = 0;
    long oldRadixNanos = 0;
    // Iteration 0 is the warm-up and is not counted
    for (int iteration = 0; iteration <= passes; iteration++) {
      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time experimental radix sort
      IntList expRadixResult = new IntArrayList(randomValues);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSortExperimental.getInstance().sort(expRadixResult);
        if (iteration > 0) {
          expRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSortExperimental in "+BigDecimal.valueOf(expRadixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time new radix sort
      /*
      IntList newRadixResult = new IntArrayList(randomValues);
        {
          long startNanos = System.nanoTime();
          NewIntegerRadixSort.getInstance().sort(newRadixResult);
          if (iteration>0) {
            newRadixNanos += System.nanoTime() - startNanos;
            // System.out.println(pass+"/"+testSize+": NewIntegerRadixSort in "+BigDecimal.valueOf(newRadixNanos, 3)+" µs");
          }
        }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }
       */

      // Time radix sort
      IntList oldRadixResult = new IntArrayList(randomValues);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSort.getInstance().sort(oldRadixResult);
        if (iteration > 0) {
          oldRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSort in "+BigDecimal.valueOf(radixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time Java sort
      IntList javaResult = new IntArrayList(randomValues);
      {
        long startNanos = System.nanoTime();
        Collections.sort(javaResult, null);
        if (iteration > 0) {
          javaNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": Collections.sort in "+BigDecimal.valueOf(javaNanos, 3)+" µs");
        }
      }

      // Check results
      assertEquals(javaResult, oldRadixResult);
      // assertEquals(javaResult, newRadixResult);
      assertEquals(javaResult, expRadixResult);
    }

    // Update total times
    totalExp[0] += expRadixNanos;
    // totalNew[0] += newRadixNanos;
    totalOld[0] += oldRadixNanos;
    totalJava[0] += javaNanos;
    // Calculate scaled values
    long scaledOld = javaNanos * 1000 / oldRadixNanos;
    // long scaledNew = javaNanos * 1000 / newRadixNanos;
    long scaledExp = javaNanos * 1000 / expRadixNanos;
    // Update average sums
    avgSumOld[0] += scaledOld;
    // avgSumNew[0] += scaledNew;
    avgSumExp[0] += scaledExp;
    // Display speedup
    System.out.println(
        testSize
            + ": Speedup (Old/Experimental): "
            + BigDecimal.valueOf(scaledOld, 3)
            // + " / "
            // + BigDecimal.valueOf(scaledNew, 3)
            + " / "
            + BigDecimal.valueOf(scaledExp, 3)
    );
  }

  public void testIntListPerformance() {
    System.out.println("testIntListPerformance");
    long[] totalOld = new long[1];
    // long[] totalNew = new long[1];
    long[] totalExp = new long[1];
    long[] totalJava = new long[1];
    long[] avgSumOld = new long[1];
    // long[] avgSumNew = new long[1];
    long[] avgSumExp = new long[1];
    IntList randomValues = new IntArrayList(END_TEST_SIZE);
    int tests = 0;
    for (int testSize = START_TEST_SIZE, passes = MAX_PASSES;
        testSize <= END_TEST_SIZE;
        testSize *= 2, passes /= 2
    ) {
      // Generate testSize random ints
      while (randomValues.size() > testSize) {
        randomValues.removeAtIndex(randomValues.size() - 1);
      }
      while (randomValues.size() < testSize) {
        randomValues.add(USE_SORTED ? randomValues.size() : getRandomValue());
      }

      tests++;
      doTestListPerformance(
          randomValues,
          testSize,
          passes < 1 ? 1 : passes,
          totalOld,
          // totalNew,
          totalExp,
          totalJava,
          avgSumOld,
          // avgSumNew,
          avgSumExp
      );
    }
    // Display total speedup
    System.out.println(
        "Total Speedup (Old/Experimental): "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalOld[0], 3)
            // + " / "
            // + BigDecimal.valueOf(totalJava[0] * 1000 / totalNew[0], 3)
            + " / "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalExp[0], 3)
    );
    System.out.println(
        "Average Speedup (Old/Experimental): "
            + BigDecimal.valueOf(avgSumOld[0] / tests, 3)
            // + " / "
            // + BigDecimal.valueOf(avgSumNew[0] / tests, 3)
            + " / "
            + BigDecimal.valueOf(avgSumExp[0] / tests, 3)
    );
  }

  private void doTestIntArrayPerformance(
      int[] randomValues,
      int testSize,
      int passes,
      long[] totalOld,
      // long[] totalNew,
      long[] totalExp,
      long[] totalJava,
      long[] avgSumOld,
      // long[] avgSumNew,
      long[] avgSumExp
  ) {
    long expRadixNanos = 0;
    // long newRadixNanos = 0;
    long javaNanos = 0;
    long oldRadixNanos = 0;
    // Iteration 0 is the warm-up and is not counted
    for (int iteration = 0; iteration <= passes; iteration++) {
      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time experimental radix sort
      int[] expRadixResult = new int[randomValues.length];
      System.arraycopy(randomValues, 0, expRadixResult, 0, randomValues.length);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSortExperimental.getInstance().sort(expRadixResult);
        // Arrays.sort(expRadixResult);
        if (iteration > 0) {
          expRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSortExperimental in "+BigDecimal.valueOf(expRadixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time new radix sort
      /*
      int[] newRadixResult = new int[randomValues.length];
      System.arraycopy(randomValues, 0, newRadixResult, 0, randomValues.length);
        {
          long startNanos = System.nanoTime();
          NewIntegerRadixSort.getInstance().sort(newRadixResult);
          // Arrays.sort(newRadixResult);
          if (iteration>0) {
            newRadixNanos += System.nanoTime() - startNanos;
            // System.out.println(pass+"/"+testSize+": NewIntegerRadixSort in "+BigDecimal.valueOf(newRadixNanos, 3)+" µs");
          }
        }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }
       */

      // Time radix sort
      int[] oldRadixResult = new int[randomValues.length];
      System.arraycopy(randomValues, 0, oldRadixResult, 0, randomValues.length);
      {
        long startNanos = System.nanoTime();
        IntegerRadixSort.getInstance().sort(oldRadixResult);
        // Arrays.sort(oldRadixResult);
        if (iteration > 0) {
          oldRadixNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": IntegerRadixSort in "+BigDecimal.valueOf(radixNanos, 3)+" µs");
        }
      }

      if (GC_EACH_PASS) {
        System.gc();
        try {
          Thread.sleep(GC_SLEEP_TIME);
        } catch (InterruptedException e) {
          e.printStackTrace(System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }

      // Time Java sort
      int[] javaResult = new int[randomValues.length];
      System.arraycopy(randomValues, 0, javaResult, 0, randomValues.length);
      {
        long startNanos = System.nanoTime();
        Arrays.sort(javaResult);
        if (iteration > 0) {
          javaNanos += System.nanoTime() - startNanos;
          // System.out.println(pass+"/"+testSize+": Collections.sort in "+BigDecimal.valueOf(javaNanos, 3)+" µs");
        }
      }

      // Check results
      List<Integer> javaResultList = new IntArrayList(javaResult);
      List<Integer> oldRadixResultList = new IntArrayList(oldRadixResult);
      // List<Integer> newRadixResultList = new IntArrayList(newRadixResult);
      List<Integer> expRadixResultList = new IntArrayList(expRadixResult);
      assertEquals("oldRadixResultList", javaResultList, oldRadixResultList);
      // assertEquals("newRadixResultList", javaResultList, newRadixResultList);
      assertEquals("expRadixResultList", javaResultList, expRadixResultList);
    }

    // Update total times
    totalExp[0] += expRadixNanos;
    // totalNew[0] += newRadixNanos;
    totalOld[0] += oldRadixNanos;
    totalJava[0] += javaNanos;
    // Calculate scaled values
    long scaledOld = javaNanos * 1000 / oldRadixNanos;
    // long scaledNew = javaNanos * 1000 / newRadixNanos;
    long scaledExp = javaNanos * 1000 / expRadixNanos;
    // Update average sums
    avgSumOld[0] += scaledOld;
    // avgSumNew[0] += scaledNew;
    avgSumExp[0] += scaledExp;
    // Display speedup
    System.out.println(
        testSize
            + ": Speedup (Old/Experimental): "
            + BigDecimal.valueOf(scaledOld, 3)
            // + " / "
            // + BigDecimal.valueOf(scaledNew, 3)
            + " / "
            + BigDecimal.valueOf(scaledExp, 3)
    );
  }

  public void testIntArrayPerformance() {
    System.out.println("testIntArrayPerformance");
    long[] totalOld = new long[1];
    // long[] totalNew = new long[1];
    long[] totalExp = new long[1];
    long[] totalJava = new long[1];
    long[] avgSumOld = new long[1];
    // long[] avgSumNew = new long[1];
    long[] avgSumExp = new long[1];
    int tests = 0;
    for (int testSize = START_TEST_SIZE, passes = MAX_PASSES;
        testSize <= END_TEST_SIZE;
        testSize *= 2, passes /= 2
    ) {
      // Generate testSize random ints
      int[] randomValues = new int[testSize];
      for (int i = 0; i < testSize; i++) {
        randomValues[i] = USE_SORTED ? i : getRandomValue();
      }

      tests++;
      doTestIntArrayPerformance(
          randomValues,
          testSize,
          passes < 1 ? 1 : passes,
          totalOld,
          // totalNew,
          totalExp,
          totalJava,
          avgSumOld,
          // avgSumNew,
          avgSumExp
      );
    }
    // Display total speedup
    System.out.println(
        "Total Speedup (Old/Experimental): "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalOld[0], 3)
            // + " / "
            // + BigDecimal.valueOf(totalJava[0] * 1000 / totalNew[0], 3)
            + " / "
            + BigDecimal.valueOf(totalJava[0] * 1000 / totalExp[0], 3)
    );
    System.out.println(
        "Average Speedup (Old/Experimental): "
            + BigDecimal.valueOf(avgSumOld[0] / tests, 3)
            // + " / "
            // + BigDecimal.valueOf(avgSumNew[0] / tests, 3)
            + " / "
            + BigDecimal.valueOf(avgSumExp[0] / tests, 3)
    );
  }
}
