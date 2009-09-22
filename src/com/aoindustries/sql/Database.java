/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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

import com.aoindustries.util.IntList;
import com.aoindustries.util.LongList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import javax.sql.DataSource;

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

    private final DataSource dataSource;
    private final Logger logger;

    public Database(String driver, String url, String user, String password, int numConnections, long maxConnectionAge, Logger logger) {
        this(new AOConnectionPool(driver, url, user, password, numConnections, maxConnectionAge, logger));
    }

    public Database(AOConnectionPool pool) {
        this.pool = pool;
        this.dataSource = null;
        this.logger = null;
    }

    public Database(DataSource dataSource, Logger logger) {
        this.pool = null;
        this.dataSource = dataSource;
        this.logger = logger;
    }

    public DatabaseConnection createDatabaseConnection() {
        return new DatabaseConnection(this);
    }

    /**
     * Gets the pool or <code>null</code> if using a <code>DataSource</code>.
     */
    public AOConnectionPool getConnectionPool() {
        return pool;
    }

    /**
     * Gets the data source or <code>null</code> if using an <code>AOConnectionPool</code>.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection(int isolationLevel, boolean readOnly, int maxConnections) throws SQLException {
        if(pool!=null) {
            // From pool
            return pool.getConnection(isolationLevel, readOnly, maxConnections);
        } else {
            // From dataSource
            Connection conn = dataSource.getConnection();
            boolean successful = false;
            try {
                if(isolationLevel!=conn.getTransactionIsolation()) conn.setTransactionIsolation(isolationLevel);
                if(readOnly!=conn.isReadOnly()) conn.setReadOnly(readOnly);
                successful = true;
                return conn;
            } finally {
                if(!successful) conn.close();
            }
        }
    }

    public void releaseConnection(Connection conn) throws SQLException {
        if(pool!=null) {
            // From pool
            pool.releaseConnection(conn);
        } else {
            // From dataSource
            if(!conn.isClosed()) conn.close();
        }
    }

    public Logger getLogger() {
        if(pool!=null) {
            // From pool
            return pool.getLogger();
        } else {
            // From dataSource
            return logger;
        }
    }

    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            BigDecimal value=conn.executeBigDecimalQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(IOException err) {
            conn.rollback();
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
        return "Database("+(pool!=null ? pool.toString() : dataSource.toString())+")";
    }
}
