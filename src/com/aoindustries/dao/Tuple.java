/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

/**
 * Allows sets of columns to be used as multi-column keys.
 *
 * @author  AO Industries, Inc.
 */
public interface Tuple<
	T extends Tuple<T> & Comparable<? super T>
> {

	/**
	 * Gets an array of all column values.
	 */
	Comparable<?>[] getColumns();

	/**
	 * Based on the column values (column1,column2,...)
	 */
	@Override
	String toString();

	/**
	 * The equals is based on equal column values.
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * The hashCode is based on the column values.
	 */
	@Override
	int hashCode();

    /**
     * The default ordering is based on column value comparisons.  If both values
     * are Strings, will use the database collator.
	 *
	 * If one tuple has few columns than the other, and all the values are equal,
	 * the tuple with fewer columns is considered to be first.
     */
    // @Override
    int compareTo(T o);
}
