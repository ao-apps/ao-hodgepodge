package com.aoindustries.tree;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.Collections;
import java.util.List;

/**
 * A set of static Tree-related classes, in the flavor of <code>java.util.Collections</code>.
 *
 * @author  AO Industries, Inc.
*/
public class Trees {

    /**
     * No instances.
     */
    private Trees() {}

    /**
     * @see #emptyTree()
     */
    public static final Tree EMPTY_TREE = new EmptyTree();

    /**
     * Returns the empty list (immutable).
     */
    @SuppressWarnings({"unchecked"})
    public static final <T> Tree<T> emptyTree() {
        return (Tree<T>) EMPTY_TREE;
    }

    private static class EmptyTree implements Tree {
        public List<Node> getRootNodes() {
            return Collections.emptyList();
        }
    }
}
