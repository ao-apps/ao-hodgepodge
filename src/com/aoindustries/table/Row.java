package com.aoindustries.table;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * An abstract structure for rows in a table.
 *
 * @author  AO Industries, Inc.
*/
public interface Row {

    /**
     * Gets the data contained within one column index in this row.
     */
    Object getColumn(int columnIndex);

}