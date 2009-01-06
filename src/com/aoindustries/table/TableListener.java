package com.aoindustries.table;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * When registered as a listener on a <code>Table</code>, a
 * <code>TableListener</code> is notified when the data in a table
 * has been updated.
 *
 * @see  Table#addTableListener(TableListener)
 *
 * @author  AO Industries, Inc.
 */
public interface TableListener {

    /**
     * Invoked when a <code>Table</code> is updated.
     */
    void tableUpdated(Table table);
}