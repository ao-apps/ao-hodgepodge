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
package com.aoapps.hodgepodge.graph;

import com.aoapps.lang.Throwables;
import java.util.List;

/**
 * Thrown when a cycle has been detected in an acyclic graph.
 *
 * @author  AO Industries, Inc.
 */
public class CycleException extends GraphException {

	private static final long serialVersionUID = 7713106090763335656L;

	private static String getMessage(List<?> vertices) {
		StringBuilder SB = new StringBuilder();
		SB.append("Cycle exists:\n");
		for(Object v : vertices) {
			SB.append("    ").append(v.getClass().getName()).append("(\"").append(v.toString()).append("\")\n");
		}
		return SB.toString();
	}

	private final List<?> vertices;

	/**
	 * No defensive copy is made.
	 */
	CycleException(List<?> vertices) {
		super(getMessage(vertices));
		if(vertices.size()<2) throw new IllegalArgumentException("Cycle must have at least two vertices (could be the same vertex)");
		if(!vertices.get(0).equals(vertices.get(vertices.size()-1))) throw new IllegalArgumentException("Cycle must start and end on the same vertex");
		this.vertices = vertices;
	}

	/**
	 * Gets all vertices that are part of the cycle in the order they create the cycle.
	 *
	 * @return  No defensive copy
	 */
	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public List<?> getVertices() {
		return vertices;
	}

	static {
		Throwables.registerSurrogateFactory(CycleException.class, (template, cause) -> {
			CycleException newEx = new CycleException(template.vertices);
			newEx.initCause(cause);
			return newEx;
		});
	}
}
