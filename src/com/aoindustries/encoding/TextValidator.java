package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.Writer;

/**
 * No validation is performed on text.
 *
 * @author  AO Industries, Inc.
 */
public class TextValidator extends MediaValidator {

    protected TextValidator(Writer out) {
        super(out);
    }

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.JAVASCRIPT        // No validation required
            || inputType==MediaType.TEXT
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.TEXT;
    }
}
