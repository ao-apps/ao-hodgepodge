package com.aoindustries.util;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class ObjectFilter extends DataFilter {

    public ObjectFilter(String expression) {
        super(expression);
    }
    
    public boolean matches(Object O) {
        if(O==null) return false;
        String S=O.toString();
        switch(function) {
            case LIKE: return S.toLowerCase().indexOf(value.toLowerCase())!=-1;
            case NOT_LIKE: return S.toLowerCase().indexOf(value.toLowerCase())==-1;
            case EQUALS: return S.equalsIgnoreCase(value);
            case NOT_EQUALS: return !S.equalsIgnoreCase(value);
            case GREATER_THAN: return S.compareToIgnoreCase(value)>0;
            case GREATER_THAN_OR_EQUAL: return S.compareToIgnoreCase(value)>=0;
            case LESS_THAN: return S.compareToIgnoreCase(value)<0;
            case LESS_THAN_OR_EQUAL: return S.compareToIgnoreCase(value)<=0;
            default: throw new RuntimeException("Unexpected function: "+function);
        }
    }
}