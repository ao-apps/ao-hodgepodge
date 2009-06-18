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
        if(size==0) {
            // No roots
            rootNodes = Collections.emptyList();
        } else if(size==1) {
            // Single root
            Node<E> nodeCopy = new NodeCopy<E>(sourceRootNodes.get(0));
            rootNodes = Collections.singletonList(nodeCopy);
        } else {
            // Multiple roots
            List<Node<E>> newRootNodes = new ArrayList<Node<E>>(size);
            for(Node<E> rootNode : sourceRootNodes) newRootNodes.add(new NodeCopy<E>(rootNode));
            rootNodes = Collections.unmodifiableList(newRootNodes);
        }
    }

    public TreeCopy(Tree<E> source, NodeFilter<E> nodeFilter) throws IOException, SQLException {
        List<Node<E>> sourceRootNodes = source.getRootNodes();

        // Apply filter
        List<Node<E>> filteredRootNodes = new ArrayList<Node<E>>(sourceRootNodes.size());
        for(Node<E> sourceRootNode : sourceRootNodes) if(!nodeFilter.isNodeFiltered(sourceRootNode)) filteredRootNodes.add(sourceRootNode);

        int size = filteredRootNodes.size();
        if(size==0) {
            // No roots
            rootNodes = Collections.emptyList();
        } else if(size==1) {
            // Single root
            Node<E> nodeCopy = new NodeCopy<E>(filteredRootNodes.get(0), nodeFilter);
            rootNodes = Collections.singletonList(nodeCopy);
        } else {
            // Multiple roots
            List<Node<E>> newRootNodes = new ArrayList<Node<E>>(size);
            for(Node<E> rootNode : filteredRootNodes) newRootNodes.add(new NodeCopy<E>(rootNode, nodeFilter));
            rootNodes = Collections.unmodifiableList(newRootNodes);
        }
    }

    /**
     * Gets the list of root nodes.
     */
    public List<Node<E>> getRootNodes() {
        return rootNodes;
    }
}
