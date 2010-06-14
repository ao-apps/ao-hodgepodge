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
package com.aoindustries.reflect;

import com.aoindustries.util.WrappedException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Simplifies the invoking of methods by wrapping both the method and the parameters.
 *
 * @author  AO Industries, Inc.
 */
public class MethodCall {

    private Method method;
    
    private Class[] parameterTypes;
    
    private Object[] parameterValues;

    /**
     * Calls the method with no parameters.
     */
    public MethodCall(Method method) {
        this.method=method;
        this.parameterTypes=new Class[0];
        this.parameterValues=new Object[0];
    }

    public MethodCall(Class<?> clazz, String name) {
        try {
            this.parameterTypes=new Class[0];
            this.method=clazz.getMethod(name, parameterTypes);
            this.parameterValues=new Object[0];
        } catch(NoSuchMethodException err) {
            throw new WrappedException(
                err,
                new Object[] {
                    "clazz.getName()="+clazz.getName(),
                    "name="+name
                }
            );
        }
    }

    /**
     * Calls the method, obtaining the parameter types from the parameter values.  Any
     * <code>null</code> parameter value will result in a <code>NullPointerException</code>.
     */
    public MethodCall(Method method, Object[] parameterValues) {
        this.method=method;
        this.parameterValues=parameterValues;
        int paramCount=parameterValues.length;
        parameterTypes=new Class[paramCount];
        for(int c=0;c<paramCount;c++) {
            parameterTypes[c]=parameterValues[c].getClass();
        }
    }
    
    /**
     * Calls the method, obtaining the parameter types from the parameter values.  Any
     * <code>null</code> parameter value will result in a <code>NullPointerException</code>.
     */
    public MethodCall(Class<?> clazz, String name, Object[] parameterValues) {
        try {
            this.parameterValues=parameterValues;
            int paramCount=parameterValues.length;
            parameterTypes=new Class[paramCount];
            for(int c=0;c<paramCount;c++) {
                parameterTypes[c]=parameterValues[c].getClass();
            }
            this.method=clazz.getMethod(name, parameterTypes);
        } catch(NoSuchMethodException err) {
            Object[] extraInfo=new Object[parameterValues.length+3];
            extraInfo[0]="clazz.getName()="+clazz.getName();
            extraInfo[1]="name="+name;
            extraInfo[2]="parameterValues.length="+parameterValues.length;
            for(int c=0;c<parameterValues.length;c++) extraInfo[c+3]="parameterValues["+c+"]="+parameterValues[c];
            throw new WrappedException(err, extraInfo);
        }
    }

    /**
     * Calls the method using the provided parameter types and values.
     */
    public MethodCall(Method method, Class[] parameterTypes, Object[] parameterValues) {
        this.method=method;
        this.parameterTypes=parameterTypes;
        this.parameterValues=parameterValues;
    }

    /**
     * Calls the method using the provided parameter types.  The values must be provided through
     * the call to <code>invokeOn(Object,Object[])</code>.
     *
     * @see  #invokeOn(Object,Object[])
     */
    public MethodCall(Method method, Class[] parameterTypes) {
        this.method=method;
        this.parameterTypes=parameterTypes;
        this.parameterValues=null;
    }

    /**
     * Calls the method using the provided parameter types.  The values must be provided through
     * the call to <code>invokeOn(Object,Object[])</code>.
     *
     * @see  #invokeOn(Object,Object[])
     */
    public MethodCall(Class<?> clazz, String name, Class[] parameterTypes) {
        try {
            this.method=clazz.getMethod(name, parameterTypes);
            this.parameterTypes=parameterTypes;
            this.parameterValues=null;
        } catch(NoSuchMethodException err) {
            Object[] extraInfo=new Object[parameterValues.length+3];
            extraInfo[0]="clazz.getName()="+clazz.getName();
            extraInfo[1]="name="+name;
            extraInfo[2]="parameterTypes.length="+parameterValues.length;
            for(int c=0;c<parameterTypes.length;c++) extraInfo[c+3]="parameterTypes["+c+"]="+parameterTypes[c];
            throw new WrappedException(err, extraInfo);
        }
    }
    
    public Object invokeOn(Object obj) {
        if(parameterValues==null) throw new RuntimeException("Parameter values were not provided in the constructor, cannot call invokeOn(Object).  Please provide the parameters in a call to invokeOn(Object,Object[])");
        try {
            return method.invoke(obj, parameterValues);
        } catch(IllegalArgumentException err) {
            throw new WrappedException(
                err,
                new Object[] {
                    "method="+method,
                    "obj.getClass().getName()="+(obj==null?"null":obj.getClass().getName())
                }
            );
        } catch(IllegalAccessException err) {
            throw new WrappedException(
                err,
                new Object[] {
                    "method="+method,
                    "obj.getClass().getName()="+(obj==null?"null":obj.getClass().getName())
                }
            );
        } catch(InvocationTargetException err) {
            throw new WrappedException(
                err,
                new Object[] {
                    "method="+method,
                    "obj.getClass().getName()="+(obj==null?"null":obj.getClass().getName())
                }
            );
        }
    }

    public Object invokeOn(Object obj, Object[] parameterValues) {
        try {
            return method.invoke(obj, parameterValues);
        } catch(IllegalArgumentException err) {
            Object[] extraInfo=new Object[parameterValues.length+3];
            extraInfo[0]="method="+method;
            extraInfo[1]="obj.getClass().getName()="+(obj==null?"null":obj.getClass().getName());
            extraInfo[2]="parameterValues.length="+parameterValues.length;
            for(int c=0;c<parameterValues.length;c++) extraInfo[c+3]="parameterValues["+c+"]="+parameterValues[c];
            throw new WrappedException(err, extraInfo);
        } catch(IllegalAccessException err) {
            Object[] extraInfo=new Object[parameterValues.length+3];
            extraInfo[0]="method="+method;
            extraInfo[1]="obj.getClass().getName()="+(obj==null?"null":obj.getClass().getName());
            extraInfo[2]="parameterValues.length="+parameterValues.length;
            for(int c=0;c<parameterValues.length;c++) extraInfo[c+3]="parameterValues["+c+"]="+parameterValues[c];
            throw new WrappedException(err, extraInfo);
        } catch(InvocationTargetException err) {
            Object[] extraInfo=new Object[parameterValues.length+3];
            extraInfo[0]="method="+method;
            extraInfo[1]="obj.getClass().getName()="+(obj==null?"null":obj.getClass().getName());
            extraInfo[2]="parameterValues.length="+parameterValues.length;
            for(int c=0;c<parameterValues.length;c++) extraInfo[c+3]="parameterValues["+c+"]="+parameterValues[c];
            throw new WrappedException(err, extraInfo);
        }
    }
}
