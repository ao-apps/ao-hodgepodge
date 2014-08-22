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

		private static class CacheKey {
			private final String language;
			private final String country;
			private final String variant;

			private CacheKey(String language, String country, String variant) {
				this.language = language;
				this.country = country;
				this.variant = variant;
			}

			@Override
			public boolean equals(Object o) {
				if(!(o instanceof CacheKey)) return false;
				CacheKey other = (CacheKey)o;
				return
					language.equals(other.language)
					&& country.equals(other.country)
					&& variant.equals(other.variant)
				;
			}

			@Override
			public int hashCode() {
				int hash = language.hashCode();
				hash = hash * 31 + country.hashCode();
				hash = hash * 31 + variant.hashCode();
				return hash;
			}
		}

		private static final ConcurrentMap<CacheKey,Locale> locales = new ConcurrentHashMap<CacheKey,Locale>(16, 0.75f, 1);

		/**
		 * @see  Locales#getCachedLocale(java.lang.String, java.lang.String, java.lang.String)
		 */
		private static Locale getCachedLocale(String language, String country, String variant) {
			language = language.toLowerCase(Locale.ENGLISH);
			country = country.toUpperCase(Locale.ENGLISH);
			CacheKey key = new CacheKey(language, country, variant);
			Locale locale = locales.get(key);
			if(locale == null) {
				locale = new Locale(
					language,
					country,
					variant
				);
				Locale existing = locales.putIfAbsent(key, locale);
				if(existing != null) locale = existing;
			}
			return locale;
		}

		// Preload all standard Java locales
		static {
			for(Locale locale : Locale.getAvailableLocales()) {
				locales.put(
					new CacheKey(
						locale.getLanguage(),
						locale.getCountry(),
						locale.getVariant()
					),
					locale
				);
			}
		}
		private LocaleCache() {
		}
	}

	/**
	 * Gets a cached locale instance.
	 */
	public static Locale getCachedLocale(String language, String country, String variant) {
		return LocaleCache.getCachedLocale(language, country, variant);
	}

	/**
	 * Finds the first underscore (_) or dash(-).
	 *
	 * @return the position in the string or -1 if not found
	 */
	private static int indexOfSeparator(String locale, int fromIndex) {
		int pos1 = locale.indexOf('_', fromIndex);
		int pos2 = locale.indexOf('-', fromIndex);
		if(pos1 == -1) {
			return pos2;
		} else {
			if(pos2 == -1) {
				return pos1;
			} else {
				return Math.min(pos1, pos2);
			}
		}
	}

	/**
	 * Parses locales from their <code>toString</code> representation.
	 * Language, country, and variant may be separated by underscore "_" or hyphen "-".
	 * Language is converted to lowercase.
	 * Country is converted to uppercase.
	 * Caches locales so the same instance will be returned for each combination of language, country, and variant.
	 * <p>
	 *   Locales are currently cached forever.
	 *   Malicious external sources of locales could fill the heap space, so protect against this if needed.
	 * </p>
	 */
	public static Locale parseLocale(String locale) {
		int pos = indexOfSeparator(locale, 0);
		if(pos == -1) {
			return getCachedLocale(locale, "", "");
		} else {
			int pos2 = indexOfSeparator(locale, pos+1);
			if(pos2 == -1) {
				return getCachedLocale(
					locale.substring(0, pos).toLowerCase(Locale.ENGLISH),
					locale.substring(pos + 1).toUpperCase(Locale.ENGLISH),
					""
				);
			} else {
				return getCachedLocale(
					locale.substring(0, pos).toLowerCase(Locale.ENGLISH),
					locale.substring(pos + 1, pos2).toUpperCase(Locale.ENGLISH),
					locale.substring(pos2 + 1)
				);
			}
		}
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
