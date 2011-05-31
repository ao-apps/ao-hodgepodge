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

import com.aoindustries.sql.NoRowException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Caches results on a per-row basis.
 */
abstract public class RowCacheTable<K extends Comparable<? super K>,R extends Row<K,R>> extends Table<K,R> {

    protected final ThreadLocal<Set<R>> unsortedRowsCache = new ThreadLocal<Set<R>>();

    private final ThreadLocal<SortedSet<R>> sortedRowsCache = new ThreadLocal<SortedSet<R>>();

    private final ThreadLocal<Map<K,R>> rowCache = new ThreadLocal<Map<K,R>>() {
        @Override
        protected Map<K,R> initialValue() {
            return new HashMap<K,R>();
        }
    };

    protected RowCacheTable(Class<K> keyClass, Class<R> rowClass, DaoDatabase database) {
        super(keyClass, rowClass, database);
    }

    @Override
    public void clearCaches(boolean requestOnly) {
        super.clearCaches(requestOnly);
        unsortedRowsCache.remove();
        sortedRowsCache.remove();
        rowCache.get().clear();
    }

    @Override
    public Set<R> getUnsortedRows() throws SQLException {
        Set<R> rows = unsortedRowsCache.get();
        if(rows==null) {
            rows = Collections.unmodifiableSet(getRowsNoCache());

            // Populate rowCache fully
            Map<K,R> cache = rowCache.get();
            cache.clear();
            for(R row : rows) if(cache.put(row.getKey(), row)!=null) throw new SQLException("Duplicate key: "+row.getKey());

            allRowsLoaded(rows);
            unsortedRowsCache.set(rows);
        }
        return rows;
    }

    /**
     * Called when all rows have been loaded at once.  This allows for subclasses
     * to populate any views or caches in a more efficient manner than row-by-row.
     *
     * This default implementation does nothing.
     */
    protected void allRowsLoaded(Set<R> rows) throws SQLException {
        // Does nothing.
    }

    @Override
    public SortedSet<R> getRows() throws SQLException {
        SortedSet<R> rows = sortedRowsCache.get();
        if(rows==null) {
            rows = Collections.unmodifiableSortedSet(new TreeSet<R>(getUnsortedRows()));
            sortedRowsCache.set(rows);
        }
        return rows;
    }

    @Override
    public R get(K key) throws NoRowException, SQLException {
        Map<K,R> cache = rowCache.get();
        if(cache.containsKey(key)) {
            R row = cache.get(key);
            if(row==null) throw new NoRowException(getClass().getSimpleName()+" not found: "+key);
            return row;
        }

        // Doesn't exist when all rows have been loaded
        if(unsortedRowsCache.get()!=null) throw new NoRowException(getClass().getSimpleName()+" not found: "+key);

        // Try single row query - cache hits and misses
        try {
            R row = getNoCache(key);
            assert row.getKey().equals(key);
            cache.put(key, row);
            return row;
        } catch(NoRowException err) {
            cache.put(key, null);
            throw new NoRowException(getClass().getSimpleName()+" not found: "+key, err);
        }
    }

    abstract protected R getNoCache(K key) throws NoRowException, SQLException;

    abstract protected Set<R> getRowsNoCache() throws SQLException;
}
