/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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
package com.aoindustries.security;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the Identifier class.
 *
 * @author  AO Industries, Inc.
 */
public class IdentifierTest extends TestCase {

    public IdentifierTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IdentifierTest.class);
        return suite;
    }

    public void testToStringValueOfEquals() {
        for(int i=0; i<100000; i++) {
            Identifier i1 = new Identifier();
            String s = i1.toString();
            Identifier i2 = Identifier.valueOf(s);
            /*
            System.out.print(Long.toHexString(i1.getHi()));
            System.out.println(Long.toHexString(i1.getLo()));
            System.out.print(Long.toHexString(i2.getHi()));
            System.out.println(Long.toHexString(i2.getLo()));
             */
            assertEquals(i1, i2);
        }
    }
}
