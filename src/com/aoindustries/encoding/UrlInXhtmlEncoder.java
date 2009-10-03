/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.encoding;

import com.aoindustries.io.StringBuilderWriter;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;

/**
 * Encodes a URL into XHTML.  It uses HttpServletRequest.encodeURL
 * rewrite the URL as needed.
 *
 * @author  AO Industries, Inc.
 */
public class UrlInXhtmlEncoder extends MediaEncoder {

    private final Writer originalOut;
    private final Locale userLocale;
    private final HttpServletResponse response;

    /**
     * Buffers all contents to pass to the HttpServletResponse.encodeURL method.
     */
    private final StringBuilderWriter buffer = new StringBuilderWriter(128);

    protected UrlInXhtmlEncoder(Writer out, Locale userLocale, HttpServletResponse response) {
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
            || inputType==MediaType.JAVASCRIPT  // No validation required
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    public MediaType getValidMediaOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public void writeSuffix() throws IOException {
        String url = StringUtility.replace(
            response.encodeURL(
                NewEncodingUtils.encodeURL(
                    buffer.toString()
                )
            ),
            "&amp;",
            "&"
        );
        StringUtility.replace(
            url,
            "&",
            "&amp;",
            originalOut
        );
    }
}
