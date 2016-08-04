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
package com.aoindustries.util.i18n;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Currency;

/**
 * Compares currencies and orders them by their currency code.
 *
 * @author  AO Industries, Inc.
 */
public class CurrencyComparator implements Comparator<Currency>, Serializable {

    private static final long serialVersionUID = -6360174337692570997L;

    private static final CurrencyComparator singleton = new CurrencyComparator();

    public static CurrencyComparator getInstance() {
        return singleton;
    }

    private CurrencyComparator() {
    }

    private Object readResolve() {
        return getInstance();
    }

    @Override
    public int compare(Currency o1, Currency o2) {
        return o1.getCurrencyCode().compareTo(o2.getCurrencyCode());
    }
}