/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2012  AO Industries, Inc.
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

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Wraps a Calendar to make it unmodifiable.
 *
 * @author  AO Industries, Inc.
 */
final public class UnmodifiableCalendar extends Calendar {

    private static final long serialVersionUID = -8096789285108910128L;

    private final Calendar wrapped;

    public UnmodifiableCalendar(Calendar wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void add(int field, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean after(Object when) {
        return wrapped.after(when);
    }

    @Override
    public boolean before(Object when) {
        return wrapped.before(when);
    }

    @Override
    public UnmodifiableCalendar clone() {
        return new UnmodifiableCalendar((Calendar)wrapped.clone());
    }

    @Override
    public int compareTo(Calendar anotherCalendar) {
        return wrapped.compareTo(anotherCalendar);
    }

    @Override
    protected void complete() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void computeFields() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void computeTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        return wrapped.equals(obj);
    }

    @Override
    public int get(int field) {
        return wrapped.get(field);
    }

    @Override
    public int getActualMaximum(int field) {
        return wrapped.getActualMaximum(field);
    }

    @Override
    public int getActualMinimum(int field) {
        return wrapped.getActualMinimum(field);
    }

    @Override
    public String getDisplayName(int field, int style, Locale locale) {
        return wrapped.getDisplayName(field, style, locale);
    }

    @Override
    public Map<String, Integer> getDisplayNames(int field, int style, Locale locale) {
        return wrapped.getDisplayNames(field, style, locale);
    }

    @Override
    public int getFirstDayOfWeek() {
        return wrapped.getFirstDayOfWeek();
    }

    @Override
    public int getGreatestMinimum(int field) {
        return wrapped.getGreatestMinimum(field);
    }

    @Override
    public int getLeastMaximum(int field) {
        return wrapped.getLeastMaximum(field);
    }

    @Override
    public int getMaximum(int field) {
        return wrapped.getMaximum(field);
    }

    @Override
    public int getMinimalDaysInFirstWeek() {
        return wrapped.getMinimalDaysInFirstWeek();
    }

    @Override
    public int getMinimum(int field) {
        return wrapped.getMinimum(field);
    }

    @Override
    public long getTimeInMillis() {
        return wrapped.getTimeInMillis();
    }

    @Override
    public TimeZone getTimeZone() {
        return wrapped.getTimeZone();
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public boolean isLenient() {
        return wrapped.isLenient();
    }

    @Override
    public void roll(int field, boolean up) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void roll(int field, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int field, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFirstDayOfWeek(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLenient(boolean lenient) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMinimalDaysInFirstWeek(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimeInMillis(long millis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTimeZone(TimeZone value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return wrapped.toString();
    }
}
