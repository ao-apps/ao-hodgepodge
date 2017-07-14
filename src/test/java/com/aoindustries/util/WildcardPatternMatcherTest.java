/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2017  AO Industries, Inc.
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
import java.util.regex.Pattern;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class WildcardPatternMatcherTest extends TestCase {

	public WildcardPatternMatcherTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(WildcardPatternMatcherTest.class);
	}

	private void doTestPerformance(String description, String patterns, String regexp, String text, boolean expectedResult) {
		System.out.println(description);
		final int endTestSize = 1000000;
		for(int testSize = 1; testSize <= endTestSize; testSize *= 100) {
			// Time make matcher
			long startNanos = System.nanoTime();
			WildcardPatternMatcher wcMatcher = WildcardPatternMatcher.getInstance(patterns);
			long timeNanos = System.nanoTime() - startNanos;
			System.out.println("    " + testSize + ": Created WildcardPatternMatcher in " + BigDecimal.valueOf(timeNanos, 3) + " \u00B5s");
			
			// Time make pattern
			startNanos = System.nanoTime();
			Pattern rePattern = Pattern.compile(regexp);
			timeNanos = System.nanoTime() - startNanos;
			System.out.println("    " + testSize + ": Created Pattern in " + BigDecimal.valueOf(timeNanos, 3) + " \u00B5s");

			// Time use matcher
			startNanos = System.nanoTime();
			for(int i = 0; i < testSize; i++) {
				assertEquals(
					expectedResult,
					wcMatcher.isMatch(text)
				);
			}
			timeNanos = System.nanoTime() - startNanos;
			System.out.println("    " + testSize + ": Uses of WildcardPatternMatcher in " + BigDecimal.valueOf(timeNanos/1000, 3) + " ms");

			// Time use pattern
			startNanos = System.nanoTime();
			for(int i = 0; i < testSize; i++) {
				assertEquals(
					expectedResult,
					rePattern.matcher(text).matches()
				);
			}
			timeNanos = System.nanoTime() - startNanos;
			System.out.println("    " + testSize + ": Uses of Pattern in " + BigDecimal.valueOf(timeNanos/1000, 3) + " ms");
		}
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
		StringBuilder longSB = new StringBuilder();
		// Got stupid slow at 10000
		// At 1000, simple matcher was 4,000 times as fast as the regular expresions
		for(int i=0; i<100; i++) longSB.append("long string ");
		String longString = longSB.toString();
		doTestPerformance(
			"Match all, long string",
			"*",
			".*",
			longString,
			true
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
}
