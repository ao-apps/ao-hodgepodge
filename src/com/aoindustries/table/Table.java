package com.aoindustries.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * An abstract structure for tables.
 *
 * @author  AO Industries, Inc.
*/
public interface Table<T extends Row> {

    /**
     * Registers a <code>TableListener</code> to be notified when
     * the cached data for this table expires.  The default
     * batching is used.
     *
     * @see  #addTableListener(TableListener,long)
     */
    void addTableListener(TableListener listener);

    /**
     * Registers a <code>TableListener</code> to be notified when
     * the cached data for this table expires.  Repetative incoming
     * requests will be batched into fewer events, in increments
     * provided by batchTime.  If batchTime is 0, the event is immediately
     * and always distributed.  Batched events are performed in
     * concurrent Threads, while immediate events are triggered by the
     * central cache invalidation thread.  In other words, don't use
     * a batchTime of zero unless you absolutely need your code to
     * run immediately, because it causes serial processing of the event
     * and may potentially slow down the responsiveness of the server.
     */
    void addTableListener(TableListener listener, long batchTime);

    /**
     * Removes a <code>TableListener</code> from the list of
     * objects being notified when the data is updated.
     */
    void removeTableListener(TableListener listener);

    List<T> getRows() throws IOException, SQLException;
    
    String getTableName() throws IOException, SQLException;
}