package com.aoindustries.sql;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.AOPool;
import com.aoindustries.util.ErrorHandler;
import com.aoindustries.util.IntList;
import com.aoindustries.util.LongList;
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

    private static final boolean DEBUG_TIMING = false;

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
        return pool;
    }

    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public byte[] executeByteArrayQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        try {
            DatabaseConnection conn=createDatabaseConnection();
            try {
                byte[] value=conn.executeByteArrayQuery(isolationLevel, readOnly, rowRequired, sql, params);
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
    }

    public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public LongList executeLongListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        try {
            DatabaseConnection conn=createDatabaseConnection();
            try {
                LongList value=conn.executeLongListQuery(isolationLevel, readOnly, sql, params);
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
    }

    public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        try {
            long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
            DatabaseConnection conn=createDatabaseConnection();
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: Database: executeObjectListQuery: createDatabaseConnection in "+(endTime-startTime)+" ms");
            }
            try {
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                List<T> value=conn.executeObjectListQuery(isolationLevel, readOnly, clazz, sql, params);
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: Database: executeObjectListQuery: executeObjectListQuery in "+(endTime-startTime)+" ms");
                }
                if(!readOnly) {
                    if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                    conn.commit();
                    if(DEBUG_TIMING) {
                        long endTime = System.currentTimeMillis();
                        System.err.println("DEBUG: Database: executeObjectListQuery: commit in "+(endTime-startTime)+" ms");
                    }
                }
                return value;
            } catch(IOException err) {
                conn.rollbackAndClose();
                throw err;
            } catch(SQLException err) {
                conn.rollbackAndClose();
                throw err;
            } finally {
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                conn.releaseConnection();
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: Database: executeObjectListQuery: releaseConnection in "+(endTime-startTime)+" ms");
                }
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
    }

    public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public int executeUpdate(String sql, Object ... params) throws IOException, SQLException {
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
    }
}
