/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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

import com.aoindustries.lang.NullArgumentException;
import java.util.Locale;

/**
 * Associates a locale with a string.  This is useful to manipulate or represent
 * the string in a locale specific manner.
 *
 * @author  AO Industries, Inc.
 */
public class LocaleString {

	private final Locale locale;
	private final String value;

    public LocaleString(Locale locale, String value) {
		this.locale = NullArgumentException.checkNotNull(locale, "locale");
		this.value = NullArgumentException.checkNotNull(value, "value");
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LocaleString)) return false;
		LocaleString other = (LocaleString)obj;
		return
			locale.equals(other.locale)
			&& value.equals(other.value)
		;
	}

	@Override
	public int hashCode() {
		int hash = locale.hashCode();
		hash = hash * 31 + value.hashCode();
		return hash;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getValue() {
		return value;
	}

	public LocaleString toLowerCase() {
		String newValue = value.toLowerCase(locale);
		return newValue == value ? this : new LocaleString(locale, newValue);
	}

	public LocaleString toUpperCase() {
		String newValue = value.toUpperCase(locale);
		return newValue == value ? this : new LocaleString(locale, newValue);
	}

	public LocaleString trim() {
		String newValue = value.trim();
		return newValue == value ? this : new LocaleString(locale, newValue);
	}

	public boolean isEmpty() {
		return value.isEmpty();
	}
}
