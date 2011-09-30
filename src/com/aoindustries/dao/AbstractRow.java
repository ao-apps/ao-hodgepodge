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

abstract public class AbstractRow<K extends Comparable<? super K>,R extends AbstractRow<K,? extends R>> implements Row<K,R> {

    private final DaoDatabase database;
    private final Class<R> clazz;

    protected AbstractRow(
        DaoDatabase database,
        Class<R> clazz
    ) {
        this.database = database;
        this.clazz = clazz;
    }

    /**
     * The default String representation is the key value.
     */
    @Override
    public String toString() {
        return getKey().toString();
    }

    /**
     * The default hashCode is based on the key value.
     */
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    /**
     * By default equality is based on same database, compatible class, and equal canonical key objects.
     */
    @Override
    public boolean equals(Object o) {
        if(o==null) return false;
        if(!clazz.isInstance(o)) return false;
        AbstractRow<K,? extends R> other = clazz.cast(o);
        if(database!=other.database) return false;
        K canonicalKey1 = getTable().canonicalize(getKey());
        K canonicalKey2 = other.getTable().canonicalize(other.getKey());
        return canonicalKey1.equals(canonicalKey2);
    }

    /**
     * The default ordering is based on key comparison.  If both keys
     * are Strings, will use the database collator.
     */
    @Override
    public int compareTo(R o) {
        K key1 = getKey();
        K key2 = o.getKey();
        if(key1.getClass()==String.class && key2.getClass()==String.class) {
            String s1 = key1.toString();
            String s2 = key2.toString();
            // TODO: If both strings begin with a number, sort by that first
            // TODO: This is for lot numbers, such as 1A, 1B, 2, 3, 10, 20, 100A
            return s1.equals(s2) ? 0 : getDatabase().getCollator().compare(s1, s2);
        } else {
            return key1.compareTo(key2);
        }
    }

    protected DaoDatabase getDatabase() {
        return database;
    }
}
