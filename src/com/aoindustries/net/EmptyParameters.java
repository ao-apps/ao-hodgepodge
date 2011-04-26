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

import com.aoindustries.util.AoCollections;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Empty parameters singleton.
 *
 * @author  AO Industries, Inc.
 */
final public class EmptyParameters implements HttpParameters {

    private static final EmptyParameters instance = new EmptyParameters();

    public static EmptyParameters getInstance() {
        return instance;
    }

    private EmptyParameters() {
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Iterator<String> getParameterNames() {
        return AoCollections.emptyIterator();
    }

    @Override
    public List<String> getParameterValues(String name) {
        return null;
    }

    @Override
    public Map<String, List<String>> getParameterMap() {
        return Collections.emptyMap();
    }
}