package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.Writer;

/**
 * Currently performs no validation of the character because JavaScript can
 * use the entire Unicode character set.
 *
 * @author  AO Industries, Inc.
 */
public class JavaScriptValidator extends MediaValidator {

    protected JavaScriptValidator(Writer out) {
        super(out);
    }

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.JAVASCRIPT
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.JAVASCRIPT;
    }
}
