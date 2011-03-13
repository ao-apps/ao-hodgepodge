/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010  AO Industries, Inc.
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
package com.aoindustries.util;

import java.io.Serializable;
import org.checkthread.annotations.ThreadSafe;

/**
 * General-purpose array utilities and constants.
 *
 * @author  AO Industries, Inc.
 */
public class AoArrays {

    private AoArrays() {
    }

    public static final Serializable[] EMPTY_SERIALIZABLE_ARRAY = {};

    /**
     * Checks if the subrange of two byte arrays is equal.
     */
    @ThreadSafe
    public static boolean equals(byte[] b1, byte[] b2, int off, int len) {
        for(int end=off+len; off<end; off++) {
            if(b1[off]!=b2[off]) return false;
        }
        return true;
    }

    /**
     * Checks if the subrange of two byte arrays is equal.
     */
    @ThreadSafe
    public static boolean equals(byte[] b1, int off1, byte[] b2, int off2, int len) {
        for(int end=off1+len; off1<end; off1++, off2++) {
            if(b1[off1]!=b2[off2]) return false;
        }
        return true;
    }

    /**
     * Checks if all the values in the provided range are equal to <code>value</code>.
     */
    @ThreadSafe
    public static boolean allEquals(byte[] b, int off, int len, byte value) {
        for(int end=off+len; off<end; off++) {
            if(b[off]!=value) return false;
        }
        return true;
    }

    /**
     * Compares two byte[].  Shorter byte[] are ordered before longer when
     * the shorter is a prefix of the longer.  The comparison considers each
     * byte as a value from 0-255.
     */
    @ThreadSafe
    public static int compare(byte[] ba1, byte[] ba2) {
        int len = Math.min(ba1.length, ba2.length);
        for(int i=0; i<len; i++) {
            int b1 = ba1[i]&255;
            int b2 = ba2[i]&255;
            if(b1<b2) return -1;
            if(b1>b2) return 1;
        }
        if(ba1.length>len) return 1;
        if(ba2.length>len) return -1;
        return 0;
    }
}
