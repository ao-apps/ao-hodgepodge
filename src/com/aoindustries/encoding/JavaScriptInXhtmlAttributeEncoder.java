/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010  AO Industries, Inc.
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
import java.io.Writer;

/**
 * Encode JavaScript into an XHTML attribute.  This does not add any quotes or
 * tags.
 *
 * @author  AO Industries, Inc.
 */
public class JavaScriptInXhtmlAttributeEncoder extends MediaEncoder {

    // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
    /**
     * Encodes a single character and returns its String representation
     * or null if no modification is necessary.  Any character that is
     * not valid in XHTML is encoded to JavaScript \\uxxxx escapes.
     * " and ' are changed to XHTML entities.
     */
    private static String getEscapedCharacter(char ch) {
        // These characters are allowed in JavaScript but need encoded for XHTML
        if(ch=='<') return "&lt;";
        if(ch=='>') return "&gt;";
        if(ch=='&') return "&amp;";
        if(ch=='"') return "&quot;";
        if(ch=='\'') return "&#39;";
        if(ch=='\r') return "&#xD;";
        if(ch=='\n') return "&#xA;";
        if(ch=='\t') return "&#x9;";
        if(
            // These character ranges are passed through unmodified
               (ch>=0x20 && ch<=0xD7FF)
            || (ch>=0xE000 && ch<=0xFFFD)
            // Out of 16-bit unicode range: || (ch>=0x10000 && ch<=0x10FFFF)
        ) return null;

        // Escape using JavaScript unicode escape.
        return NewEncodingUtils.getJavaScriptUnicodeEscapeString(ch);
    }

    public static void encodeJavaScriptInXhtmlAttribute(CharSequence S, Appendable out) throws IOException {
        if(S==null) S = "null";
        encodeJavaScriptInXhtmlAttribute(S, 0, S.length(), out);
    }

    public static void encodeJavaScriptInXhtmlAttribute(CharSequence S, int start, int end, Appendable out) throws IOException {
        if(S==null) S = "null";
        int toPrint = 0;
        for (int c = start; c < end; c++) {
            String escaped = getEscapedCharacter(S.charAt(c));
            if(escaped!=null) {
                if(toPrint>0) {
                    out.append(S, c-toPrint, c);
                    toPrint=0;
                }
                out.append(escaped);
            } else {
                toPrint++;
            }
        }
        if(toPrint>0) out.append(S, end-toPrint, end);
    }

    public static void encodeJavaScriptInXhtmlAttribute(char[] cbuf, int start, int len, Writer out) throws IOException {
        int end = start+len;
        int toPrint = 0;
        for (int c = start; c < end; c++) {
            String escaped = getEscapedCharacter(cbuf[c]);
            if(escaped!=null) {
                if(toPrint>0) {
                    out.write(cbuf, c-toPrint, toPrint);
                    toPrint=0;
                }
                out.append(escaped);
            } else {
                toPrint++;
            }
        }
        if(toPrint>0) out.write(cbuf, end-toPrint, toPrint);
    }

    public static void encodeJavaScriptInXhtmlAttribute(char ch, Appendable out) throws IOException {
        String escaped = getEscapedCharacter(ch);
        if(escaped!=null) out.append(escaped);
        else out.append(ch);
    }
    // </editor-fold>

    protected JavaScriptInXhtmlAttributeEncoder(Writer out) {
        super(out);
    }

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.JAVASCRIPT
            || inputType==MediaType.TEXT  // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public void write(int c) throws IOException {
        if(c>Character.MAX_VALUE) throw new AssertionError("Character value out of range: 0x"+Integer.toHexString(c));
        encodeJavaScriptInXhtmlAttribute((char)c, out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        encodeJavaScriptInXhtmlAttribute(cbuf, off, len, out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        encodeJavaScriptInXhtmlAttribute(str, off, off+len, out);
    }

    @Override
    public JavaScriptInXhtmlAttributeEncoder append(CharSequence csq) throws IOException {
        encodeJavaScriptInXhtmlAttribute(csq, out);
        return this;
    }

    @Override
    public JavaScriptInXhtmlAttributeEncoder append(CharSequence csq, int start, int end) throws IOException {
        encodeJavaScriptInXhtmlAttribute(csq, start, end, out);
        return this;
    }

    @Override
    public JavaScriptInXhtmlAttributeEncoder append(char c) throws IOException {
        encodeJavaScriptInXhtmlAttribute(c, out);
        return this;
    }
}
