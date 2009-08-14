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
package com.aoindustries.encoding;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Encoding helper utilities.
 * TODO: Rename to EncodingUtils once com.aoindustries.util.EncodingUtils has been eliminated.
 *
 * @author  AO Industries, Inc.
 */
public class NewEncodingUtils {

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

    public static void encodeTextInJavaScriptInXhtml(String text, Appendable out) throws IOException {
        StringBuilder javascript = new StringBuilder(text.length());
        TextInJavaScriptEncoder.encodeTextInJavaScript(text, javascript);
        JavaScriptInXhtmlEncoder.encodeJavaScriptInXhtml(javascript, out);
    }

    public static void encodeTextInJavaScriptInXhtmlAttribute(String text, Appendable out) throws IOException {
        StringBuilder javascript = new StringBuilder(text.length());
        TextInJavaScriptEncoder.encodeTextInJavaScript(text, javascript);
        JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(javascript, out);
    }

    public static String getTextInJavaScriptInXhtmlAttribute(String text) throws IOException {
        StringBuilder xhtml = new StringBuilder(text.length());
        encodeTextInJavaScriptInXhtmlAttribute(text, xhtml);
        return xhtml.toString();
    }
}
