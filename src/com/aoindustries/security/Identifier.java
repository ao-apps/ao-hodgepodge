/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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
package com.aoindustries.security;

import com.aoindustries.math.LongLong;
import com.aoindustries.util.persistent.PersistentCollections;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;

/**
 * A 128-bit random identifier internally stored as two long values.
 *
 * @author  AO Industries, Inc.
 */
public class Identifier implements Serializable, Comparable<Identifier> {

    private static final long serialVersionUID = 1L;

    /**
     * @see  #toString()
     */
    public static Identifier valueOf(String encoded) throws IllegalArgumentException {
        return new Identifier(encoded);
    }

    private static final char[] characters = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '_'
    };

    /**
     * Gets the character for the low-order 6 bits of a long value.
     */
    private static char getCharacter(long value) {
        return characters[((int)value) & 0x3f];
        /*
        int lowBits = ((int)value) & 0x3f;
        if(lowBits>=0 && lowBits<=25) return (char)(lowBits + 'A');
        if(lowBits>=26 && lowBits<=51) return (char)(lowBits - 26 + 'a');
        if(lowBits>=52 && lowBits<=61) return (char)(lowBits - 52 + '0');
        if(lowBits==62) return '*';
        assert lowBits==63;
        return '_';
         */
    }

    /**
     * Gets the value for a character as a long.
     */
    private static long getValue(char ch) {
        if(ch>='A' && ch<='Z') return (long)(ch - 'A');
        if(ch>='a' && ch<='z') return (long)(ch - 'a' + 26);
        if(ch>='0' && ch<='9') return (long)(ch - '0' + 52);
        if(ch=='*') return 62;
        if(ch=='_') return 63;
        throw new IllegalArgumentException(Character.toString(ch));
    }

    private static final Random random = new SecureRandom();

    private final long hi;
    private final long lo;

    /**
     * Creates a new, random Identifier using the default SecureRandom instance.
     */
    public Identifier() {
        this(random);
    }

    /**
     * Creates a new, random Identifier using the provided Random source.
     */
    public Identifier(Random random) {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        hi = PersistentCollections.bufferToLong(bytes);
        lo = PersistentCollections.bufferToLong(bytes, 8);
    }

    public Identifier(long hi, long lo) {
        this.hi = hi;
        this.lo = lo;
    }

    /**
     * @see  #toString()
     */
    public Identifier(String encoded) throws IllegalArgumentException {
        if(encoded.length()!=22) throw new IllegalArgumentException();
        // Only two bits encoded in the top-most character
        long topBits = getValue(encoded.charAt(0));
        if(topBits>3) throw new IllegalArgumentException();
        long midBits = getValue(encoded.charAt(11));
        hi =
            (topBits << 62)
            | (getValue(encoded.charAt(1)) << 56)
            | (getValue(encoded.charAt(2)) << 50)
            | (getValue(encoded.charAt(3)) << 44)
            | (getValue(encoded.charAt(4)) << 38)
            | (getValue(encoded.charAt(5)) << 32)
            | (getValue(encoded.charAt(6)) << 26)
            | (getValue(encoded.charAt(7)) << 20)
            | (getValue(encoded.charAt(8)) << 14)
            | (getValue(encoded.charAt(9)) <<  8)
            | (getValue(encoded.charAt(10)) << 2)
            | (midBits >>> 4)
        ;
        lo =
            (midBits << 60)
            | (getValue(encoded.charAt(12)) << 54)
            | (getValue(encoded.charAt(13)) << 48)
            | (getValue(encoded.charAt(14)) << 42)
            | (getValue(encoded.charAt(15)) << 36)
            | (getValue(encoded.charAt(16)) << 30)
            | (getValue(encoded.charAt(17)) << 24)
            | (getValue(encoded.charAt(18)) << 18)
            | (getValue(encoded.charAt(19)) << 12)
            | (getValue(encoded.charAt(20)) <<  6)
            | getValue(encoded.charAt(21))
        ;
    }

    @Override
    public boolean equals(Object O) {
        if(!(O instanceof Identifier)) return false;
        Identifier other = (Identifier)O;
        return hi==other.hi && lo==other.lo;
    }

    @Override
    public int hashCode() {
        // The values should be well distributed, any set of 32 bits should be equally good.
        return (int)lo;
    }

    /**
     * The external representation is a string of characters similar to base-64 in
     * that 6 bits are encoded in each character, with the exception that '+' is
     * replaced with '*' and '/' is replaced with '_' to be compatible with URL
     * parameter values without further encoding.
     */
    @Override
    public String toString() {
        return new String(
            new char[] {
                getCharacter(hi >>> 62),
                getCharacter(hi >>> 56),
                getCharacter(hi >>> 50),
                getCharacter(hi >>> 44),
                getCharacter(hi >>> 38),
                getCharacter(hi >>> 32),
                getCharacter(hi >>> 26),
                getCharacter(hi >>> 20),
                getCharacter(hi >>> 14),
                getCharacter(hi >>> 8),
                getCharacter(hi >>> 2),
                getCharacter((lo >>> 60) | ((hi & 3) << 4)),
                getCharacter(lo >>> 54),
                getCharacter(lo >>> 48),
                getCharacter(lo >>> 42),
                getCharacter(lo >>> 36),
                getCharacter(lo >>> 30),
                getCharacter(lo >>> 24),
                getCharacter(lo >>> 18),
                getCharacter(lo >>> 12),
                getCharacter(lo >>> 6),
                getCharacter(lo)
            }
        );
    }

    /**
     * Unsigned ordering.
     */
    @Override
    public int compareTo(Identifier other) {
        int diff = LongLong.compareUnsigned(hi, other.hi);
        if(diff != 0) return diff;
        return LongLong.compareUnsigned(lo, other.lo);
    }
    
    public long getHi() {
        return hi;
    }
    
    public long getLo() {
        return lo;
    }
}
