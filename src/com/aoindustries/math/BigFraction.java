/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011  AO Industries, Inc.
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * Stores arbitrary size fractions by their numerator and denominator.
 *
 * @author  AO Industries, Inc.
 */
public class BigFraction extends Number implements Serializable, Comparable<BigFraction> {

    private static final long serialVersionUID = 6382807525128346490L;

    private static final BigInteger
        ONE_HUNDRED = BigInteger.valueOf(100),
        ONE_THOUSAND = BigInteger.valueOf(1000),
        TEN_THOUSAND = BigInteger.valueOf(10000),
        ONE_HUNDRED_THOUSAND = BigInteger.valueOf(100000)
    ;

    public static final BigFraction
        ZERO = new BigFraction(0,1,false),
        ONE = new BigFraction(1,1,false)
    ;

    public static BigFraction valueOf(long numerator, long denominator, boolean displayPercentage) throws NumberFormatException {
        if(!displayPercentage && denominator==1) {
            if(numerator==0) return ZERO;
            if(numerator==1) return ONE;
        }
        return new BigFraction(numerator, denominator, displayPercentage);
    }

    public static BigFraction valueOf(BigInteger value, boolean displayPercentage) throws NumberFormatException {
        if(!displayPercentage) {
            if(value.compareTo(BigInteger.ZERO)==0) return ZERO;
            if(value.compareTo(BigInteger.ONE)==0) return ONE;
        }
        return new BigFraction(value, BigInteger.ONE, displayPercentage);
    }

    /**
     * Gets the big decimal as a fraction, reduced.
     */
    public static BigFraction valueOf(BigDecimal value, boolean displayPercentage) throws NumberFormatException {
        if(!displayPercentage) {
            if(value.compareTo(BigDecimal.ZERO)==0) return ZERO;
            if(value.compareTo(BigDecimal.ONE)==0) return ONE;
        }
        int scale = value.scale();
        if(scale<=0) {
            // Has no decimal point
            return new BigFraction(
                value.toBigIntegerExact(),
                BigInteger.ONE,
                displayPercentage
            );
        } else {
            // Has a decimal point
            return new BigFraction(
                value.movePointRight(scale).toBigIntegerExact(),
                BigInteger.TEN.pow(scale),
                displayPercentage
            ).reduce();
        }
    }

    public static BigFraction valueOf(BigInteger numerator, BigInteger denominator, boolean displayPercentage) throws NumberFormatException {
        if(!displayPercentage && denominator.compareTo(BigInteger.ONE)==0) {
            if(numerator.compareTo(BigInteger.ZERO)==0) return ZERO;
            if(numerator.compareTo(BigInteger.ONE)==0) return ONE;
        }
        return new BigFraction(numerator, denominator, displayPercentage);
    }

    /**
     * Evenly distributes the total value of BigDecimal by fractional amounts.
     * <ul>
     *   <li>The sum of the results will equal <code>total</code>.</li>
     *   <li>The results will use the same scale as <code>total</code>.</li>
     *   <li>The results will be rounded where necessary to match the scale of <code>total</code>.</li>
     *   <li>The results are rounded-up for the larger fractions first.</li>
     *   <li>Each result will be zero or have a sign matching <code>total</code>.</li>
     * </ul>
     *
     * @param total The total value to be distributed within the results, the sum must be equal to one.
     * @param fractions The fractional amount of each result.  The array elements are unmodified.
     *
     * @return the results corresponding to each fractional amount.
     *         
     */
    public static BigDecimal[] distributeValue(final BigDecimal total, final BigFraction... fractions) {
        // Must have at least one fraction
        int len = fractions.length;
        if(len==0) throw new IllegalArgumentException("fractions must contain at least one element");

        // Make sure fractions sum to one
        BigFraction sum = BigFraction.ZERO;
        for(BigFraction fraction : fractions) {
            if(fraction.compareTo(BigFraction.ZERO)<0) throw new IllegalArgumentException("fractions contains value<0: "+fraction);
            sum = sum.add(fraction);
        }
        if(sum.compareTo(BigFraction.ONE)!=0) throw new IllegalArgumentException("sum(fractions)!=1: " + sum);

        // Sort the fractions from lowest to highest
        BigFraction[] fractionOrdereds = Arrays.copyOf(fractions, len);
        Arrays.sort(fractionOrdereds);
        BigFraction[] fractionAltereds = Arrays.copyOf(fractionOrdereds, len);

        BigDecimal remaining = total;
        BigDecimal[] results = new BigDecimal[len];
        for(int c=len-1; c>=0; c--) {
            BigFraction fractionAltered = fractionAltereds[c];
            BigDecimal result = BigFraction.valueOf(remaining, false).multiply(fractionAltered).getBigDecimal(total.scale(), RoundingMode.UP);
            if(result.compareTo(BigDecimal.ZERO)!=0 && result.signum()!=total.signum()) throw new AssertionError("sign(result)!=sign(total): "+result);
            remaining = remaining.subtract(result);
            BigFraction divisor = BigFraction.ONE.subtract(fractionAltered);
            for(int d=c-1; d>=0; d--) fractionAltereds[d] = fractionAltereds[d].divide(divisor);

            BigFraction fractionOrdered = fractionOrdereds[c];
            for(int d=0;d<len;d++) {
                if(results[d]==null && fractions[d].compareTo(fractionOrdered)==0) {
                    results[d] = result;
                    break;
                }
            }
        }
        if(remaining.compareTo(BigDecimal.ZERO)!=0) throw new AssertionError("remaining!=0: "+remaining);
        return results;
    }

