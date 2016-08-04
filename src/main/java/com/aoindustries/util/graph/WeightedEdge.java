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
 * A weighted edge (or arc) between two vertices.
 */
public class WeightedEdge<V,W extends Weight<?>> extends Edge<V> {

    protected final W weight;

    public WeightedEdge(V from, V to, W weight) {
        super(from, to);
        this.weight = weight;
    }

    /**
     * The weight of the edge.
     */
    final public W getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return super.toString()+" ("+weight+')';
    }
}
