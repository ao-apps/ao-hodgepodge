/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2013  AO Industries, Inc.
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Utilities using HttpParameters.
 *
 * @author  AO Industries, Inc.
 */
final public class HttpParametersUtils {

    /**
     * Adds all of the parameters to a URL.
     */
    public static String addParams(String href, HttpParameters params) throws UnsupportedEncodingException {
        if(params!=null) {
            boolean hasQuestion = href.indexOf('?')!=-1;
            StringBuilder sb = new StringBuilder(href);
            for(Map.Entry<String,List<String>> entry : params.getParameterMap().entrySet()) {
                String encodedName = URLEncoder.encode(entry.getKey(), "UTF-8");
                for(String value : entry.getValue()) {
                    if(hasQuestion) sb.append('&');
                    else {
                        sb.append('?');
                        hasQuestion = true;
                    }
                    sb.append(encodedName);
					assert value!=null : "null values no longer supported to be consistent with servlet environment";
                    sb.append('=').append(URLEncoder.encode(value, "UTF-8"));
                }
            }
            href = sb.toString();
        }
        return href;
    }

    /**
     * Make no instances.
     */
    private HttpParametersUtils() {
    }
}
