package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * Encodes arbitrary data into XHTML.  Minimal conversion is performed, just
 * encoding of necessary values and throwing an IOException when any character
 * is found that cannot be converted to XHTML entities.
 * 
 * @author  AO Industries, Inc.
 */
public class TextInXhtmlEncoder extends MediaEncoder {

    // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
    /**
     * Encodes a single character and returns its String representation
     * or null if no modification is necessary.
     *
     * @see XhtmlMediaValidator#checkCharacter(java.util.Locale, int)
     */
    private static String getEscapedCharacter(Locale userLocale, int c) throws IOException {
        switch(c) {
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '&': return "&amp;";
            default:
                XhtmlMediaValidator.checkCharacter(userLocale, c);
                return null;
        }
    }

    public static void encodeTextInXhtml(Locale userLocale, CharSequence S, Appendable out) throws IOException {
        if(S!=null) encodeTextInXhtml(userLocale, S, 0, S.length(), out);
    }

    public static void encodeTextInXhtml(Locale userLocale, CharSequence S, int start, int end, Appendable out) throws IOException {
        if (S != null) {
            int toPrint = 0;
            for (int c = start; c < end; c++) {
                String escaped = getEscapedCharacter(userLocale, S.charAt(c));
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

    public static void encodeTextInXhtml(Locale userLocale, char[] cbuf, int start, int len, Writer out) throws IOException {
        if(cbuf != null) {
            int end = start+len;
            int toPrint = 0;
            for (int c = start; c < end; c++) {
                String escaped = getEscapedCharacter(userLocale, cbuf[c]);
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

    public static void encodeTextInXhtml(Locale userLocale, char ch, Appendable out) throws IOException {
        String escaped = getEscapedCharacter(userLocale, ch);
        if(escaped!=null) out.append(escaped);
        else out.append(ch);
    }
    // </editor-fold>

    private final Locale userLocale;

    protected TextInXhtmlEncoder(Writer out, Locale userLocale) {
        super(out);
        this.userLocale = userLocale;
    }

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.TEXT
            || inputType==MediaType.JAVASCRIPT  // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML;
    }

    /**
     * Will accept values higher than <code>Character.MAX_VALUE</code>.
     * The wrapped out may reject them, however.
     */
    @Override
    public void write(int c) throws IOException {
        String escaped = getEscapedCharacter(userLocale, c);
        if(escaped!=null) out.write(escaped);
        else out.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        encodeTextInXhtml(userLocale, cbuf, off, len, out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        encodeTextInXhtml(userLocale, str, off, off+len, out);
    }
}
