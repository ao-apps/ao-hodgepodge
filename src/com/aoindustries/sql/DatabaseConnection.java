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
package com.aoindustries.sql;

import com.aoindustries.util.IntArrayList;
import com.aoindustries.util.IntList;
import com.aoindustries.util.LongArrayList;
import com.aoindustries.util.LongList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * A <code>DatabaseConnection</code> is used to only get actual database connections when needed.
 *
 * @see  Database
 *
 * @author  AO Industries, Inc.
 */
public class DatabaseConnection extends AbstractDatabaseAccess {

    private final Database database;

    Connection _conn;
    
    protected DatabaseConnection(Database database) {
       this.database=database;
    }
    
    public Database getDatabase() {
        return database;
    }

    public Connection getConnection(int isolationLevel, boolean readOnly) throws SQLException {
        return getConnection(isolationLevel, readOnly, 1);
    }

    public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws SQLException {
        Connection c=_conn;
        if(c==null) {
            c=database.getConnection(isolationLevel, readOnly, maxConnections);
            if(!readOnly || isolationLevel>=Connection.TRANSACTION_REPEATABLE_READ) c.setAutoCommit(false);
            _conn=c;
        } else if(c.getTransactionIsolation()<isolationLevel) {
            if(!c.getAutoCommit()) {
                c.commit();
                c.setAutoCommit(true);
            }
            c.setTransactionIsolation(isolationLevel);
            if(!readOnly && c.isReadOnly()) c.setReadOnly(false);
            if(!readOnly || isolationLevel>=Connection.TRANSACTION_REPEATABLE_READ) c.setAutoCommit(false);
        } else if(!readOnly && c.isReadOnly()) {
            if(!c.getAutoCommit()) {
                // May be able to get rid of the commit - setAutoCommit should commit according to the documentation
                // c.commit();
                c.setAutoCommit(true);
            }
            c.setReadOnly(false);
            c.setAutoCommit(false);
        }
        return c;
    }

    protected static void setParam(PreparedStatement pstmt, int pos, Object param) throws SQLException {
        if(param==null) pstmt.setNull(pos, Types.VARCHAR);
        else if(param instanceof Null) pstmt.setNull(pos, ((Null)param).getType());
        else if(param instanceof Array) pstmt.setArray(pos, (Array)param);
        else if(param instanceof BigDecimal) pstmt.setBigDecimal(pos, (BigDecimal)param);
        else if(param instanceof Blob) pstmt.setBlob(pos, (Blob)param);
        else if(param instanceof Boolean) pstmt.setBoolean(pos, (Boolean)param);
        else if(param instanceof Byte) pstmt.setByte(pos, (Byte)param);
        else if(param instanceof byte[]) pstmt.setBytes(pos, (byte[])param);
        else if(param instanceof Clob) pstmt.setClob(pos, (Clob)param);
        else if(param instanceof Date) pstmt.setDate(pos, (Date)param);
        else if(param instanceof Double) pstmt.setDouble(pos, (Double)param);
        else if(param instanceof Float) pstmt.setFloat(pos, (Float)param);
        else if(param instanceof Integer) pstmt.setInt(pos, (Integer)param);
        else if(param instanceof Long) pstmt.setLong(pos, (Long)param);
        else if(param instanceof Ref) pstmt.setRef(pos, (Ref)param);
        else if(param instanceof Short) pstmt.setShort(pos, (Short)param);
        else if(param instanceof String) pstmt.setString(pos, (String)param);
        else if(param instanceof Time) pstmt.setTime(pos, (Time)param);
        else if(param instanceof Timestamp) pstmt.setTimestamp(pos, (Timestamp)param);
        else if(param instanceof URL) pstmt.setURL(pos, (URL)param);
        else if(
            // Note: Several more added in Java 1.6
            (param instanceof SQLData)
            || (param instanceof Struct)
        ) pstmt.setObject(pos, param);
        else {
            // Defaults to string with object.toString only when the class has a valueOf(String) method that will reconstitute it in AutoObjectFactory
            Class<?> clazz = param.getClass();
            if(AutoObjectFactory.getValueOfStringMethod(clazz)!=null) pstmt.setString(pos, param.toString());
            else throw new SQLException("Unexpected parameter class: "+clazz.getName());
        }
    }

