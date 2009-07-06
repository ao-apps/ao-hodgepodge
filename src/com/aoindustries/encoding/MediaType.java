package com.aoindustries.encoding;

import java.util.Locale;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

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
     * An HTML document (<code>text/html</code>).
     */
    // HTML("text/html"),

    /**
     * A JavaScript script (<code>text/javascript</code>).
     */
    JAVASCRIPT("text/javascript") {
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
        boolean isUsedFor(String contentType) {
            return "text/plain".equalsIgnoreCase(contentType);
        }
    },

    /**
     * A URL-encoded, &amp; (not &amp;amp;) separated URL.
     */
    URL("text/url") {
        boolean isUsedFor(String contentType) {
            return "text/url".equalsIgnoreCase(contentType);
        }
    },

    /**
     * An XHTML 1.0 document (<code>application/xhtml+xml</code>).
     */
    XHTML("application/xhtml+xml") {
        boolean isUsedFor(String contentType) {
            return
                "application/xhtml+xml".equalsIgnoreCase(contentType)
                || "text/html".equalsIgnoreCase(contentType)
            ;
        }
    },

    /**
     * A preformatted element within a (X)HTML document, such as the <code>pre</code>
     * or <code>textarea</code> tags. (<code>application/xhtml+xml+pre</code>).  This is
     * a non-standard media type and is only used during internal conversions.  The
     * final output should not be this type.
     */
    XHTML_PRE("application/xhtml+xml+pre") {
        boolean isUsedFor(String contentType) {
            return "application/xhtml+xml+pre".equalsIgnoreCase(contentType);
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
    public static MediaType getMediaType(final Locale userLocale, final String fullContentType) throws MediaException {
        int semiPos = fullContentType.indexOf(';');
        String contentType = ((semiPos==-1) ? fullContentType : fullContentType.substring(0, semiPos)).trim();
        for(MediaType value : values) {
            if(value.isUsedFor(contentType)) return value;
        }
        throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaType.getMediaType.unknownType", fullContentType));
    }
}
