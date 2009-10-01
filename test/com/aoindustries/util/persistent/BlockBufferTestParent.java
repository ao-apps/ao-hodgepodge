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

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import junit.framework.TestCase;

/**
 * @author  AO Industries, Inc.
 */
abstract public class BlockBufferTestParent extends TestCase {

    public BlockBufferTestParent(String testName) {
        super(testName);
    }

    private static final Random random = new SecureRandom();

    abstract public PersistentBlockBuffer getBlockBuffer() throws IOException;
    abstract public long getAllocationSize(Random random) throws IOException;

    public void testAllocateDeallocate() throws Exception {
        PersistentBlockBuffer blockBuffer = getBlockBuffer();
        try {
            Set<Long> allocatedIds = new HashSet<Long>();
            for(int c=0;c<100;c++) {
                if(((c+1)%10)==0) System.out.println(getClass()+": testAllocateDeallocate: Test loop "+(c+1)+" of 100");
                // Allocate some blocks, must not return duplicate ids.
                for(int d=0;d<1000;d++) {
                    assertTrue(allocatedIds.add(blockBuffer.allocate(getAllocationSize(random))));
                }

                // Iterate the block ids.  Each must be allocated.  All allocated must
                // be returned once and only once.
                Set<Long> notReturnedIds = new HashSet<Long>(allocatedIds);
                Iterator<Long> iter = blockBuffer.iterateBlockIds();
                while(iter.hasNext()) {
                    Long id = iter.next();
                    assertTrue(notReturnedIds.remove(id));
                }
                assertTrue(notReturnedIds.isEmpty());

                // Randomly deallocate 900 of the entries
                List<Long> ids = new ArrayList<Long>(allocatedIds);
                Collections.shuffle(ids, random);
                for(int d=0;d<500;d++) {
                    long id = ids.get(d);
                    blockBuffer.deallocate(id);
                    allocatedIds.remove(id);
                }
            }
        } finally {
            blockBuffer.close();
        }
    }
}
