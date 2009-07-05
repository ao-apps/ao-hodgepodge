package com.aoindustries.media;

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
 * for XML.
 *
 * {@link http://www.w3.org/TR/REC-xml/#charsets}
 *
 * @author  AO Industries, Inc.
 */
public class XmlMediaValidator extends MediaValidator {

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
        ) throw new IOException(ApplicationResourcesAccessor.getMessage(userLocale, "XmlMediaValidator.invalidCharacter", Integer.toHexString(c)));
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static void checkCharacters(Locale userLocale, char[] cbuf, int off, int len) throws IOException {
        int end = off + len;
        while(off<end) checkCharacter(userLocale, cbuf[off]);
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static void checkCharacters(Locale userLocale, CharSequence str, int off, int len) throws IOException {
        int end = off + len;
        while(off<end) checkCharacter(userLocale, str.charAt(off));
    }

    private final Locale userLocale;

    protected XmlMediaValidator(Writer out, Locale userLocale) {
        super(out);
        this.userLocale = userLocale;
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
