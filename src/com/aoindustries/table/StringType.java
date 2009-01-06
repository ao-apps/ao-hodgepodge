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
public class StringType implements Type {

    private static final StringType type=new StringType();

    public static StringType getInstance() {
        return type;
    }

    private StringType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            return O2==null?1:((String)O1).compareTo((String)O2);
        }
    }
    
    public Object getDisplay(Object O) {
        return O;
    }
    
    public Class getTypeClass() {
        return String.class;
    }
}