package com.aoindustries.encoding;

import java.util.concurrent.atomic.AtomicReferenceArray;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Encoding helper utilities.
 * TODO: Rename to EncodingUtils once com.aoindustries.util.EncodingUtils has been eliminated.
 *
 * @author  AO Industries, Inc.
 */
class NewEncodingUtils {

    private NewEncodingUtils() {
    }

    static final String EOL = System.getProperty("line.separator");
    static final String BR_EOL = "<br />"+EOL;

    static final char[] hexChars={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    static char getHex(int value) {
        return hexChars[value & 15];
    }

    /**
     * The Strings are kept here after first created.
     */
    private static final AtomicReferenceArray<String> javaScriptUnicodeEscapeStrings = new AtomicReferenceArray<String>((int)Character.MAX_VALUE+1);
    static String getJavaScriptUnicodeEscapeString(char ch) {
        int chInt = ch;
        String escaped = javaScriptUnicodeEscapeStrings.get(chInt);
        if(escaped==null) {
            escaped = "\\u" + getHex(chInt>>>12) + getHex(chInt>>>8) + getHex(chInt>>>4) + getHex(chInt);
            javaScriptUnicodeEscapeStrings.set(chInt, escaped);
        }
        return escaped;
    }
}
