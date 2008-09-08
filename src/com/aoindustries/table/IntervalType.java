package com.aoindustries.table;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.*;
import com.aoindustries.util.*;

/**
 * Useful for displaying any generic type of object.
 *
 * @author  AO Industries, Inc.
*/
public class IntervalType implements Type {

    private static final IntervalType type=new IntervalType();

    public static IntervalType getInstance() {
        return type;
    }

    private IntervalType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            return O2==null?1:((Long)O1).compareTo((Long)O2);
        }
    }
    
    public Object getDisplay(Object O) {
        if(O==null) return null;
        long time=((Long)O).longValue();
        if(time==-1) return null;
        return StringUtility.getTimeLengthString(time);
    }
    
    public Class getTypeClass() {
        return Long.class;
    }
}