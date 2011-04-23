/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A modifiable parameter map.
 *
 * @author  AO Industries, Inc.
 */
public class HttpParametersMap implements MutableHttpParameters {

    private final Map<String,List<String>> map = new TreeMap<String,List<String>>();
    private final Map<String,List<String>> unmodifiableMap = Collections.unmodifiableMap(map);

    /**
     * Creates an empty set of parameters.
     */
    public HttpParametersMap() {
    }

    /**
     * Parses the provided URL-Encoded parameter string in the UTF-8 encoding.
     */
    public HttpParametersMap(String parameters) throws UnsupportedEncodingException {
        this(parameters, "UTF-8");
    }

    /**
     * Parses the provided URL-Encoded parameter string.
     * It is strongly recommended to use UTF-8 encoding.
     */
    public HttpParametersMap(String parameters, String encoding) throws UnsupportedEncodingException {
        for(String nameValue : StringUtility.splitString(parameters, '&')) {
            int pos = nameValue.indexOf('=');
            String name;
            String value;
            if(pos==-1) {
                name = URLDecoder.decode(nameValue, encoding);
                value = null;
            } else {
                name = URLDecoder.decode(nameValue.substring(0, pos), encoding);
                value = URLDecoder.decode(nameValue.substring(pos+1), encoding);
            }
            addParameter(name, value);
        }
    }

    @Override
    public String getParameter(String name) {
        List<String> values = map.get(name);
        if(values==null) return null;
        assert !values.isEmpty();
        return values.get(0);
    }

    @Override
    public Iterator<String> getParameterNames() {
        return unmodifiableMap.keySet().iterator();
    }

    @Override
    public List<String> getParameterValues(String name) {
        return unmodifiableMap.get(name);
    }

    @Override
    public Map<String, List<String>> getParameterMap() {
        return unmodifiableMap;
    }

    @Override
    public void addParameter(String name, String value) {
        List<String> values = map.get(name);
        if(values==null) map.put(name, values = new ArrayList<String>());
        values.add(value);
    }
}
