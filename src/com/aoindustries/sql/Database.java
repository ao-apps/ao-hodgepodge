package com.aoindustries.sql;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.AOPool;
import com.aoindustries.profiler.Profiler;
import com.aoindustries.util.ErrorHandler;
import com.aoindustries.util.IntList;
import com.aoindustries.util.StandardErrorHandler;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Wraps and simplifies access to a JDBC database.  If used directly as a <code>DatabaseAccess</code> each individual call is a separate transaction.
 * For transactions across multiple statements, use <code>DatabaseConnection</code>.
 *
 * @see  #createDatabaseConnection
 * @see  DatabaseConnection
 *
 * @author  AO Industries, Inc.
 */
public class Database extends AbstractDatabaseAccess {

    /**
     * Only one connection pool is made to the database.
     */
    private final AOConnectionPool pool;

    /**
     * @deprecated  Please use Database(String,String,String,String,int,long,ErrorHandler), providing maxConnectionAge
     *
     * @see  #Database(String,String,String,String,int,long,ErrorHandler)
     */
    public Database(String driver, String url, String user, String password, int numConnections) {
        this(driver, url, user, password, numConnections, AOPool.DEFAULT_MAX_CONNECTION_AGE, new StandardErrorHandler());
    }

    /**
     * @deprecated  Please use Database(String,String,String,String,int,long,ErrorHandler)
     *
     * @see  #Database(String,String,String,String,int,long,ErrorHandler)
     */
    public Database(String driver, String url, String user, String password, int numConnections, long maxConnectionAge) {
        this(driver, url, user, password, numConnections, maxConnectionAge, new StandardErrorHandler());
    }

    public Database(String driver, String url, String user, String password, int numConnections, long maxConnectionAge, ErrorHandler errorHandler) {
        this(new AOConnectionPool(driver, url, user, password, numConnections, maxConnectionAge, errorHandler));
    }

    public Database(AOConnectionPool pool) {
        this.pool=pool;
    }

    private long updateCount=0;
    private final Object updateCountLock=new Object();
    void incrementUpdateCount() {
        synchronized(updateCountLock) {
            updateCount++;
        }
    }
    public long getUpdateCount() {
        synchronized(updateCountLock) {
            return updateCount;
        }
    }

    private long queryCount=0;
    private final Object queryCountLock=new Object();
    void incrementQueryCount() {
        synchronized(queryCountLock) {
            queryCount++;
        }
    }
    public long getQueryCount() {
        synchronized(queryCountLock) {
            return queryCount;
        }
    }

    public DatabaseConnection createDatabaseConnection() {
        return new DatabaseConnection(this);
    }

    public AOConnectionPool getConnectionPool() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, Database.class, "getConnectionPool()", null);
        try {
            return pool;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeBigDecimalQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    BigDecimal value=conn.executeBigDecimalQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeBooleanQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    boolean value=conn.executeBooleanQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeDateQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    Date value=conn.executeDateQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeIntListQuery(int,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    IntList value=conn.executeIntListQuery(isolationLevel, readOnly, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeIntQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    int value=conn.executeIntQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeLongQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    long value=conn.executeLongQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeObjectQuery(int,boolean,boolean,Class<T>,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    T value=conn.executeObjectQuery(isolationLevel, readOnly, rowRequired, clazz, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeObjectListQuery(int,boolean,Class<T>,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    List<T> value=conn.executeObjectListQuery(isolationLevel, readOnly, clazz, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeShortQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    short value=conn.executeShortQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeStringQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    String value=conn.executeStringQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeStringListQuery(int,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    List<String> value=conn.executeStringListQuery(isolationLevel, readOnly, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeTimestampQuery(int,boolean,boolean,String,...)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    Timestamp value=conn.executeTimestampQuery(isolationLevel, readOnly, rowRequired, sql, params);
                    if(!readOnly) conn.commit();
                    return value;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public int executeUpdate(String sql, Object ... params) throws IOException, SQLException {
        Profiler.startProfile(Profiler.IO, Database.class, "executeUpdate(String,params)", null);
        try {
            try {
                DatabaseConnection conn=createDatabaseConnection();
                try {
                    int updateCount = conn.executeUpdate(sql, params);
                    conn.commit();
                    return updateCount;
                } catch(IOException err) {
                    conn.rollbackAndClose();
                    throw err;
                } catch(SQLException err) {
                    conn.rollbackAndClose();
                    throw err;
                } finally {
                    conn.releaseConnection();
                }
            } catch(RuntimeException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(IOException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            } catch(SQLException err) {
                getConnectionPool().getErrorHandler().reportError(err, null);
                throw err;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
