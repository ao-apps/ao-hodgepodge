package com.aoindustries.tree;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.sql.SQLException;

/**
 * Filters may be applied while copying trees.
 *
 * @author  AO Industries, Inc.
*/
public interface NodeFilter<E> {

    boolean isNodeFiltered(Node<E> node) throws IOException, SQLException;
}
