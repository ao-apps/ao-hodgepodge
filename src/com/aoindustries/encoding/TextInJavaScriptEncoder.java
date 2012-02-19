/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012  AO Industries, Inc.
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
 * Encodes arbitrary text into a JavaScript string.  The static utility
 * methods to not add the quotes.  When used as a MediaEncoder, the text is
 * automatically surrounded by double quotes.  Any binary data is encoded with
 * \\uxxxx escapes.
 *
 * @author  AO Industries, Inc.
 */
public class TextInJavaScriptEncoder extends MediaEncoder {

    // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
    /**
     * Encodes a single character and returns its String representation
     * or null if no modification is necessary.
     */
    private static String getEscapedCharacter(char ch) {
        switch(ch) {
            case '"': return "\\\"";
            case '\'': return "\\'";
            case '\\': return "\\\\";
            case '\b': return "\\b";
            case '\f': return "\\f";
            case '\r': return "\\r";
            case '\n': return "\\n";
            case '\t': return "\\t";

            // Encode the following as unicode because escape for HTML and XHTML is different
            case '&': return "\\u0026";
            case '<': return "\\u003c";
            case '>': return "\\u003e";
            default:
                if(ch<' ') return NewEncodingUtils.getJavaScriptUnicodeEscapeString(ch);
                // No conversion necessary
                return null;
        }
    }

    public static void encodeTextInJavaScript(CharSequence S, Appendable out) throws IOException {
        if(S==null) S = "null";
        encodeTextInJavaScript(S, 0, S.length(), out);
    }

    public static void encodeTextInJavaScript(CharSequence S, int start, int end, Appendable out) throws IOException {
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

    public static void encodeTextInJavaScript(char[] cbuf, int start, int len, Writer out) throws IOException {
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

    public static void encodeTextInJavaScript(char ch, Appendable out) throws IOException {
        String escaped = getEscapedCharacter(ch);
        if(escaped!=null) out.append(escaped);
        else out.append(ch);
    }
    // </editor-fold>

    protected TextInJavaScriptEncoder(Writer out) {
        super(out);
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.TEXT
            //|| inputType==MediaType.JAVASCRIPT  // No validation required
        ;
    }

    @Override
    public MediaType getValidMediaOutputType() {
        return MediaType.JAVASCRIPT;
    }

    @Override
    public void writePrefix() throws IOException {
        out.write('"');
    }

    @Override
    public void write(int c) throws IOException {
        encodeTextInJavaScript((char)c, out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        encodeTextInJavaScript(cbuf, off, len, out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        encodeTextInJavaScript(str, off, off+len, out);
    }

    @Override
    public TextInJavaScriptEncoder append(CharSequence csq) throws IOException {
        encodeTextInJavaScript(csq, out);
        return this;
    }

    @Override
    public TextInJavaScriptEncoder append(CharSequence csq, int start, int end) throws IOException {
        encodeTextInJavaScript(csq, start, end, out);
        return this;
    }

    @Override
    public TextInJavaScriptEncoder append(char c) throws IOException {
        encodeTextInJavaScript(c, out);
        return this;
    }

    @Override
    public void writeSuffix() throws IOException {
        out.write('"');
    }
}
