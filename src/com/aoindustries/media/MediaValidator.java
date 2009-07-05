package com.aoindustries.media;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.FilterWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * Verify that the data passing through this filter is valid for the provided media type.
 *
 * @author  AO Industries, Inc.
 */
abstract public class MediaValidator extends FilterWriter {

    /**
     * Gets the media validator for the given type.  If no validation is necessary,
     * will return the provided writer.  If the given writer is already validator
     * for the requested type, will return the provided writer.
     *
     * @exception MediaException when unable to find an appropriate validator.
     */
    public static Writer getMediaValidator(Locale userLocale, MediaType contentType, Writer out) throws MediaException {
        switch(contentType) {
            case JAVASCRIPT:
                return out; // No character restrictions
            case TEXT:
                return out; // No character restrictions
            case URL:
                return (out instanceof UrlMediaValidator) ? out : new UrlMediaValidator(out, userLocale);
            case XHTML:
                return
                    (out instanceof XmlMediaValidator)
                    || (out instanceof XhtmlPreMediaValidator) // This is more restrictive so is an acceptable substitution
                    ? out
                    : new XmlMediaValidator(out, userLocale)
                ;
            case XHTML_PRE:
                return
                    (out instanceof XhtmlPreMediaValidator)
                    ? out
                    : new XhtmlPreMediaValidator(out, userLocale)
                ;
            default:
                throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaValidator.unableToFindValidator", contentType.getMediaType()));
        }
    }

    protected MediaValidator(Writer out) {
        super(out);
    }
}
