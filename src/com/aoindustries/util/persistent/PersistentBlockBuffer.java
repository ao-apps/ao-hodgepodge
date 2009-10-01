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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
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
     * Gets the protection level currently implemented by the buffer.
     *
     * @see  #barrier(boolean)
     */
    ProtectionLevel getProtectionLevel();

    /**
     * Ensures that all writes before this barrier occur before all writes after
     * this barrier.  If <code>force</code> is <code>true</code>, will also
     * commit to physical media synchronously before returning.  This request
     * may be ignored depending on the current protection level.
     *
     * @see  #getProtectionLevel()
     */
    void barrier(boolean force) throws IOException;

    /**
     * <p>
     * Iterates over the allocated block IDs in no specific order, with one
     * exception: the first block allocated must be the first block iterated.
     * This block may contain critical higher-level data structure meta data.
     * If all blocks are deallocated, then the first one added has this same
     * requirement.
     * </p>
     * <p>
     * The <code>remove()</code> method may be used from the iterator in order
     * to deallocate a block.  The block allocation should not be modified
     * during the iteration through any means other than the iterator itself.
     * An attempt will be made to throw <code>ConcurrentModificationException</code>
     * in this case, but this is only intended to catch bugs.
     * </p>
     */
    Iterator<Long> iterateBlockIds() throws IOException;

    /**
     * Allocates a new block buffer that is at least as large as the requested space.
     * The id should always be >=0, higher level data structures may use the
     * negative values for other purposes, such as indicating <code>null</code>
     * with <code>-1</code>.
     */
    long allocate(long minimumSize) throws IOException;

    /**
     * Deallocates the block with the provided id.  The ids of other blocks
     * will not be altered.  The space may later be reallocated with the same,
     * or possibly different id.  The space may also be reclaimed.
     *
     * @throws IllegalStateException if the block is not allocated.
     */
    void deallocate(long id) throws IOException, IllegalStateException;

    /**
     * Gets the block size for the provided id.
     */
    long getBlockSize(long id) throws IOException;

    /**
     * Gets bytes from this block.  Bounds checking is performed.
     *
     * @throws BufferUnderflowException
     */
    void get(long id, long offset, byte[] buff, int off, int len) throws IOException, BufferUnderflowException;

    /**
     * Gets an integer from this block.  Bounds checking is performed.
     *
     * @throws BufferUnderflowException
     */
    int getInt(long id, long offset) throws IOException, BufferUnderflowException;

    /**
     * Gets a long from this block.  Bounds checking is performed.
     *
     * @throws BufferUnderflowException
     */
    long getLong(long id, long offset) throws IOException, BufferUnderflowException;

    /**
     * Gets an input stream that reads from this buffer.  Bounds checking is performed.
     *
     * @throws BufferUnderflowException
     */
    InputStream getInputStream(long id, long offset, long length) throws IOException, BufferUnderflowException;

    /**
     * Puts bytes to this block.  Bounds checking is performed.
     *
     * @throws BufferOverflowException
     */
    void put(long id, long offset, byte[] buff, int off, int len) throws IOException, BufferOverflowException;

    /**
     * Puts an integer to this block.  Bounds checking is performed.
     *
     * @throws BufferOverflowException
     */
    void putInt(long id, long offset, int value) throws IOException, BufferOverflowException;

    /**
     * Puts a long to this block.  Bounds checking is performed.
     *
     * @throws BufferOverflowException
     */
    void putLong(long id, long offset, long value) throws IOException, BufferOverflowException;

    /**
     * Gets an output stream that writes to this buffer.  Bounds checking is performed.
     *
     * @throws BufferOverflowException
     */
    OutputStream getOutputStream(long id, long offset, long length) throws IOException, BufferOverflowException;
}
