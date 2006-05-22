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
public class BooleanType implements Type {

    private static final BooleanType type=new BooleanType();

    public static BooleanType getInstance() {
        return type;
    }

    private BooleanType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            if(O2==null) return 1;
            boolean b1=((Boolean)O1).booleanValue();
            boolean b2=((Boolean)O2).booleanValue();
            return b1?
                (b2?0:1)
                :(b2?-1:0)
            ;
        }
    }
    
    public Object getDisplay(Object O) {
        return O;
    }
    
    public Class getTypeClass() {
        return Boolean.class;
    }
}