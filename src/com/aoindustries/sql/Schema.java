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
package com.aoindustries.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A friendly wrapper around database meta data.
 *
 * @author  AO Industries, Inc.
 */
public class Schema {

    private final Catalog catalog;
    private final String name;

    protected Schema(Catalog catalog, String name) {
        this.catalog = catalog;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public String getName() {
        return name;
    }

    private final Object getTablesLock = new Object();
    private SortedMap<String,Table> getTablesCache;

    /**
     * Gets all tables for this schema keyed by unique name.
     *
     * @see  java.sql.DatabaseMetaData#getTables()
     */
    public SortedMap<String,Table> getTables() throws SQLException {
        synchronized(getTablesLock) {
            if(getTablesCache==null) {
                SortedMap<String,Table> newTables = new TreeMap<String,Table>(DatabaseMetaData.getCollator());
                ResultSet results = catalog.getMetaData().getMetaData().getTables(catalog.getName(), name, null, null);
                try {
                    while(results.next()) {
                        Table newTable = new Table(this, results.getString("TABLE_NAME"), results.getString("TABLE_TYPE"));
                        if(newTables.put(newTable.getName(), newTable)!=null) throw new AssertionError("Duplicate table: "+newTable);
                    }
                } finally {
                    results.close();
                }
                getTablesCache = Collections.unmodifiableSortedMap(newTables);
            }
            return getTablesCache;
        }
    }

    /**
     * Gets the table of the provided name.
     *
     * @throws  NoRowException if the table doesn't exist
     */
    public Table getTable(String name) throws NoRowException, SQLException {
        Table table = getTables().get(name);
        if(table==null) throw new NoRowException();
        return table;
    }
}
