/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015  AO Industries, Inc.
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

import java.security.SecureRandom;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class StringUtilityTest extends TestCase {

    public StringUtilityTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(StringUtilityTest.class);
    }

    private static final Random random = new SecureRandom();

    public void testConvertToFromHexInt() {
		for(int i=0; i<1000; i++) {
			int before = random.nextInt();
			int after = StringUtility.convertIntArrayFromHex(StringUtility.convertToHex(before).toCharArray());
			assertEquals(before, after);
		}
    }

	public void testConvertToFromHexLong() {
		for(int i=0; i<1000; i++) {
			long before = random.nextLong();
			long after = StringUtility.convertLongArrayFromHex(StringUtility.convertToHex(before).toCharArray());
			assertEquals(before, after);
		}
    }
}
