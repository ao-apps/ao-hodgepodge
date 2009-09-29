/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.util.persistent;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the <code>PersistentCollections</code> class.
 *
 * @author  AO Industries, Inc.
 */
public class PersistentCollectionsTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(PersistentCollectionsTest.class);
        return suite;
    }

    public PersistentCollectionsTest(String testName) {
        super(testName);
    }

    public void testGetPersistentBuffer() throws Exception {
        PersistentBuffer smallBuffer = PersistentCollections.getPersistentBuffer(1L<<20);
        smallBuffer.close();

        PersistentBuffer largeBuffer = PersistentCollections.getPersistentBuffer(Long.MAX_VALUE);
        largeBuffer.close();
    }
}
