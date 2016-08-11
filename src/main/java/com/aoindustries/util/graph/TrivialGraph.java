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
package com.aoindustries.util.graph;

import java.util.Collections;
import java.util.Set;

/**
 * A trivial graph is unmodifiable and has one vertex and no edges.
 *
 * @author  AO Industries, Inc.
 */
public class TrivialGraph<V,E extends Edge<V>,EX extends Exception> implements SymmetricGraph<V,E,EX> {

	private final Set<V> vertices;

	public TrivialGraph(V vertex) {
		this.vertices = Collections.singleton(vertex);
	}

	@Override
	public Set<V> getVertices() {
		return vertices;
	}

	@Override
	public Set<E> getEdgesFrom(V from) {
		return Collections.emptySet();
	}

	@Override
	public Set<E> getEdgesTo(V to) {
		return Collections.emptySet();
	}
}
