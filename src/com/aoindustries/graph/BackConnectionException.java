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
package com.aoindustries.graph;

/**
 * Thrown when a back connection doesn't exist for a connection in a back connected graph.
 *
 * @author  AO Industries, Inc.
 */
public class BackConnectionException extends GraphException {

    private static String getMessage(
        BackConnectedDirectedGraphVertex<?,?> vertex,
        BackConnectedDirectedGraphVertex<?,?> connected
    ) {
        return
            "No back connection matching connection:\n"
            + "    vertex="+vertex.getClass().getName()+"(\""+vertex.toString()+"\")\n"
            + "    connected="+connected.getClass().getName()+"(\""+connected.toString()+"\")"
        ;
    }

    private final BackConnectedDirectedGraphVertex<?,?> vertex;
    private final BackConnectedDirectedGraphVertex<?,?> connected;

    BackConnectionException(
        BackConnectedDirectedGraphVertex<?,?> vertex,
        BackConnectedDirectedGraphVertex<?,?> connected
    ) {
        super(getMessage(vertex, connected));
        this.vertex = vertex;
        this.connected = connected;
    }

    /**
     * Gets the vertex that the connection was from.
     */
    public BackConnectedDirectedGraphVertex<?,?> getVertex() {
        return vertex;
    }

    /**
     * Gets the connected vertex that doesn't back connect.
     */
    public BackConnectedDirectedGraphVertex<?,?> getConnected() {
        return connected;
    }
}
