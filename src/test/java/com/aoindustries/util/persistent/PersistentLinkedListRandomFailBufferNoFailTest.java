/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011  AO Industries, Inc.
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

import java.io.File;
import java.io.RandomAccessFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the <code>RandomFailBuffer</code> in no-fail mode.
 *
 * @author  AO Industries, Inc.
 */
public class PersistentLinkedListRandomFailBufferNoFailTest extends PersistentLinkedListTestParent {

    public static Test suite() {
        TestSuite suite = new TestSuite(PersistentLinkedListRandomFailBufferNoFailTest.class);
        return suite;
    }

    public PersistentLinkedListRandomFailBufferNoFailTest(String testName) {
        super(testName);
    }

    @Override
    protected PersistentBuffer getPersistentBuffer(File tempFile, ProtectionLevel protectionLevel) throws Exception {
        return new RandomFailBuffer(PersistentCollections.getPersistentBuffer(new RandomAccessFile(tempFile, "rw"), protectionLevel, Long.MAX_VALUE), false);
    }
}
