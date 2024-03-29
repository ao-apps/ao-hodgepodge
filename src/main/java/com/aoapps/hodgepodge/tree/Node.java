/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2016, 2021, 2022  AO Industries, Inc.
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
import java.util.List;

/**
 * An abstract structure for trees.  Each tree may have multiple roots.
 *
 * @author  AO Industries, Inc.
*/
public interface Node<E> {

  /**
   * Gets the list of direct children of this node.
   * If this node cannot have children then return <code>null</code>.
   * If this node can have children but there are none, return an empty List.
   */
  List<Node<E>> getChildren() throws IOException, SQLException;

  /**
   * Gets the value contained in this node.  Comparisons of this value
   * will be performed using the equals method.
   */
  E getValue();
}
