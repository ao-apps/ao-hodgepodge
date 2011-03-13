/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011  AO Industries, Inc.
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
import java.util.Collection;
import java.util.List;

/**
 * Wraps and simplifies access to a JDBC database.
 *
 * @author  AO Industries, Inc.
 */
abstract public class AbstractDatabaseAccess implements DatabaseAccess {

    @Override
    public BigDecimal executeBigDecimalQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeBigDecimalQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    @Override
    public boolean executeBooleanQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeBooleanQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }
    
    @Override
    public byte[] executeByteArrayQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeByteArrayQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    @Override
    public Date executeDateQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeDateQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    @Override
    public IntList executeIntListQuery(String sql, Object ... params) throws SQLException {
        return executeIntListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    @Override
    public int executeIntQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeIntQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    @Override
    public LongList executeLongListQuery(String sql, Object ... params) throws SQLException {
        return executeLongListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    @Override
    public long executeLongQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeLongQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    @Override
    public <T> T executeObjectQuery(Class<T> clazz, String sql, Object ... params) throws NoRowException, SQLException {
        return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, clazz, sql, params);
    }

    @Override
    public <T> T executeObjectQuery(ObjectFactory<T> objectFactory, String sql, Object ... params) throws NoRowException, SQLException {
        return executeObjectQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, objectFactory, sql, params);
    }

    @Override
    public <T> List<T> executeObjectListQuery(Class<T> clazz, String sql, Object ... params) throws SQLException {
        return executeObjectListQuery(Connection.TRANSACTION_READ_COMMITTED, true, clazz, sql, params);
    }

    @Override
    public <T> List<T> executeObjectListQuery(ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        return executeObjectListQuery(Connection.TRANSACTION_READ_COMMITTED, true, objectFactory, sql, params);
    }

    @Override
    public <T,C extends Collection<? super T>> C executeObjectCollectionQuery(C collection, Class<T> clazz, String sql, Object ... params) throws SQLException {
        return executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, collection, clazz, sql, params);
    }

    @Override
    public <T,C extends Collection<? super T>> C executeObjectCollectionQuery(C collection, ObjectFactory<T> objectFactory, String sql, Object ... params) throws SQLException {
        return executeObjectCollectionQuery(Connection.TRANSACTION_READ_COMMITTED, true, collection, objectFactory, sql, params);
    }

    @Override
    public void executeQuery(ResultSetHandler resultSetHandler, String sql, Object ... params) throws SQLException {
        executeQuery(Connection.TRANSACTION_READ_COMMITTED, true, resultSetHandler, sql, params);
    }

    @Override
    public List<Short> executeShortListQuery(String sql, Object ... params) throws SQLException {
        return executeShortListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    @Override
    public short executeShortQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeShortQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    @Override
    public String executeStringQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeStringQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }

    @Override
    public List<String> executeStringListQuery(String sql, Object ... params) throws SQLException {
        return executeStringListQuery(Connection.TRANSACTION_READ_COMMITTED, true, sql, params);
    }

    @Override
    public Timestamp executeTimestampQuery(String sql, Object ... params) throws NoRowException, SQLException {
        return executeTimestampQuery(Connection.TRANSACTION_READ_COMMITTED, true, true, sql, params);
    }
}
