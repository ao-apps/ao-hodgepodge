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

import com.aoindustries.util.AoCollections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A back-connected, directed acyclic graph.
 *
 * @author  AO Industries, Inc.
 */
public class SymmetricDirectedAcyclicGraph<T extends SymmetricDirectedGraphVertex<T,E>, E extends Exception> {

    public SymmetricDirectedAcyclicGraph() {
    }

    private enum Color {WHITE, GRAY, BLACK};

    /**
     * @throws GraphCycleException if there is a cycle in the graph
     */
    private void doCheckBackConnectedDirectedAcyclicGraph(Map<T,Color> colors, Map<T,T> predecessors, boolean isForward, T vertex) throws SymmetricException, GraphCycleException, E {
        colors.put(vertex, Color.GRAY);
        final Set<? extends T> vConnected;
        if(isForward) vConnected = vertex.getConnectedVertices();
        else vConnected = vertex.getBackConnectedVertices();
        for(T connected : vConnected) {
            // The directed edges should match
            final Set<? extends T> uBackConnected;
            if(isForward) uBackConnected = connected.getBackConnectedVertices();
            else uBackConnected = connected.getConnectedVertices();
            if(!uBackConnected.contains(vertex)) {
                throw new SymmetricException(vertex, connected);
            }
            // Check for cycle
            Color uMark = colors.get(connected);
            if(Color.GRAY==uMark /*&& child.equals(predecessors.get(obj))*/) {
                List<SymmetricDirectedGraphVertex<?,?>> vertices = new ArrayList<SymmetricDirectedGraphVertex<?,?>>();
                vertices.add(connected);
                SymmetricDirectedGraphVertex<?,?> pred = vertex;
                while(pred!=null) {
                    vertices.add(pred);
                    pred = pred.equals(connected) ? null : predecessors.get(pred);
                }
                throw new GraphCycleException(AoCollections.optimalUnmodifiableList(vertices));
            }
            if(uMark==null) {
                predecessors.put(connected, vertex);
                doCheckBackConnectedDirectedAcyclicGraph(colors, predecessors, isForward, connected);
            }
        }
        predecessors.remove(vertex);
        colors.put(vertex, Color.BLACK);
    }

    /**
     * Test the graph for cycles and makes sure that all connections are consistent with back connections.
     *
     * Cycle algorithm adapted from:
     *     http://www.personal.kent.edu/~rmuhamma/Algorithms/MyAlgorithms/GraphAlgor/depthSearch.htm
     *     http://www.eecs.berkeley.edu/~kamil/teaching/sp03/041403.pdf
     *
     * @param iter An iterable source of vertices, will be iterated one time.  Duplicates are OK and will be ignored.
     *
     * @throws GraphCycleException if the graph is not consistent
     */
    public void checkBackConnectedDirectedAcyclicGraph(Iterable<? extends T> vertices, boolean isForward) throws SymmetricException, GraphCycleException, E {
        int mapSize;
        if(vertices instanceof Collection) mapSize = ((Collection)vertices).size()*4/3+1;
        else mapSize = 100;
        Map<T,Color> colors = new HashMap<T,Color>(mapSize);
        Map<T,T> predecessors = new HashMap<T,T>();
        for(T v : vertices) {
            if(!colors.containsKey(v)) doCheckBackConnectedDirectedAcyclicGraph(colors, predecessors, isForward, v);
        }
    }

    /**
     * Performs a topological sort of the graph.
     * If forward, objects with no connections are first.
     * If backward, objects with no back connections are first.
     *
     * {@link http://en.wikipedia.org/wiki/Topological_sorting#Algorithms}
     */
    public List<T> topologicalSort(Iterable<? extends T> vertices, boolean isForward) throws GraphCycleException, E {
        int listSize;
        if(vertices instanceof Collection) listSize = ((Collection)vertices).size();
        else listSize = 100;
        Set<T> visited = new HashSet<T>(listSize*4/3+1);
        LinkedHashSet<T> sequence = new LinkedHashSet<T>();
        //L ← Empty list that will contain the sorted nodes
        List<T> L = new ArrayList<T>(listSize);
        //S ← Set of all nodes with no incoming edges
        //for each node n in S do
        for(T n : vertices) {
            if(
                // Getting vertices can be expensive, while checking visited should always be cheap
                !visited.contains(n)
                && (isForward ? n.getBackConnectedVertices() : n.getConnectedVertices()).isEmpty()
            ) {
                topologicalSortVisit(n, L, visited, sequence, isForward);
            }
        }
        return AoCollections.optimalUnmodifiableList(L);
    }

    //function visit(node n)
    private void topologicalSortVisit(T n, List<T> L, Set<T> visited, LinkedHashSet<T> sequence, boolean isForward) throws GraphCycleException, E {
        //    if n has not been visited yet then
        //        mark n as visited
        if(visited.add(n)) {
            //        for each node m with an edge from n to m do
            for(T m : (isForward ? n.getConnectedVertices() : n.getBackConnectedVertices())) {
                //            visit(m)
                if(!sequence.add(m)) {
                    List<T> vertices = new ArrayList<T>();
                    boolean found = false;
                    for(T seq : sequence) {
                        if(!found && seq.equals(m)) found = true;
                        if(found) vertices.add(seq);
                    }
                    vertices.add(m);
                    throw new GraphCycleException(AoCollections.optimalUnmodifiableList(vertices));
                }
                topologicalSortVisit(m, L, visited, sequence, isForward);
                sequence.remove(m);
            }
            //        add n to L
            L.add(n);
        }
    }
}
