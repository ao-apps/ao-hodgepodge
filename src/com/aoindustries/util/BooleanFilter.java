package com.aoindustries.util;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
public class BooleanFilter extends DataFilter {

    public BooleanFilter(String expression) {
        super();
        expression=expression.trim();
        if(expression.equalsIgnoreCase("true")) value="true";
        else if(expression.equalsIgnoreCase("false")) value="false";
        else throw new IllegalArgumentException("Unknown expression: "+expression);
        function=EQUALS;
    }
    
    public boolean matches(Object O) {
        if(O==null) return false;
        Boolean B=(Boolean)O;
        switch(function) {
            case EQUALS: return B.booleanValue()?"true".equals(value):"false".equals(value);
            default: throw new RuntimeException("Unexpected function: "+function);
        }
    }
}