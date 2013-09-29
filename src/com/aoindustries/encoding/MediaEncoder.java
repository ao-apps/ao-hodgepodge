/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013  AO Industries, Inc.
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

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;

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
abstract public class MediaEncoder implements ValidMediaFilter {

    /**
     * Gets the media encoder for the requested types or <code>null</code> if
     * no encoding is necessary.  When an encoder is returned it is also a validator
     * for the contentType and produces valid output for the containerType.
     * If no encoder is returned, it is necessary to use a separate validator
     * if character validation is required.
     *
	 * @param  response  Only required when contentType is MediaType.URL
	 *
     * @return the encoder or <code>null</code> if no encoding is necessary
     *
     * @exception MediaException when unable to encode the content into the container
     *                           either because it is impossible or not yet implemented.
     */
    public static MediaEncoder getInstance(HttpServletResponse response, MediaType contentType, MediaType containerType) throws MediaException {
        final MediaEncoder encoder;
        switch(contentType) {
            case JAVASCRIPT :
                switch(containerType) {
                    case JAVASCRIPT :      return null;
                    case TEXT :            return null;
                    case XHTML :           encoder = JavaScriptInXhtmlEncoder.javaScriptInXhtmlEncoder; break;
                    case XHTML_ATTRIBUTE : encoder = JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder; break;
					default :              throw new MediaException(ApplicationResources.accessor.getMessage("MediaWriter.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            case TEXT:
                switch(containerType) {
                    case JAVASCRIPT :      encoder = TextInJavaScriptEncoder.textInJavaScriptEncoder; break;
                    case TEXT :            return null;
                    case XHTML :           encoder = TextInXhtmlEncoder.textInXhtmlEncoder; break;
                    case XHTML_ATTRIBUTE : encoder = TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder; break;
					default :              throw new MediaException(ApplicationResources.accessor.getMessage("MediaWriter.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            case URL :
                switch(containerType) {
                    case JAVASCRIPT :      encoder = new UrlInJavaScriptEncoder(response); break;
                    case TEXT :            return null;
                    case URL :             return null;
                    case XHTML :           encoder = new UrlInXhtmlEncoder(response); break;
                    case XHTML_ATTRIBUTE : encoder = new UrlInXhtmlAttributeEncoder(response); break;
					default :              throw new MediaException(ApplicationResources.accessor.getMessage("MediaWriter.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                break;
            case XHTML :
                switch(containerType) {
                    case TEXT :            return null;
                    case XHTML :           return null;
					default :              throw new MediaException(ApplicationResources.accessor.getMessage("MediaWriter.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                //break;
            case XHTML_ATTRIBUTE :
                switch(containerType) {
                    case TEXT :            return null;
                    case XHTML :           return null;
                    case XHTML_ATTRIBUTE : return null;
					default :              throw new MediaException(ApplicationResources.accessor.getMessage("MediaWriter.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
                }
                //break;
			default : throw new MediaException(ApplicationResources.accessor.getMessage("MediaWriter.unableToFindEncoder", contentType.getMediaType(), containerType.getMediaType()));
        }
		// Make sure types match - bug catching
		assert encoder.getValidMediaOutputType()==containerType : "encoder.getValidMediaOutputType()!=containerType: "+encoder.getValidMediaOutputType()+"!="+containerType;
		assert encoder.isValidatingMediaInputType(contentType) : "encoder="+encoder.getClass().getName()+" is not a validator for contentType="+contentType;
        return encoder;
    }

    protected MediaEncoder() {
    }

    /**
     * <p>
     * This is called before any data is written.
     * </p>
     * <p>
     * This default implementation does nothing.
     * </p>
     */
    public void writePrefixTo(Appendable out) throws IOException {
    }

    abstract public void write(int c, Writer out) throws IOException;
	
    abstract public void write(char cbuf[], Writer out) throws IOException;

	abstract public void write(char cbuf[], int off, int len, Writer out) throws IOException;

    abstract public void write(String str, Writer out) throws IOException;

	abstract public void write(String str, int off, int len, Writer out) throws IOException;

    abstract public MediaEncoder append(char c, Appendable out) throws IOException;

	abstract public MediaEncoder append(CharSequence csq, Appendable out) throws IOException;

    abstract public MediaEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException;

    /**
     * <p>
     * This is called when no more data will be written.
     * This should also flush any internal buffers to <code>out</code>.  It
     * should not, however, call flush on <code>out</code> itself.  This is
     * to not interfere with any output buffering of <code>out</code>.
     * </p>
     * <p>
     * This default implementation does nothing.
     * </p>
     */
    public void writeSuffixTo(Appendable out) throws IOException {
    }
}
