/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Protects a set of parameters from modification.
 *
 * @author  AO Industries, Inc.
 */
public class UnmodifiableHttpParameters implements HttpParameters {

	/**
	 * Wraps the given parameters to ensure they are unmodifiable.
	 */
	public static HttpParameters wrap(HttpParameters wrapped) {
		// Empty are unmodifiable
		if(wrapped==EmptyParameters.getInstance()) return wrapped;
		// ServletRequest parameters are unmodifiable already
		if(wrapped instanceof ServletRequestParameters) return wrapped;
		// Already wrapped
		if(wrapped instanceof UnmodifiableHttpParameters) return wrapped;
		// Wrapping necessary
		return new UnmodifiableHttpParameters(wrapped);
	}

	private final HttpParameters wrapped;

	private UnmodifiableHttpParameters(HttpParameters wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public String getParameter(String name) {
		return wrapped.getParameter(name);
	}

	@Override
	public Iterator<String> getParameterNames() {
		return AoCollections.unmodifiableIterator(wrapped.getParameterNames());
	}

	@Override
	public List<String> getParameterValues(String name) {
		return Collections.unmodifiableList(wrapped.getParameterValues(name));
	}

	@Override
	public Map<String,List<String>> getParameterMap() {
		Map<String,List<String>> wrappedMap = wrapped.getParameterMap();
		Map<String,List<String>> map = new LinkedHashMap<>(wrappedMap.size()*4/3+1);
		for(Map.Entry<String,List<String>> entry : wrappedMap.entrySet()) {
			map.put(
				entry.getKey(),
				Collections.unmodifiableList(
					entry.getValue()
				)
			);
		}
		return Collections.unmodifiableMap(map);
	}
}
