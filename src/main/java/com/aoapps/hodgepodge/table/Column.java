/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2016, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.table;

/**
 * An abstract structure for columns.
 *
 * @author  AO Industries, Inc.
 */
public class Column implements Comparable<Column> {

  private final String name;
  private final IndexType indexType;

  public Column(String name, IndexType indexType) {
    this.name = name;
    this.indexType = indexType;
  }

  /**
   * Two column with the same name are equal.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Column)) {
      return false;
    }
    Column other = (Column)obj;
    return name.equals(other.name);
  }

  /**
   * Hashed by column name only.
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Ordered by column name only.
   */
  @Override
  public int compareTo(Column o) {
    int diff = name.compareToIgnoreCase(o.name);
    if (diff != 0) {
      return diff;
    }
    return name.compareTo(o.name);
  }

  @Override
  public String toString() {
    if (indexType != IndexType.NONE) {
      return name+" ("+indexType+')';
    }
    return name;
  }

  public String getName() {
    return name;
  }

  public IndexType getIndexType() {
    return indexType;
  }
}
