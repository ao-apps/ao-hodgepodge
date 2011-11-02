/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;

/**
 * Encodes a URL into a JavaScript string.  It uses HttpServletRequest.encodeURL
 * to rewrite the URL as needed and surrounds it in double quotes.
 *
 * @author  AO Industries, Inc.
 */
public class UrlInJavaScriptEncoder extends MediaEncoder {

    private final Writer originalOut;
    private final HttpServletResponse response;

    /**
     * Buffers all contents to pass to the HttpServletResponse.encodeURL method.
     */
    private final StringBuilderWriter buffer = new StringBuilderWriter(128);

    protected UrlInJavaScriptEncoder(Writer out, HttpServletResponse response) {
        super(out);
        this.originalOut = out;
        this.response = response;
        // Replace out to write to a validated buffer instead
        this.out = new UrlValidator(buffer);
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.URL
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

    @Override
    public MediaType getValidMediaOutputType() {
        return MediaType.JAVASCRIPT;
    }

    @Override
    public void writePrefix() throws IOException {
        originalOut.write('"');
    }

    @Override
    public void writeSuffix() throws IOException {
        TextInJavaScriptEncoder.encodeTextInJavaScript(
            response.encodeURL(
                NewEncodingUtils.encodeUrlPath(
                    buffer.toString()
                )
            ),
            originalOut
        );
        originalOut.write('"');
    }
}
