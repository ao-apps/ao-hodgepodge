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
 * document.
 *
 * @author  AO Industries, Inc.
 */
abstract public class MediaEncoder extends FilterWriter {

    /**
     * Gets the media encoder for the requested types.
     *
     * @return the converter or <code>null</code> if no encoding is necessary
     *
     * @exception MediaException when unable to encode the content into the container
     *                                   either because it is impossible or not yet implemented.
     */
    public static MediaEncoder getMediaEncoder(Locale userLocale, MediaType contentType, MediaType containerType, Writer out) throws MediaException {
        switch(contentType) {
            // case DATA:
            //    switch(containerType) {
            //        case DATA: return null; // No conversion necessary
            //        case HTML: return new DataInXhtmlEncoder(out);
            //        case XHTML: return new DataInXhtmlEncoder(out);
            //        case XHTML_PRE: return new DataInXmlEncoder(out);
            //    }
            //    break;
            // case HTML:
            //    switch(containerType) {
            //        case DATA: return null; // No conversion necessary
            //        case HTML: return null; // No conversion necessary
            //        case XHTML_PRE: return new DataInXmlEncoder(out);
            //    }
            //    break;
            case JAVASCRIPT:
                switch(containerType) {
                    // case DATA: return null; // No conversion necessary
                    // case HTML : return new JavaScriptInXhtmlEncoder(out); // XHTML is allowed to pass directly to HTML
                    case JAVASCRIPT: return null; // No conversion necessary
                    case TEXT: return null; // No conversion necessary
                    case XHTML : return new JavaScriptInXhtmlEncoder(out);
                    case XHTML_PRE: return new DataInXmlEncoder(out);
                }
                break;
            case TEXT:
                switch(containerType) {
                    // case DATA: return null; // No conversion necessary
                    // case HTML: return new DataInXhtmlEncoder(out);
                    case JAVASCRIPT: return new DataInJavaScriptEncoder(out);
                    case TEXT: return null; // No conversion necessary
                    case XHTML: return new DataInXhtmlEncoder(out);
                    case XHTML_PRE: return new DataInXmlEncoder(out);
                }
                break;
            case XHTML:
                switch(containerType) {
                    // case DATA: return null; // No conversion necessary
                    // case HTML: return null; // XHTML is allowed to pass directly to HTML
                    case JAVASCRIPT: return new DataInJavaScriptEncoder(out);
                    case TEXT: return null; // No conversion necessary
                    case XHTML: return null; // No conversion necessary
                    case XHTML_PRE: return new DataInXmlEncoder(out);
                }
                break;
            case XHTML_PRE:
                switch(containerType) {
                    // case DATA: return null; // No conversion necessary
                    // case HTML: return new XhtmlPreInXhtmlEncoder(out);
                    case JAVASCRIPT: return new DataInJavaScriptEncoder(out);
                    case TEXT: return null; // No conversion necessary
                    case XHTML: return new XhtmlPreInXhtmlEncoder(out);
                    case XHTML_PRE: return null; // No conversion necessary
                }
                break;
        }
        throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaEncoder.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
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
