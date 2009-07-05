package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.Writer;

/**
 * Encode JavaScript into XHTML.  The static utility methods only encode
 * the characters.  When used as a MediaEncoder, it automatically adds
 * the &lt;script&gt; tags.
 *
 * @author  AO Industries, Inc.
 */
public class JavaScriptInXhtmlEncoder extends MediaEncoder {

    // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
    /**
     * Encodes a single character and returns its String representation
     * or null if no modification is necessary.  Any character that is
     * not valid in XHTML, or is '&lt;', '&amp;', or '&gt;' is encoded to
     * JavaScript \\uxxxx escapes.  XHTML entities are not used so they script
     * may or may not be in CDATA with no impact.
     */
    private static String getEscapedCharacter(char ch) {
        if(
            // These characters are allowed in JavaScript but not need escaped for HTML
            ch!='<'
            && ch!='>'
            && ch!='&'
            && (
                // These character ranges are passed through unmodified
                (ch>=0x20 && ch<=0xD7FF)
                || (ch>=0xE000 && ch<=0xFFFD)
                // Out or 16-bit unicode range: || (ch>=0x10000 && ch<=0x10FFFF)
            )
        ) return null;

        // Escape using JavaScript unicode escape.
        return NewEncodingUtils.getJavaScriptUnicodeEscapeString(ch);
    }

    public static void encodeJavaScriptInXhtml(CharSequence S, Appendable out) throws IOException {
        if(S!=null) encodeJavaScriptInXhtml(S, 0, S.length(), out);
    }

    public static void encodeJavaScriptInXhtml(CharSequence S, int start, int end, Appendable out) throws IOException {
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

    public static void encodeJavaScriptInXhtml(char[] cbuf, int start, int len, Writer out) throws IOException {
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

    public static void encodeJavaScriptInXhtml(char ch, Appendable out) throws IOException {
        String escaped = getEscapedCharacter(ch);
        if(escaped!=null) out.append(escaped);
        else out.append(ch);
    }
    // </editor-fold>

    protected JavaScriptInXhtmlEncoder(Writer out) {
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
    public void writePrefix() throws IOException {
        out.write("<script lang=\"text/javascript\">");
    }

    @Override
    public void write(int c) throws IOException {
        if(c>Character.MAX_VALUE) throw new AssertionError("Character value out of range: 0x"+Integer.toHexString(c));
        encodeJavaScriptInXhtml((char)c, out);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        encodeJavaScriptInXhtml(cbuf, off, len, out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        encodeJavaScriptInXhtml(str, off, off+len, out);
    }

    @Override
    public void writeSuffix() throws IOException {
        out.write("</script>");
    }
}