    public static void setParams(PreparedStatement pstmt, Object ... params) throws SQLException {
        int pos=1;
        for(Object param : params) setParam(pstmt, pos++, param);
    }

    @Override
    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    BigDecimal b=results.getBigDecimal(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return b;
                }
                if(rowRequired) throw new NoRowException();
                return null;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    boolean b=results.getBoolean(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return b;
                }
                if(rowRequired) throw new NoRowException();
                return false;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public byte[] executeByteArrayQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    byte[] b=results.getBytes(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return b;
                }
                if(rowRequired) throw new NoRowException();
                return null;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    java.sql.Date D=results.getDate(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return D;
                }
                if(rowRequired) throw new NoRowException();
                return null;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                IntList V=new IntArrayList();
                while(results.next()) V.add(results.getInt(1));
                return V;
            } finally {
                results.close();
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    int i=results.getInt(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return i;
                }
                if(rowRequired) throw new NoRowException();
                return 0;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public LongList executeLongListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                LongList V=new LongArrayList();
                while(results.next()) V.add(results.getLong(1));
                return V;
            } finally {
                results.close();
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    long l=results.getLong(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return l;
                }
                if(rowRequired) throw new NoRowException();
                return 0;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws NoRowException, SQLException {
        Connection conn = getConnection(isolationLevel, readOnly);

        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            try {
                setParams(pstmt, params);
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        Constructor<T> constructor = clazz.getConstructor(ResultSet.class);
                        T object = constructor.newInstance(results);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return object;
                    }
                    if(rowRequired) throw new NoRowException();
                    return null;
                } finally {
                    results.close();
                }
            } catch(NoSuchMethodException err) {
                SQLException sqlErr = new SQLException("Unable to find constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(InstantiationException err) {
                SQLException sqlErr = new SQLException("Unable to instantiate object: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(IllegalAccessException err) {
                SQLException sqlErr = new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(InvocationTargetException err) {
                SQLException sqlErr = new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, ObjectFactory<T> objectFactory, String sql, Object ... params) throws NoRowException, SQLException {
        Connection conn = getConnection(isolationLevel, readOnly);

        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            setParams(pstmt, params);

            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    T object = objectFactory.createObject(results);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return object;
                }
                if(rowRequired) throw new NoRowException();
                return null;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws SQLException {
        Connection conn = getConnection(isolationLevel, readOnly);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            try {
                setParams(pstmt, params);
                ResultSet results=pstmt.executeQuery();
                try {
                    Constructor<T> constructor = clazz.getConstructor(ResultSet.class);
                    List<T> list=new ArrayList<T>();
                    while(results.next()) list.add(constructor.newInstance(results));
                    return list;
                } finally {
                    results.close();
                }
            } catch(NoSuchMethodException err) {
                SQLException sqlErr = new SQLException("Unable to find constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(InstantiationException err) {
                SQLException sqlErr = new SQLException("Unable to instantiate object: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(IllegalAccessException err) {
                SQLException sqlErr = new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(InvocationTargetException err) {
                SQLException sqlErr = new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        Connection conn = getConnection(isolationLevel, readOnly);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            setParams(pstmt, params);

            ResultSet results=pstmt.executeQuery();
            try {
                List<T> list=new ArrayList<T>();
                while(results.next()) list.add(objectFactory.createObject(results));
                return list;
            } finally {
                results.close();
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public <T> Set<T> executeObjectSetQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws SQLException {
        Connection conn = getConnection(isolationLevel, readOnly);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            try {
                setParams(pstmt, params);
                ResultSet results=pstmt.executeQuery();
                try {
                    Constructor<T> constructor = clazz.getConstructor(ResultSet.class);
                    Set<T> set=new HashSet<T>();
                    while(results.next()) {
                        T newObj = constructor.newInstance(results);
                        if(!set.add(newObj)) throw new SQLException("Duplicate row in results: "+newObj);
                    }
                    return set;
                } finally {
                    results.close();
                }
            } catch(NoSuchMethodException err) {
                SQLException sqlErr = new SQLException("Unable to find constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(InstantiationException err) {
                SQLException sqlErr = new SQLException("Unable to instantiate object: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(IllegalAccessException err) {
                SQLException sqlErr = new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            } catch(InvocationTargetException err) {
                SQLException sqlErr = new SQLException("Illegal access on constructor: "+clazz.getName()+"(java.sql.ResultSet)");
                sqlErr.initCause(err);
                throw sqlErr;
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public <T> Set<T> executeObjectSetQuery(int isolationLevel, boolean readOnly, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        Connection conn = getConnection(isolationLevel, readOnly);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            setParams(pstmt, params);

            ResultSet results=pstmt.executeQuery();
            try {
                Set<T> set=new HashSet<T>();
                while(results.next()) {
                    T newObj = objectFactory.createObject(results);
                    if(!set.add(newObj)) throw new SQLException("Duplicate row in results: "+newObj);
                }
                return set;
            } finally {
                results.close();
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public void executeQuery(int isolationLevel, boolean readOnly, ResultSetHandler resultSetHandler, String sql, Object ... params) throws SQLException {
        Connection conn = getConnection(isolationLevel, readOnly);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        try {
            setParams(pstmt, params);

            ResultSet results=pstmt.executeQuery();
            try {
                while(results.next()) resultSetHandler.handleResultSet(results);
            } finally {
                results.close();
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public List<Short> executeShortListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                List<Short> V=new ArrayList<Short>();
                while(results.next()) V.add(results.getShort(1));
                return V;
            } finally {
                results.close();
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    short s=results.getShort(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return s;
                }
                if(rowRequired) throw new NoRowException();
                return 0;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    String S=results.getString(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return S;
                }
                if(rowRequired) throw new NoRowException();
                return null;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                List<String> V=new ArrayList<String>();
                while(results.next()) V.add(results.getString(1));
                return V;
            } finally {
                results.close();
            }
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            ResultSet results=pstmt.executeQuery();
            try {
                if(results.next()) {
                    Timestamp T=results.getTimestamp(1);
                    if(results.next()) throw new SQLException("More than one row returned.");
                    return T;
                }
                if(rowRequired) throw new NoRowException();
                return null;
            } finally {
                results.close();
            }
        } catch(NoRowException err) {
            throw err;
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    @Override
    public int executeUpdate(String sql, Object ... params) throws SQLException {
        PreparedStatement pstmt = getConnection(Connection.TRANSACTION_READ_COMMITTED, false).prepareStatement(sql);
        try {
            setParams(pstmt, params);
            return pstmt.executeUpdate();
        } catch(SQLException err) {
            throw new WrappedSQLException(err, pstmt);
        } finally {
            pstmt.close();
        }
    }

    public void commit() throws SQLException {
        Connection c=_conn;
        if(c!=null) c.commit();
    }

    public boolean isClosed() throws SQLException {
        Connection c=_conn;
        return c==null || c.isClosed();
    }

    public void releaseConnection() throws SQLException {
        Connection c=_conn;
        if(c!=null) {
            _conn=null;
            database.releaseConnection(c);
        }
    }
    
    public boolean rollback() {
        boolean rolledBack=false;
        try {
            if(_conn!=null && !_conn.isClosed()) {
                rolledBack=true;
                _conn.rollback();
            }
        } catch(SQLException err) {
            database.getLogger().logp(Level.SEVERE, DatabaseConnection.class.getName(), "rollback", null, err);
        }
        return rolledBack;
    }

    public boolean rollbackAndClose() {
        boolean rolledBack=false;
        try {
            if(_conn!=null && !_conn.isClosed()) {
                rolledBack=true;
                _conn.rollback();
                _conn.close();
            }
        } catch(SQLException err) {
            database.getLogger().logp(Level.SEVERE, DatabaseConnection.class.getName(), "rollbackAndClose", null, err);
        }
        return rolledBack;
    }
}
