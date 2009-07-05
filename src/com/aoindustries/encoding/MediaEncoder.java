package com.aoindustries.encoding;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * Encodes media to allow it to be contained in a different type of media.
 * For example, one may have plaintext inside of HTML, or arbitrary data inside
 * a JavaScript String inside an onclick attribute of an area tag in a XHTML
 * document.  All necessary encoding is automatically performed.
 *
 * Each encoder both validates its input characters and produces valid output
 * characters.
 *
 * @author  AO Industries, Inc.
 */
abstract public class MediaEncoder extends FilterWriter implements MediaInputValidator, ValidMediaOutput {

    /**
     * Gets the media encoder for the requested types or <code>null</code> if
     * no encoding is necessary.  When an encoder is returned it is also a validator
     * for the contentType.  If no encoder is returned, it may be necessary to
     * use a separate validator if character validation is required.
     *
     * @return the encoder or <code>null</code> if no encoding is necessary
     *
     * @exception MediaException when unable to encode the content into the container
     *                            either because it is impossible or not yet implemented.
     */
    public static MediaEncoder getMediaEncoder(Locale userLocale, MediaType contentType, MediaType containerType, Writer out) throws MediaException {
        // If the types match then no conversion is necessary
        if(contentType==containerType) return null;
        final MediaEncoder encoder;
        switch(contentType) {
            case JAVASCRIPT:
                switch(containerType) {
                    case TEXT: encoder = null; break; // No conversion necessary
                    case XHTML : encoder = new JavaScriptInXhtmlEncoder(out); break;
                    case XHTML_PRE: encoder = new TextInXhtmlPreEncoder(out, userLocale); break; // Just treat as text
                    default: throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaEncoder.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            case TEXT:
                switch(containerType) {
                    case JAVASCRIPT: encoder = new TextInJavaScriptEncoder(out); break;
                    case XHTML: encoder = new TextInXhtmlEncoder(out, userLocale); break;
                    case XHTML_PRE: encoder = new TextInXhtmlPreEncoder(out, userLocale); break;
                    default: throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaEncoder.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            case URL:
                switch(containerType) {
                    case TEXT: encoder = null; break; // No conversion necessary
                    case XHTML: encoder = new TextInXhtmlEncoder(out, userLocale); break; // Just treat as text
                    case XHTML_PRE: encoder = new TextInXhtmlPreEncoder(out, userLocale); break; // Just treat as text
                    default: throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaEncoder.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            case XHTML:
                switch(containerType) {
                    case TEXT: encoder = null; break; // No conversion necessary
                    default: throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaEncoder.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            case XHTML_PRE:
                switch(containerType) {
                    case TEXT: encoder = null; break; // No conversion necessary
                    case XHTML: encoder = new XhtmlPreInXhtmlEncoder(out, userLocale); break;
                    default: throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaEncoder.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            default: throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaEncoder.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
        }
        if(encoder!=null) {
            // Make sure types match - bug catching
            if(encoder.getValidMediaOutputType()!=containerType) throw new AssertionError("encoder.getValidMediaOutputType()!=containerType: "+encoder.getValidMediaOutputType()+"!="+containerType);
            if(!encoder.isValidatingMediaInputType(contentType)) throw new AssertionError("encoder="+encoder.getClass().getName()+" is not a validator for contentType="+contentType);
        }
        return encoder;
    }

    protected MediaEncoder(Writer out) {
        super(out);
    }

    /**
     * This is called before any data is written.
     */
    public void writePrefix() throws IOException {
    }

    /**
     * This is called when no more data will be written.
     */
    public void writeSuffix() throws IOException {
    }
}
