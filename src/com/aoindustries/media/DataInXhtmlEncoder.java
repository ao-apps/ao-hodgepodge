package com.aoindustries.media;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

/**
 * Encodes arbitrary data into (X)HTML.  This adds a &lt;br /&gt; before any
 * newlines and converts all spaces to &#160; (non breaking white space).
 * Also, the end of line is converted to the platform-specific separator.
 * This will make the document generally look like its source within
 * the (X)HTML.
 *
 * @author  AO Industries, Inc.
 */
public class DataInXhtmlEncoder extends MediaEncoder {

    protected DataInXhtmlEncoder(Writer out) {
        super(out);
    }

    /**
     * @see EncodingUtils#encodeHtml(char, java.lang.Appendable)
     */
    @Override
    public void write(int c) throws IOException {
        EncodingUtils.encodeHtml((char)c, out);
    }

    /**
     * @see EncodingUtils#encodeHtml(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        EncodingUtils.encodeHtml(CharBuffer.wrap(cbuf, off, len), out);
    }

    /**
     * @see EncodingUtils#encodeHtml(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        EncodingUtils.encodeHtml(str, off, off+len, out);
    }
}
