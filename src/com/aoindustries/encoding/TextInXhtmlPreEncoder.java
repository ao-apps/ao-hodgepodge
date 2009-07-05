package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.Writer;
import java.util.Locale;

/**
 * Same encoding as <code>TextInXhtmlEncoder</code>, but with an output type
 * of <code>application/xhtml+xml+pre</code>.
 * 
 * @author  AO Industries, Inc.
 */
public class TextInXhtmlPreEncoder extends TextInXhtmlEncoder {

    protected TextInXhtmlPreEncoder(Writer out, Locale userLocale) {
        super(out, userLocale);
    }

    @Override
    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML_PRE;
    }
}