    private final BigInteger numerator;
    private final BigInteger denominator;
    private final boolean displayPercentage;

    public BigFraction(String value) throws NumberFormatException {
        if(value.endsWith("%")) {
            BigDecimal bd = new BigDecimal(value.substring(0, value.length()-1)).movePointLeft(2);
            int scale = bd.scale();
            if(scale<=0) {
                // Has no decimal point
                this.numerator = bd.toBigIntegerExact();
                this.denominator = BigInteger.ONE;
            } else {
                // Has a decimal point
                this.numerator = bd.movePointRight(scale).toBigIntegerExact();
                this.denominator = BigInteger.TEN.pow(scale);
            }
            this.displayPercentage = true;
        } else {
            int slashPos = value.indexOf('/');
            if(slashPos==-1) throw new NumberFormatException("Unable to find slash (/)");
            this.numerator = new BigInteger(value.substring(0, slashPos));
            this.denominator = new BigInteger(value.substring(slashPos+1));
            this.displayPercentage = false;
        }
        validate();
    }

    public BigFraction(long numerator, long denominator, boolean displayPercentage) throws NumberFormatException {
        this.numerator = BigInteger.valueOf(numerator);
        this.denominator = BigInteger.valueOf(denominator);
        this.displayPercentage = displayPercentage;
        validate();
    }

