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
public class DollarType implements Type {

    private static final DollarType type=new DollarType();

    public static DollarType getInstance() {
        return type;
    }

    private DollarType() {
    }

    public int compare(Object O1, Object O2) {
        if(O1==null) {
            return O2==null?0:-1;
        } else {
            return O2==null?1:((Integer)O1).compareTo((Integer)O2);
        }
    }
    
    public Object getDisplay(Object O) {
        if(O==null) return null;
        int pennies=((Integer)O).intValue();
        if(pennies==-1) return null;
        return SQLUtility.getDecimal(pennies);
    }
    
    public Class getTypeClass() {
        return Integer.class;
    }
}