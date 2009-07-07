package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.StringBuilderWriter;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;

/**
 * Encodes a URL into a JavaScript string.  It uses HttpServletRequest.encodeURL
 * rewrite the URL as needed and surrounds it in double quotes.
 *
 * @author  AO Industries, Inc.
 */
public class UrlInJavaScriptEncoder extends MediaEncoder {

    private final Writer originalOut;
    private final Locale userLocale;
    private final HttpServletResponse response;

    /**
     * Buffers all contents to pass to the HttpServletResponse.encodeURL method.
     */
    private final StringBuilderWriter buffer = new StringBuilderWriter(128);

    protected UrlInJavaScriptEncoder(Writer out, Locale userLocale, HttpServletResponse response) {
        super(out);
        this.originalOut = out;
        this.userLocale = userLocale;
        this.response = response;
        // Replace out to write to a validated buffer instead
        this.out = new UrlValidator(buffer, userLocale);
    }

    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.URL
            || inputType==MediaType.XHTML_ATTRIBUTE // All valid URL characters are also valid XHTML+ATTRIBUTE characters
            || inputType==MediaType.XHTML_PRE   // All valid URL characters are also valid XHTML+PRE characters
            || inputType==MediaType.XHTML       // All valid URL characters are also valid XHTML characters
            || inputType==MediaType.JAVASCRIPT  // No validation required
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.JAVASCRIPT;
    }

    @Override
    public void writePrefix() throws IOException {
        originalOut.write('"');
    }

    @Override
    public void writeSuffix() throws IOException {
        StringUtility.replace(
            response.encodeURL(buffer.toString()),
            "&amp;",
            "&",
            originalOut
        );
        originalOut.write('"');
    }
}
