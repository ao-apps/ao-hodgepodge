/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util;

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