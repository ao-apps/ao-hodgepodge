package com.aoindustries.io;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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

    public void write(char cbuf[], int off, int len) {
        buffer.append(cbuf, off, len);
    }

    @Override
    public void write(String str) {
        buffer.append(str);
    }

    @Override
    public void write(String str, int off, int len) {
        buffer.append((CharSequence)str, off, off+len);
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
    public void flush() {
        // Do nothing
    }

    /**
     * Does nothing on close.
     */
    public void close() {
        // Do nothing
    }

    /**
     * Gets the <code>StringBuilder</code> used as the buffer.
     */
    public StringBuilder getBuffer() {
        return buffer;
    }
}
