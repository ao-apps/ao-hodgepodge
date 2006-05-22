package com.aoindustries.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * Useful for displaying any generic type of object.
 *
 * @author  AO Industries, Inc.
*/
public class IntegerType implements Type {

    private static final IntegerType type=new IntegerType();

    public static IntegerType getInstance() {
        return type;
    }

    private IntegerType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            return O2==null?1:((Integer)O1).compareTo((Integer)O2);
        }
    }
    
    public Object getDisplay(Object O) {
        return O;
    }
    
    public Class getTypeClass() {
        return Integer.class;
    }
}