/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.profiler;

/**
 * @author  AO Industries, Inc.
 */
final class PrivateMethodProfile {

    final int level;
    private final Class clazz;
    private final String method;
    private final Object param1;
    private final int hashCode;
    long useCount=0;
    long totalTime=0;
    long minTime=Long.MAX_VALUE;
    long maxTime=Long.MIN_VALUE;

    PrivateMethodProfile(int level, Class clazz, String method, Object param1) {
        this.level=level;
        this.clazz=clazz;
        this.method=method;
        this.param1=param1;
        this.hashCode=hashCode(clazz, method, param1);
    }

    int getLevel() {
        return level;
    }

    String getLevelString() {
        return Profiler.getProfilerLevelString(level);
    }

    Class getProfiledClass() {
        return clazz;
    }

    @Override
    public boolean equals(Object O) {
        return O!=null && (O instanceof PrivateMethodProfile) ? equals((PrivateMethodProfile)O):false;
    }
    
    boolean equals(PrivateMethodProfile mp) {
        return this==mp || equals(mp.level, mp.clazz, mp.method, mp.param1);
    }

    boolean equals(int level, Class clazz, String method, Object param1) {
        return
            this.level==level
            && this.clazz==clazz
            && this.method.equals(method)
            && this.param1==null?param1==null:this.param1.equals(param1)
        ;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    static int hashCode(Class clazz, String method, Object param1) {
        int hash=clazz.hashCode() + method.hashCode();
        if(param1!=null) hash+=param1.hashCode();
        return hash;
    }

    Object getParameter1() {
        return param1;
    }
    
    String getMethodName() {
        return method;
    }
}