/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2012, 2016, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * A <code>ByteCount</code> consists of a long quantity and an optional unit.
 *
 * @author  AO Industries, Inc.
 */
public class ByteCount implements Serializable, Comparable<ByteCount> {

	public enum Unit {
		BYTE("byte",     1L),
		KBYTE("kbyte",   1000L),
		KIBYTE("Kibyte", 1024L),
		MBYTE("Mbyte",   1000L*1000L),
		MIBYTE("Mibyte", 1024L*1024L),
		GBYTE("Gbyte",   1000L*1000L*1000L),
		GIBYTE("Gibyte", 1024L*1024L*1024L),
		TBYTE("Tbyte",   1000L*1000L*1000L*1000L),
		TIBYTE("Tibyte", 1024L*1024L*1024L*1024L),
		PBYTE("Pbyte",   1000L*1000L*1000L*1000L*1000L),
		PIBYTE("Pibyte", 1024L*1024L*1024L*1024L*1024L),
		EBYTE("Ebyte",   1000L*1000L*1000L*1000L*1000L*1000L),
		EIBYTE("Eibyte", 1024L*1024L*1024L*1024L*1024L*1024L);

		private static final Unit[] values = values();

		private final String name;
		private final long coefficient;

		private Unit(String name, long coefficient) {
			this.name = name;
			this.coefficient = coefficient;
		}

		@Override
		public String toString() {
			return name;
		}

		public String getName() {
			return name;
		}

		public long getCoefficient() {
			return coefficient;
		}
	}

	/**
	 * @param unit if <code>null</code>, defaults to bits per second.
	 */
	private static long getByteCount(long quantity, Unit unit) {
		if(quantity<1) throw new IllegalArgumentException("quantity<1");
		return Math.multiplyExact(quantity, unit==null ? 1 : unit.getCoefficient());
	}

	public static ByteCount valueOf(String value) {
		return new ByteCount(value);
	}

	private static final long serialVersionUID = 1712831669919116474L;

	private final long quantity;
	private final Unit unit;
	private transient long byteCount;

	public ByteCount(long byteCount) {
		this(byteCount, null);
	}

	/**
	 * @param unit if <code>null</code>, defaults to bits per second.
	 */
	public ByteCount(long quantity, Unit unit) {
		this.quantity = quantity;
		this.unit = unit;
		this.byteCount = getByteCount(quantity, unit);
	}

	public ByteCount(String value) {
		Unit valueUnit = null;
		for(int c=Unit.values.length-1; c>=0; c--) {
			Unit u = Unit.values[c];
			String name = u.getName();
			if(value.endsWith(name)) {
				valueUnit = u;
				value = value.substring(0, value.length() - name.length());
			}
		}
		this.quantity = Long.parseLong(value);
		this.unit = valueUnit;
		this.byteCount = getByteCount(quantity, unit);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		this.byteCount = getByteCount(quantity, unit);
	}

	/**
	 * Two BitRates are equal when they have the same quantity and the same unit.
	 *
	 * @see  #compareTo
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ByteCount)) return false;
		ByteCount other = (ByteCount)obj;
		return quantity==other.quantity && Objects.equals(unit, other.unit);
	}

	@Override
	public int hashCode() {
		return (int)(byteCount ^ (byteCount >>> 32));
	}

	@Override
	public String toString() {
		if(unit==null) return Long.toString(quantity);
		return Long.toString(quantity)+unit.getName();
	}

	@Override
	public int compareTo(ByteCount o) {
		return byteCount<o.byteCount ? -1 : byteCount==o.byteCount ? 0 : 1;
	}

	public long getQuantity() {
		return quantity;
	}

	public Unit getUnit() {
		return unit;
	}

	public long getByteCount() {
		return byteCount;
	}
}
