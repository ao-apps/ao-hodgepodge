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

import com.aoindustries.util.EnumerationIterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

/**
 * Gets parameters from the provided request.
 *
 * @author  AO Industries, Inc.
 */
public class ServletRequestParameters implements HttpParameters {

    private final ServletRequest request;

    public ServletRequestParameters(ServletRequest request) {
        this.request = request;
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<String> getParameterNames() {
        return new EnumerationIterator<String>(request.getParameterNames());
    }

    @Override
    public List<String> getParameterValues(String name) {
        String[] values = request.getParameterValues(name);
        return values==null ? null : Collections.unmodifiableList(Arrays.asList(values));
    }

    @Override
    public Map<String, List<String>> getParameterMap() {
        @SuppressWarnings("unchecked") Map<String,String[]> requestMap = request.getParameterMap();
        Map<String,List<String>> map = new LinkedHashMap<String,List<String>>(requestMap.size()*4/3+1);
        for(Map.Entry<String,String[]> entry : requestMap.entrySet()) {
            map.put(entry.getKey(), Collections.unmodifiableList(Arrays.asList(entry.getValue())));
        }
        return Collections.unmodifiableMap(map);
    }
}
