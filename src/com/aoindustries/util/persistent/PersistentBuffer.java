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

/**
 * <p>
 * A persistent buffer retains its data between uses.  They should not be used by
 * multiple virtual machines or even multiple instances objects within the same
 * virtual machine.  They are meant for persistence only, not interprocess
 * communication.
 * </p>
 * <p>
 * The ensure the data integrity of higher-level data structures, the barrier method
 * must be used.  The barrier ensures that all writes before the barrier happen before
 * all writes after the barrier.  It also accepts a parameter meaning that it should
 * also force (fsync) all writes before the barrier to physical media.  Write order
 * between <code>barrier</code> calls is not maintained.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public interface PersistentBuffer {

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
     * Gets the capacity of this buffer.
     */
    long capacity() throws IOException;

    /**
     * Sets the capacity of this buffer.  If the buffer is increased in size, the
     * new space will be zero-filled.
     */
    void setCapacity(long newCapacity) throws IOException;

    /**
     * Reads to the provided <code>byte[]</code>, starting at the provided
     * position and for the designated number of bytes.
     *
     * @exception  BufferUnderflowException on end of file
     * @exception  IOException
     */
    void get(long position, byte[] buff, int off, int len) throws IOException;

    /**
     * Reads to the provided <code>byte[]</code>, may read fewer than <code>len</code>
     * bytes, but will always reads at least one byte.  Blocks if no data is
     * available.
     *
     * @exception  BufferUnderflowException on end of file
     * @exception  IOException
     */
    int getSome(long position, byte[] buff, int off, int len) throws IOException;

    /**
     * Reads a boolean at the provided position, zero is considered <code>false</code>
     * and any non-zero value is <code>true</code>.
     */
    boolean getBoolean(long position) throws IOException;

    /**
     * Reads a byte at the provided position.
     */
    byte get(long position) throws IOException;

    /**
     * Reads an integer at the provided position.
     */
    int getInt(long position) throws IOException;

    /**
     * Reads a long at the provided position.
     */
    long getLong(long position) throws IOException;

    /**
     * Puts a single value in the buffer.
     */
    void put(long position, byte value) throws IOException;

    /**
     * Writes the bytes to the provided position.  The buffer will not be expanded
     * automatically.
     *
     * @exception  BufferOverflowException on end of file
     */
    void put(long position, byte[] buff, int off, int len) throws IOException;

    /**
     * Writes an integer at the provided position.  The buffer will not be expanded
     * automatically.
     *
     * @exception  BufferOverflowException on end of file
     */
    void putInt(long position, int value) throws IOException;

    /**
     * Writes a long at the provided position.  The buffer will not be expanded
     * automatically.
     *
     * @exception  BufferOverflowException on end of file
     */
    void putLong(long position, long value) throws IOException;

    /**
     * Ensures that all writes before this barrier occur before all writes after
     * this barrier.  If <code>force</code> is <code>true</code>, will also
     * commit to physical media synchronously before returning.
     */
    void barrier(boolean force) throws IOException;
}
