package com.aoindustries.sql;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.Profiler;
import com.aoindustries.util.IntArrayList;
import com.aoindustries.util.IntList;
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

    private final Database database;

    Connection conn;
    
    protected DatabaseConnection(Database database) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, DatabaseConnection.class, "<init>(Database)", null);
        try {
            this.database=database;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public Database getDatabase() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, DatabaseConnection.class, "getDatabase()", null);
        try {
            return database;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public Connection getConnection(int isolationLevel, boolean readOnly) throws IOException, SQLException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, DatabaseConnection.class, "getConnection(int,boolean)", null);
        try {
	    return getConnection(isolationLevel, readOnly, 1);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "getConnection(int,boolean,int)", null);
        try {
            Connection c=conn;
            if(c==null) {
                c=database.getConnectionPool().getConnection(isolationLevel, readOnly, maxConnections);
                if(!readOnly || isolationLevel>=Connection.TRANSACTION_REPEATABLE_READ) c.setAutoCommit(false);
                conn=c;
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
                    c.commit();
                    c.setAutoCommit(true);
                }
                c.setReadOnly(false);
                c.setAutoCommit(false);
            }
            return c;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    protected static void setParam(PreparedStatement pstmt, int pos, Object param) throws SQLException {
        Profiler.startProfile(Profiler.FAST, DatabaseConnection.class, "setParam(PreparedStatement,int,Object)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static void setParams(PreparedStatement pstmt, Object ... params) throws SQLException {
        Profiler.startProfile(Profiler.FAST, DatabaseConnection.class, "setParams(PreparedStatement,...)", null);
        try {
            int pos=1;
            for(Object param : params) setParam(pstmt, pos++, param);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeBigDecimalQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeBooleanQuery(isolationLevel, readOnly, true, sql, params);
    }

    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeBooleanQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public Date executeDateQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeDateQuery(isolationLevel, readOnly, false, sql, params);
    }

    public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeDateQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeIntListQuery(int,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public int executeIntQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeIntQuery(isolationLevel, readOnly, true, sql, params);
    }

    public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeIntQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public long executeLongQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeLongQuery(isolationLevel, readOnly, true, sql, params);
    }

    public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeLongQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeObjectQuery(int,boolean,boolean,Class<T>,String,...)", null);
        try {
            try {
                PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
                try {
                    try {
                        setParams(pstmt, params);
                        incrementQueryCount();
                        ResultSet results=pstmt.executeQuery();
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeObjectListQuery(int,boolean,Class<T>,String,...)", null);
        try {
            try {
                PreparedStatement pstmt = getConnection(isolationLevel, readOnly).prepareStatement(sql);
                try {
                    try {
                        setParams(pstmt, params);
                        incrementQueryCount();
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public short executeShortQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeShortQuery(isolationLevel, readOnly, true, sql, params);
    }

    public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeShortQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public String executeStringQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeStringQuery(isolationLevel, readOnly, false, sql, params);
    }

    public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeStringQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeStringListQuery(int,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @deprecated  Please provide the rowRequired flag.
     */
    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        return executeTimestampQuery(isolationLevel, readOnly, false, sql, params);
    }

    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeTimestampQuery(int,boolean,boolean,String,...)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int executeUpdate(String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "executeUpdate(String,params)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void commit() throws IOException, SQLException {
        Profiler.startProfile(Profiler.FAST, DatabaseConnection.class, "commit()", null);
        try {
            Connection c=conn;
            if(c!=null) c.commit();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void incrementQueryCount() {
        database.incrementQueryCount();
    }

    public void incrementUpdateCount() {
        database.incrementUpdateCount();
    }

    public boolean isClosed() throws IOException, SQLException {
        Profiler.startProfile(Profiler.FAST, DatabaseConnection.class, "isClosed()", null);
        try {
            Connection c=conn;
            return c==null || c.isClosed();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void releaseConnection() throws IOException, SQLException {
        Profiler.startProfile(Profiler.FAST, DatabaseConnection.class, "releaseConnection()", null);
        try {
            Connection c=conn;
            if(c!=null) {
                conn=null;
                database.getConnectionPool().releaseConnection(c);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public boolean rollbackAndClose() {
        Profiler.startProfile(Profiler.IO, DatabaseConnection.class, "rollbackAndClose()", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
