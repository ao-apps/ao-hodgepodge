/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Creates instances of objects by using reflection and passing-in the parameters in the same
 * order as the matching constructor.  For unknown classes, will try to find any
 * <code>valueOf(int)</code> or <code>valueOf(String)</code> methods to create the object instance.
 *
 * @author  AO Industries, Inc.
 */
public class AutoObjectFactory<T> implements ObjectFactory<T> {

    static final String EOL = System.getProperty("line.separator");

    /**
     * Concurrent map can't store nulls, uses this in place of null values when lookup fails.
     */
    private static final Method notExists;
    static {
        try {
            notExists = AutoObjectFactory.class.getMethod("getValueOfStringMethod", Class.class);
        } catch(NoSuchMethodException err) {
            throw new RuntimeException(err);
        }
    }

    private static final ConcurrentMap<Class<?>,Method> valueOfIntMethods = new ConcurrentHashMap<Class<?>,Method>();

    /**
     * Gets the <code>valueOf(int)</code> for the provided class or <code>null</code> if doesn't
     * exist or is non-static or non-public.
     */
    public static Method getValueOfIntMethod(Class<?> clazz) {
        Method existing = valueOfIntMethods.get(clazz);
        if(existing==null) {
            Method newMethod;
            try {
                newMethod = clazz.getMethod("valueOf", Integer.TYPE);
                int mod = newMethod.getModifiers();
                if(!Modifier.isStatic(mod) || !Modifier.isPublic(mod)) newMethod = notExists;
            } catch(NoSuchMethodException err) {
                newMethod = notExists;
            }
            existing = valueOfIntMethods.put(clazz, newMethod);
            if(existing==null) existing = newMethod;
        }
        return existing==notExists ? null : existing;
    }

    private static final ConcurrentMap<Class<?>,Method> valueOfStringMethods = new ConcurrentHashMap<Class<?>,Method>();

    /**
     * Gets the <code>valueOf(String)</code> for the provided class or <code>null</code> if doesn't
     * exist or is non-static or non-public.
     */
    public static Method getValueOfStringMethod(Class<?> clazz) {
        //System.err.println("clazz="+clazz);
        Method existing = valueOfStringMethods.get(clazz);
        if(existing==null) {
            Method newMethod;
            try {
                newMethod = clazz.getMethod("valueOf", String.class);
                int mod = newMethod.getModifiers();
                if(!Modifier.isStatic(mod) || !Modifier.isPublic(mod)) newMethod = notExists;
            } catch(NoSuchMethodException err) {
                newMethod = notExists;
            }
            existing = valueOfStringMethods.put(clazz, newMethod);
            if(existing==null) existing = newMethod;
        }
        return existing==notExists ? null : existing;
    }

    private final Class<T> clazz;
    private final Object[] prefixParams;

    public AutoObjectFactory(Class<T> clazz, Object... prefixParams) {
        this.clazz = clazz;
        this.prefixParams = prefixParams;
    }

