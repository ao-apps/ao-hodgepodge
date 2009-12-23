/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.table;

/**
 * An abstract structure for columns.
 *
 * @author  AO Industries, Inc.
 */
public class Column implements Comparable<Column> {

    private final String columnName;
    private final boolean unique;

    public Column(String columnName, boolean unique) {
        this.columnName = columnName;
        this.unique = unique;
    }

    @Override
    public boolean equals(Object O) {
        if(O==null) return false;
        if(!(O instanceof Column)) return false;
        Column other = (Column)O;
        return
            columnName.equals(other.columnName)
            && unique==other.unique
        ;
    }

    @Override
    public int hashCode() {
        return columnName.hashCode()<<1 | (unique ? 1 : 0);
    }

    public int compareTo(Column o) {
        int diff = columnName.compareToIgnoreCase(o.columnName);
        if(diff!=0) return diff;
        diff = columnName.compareTo(o.columnName);
        if(diff!=0) return diff;
        return (o.unique == unique ? 0 : (unique ? 1 : -1));
    }

    @Override
    public String toString() {
        if(unique) return columnName+" (unique)";
        return columnName;
    }
    
    public String getColumnName() {
        return columnName;
    }

    public boolean isUnique() {
        return unique;
    }
}
