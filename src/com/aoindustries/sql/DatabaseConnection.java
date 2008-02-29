package com.aoindustries.sql;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.IntArrayList;
import com.aoindustries.util.IntList;
import com.aoindustries.util.LongArrayList;
import com.aoindustries.util.LongList;
import java.io.IOException;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>DatabaseConnection</code> is used to only get actual database connections when needed.
 *
 * @see  Database
 *
 * @author  AO Industries, Inc.
 */
public class DatabaseConnection extends AbstractDatabaseAccess {

    private static final boolean DEBUG_TIMING = false;

    private final Database database;

    Connection conn;
    
    protected DatabaseConnection(Database database) {
       this.database=database;
    }
    
    public Database getDatabase() {
        return database;
    }

    public Connection getConnection(int isolationLevel, boolean readOnly) throws IOException, SQLException {
        return getConnection(isolationLevel, readOnly, 1);
    }

    public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws IOException, SQLException {
        Connection c=conn;
        if(c==null) {
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            c=database.getConnectionPool().getConnection(isolationLevel, readOnly, maxConnections);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: getConnection: getConnectionPool().getConnection in "+(endTime-startTime)+" ms");
            }
            if(!readOnly || isolationLevel>=Connection.TRANSACTION_REPEATABLE_READ) {
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                c.setAutoCommit(false);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: DatabaseConnection: getConnection: setAutoCommit(false) in "+(endTime-startTime)+" ms");
                }
            }
            conn=c;
        } else if(c.getTransactionIsolation()<isolationLevel) {
            if(!c.getAutoCommit()) {
                c.commit();
                long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
                c.setAutoCommit(true);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: DatabaseConnection: getConnection: setAutoCommit(true) in "+(endTime-startTime)+" ms");
                }
            }
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            c.setTransactionIsolation(isolationLevel);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: getConnection: setTransactionIsolation("+isolationLevel+") in "+(endTime-startTime)+" ms");
            }
            if(!readOnly && c.isReadOnly()) {
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                c.setReadOnly(false);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: DatabaseConnection: getConnection: setReadOnly(false) in "+(endTime-startTime)+" ms");
                }
            }
            if(!readOnly || isolationLevel>=Connection.TRANSACTION_REPEATABLE_READ) {
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                c.setAutoCommit(false);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: DatabaseConnection: getConnection: setAutoCommit(false) in "+(endTime-startTime)+" ms");
                }
            }
        } else if(!readOnly && c.isReadOnly()) {
            if(!c.getAutoCommit()) {
                c.commit();
                // TODO: May be able to get rid of the commit - setAutoCommit should commit according to the documentation
                long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
                c.setAutoCommit(true);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: DatabaseConnection: getConnection: setAutoCommit(true) in "+(endTime-startTime)+" ms");
                }
            }
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            c.setReadOnly(false);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: getConnection: setReadOnly(false) in "+(endTime-startTime)+" ms");
            }
            if(DEBUG_TIMING) startTime = System.currentTimeMillis();
            c.setAutoCommit(false);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: getConnection: setAutoCommit(false) in "+(endTime-startTime)+" ms");
            }
        }
        return c;
    }

    protected static void setParam(PreparedStatement pstmt, int pos, Object param) throws SQLException {
        if(param==null) pstmt.setNull(pos, Types.VARCHAR); // TODO: how should we set the type when all we know is that it is null?
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
        else pstmt.setObject(pos, param);
    }

    public static void setParams(PreparedStatement pstmt, Object ... params) throws SQLException {
        int pos=1;
        for(Object param : params) setParam(pstmt, pos++, param);
    }

    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        BigDecimal b=results.getBigDecimal(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return b;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return null;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeBooleanQuery(isolationLevel, readOnly, true, sql, params);
    }

    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        boolean b=results.getBoolean(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return b;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return false;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    public byte[] executeByteArrayQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        byte[] b=results.getBytes(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return b;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return null;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public Date executeDateQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeDateQuery(isolationLevel, readOnly, false, sql, params);
    }

    public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        java.sql.Date D=results.getDate(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return D;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return null;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
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
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public int executeIntQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeIntQuery(isolationLevel, readOnly, true, sql, params);
    }

    public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        int i=results.getInt(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return i;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return 0;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * TODO: Should we use cursors for this?
     */
    public LongList executeLongListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
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
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public long executeLongQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeLongQuery(isolationLevel, readOnly, true, sql, params);
    }

    public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        long l=results.getLong(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return l;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return 0;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        try {
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            Connection conn = getConnection(isolationLevel, readOnly);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: executeObjectQuery: getConnection in "+(endTime-startTime)+" ms");
            }

            if(DEBUG_TIMING) startTime = System.currentTimeMillis();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: executeObjectQuery: prepareStatement in "+(endTime-startTime)+" ms");
            }
            try {
                try {
                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    setParams(pstmt, params);
                    if(DEBUG_TIMING) {
                        long endTime = System.currentTimeMillis();
                        System.err.println("DEBUG: DatabaseConnection: executeObjectQuery: setParams in "+(endTime-startTime)+" ms");
                    }

                    incrementQueryCount();

                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    ResultSet results=pstmt.executeQuery();
                    if(DEBUG_TIMING) {
                        long endTime = System.currentTimeMillis();
                        System.err.println("DEBUG: DatabaseConnection: executeObjectQuery: executeQuery in "+(endTime-startTime)+" ms");
                    }

                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    try {
                        if(results.next()) {
                            Constructor<T> constructor = clazz.getConstructor(ResultSet.class);
                            T object = constructor.newInstance(results);
                            if(results.next()) throw new SQLException("More than one row returned.");
                            return object;
                        }
                        if(rowRequired) throw new SQLException("No row returned.");
                        return null;
                    } finally {
                        if(DEBUG_TIMING) {
                            long endTime = System.currentTimeMillis();
                            System.err.println("DEBUG: DatabaseConnection: executeObjectQuery: got all results in "+(endTime-startTime)+" ms");
                        }

                        if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                        results.close();
                        if(DEBUG_TIMING) {
                            long endTime = System.currentTimeMillis();
                            System.err.println("DEBUG: DatabaseConnection: executeObjectQuery: results.close() in "+(endTime-startTime)+" ms");
                        }
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
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                pstmt.close();
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: DatabaseConnection: executeObjectQuery: pstmt.close() in "+(endTime-startTime)+" ms");
                }
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        try {
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            Connection conn = getConnection(isolationLevel, readOnly);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: executeObjectListQuery: getConnection in "+(endTime-startTime)+" ms");
            }
            if(DEBUG_TIMING) startTime = System.currentTimeMillis();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: DatabaseConnection: executeObjectListQuery: prepareStatement in "+(endTime-startTime)+" ms");
            }
            try {
                try {
                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    setParams(pstmt, params);
                    if(DEBUG_TIMING) {
                        long endTime = System.currentTimeMillis();
                        System.err.println("DEBUG: DatabaseConnection: executeObjectListQuery: setParams in "+(endTime-startTime)+" ms");
                    }

                    incrementQueryCount();

                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    ResultSet results=pstmt.executeQuery();
                    if(DEBUG_TIMING) {
                        long endTime = System.currentTimeMillis();
                        System.err.println("DEBUG: DatabaseConnection: executeObjectListQuery: executeQuery in "+(endTime-startTime)+" ms");
                    }

                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    try {
                        Constructor<T> constructor = clazz.getConstructor(ResultSet.class);
                        List<T> list=new ArrayList<T>();
                        while(results.next()) list.add(constructor.newInstance(results));
                        return list;
                    } finally {
                        results.close();
                        if(DEBUG_TIMING) {
                            long endTime = System.currentTimeMillis();
                            System.err.println("DEBUG: DatabaseConnection: executeObjectListQuery: get results in "+(endTime-startTime)+" ms");
                        }
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
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                pstmt.close();
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: DatabaseConnection: executeObjectListQuery: pstmt.close() in "+(endTime-startTime)+" ms");
                }
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public short executeShortQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeShortQuery(isolationLevel, readOnly, true, sql, params);
    }

    public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        short s=results.getShort(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return s;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return 0;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public String executeStringQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeStringQuery(isolationLevel, readOnly, false, sql, params);
    }

    public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        String S=results.getString(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return S;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return null;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
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
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeTimestampQuery(isolationLevel, readOnly, false, sql, params);
    }

    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementQueryCount();
                ResultSet results=pstmt.executeQuery();
                try {
                    if(results.next()) {
                        Timestamp T=results.getTimestamp(1);
                        if(results.next()) throw new SQLException("More than one row returned.");
                        return T;
                    }
                    if(rowRequired) throw new SQLException("No row returned.");
                    return null;
                } finally {
                    results.close();
                }
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    public int executeUpdate(String sql, Object ... params) throws IOException, SQLException {
        try {
            PreparedStatement pstmt = getConnection(Connection.TRANSACTION_READ_COMMITTED, false).prepareStatement(sql);
            try {
                setParams(pstmt, params);
                incrementUpdateCount();
                return pstmt.executeUpdate();
            } catch(SQLException err) {
                throw new WrappedSQLException(err, pstmt);
            } finally {
                pstmt.close();
            }
        } catch(RuntimeException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(IOException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        } catch(SQLException err) {
            getDatabase().getConnectionPool().getErrorHandler().reportError(err, null);
            throw err;
        }
    }

    public void commit() throws IOException, SQLException {
        Connection c=conn;
        if(c!=null) c.commit();
    }

    public void incrementQueryCount() {
        database.incrementQueryCount();
    }

    public void incrementUpdateCount() {
        database.incrementUpdateCount();
    }

    public boolean isClosed() throws IOException, SQLException {
        Connection c=conn;
        return c==null || c.isClosed();
    }

    public void releaseConnection() throws IOException, SQLException {
        Connection c=conn;
        if(c!=null) {
            conn=null;
            database.getConnectionPool().releaseConnection(c);
        }
    }
    
    public boolean rollbackAndClose() {
        boolean rolledBack=false;
        try {
            if(conn!=null && !conn.isClosed()) {
                rolledBack=true;
                conn.rollback();
                conn.close();
            }
        } catch(SQLException err) {
            database.getConnectionPool().getErrorHandler().reportError(err, null);
        }
        return rolledBack;
    }
}
