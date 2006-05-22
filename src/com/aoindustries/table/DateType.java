package com.aoindustries.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.*;

/**
 * Useful for displaying any generic type of object.
 *
 * @author  AO Industries, Inc.
*/
public class DateType implements Type {

    private static final DateType type=new DateType();

    public static DateType getInstance() {
        return type;
    }

    private DateType() {
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
        return SQLUtility.getDate(time);
    }
    
    public Class getTypeClass() {
        return Long.class;
    }
}