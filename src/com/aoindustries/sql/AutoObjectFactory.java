/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009  AO Industries, Inc.
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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Creates instances of objects by using reflection and passing-in the parameters in the same
 * order as the matching constructor.
 *
 * @author  AO Industries, Inc.
 */
public class AutoObjectFactory<T> implements ObjectFactory<T> {

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
    public T createObject(ResultSet result) throws SQLException {
        try {
            int numColumns = result.getMetaData().getColumnCount();
            int numParams = prefixParams.length + numColumns;
            Object[] params = new Object[numParams];

            // Find the candidate constructor
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
                        // String
                        if(paramType==String.class) {
                            params[i] = result.getString(c);

                        // Primitives
                        } else if(paramType==Integer.TYPE) {
                            int value = result.getInt(c);
                            params[i] = result.wasNull() ? -1 : value;
                        } else if(paramType==Boolean.TYPE) {
                            boolean b = result.getBoolean(c);
                            if(result.wasNull()) throw new NullPointerException("null boolean");
                            params[i] = b;
                        } else if(paramType==Float.TYPE) {
                            float value = result.getFloat(c);
                            params[i] = result.wasNull() ? Float.NaN : value;

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
                        } else {
                            System.err.println("AutoObjectFactory: Unexpected class: "+paramType.getName());
                            continue CONSTRUCTORS;
                        }
                        //System.err.println(paramType.getName()+" ? "+(params[i]==null ? "null" : params[i].getClass()));
                    }
                    Object newInstance = constructor.newInstance(params);
                    //System.err.println(newInstance.getClass().getName()+": "+newInstance);
                    return clazz.cast(newInstance);
                }
            }
            throw new SQLException("Unable to find matching constructor");
        } catch(InstantiationException err) {
            throw new SQLException(err);
        } catch(IllegalAccessException err) {
            throw new SQLException(err);
        } catch(InvocationTargetException err) {
            throw new SQLException(err);
        }
    }
}
