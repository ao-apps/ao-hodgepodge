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

import java.io.DataOutput;
import java.io.IOException;

/**
 * Buffer utilities.
 *
 * @author  AO Industries, Inc.
 */
class Utils {

    static void longToBuffer(long l, byte[] ioBuffer, int off) {
        ioBuffer[off] = (byte)(l >>> 56);
        ioBuffer[off+1] = (byte)(l >>> 48);
        ioBuffer[off+2] = (byte)(l >>> 40);
        ioBuffer[off+3] = (byte)(l >>> 32);
        ioBuffer[off+4] = (byte)(l >>> 24);
        ioBuffer[off+5] = (byte)(l >>> 16);
        ioBuffer[off+6] = (byte)(l >>> 8);
        ioBuffer[off+7] = (byte)l;
    }

    static long bufferToLong(byte[] ioBuffer, int off) {
        return
              ((ioBuffer[off]&255L) << 56)
            + ((ioBuffer[off+1]&255L) << 48)
            + ((ioBuffer[off+2]&255L) << 40)
            + ((ioBuffer[off+3]&255L) << 32)
            + ((ioBuffer[off+4]&255L) << 24)
            + ((ioBuffer[off+5]&255L) << 16)
            + ((ioBuffer[off+6]&255L) << 8)
            + (ioBuffer[off+7]&255L)
        ;
    }

    static void intToBuffer(int i, byte[] ioBuffer, int off) {
        ioBuffer[off++] = (byte)(i >>> 24);
        ioBuffer[off++] = (byte)(i >>> 16);
        ioBuffer[off++] = (byte)(i >>> 8);
        ioBuffer[off] = (byte)i;
    }

    static int bufferToInt(byte[] ioBuffer, int off) {
        return
              ((ioBuffer[off]&255) << 24)
            + ((ioBuffer[off+1]&255) << 16)
            + ((ioBuffer[off+2]&255) << 8)
            + (ioBuffer[off+3]&255)
        ;
    }

    private static final byte[] zeros = new byte[4096];

    /**
     * Writes the requested number of zeros to the provided output.
     */
    static void fillZeros(DataOutput out, long count) throws IOException {
        if(count<0) throw new IllegalArgumentException("count<0: "+count);
        while(count>4096) {
            out.write(zeros, 0, 4096);
            count -= 4096;
        }
        if(count>0) out.write(zeros, 0, (int)count);
    }

    private Utils() {
    }
}
