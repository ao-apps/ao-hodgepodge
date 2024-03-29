/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2019, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.tree;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a deep copy of a tree.  The value objects are not copied.  This has two purposes:
 * <ol>
 *   <li>
 *     If the source tree accesses resources that may be slow, the tree may be
 *     copied in a background thread and then the swing components updated from
 *     this copy.
 *   </li>
 *   <li>
 *     If it is necessary to operate on a copy of the tree that will not throw
 *     any exceptions during traversal, then by using this technique any
 *     exception will occur during the copy phase.  The copy should not throw
 *     any exceptions.
 *   </li>
 * </ol>
 *
 * @author  AO Industries, Inc.
 */
public class TreeCopy<E> implements Tree<E> {

  private final List<Node<E>> rootNodes;

  public TreeCopy(Tree<E> source) throws IOException, SQLException {
    List<Node<E>> sourceRootNodes = source.getRootNodes();
    int size = sourceRootNodes.size();
    if (size == 0) {
      // No roots
      rootNodes = Collections.emptyList();
    } else if (size == 1) {
      // Single root
      Node<E> nodeCopy = new NodeCopy<>(sourceRootNodes.get(0));
      rootNodes = Collections.singletonList(nodeCopy);
    } else {
      // Multiple roots
      List<Node<E>> newRootNodes = new ArrayList<>(size);
      for (Node<E> rootNode : sourceRootNodes) {
        newRootNodes.add(new NodeCopy<>(rootNode));
      }
      rootNodes = Collections.unmodifiableList(newRootNodes);
    }
  }

  public TreeCopy(Tree<E> source, NodeFilter<E> nodeFilter) throws IOException, SQLException {
    List<Node<E>> sourceRootNodes = source.getRootNodes();

    // Apply filter
    List<Node<E>> filteredRootNodes = new ArrayList<>(sourceRootNodes.size());
    for (Node<E> sourceRootNode : sourceRootNodes) {
      if (!nodeFilter.isNodeFiltered(sourceRootNode)) {
        filteredRootNodes.add(sourceRootNode);
      }
    }

    int size = filteredRootNodes.size();
    if (size == 0) {
      // No roots
      rootNodes = Collections.emptyList();
    } else if (size == 1) {
      // Single root
      Node<E> nodeCopy = new NodeCopy<>(filteredRootNodes.get(0), nodeFilter);
      rootNodes = Collections.singletonList(nodeCopy);
    } else {
      // Multiple roots
      List<Node<E>> newRootNodes = new ArrayList<>(size);
      for (Node<E> rootNode : filteredRootNodes) {
        newRootNodes.add(new NodeCopy<>(rootNode, nodeFilter));
      }
      rootNodes = Collections.unmodifiableList(newRootNodes);
    }
  }

  /**
   * Gets the list of root nodes.
   */
  @Override
  public List<Node<E>> getRootNodes() {
    return rootNodes;
  }
}
