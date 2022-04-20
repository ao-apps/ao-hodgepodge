/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2013, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.graph;

import com.aoapps.collections.AoCollections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A symmetric directed acyclic graph checker.
 *
 * @param  <Ex>  An arbitrary exception type that may be thrown
 *
 * @author  AO Industries, Inc.
 */
// TODO: Ex extends Throwable
public class SymmetricAcyclicGraphChecker<V, Ex extends Exception> implements GraphChecker<Ex> {

  private final SymmetricMultiGraph<V, ?, ? extends Ex> graph;
  private final boolean isForward;

  public SymmetricAcyclicGraphChecker(SymmetricMultiGraph<V, ?, ? extends Ex> graph, boolean isForward) {
    this.graph = graph;
    this.isForward = isForward;
  }

  private enum Color {WHITE, GRAY, BLACK}

  /**
   * Test the graph for cycles and makes sure that all connections are consistent with back connections.
   *
   * Cycle algorithm adapted from:
   *     http://www.personal.kent.edu/~rmuhamma/Algorithms/MyAlgorithms/GraphAlgor/depthSearch.htm
   *     http://www.eecs.berkeley.edu/~kamil/teaching/sp03/041403.pdf
   *
   * In the case of a multigraph, any number of edges one direction is considered a match to any number
   * of edges back.  The number does not need to be equal.
   *
   * @throws AsymmetricException where the edges are not symmetric
   * @throws CycleException if there is a cycle in the graph
   */
  @Override
  public void checkGraph() throws AsymmetricException, CycleException, Ex {
    Set<V> vertices = graph.getVertices();
    Map<V, Color> colors = AoCollections.newHashMap(vertices.size());
    Map<V, V> predecessors = new HashMap<>(); // Could this be a simple sequence like TopologicalSorter?  Any benefit?
    for (V v : vertices) {
      if (!colors.containsKey(v)) {
        doCheck(colors, predecessors, v);
      }
    }
  }

  /**
   * @throws CycleException if there is a cycle in the graph
   */
  private void doCheck(Map<V, Color> colors, Map<V, V> predecessors, V vertex) throws AsymmetricException, CycleException, Ex {
    colors.put(vertex, Color.GRAY);
    for (Edge<V> vEdge : isForward ? graph.getEdgesFrom(vertex) : graph.getEdgesTo(vertex)) {
      //if (!isForward) {
      //    System.out.println("BREAKPOINT");
      //}
      V connected = isForward ? vEdge.getTo() : vEdge.getFrom();
      // The directed edges should match
      if (
        !(
          isForward
          ? graph.getEdgesTo(connected).contains(new Edge<>(vertex, connected))
          : graph.getEdgesFrom(connected).contains(new Edge<>(connected, vertex))
        )
      ) {
        throw new AsymmetricException(vertex, connected);
      }
      // Check for cycle
      Color uMark = colors.get(connected);
      if (Color.GRAY == uMark /* && child.equals(predecessors.get(obj))*/) {
        List<V> vertices = new ArrayList<>();
        vertices.add(connected);
        V pred = vertex;
        while (pred != null) {
          vertices.add(pred);
          pred = pred.equals(connected) ? null : predecessors.get(pred);
        }
        throw new CycleException(AoCollections.optimalUnmodifiableList(vertices));
      }
      if (uMark == null) {
        predecessors.put(connected, vertex);
        doCheck(colors, predecessors, connected);
      }
    }
    predecessors.remove(vertex);
    colors.put(vertex, Color.BLACK);
  }
}
