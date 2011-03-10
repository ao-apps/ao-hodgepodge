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
package com.aoindustries.util.graph;

import java.util.List;

/**
 * A directed acyclic graph where the connections only go one direction.
 *
 * @author  AO Industries, Inc.
 */
public class DirectedAcyclicGraph<T extends DirectedGraphVertex<T,E>, E extends Exception> {

    private final Iterable<? extends T> vertices;

    public DirectedAcyclicGraph(Iterable<? extends T> vertices) {
        this.vertices = vertices;
    }

    /**
     * Test the graph for consistency.  For a directed acyclic graph, must have no cycles.
     *
     * @param iter An iterable source of vertices, will be iterated one time.  Duplicates are OK and will be ignored.
     *
     * @throws GraphCycleException if the graph is not consistent
     */
    public void checkDirectedAcyclicGraph() throws GraphCycleException, E {
        throw new RuntimeException("TODO: Not implemented");
    }

    /**
     * Performs a topological sort of the graph.
     */
    public List<T> topologicalSort(Iterable<? extends T> vertices) throws GraphCycleException, E {
        throw new RuntimeException("TODO: Not implemented");
    }
}