    public BigFraction(BigInteger numerator, BigInteger denominator, boolean displayPercentage) throws NumberFormatException {
        this.numerator = numerator;
        this.denominator = denominator;
        this.displayPercentage = displayPercentage;
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
        if(displayPercentage) {
            // Short-cut for x/100
            if(denominator.compareTo(ONE_HUNDRED)==0) return numerator.toString()+'%';

            // Reduce first
            BigFraction reduced = this.reduce();

            // See if goes into 100 evenly
            BigInteger[] divideAndRemainer = ONE_HUNDRED.divideAndRemainder(reduced.denominator);
            if(divideAndRemainer[1].compareTo(BigInteger.ZERO)==0) {
                return reduced.numerator.multiply(divideAndRemainer[0]).toString()+"%";
            }
            // See if goes into 1000 evenly
            divideAndRemainer = ONE_THOUSAND.divideAndRemainder(reduced.denominator);
            if(divideAndRemainer[1].compareTo(BigInteger.ZERO)==0) {
                return new BigDecimal(reduced.numerator.multiply(divideAndRemainer[0]), 1).toPlainString()+"%";
            }
            // See if goes into 10000 evenly
            divideAndRemainer = TEN_THOUSAND.divideAndRemainder(reduced.denominator);
            if(divideAndRemainer[1].compareTo(BigInteger.ZERO)==0) {
                return new BigDecimal(reduced.numerator.multiply(divideAndRemainer[0]), 2).toPlainString()+"%";
            }
            // See if goes into 100000 evenly
            divideAndRemainer = ONE_HUNDRED_THOUSAND.divideAndRemainder(reduced.denominator);
            if(divideAndRemainer[1].compareTo(BigInteger.ZERO)==0) {
                return new BigDecimal(reduced.numerator.multiply(divideAndRemainer[0]), 3).toPlainString()+"%";
            }
        }
        return numerator.toString() + '/' + denominator.toString();
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public boolean isDisplayPercentage() {
        return displayPercentage;
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

    /**
     * Gets this fraction as a BigInteger using <code>RoundingMode.UNNECESSARY</code>
     */
    public BigInteger getBigInteger() throws ArithmeticException {
        return getBigInteger(RoundingMode.UNNECESSARY);
    }

    /**
     * Gets this fraction as a BigInteger using the provided rounding mode.
     */
    public BigInteger getBigInteger(RoundingMode roundingMode) throws ArithmeticException {
        return getBigDecimal(0, roundingMode).toBigIntegerExact();
    }

    /**
     * Gets this fraction as a BigDecimal using <code>RoundingMode.UNNECESSARY</code>
     */
    public BigDecimal getBigDecimal(int scale) throws ArithmeticException {
        return getBigDecimal(scale, RoundingMode.UNNECESSARY);
    }

    /**
     * Gets this fraction as a BigDecimal using the provided rounding mode.
     */
    public BigDecimal getBigDecimal(int scale, RoundingMode roundingMode) throws ArithmeticException {
        return new BigDecimal(numerator).divide(
            new BigDecimal(denominator),
            scale,
            roundingMode
        );
    }

    @Override
    public int hashCode() {
        return numerator.hashCode() * 31 + denominator.hashCode() + (displayPercentage ? 1 : 0);
    }

    /**
     * Two fractions are equal when they have both the same numerator, denominator, and displayPercentage.
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
            displayPercentage==other.displayPercentage
            && numerator.equals(other.numerator)
            && denominator.equals(other.denominator)
        ;
    }

    @Override
    public int compareTo(BigFraction o) {
        // Short-cut for same denominator
        if(denominator.compareTo(o.denominator)==0) return numerator.compareTo(o.numerator);
        return numerator.multiply(o.denominator).compareTo(o.numerator.multiply(denominator));
    }

    private BigFraction reduce(BigInteger newNumerator, BigInteger newDenominator, boolean displayPercentage) {
        // Reduce result
        if(!displayPercentage && newNumerator.compareTo(BigInteger.ZERO)==0) return ZERO;
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
        if(displayPercentage==this.displayPercentage && newNumerator.compareTo(numerator)==0 && newDenominator.compareTo(denominator)==0) return this;
        return valueOf(newNumerator, newDenominator, displayPercentage);
    }

    /**
     * Reduces this fraction to its lowest terms.
     */
    public BigFraction reduce() {
        return reduce(numerator, denominator, displayPercentage);
    }

    /**
     * Adds two fractions, returning the value in lowest terms.
     *
     * If either is a percentage, the result will be a percentage.
     */
    public BigFraction add(BigFraction val) {
        if(denominator.compareTo(val.denominator)==0) {
            return reduce(
                numerator.add(val.numerator),
                denominator,
                displayPercentage || val.displayPercentage
            );
        } else {
            return reduce(
                numerator.multiply(val.denominator).add(val.numerator.multiply(denominator)),
                denominator.multiply(val.denominator),
                displayPercentage || val.displayPercentage
            );
        }
    }

    /**
     * Subtracts two fractions, returning the value in lowest terms.
     *
     * If either is a percentage, the result will be a percentage.
     */
    public BigFraction subtract(BigFraction val) {
        if(denominator.compareTo(val.denominator)==0) {
            return reduce(
                numerator.subtract(val.numerator),
                denominator,
                displayPercentage || val.displayPercentage
            );
        } else {
            return reduce(
                numerator.multiply(val.denominator).subtract(val.numerator.multiply(denominator)),
                denominator.multiply(val.denominator),
                displayPercentage || val.displayPercentage
            );
        }
    }

    /**
     * Multiplies two fractions, returning the value in lowest terms.
     *
     * If both are percentages, the result will be a percentage.
     */
    public BigFraction multiply(BigFraction val) {
        if(val.compareTo(ONE)==0) {
            return this.reduce(this.numerator, this.denominator, this.displayPercentage && val.displayPercentage);
        } else if(this.compareTo(ONE)==0) {
            return val.reduce(val.numerator, val.denominator, this.displayPercentage && val.displayPercentage);
        } else {
            return reduce(
                numerator.multiply(val.numerator),
                denominator.multiply(val.denominator),
                this.displayPercentage && val.displayPercentage
            );
        }
    }

    /**
     * Divides two fractions, returning the value in lowest terms.
     *
     * <ul>
     *   <li>If percent divide by percent: percent</li>
     *   <li>If percent divide by non-percent: percent</li>
     *   <li>If non-percent divide by percent: non-percent</li>
     *   <li>If non-percent divide by non-percent: non-percent</li>
     * </ul>
     */
    public BigFraction divide(BigFraction val) {
        if(val.compareTo(ONE)==0) {
            return this.reduce();
        } else if(this.compareTo(ONE)==0) {
            // Simple reciprocal
            return reduce(
                val.denominator,
                val.numerator,
                this.displayPercentage
            );
        } else {
            return reduce(
                numerator.multiply(val.denominator),
                denominator.multiply(val.numerator),
                this.displayPercentage
            );
        }
    }

    /**
     * Negates the value, but is not reduced.
     */
    public BigFraction negate() {
        return valueOf(numerator.negate(), denominator, displayPercentage);
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
            reduced.denominator.pow(exponent),
            reduced.displayPercentage
        );
    }
}
