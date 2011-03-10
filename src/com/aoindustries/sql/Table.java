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

import com.aoindustries.graph.BackConnectedDirectedGraphVertex;
import com.aoindustries.table.IndexType;
import com.aoindustries.util.AutoGrowArrayList;
import com.aoindustries.util.Collections;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A friendly wrapper around database meta data.
 *
 * @author  AO Industries, Inc.
 */
public class Table implements BackConnectedDirectedGraphVertex<Table,SQLException> {

    private final Schema schema;
    private final String name;
    private final String tableType;
    private final int hashCode;

    protected Table(Schema schema, String name, String tableType) {
        this.schema = schema;
        if(name.indexOf('"')!=-1) throw new IllegalArgumentException();
        this.name = name;
        this.tableType = tableType;
        this.hashCode = schema.hashCode() * 31 + name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Two tables are equal if they have the same schema name and table name.
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Table)) return false;
        Table other = (Table)obj;
        return
            hashCode==other.hashCode
            && name.equals(other.name)
            && schema.equals(other.schema)
        ;
    }

    @Override
    public int hashCode() {
        return hashCode;
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

    private final Object getColumnMapLock = new Object();
    private SortedMap<String,Column> getColumnMapCache;

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
    public SortedMap<String,Column> getColumnMap() throws SQLException {
        synchronized(getColumnMapLock) {
            if(getColumnMapCache==null) {
                SortedMap<String,Column> newColumnMap = new TreeMap<String,Column>(DatabaseMetaData.getCollator());
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
                        if(newColumnMap.put(newColumn.getName(), newColumn)!=null) throw new AssertionError("Duplicate column: "+newColumn);
                    }
                } finally {
                    results.close();
                }
                getColumnMapCache = Collections.optimalUnmodifiableSortedMap(newColumnMap);
            }
            return getColumnMapCache;
        }
    }

    /**
     * Gets the column of the provided name.
     *
     * @throws  NoRowException if the column doesn't exist
     */
    public Column getColumn(String name) throws NoRowException, SQLException {
        Column column = getColumnMap().get(name);
        if(column==null) throw new NoRowException();
        return column;
    }

    private final Object getColumnsLock = new Object();
    private List<Column> getColumnsCache;

    /**
     * Gets all columns for this schema in their ordinal position order.
     * Column with ordinal position one is at index zero.
     *
     * @see  #getColumnMap()
     */
    public List<Column> getColumns() throws SQLException {
        synchronized(getColumnsLock) {
            if(getColumnsCache==null) {
                SortedMap<String,Column> columnMap = getColumnMap();
                List<Column> newColumns = new ArrayList<Column>(columnMap.size());
                for(int i=0; i<columnMap.size(); i++) newColumns.add(null);
                for(Column column : columnMap.values()) {
                    int ordinalPosition = column.getOrdinalPosition();
                    if(newColumns.set(ordinalPosition-1, column)!=null) throw new SQLException("Duplicate ordinal position: "+ordinalPosition);
                }
                for(int i=0; i<newColumns.size(); i++) {
                    if(newColumns.get(i)==null) throw new SQLException("Missing ordinal position: "+(i+1));
                }
                getColumnsCache = Collections.optimalUnmodifiableList(newColumns);
            }
            return getColumnsCache;
        }
    }

    /**
     * Gets the column of the provided ordinal position, where positions start at one.
     *
     * @throws  NoRowException if the column doesn't exist
     */
    public Column getColumn(int ordinalPosition) throws NoRowException, SQLException {
        try {
            return getColumns().get(ordinalPosition-1);
        } catch(IndexOutOfBoundsException exc) {
            throw new NoRowException(exc);
        }
    }

    private final Object getPrimaryKeyLock = new Object();
    private boolean getPrimaryKeyCached = false;
    private Index getPrimaryKeyCache;

    /**
     * Gets the primary key for this table or <code>null</code> if not found.
     */
    public Index getPrimaryKey() throws SQLException {
        synchronized(getPrimaryKeyLock) {
            if(!getPrimaryKeyCached) {
                String pkName = null;
                List<Column> columns = new AutoGrowArrayList<Column>();
                ResultSet results = schema.getCatalog().getMetaData().getMetaData().getPrimaryKeys(schema.getCatalog().getName(), schema.getName(), name);
                try {
                    while(results.next()) {
                        String columnName = results.getString("COLUMN_NAME");
                        int keySeq = results.getInt("KEY_SEQ");
                        String newPkName = results.getString("PK_NAME");
                        if(newPkName!=null) {
                            if(pkName==null) pkName = newPkName;
                            else if(!newPkName.equals(pkName)) throw new SQLException("Mismatched PK_NAME values: "+newPkName+"!="+pkName);
                        }
                        if(columns.set(keySeq-1, getColumn(columnName))!=null) throw new SQLException("Duplicate key sequence: "+keySeq);
                    }
                } finally {
                    results.close();
                }
                if(columns.isEmpty()) {
                    getPrimaryKeyCache = null;
                    getPrimaryKeyCached = true;
                } else {
                    // Make sure no gaps in the key sequence
                    for(int i=0; i<columns.size(); i++) {
                        if(columns.get(i)==null) throw new SQLException("Missing key sequence in index: "+(i+1));
                    }
                    getPrimaryKeyCache = new Index(this, pkName, IndexType.PRIMARY_KEY, columns);
                    getPrimaryKeyCached = true;
                }
            }
            return getPrimaryKeyCache;
        }
    }

    private final Object getConnectedVerticesLock = new Object();
    private Set<? extends Table> getConnectedVerticesCache;

    /**
     * Gets the set of tables that this table depends on.
     *
     * This is based on getImportedKeys
     */
    @Override
    public Set<? extends Table> getConnectedVertices() throws SQLException {
        synchronized(getConnectedVerticesLock) {
            if(getConnectedVerticesCache==null) {
                Set<Table> newConnectedVertices = new HashSet<Table>();
                Catalog catalog = schema.getCatalog();
                DatabaseMetaData metaData = catalog.getMetaData();
                ResultSet results = schema.getCatalog().getMetaData().getMetaData().getImportedKeys(schema.getCatalog().getName(), schema.getName(), name);
                try {
                    while(results.next()) {
                        String pkCat = results.getString("PKTABLE_CAT");
                        Catalog pkCatalog = pkCat==null ? catalog : metaData.getCatalog(pkCat);
                        newConnectedVertices.add(
                            pkCatalog
                            .getSchema(results.getString("PKTABLE_SCHEM"))
                            .getTable(results.getString("PKTABLE_NAME"))
                        );
                    }
                } finally {
                    results.close();
                }
                getConnectedVerticesCache = Collections.optimalUnmodifiableSet(newConnectedVertices);
            }
            return getConnectedVerticesCache;
        }
    }

    private final Object getBackConnectedVerticesLock = new Object();
    private Set<? extends Table> getBackConnectedVerticesCache;

    /**
     * Gets the set of tables that depend on this table.
     *
     * This is based on getExportedKeys
     */
    @Override
    public Set<? extends Table> getBackConnectedVertices() throws SQLException {
        synchronized(getBackConnectedVerticesLock) {
            if(getBackConnectedVerticesCache==null) {
                Set<Table> newBackConnectedVertices = new HashSet<Table>();
                Catalog catalog = schema.getCatalog();
                DatabaseMetaData metaData = catalog.getMetaData();
                ResultSet results = schema.getCatalog().getMetaData().getMetaData().getExportedKeys(schema.getCatalog().getName(), schema.getName(), name);
                try {
                    while(results.next()) {
                        String fkCat = results.getString("FKTABLE_CAT");
                        Catalog fkCatalog = fkCat==null ? catalog : metaData.getCatalog(fkCat);
                        newBackConnectedVertices.add(
                            fkCatalog
                            .getSchema(results.getString("FKTABLE_SCHEM"))
                            .getTable(results.getString("FKTABLE_NAME"))
                        );
                    }
                } finally {
                    results.close();
                }
                getBackConnectedVerticesCache = Collections.optimalUnmodifiableSet(newBackConnectedVertices);
            }
            return getBackConnectedVerticesCache;
        }
    }
}
