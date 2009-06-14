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
public interface Node<E> {

    /**
     * Gets the list of direct children of this node.
     * If this node cannot have children then return <code>null</code>.
     * If this node can have children but there are none, return an empty List.
     */
    List<Node<E>> getChildren() throws IOException, SQLException;

    /**
     * Gets the value contained in this node.  Comparisions of this value
     * will be performed using the equals method.
     */
    E getValue();
}
