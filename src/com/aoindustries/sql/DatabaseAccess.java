package com.aoindustries.sql;

/*
 * Copyright 2001-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.IntList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Wraps and simplifies access to a JDBC database.
 *
 * @author  AO Industries, Inc.
 */
public interface DatabaseAccess {

    BigDecimal executeBigDecimalQuery(String sql, Object ... params) throws IOException, SQLException;
    BigDecimal executeBigDecimalQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    boolean executeBooleanQuery(String sql, Object ... params) throws IOException, SQLException;
    boolean executeBooleanQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;
    
    Date executeDateQuery(String sql, Object ... params) throws IOException, SQLException;
    Date executeDateQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    IntList executeIntListQuery(String sql, Object ... params) throws IOException, SQLException;
    IntList executeIntListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException;

    int executeIntQuery(String sql, Object ... params) throws IOException, SQLException;
    int executeIntQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    long executeLongQuery(String sql, Object ... params) throws IOException, SQLException;
    long executeLongQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    <T> T executeObjectQuery(Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;
    <T> T executeObjectQuery(int isolationLevel, boolean readOnly, boolean rowRequired, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;

    <T> List<T> executeObjectListQuery(Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;
    <T> List<T> executeObjectListQuery(int isolationLevel, boolean readOnly, Class<T> clazz, String sql, Object ... params) throws IOException, SQLException;

    short executeShortQuery(String sql, Object ... params) throws IOException, SQLException;
    short executeShortQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    String executeStringQuery(String sql, Object ... params) throws IOException, SQLException;
    String executeStringQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    List<String> executeStringListQuery(String sql, Object ... params) throws IOException, SQLException;
    List<String> executeStringListQuery(int isolationLevel, boolean readOnly, String sql, Object ... params) throws IOException, SQLException;

    Timestamp executeTimestampQuery(String sql, Object ... params) throws IOException, SQLException;
    Timestamp executeTimestampQuery(int isolationLevel, boolean readOnly, boolean rowRequired, String sql, Object ... params) throws IOException, SQLException;

    int executeUpdate(String sql, Object ... params) throws IOException, SQLException;
}
