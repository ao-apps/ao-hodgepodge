package com.aoindustries.sql;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Processes result sets from a query one-at-a-time.
 *
 * @author  AO Industries, Inc.
 */
public interface ResultSetHandler {

    /**
     * Process one result set.
     */
    void handleResultSet(ResultSet result) throws SQLException;
}
