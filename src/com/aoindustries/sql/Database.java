package com.aoindustries.sql;

/*
 * Copyright 2001-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.IntList;
import com.aoindustries.util.LongList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;

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

    public Database(String driver, String url, String user, String password, int numConnections, long maxConnectionAge, Logger logger) {
        this(new AOConnectionPool(driver, url, user, password, numConnections, maxConnectionAge, logger));
    }

    public Database(AOConnectionPool pool) {
        this.pool=pool;
    }

    public DatabaseConnection createDatabaseConnection() {
        return new DatabaseConnection(this);
    }

    public AOConnectionPool getConnectionPool() {
        return pool;
    }

    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public byte[] executeByteArrayQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public LongList executeLongListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, ObjectFactory<T> objectFactory, String sql, Object ... params) throws IOException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            T value=conn.executeObjectQuery(isolationLevel, readOnly, rowRequired, objectFactory, sql, params);
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
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, ObjectFactory<T> objectFactory, String sql, Object ... params) throws IOException, SQLException {
        long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
        DatabaseConnection conn=createDatabaseConnection();
        if(DEBUG_TIMING) {
            long endTime = System.currentTimeMillis();
            System.err.println("DEBUG: Database: executeObjectListQuery: createDatabaseConnection in "+(endTime-startTime)+" ms");
        }
        try {
            if(DEBUG_TIMING) startTime = System.currentTimeMillis();
            List<T> value=conn.executeObjectListQuery(isolationLevel, readOnly, objectFactory, sql, params);
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
    }

    public void executeQuery(int isolationLevel, boolean readOnly, ResultSetHandler resultSetHandler, String sql, Object ... params) throws IOException, SQLException {
        long startTime = DEBUG_TIMING ? System.currentTimeMillis() : 0;
        DatabaseConnection conn=createDatabaseConnection();
        if(DEBUG_TIMING) {
            long endTime = System.currentTimeMillis();
            System.err.println("DEBUG: Database: executeQuery: createDatabaseConnection in "+(endTime-startTime)+" ms");
        }
        try {
            if(DEBUG_TIMING) startTime = System.currentTimeMillis();
            conn.executeQuery(isolationLevel, readOnly, resultSetHandler, sql, params);
            if(DEBUG_TIMING) {
                long endTime = System.currentTimeMillis();
                System.err.println("DEBUG: Database: executeQuery: executeQuery in "+(endTime-startTime)+" ms");
            }
            if(!readOnly) {
                if(DEBUG_TIMING) startTime = System.currentTimeMillis();
                conn.commit();
                if(DEBUG_TIMING) {
                    long endTime = System.currentTimeMillis();
                    System.err.println("DEBUG: Database: executeQuery: commit in "+(endTime-startTime)+" ms");
                }
            }
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
                System.err.println("DEBUG: Database: executeQuery: releaseConnection in "+(endTime-startTime)+" ms");
            }
        }
    }

    public List<Short> executeShortListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            List<Short> value=conn.executeShortListQuery(isolationLevel, readOnly, sql, params);
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
    }

    public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
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
    }

    public int executeUpdate(String sql, Object ... params) throws IOException, SQLException {
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
    }

    @Override
    public String toString() {
        return "Database("+pool.toString()+")";
    }
}
