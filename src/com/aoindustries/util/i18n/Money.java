/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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

import com.aoindustries.io.FastExternalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

/**
 * Stores a monetary value as a combination of currency and amount.  It supports
 * many of the basic operators from <code>BigDecimal</code>, and more will be added
 * as needed.  An <code>ArithmeticException</code> on any attempt to perform operations
 * on monetary values of different currencies.
 *
 * @author  AO Industries, Inc.
 */
final public class Money implements FastExternalizable, Comparable<Money> {

    private static final Currency defaultCurrency = Currency.getInstance(Locale.getDefault());
    private static final int defaultScale = defaultCurrency.getDefaultFractionDigits()==-1 ? 0 : defaultCurrency.getDefaultFractionDigits();

    private Currency currency;
    private long value;
    private int scale;

    /**
     * Creates money equal to zero in the default currency.
     */
    public Money() {
        currency = defaultCurrency;
        value = 0;
        scale = defaultScale;
    }

    /**
     * Will change the scale of the value to match the currency, but will not round.
     * @throws NumberFormatException if unable to scale the value.
     */
    public Money(Currency currency, BigDecimal value) throws NumberFormatException {
        this.currency = currency;
        try {
            int currencyScale = currency.getDefaultFractionDigits();
            if(currencyScale!=-1) value = value.setScale(currencyScale);
            this.scale = value.scale();
            this.value = value.movePointRight(value.scale()).longValueExact();
        } catch(ArithmeticException err) {
            NumberFormatException newErr = new NumberFormatException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
        validate();
    }

    public Money(Currency currency, long value, int scale) throws NumberFormatException {
        this.currency = currency;
        try {
            int currencyScale = currency.getDefaultFractionDigits();
            if(currencyScale!=-1 && currencyScale!=scale) {
                value = BigDecimal.valueOf(value, scale).setScale(currencyScale).movePointRight(currencyScale).longValueExact();
                scale = currencyScale;
            }
            this.value = value;
            this.scale = scale;
        } catch(ArithmeticException err) {
            NumberFormatException newErr = new NumberFormatException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
        validate();
    }

    private void validate() throws NumberFormatException {
        int currencyScale = currency.getDefaultFractionDigits();
        if(currencyScale!=-1 && currencyScale!=scale) throw new NumberFormatException("currency.scale!=value.scale: "+currencyScale+"!="+scale);
    }

    /**
     * Equal when has same currency, value, and scale.
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Money)) return false;
        Money other = (Money)o;
        return
            currency==other.currency
            && value==other.value
            && scale==other.scale
        ;
    }

    @Override
    public int hashCode() {
        int hash = currency.getCurrencyCode().hashCode();
        hash = hash * 31 + (int)value;
        hash = hash * 31 + scale;
        return hash;
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
        return getValue().compareTo(other.getValue());
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getValue() {
        return BigDecimal.valueOf(value, scale);
    }

    /**
     * Gets the unscaled value of this currency.
     */
    public long getUnscaledValue() {
        return value;
    }

    /**
     * Gets the scale of this currency.
     */
    public int getScale() {
        return scale;
    }

    /**
     * Displays the monetary value as currency symbol (in Locale-specific display) followed by value, such as $100.00
     * or $-100.50.
     */
    @Override
    public String toString() {
        return currency.getSymbol(ThreadLocale.get())+getValue().toPlainString();
    }

    public Money add(Money augend) throws ArithmeticException {
        if(currency!=augend.currency) throw new ArithmeticException("currency!=augend.currency: "+currency+"!="+augend.currency);
        return new Money(currency, getValue().add(augend.getValue()));
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
        int currencyScale = currency.getDefaultFractionDigits();
        if(currencyScale==-1) currencyScale = scale; // Use same scale if currency doesn't dictate
        return new Money(currency, getValue().multiply(multiplicand).setScale(currencyScale, roundingMode));
    }

    /**
     * Returns a monetary amount that is the negative of this amount.
     */
    public Money negate() {
        return new Money(currency, getValue().negate());
    }

    // <editor-fold defaultstate="collapsed" desc="FastExternalizable">
    private static final long serialVersionUID = 2287045704444180509L;

    @Override
    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(currency.getCurrencyCode());
        out.writeLong(value);
        out.writeInt(scale);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        currency = Currency.getInstance(in.readUTF());
        value = in.readLong();
        scale = in.readInt();
        validate();
    }
    // </editor-fold>
}
