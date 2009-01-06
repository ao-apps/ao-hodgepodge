package com.aoindustries.util;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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