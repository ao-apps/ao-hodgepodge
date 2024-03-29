/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2016, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.ws;

import com.aoapps.lang.io.IoUtils;
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

  /**
   * A fast pseudo-random number generator for non-cryptographic purposes.
   */
  private static final Random fastRandom = new Random(IoUtils.bufferToLong(new SecureRandom().generateSeed(Long.BYTES)));

  public WsEncoderTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(WsEncoderTest.class);
  }

  public void testEncodeDecode() {
    StringBuilder sb = new StringBuilder();
    for (int c = 0; c < 1000; c++) {
      sb.setLength(0);
      for (int d = 0; d < 100; d++) {
        sb.append((char) fastRandom.nextInt(Character.MAX_VALUE + 1));
      }
      String value = sb.toString();
      String encoded = WsEncoder.encode(value);
      String decoded = WsEncoder.decode(encoded);
      assertEquals(value, decoded);
    }
  }
}
