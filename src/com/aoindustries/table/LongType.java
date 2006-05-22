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
public class LongType implements Type {

    private static final LongType type=new LongType();

    public static LongType getInstance() {
        return type;
    }

    private LongType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            return O2==null?1:((Long)O1).compareTo((Long)O2);
        }
    }
    
    public Object getDisplay(Object O) {
        return O;
    }
    
    public Class getTypeClass() {
        return Long.class;
    }
}