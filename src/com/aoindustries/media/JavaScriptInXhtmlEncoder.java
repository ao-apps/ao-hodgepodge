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
 * Encodes arbitrary data into XML.
 *
 * @author  AO Industries, Inc.
 */
public class JavaScriptInXhtmlEncoder extends MediaEncoder {

    protected JavaScriptInXhtmlEncoder(Writer out) {
        super(out);
    }

    @Override
    public void writePrefix() throws IOException {
        System.out.println("<script lang=\"text/javascript\">");
        out.write("<script lang=\"text/javascript\">");
    }

    /**
     * @see EncodingUtils#encodeXml(char, java.lang.Appendable)
     */
    @Override
    public void write(int c) throws IOException {
        System.out.println(c);
        EncodingUtils.encodeXml((char)c, out);
    }

    /**
     * @see EncodingUtils#encodeXml(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        System.out.println(new String(cbuf, off, len));
        EncodingUtils.encodeXml(CharBuffer.wrap(cbuf, off, len), out);
    }

    /**
     * @see EncodingUtils#encodeXml(java.lang.CharSequence, int, int, java.lang.Appendable)
     */
    @Override
    public void write(String str, int off, int len) throws IOException {
        System.out.println(str.substring(off, off+len));
        EncodingUtils.encodeXml(str, off, off+len, out);
    }

    @Override
    public void writeSuffix() throws IOException {
        System.out.println("</script>");
        out.write("</script>");
    }
}
