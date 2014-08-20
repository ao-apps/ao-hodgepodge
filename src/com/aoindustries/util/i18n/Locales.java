/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2014  AO Industries, Inc.
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

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author  AO Industries, Inc.
 */
public class Locales {

    private Locales() {}

	// Was getting NullPointerException on class init, trying cache in separate class.
	// It might have been due to memory exhausted in Tomcat, but this won't hurt.
	private static class LocaleCache {

		private static final ConcurrentMap<String,Locale> locales = new ConcurrentHashMap<String,Locale>(16, 0.75f, 1);

		/**
		 * @see  Locales#parseLocale(java.lang.String)
		 */
		private static Locale parseLocale(String locale) {
			Locale l = locales.get(locale);
			if(l==null) {
				int pos = locale.indexOf('_');
				if(pos==-1) pos = locale.indexOf('-');
				if(pos==-1) {
					l = new Locale(
						locale.toLowerCase(Locale.ENGLISH)
					);
				} else {
					int pos2 = locale.indexOf('_', pos+1);
					if(pos2==-1) pos2 = locale.indexOf('-', pos+1);
					if(pos2==-1) {
						l = new Locale(
							locale.substring(0, pos).toLowerCase(Locale.ENGLISH),
							locale.substring(pos + 1).toUpperCase(Locale.ENGLISH)
						);
					} else {
						l = new Locale(
							locale.substring(0, pos).toLowerCase(Locale.ENGLISH),
							locale.substring(pos + 1, pos2).toUpperCase(Locale.ENGLISH),
							locale.substring(pos2 + 1)
						);
					}
				}
				Locale existing = locales.putIfAbsent(locale, l);
				if(existing!=null) l = existing;
			}
			return l;
		}

		private LocaleCache() {
		}
	}

	/**
	 * Parses locales from their <code>toString</code> representation.
	 * Language, country, and variant may be separated by underscore "_" or hyphen "-".
	 * Caches locales for faster lookups.
	 */
	public static Locale parseLocale(String locale) {
		return LocaleCache.parseLocale(locale);
	}

	/**
     * Determines if the provided locale should be displayed from right to left.
     */
    public static boolean isRightToLeft(Locale locale) {
        String language = locale.getLanguage();
        return
            "ar".equals(language)    // arabic
            || "iw".equals(language) // hebrew
            || "fa".equals(language) // persian
        ;
    }

	/**
	 * Some locale constants not provided directly by Java, along with those provided by Java.
	 */
	public static final Locale
		/** Root locale */
		ROOT = Locale.ROOT,
		/** Languages */
		ARABIC = parseLocale("ar"),
		BULGARIAN = parseLocale("bg"),
		CATALAN = parseLocale("ca"),
		CZECH = parseLocale("cs"),
		DANISH = parseLocale("da"),
		GERMAN = Locale.GERMAN,
		GREEK = parseLocale("el"),
		ENGLISH = Locale.ENGLISH,
		SPANISH = parseLocale("es"),
		ESTONIAN = parseLocale("et"),
		PERSIAN = parseLocale("fa"),
		FINNISH = parseLocale("fi"),
		FRENCH = Locale.FRENCH,
		HINDI = parseLocale("hi"),
		CROATIAN = parseLocale("hr"),
		HUNGARIAN = parseLocale("hu"),
		// INDONESIAN is now "id" - this matches Java's backward compatibility
		INDONESIAN = parseLocale("in"),
		ICELANDIC = parseLocale("is"),
		ITALIAN = Locale.ITALIAN,
		JAPANESE = Locale.JAPANESE,
		KOREAN = Locale.KOREAN,
		// HEBREW is now "he" - this matches Java's backward compatibility
		HEBREW = parseLocale("iw"),
		LITHUANIAN = parseLocale("lt"),
		LATVIAN = parseLocale("lv"),
		DUTCH = parseLocale("nl"),
		NORWEGIAN = parseLocale("no"),
		POLISH = parseLocale("pl"),
		PORTUGUESE = parseLocale("pt"),
		ROMANIAN = parseLocale("ro"),
		RUSSIAN = parseLocale("ru"),
		SLOVAK = parseLocale("sk"),
		SLOVENIAN = parseLocale("sl"),
		SERBIAN = parseLocale("sr"),
		SWEDISH = parseLocale("sv"),
		TURKISH = parseLocale("tr"),
		CHINESE = Locale.CHINESE,
		SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE,
		TRADITIONAL_CHINESE = Locale.TRADITIONAL_CHINESE,
		/** Countries */
		FRANCE = Locale.FRANCE,
		GERMANY = Locale.GERMANY,
		ITALY = Locale.ITALY,
		JAPAN = Locale.JAPAN,
		KOREA = Locale.KOREA,
		CHINA = Locale.SIMPLIFIED_CHINESE,
		PRC = Locale.SIMPLIFIED_CHINESE,
		TAIWAN = Locale.TRADITIONAL_CHINESE,
		UK = Locale.UK,
		US = Locale.US,
		CANADA = Locale.CANADA,
		CANADA_FRENCH = Locale.CANADA_FRENCH
	;
}
