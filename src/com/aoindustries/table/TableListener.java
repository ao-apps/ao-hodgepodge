package com.aoindustries.table;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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