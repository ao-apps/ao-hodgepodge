/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2016, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util.graph;

import com.aoindustries.lang.Throwables;

/**
 * Exceptions indicating problems have been detected in graph state.
 *
 * @author  AO Industries, Inc.
 */
public class GraphException extends RuntimeException {

	private static final long serialVersionUID = -1829212989642756232L;

	public GraphException() {
		super();
	}

	public GraphException(String message) {
		super(message);
	}

	public GraphException(Throwable cause) {
		super(cause);
	}

	public GraphException(String message, Throwable cause) {
		super(message, cause);
	}

	static {
		Throwables.registerSurrogateFactory(GraphException.class, (template, cause) ->
			new GraphException(template.getMessage(), cause)
		);
	}
}
