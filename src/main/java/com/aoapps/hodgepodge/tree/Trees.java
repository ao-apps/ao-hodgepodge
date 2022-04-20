/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2021, 2022  AO Industries, Inc.
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

import java.util.Collections;
import java.util.List;

/**
 * A set of static Tree-related classes, in the flavor of <code>java.util.Collections</code>.
 *
 * @author  AO Industries, Inc.
*/
public final class Trees {

  /** Make no instances. */
  private Trees() {
    throw new AssertionError();
  }

  /**
   * @see #emptyTree()
   */
  public static final Tree<?> EMPTY_TREE = new EmptyTree();

  /**
   * Returns the empty list (immutable).
   */
  @SuppressWarnings({"unchecked"})
  public static final <T> Tree<T> emptyTree() {
    return (Tree<T>) EMPTY_TREE;
  }

  private static class EmptyTree implements Tree<Object> {
    @Override
    public List<Node<Object>> getRootNodes() {
      return Collections.emptyList();
    }
  }
}
