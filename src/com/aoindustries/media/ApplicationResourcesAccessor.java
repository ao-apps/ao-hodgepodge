package com.aoindustries.media;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * Provides a simplified interface for obtaining localized values from the ApplicationResources.properties files.
 *
 * @author  AO Industries, Inc.
 */
final class ApplicationResourcesAccessor {

    /**
     * Make no instances.
     */
    private ApplicationResourcesAccessor() {
    }

    private static final com.aoindustries.util.ApplicationResourcesAccessor accessor = new com.aoindustries.util.ApplicationResourcesAccessor("com.aoindustries.media.ApplicationResources");

    static String getMessage(Locale locale, String key) {
        return accessor.getMessage(locale, key);
    }
    
    static String getMessage(Locale locale, String key, Object... args) {
        return accessor.getMessage(locale, key, args);
    }

    static String getMessage(String missingDefault, Locale locale, String key) {
        return accessor.getMessage(missingDefault, locale, key);
    }

    static String getMessage(String missingDefault, Locale locale, String key, Object... args) {
        return accessor.getMessage(missingDefault, locale, key, args);
    }
}
