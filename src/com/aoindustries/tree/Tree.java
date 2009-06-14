package com.aoindustries.tree;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * An abstract structure for trees.  Each tree may have multiple roots.
 *
 * @author  AO Industries, Inc.
*/
public interface Tree<E> {

    /**
     * Gets the list of root nodes.  Each root node should have a
     * <code>null</code> parent.  If there are no roots, should
     * return an empty list, not null.
     */
    List<Node<E>> getRootNodes() throws IOException, SQLException;
}
