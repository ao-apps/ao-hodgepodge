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

/**
 * A friendly wrapper around database meta data.
 *
 * @author  AO Industries, Inc.
 */
public class Column {

    private final Table table;
    private final String name;
    private final int dataType;
    private final String typeName;
    private final Integer columnSize;
    private final Integer decimalDigits;
    private final int nullable;
    private final String columnDef;
    private final Integer charOctetLength;
    private final int ordinalPosition;
    private final String isNullable;
    private final String isAutoincrement;

    protected Column(
        Table table,
        String name,
        int dataType,
        String typeName,
        Integer columnSize,
        Integer decimalDigits,
        int nullable,
        String columnDef,
        Integer charOctetLength,
        int ordinalPosition,
        String isNullable,
        String isAutoincrement
    ) {
        this.table = table;
        this.name = name;
        this.dataType = dataType;
        this.typeName = typeName;
        this.columnSize = columnSize;
        this.decimalDigits = decimalDigits;
        this.nullable = nullable;
        this.columnDef = columnDef;
        this.charOctetLength = charOctetLength;
        this.ordinalPosition = ordinalPosition;
        this.isNullable = isNullable;
        this.isAutoincrement = isAutoincrement;
    }

    @Override
    public String toString() {
        return name;
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public int getDataType() {
        return dataType;
    }

    public String getTypeName() {
        return typeName;
    }

    public Integer getColumnSize() {
        return columnSize;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public int getNullable() {
        return nullable;
    }

    public String getColumnDef() {
        return columnDef;
    }

    public Integer getCharOctetLength() {
        return charOctetLength;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public String getIsAutoincrement() {
        return isAutoincrement;
    }
}
