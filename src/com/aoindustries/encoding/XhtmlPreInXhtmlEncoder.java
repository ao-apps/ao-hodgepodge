package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.Writer;

/**
 * Writes HTML preformatted text into (X)HTML by adding a pre tag wrapper.
 *
 * @author  AO Industries, Inc.
 */
public class XhtmlPreInXhtmlEncoder extends MediaEncoder {

    protected XhtmlPreInXhtmlEncoder(Writer out) {
        super(out);
    }

    @Override
    public void writePrefix() throws IOException {
        out.write("<pre>");
    }

    @Override
    public void writeSuffix() throws IOException {
        out.write("</pre>");
    }
}
