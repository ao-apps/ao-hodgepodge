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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Stores arbitrary size fractions by their numerator and denominator.
 *
 * @author  AO Industries, Inc.
 */
public class BigFraction extends Number implements Serializable, Comparable<BigFraction> {

    private static final long serialVersionUID = 1L;

    public static final char SOLIDUS = '\u2044';

    public static final BigFraction
        ZERO = new BigFraction(0,1),
        ONE = new BigFraction(1,1)
    ;

    public static BigFraction valueOf(long numerator, long denominator) throws NumberFormatException {
        if(denominator==1) {
            if(numerator==0) return ZERO;
            if(numerator==1) return ONE;
        }
        return new BigFraction(numerator, denominator);
    }

    public static BigFraction valueOf(BigInteger numerator, BigInteger denominator) throws NumberFormatException {
        if(denominator.compareTo(BigInteger.ONE)==0) {
            if(numerator.compareTo(BigInteger.ZERO)==0) return ZERO;
            if(numerator.compareTo(BigInteger.ONE)==0) return ONE;
        }
        return new BigFraction(numerator, denominator);
    }

    private final BigInteger numerator;
    private final BigInteger denominator;

    public BigFraction(String value) throws NumberFormatException {
        int slashPos = value.indexOf(SOLIDUS);
        if(slashPos==-1) slashPos = value.indexOf('/'); // Alternate slash
        if(slashPos==-1) throw new NumberFormatException("Unable to find solidus ("+SOLIDUS+") or slash (/)");
        this.numerator = new BigInteger(value.substring(0, slashPos));
        this.denominator = new BigInteger(value.substring(slashPos+1));
        validate();
    }

    public BigFraction(long numerator, long denominator) throws NumberFormatException {
        this.numerator = BigInteger.valueOf(numerator);
        this.denominator = BigInteger.valueOf(denominator);
        validate();
    }

    public BigFraction(BigInteger numerator, BigInteger denominator) throws NumberFormatException {
        this.numerator = numerator;
        this.denominator = denominator;
        validate();
    }

    private void validate() throws NumberFormatException {
        if(denominator.compareTo(BigInteger.ZERO)<=0) throw new NumberFormatException("denominator<=0");
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        validate();
    }

    @Override
    public String toString() {
        return numerator.toString() + SOLIDUS + denominator.toString();
    }

    @Override
    public int intValue() {
        return numerator.divide(denominator).intValue();
    }

    @Override
    public long longValue() {
        return numerator.divide(denominator).longValue();
    }

    @Override
    public float floatValue() {
        return numerator.floatValue() / denominator.floatValue();
    }

    @Override
    public double doubleValue() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    @Override
    public int hashCode() {
        return numerator.hashCode() * 31 + denominator.hashCode();
    }

    /**
     * Two fractions are equal when they have both the same numerator and denominator.
     * For numerical equality independent of denominator, use <code>compareTo</code>.
     *
     * @see  #compareTo(BigFraction)
     */
    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if(!(o instanceof BigFraction)) return false;
        BigFraction other = (BigFraction)o;
        return
            numerator.equals(other.numerator)
            && denominator.equals(other.denominator)
        ;
    }

    @Override
    public int compareTo(BigFraction o) {
        // Short-cut for same denominator
        if(denominator.compareTo(o.denominator)==0) return numerator.compareTo(o.numerator);
        return numerator.multiply(o.denominator).compareTo(o.numerator.multiply(denominator));
    }

    private BigFraction reduce(BigInteger newNumerator, BigInteger newDenominator) {
        // Reduce result
        if(newNumerator.compareTo(BigInteger.ZERO)==0) return ZERO;
        // Change signs if denominator is negative
        if(newDenominator.compareTo(BigInteger.ZERO)<0) {
            newNumerator = newNumerator.negate();
            newDenominator = newDenominator.negate();
        }
        // Reduce
        BigInteger gcd = newNumerator.gcd(newDenominator);
        if(gcd.compareTo(BigInteger.ONE)!=0) {
            newNumerator = newNumerator.divide(gcd);
            newDenominator = newDenominator.divide(gcd);
        }
        if(newNumerator.compareTo(numerator)==0 && newDenominator.compareTo(denominator)==0) return this;
        return valueOf(newNumerator, newDenominator);
    }

    /**
     * Reduces this fraction to its lowest terms.
     */
    public BigFraction reduce() {
        return reduce(numerator, denominator);
    }

    /**
     * Adds two fractions, returning the value in lowest terms.
     */
    public BigFraction add(BigFraction val) {
        if(denominator.compareTo(val.denominator)==0) {
            return reduce(
                numerator.add(val.numerator),
                denominator
            );
        } else {
            return reduce(
                numerator.multiply(val.denominator).add(val.numerator.multiply(denominator)),
                denominator.multiply(val.denominator)
            );
        }
    }

    /**
     * Subtracts two fractions, returning the value in lowest terms.
     */
    public BigFraction subtract(BigFraction val) {
        if(denominator.compareTo(val.denominator)==0) {
            return reduce(
                numerator.subtract(val.numerator),
                denominator
            );
        } else {
            return reduce(
                numerator.multiply(val.denominator).subtract(val.numerator.multiply(denominator)),
                denominator.multiply(val.denominator)
            );
        }
    }

    /**
     * Multiplies two fractions, returning the value in lowest terms.
     */
    public BigFraction multiply(BigFraction val) {
        if(val.equals(ONE)) {
            return this.reduce();
        } else if(this.equals(ONE)) {
            return val.reduce();
        } else {
            return reduce(
                numerator.multiply(val.numerator),
                denominator.multiply(val.denominator)
            );
        }
    }

    /**
     * Divides two fractions, returning the value in lowest terms.
     */
    public BigFraction divide(BigFraction val) {
        if(val.equals(ONE)) {
            return this.reduce();
        } else if(this.equals(ONE)) {
            return reduce(
                val.denominator,
                val.numerator
            );
        } else {
            return reduce(
                numerator.multiply(val.denominator),
                denominator.multiply(val.numerator)
            );
        }
    }

    /**
     * Negates the value, but is not reduced.
     */
    public BigFraction negate() {
        return valueOf(numerator.negate(), denominator);
    }

    /**
     * Gets the absolute value, but is not reduced.
     */
    public BigFraction abs() {
        return numerator.compareTo(BigInteger.ZERO)>=0 ? this : negate();
    }

    /**
     * Gets the higher of the two fractions.  When they are equal the one
     * with the lower denominator is returned.  When their denominators are also
     * equal, returns <code>this</code>.
     */
    public BigFraction max(BigFraction val) {
        int diff = this.compareTo(val);
        if(diff>0) return this;
        if(diff<0) return val;
        diff = denominator.compareTo(val.denominator);
        return diff<=0 ? this : val;
    }

    /**
     * Gets the lower of the two fractions.  When they are equal the one
     * with the lower denominator is returned.  When their denominators are also
     * equal, returns <code>this</code>.
     */
    public BigFraction min(BigFraction val) {
        int diff = this.compareTo(val);
        if(diff<0) return this;
        if(diff>0) return val;
        diff = denominator.compareTo(val.denominator);
        return diff<=0 ? this : val;
    }

    /**
     * Raises this fraction to the provided exponent, returning the value in lowest terms.
     */
    public BigFraction pow(int exponent) {
        if(exponent==0) return ONE;
        BigFraction reduced = reduce();
        if(exponent==1) return reduced;
        return valueOf(
            reduced.numerator.pow(exponent),
            reduced.denominator.pow(exponent)
        );
    }
}
