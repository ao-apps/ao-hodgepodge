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

import com.aoindustries.util.IntList;
import com.aoindustries.util.LongList;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
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

    public BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            BigDecimal value=conn.executeBigDecimalQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            boolean value=conn.executeBooleanQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public byte[] executeByteArrayQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            byte[] value=conn.executeByteArrayQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            Date value=conn.executeDateQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            IntList value=conn.executeIntListQuery(isolationLevel, readOnly, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            int value=conn.executeIntQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public LongList executeLongListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            LongList value=conn.executeLongListQuery(isolationLevel, readOnly, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            long value=conn.executeLongQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            T value=conn.executeObjectQuery(isolationLevel, readOnly, rowRequired, clazz, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, ObjectFactory<T> objectFactory, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            T value=conn.executeObjectQuery(isolationLevel, readOnly, rowRequired, objectFactory, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            List<T> value=conn.executeObjectListQuery(isolationLevel, readOnly, clazz, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            List<T> value=conn.executeObjectListQuery(isolationLevel, readOnly, objectFactory, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public <T> Set<T> executeObjectSetQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            Set<T> value=conn.executeObjectSetQuery(isolationLevel, readOnly, clazz, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public <T> Set<T> executeObjectSetQuery(int isolationLevel, boolean readOnly, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            Set<T> value=conn.executeObjectSetQuery(isolationLevel, readOnly, objectFactory, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public void executeQuery(int isolationLevel, boolean readOnly, ResultSetHandler resultSetHandler, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            conn.executeQuery(isolationLevel, readOnly, resultSetHandler, sql, params);
            if(!readOnly) conn.commit();
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public List<Short> executeShortListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            List<Short> value=conn.executeShortListQuery(isolationLevel, readOnly, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            short value=conn.executeShortQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            String value=conn.executeStringQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            List<String> value=conn.executeStringListQuery(isolationLevel, readOnly, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws NoRowException, SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            Timestamp value=conn.executeTimestampQuery(isolationLevel, readOnly, rowRequired, sql, params);
            if(!readOnly) conn.commit();
            return value;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    public int executeUpdate(String sql, Object ... params) throws SQLException {
        DatabaseConnection conn=createDatabaseConnection();
        try {
            int updateCount = conn.executeUpdate(sql, params);
            conn.commit();
            return updateCount;
        } catch(RuntimeException err) {
            conn.rollback();
            throw err;
        } catch(SQLException err) {
            conn.rollbackAndClose();
            throw err;
        } finally {
            conn.releaseConnection();
        }
    }

    private final ThreadLocal<DatabaseConnection> transactionConnection = new ThreadLocal<DatabaseConnection>();

    /**
     * <p>
     * Executes an arbitrary transaction, providing automatic commit, rollback, and connection management.
     * Rolls-back the transaction on RuntimeException.  Rolls-back and closes the connection
     * on SQLException.
     * </p>
     * <p>
     * The connection allocated is stored as a ThreadLocal and will be automatically reused if
     * another transaction is performed within this transaction.  Any nested transaction will automatically
     * become part of the enclosing transaction.  For safety, a nested transaction will still rollback the
     * entire transaction on any exception.
     * </p>
     */
    public <V> V executeTransaction(DatabaseCallable<V> callable) throws SQLException {
        DatabaseConnection conn = transactionConnection.get();
        if(conn!=null) {
            // Reuse existing connection
            try {
                return callable.call(conn);
            } catch(RuntimeException err) {
                conn.rollback();
                throw err;
            } catch(SQLException err) {
                conn.rollbackAndClose();
                throw err;
            }
        } else {
            // Create new connection
            conn=createDatabaseConnection();
            try {
                transactionConnection.set(conn);
                try {
                    V result = callable.call(conn);
                    conn.commit();
                    return result;
                } finally {
                    transactionConnection.remove();
                }
            } catch(RuntimeException err) {
                conn.rollback();
                throw err;
            } catch(SQLException err) {
                conn.rollbackAndClose();
                throw err;
            } finally {
                conn.releaseConnection();
            }
        }
    }

    @Override
    public String toString() {
        return "Database("+(pool!=null ? pool.toString() : dataSource.toString())+")";
    }
}
