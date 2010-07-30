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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the filesystem iterator.
 *
 * @author  AO Industries, Inc.
 */
public class BigFractionTest extends TestCase {

    public BigFractionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BigFractionTest.class);
        return suite;
    }

    public void testCompareTo() {
        assertTrue(new BigFraction("1/3").compareTo(new BigFraction("2/6"))==0);
        assertTrue(new BigFraction("-1/3").compareTo(new BigFraction("-2/6"))==0);
        assertTrue(new BigFraction("1/3").compareTo(new BigFraction("1/4"))>0);
        assertTrue(new BigFraction("1/4").compareTo(new BigFraction("1/3"))<0);
    }

    public void testReduce() {
        assertEquals(
            new BigFraction("1/1"),
            new BigFraction("10/10").reduce()
        );
        assertEquals(
            new BigFraction("-1/1"),
            new BigFraction("-10/10").reduce()
        );
        assertEquals(
            new BigFraction("23/17"),
            new BigFraction("5516481/4077399").reduce()
        );
        assertEquals(
            new BigFraction("-23/17"),
            new BigFraction("-5516481/4077399").reduce()
        );
        assertEquals(
            new BigFraction("17/23"),
            new BigFraction("4077399/5516481").reduce()
        );
        assertEquals(
            new BigFraction("-17/23"),
            new BigFraction("-4077399/5516481").reduce()
        );
    }

    public void testAdd() {
        assertEquals(
            new BigFraction("1/1"),
            new BigFraction("1/3").add(new BigFraction("2/3"))
        );
        assertEquals(
            new BigFraction("1/3"),
            new BigFraction("-1/3").add(new BigFraction("2/3"))
        );
        assertEquals(
            new BigFraction("11/12"),
            new BigFraction("1/4").add(new BigFraction("2/3"))
        );
        assertEquals(
            new BigFraction("101/10000"),
            new BigFraction("1/100").add(new BigFraction("1/10000"))
        );
    }

    public void testSubtract() {
        assertEquals(
            new BigFraction("-1/3"),
            new BigFraction("1/3").subtract(new BigFraction("2/3"))
        );
        assertEquals(
            new BigFraction("-1/1"),
            new BigFraction("-1/3").subtract(new BigFraction("2/3"))
        );
        assertEquals(
            new BigFraction("-5/12"),
            new BigFraction("1/4").subtract(new BigFraction("2/3"))
        );
        assertEquals(
            new BigFraction("99/10000"),
            new BigFraction("1/100").subtract(new BigFraction("1/10000"))
        );
    }

    public void testMultiply() {
        // Test short-cuts
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("6/8").multiply(BigFraction.ONE)
        );
        assertEquals(
            new BigFraction("3/4"),
            BigFraction.ONE.multiply(new BigFraction("6/8"))
        );
        // Test signs
        assertEquals(
            new BigFraction("1/2"),
            new BigFraction("2/3").multiply(new BigFraction("3/4"))
        );
        assertEquals(
            new BigFraction("-1/2"),
            new BigFraction("2/3").multiply(new BigFraction("-3/4"))
        );
        assertEquals(
            new BigFraction("-1/2"),
            new BigFraction("-2/3").multiply(new BigFraction("3/4"))
        );
        assertEquals(
            new BigFraction("1/2"),
            new BigFraction("-2/3").multiply(new BigFraction("-3/4"))
        );
    }

    public void testDivide() {
        // Test short-cuts
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("6/8").divide(BigFraction.ONE)
        );
        assertEquals(
            new BigFraction("4/3"),
            BigFraction.ONE.divide(new BigFraction("6/8"))
        );
        // Test signs
        assertEquals(
            new BigFraction("8/9"),
            new BigFraction("2/3").divide(new BigFraction("3/4"))
        );
        assertEquals(
            new BigFraction("-8/9"),
            new BigFraction("2/3").divide(new BigFraction("-3/4"))
        );
        assertEquals(
            new BigFraction("-8/9"),
            new BigFraction("-2/3").divide(new BigFraction("3/4"))
        );
        assertEquals(
            new BigFraction("8/9"),
            new BigFraction("-2/3").divide(new BigFraction("-3/4"))
        );
    }

    public void testNegate() {
        assertEquals(
            new BigFraction("-3/4"),
            new BigFraction("3/4").negate()
        );
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("-3/4").negate()
        );
        // Should not trigger reduce
        assertEquals(
            new BigFraction("-6/8"),
            new BigFraction("6/8").negate()
        );
        assertEquals(
            new BigFraction("6/8"),
            new BigFraction("-6/8").negate()
        );
    }

    public void testAbs() {
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("3/4").abs()
        );
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("-3/4").abs()
        );
        // Should not trigger reduce
        assertEquals(
            new BigFraction("6/8"),
            new BigFraction("6/8").abs()
        );
        assertEquals(
            new BigFraction("6/8"),
            new BigFraction("-6/8").abs()
        );
    }

    public void testMax() {
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("3/4").max(new BigFraction("6/8"))
        );
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("6/8").max(new BigFraction("3/4"))
        );
        assertEquals(
            new BigFraction("6/8"),
            new BigFraction("6/8").max(new BigFraction("12/16"))
        );
        assertEquals(
            new BigFraction("6/8"),
            new BigFraction("12/16").max(new BigFraction("6/8"))
        );
        assertEquals(
            new BigFraction("1/3"),
            new BigFraction("1/3").max(new BigFraction("1/6"))
        );
        assertEquals(
            new BigFraction("1/6"),
            new BigFraction("-1/3").max(new BigFraction("1/6"))
        );
    }

    public void testMin() {
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("3/4").min(new BigFraction("6/8"))
        );
        assertEquals(
            new BigFraction("3/4"),
            new BigFraction("6/8").min(new BigFraction("3/4"))
        );
        assertEquals(
            new BigFraction("6/8"),
            new BigFraction("6/8").min(new BigFraction("12/16"))
        );
        assertEquals(
            new BigFraction("6/8"),
            new BigFraction("12/16").min(new BigFraction("6/8"))
        );
        assertEquals(
            new BigFraction("1/6"),
            new BigFraction("1/3").min(new BigFraction("1/6"))
        );
        assertEquals(
            new BigFraction("-1/3"),
            new BigFraction("-1/3").min(new BigFraction("1/6"))
        );
    }

    public void testPow() {
        assertEquals(
            new BigFraction("1/1"),
            new BigFraction("-1/3").pow(0)
        );
        assertEquals(
            new BigFraction("-1/3"),
            new BigFraction("-1/3").pow(1)
        );
        assertEquals(
            new BigFraction("-1/3"),
            new BigFraction("-3/9").pow(1)
        );
        assertEquals(
            new BigFraction("1/9"),
            new BigFraction("-1/3").pow(2)
        );
        assertEquals(
            new BigFraction("1/9"),
            new BigFraction("-3/9").pow(2)
        );
        assertEquals(
            new BigFraction("-1/27"),
            new BigFraction("-1/3").pow(3)
        );
        assertEquals(
            new BigFraction("-1/27"),
            new BigFraction("-3/9").pow(3)
        );
    }

    public void testValueOfBigDecimal() {
        assertEquals(
            new BigFraction("1/10"),
            BigFraction.valueOf(new BigDecimal("0.1"))
        );
        assertEquals(
            new BigFraction("1/4"),
            BigFraction.valueOf(new BigDecimal("0.25"))
        );
        assertEquals(
            new BigFraction("2398/1"),
            BigFraction.valueOf(new BigDecimal("2398"))
        );
        assertEquals(
            new BigFraction("4797/2"),
            BigFraction.valueOf(new BigDecimal("2398.5"))
        );
    }

    public void testGetBigInteger() {
        assertEquals(
            new BigInteger("0"),
            new BigFraction("1/3").getBigInteger(RoundingMode.DOWN)
        );
        assertEquals(
            new BigInteger("0"),
            new BigFraction("-1/3").getBigInteger(RoundingMode.DOWN)
        );
        assertEquals(
            new BigInteger("1"),
            new BigFraction("1/3").getBigInteger(RoundingMode.UP)
        );
        assertEquals(
            new BigInteger("-1"),
            new BigFraction("-1/3").getBigInteger(RoundingMode.UP)
        );
        assertEquals(
            new BigInteger("0"),
            new BigFraction("1/3").getBigInteger(RoundingMode.FLOOR)
        );
        assertEquals(
            new BigInteger("-1"),
            new BigFraction("-1/3").getBigInteger(RoundingMode.FLOOR)
        );
        assertEquals(
            new BigInteger("254"),
            new BigFraction("59436/234").getBigInteger()
        );
        assertEquals(
            new BigInteger("25"),
            new BigFraction("59436/2340").getBigInteger(RoundingMode.HALF_DOWN)
        );
    }

    public void testGetBigDecimal() {
        assertEquals(
            new BigDecimal("0.333"),
            new BigFraction("1/3").getBigDecimal(3, RoundingMode.DOWN)
        );
        assertEquals(
            new BigDecimal("-0.333"),
            new BigFraction("-1/3").getBigDecimal(3, RoundingMode.DOWN)
        );
        assertEquals(
            new BigDecimal("-0.334"),
            new BigFraction("-1/3").getBigDecimal(3, RoundingMode.FLOOR)
        );
        assertEquals(
            new BigDecimal("0.33333333333333333333"),
            new BigFraction("1/3").getBigDecimal(20, RoundingMode.DOWN)
        );
        assertEquals(
            new BigDecimal("-0.33333333333333333333"),
            new BigFraction("-1/3").getBigDecimal(20, RoundingMode.DOWN)
        );
        assertEquals(
            new BigDecimal("-0.33333333333333333334"),
            new BigFraction("-1/3").getBigDecimal(20, RoundingMode.FLOOR)
        );
    }

    private BigDecimal[] distributeFractionalMoney(BigDecimal total, BigFraction[] fractions) {
        // Make sure fractions sum to one
        BigFraction sum = BigFraction.ZERO;
        for(BigFraction fraction : fractions) sum = sum.add(fraction);
        assertEquals(
            BigFraction.ONE,
            sum
        );
        BigFraction totalFraction = BigFraction.valueOf(total);
        BigDecimal remaining = total;
        BigDecimal[] results = new BigDecimal[fractions.length];
        for(int c=0; c<fractions.length-1; c++) {
            remaining = remaining.subtract(results[c] = totalFraction.multiply(fractions[c]).getBigDecimal(2, RoundingMode.HALF_EVEN));
        }
        results[fractions.length-1] = remaining;
        return results;
    }

    public static void assertEquals(Object[] array1, Object[] array2) {
        if(!Arrays.equals(array1, array2)) {
            failNotEquals(null, Arrays.toString(array1), Arrays.toString(array2));
        }
    }

    public void testFractionalMoney() {
        assertEquals(
            new BigDecimal[] {
                new BigDecimal("33333.33"),
                new BigDecimal("33333.33"),
                new BigDecimal("33333.34")
            },
            distributeFractionalMoney(
                new BigDecimal("100000.00"),
                new BigFraction[] {
                    new BigFraction("1/3"),
                    new BigFraction("1/3"),
                    new BigFraction("1/3")
                }
            )
        );
        assertEquals(
            new BigDecimal[] {
                new BigDecimal("25000.00"),
                new BigDecimal("25000.00"),
                new BigDecimal("25000.00"),
                new BigDecimal("25000.00")
            },
            distributeFractionalMoney(
                new BigDecimal("100000.00"),
                new BigFraction[] {
                    new BigFraction("1/4"),
                    new BigFraction("1/4"),
                    new BigFraction("1/4"),
                    new BigFraction("1/4")
                }
            )
        );
        assertEquals(
            new BigDecimal[] {
                new BigDecimal("25000.00"),
                new BigDecimal("25000.00"),
                new BigDecimal("25000.00"),
                new BigDecimal("25000.00")
            },
            distributeFractionalMoney(
                new BigDecimal("0.02"),
                new BigFraction[] {
                    new BigFraction("1/4"),
                    new BigFraction("1/4"),
                    new BigFraction("1/4"),
                    new BigFraction("1/4")
                }
            )
        );
    }
}
