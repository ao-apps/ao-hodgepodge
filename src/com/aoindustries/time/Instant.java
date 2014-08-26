/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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
package com.aoindustries.time;

import java.io.InvalidObjectException;
import java.io.ObjectInputValidation;
import java.io.Serializable;

/**
 * Wraps the number of seconds from the Epoch as well as positive nanoseconds into an
 * immutable value type.
 * <p>
 * This will be deprecated once Java 8 is ubiquitous and only serves as an extremely
 * simplified stop-gap.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class Instant implements Comparable<Instant>, Serializable, ObjectInputValidation {

	private static final int NANOS_PER_SECOND = 1000000000;

	public static final Instant EPOCH = new Instant(0, 0);

	private static final long serialVersionUID = 1L;

	/**
	 * Parses an Instant's string representation.
	 *
	 * @return Instant the instant or null when toString is null
	 *
	 * @throws IllegalArgumentException when unable to parse
	 */
	public static Instant valueOf(String s) {
		if(s == null) return null;
		int dotPos = s.indexOf('.');
		if(dotPos == -1) throw new IllegalArgumentException("Period (.) not found: " + s);
		return new Instant(
			Long.parseLong(s.substring(0, dotPos)),
			Integer.parseInt(s.substring(dotPos + 1))
		);
	}

	final long seconds;
	final int nanos;

	public Instant(long seconds, int nanos) {
		this.seconds = seconds;
		this.nanos = nanos;
		validate();
	}

    private void validate() throws IllegalArgumentException {
		if(nanos < 0 || nanos >= NANOS_PER_SECOND) throw new IllegalArgumentException("nanoseconds out of range 0-" + (NANOS_PER_SECOND - 1));
    }

    @Override
    public void validateObject() throws InvalidObjectException {
        try {
            validate();
        } catch(IllegalArgumentException err) {
            InvalidObjectException newErr = new InvalidObjectException(err.getMessage());
            newErr.initCause(err);
            throw newErr;
        }
    }

	private Object readResolve() {
		if(seconds == 0 && nanos == 0) return EPOCH;
		return this;
	}

	static String toString(long seconds, int nanos) {
		StringBuilder sb = new StringBuilder(
			20 // Length of "-9223372036854775808"
			+ 1 // "."
			+ 9 // Nanoseconds
		);
		sb.append(seconds).append('.');
		if(nanos < 100000000) {
			sb.append('0');
			if(nanos < 10000000) {
				sb.append('0');
				if(nanos < 1000000) {
					sb.append('0');
					if(nanos < 100000) {
						sb.append('0');
						if(nanos < 10000) {
							sb.append('0');
							if(nanos < 1000) {
								sb.append('0');
								if(nanos < 100) {
									sb.append('0');
									if(nanos < 10) {
										sb.append('0');
									}
								}
							}
						}
					}
				}
			}
		}
		sb.append(nanos);
		return sb.toString();
	}

	@Override
	public String toString() {
		return toString(seconds, nanos);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Instant)) return false;
		return equals((Instant)obj);
	}

	public boolean equals(Instant other) {
		return
			other != null
			&& seconds == other.seconds
			&& nanos == other.nanos
		;
	}

	@Override
	public int hashCode() {
		return (int)(seconds ^ (seconds >>> 32)) ^ nanos;
	}

	@Override
	public int compareTo(Instant other) {
		if(seconds < other.seconds) return -1;
		if(seconds > other.seconds) return 1;
		if(nanos < other.nanos) return -1;
		if(nanos > other.nanos) return 1;
		return 0;
	}

	public long getSeconds() {
		return seconds;
	}

	/**
	 * <p>
	 * The nanoseconds, to simplify this is always in the positive direction.
	 * For negative instants, this means the nanos goes up from zero to 1 billion,
	 * then the seconds go up one (toward zero).  This may be counterintuitive if
	 * one things of nanoseconds as a fractional part of seconds, but this definition
	 * leads to a very clean implementation.
	 * </p>
	 * <p>
	 * Counting up by nanoseconds:
	 * </p>
	 * <ol>
	 *   <li>-1.999999998</li>
	 *   <li>-1.999999999</li>
	 *   <li>0.000000000</li>
	 *   <li>0.000000001</li>
	 *   <li>0.000000002</li>
	 * </ol>
	 */
	public int getNanos() {
		return nanos;
	}
}
