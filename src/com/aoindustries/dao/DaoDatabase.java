/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
package com.aoindustries.dao;

import com.aoindustries.sql.Database;
import com.aoindustries.sql.DatabaseCallable;
import com.aoindustries.sql.DatabaseConnection;
import com.aoindustries.sql.DatabaseRunnable;
import java.sql.SQLException;
import java.text.Collator;
import java.util.Map;

/**
 * A database is a collection of tables, and a collection of reports.
 */
abstract public class DaoDatabase {

    /**
     * Provides a single Collator for shared use.  This sorts in the system
     * locale in effect when first created.
     */
    public static Collator collator = Collator.getInstance();

    /**
     * Gets the name of this database.
     */
    abstract public String getName();

    /**
     * Gets the underlying database that should be used at this moment in time.
     * It is possible that the database will change in an fail-over state.
     * Within a single transaction, however, the database returned must be the
     * same.
     */
    abstract protected Database getDatabase() throws SQLException;

    /**
     * Gets the set of all tables in this database.  This is a map keyed on table
     * name to be useful in JSP EL without requiring a separate getter for each
     * table.
     */
    abstract public Map<String,? extends Table<?,?>> getTables();

    /**
     * Clears all caches used for the current request.
     */
    public void clearAllCaches(boolean requestOnly) {
        for(Table<?,?> table : getTables().values()) table.clearCaches(requestOnly);
    }

    /**
     * Uses a ThreadLocal to make sure an entire transaction is executed against the same
     * underlying database.  This way, nothing funny will happen if master/slave databases
     * are switched mid-transaction.
     */
    protected final ThreadLocal<Database> transactionDatabase = new ThreadLocal<Database>();

    protected <V> V executeTransaction(DatabaseCallable<V> callable) throws SQLException {
        Database database = transactionDatabase.get();
        if(database!=null) {
            // Reuse current database
            return database.executeTransaction(callable);
        } else {
            // Get database
            database=getDatabase();
            transactionDatabase.set(database);
            try {
                return database.executeTransaction(callable);
            } finally {
                transactionDatabase.remove();
            }
        }
    }

    protected void executeTransaction(DatabaseRunnable runnable) throws SQLException {
        Database database = transactionDatabase.get();
        if(database!=null) {
            // Reuse current database
            database.executeTransaction(runnable);
        } else {
            // Get database
            database=getDatabase();
            transactionDatabase.set(database);
            try {
                database.executeTransaction(runnable);
            } finally {
                transactionDatabase.remove();
            }
        }
    }

    /**
     * Executes a transaction between any number of calls to this database and
     * its tables.
     */
    public void executeTransaction(final Runnable runnable) throws SQLException {
        executeTransaction(
            new DatabaseRunnable() {
                @Override
                public void run(DatabaseConnection db) {
                    runnable.run();
                }
            }
        );
    }

    /**
     * Gets the set of all reports that are supported by this repository implementation, keyed on its unique name.
     */
    public abstract Map<String,? extends Report> getReports();
}
