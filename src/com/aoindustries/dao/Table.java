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
import com.aoindustries.util.WrappedException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

abstract public class Table<K extends Comparable<? super K>,R extends Row<K,R>> implements Collection<R> {

    private final Class<K> keyClass;
    private final Class<R> rowClass;
    private final DaoDatabase database;

    protected final Map<K,R> map = new Map<K,R>() {
        @Override
        public int size() {
            return Table.this.size();
        }

        @Override
        public boolean isEmpty() {
            return size()==0;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key)!=null;
        }

        @Override
        public boolean containsValue(Object value) {
            if(value!=null && rowClass.isInstance(value)) {
                try {
                    R row = Table.this.get(rowClass.cast(value).getKey());
                    return true;
                } catch(NoRowException err) {
                    return false;
                } catch(SQLException err) {
                    throw new WrappedException(err);
                }
            } else {
                return false;
            }
        }

        @Override
        public R get(Object key) {
            if(key!=null && keyClass.isInstance(key)) {
                try {
                    return Table.this.get(keyClass.cast(key));
                } catch(NoRowException err) {
                    return null;
                } catch(SQLException err) {
                    throw new WrappedException(err);
                }
            } else {
                return null;
            }
        }

        @Override
        public R put(K key, R value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public R remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends K, ? extends R> map) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<K> keySet() {
            throw new UnsupportedOperationException("TODO: Not supported yet.");
        }

        @Override
        public Collection<R> values() {
            try {
                return getUnsortedRows();
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public Set<Map.Entry<K,R>> entrySet() {
            throw new UnsupportedOperationException("TODO: Not supported yet.");
        }
    };

    protected final SortedMap<K,R> sortedMap = new SortedMap<K,R>() {
        @Override
        public int size() {
            return Table.this.size();
        }

        @Override
        public boolean isEmpty() {
            return size()==0;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key)!=null;
        }

        @Override
        public boolean containsValue(Object value) {
            if(value!=null && rowClass.isInstance(value)) {
                try {
                    R row = Table.this.get(rowClass.cast(value).getKey());
                    return true;
                } catch(NoRowException err) {
                    return false;
                } catch(SQLException err) {
                    throw new WrappedException(err);
                }
            } else {
                return false;
            }
        }

        @Override
        public R get(Object key) {
            if(key!=null && keyClass.isInstance(key)) {
                try {
                    return Table.this.get(keyClass.cast(key));
                } catch(NoRowException err) {
                    return null;
                } catch(SQLException err) {
                    throw new WrappedException(err);
                }
            } else {
                return null;
            }
        }

        @Override
        public R put(K key, R value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public R remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends K, ? extends R> map) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<K> keySet() {
            throw new UnsupportedOperationException("TODO: Not supported yet.");
        }

        @Override
        public Collection<R> values() {
            try {
                return getRows();
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public Set<Map.Entry<K,R>> entrySet() {
            throw new UnsupportedOperationException("TODO: Not supported yet.");
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public SortedMap<K, R> subMap(K fromKey, K toKey) {
            throw new UnsupportedOperationException("TODO: Not supported yet.");
        }

        @Override
        public SortedMap<K, R> headMap(K toKey) {
            throw new UnsupportedOperationException("TODO: Not supported yet.");
        }

        @Override
        public SortedMap<K, R> tailMap(K fromKey) {
            throw new UnsupportedOperationException("TODO: Not supported yet.");
        }

        @Override
        public K firstKey() throws NoSuchElementException {
            try {
                return getRows().first().getKey();
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        }

        @Override
        public K lastKey() {
            try {
                return getRows().last().getKey();
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        }
    };

    protected Table(Class<K> keyClass, Class<R> rowClass, DaoDatabase database) {
        this.keyClass = keyClass;
        this.rowClass = rowClass;
        this.database = database;
    }

    public DaoDatabase getDatabase() {
        return database;
    }

    /**
     * Any overriding method should call super.clearCaches(boolean).
     */
    public void clearCaches(boolean requestOnly) {
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends R> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o : c) if(!map.containsValue(o)) return false;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(R e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        try {
            return getRows().toArray(a);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    @Override
    public Object[] toArray() {
        try {
            return getRows().toArray();
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    @Override
    public boolean contains(Object o) {
        return map.containsValue(o);
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    public int getSize() throws SQLException {
        return getUnsortedRows().size();
    }

    @Override
    public int size() {
        try {
            return getSize();
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Iterates the rows in sorted order.
     */
    public Iterator<R> getIterator() throws SQLException {
        return getRows().iterator();
    }

    /**
     * Iterates the rows in sorted order.
     */
    @Override
    public Iterator<R> iterator() {
        try {
            return getIterator();
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    /**
     * Gets a map view of this table.
     */
    public Map<K,R> getMap() {
        return map;
    }

    /**
     * Gets a sorted map view of this table.
     */
    public SortedMap<K,R> getSortedMap() {
        return sortedMap;
    }

    /**
     * Gets the table name.  This default implementation is based on the class name.
     */
    public String getName() {
        String name = getClass().getName();
        int dotPos = name.lastIndexOf('.');
        return dotPos==-1 ? name : name.substring(dotPos+1);
    }

    /**
     * Gets all rows in no particular order.
     */
    abstract public Set<R> getUnsortedRows() throws SQLException;

    /**
     * Gets all rows, sorted by their natural ordering.
     */
    abstract public SortedSet<R> getRows() throws SQLException;

    /**
     * Gets the row with the provided key.
     *
     * @throws NoRowException if not found
     * @throws SQLException if database error occurs
     */
    abstract public R get(K key) throws NoRowException, SQLException;
}
