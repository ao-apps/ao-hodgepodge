package com.aoindustries.table;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * An abstract structure for column types in a table.
 *
 * @author  AO Industries, Inc.
*/
public interface Type {

    /**
     * Compare two objects lexically.  Return 0 if null,
     * <0 if O1 is less than O2, or >0 if O1 is greater than O2
     */
    int compare(Object O1, Object O2);
    
    /**
     * Gets the display value object for a type.
     */
    Object getDisplay(Object O1);
    
    /**
     * Gets the class for objects of this type.
     */
    Class getTypeClass();
}
