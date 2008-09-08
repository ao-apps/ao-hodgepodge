package com.aoindustries.sql;

/*
 * Copyright 2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates instances of objects of the generics type from a result set.
 *
 * @author  AO Industries, Inc.
 */
public interface ObjectFactory<T> {

    /**
     * Creates one object from the current values in the ResultSet.
     */
    T createObject(ResultSet result) throws SQLException;
}
