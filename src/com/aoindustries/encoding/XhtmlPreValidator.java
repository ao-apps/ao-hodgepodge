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
 * Makes sure that all data going through this writer has the correct characters
 * for XML and is also not &lt; or &gt;.
 *
 * {@link http://www.w3.org/TR/REC-xml/#charsets}
 *
 * @author  AO Industries, Inc.
 */
public class XhtmlPreValidator extends MediaValidator {

    /**
     * Checks one character, throws IOException if invalid.
     *
     * {@link http://www.w3.org/TR/REC-xml/#charsets}
     */
    public static void checkCharacter(Locale userLocale, int c) throws IOException {
        if(
            c!=0x9
            && c!=0xA
            && c!=0xD
            && (c<0x20 || c>0xD7FF)
            && (c<0xE000 || c>0xFFFD)
            && (c<0x10000 || c>0x10FFFF)
            // Also don't allow any XML tags
            && c!='<'
            && c!='>'
        ) throw new IOException(ApplicationResourcesAccessor.getMessage(userLocale, "XhtmlPreMediaValidator.invalidCharacter", Integer.toHexString(c)));
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static void checkCharacters(Locale userLocale, char[] cbuf, int off, int len) throws IOException {
        int end = off + len;
        while(off<end) checkCharacter(userLocale, cbuf[off++]);
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static void checkCharacters(Locale userLocale, CharSequence str, int off, int end) throws IOException {
        while(off<end) checkCharacter(userLocale, str.charAt(off++));
    }

    private final Locale userLocale;

    protected XhtmlPreValidator(Writer out, Locale userLocale) {
        super(out);
        this.userLocale = userLocale;
    }

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.XHTML_PRE
            || inputType==MediaType.XHTML       // All valid XHTML+PRE characters are also valid XHTML characters
            || inputType==MediaType.JAVASCRIPT  // No validation required
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML_PRE;
    }

    @Override
    public void write(int c) throws IOException {
        checkCharacter(userLocale, c);
        out.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        checkCharacters(userLocale, cbuf, off, len);
        out.write(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        checkCharacters(userLocale, str, off, off + len);
        out.write(str, off, len);
    }

    @Override
    public XhtmlPreValidator append(CharSequence csq) throws IOException {
        checkCharacters(userLocale, csq, 0, csq.length());
        out.append(csq);
        return this;
    }

    @Override
    public XhtmlPreValidator append(CharSequence csq, int start, int end) throws IOException {
        checkCharacters(userLocale, csq, start, end);
        out.append(csq, start, end);
        return this;
    }

    @Override
    public XhtmlPreValidator append(char c) throws IOException {
        checkCharacter(userLocale, c);
        out.append(c);
        return this;
    }
}
