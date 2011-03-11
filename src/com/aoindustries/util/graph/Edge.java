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

/**
 * An edge (or arc) between two vertices.
 */
public class Edge<V> {

    protected final V from;
    protected final V to;

    public Edge(V from, V to) {
        this.from = from;
        this.to = to;
    }

    /**
     * The vertex the edge is from.
     */
    final public V getFrom() {
        return from;
    }

    /**
     * The vertex the edge is to.
     */
    final public V getTo() {
        return to;
    }

    @Override
    public String toString() {
        return from+" -> "+to;
    }

    /**
     * Two edges are equal if they have equal from and to.
     */
    @Override
    final public boolean equals(Object obj) {
        if(!(obj instanceof Edge<?>)) return false;
        Edge<?> other = (Edge<?>)obj;
        return
            from.equals(other.from)
            && to.equals(other.to)
        ;
    }

    /**
     * The hashCode is generated from the from and to.
     */
    @Override
    final public int hashCode() {
        return from.hashCode() * 31 + to.hashCode();
    }
}
