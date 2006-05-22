package com.aoindustries.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
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