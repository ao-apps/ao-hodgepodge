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
package com.aoindustries.io;

import java.io.File;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the <code>LinkedFileList</code> against the standard <code>LinkedList</code>
 * by performing equal, random actions on each and ensuring equal results.
 *
 * @author  AO Industries, Inc.
 */
public class LinkedFileListTest extends TestCase {

    public LinkedFileListTest(String testName) {
        super(testName);
    }

    private Random random = new SecureRandom();

    public static Test suite() {
        TestSuite suite = new TestSuite(LinkedFileListTest.class);
        return suite;
    }

    private void doTest(int numElements) throws Exception {
        File tempFile = File.createTempFile("LinkedFileListTest", null);
        tempFile.deleteOnExit();
        LinkedFileList<Integer> linkedFileList = new LinkedFileList<Integer>(tempFile, false, false);
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        try {
            // Populate the list
            for(int c=0;c<numElements;c++) {
                Integer i = random.nextInt();
                assertEquals(linkedFileList.add(i), linkedList.add(i));
            }
            // Check size match
            assertEquals(linkedFileList.size(), linkedList.size());
            if(numElements>0) {
                // Check first
                assertEquals(linkedFileList.getFirst(), linkedList.getFirst());
                // Check last
                assertEquals(linkedFileList.getLast(), linkedList.getLast());
                // Update random locations to random values
                for(int c=0;c<numElements;c++) {
                    int index = random.nextInt(numElements);
                    int newVal = random.nextInt();
                    assertEquals(linkedFileList.set(index, newVal), linkedList.set(index, newVal));
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Remove random indexes
            if(numElements>0) {
                int numRemove = random.nextInt(numElements);
                for(int c=0;c<numRemove;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    int index = random.nextInt(linkedFileList.size());
                    assertEquals(
                        linkedFileList.remove(index),
                        linkedList.remove(index)
                    );
                }
            }
            // Add random values to end
            if(numElements>0) {
                int numAdd = random.nextInt(numElements);
                for(int c=0;c<numAdd;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    int newVal = random.nextInt();
                    assertEquals(linkedFileList.add(newVal), linkedList.add(newVal));
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Add random values in middle
            if(numElements>0) {
                int numAdd = random.nextInt(numElements);
                for(int c=0;c<numAdd;c++) {
                    assertEquals(linkedFileList.size(), linkedList.size());
                    int index = random.nextInt(linkedFileList.size());
                    int newVal = random.nextInt();
                    linkedFileList.add(index, newVal);
                    linkedList.add(index, newVal);
                    assertEquals(
                        linkedFileList.remove(index),
                        linkedList.remove(index)
                    );
                }
            }
            // Check equality
            assertEquals(linkedFileList, linkedList);
            // Save and restore, checking matches
            linkedFileList.close();
            LinkedFileList<Integer> newFileList = new LinkedFileList<Integer>(tempFile, true, true);
            assertEquals(newFileList, linkedList);
        } finally {
            linkedFileList.close();
            linkedFileList = null;
            tempFile.delete();
            linkedList = null;
        }
    }

    /**
     * Adds 1000 items to the list, comparing for equality after each add.
     */
    public void testList() throws Exception {
        doTest(0);
        doTest(1);
        for(int c=0; c<10; c++) doTest(100 + random.nextInt(101));
    }
}
