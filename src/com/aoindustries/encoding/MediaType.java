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

/**
 * Supported content types.
 *
 * @author  AO Industries, Inc.
 */
public enum MediaType {

    /**
     * Arbitrary 8-bit binary data (<code>application/octet-stream</code>).
     * Please note that some conversions of this will possibly lose data, such
     * as being contained by XML.  In this case control characters except \t,
     * \r, and \n will be discarded.  Consider what to do about character
     * encodings before enabling this.
     */
    // DATA("application/octet-stream"),

    /**
     * An XHTML 1.0 document (<code>application/xhtml+xml</code>).
     */
    XHTML("application/xhtml+xml") {
        @Override
        boolean isUsedFor(String contentType) {
            return
                "application/xhtml+xml".equalsIgnoreCase(contentType)
                || "text/html".equalsIgnoreCase(contentType)
            ;
        }
    },

    /**
     * Indicates that a value contains a XHTML attribute only.  This is a non-standard
     * media type and is only used during internal conversions.  The final output
     * should not be this type.
     */
    XHTML_ATTRIBUTE("application/xhtml+xml+attribute") {
        @Override
        boolean isUsedFor(String contentType) {
            return "application/xhtml+xml+attribute".equalsIgnoreCase(contentType);
        }
    },

    /**
     * A preformatted element within a (X)HTML document, such as the <code>pre</code>
     * or <code>textarea</code> tags. (<code>application/xhtml+xml+pre</code>).  This is
     * a non-standard media type and is only used during internal conversions.  The
     * final output should not be this type.
     */
    XHTML_PRE("application/xhtml+xml+pre") {
        @Override
        boolean isUsedFor(String contentType) {
            return "application/xhtml+xml+pre".equalsIgnoreCase(contentType);
        }
    },

    /**
     * An HTML document (<code>text/html</code>).
     */
    // HTML("text/html"),

    /**
     * A JavaScript script (<code>text/javascript</code>).
     */
    JAVASCRIPT("text/javascript") {
        @Override
        boolean isUsedFor(String contentType) {
            return "text/javascript".equalsIgnoreCase(contentType);
        }
    },

    /**
     * Any plaintext document comprised of unicode characters (<code>text/plain</code>).
     * This is used for any arbitrary, unknown and untrusted data.
     *
     * @see #DATA
     */
    TEXT("text/plain") {
        @Override
        boolean isUsedFor(String contentType) {
            return "text/plain".equalsIgnoreCase(contentType);
        }
    },

    /**
     * A URL-encoded, &amp; (not &amp;amp;) separated URL.
     */
    URL("text/url") {
        @Override
        boolean isUsedFor(String contentType) {
            return "text/url".equalsIgnoreCase(contentType);
        }
    };

    private final String mediaType;

    private MediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    abstract boolean isUsedFor(String contentType);

    /**
     * Gets the actual media type, such as <code>text/html</code>.
     */
    public String getMediaType() {
        return mediaType;
    }

    private static final MediaType[] values = values();

    /**
     * Gets the media type for the provided textual content type.
     */
    public static MediaType getMediaType(final String fullContentType) throws MediaException {
        int semiPos = fullContentType.indexOf(';');
        String contentType = ((semiPos==-1) ? fullContentType : fullContentType.substring(0, semiPos)).trim();
        for(MediaType value : values) {
            if(value.isUsedFor(contentType)) return value;
        }
        throw new MediaException(ApplicationResources.accessor.getMessage("MediaType.getMediaType.unknownType", fullContentType));
    }
}
