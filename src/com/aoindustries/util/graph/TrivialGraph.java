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
import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * A trivial graph is unmodifiable and has one vertex and no edges.
 *
 * @author  AO Industries, Inc.
 */
public class TrivialGraph<V,E extends Edge<V>> extends AbstractCollection<V> implements Graph<V,E> {

    private final V vertex;
    
    public TrivialGraph(V vertex) {
        this.vertex = vertex;
    }

    // <editor-fold desc="Collection Implementation">
    @Override
    public Iterator<V> iterator() {
        return AoCollections.singletonIterator(vertex);
    }

    @Override
    public int size() {
        return 1;
    }
    // </editor-fold>

    // <editor-fold desc="Graph Implementation">
    /**
     * Always consistent.
     */
    @Override
    public void checkGraph() {
        // Nothing to do
    }

    @Override
    public Set<E> getEdgesFrom(V from) {
        return Collections.emptySet();
    }

    @Override
    public Set<E> getEdgesTo(V to) {
        return Collections.emptySet();
    }

    @Override
    public Set<E> getEdges() {
        return Collections.emptySet();
    }
    // </editor-fold>
}
