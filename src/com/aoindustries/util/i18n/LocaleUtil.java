/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013  AO Industries, Inc.
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
public class LocaleUtil {

    private LocaleUtil() {}
    
    private static final ConcurrentMap<String,Locale> locales = new ConcurrentHashMap<String,Locale>();

    /**
     * Parses locales from their <code>toString</code> representation.  Caches
     * locales for faster lookups.
     */
    public static Locale parseLocale(String locale) {
        Locale l = locales.get(locale);
        if(l==null) {
            int pos = locale.indexOf('_');
            if(pos==-1) l = new Locale(locale);
            else {
                int pos2 = locale.indexOf('_', pos+1);
                if(pos2==-1) {
                    l = new Locale(locale.substring(0, pos), locale.substring(pos+1));
                } else {
                    l = new Locale(locale.substring(0, pos), locale.substring(pos+1, pos2), locale.substring(pos2+1));
                }
            }
            Locale existing = locales.putIfAbsent(locale, l);
            if(existing!=null) l = existing;
        }
        return l;
    }

    /**
     * Determines if the provided locale should be displayed from right to left.
     */
    public static final boolean isRightToLeft(Locale locale) {
        String language = locale.getLanguage();
        return
            "ar".equals(language)    // arabic
            || "iw".equals(language) // hebrew
            || "fa".equals(language) // persian
        ;
    }
}
