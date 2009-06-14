package com.aoindustries.tree;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
                Node<E> nodeCopy = new NodeCopy<E>(nodeChildren.get(0));
                children = Collections.singletonList(nodeCopy);
            } else {
                // Multiple children
                List<Node<E>> childrenCopy = new ArrayList<Node<E>>(size);
                for(Node<E> child : nodeChildren) childrenCopy.add(new NodeCopy<E>(child));
                children = Collections.unmodifiableList(childrenCopy);
            }
        }
    }

    public List<Node<E>> getChildren() {
        return children;
    }

    public E getValue() {
        return value;
    }
}
