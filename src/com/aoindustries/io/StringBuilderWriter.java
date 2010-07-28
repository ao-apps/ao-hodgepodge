/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010  AO Industries, Inc.
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

import java.io.Writer;

/**
 * A <code>Writer</code> that buffers its content into a <code>StringBuilder</code>.
 * This implementation is completely unsynchronized.  Every method of <code>Writer</code>
 * that uses <code>lock</code> has been overridden to ensure this.
 *
 * @author  AO Industries, Inc.
 */
public class StringBuilderWriter extends Writer {

    private StringBuilder buffer;

    public StringBuilderWriter() {
        buffer = new StringBuilder();
    }

    public StringBuilderWriter(int initialCapacity) {
        buffer = new StringBuilder(initialCapacity);
    }

    public StringBuilderWriter(StringBuilder buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int c) {
        buffer.append((char)c);
    }

    @Override
    public void write(char cbuf[]) {
        buffer.append(cbuf);
    }

    @Override
    public void write(char cbuf[], int off, int len) {
        buffer.append(cbuf, off, len);
    }

    @Override
    public void write(String str) {
        buffer.append(str);
    }

    @Override
    public void write(String str, int off, int len) {
        buffer.append(str, off, off+len);
    }

    @Override
    public StringBuilderWriter append(CharSequence csq) {
        buffer.append(csq);
        return this;
    }

    @Override
    public StringBuilderWriter append(CharSequence csq, int start, int end) {
        buffer.append(csq, start, end);
        return this;
    }

    @Override
    public StringBuilderWriter append(char c) {
        buffer.append(c);
        return this;
    }

    /**
     * Does nothing on flush.
     */
    @Override
    public void flush() {
        // Do nothing
    }

    /**
     * Does nothing on close.
     */
    @Override
    public void close() {
        // Do nothing
    }

    /**
     * Gets the <code>StringBuilder</code> used as the buffer.
     */
    public StringBuilder getBuffer() {
        return buffer;
    }

    /**
     * Gets the captured body as a String.
     * This is equivalent to <code>getBuffer().toString()</code>.
     */
    @Override
    public String toString() {
        return buffer.toString();
    }
}
