package com.aoindustries.table;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * Useful for displaying any generic type of object.
 *
 * @author  AO Industries, Inc.
*/
public class ObjectType implements Type {

    private static final ObjectType type=new ObjectType();

    public static ObjectType getInstance() {
        return type;
    }

    private ObjectType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            return O2==null?1:O1.toString().compareTo(O2.toString());
        }
    }
    
    public Object getDisplay(Object O) {
        return O;
    }
    
    public Class getTypeClass() {
        return Object.class;
    }
}