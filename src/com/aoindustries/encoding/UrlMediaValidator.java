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
 * for URI/URL data.
 *
 * @author  AO Industries, Inc.
 */
public class UrlMediaValidator extends MediaValidator {

    /**
     * Checks one character, throws IOException if invalid.
     * @see java.net.URLEncoder
     */
    public static void checkCharacter(Locale userLocale, int c) throws IOException {
        if(
            (c<'a' || c>'z')
            && (c<'A' || c>'Z')
            && (c<'0' || c>'9')
            && c!='.'
            && c!='-'
            && c!='*'
            && c!='_'
            && c!='+' // converted space
            && c!='%' // encoded value
            // Other characters used outside the URL data
            && c!=':'
            && c!='/'
            && c!=';'
            && c!='?'
            && c!='='
            && c!='&'
            && c!='#'
        ) throw new IOException(ApplicationResourcesAccessor.getMessage(userLocale, "UrlMediaValidator.invalidCharacter", Integer.toHexString(c)));
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
    public static void checkCharacters(Locale userLocale, CharSequence str, int off, int len) throws IOException {
        int end = off + len;
        while(off<end) checkCharacter(userLocale, str.charAt(off++));
    }

    private final Locale userLocale;

    protected UrlMediaValidator(Writer out, Locale userLocale) {
        super(out);
        this.userLocale = userLocale;
    }

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.URL
            || inputType==MediaType.XHTML_PRE   // All valid URL characters are also valid XHTML+PRE characters
            || inputType==MediaType.XHTML       // All valid URL characters are also valid XHTML characters
            || inputType==MediaType.JAVASCRIPT  // No validation required
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.URL;
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
        checkCharacters(userLocale, str, off, len);
        out.write(str, off, len);
    }
}
