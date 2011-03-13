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
import java.io.IOException;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author  AO Industries, Inc.
 */
public class BlockBufferSingleBitmapFixedTest extends BlockBufferTestParent {

    public static Test suite() {
        TestSuite suite = new TestSuite(BlockBufferSingleBitmapFixedTest.class);
        return suite;
    }

    public BlockBufferSingleBitmapFixedTest(String testName) {
        super(testName);
    }

    public PersistentBuffer getBuffer(File tempFile, ProtectionLevel protectionLevel) throws IOException {
        return new SparseBuffer(protectionLevel);
    }

    public PersistentBlockBuffer getBlockBuffer(PersistentBuffer pbuffer) throws IOException {
        return new FixedPersistentBlockBuffer(pbuffer, (1L<<30));
    }

    @Override
    public long getAllocationSize(Random random) throws IOException {
        return random.nextInt((1<<30)+1);
    }

    /**
     * This test is not compatible with non-persistent {@link SparseBuffer}
     */
    @Override
    public void testFailureRecoveryBarrier() {
        // Skip test
    }

    /**
     * This test is not compatible with non-persistent {@link SparseBuffer}
     */
    @Override
    public void testFailureRecoveryForce() {
        // Skip test
    }
}
