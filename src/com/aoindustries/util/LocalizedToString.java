package com.aoindustries.util;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Locale;

/**
 * Any object that provides a <code>Locale</code>-specific <code>toString</code>
 * method may indicate so by implementing this interface.  When providing the
 * localized <code>toString</code>, please override <code>toString()</code> as
 * follows:
 * <pre>
 * public String toString() {
 *     return toString(Locale.getDefault());
 * }
 * </pre>
 *
 * @author  AO Industries, Inc.
 */
public interface LocalizedToString {

    String toString(Locale userLocale);
}
