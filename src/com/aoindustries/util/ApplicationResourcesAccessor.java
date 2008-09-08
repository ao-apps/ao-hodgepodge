package com.aoindustries.util;

/*
 * Copyright 2007-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides a simplified interface for obtaining localized values from a <code>ResourceBundle</code>.
 * It is designed to be similar to the use of the related Struts classes.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class ApplicationResourcesAccessor {

    // Access to this array is not synchronized, because it doesn't hurt to create the value more than once under high concurrency
    private static final String[] argsHolderCache = new String[256];

    final private String resourceName;

    public ApplicationResourcesAccessor(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getMessage(Locale locale, String key) {
        return getString(locale, key);
    }

    /**
     * Substitutes arguments in the text where it finds {0}, {1}, {2}, ...
     */
    public String getMessage(Locale locale, String key, Object... args) {
        String message = getString(locale, key);
        return multiReplace(message, args);
    }
    
    private static String multiReplace(String message, Object... args) {
        int messageLen = message.length();
        int argsLen = args.length;
        if(messageLen<3 || argsLen==0) return message;
        if(argsLen>argsHolderCache.length) throw new IllegalArgumentException("Maximum of "+argsHolderCache.length+" arguments supported");

        StringBuilder messageSB = new StringBuilder(messageLen+argsLen*20);
        StringBuilder argSB = null;
        int lastPos = 0;
        while(lastPos<messageLen) {
            int nextArg = -1;
            int nextArgPos = -1;
            int nextArgHolderLen = -1;
            for(int c=0;c<argsLen;c++) {
                String argHolder = argsHolderCache[c];
                if(argHolder==null) {
                    if(argSB==null) {
                        argSB = new StringBuilder();
                        argSB.append('{');
                    } else {
                        argSB.setLength(1);
                    }
                    argSB.append(c).append('}');
                    argsHolderCache[c] = argHolder = argSB.toString();
                }
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
     *
     * TODO: read/write locks for higher concurrency.
     */
    private final Map<Locale,Map<String,String>> cache = new HashMap<Locale,Map<String,String>>();

    /**
     * Looks for a match, caches results.
     */
    private String getString(Locale locale, String key) {
        synchronized(cache) {
            // Find the locale-specific cache
            Map<String,String> localeMap = cache.get(locale);
            if(localeMap==null) cache.put(locale, localeMap = new HashMap<String,String>());

            // Look in the cache
            String string = localeMap.get(key);
            if(string==null) {
                try {
                    ResourceBundle applicationResources = ResourceBundle.getBundle(resourceName, locale);
                    string = applicationResources.getString(key);
                } catch(MissingResourceException err) {
                    // string remains null
                }

                // Default to struts-style ??? formatting
                if(string==null) string="???"+locale.toString()+"."+key+"???";

                // Add to cache
                localeMap.put(key, string);
            }
            return string;
        }
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
