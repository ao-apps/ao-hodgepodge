/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
import java.util.Iterator;

/**
 * <p>
 * Treats a <code>PersistentBuffer</code> as a set of allocatable blocks.
 * Each block is stored in a block of fixed size.  This provides fast
 * random access and is marked with the <code>RandomAccess</code> interface.
 * </p>
 * <p>
 * Free space maps are ... TODO
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class FixedPersistentBlockBuffer extends AbstractPersistentBlockBuffer implements RandomAccessPersistentBlockBuffer {

    private final long blockSize;

    /**
     * Creates a persistent buffer with the provided block size.  There may be
     * performance advantages to block sizes that match or are multiples of the
     * system page size.  For smaller block sizes, there may also be reliability
     * advantages to block sizes that are fractions of the system page size
     * or the physical media block size.  A good overall approach would be to
     * select even powers of two (1, 2, 4, 8, ...).
     *
     * @param pbuffer
     * @param blockSize
     */
    public FixedPersistentBlockBuffer(PersistentBuffer pbuffer, long blockSize) {
        super(pbuffer);
        this.blockSize = blockSize;
    }

    public Iterator<Long> iterateBlockIds() {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }

    public long getBlockCount() throws IOException {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }

    public long getBlockId(long index) throws IOException {
        throw new UnsupportedOperationException("TODO: Not supported yet.");
    }
}
