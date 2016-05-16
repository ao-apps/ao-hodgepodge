/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015, 2016  AO Industries, Inc.
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
package com.aoindustries.net;

import com.aoindustries.util.StringUtility;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Encoding helper utilities.
 * TODO: Rename to EncodingUtils once com.aoindustries.util.EncodingUtils has been eliminated.
 *
 * @author  AO Industries, Inc.
 */
public class UrlUtils {

    private UrlUtils() {
    }

    private static final char[] noEncodeCharacters = {
        '?', ':', '/', ';', '#', '+'
    };

    /**
     * UTF-8 encodes the URL up to the first ?, if present.  Does not encode
     * any characters in the set { '?', ':', '/', ';', '#', '+' }.
	 *
	 * Encodes tel: (case-sensitive) urls by relacing spaces with hyphens.
	 *
	 * @see  #decodeUrlPath(java.lang.String) 
     */
    public static String encodeUrlPath(String href) throws UnsupportedEncodingException {
		if(href.startsWith("tel:")) return href.replace(' ', '-');
        int len = href.length();
        int pos = 0;
        StringBuilder SB = new StringBuilder(href.length()*2); // Leave a little room for encoding
        while(pos<len) {
            int nextPos = StringUtility.indexOf(href, noEncodeCharacters, pos);
            if(nextPos==-1) {
                SB.append(URLEncoder.encode(href.substring(pos, len), "UTF-8"));
                pos = len;
            } else {
                SB.append(URLEncoder.encode(href.substring(pos, nextPos), "UTF-8"));
                char nextChar = href.charAt(nextPos);
                if(nextChar=='?') {
                    // End encoding
                    SB.append(href, nextPos, len);
                    pos = len;
                } else {
                    SB.append(nextChar);
                    pos = nextPos+1;
                }
            }
        }
        return SB.toString();
    }

	/**
     * UTF-8 decodes the URL up to the first ?, if present.  Does not decode
     * any characters in the set { '?', ':', '/', ';', '#', '+' }.
	 *
	 * Does not decode tel: urls (case-sensitive).
	 * 
	 * @see  #encodeUrlPath(java.lang.String) 
     */
    public static String decodeUrlPath(String href) throws UnsupportedEncodingException {
		if(href.startsWith("tel:")) return href;
        int len = href.length();
        int pos = 0;
        StringBuilder SB = new StringBuilder(href.length()*2); // Leave a little room for encoding
        while(pos<len) {
            int nextPos = StringUtility.indexOf(href, noEncodeCharacters, pos);
            if(nextPos==-1) {
                SB.append(URLDecoder.decode(href.substring(pos, len), "UTF-8"));
                pos = len;
            } else {
                SB.append(URLDecoder.decode(href.substring(pos, nextPos), "UTF-8"));
                char nextChar = href.charAt(nextPos);
                if(nextChar=='?') {
                    // End decoding
                    SB.append(href, nextPos, len);
                    pos = len;
                } else {
                    SB.append(nextChar);
                    pos = nextPos+1;
                }
            }
        }
        return SB.toString();
    }
}
