package com.aoindustries.encoding;

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
abstract public class MediaValidator extends FilterWriter implements ValidMediaFilter {

    /**
     * Gets the media validator for the given type.  If the given writer is
     * already validator for the requested type, will return the provided writer.
     *
     * @exception MediaException when unable to find an appropriate validator.
     */
    public static ValidMediaInput getMediaValidator(Locale userLocale, MediaType contentType, Writer out) throws MediaException {
        // If the existing out is already validating for this type, use it
        if(out instanceof ValidMediaInput) {
            ValidMediaInput inputValidator = (ValidMediaInput)out;
            if(inputValidator.isValidatingMediaInputType(contentType)) return inputValidator;
        }
        // Add filter if needed for the given type
        switch(contentType) {
            case JAVASCRIPT:
                return new JavaScriptValidator(out);
            case TEXT:
                return new TextValidator(out);
            case URL:
                return new UrlMediaValidator(out, userLocale);
            case XHTML:
                return new XhtmlMediaValidator(out, userLocale);
            case XHTML_PRE:
                return new XhtmlPreMediaValidator(out, userLocale);
            default:
                throw new MediaException(ApplicationResourcesAccessor.getMessage(userLocale, "MediaValidator.unableToFindValidator", contentType.getMediaType()));
        }
    }

    protected MediaValidator(Writer out) {
        super(out);
    }
}
