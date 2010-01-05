/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009  AO Industries, Inc.
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

import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provides a simplified interface for obtaining localized values from a <code>ResourceBundle</code>.
 * It is designed to be similar to the use of the related Struts classes.  Also uses
 * the parameter <code>toString(Locale)</code> when available, even if not marked with the
 * <code>LocalizedToString</code> interface.
 *
 * @author  AO Industries, Inc.
 */
public class ApplicationResourcesAccessor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The maximum number of arguments allowed 
     */
    public static final int MAX_ARGUMENTS = 256;

    /**
     * Access to this array is not synchronized because it uses a static initializer
     * and is then read-only.
     */
    private static final String[] argsHolderCache = new String[MAX_ARGUMENTS];
    static {
        StringBuilder argSB = new StringBuilder(5);
        argSB.append('{');
        for(int c=0;c<MAX_ARGUMENTS;c++) {
            argSB.setLength(1);
            argSB.append(c).append('}');
            argsHolderCache[c] = argSB.toString();
        }
    }

    private static final Class[] toStringParamTypes = new Class[] {Locale.class};

    /**
     * Gets the best <code>toString</code> for the provided object in the provided locale.
     * <ol>
     *   <li>If is <code>null</code>, returns <code>"null"</code></li>
     *   <li>If instance of <code>LocalizedToString</code>, casts and calls directly</li>
     *   <li>Calls <code>toString(Locale)</code> through reflection</li>
     *   <li>Uses standard <code>toString()</code></li>
     * </ol>
     */
    public static String getToString(Locale locale, Object object) {
        if(object==null) return "null";
        if(object instanceof LocalizedToString) return ((LocalizedToString)object).toString(locale);
        try {
            Method method = object.getClass().getMethod("toString", toStringParamTypes);
            int mod = method.getModifiers();
            if(
                Modifier.isPublic(mod)
                && !Modifier.isStatic(mod)
                && method.getReturnType()==String.class
            ) return (String)method.invoke(object, locale);
        } catch(NoSuchMethodException err) {
            // Fall-through to default toString
        } catch(IllegalAccessException err) {
            throw new WrappedException(err);
        } catch(InvocationTargetException err) {
            throw new WrappedException(err);
        }
        return object.toString();
    }

    private static String multiReplace(Locale locale, String message, Object... args) {
        int messageLen = message.length();
        int argsLen = args.length;
        if(messageLen<3 || argsLen==0) return message;
        if(argsLen>MAX_ARGUMENTS) throw new IllegalArgumentException("Maximum of "+MAX_ARGUMENTS+" arguments supported");

        StringBuilder messageSB = new StringBuilder(messageLen+argsLen*20);
        int lastPos = 0;
        while(lastPos<messageLen) {
            int nextArg = -1;
            int nextArgPos = -1;
            int nextArgHolderLen = -1;
            for(int c=0;c<argsLen;c++) {
                String argHolder = argsHolderCache[c];
                int argPos = nextArgPos==-1 ? message.indexOf(argHolder, lastPos) : StringUtility.indexOf(message, argHolder, lastPos, nextArgPos);
                if(argPos!=-1) {
                    nextArg = c;
                    nextArgPos = argPos;
                    nextArgHolderLen = argHolder.length();
                }
            }
            if(nextArg==-1) {
                // None found
                messageSB.append(message, lastPos, messageLen);
                lastPos = messageLen;
            } else {
                messageSB.append(message, lastPos, nextArgPos).append(getToString(locale, args[nextArg]));
                lastPos = nextArgPos + nextArgHolderLen;
            }
        }
        return messageSB.toString();
    }

    private static final ConcurrentMap<String,ApplicationResourcesAccessor> accessors = new ConcurrentHashMap<String,ApplicationResourcesAccessor>();

    public static ApplicationResourcesAccessor getInstance(String resourceName) {
        ApplicationResourcesAccessor existing = accessors.get(resourceName);
        if(existing==null) {
            ApplicationResourcesAccessor newAccessor = new ApplicationResourcesAccessor(resourceName);
            existing = accessors.putIfAbsent(resourceName, newAccessor);
            if(existing==null) existing = newAccessor;
        }
        return existing;
    }

    final private String resourceName;

    private ApplicationResourcesAccessor(String resourceName) {
        this.resourceName = resourceName;
    }

    private Object readResolve() {
        return getInstance(resourceName);
    }

    public String getMessage(Locale locale, String key) {
        return getMessage(null, locale, key);
    }

    /**
     * Gets the message.  If the messages is missing will use the missingDefault
     * value.  If missingDefault is null, will generate a struts-like value
     * including the locale and key.
     *
     * This should be used very sparingly.  It is intended for situations where
     * the associated properties file may be unavailable in specific
     * circumstances, such as a TagExtraInfo implementation in NetBeans 6.5.
     */
    public String getMessage(String missingDefault, Locale locale, String key) {
        return getString(missingDefault, locale, key);
    }

    /**
     * Substitutes arguments in the text where it finds {0}, {1}, {2}, ...
     */
    public String getMessage(Locale locale, String key, Object... args) {
        return getMessage(null, locale, key, args);
    }

    /**
     * Substitutes arguments in the text where it finds {0}, {1}, {2}, ...
     *
     * @see  #getMessage(String,Locale,String)
     */
    public String getMessage(String missingDefault, Locale locale, String key, Object... args) {
        String message = getString(missingDefault, locale, key);
        return multiReplace(locale, message, args);
    }

    private String getString(String missingDefault, Locale locale, String key) {
        String string = null;
        try {
            ResourceBundle applicationResources = ResourceBundle.getBundle(resourceName, locale);
            string = applicationResources.getString(key);
        } catch(MissingResourceException err) {
            // string remains null
        }

        if(string==null) {
            // Use provided missingDefault then default to struts-style ??? formatting
            string = missingDefault!=null ? missingDefault : ("???"+locale.toString()+"."+key+"???");
        }
        return string;
    }

    /*
    public static void main(String[] args) {
        System.out.println(multiReplace("test"));
        System.out.println(multiReplace("test {0} {1}", "one", "two"));
        System.out.println(multiReplace("test {1} {0}", "one", "two"));
        System.out.println(multiReplace("test {0} {1}", "{1}", "{0}"));
        System.out.println(multiReplace("test {1} {0}", "{1}", "{0}"));
        System.out.println(multiReplace("test {0} {1}", "{2}", "{2}", "three"));
        System.out.println(multiReplace("test {1} {0}", "{2}", "{2}", "three"));
    }*/
}
