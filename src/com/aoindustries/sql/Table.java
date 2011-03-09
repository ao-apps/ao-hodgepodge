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
public class Table {

    private final Schema schema;
    private final String name;
    private final String tableType;

    protected Table(Schema schema, String name, String tableType) {
        this.schema = schema;
        this.name = name;
        this.tableType = tableType;
    }

    @Override
    public String toString() {
        return name;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public String getTableType() {
        return tableType;
    }

    private final Object getColumnsLock = new Object();
    private SortedMap<String,Column> getColumnsCache;

    private static Integer getInteger(ResultSet results, String columnName) throws SQLException {
        int val = results.getInt(columnName);
        if(results.wasNull()) return null;
        return Integer.valueOf(val);
    }

    /**
     * Gets all columns for this schema keyed by unique name.
     *
     * @see  java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public SortedMap<String,Column> getColumns() throws SQLException {
        synchronized(getColumnsLock) {
            if(getColumnsCache==null) {
                SortedMap<String,Column> newColumns = new TreeMap<String,Column>(DatabaseMetaData.getCollator());
                ResultSet results = schema.getCatalog().getMetaData().getMetaData().getColumns(schema.getCatalog().getName(), schema.getName(), name, null);
                try {
                    while(results.next()) {
                        Column newColumn = new Column(
                            this,
                            results.getString("COLUMN_NAME"),
                            results.getInt("DATA_TYPE"),
                            results.getString("TYPE_NAME"),
                            getInteger(results, "COLUMN_SIZE"),
                            getInteger(results, "DECIMAL_DIGITS"),
                            results.getInt("NULLABLE"),
                            results.getString("COLUMN_DEF"),
                            getInteger(results, "CHAR_OCTET_LENGTH"),
                            results.getInt("ORDINAL_POSITION"),
                            results.getString("IS_NULLABLE"),
                            results.getString("IS_AUTOINCREMENT")
                        );
                        if(newColumns.put(newColumn.getName(), newColumn)!=null) throw new AssertionError("Duplicate column: "+newColumn);
                    }
                } finally {
                    results.close();
                }
                getColumnsCache = Collections.unmodifiableSortedMap(newColumns);
            }
            return getColumnsCache;
        }
    }

    /**
     * Gets the column of the provided name.
     *
     * @throws  NoRowException if the column doesn't exist
     */
    public Column getColumn(String name) throws NoRowException, SQLException {
        Column column = getColumns().get(name);
        if(column==null) throw new NoRowException();
        return column;
    }
}
