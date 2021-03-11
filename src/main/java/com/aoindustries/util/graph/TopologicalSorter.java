/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2013, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.collections.AoCollections;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Performs a topological sort of all the vertices in the provided symmetric multi graph.
 *
 * If forward, objects with no edges directed out are first.
 * If backward, objects with no edges directed in are first.
 * <p>
 * See <a href="https://wikipedia.org/wiki/Topological_sorting#Algorithms">https://wikipedia.org/wiki/Topological_sorting#Algorithms</a>
 * </p>
 *
 * @param  <Ex>  An arbitrary exception type that may be thrown
 *
 * @author  AO Industries, Inc.
 */
// TODO: Ex extends Throwable
public class TopologicalSorter<V, Ex extends Exception> implements GraphSorter<V, Ex> {

	private final SymmetricMultiGraph<V, ?, ? extends Ex> graph;
	private final boolean isForward;

	public TopologicalSorter(SymmetricMultiGraph<V, ?, ? extends Ex> graph, boolean isForward) {
		this.graph = graph;
		this.isForward = isForward;
	}

	@Override
	public Set<V> sortGraph() throws CycleException, Ex {
		Set<V> vertices = graph.getVertices();
		final int size = vertices.size();
		Set<V> visited = AoCollections.newHashSet(size);
		Set<V> sequence = new LinkedHashSet<>();
		//L ← Empty list that will contain the sorted nodes
		Set<V> L = AoCollections.newLinkedHashSet(size);
		//S ← Set of all nodes with no incoming edges
		//for each node n in S do
		for(V n : vertices) {
			if(
				// Getting edges can be expensive, while checking visited should always be cheap
				!visited.contains(n)
				// This check is looking for starting nodes
				&& (isForward ? graph.getEdgesTo(n) : graph.getEdgesFrom(n)).isEmpty()
			) {
				topologicalSortVisit(n, L, visited, sequence);
			}
		}
		if(L.size() != size) {
			throw new IllegalArgumentException(
				"Not all vertices added.  Does the symmetric graph have consistent edges to and from?\n"
				+ "    vertices(" + size + "): " + vertices
				+ "    results(" + L.size() + "): " + L
			);
		}
		return L;
	}

	//function visit(node n)
	private void topologicalSortVisit(V n, Set<V> L, Set<V> visited, Set<V> sequence) throws CycleException, Ex {
		//    if n has not been visited yet then
		//        mark n as visited
		if(visited.add(n)) {
			//        for each node m with an edge from n to m do
			for(Edge<V> e : (isForward ? graph.getEdgesFrom(n) : graph.getEdgesTo(n))) {
				V m = isForward ? e.getTo() : e.getFrom();
				//            visit(m)
				if(!sequence.add(m)) {
					List<V> vertices = new ArrayList<>();
					boolean found = false;
					for(V seq : sequence) {
						if(!found && seq.equals(m)) found = true;
						if(found) vertices.add(seq);
					}
					vertices.add(m);
					throw new CycleException(AoCollections.optimalUnmodifiableList(vertices));
				}
				topologicalSortVisit(m, L, visited, sequence);
				sequence.remove(m);
			}
			//        add n to L
			if(!L.add(n)) throw new AssertionError();
		}
	}
}
