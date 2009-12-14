/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.i18n;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Groups resource bundles into a set.  Only the most recent bundle created
 * per locale is kept.
 *
 * @author  AO Industries, Inc.
 */
public class EditableResourceBundleSet {

    private final String baseName;

    /**
     * Unmodifiable set of all supported locales for this bundle.
     */
    private final SortedSet<Locale> locales;

    private final Map<Locale,EditableResourceBundle> bundles = new ConcurrentHashMap<Locale, EditableResourceBundle>();

    /**
     * @param baseName the base name of the default locale's bundle.
     * @param locales the set of all allowed locales.
     */
    public EditableResourceBundleSet(String baseName, Collection<Locale> locales) {
        // The locales are sorted by language, country, then variant.
        SortedSet<Locale> modifiableSet = new TreeSet<Locale>(LocaleComparator.getInstance());
        modifiableSet.addAll(locales);
        this.baseName = baseName;
        this.locales = Collections.unmodifiableSortedSet(modifiableSet);
    }

    /**
     * The constructor of EditableResourceBundle adds itself here.
     */
    void addBundle(EditableResourceBundle bundle) {
        Locale locale = bundle.locale;
        if(!locales.contains(locale)) throw new AssertionError("locale not in locales: "+locale);
        bundles.put(locale, bundle);
    }

    public String getBaseName() {
        return baseName;
    }

    /**
     * Gets the unmodifiable set of all locales supported by this bundle set.
     */
    public SortedSet<Locale> getLocales() {
        return locales;
    }

    /**
     * Gets the editable bundle for the provided locale.
     */
    public EditableResourceBundle getResourceBundle(Locale locale) {
        EditableResourceBundle localeBundle = bundles.get(locale);
        if(localeBundle==null) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
            if(!resourceBundle.getLocale().equals(locale)) throw new AssertionError("ResourceBundle not for this locale: "+locale);
            if(!(resourceBundle instanceof EditableResourceBundle)) throw new AssertionError("ResourceBundle is not a EditableResourceBundle: "+resourceBundle);
            localeBundle = (EditableResourceBundle)resourceBundle;
            if(localeBundle.bundleSet!=this) throw new AssertionError("EditableResourceBundle not for this EditableResourceBundleSet: "+localeBundle);
            if(!localeBundle.locale.equals(locale)) throw new AssertionError("EditableResourceBundle not for this locale: "+locale);
            // EditableResourceBundle will have added the bundle to the bundles map.
        }
        return localeBundle;
    }
}
