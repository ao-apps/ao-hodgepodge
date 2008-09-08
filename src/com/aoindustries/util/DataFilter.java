package com.aoindustries.util;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @author  AO Industries, Inc.
 */
abstract public class DataFilter {

    public static final int
        LIKE=0,
        NOT_LIKE=1,
        EQUALS=2,
        NOT_EQUALS=3,
        GREATER_THAN=4,
        GREATER_THAN_OR_EQUAL=5,
        LESS_THAN=6,
        LESS_THAN_OR_EQUAL=7
    ;

    public static DataFilter getDataFilter(Class clazz, String filter) {
        if(filter==null) return null;
        if(clazz==Boolean.class) return new BooleanFilter(filter);
        if(clazz==String.class) return new StringFilter(filter);
        if(clazz==Integer.class) return new IntegerFilter(filter);
        if(clazz==Object.class) return new ObjectFilter(filter);
        throw new IllegalArgumentException("Unsupported class: "+clazz.getName());
    }

    protected int function;
    protected String value;

    public DataFilter() {
    }

    public DataFilter(String expression) {
        expression=expression.trim();
        if(expression.startsWith("=")) {
            function=EQUALS;
            value=expression.substring(1).trim();
        } else if(expression.startsWith("!=") || expression.startsWith("<>")) {
            function=NOT_EQUALS;
            value=expression.substring(2).trim();
        } else if(expression.startsWith(">=") || expression.startsWith("=>")) {
            function=GREATER_THAN_OR_EQUAL;
            value=expression.substring(2).trim();
        } else if(expression.startsWith(">")) {
            function=GREATER_THAN;
            value=expression.substring(1).trim();
        } else if(expression.startsWith("<=") || expression.startsWith("=<")) {
            function=LESS_THAN_OR_EQUAL;
            value=expression.substring(2).trim();
        } else if(expression.startsWith("<")) {
            function=LESS_THAN;
            value=expression.substring(1).trim();
        } else if(expression.startsWith("!~")) {
            function=NOT_LIKE;
            value=expression.substring(2).trim();
        } else if(expression.startsWith("!")) {
            function=NOT_LIKE;
            value=expression.substring(1).trim();
        } else if(expression.startsWith("~")) {
            function=LIKE;
            value=expression.substring(1).trim();
        } else {
            function=LIKE;
            value=expression;
        }
    }
    
    abstract public boolean matches(Object O);
}