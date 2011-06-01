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
import com.aoindustries.util.AoCollections;
import com.aoindustries.util.WrappedException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

abstract public class AbstractTable<K extends Comparable<? super K>,R extends Row<K,R>> implements Table<K,R> {

    private final Class<K> keyClass;
    private final Class<R> rowClass;
    private final DaoDatabase database;

    protected final Map<K,R> map = new Map<K,R>() {
        @Override
        public int size() {
            return AbstractTable.this.size();
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
                    R row = AbstractTable.this.get(rowClass.cast(value).getKey());
                    if(row==null) throw new AssertionError();
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
                    R row = AbstractTable.this.get(keyClass.cast(key));
                    if(row==null) throw new AssertionError();
                    return row;
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
            return AbstractTable.this.size();
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
                    R row = AbstractTable.this.get(rowClass.cast(value).getKey());
                    if(row==null) throw new AssertionError();
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
                    R row = AbstractTable.this.get(keyClass.cast(key));
                    if(row==null) throw new AssertionError();
                    return row;
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

    protected AbstractTable(Class<K> keyClass, Class<R> rowClass, DaoDatabase database) {
        this.keyClass = keyClass;
        this.rowClass = rowClass;
        this.database = database;
    }

    @Override
    public DaoDatabase getDatabase() {
        return database;
    }

    @Override
    public void clearCaches() {
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

    @Override
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

    @Override
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

    @Override
    public Map<K,R> getMap() {
        return map;
    }

    @Override
    public SortedMap<K,R> getSortedMap() {
        return sortedMap;
    }

    /**
     * {@inheritDoc}  This default implementation is based on the class simple name.
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();
        /*
        String name = getClass().getName();
        int dotPos = name.lastIndexOf('.');
        return dotPos==-1 ? name : name.substring(dotPos+1);
         */
    }

    /**
     * {@inheritDoc}
     *
     * This default implementation returns the key unmodified.
     */
    @Override
    public K canonicalize(K key) {
        return key;
    }

    /**
     * {@inheritDoc}  This implementation iterates through the keys calling get.
     */
    @Override
    public SortedSet<R> getRows(Iterable<? extends K> keys) throws SQLException {
        Iterator<? extends K> iter = keys.iterator();
        if(!iter.hasNext()) return AoCollections.emptySortedSet();
        SortedSet<R> results = new TreeSet<R>();
        do {
            results.add(get(iter.next()));
        } while(iter.hasNext());
        return Collections.unmodifiableSortedSet(results);
    }
}
