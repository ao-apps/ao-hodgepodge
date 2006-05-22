package com.aoindustries.profiler;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.StringUtility;
import java.io.*;
import java.util.*;

/**
 * @author  AO Industries, Inc.
 */
final public class MethodProfile {

    final int level;
    private final Class clazz;
    private final String method;
    private final Object param1;
    long useCount=0;
    long totalTime=0;
    long minTime=Long.MAX_VALUE;
    long maxTime=Long.MIN_VALUE;

    MethodProfile(int level, Class clazz, String method, Object param1) {
        this.level=level;
        this.clazz=clazz;
        this.method=method;
        this.param1=param1;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelString() {
        return Profiler.getProfilerLevelString(level);
    }

    public Class getProfiledClass() {
        return clazz;
    }

    public boolean equals(Object O) {
        return O!=null && (O instanceof MethodProfile) ? equals((MethodProfile)O):false;
    }
    
    public boolean equals(MethodProfile mp) {
        return this==mp || equals(mp.level, mp.clazz, mp.method, mp.param1);
    }

    public boolean equals(int level, Class clazz, String method, Object param1) {
        return
            this.level==level
            && this.clazz==clazz
            && this.method.equals(method)
            && this.param1==null?param1==null:this.param1.equals(param1)
        ;
    }

    private int hashCode;
    private boolean hashCalculated;
    public int hashCode() {
        if(!hashCalculated) {
            hashCode=hashCode(clazz, method, param1);
            hashCalculated=true;
        }
        return hashCode;
    }

    public static int hashCode(Class clazz, String method, Object param1) {
        int hash=clazz.hashCode() + method.hashCode();
        if(param1!=null) hash+=param1.hashCode();
        return hash;
    }

    public Object getParameter1() {
        return param1;
    }
    
    public String getMethodName() {
        return method;
    }
    
    public long getUseCount() {
        return useCount;
    }
    
    public long getTotalTime() {
        return totalTime;
    }
    
    public String getTotalTimeString() {
        return StringUtility.getDecimalTimeLengthString(totalTime);
    }
    
    public long getMinTime() {
        return minTime;
    }
    
    public String getMinTimeString() {
        return minTime==Long.MAX_VALUE ? "" : StringUtility.getDecimalTimeLengthString(minTime);
    }

    public long getMaxTime() {
        return maxTime;
    }

    public String getMaxTimeString() {
        return maxTime==Long.MIN_VALUE ? "" : StringUtility.getDecimalTimeLengthString(maxTime);
    }
}