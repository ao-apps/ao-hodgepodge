/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2016  AO Industries, Inc.
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
package com.aoindustries.ws;

import java.security.SecureRandom;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the WsEncoder for binary transparency.
 *
 * @author  AO Industries, Inc.
 */
public class WsEncoderTest extends TestCase {

	public WsEncoderTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(WsEncoderTest.class);
	}

	public void testEncodeDecode() {
		Random random = new SecureRandom();
		StringBuilder SB = new StringBuilder();
		for(int c=0;c<1000;c++) {
			SB.setLength(0);
			for(int d=0;d<100;d++) SB.append((char)random.nextInt(Character.MAX_VALUE+1));
			String value = SB.toString();
			String encoded = WsEncoder.encode(value);
			String decoded = WsEncoder.decode(encoded);
			assertEquals(value, decoded);
		}
	}
}
