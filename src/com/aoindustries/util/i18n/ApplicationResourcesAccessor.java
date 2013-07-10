/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013  AO Industries, Inc.
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

import com.aoindustries.text.MessageFormatFactory;
import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provides a simplified interface for obtaining localized and formatted values
 * from a <code>ResourceBundle</code>.  It is designed to be compatible with the
 * use of JSTL classes and taglibs.
 *
 * @author  AO Industries, Inc.
 */
public class ApplicationResourcesAccessor implements Serializable {

    private static final long serialVersionUID = -8735217773587095120L;

    private static final ConcurrentMap<String,ApplicationResourcesAccessor> accessors = new ConcurrentHashMap<>();

    public static ApplicationResourcesAccessor getInstance(String baseName) {
        ApplicationResourcesAccessor existing = accessors.get(baseName);
        if(existing==null) {
            ApplicationResourcesAccessor newAccessor = new ApplicationResourcesAccessor(baseName);
            existing = accessors.putIfAbsent(baseName, newAccessor);
            if(existing==null) existing = newAccessor;
        }
        return existing;
    }

    final private String baseName;

    private ApplicationResourcesAccessor(String baseName) {
        this.baseName = baseName;
    }

    private Object readResolve() {
        return getInstance(baseName);
    }

    /**
     * Gets the baseName being accessed by this accessor.
     */
    public String getBaseName() {
        return baseName;
    }

    /**
     * Gets the bundle for the provided locale.
     */
    public ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }

    /**
     * <p>
     * Gets the message.  If missing, will generate a struts-like value including
     * the locale and key.
     * </p>
     * <p>
     * Gets the message in the current thread's locale.
     * </p>
     *
     * @see ThreadLocale
     */
    public String getMessage(String key) {
        Locale locale = ThreadLocale.get();
        String string = null;
        try {
            string = getResourceBundle(locale).getString(key);
        } catch(MissingResourceException err) {
            // string remains null
        }
        if(string==null) return "???"+locale.toString()+"."+key+"???";
        return string;
    }

    /**
     * Substitutes arguments in the text where it finds {0}, {1}, {2}, ...
     * Gets the message in the current thread's locale.
     *
     * @see ThreadLocale
     * @see  #getMessage(String,Locale,String)
     */
    public String getMessage(String key, Object... args) {
        Locale locale = ThreadLocale.get();
        String string = null;
        try {
            string = getResourceBundle(locale).getString(key);
        } catch(MissingResourceException err) {
            // string remains null
        }
        if(string==null) return "???"+locale.toString()+"."+key+"???";
        if(args.length==0) return string;
        return MessageFormatFactory.getMessageFormat(string, locale).format(args, new StringBuffer(), null).toString();
    }
}
