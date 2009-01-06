package com.aoindustries.profiler;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

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