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
package com.aoindustries.util;

import java.util.Date;

/**
 * An unmodifiable Date.  Any mutator is overridden to throw UnsupportedOperationException.
 *
 * @author  AO Industries, Inc.
 */
final public class UnmodifiableDate extends Date {

    private static final long serialVersionUID = 1L;

    public UnmodifiableDate() {
        super();
    }

    public UnmodifiableDate(long date) {
        super(date);
    }

    @Deprecated
    @Override
    public void setYear(int year) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMonth(int month) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setDate(int date) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setHours(int hours) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMinutes(int minutes) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setSeconds(int seconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTime(long time) {
        throw new UnsupportedOperationException();
    }
}
