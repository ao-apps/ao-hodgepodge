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

import com.aoindustries.util.Collections;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A friendly wrapper around database meta data.
 *
 * @author  AO Industries, Inc.
 */
public class Catalog {

    private final DatabaseMetaData metaData;
    private final String name;

    protected Catalog(DatabaseMetaData metaData, String name) {
        this.metaData = metaData;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    public String getName() {
        return name;
    }

    private final Object getSchemasLock = new Object();
    private SortedMap<String,Schema> getSchemasCache;

    /**
     * Gets all schemas for this catalog keyed by unique name.
     *
     * @see  java.sql.DatabaseMetaData#getSchemas()
     */
    public SortedMap<String,Schema> getSchemas() throws SQLException {
        synchronized(getSchemasLock) {
            if(getSchemasCache==null) {
                SortedMap<String,Schema> newSchemas = new TreeMap<String,Schema>(DatabaseMetaData.getCollator());
                ResultSet results = metaData.getMetaData().getSchemas();
                try {
                    ResultSetMetaData resultsMeta = results.getMetaData();
                    while(results.next()) {
                        if(
                            resultsMeta.getColumnCount()==1 // PostgreSQL only returns one column
                            || name.equals(results.getString("TABLE_CATALOG"))
                        ) {
                            Schema newSchema = new Schema(this, results.getString("TABLE_SCHEM"));
                            if(newSchemas.put(newSchema.getName(), newSchema)!=null) throw new AssertionError("Duplicate schema: "+newSchema);
                        }
                    }
                } finally {
                    results.close();
                }
                getSchemasCache = Collections.optimalUnmodifiableSortedMap(newSchemas);
            }
            return getSchemasCache;
        }
    }

    /**
     * Gets the schema of the provided name.
     *
     * @throws  NoRowException if the schema doesn't exist
     */
    public Schema getSchema(String name) throws NoRowException, SQLException {
        Schema schema = getSchemas().get(name);
        if(schema==null) throw new NoRowException();
        return schema;
    }
}
