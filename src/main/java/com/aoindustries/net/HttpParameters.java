/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2016  AO Industries, Inc.
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides read-only access to HttpParameters.
 *
 * @author  AO Industries, Inc.
 */
public interface HttpParameters {

	/**
	 * Gets the value for the provided parameter name or <code>null</code> if doesn't exist.
	 * If the parameter has multiple values, the first value is returned.
	 */
	String getParameter(String name);

	/**
	 * Gets an unmodifiable interator of the parameter names.
	 */
	Iterator<String> getParameterNames();

	/**
	 * Gets an unmodifiable view of all values for a multi-value parameter or <code>null</code> if has no values.
	 */
	List<String> getParameterValues(String name);

	/**
	 * Gets an unmodifiable map view of all parameters.
	 */
	Map<String,List<String>> getParameterMap();
}
