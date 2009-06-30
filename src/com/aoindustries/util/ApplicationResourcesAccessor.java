package com.aoindustries.util;

/*
 * Copyright 2007-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Provides a simplified interface for obtaining localized values from a <code>ResourceBundle</code>.
 * It is designed to be similar to the use of the related Struts classes.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class ApplicationResourcesAccessor {

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

    final private String resourceName;

    public ApplicationResourcesAccessor(String resourceName) {
        this.resourceName = resourceName;
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
        return multiReplace(message, args);
    }
    
    private static String multiReplace(String message, Object... args) {
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
                messageSB.append(message, lastPos, nextArgPos).append(args[nextArg]);
                lastPos = nextArgPos + nextArgHolderLen;
            }
        }
        return messageSB.toString();
    }

    /**
     * Cache for resource lookups.
     */
    private final ConcurrentMap<Locale,ConcurrentMap<String,String>> concurrentCache = new ConcurrentHashMap<Locale,ConcurrentMap<String,String>>();

    /**
     * Looks for a match, caches successful results.
     */
    private String getString(String missingDefault, Locale locale, String key) {
        // Find the locale-specific cache
        ConcurrentMap<String,String> concurrentLocaleMap = concurrentCache.get(locale);
        if(concurrentLocaleMap==null) concurrentCache.putIfAbsent(locale, concurrentLocaleMap = new ConcurrentHashMap<String,String>());

        // Look in the cache
        String string = concurrentLocaleMap.get(key);
        if(string==null) {
            try {
                ResourceBundle applicationResources = ResourceBundle.getBundle(resourceName, locale);
                string = applicationResources.getString(key);
            } catch(MissingResourceException err) {
                // string remains null
            }

            if(string==null) {
                // Use provided missingDefault then default to struts-style ??? formatting
                string = missingDefault!=null ? missingDefault : ("???"+locale.toString()+"."+key+"???");
            } else {
                // Add to cache to avoid subsequent lookups to ResourceBundle
                concurrentLocaleMap.putIfAbsent(key, string);
            }
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
