package com.aoindustries.encoding;

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
 * Encodes arbitrary data into a JavaScript string.  The data is surrounded
 * by double quotes.  The binary data is encoded with \\uxxxx escapes.
 *
 * @author  AO Industries, Inc.
 */
public class DataInJavaScriptEncoder extends MediaEncoder {

    protected DataInJavaScriptEncoder(Writer out) {
        super(out);
    }

    @Override
    public void writePrefix() throws IOException {
        out.write('"');
    }

    /**
     * @see EncodingUtils#encodeJavaScriptString(char, java.lang.Appendable)
     */
    @Override
    public void write(int c) throws IOException {
        EncodingUtils.encodeJavaScriptString((char)c, out);
    }

    /**
     * @see EncodingUtils#encodeJavaScriptString(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        EncodingUtils.encodeJavaScriptString(CharBuffer.wrap(cbuf, off, len), out);
    }

    /**
     * @see EncodingUtils#encodeJavaScriptString(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        EncodingUtils.encodeJavaScriptString(str, off, off+len, out);
    }

    @Override
    public void writeSuffix() throws IOException {
        out.write('"');
    }
}
