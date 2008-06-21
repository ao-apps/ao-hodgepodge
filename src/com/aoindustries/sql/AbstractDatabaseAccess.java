package com.aoindustries.sql;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.IntList;
import com.aoindustries.util.LongList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Wraps and simplifies access to a JDBC database.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractDatabaseAccess implements DatabaseAccess {

    public BigDecimal executeBigDecimalQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeBigDecimalQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    public boolean executeBooleanQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeBooleanQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }
    
    public byte[] executeByteArrayQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeByteArrayQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    public Date executeDateQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeDateQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    public IntList executeIntListQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeIntListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    public int executeIntQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeIntQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    public LongList executeLongListQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeLongListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    public long executeLongQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeLongQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    public <T> T executeObjectQuery(Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, clazz, sql, params);
    }

    public <T> List<T> executeObjectListQuery(Class<T> clazz, String sql, Object ... params) throws IOException, SQLException {
        return executeObjectListQuery(Connection.TRANSACTION_READ_COMMITTED, true, clazz, sql, params);
    }

    public <T> T executeObjectQuery(ObjectFactory<T> objectFactory, String sql, Object ... params) throws IOException, SQLException {
        return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, objectFactory, sql, params);
    }

    public <T> List<T> executeObjectListQuery(ObjectFactory<T> objectFactory, String sql, Object ... params) throws IOException, SQLException {
        return executeObjectListQuery(Connection.TRANSACTION_READ_COMMITTED, true, objectFactory, sql, params);
    }

    public List<Short> executeShortListQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeShortListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    public short executeShortQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeShortQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    public String executeStringQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeStringQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    public List<String> executeStringListQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeStringListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    public Timestamp executeTimestampQuery(String sql, Object ... params) throws IOException, SQLException {
        return executeTimestampQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }
}
