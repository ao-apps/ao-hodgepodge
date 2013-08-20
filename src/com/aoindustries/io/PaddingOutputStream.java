/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Pads the last block with the necessary number of bytes before closing the stream.
 * If padding is necessary without closing, use <code>finish</code>.
 */
public class PaddingOutputStream extends FilterOutputStream {

    private final int blockSize;
    private final byte padding;

    private long byteCount = 0;

    public PaddingOutputStream(OutputStream out, int blockSize, byte padding) {
        super(out);
        this.blockSize = blockSize;
        this.padding = padding;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        byteCount++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        byteCount += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        byteCount += len;
    }

    /**
     * Pads and flushes without closing the underlying stream.
     */
    public void finish() throws IOException {
        int lastBlockSize = (int)(byteCount % blockSize);
        if(lastBlockSize!=0) {
            while(lastBlockSize<blockSize) {
                out.write(padding);
                byteCount++;
                lastBlockSize++;
            }
        }
        out.flush();
    }

    /**
     * Pads, flushes, and closes the underlying stream.
     */
    @Override
    public void close() throws IOException {
        try {
            finish();
        } catch (IOException ignored) {
        }
        out.close();
    }
}
