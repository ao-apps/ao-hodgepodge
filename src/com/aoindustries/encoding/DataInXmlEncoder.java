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
 * Encodes arbitrary data into XML.
 *
 * @author  AO Industries, Inc.
 */
public class DataInXmlEncoder extends MediaEncoder {

    protected DataInXmlEncoder(Writer out) {
        super(out);
    }

    /**
     * @see EncodingUtils#encodeXml(char, java.lang.Appendable)
     */
    @Override
    public void write(int c) throws IOException {
        EncodingUtils.encodeXml((char)c, out);
    }

    /**
     * @see EncodingUtils#encodeXml(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        EncodingUtils.encodeXml(CharBuffer.wrap(cbuf, off, len), out);
    }

    /**
     * @see EncodingUtils#encodeXml(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        EncodingUtils.encodeXml(str, off, off+len, out);
    }
}
