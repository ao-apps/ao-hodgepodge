/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2016, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.Throwables;

/**
 * Thrown when edges are not symmetric in a symmetric graph.
 * Symmetric is based only on existence of the edge.  Edge-specific details
 * like weight or label are not compared.
 *
 * @see  SymmetricGraph
 *
 * @author  AO Industries, Inc.
 */
public class AsymmetricException extends GraphException {

  private static final long serialVersionUID = 7461271328813491659L;

  private static String getMessage(
      Object vertex,
      Object connected
  ) {
    return
        "No back connection matching connection:\n"
            + "    vertex=" + vertex.getClass().getName() + "(\"" + vertex.toString() + "\")\n"
            + "    connected=" + connected.getClass().getName() + "(\"" + connected.toString() + "\")"
    ;
  }

  private final Object vertex;
  private final Object connected;

  <V> AsymmetricException(V vertex, V connected) {
    super(getMessage(vertex, connected));
    this.vertex = vertex;
    this.connected = connected;
  }

  <V> AsymmetricException(V vertex, V connected, Throwable cause) {
    super(getMessage(vertex, connected), cause);
    this.vertex = vertex;
    this.connected = connected;
  }

  /**
   * Gets the vertex that the connection was from.
   */
  public Object getVertex() {
    return vertex;
  }

  /**
   * Gets the connected vertex that doesn't back connect.
   */
  public Object getConnected() {
    return connected;
  }

  static {
    Throwables.registerSurrogateFactory(AsymmetricException.class, (template, cause) ->
        new AsymmetricException(template.vertex, template.connected, cause)
    );
  }
}
