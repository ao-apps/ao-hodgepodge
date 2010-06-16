/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.util.i18n;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Stores a monetary value as a combination of currency and amount.  It supports
 * many of the basic operators from <code>BigDecimal</code>, and more will be added
 * as needed.  An <code>ArithmeticException</code> on any attempt to perform operations
 * on monetary values of different currencies.
 *
 * @author  AO Industries, Inc.
 */
final public class Money implements Serializable, ObjectInputValidation, Comparable<Money> {

    private static final long serialVersionUID = 1L;

    private final Currency currency;
    private final BigDecimal value;

    /**
     * @throws NumberFormatException if value scale is not correct for the currency.
     */
    public Money(Currency currency, BigDecimal value) throws NumberFormatException {
        this.currency = currency;
        this.value = value;
        validate();
    }

    private void validate() throws NumberFormatException {
        int scale = currency.getDefaultFractionDigits();
        if(scale!=-1 && scale!=value.scale()) throw new NumberFormatException("currency.scale!=value.scale: "+scale+"!="+value.scale());
    }

    /**
     * Perform same validation as constructor on readObject.
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.registerValidation(this, 0);
        ois.defaultReadObject();
    }

    @Override
    public void validateObject() throws InvalidObjectException {
        try {
            validate();
        } catch(NumberFormatException err) {
            InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this==o) return true;
        if(o==null || !(o instanceof Money)) return false;
        Money other = (Money)o;
        return
            value.equals(other.value)
            && (
                currency==other.currency
                || currency.getCurrencyCode().equals(other.currency.getCurrencyCode())
            )
        ;
    }

    @Override
    public int hashCode() {
        return currency.getCurrencyCode().hashCode()*31 + value.hashCode();
    }

    /**
     * Sorts by currency code and then value.
     *
     * @see  CurrencyComparator
     */
    @Override
    public int compareTo(Money other) {
        int diff = CurrencyComparator.getInstance().compare(currency, other.currency);
        if(diff!=0) return diff;
        return value.compareTo(other.value);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    /**
     * Displays the monetary value as currency symbol (in Locale-specific display) followed by value, such as $100.00
     * or $-100.50.
     */
    @Override
    public String toString() {
        return currency.getSymbol(ThreadLocale.get())+value.toPlainString();
    }

    public Money add(Money augend) throws ArithmeticException {
        if(currency!=augend.currency) throw new ArithmeticException("currency!=augend.currency: "+currency+"!="+augend.currency);
        return new Money(currency, value.add(augend.value));
    }

    /**
     * Multiplies without rounding.
     */
    public Money multiply(BigDecimal multiplicand) throws ArithmeticException {
        return multiply(multiplicand, RoundingMode.UNNECESSARY);
    }

    /**
     * Multiplies with rounding.
     */
    public Money multiply(BigDecimal multiplicand, RoundingMode roundingMode) throws ArithmeticException {
        return new Money(currency, value.multiply(multiplicand).setScale(currency.getDefaultFractionDigits(), roundingMode));
    }
}
