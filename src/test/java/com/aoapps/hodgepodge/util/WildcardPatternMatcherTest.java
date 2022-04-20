/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2017, 2018, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class WildcardPatternMatcherTest extends TestCase {

  private static final int END_TEST_SIZE = 100000; // 1000000

  private static final String longString;
  private static final String craftedLongString;
  static {
    // Got stupid slow at 10000
    // At 1000, simple matcher was 4,000 times as fast as the regular expressions
    StringBuilder longSB = new StringBuilder();
    for (int i=0; i<100; i++) {
      longSB.append("long string ");
    }
    longString = longSB.toString();
    longSB.setLength(0);
    for (int i=0; i<100; i++) {
      longSB.append("jpepngifitif");
    }
    // ".*(jpg|jpeg|png|gif|tiff)$",
    craftedLongString = longSB.toString();
  }

  public WildcardPatternMatcherTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(WildcardPatternMatcherTest.class);
  }

  @SuppressWarnings("UseOfSystemOutOrSystemErr")
  private void doTestPerformance(String description, String patterns, String regexp, String text, boolean expectedResult) {
    System.out.println(description);
    for (int testSize = 1; testSize <= END_TEST_SIZE; testSize *= 100) {
      // Time make matcher
      long startNanos = System.nanoTime();
      WildcardPatternMatcher wcMatcher = WildcardPatternMatcher.compile(patterns);
      long timeNanos = System.nanoTime() - startNanos;
      System.out.println("    " + testSize + ": Created WildcardPatternMatcher in " + BigDecimal.valueOf(timeNanos, 3) + " \u00B5s");

      // Time make pattern
      startNanos = System.nanoTime();
      Pattern rePattern = Pattern.compile(regexp);
      timeNanos = System.nanoTime() - startNanos;
      System.out.println("    " + testSize + ": Created Pattern                in " + BigDecimal.valueOf(timeNanos, 3) + " \u00B5s");

      // Time use matcher
      startNanos = System.nanoTime();
      for (int i = 0; i < testSize; i++) {
        assertEquals(
          expectedResult,
          wcMatcher.isMatch(text)
        );
      }
      long wcTimeNanos = System.nanoTime() - startNanos;
      System.out.println("    " + testSize + ": Uses of WildcardPatternMatcher in " + BigDecimal.valueOf(wcTimeNanos/1000, 3) + " ms");

      // Time use pattern
      startNanos = System.nanoTime();
      for (int i = 0; i < testSize; i++) {
        assertEquals(
          expectedResult,
          rePattern.matcher(text).matches()
        );
      }
      timeNanos = System.nanoTime() - startNanos;
      System.out.println("    " + testSize + ": Uses of Pattern                in " + BigDecimal.valueOf(timeNanos/1000, 3) + " ms (" + ((float)((double)timeNanos / (double)wcTimeNanos)) + ')');
    }
  }

  public void testMatchNoneShortString() {
    doTestPerformance(
      "Match none, short string",
      "",
      "a^",
      "Short string",
      false
    );
  }

  public void testMatchNoneLongString() {
    doTestPerformance(
      "Match none, long string",
      "",
      "a^",
      longString,
      false
    );
  }

  public void testMatchAllShortString() {
    doTestPerformance(
      "Match all, short string",
      "*",
      ".*",
      "Short string",
      true
    );
  }

  public void testMatchAllLongString() {
    doTestPerformance(
      "Match all, long string",
      "*",
      ".*",
      longString,
      true
    );
  }

  public void testMatchPrefix() {
    doTestPerformance(
      "Match prefix",
      "something*",
      "^something.*",
      "something that matches",
      true
    );
  }

  public void testNoMatchPrefix() {
    doTestPerformance(
      "No match prefix",
      "something*",
      "^something.*",
      "Not something that matches",
      false
    );
  }

  public void testMatchSuffix() {
    doTestPerformance(
      "Match suffix",
      "*something",
      ".*something$",
      "matches this something",
      true
    );
  }

  public void testNoMatchSuffix() {
    doTestPerformance(
      "No match suffix",
      "*something",
      ".*something$",
      "not matches this something else",
      false
    );
  }

  public void testNoMatchSuffixLong() {
    doTestPerformance(
      "No match suffix, long string",
      "*something",
      ".*something$",
      longString,
      false
    );
  }

  public void testMatchPrefixSuffix() {
    doTestPerformance(
      "Match prefix and suffix",
      "blargs*something",
      "^blargs.*something$",
      "blargssomething",
      true
    );
  }

  public void testNoMatchPrefixSuffix1() {
    doTestPerformance(
      "No match prefix and suffix 1",
      "blargs*something",
      "^blargs.*something$",
      " blargssomething",
      false
    );
  }

  public void testNoMatchPrefixSuffix2() {
    doTestPerformance(
      "No match prefix and suffix 2",
      "blargs*something",
      "^blargs.*something$",
      "blargssomething ",
      false
    );
  }

  public void testNoMatchPrefixSuffix3() {
    doTestPerformance(
      "No match prefix and suffix 3",
      "blargs*something",
      "^blargs.*something$",
      "blargsomething",
      false
    );
  }

  public void testMatchTestPng() {
    doTestPerformance(
      "Match test.png vs *.jpg, *.jpeg, *.png, *.gif, *.tiff",
      "*.jpg, *.jpeg, *.png, *.gif, *.tiff",
      ".*(jpg|jpeg|png|gif|tiff)$",
      "test.png",
      true
    );
  }

  public void testNoMatchTestDoc() {
    doTestPerformance(
      "Match test.doc vs *.jpg, *.jpeg, *.png, *.gif, *.tiff",
      "*.jpg, *.jpeg, *.png, *.gif, *.tiff",
      ".*(jpg|jpeg|png|gif|tiff)$",
      "test.doc",
      false
    );
  }

  public void testNoMatchTestGifTxt() {
    doTestPerformance(
      "Match test.gif.txt vs *.jpg, *.jpeg, *.png, *.gif, *.tiff",
      "*.jpg, *.jpeg, *.png, *.gif, *.tiff",
      ".*(jpg|jpeg|png|gif|tiff)$",
      "test.gif.txt",
      false
    );
  }

  public void testNoMatchTestGIF() {
    doTestPerformance(
      "Match test.GIF vs *.jpg, *.jpeg, *.png, *.gif, *.tiff",
      "*.jpg, *.jpeg, *.png, *.gif, *.tiff",
      ".*(jpg|jpeg|png|gif|tiff)$",
      "test.GIF",
      false
    );
  }

  public void testMatchTestTiff() {
    doTestPerformance(
      "Match test.tiff vs *.jpg, *.jpeg, *.png, *.gif, *.tiff",
      "*.jpg, *.jpeg, *.png, *.gif, *.tiff",
      ".*(jpg|jpeg|png|gif|tiff)$",
      "test.tiff",
      true
    );
  }

  /*
   * This crafted string is indeed slow in regex, so slow that I'll leave this
   * test commented-out.
   *
   * 1000000: Uses of WildcardPatternMatcher in 52.211 ms
   * 1000000: Uses of Pattern                in 102805.092 ms (1969.0042)
   *
  public void testNoMatchMultipleExtensionsCraftedLong() {
    doTestPerformance(
      "No match vs *.jpg, *.jpeg, *.png, *.gif, *.tiff, crafted long string",
      "*.jpg, *.jpeg, *.png, *.gif, *.tiff",
      ".*(jpg|jpeg|png|gif|tiff)$",
      craftedLongString,
      false
    );
  }
   */

  /*
   * This is indeed slow, but regex only 3.9290571 slower than wildcard.
   * Leaving commented-out for routine test speed.
   *
   * 1000000: Uses of WildcardPatternMatcher in 15758.539 ms
   * 1000000: Uses of Pattern                in 61916.204 ms (3.9290571)
   *
  public void testNoMatchCrafted() {
    doTestPerformance(
      "No match vs crafted",
      "*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa*",
      ".*aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.*",
      " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
      + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
      + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
      + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
      + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
      + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
      + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa ",
      false
    );
  }
   */

  /*
   * This is indeed slow, but regex only 3.2906272 slower than wildcard.
   * Leaving commented-out for routine test speed.
   *
   *1000000: Uses of WildcardPatternMatcher in 13691.655 ms
   *1000000: Uses of Pattern                in 45054.135 ms (3.2906272)
   *
  public void testNoMatchCraftedLonger() {
    int size = 200;
    StringBuilder sb = new StringBuilder(size * 2);
    for (int i = 0; i < size; i++) sb.append('a');
    String findme = sb.substring(0, size);
    sb.setCharAt(size - 1, ' ');
    for (int i = 0; i < size; i++) sb.append(' ');
    String findin = sb.toString();
    doTestPerformance(
      "No match vs crafted longer",
      "*" + findme + "*",
      ".*" + findme + "*",
      findin,
      false
    );
  }
   */
}
