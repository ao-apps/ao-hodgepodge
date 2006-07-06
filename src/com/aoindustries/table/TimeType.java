package com.aoindustries.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.*;

/**
 * Useful for displaying any generic type of object.
 *
 * @author  AO Industries, Inc.
*/
public class TimeType implements Type {

    private static final TimeType type=new TimeType();

    public static TimeType getInstance() {
        return type;
    }

    private TimeType() {
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
        return SQLUtility.getDateTime(time);
    }
    
    public Class getTypeClass() {
        return Long.class;
    }
}