    /**
     * Creates one object from the current values in the ResultSet.  Looks for a
     * constructor that is assignable to the prefixParams and the result set values.
     * The constructor must exactly match the number of prefixParams plus the
     * result set.
     */
    @Override
    public T createObject(ResultSet result) throws SQLException {
        try {
            ResultSetMetaData metaData = result.getMetaData();
            int numColumns = metaData.getColumnCount();
            int numParams = prefixParams.length + numColumns;
            Object[] params = new Object[numParams];

            // Find the candidate constructor
            List<String> warnings = null;
            Constructor<?>[] constructors = clazz.getConstructors();
        CONSTRUCTORS :
            for(Constructor<?> constructor : constructors) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if(paramTypes.length==numParams) {
                    for(int i=0;i<prefixParams.length;i++) {
                        Class<?> paramType = paramTypes[i];
                        if(!paramType.isAssignableFrom(prefixParams[i].getClass())) continue CONSTRUCTORS;
                        params[i] = prefixParams[i];
                        //System.err.println(paramType.getName()+" ? "+(params[i]==null ? "null" : params[i].getClass()));
                    }
                    // All remaining columns must be assignable from JDBC
                    for(int c=1; c<=numColumns; c++) {
                        int i = prefixParams.length + c-1;
                        Class<?> paramType = paramTypes[i];
                        // String first because it is commonly used
                        if(paramType==String.class) {
                            params[i] = result.getString(c);

                        // Primitives
                        } else if(paramType==Integer.TYPE) {
                            int value = result.getInt(c);
                            if(result.wasNull()) throw new SQLException(c+": "+metaData.getColumnName(c)+": null int");
                            params[i] = value;
                        } else if(paramType==Short.TYPE) {
                            short value = result.getShort(c);
                            if(result.wasNull()) throw new SQLException(c+": "+metaData.getColumnName(c)+": null short");
                            params[i] = value;
                        } else if(paramType==Boolean.TYPE) {
                            boolean b = result.getBoolean(c);
                            if(result.wasNull()) throw new SQLException(c+": "+metaData.getColumnName(c)+": null boolean");
                            params[i] = b;
                        } else if(paramType==Float.TYPE) {
                            float value = result.getFloat(c);
                            if(result.wasNull()) throw new SQLException(c+": "+metaData.getColumnName(c)+": null float");
                            params[i] = value;
                        } else if(paramType==Long.TYPE) {
                            long value = result.getLong(c);
                            if(result.wasNull()) throw new SQLException(c+": "+metaData.getColumnName(c)+": null long");
                            params[i] = value;

                        // Other types
                        } else if(paramType==Date.class) {
                            params[i] = result.getDate(c);
                        } else if(paramType==Boolean.class) {
                            boolean b = result.getBoolean(c);
                            params[i] = result.wasNull() ? null : b;
                        } else if(paramType==Timestamp.class) {
                            params[i] = result.getTimestamp(c);
                        } else if(paramType==Integer.class) {
                            int value = result.getInt(c);
                            params[i] = result.wasNull() ? null : value;
                        } else if(paramType==Float.class) {
                            float value = result.getFloat(c);
                            params[i] = result.wasNull() ? null : value;
                        } else if(paramType==Short.class) {
                            short value = result.getShort(c);
                            params[i] = result.wasNull() ? null : value;
                        } else if(paramType==Long.class) {
                            long value = result.getLong(c);
                            params[i] = result.wasNull() ? null : value;
                        } else {
                            // Try to find valueOf(int) for unknown types
                            Method valueOfIntMethod = getValueOfIntMethod(paramType);
                            if(valueOfIntMethod!=null) {
                                int value = result.getInt(c);
                                if(result.wasNull()) params[i] = null;
                                params[i] = valueOfIntMethod.invoke(null, value);
                            } else {
                                // Try to find valueOf(String) for unknown types
                                Method valueOfStringMethod = getValueOfStringMethod(paramType);
                                if(valueOfStringMethod!=null) {
                                    String value = result.getString(c);
                                    params[i] = result.wasNull() ? null : valueOfStringMethod.invoke(null, value);
                                } else {
                                    if(warnings==null) warnings = new ArrayList<String>();
                                    warnings.add("Unexpected parameter class: "+paramType.getName());
                                    continue CONSTRUCTORS;
                                }
                            }
                        }
                        //System.err.println(paramType.getName()+" ? "+(params[i]==null ? "null" : params[i].getClass()));
                    }
                    Object newInstance = constructor.newInstance(params);
                    //System.err.println(newInstance.getClass().getName()+": "+newInstance);
                    return clazz.cast(newInstance);
                }
            }
            StringBuilder message = new StringBuilder("Unable to find matching constructor");
            if(warnings!=null) for(String warning : warnings) message.append(EOL).append(warning);
            throw new SQLException(message.toString());
        } catch(InstantiationException err) {
            throw new SQLException(err);
        } catch(IllegalAccessException err) {
            throw new SQLException(err);
        } catch(InvocationTargetException err) {
            throw new SQLException(err);
        }
    }
}
