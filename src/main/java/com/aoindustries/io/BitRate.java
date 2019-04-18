/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2012, 2016, 2019  AO Industries, Inc.
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
package com.aoindustries.io;

import com.aoindustries.math.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * A <code>BitRate</code> consists of a long quantity and an optional unit.
 *
 * @author  AO Industries, Inc.
 */
public class BitRate implements Serializable, Comparable<BitRate> {

	public enum Unit {
		BIT("bit",     1L),
		KBIT("kbit",   1000L),
		KIBIT("Kibit", 1024L),
		MBIT("Mbit",   1000L*1000L),
		MIBIT("Mibit", 1024L*1024L),
		GBIT("Gbit",   1000L*1000L*1000L),
		GIBIT("Gibit", 1024L*1024L*1024L),
		TBIT("Tbit",   1000L*1000L*1000L*1000L),
		TIBIT("Tibit", 1024L*1024L*1024L*1024L),
		PBIT("Pbit",   1000L*1000L*1000L*1000L*1000L),
		PIBIT("Pibit", 1024L*1024L*1024L*1024L*1024L),
		EBIT("Ebit",   1000L*1000L*1000L*1000L*1000L*1000L),
		EIBIT("Eibit", 1024L*1024L*1024L*1024L*1024L*1024L),
		BYTE("byte",     8L*1L),
		KBYTE("kbyte",   8L*1000L),
		KIBYTE("Kibyte", 8L*1024L),
		MBYTE("Mbyte",   8L*1000L*1000L),
		MIBYTE("Mibyte", 8L*1024L*1024L),
		GBYTE("Gbyte",   8L*1000L*1000L*1000L),
		GIBYTE("Gibyte", 8L*1024L*1024L*1024L),
		TBYTE("Tbyte",   8L*1000L*1000L*1000L*1000L),
		TIBYTE("Tibyte", 8L*1024L*1024L*1024L*1024L),
		PBYTE("Pbyte",   8L*1000L*1000L*1000L*1000L*1000L),
		PIBYTE("Pibyte", 8L*1024L*1024L*1024L*1024L*1024L),
		EBYTE("Ebyte",   8L*1000L*1000L*1000L*1000L*1000L*1000L),
		EIBYTE("Eibyte", 8L*1024L*1024L*1024L*1024L*1024L*1024L);

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
	private static long getBitRate(long quantity, Unit unit) {
		if(quantity<1) throw new IllegalArgumentException("quantity<1");
		return SafeMath.multiply(quantity, unit==null ? 1 : unit.getCoefficient());
	}

	public static BitRate valueOf(String value) {
		return new BitRate(value);
	}

	private static final long serialVersionUID = -7706564767874892683L;

	private final long quantity;
	private final Unit unit;
	private transient Long bitRate;

	public BitRate(long bitRate) {
		this(bitRate, null);
	}

	/**
	 * @param unit if <code>null</code>, defaults to bits per second.
	 */
	public BitRate(long quantity, Unit unit) {
		this.quantity = quantity;
		this.unit = unit;
		this.bitRate = getBitRate(quantity, unit);
	}

	public BitRate(String value) {
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
		this.bitRate = getBitRate(quantity, unit);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		this.bitRate = getBitRate(quantity, unit);
	}

	/**
	 * Two BitRates are equal when they have the same quantity and the same unit.
	 *
	 * @see  #compareTo
	 */
	@Override
	public boolean equals(Object O) {
		if(!(O instanceof BitRate)) return false;
		BitRate other = (BitRate)O;
		return quantity==other.quantity && Objects.equals(unit, other.unit);
	}

	@Override
	public int hashCode() {
		return bitRate.hashCode();
	}

	@Override
	public String toString() {
		if(unit==null) return Long.toString(quantity);
		return Long.toString(quantity)+unit.getName();
	}

	@Override
	public int compareTo(BitRate o) {
		return bitRate.compareTo(o.bitRate);
	}

	public long getQuantity() {
		return quantity;
	}

	public Unit getUnit() {
		return unit;
	}

	public Long getBitRate() {
		return bitRate;
	}
}
