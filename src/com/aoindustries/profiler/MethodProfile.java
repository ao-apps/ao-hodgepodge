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

import com.aoindustries.util.StringUtility;
import java.io.Serializable;

/**
 * Immutable snapshot of <code>PrivateMethodProfile</code> used for reporting.
 *
 * @author  AO Industries, Inc.
 */
final public class MethodProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int level;
    private final String classname;
    private final String method;
    private final String param1;
    private final long useCount;
    private final long totalTime;
    private final long minTime;
    private final long maxTime;

    /**
     * Creates a clone of the provided object.
     */
    MethodProfile(PrivateMethodProfile other) {
        this.level = other.level;
        this.classname = other.getProfiledClass().getName();
        this.method = other.getMethodName();
        Object oParam1 = other.getParameter1();
        this.param1 = oParam1==null ? null : oParam1.toString();
        this.useCount = other.useCount;
        this.totalTime = other.totalTime;
        this.minTime = other.minTime;
        this.maxTime = other.maxTime;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelString() {
        return Profiler.getProfilerLevelString(level);
    }

    public String getProfiledClassName() {
        return classname;
    }

    @Override
    public boolean equals(Object O) {
        return O!=null && (O instanceof MethodProfile) ? equals((MethodProfile)O):false;
    }
    
    public boolean equals(MethodProfile mp) {
        return this==mp || equals(mp.level, mp.classname, mp.method, mp.param1);
    }

    public boolean equals(int level, String classname, String method, String param1) {
        return
            this.level==level
            && this.classname.equals(classname)
            && this.method.equals(method)
            && this.param1==null?param1==null:this.param1.equals(param1)
        ;
    }

    @Override
    public int hashCode() {
        return hashCode(classname, method, param1);
    }

    public static int hashCode(String classname, String method, String param1) {
        int hash=classname.hashCode() + method.hashCode();
        if(param1!=null) hash+=param1.hashCode();
        return hash;
    }

    public String getParameter1() {
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