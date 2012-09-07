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

import static com.aoindustries.math.UnsignedLong.divide;
import static com.aoindustries.math.UnsignedLong.remainder;
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
        '0', '1', '2', '3', '4', /*'5', '6', '7', '8', '9'*/
    };

    private static final long BASE = 57;
    /**
     * Gets the character for the low-order modulus BASE a long value.
     */
    private static char getCharacter(long value) {
        //long remainder = value % BASE;
        //int index = (int)((remainder + BASE) % BASE); // Make sure is always positive
        //int index = (int)Math.abs(remainder);
        int index = (int)remainder(value, BASE);
        return characters[index];
    }

    /**
     * Gets the value for a character as a long.
     */
    private static long getValue(char ch) {
        if(ch>='A' && ch<='Z') return (long)(ch - 'A');
        if(ch>='a' && ch<='z') return (long)(ch - 'a' + 26);
        if(ch>='0' && ch<='4') return (long)(ch - '0' + 52);
        throw new IllegalArgumentException(Character.toString(ch));
    }

    /**
     * Decodes one set of 11 characters to a long.
     */
    private static long decode(String encoded) {
        assert encoded.length()==11;
        return
              getValue(encoded.charAt(0)) * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE
            + getValue(encoded.charAt(1)) * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE
            + getValue(encoded.charAt(2)) * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE
            + getValue(encoded.charAt(3)) * BASE * BASE * BASE * BASE * BASE * BASE * BASE
            + getValue(encoded.charAt(4)) * BASE * BASE * BASE * BASE * BASE * BASE
            + getValue(encoded.charAt(5)) * BASE * BASE * BASE * BASE * BASE
            + getValue(encoded.charAt(6)) * BASE * BASE * BASE * BASE
            + getValue(encoded.charAt(7)) * BASE * BASE * BASE
            + getValue(encoded.charAt(8)) * BASE * BASE
            + getValue(encoded.charAt(9)) * BASE
            + getValue(encoded.charAt(10))
        ;
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
        /*
        for(int i=0; i<16; i+=2) {
            int val = random.nextInt();
            bytes[i] = (byte)(val>>>8);
            bytes[i+1] = (byte)(val);
        }
         */
        // This seems to never give non-zero in the high range:
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
        this.hi = decode(encoded.substring(0, 11));
        this.lo = decode(encoded.substring(11));
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
     * The external representation is a string of characters encoded in base 62, with
     * the first 11 characters for "hi" and the last 11 characters for "lo".
     */
    @Override
    public String toString() {
        return new String(
            new char[] {
                getCharacter(divide(hi, BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE * BASE)),
                getCharacter(divide(hi, BASE * BASE)),
                getCharacter(divide(hi, BASE)),
                getCharacter(hi),
                getCharacter(divide(lo, BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE * BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE * BASE)),
                getCharacter(divide(lo, BASE * BASE)),
                getCharacter(divide(lo, BASE)),
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