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

import java.io.File;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the <code>LinkedFileList</code> against the standard <code>LinkedList</code>
 * by performing equal, random actions on each and ensuring equal results.
 *
 * @author  AO Industries, Inc.
 */
public class LargeMappedPersistentLinkedListTest extends PersistentLinkedListTestParent {

    public static Test suite() {
        TestSuite suite = new TestSuite(LargeMappedPersistentLinkedListTest.class);
        return suite;
    }

    public LargeMappedPersistentLinkedListTest(String testName) {
        super(testName);
    }

    protected PersistentBuffer getPersistentBuffer(File tempFile) throws Exception {
        return new LargeMappedPersistentBuffer(tempFile);
    }

    /**
     * Test larger files.
     */
    /*
    public void testLargeList() throws Exception {
        File tempFile = File.createTempFile("LinkedFileListTest", null);
        tempFile.deleteOnExit();
        PersistentLinkedList<byte[]> linkedFileList = new PersistentLinkedList<byte[]>(getPersistentBuffer(tempFile), false, false);
        try {
            byte[] buff = new byte[1024*1024];
            System.out.println("Filling list");
            for(int c=0;c<1024;c++) {
                linkedFileList.add(buff);
            }
            System.out.println("Testing as circular list");
            for(int c=0;c<1000000;c++) {
                linkedFileList.removeLast();
                linkedFileList.addFirst(buff);
            }
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
        }
    }*/
}
