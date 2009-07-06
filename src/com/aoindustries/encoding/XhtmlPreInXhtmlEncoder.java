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
 * Writes XHTML preformatted text into (X)HTML by adding a pre tag wrapper.
 * Also validates its input (and thus its output) for XML-compatible characters.
 *
 * @author  AO Industries, Inc.
 */
public class XhtmlPreInXhtmlEncoder extends MediaEncoder {

    private final Locale userLocale;

    protected XhtmlPreInXhtmlEncoder(Writer out, Locale userLocale) {
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
        return MediaType.XHTML;
    }

    @Override
    public void writePrefix() throws IOException {
        out.write("<pre>");
    }

    @Override
    public void write(int c) throws IOException {
        XhtmlPreValidator.checkCharacter(userLocale, c);
        out.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        XhtmlPreValidator.checkCharacters(userLocale, cbuf, off, len);
        out.write(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        XhtmlPreValidator.checkCharacters(userLocale, str, off, off + len);
        out.write(str, off, len);
    }

    @Override
    public XhtmlPreInXhtmlEncoder append(CharSequence csq) throws IOException {
        XhtmlPreValidator.checkCharacters(userLocale, csq, 0, csq.length());
        out.append(csq);
        return this;
    }

    @Override
    public XhtmlPreInXhtmlEncoder append(CharSequence csq, int start, int end) throws IOException {
        XhtmlPreValidator.checkCharacters(userLocale, csq, start, end);
        out.append(csq, start, end);
        return this;
    }

    @Override
    public XhtmlPreInXhtmlEncoder append(char c) throws IOException {
        XhtmlPreValidator.checkCharacter(userLocale, c);
        out.append(c);
        return this;
    }

    @Override
    public void writeSuffix() throws IOException {
        out.write("</pre>");
    }
}
