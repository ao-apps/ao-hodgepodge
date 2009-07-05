package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
            default:
            {
                if(ch<' ') return NewEncodingUtils.getJavaScriptUnicodeEscapeString(ch);
                // No conversion necessary
                return null;
            }
        }
    }

    public static void encodeTextInJavaScript(CharSequence S, Appendable out) throws IOException {
        if(S!=null) encodeTextInJavaScript(S, 0, S.length(), out);
    }

    public static void encodeTextInJavaScript(CharSequence S, int start, int end, Appendable out) throws IOException {
        if (S != null) {
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
    }

    public static void encodeTextInJavaScript(char[] cbuf, int start, int len, Writer out) throws IOException {
        if(cbuf != null) {
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

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.TEXT
            || inputType==MediaType.JAVASCRIPT  // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.JAVASCRIPT;
    }

    @Override
    public void writePrefix() throws IOException {
        out.write('"');
    }

    @Override
    public void write(int c) throws IOException {
        if(c>Character.MAX_VALUE) throw new AssertionError("Character value out of range: 0x"+Integer.toHexString(c));
        encodeTextInJavaScript((char)c, out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        encodeTextInJavaScript(cbuf, off, len, out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        encodeTextInJavaScript(str, off, off+len, out);
    }

    @Override
    public void writeSuffix() throws IOException {
        out.write('"');
    }
}
