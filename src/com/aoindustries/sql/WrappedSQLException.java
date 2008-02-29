package com.aoindustries.sql;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Wraps a <code>SQLException</code> to include its source SQL statement.
 *
 * @author  AO Industries, Inc.
 */
public class WrappedSQLException extends SQLException {

    final private String sqlString;

    public WrappedSQLException(
        SQLException initCause,
        PreparedStatement pstmt
    ) {
        this(initCause, pstmt.toString());
    }
    
    public WrappedSQLException(
        SQLException initCause,
        String sqlString
    ) {
        super(initCause.getMessage(), initCause.getSQLState(), initCause.getErrorCode());
        initCause(initCause);
        this.sqlString=sqlString;
    }

    public String getSqlString() {
        return sqlString;
    }
}
