/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2019  AO Industries, Inc.
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
package com.aoindustries.util.tree;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a copy of a Node.
 *
 * @see TreeCopy
 *
 * @author  AO Industries, Inc.
*/
public class NodeCopy<E> implements Node<E> {

	private final E value;
	private final List<Node<E>> children;

	public NodeCopy(Node<E> node) throws IOException, SQLException {
		this.value = node.getValue();
		List<Node<E>> nodeChildren = node.getChildren();
		if(nodeChildren==null) {
			// No children allowed
			children = null;
		} else {
			int size = nodeChildren.size();
			if(size==0) {
				// No children
				children = Collections.emptyList();
			} else if(size==1) {
				// One child
				Node<E> nodeCopy = new NodeCopy<>(nodeChildren.get(0));
				children = Collections.singletonList(nodeCopy);
			} else {
				// Multiple children
				List<Node<E>> childrenCopy = new ArrayList<>(size);
				for(Node<E> child : nodeChildren) childrenCopy.add(new NodeCopy<>(child));
				children = Collections.unmodifiableList(childrenCopy);
			}
		}
	}

	public NodeCopy(Node<E> node, NodeFilter<E> nodeFilter) throws IOException, SQLException {
		this.value = node.getValue();
		List<Node<E>> nodeChildren = node.getChildren();
		if(nodeChildren==null) {
			// No children allowed
			children = null;
		} else {
			// Apply filter
			List<Node<E>> filteredChildren = new ArrayList<>(nodeChildren.size());
			for(Node<E> child : nodeChildren) if(!nodeFilter.isNodeFiltered(child)) filteredChildren.add(child);

			int size = filteredChildren.size();
			if(size==0) {
				// No children
				children = Collections.emptyList();
			} else if(size==1) {
				// One child
				Node<E> nodeCopy = new NodeCopy<>(filteredChildren.get(0), nodeFilter);
				children = Collections.singletonList(nodeCopy);
			} else {
				// Multiple children
				List<Node<E>> childrenCopy = new ArrayList<>(size);
				for(Node<E> child : filteredChildren) childrenCopy.add(new NodeCopy<>(child, nodeFilter));
				children = Collections.unmodifiableList(childrenCopy);
			}
		}
	}

	@Override
	public List<Node<E>> getChildren() {
		return children;
	}

	@Override
	public E getValue() {
		return value;
	}
}
