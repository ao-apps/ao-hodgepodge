package com.aoindustries.util;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class IntegerFilter extends DataFilter {

    public IntegerFilter(String expression) {
        super(expression);
    }
    
    public boolean matches(Object O) {
        if(O==null) return false;
        Integer I=(Integer)O;
        switch(function) {
            case LIKE: return I.toString().indexOf(value.toLowerCase())!=-1;
            case NOT_LIKE: return I.toString().indexOf(value.toLowerCase())==-1;
            case EQUALS: return I.intValue()==Integer.parseInt(value);
            case NOT_EQUALS: return I.intValue()!=Integer.parseInt(value);
            case GREATER_THAN: return I.intValue()>Integer.parseInt(value);
            case GREATER_THAN_OR_EQUAL: return I.intValue()>=Integer.parseInt(value);
            case LESS_THAN: return I.intValue()<Integer.parseInt(value);
            case LESS_THAN_OR_EQUAL: return I.intValue()<=Integer.parseInt(value);
            default: throw new RuntimeException("Unexpected function: "+function);
        }
    }
}