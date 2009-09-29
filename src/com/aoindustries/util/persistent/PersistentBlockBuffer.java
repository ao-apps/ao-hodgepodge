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
 * A persistent set of blocks of arbitrary data.  Each block may be any
 * 64-bit size.  All implementations should have a relatively efficient
 * iteration in forward direction.  Some implementations may also have
 * efficient indexed access, and will implement the <code>RandomAccessPersistentBlockBuffer</code>
 * interface.
 *
 * @see  RandomAccessPersistentBlockBuffer
 *
 * @author  AO Industries, Inc.
 */
public interface PersistentBlockBuffer {

    /**
     * Checks if this buffer is closed.
     */
    boolean isClosed();

    /**
     * Closes this buffer.
     */
    void close() throws IOException;

    /**
     * Gets the read-only flag for this buffer.
     */
    boolean isReadOnly();

    /**
     * Ensures that all writes before this barrier occur before all writes after
     * this barrier.  If <code>force</code> is <code>true</code>, will also
     * commit to physical media synchronously before returning.
     */
    void barrier(boolean force) throws IOException;

    /**
     * Iterates over the block IDs in no specific order.
     */
    Iterator<Long> iterateBlockIds() throws IOException;
}
