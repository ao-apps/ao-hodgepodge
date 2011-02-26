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
package com.aoindustries.math;

/**
 * Math routines that check for overflow conditions.
 *
 * @author  AO Industries, Inc.
 */
public class SafeMath {

    private SafeMath() {
    }

    public static long multiply(long value1, long value2) {
        if(value1>value2) {
            long t = value1;
            value1 = value2;
            value2 = t;
        }
        if(value1<0) {
            if(value2<0) {
                if(value1>(Long.MAX_VALUE/value2)) throw new ArithmeticException("long*long overflow");
            } else if(value2>0) {
                if(value1<(Long.MIN_VALUE/value2)) throw new ArithmeticException("long*long overflow");
            } else {
                // value2==0
                return 0;
            }
        } else if(value1>0) {
            if(value1>(Long.MAX_VALUE/value2)) throw new ArithmeticException("long*long overflow");
        } else {
            // value1==0
            return 0;
        }
        return value1*value2;
    }
    /*
     * The following is an attempt to accomplish overflow checks using bit trickery only (no division)
    public static void main(String[] args) {
        for(int value1=1; value1<=16; value1++) {
            int zeros1 = Integer.numberOfLeadingZeros(value1);
            int bitCount1 = Integer.bitCount(value1);
            for(int value2=1; value2<=16; value2++) {
                int product = value1 * value2;
                int zeros2 = Integer.numberOfLeadingZeros(value2);
                int bitCount2 = Integer.bitCount(value2);
                int calcZeros = Integer.numberOfLeadingZeros(value1+1) + Integer.numberOfLeadingZeros(value2+1) + 2;
                //if(value1!=1 && (bitCount1+zeros1)==32) calcZeros--;
                //if(value2!=1 && (bitCount2+zeros2)==32) calcZeros--;
                int actualZeros = 32 + Integer.numberOfLeadingZeros(product);
                System.out.println(Integer.toBinaryString(value1)+"*"+Integer.toBinaryString(value2)+"="+Integer.toBinaryString(product)+": "+calcZeros+"->"+actualZeros);
                if(calcZeros!=actualZeros) {
                    System.out.flush();
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException err) {
                        err.printStackTrace();
                    }
                    System.err.println("Check failed");
                    System.err.println("value1="+Integer.toBinaryString(value1));
                    System.err.println("value2="+Integer.toBinaryString(value2));
                    System.err.println("calcZeros="+calcZeros);
                    System.err.println("actualZeros="+actualZeros);
                    System.err.flush();
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException err) {
                        err.printStackTrace();
                    }
                    //System.exit(1);
                }
            }
        }
    }*/
}