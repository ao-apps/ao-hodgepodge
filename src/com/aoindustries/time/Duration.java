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
 * Wraps a duration as the number of seconds as well as positive nanoseconds into an
 * immutable value type.
 * <p>
 * This will be deprecated once Java 8 is ubiquitous and only serves as an extremely
 * simplified stop-gap.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class Duration implements Comparable<Duration>, Serializable, ObjectInputValidation {

	private static final int NANOS_PER_SECOND = 1000000000;

	public static final Duration ZERO = new Duration(0, 0);

	public static Duration between(Instant startInclusive, Instant endExclusive) {
		long diffSeconds = endExclusive.seconds - startInclusive.seconds;
		int diffNanos = endExclusive.nanos - startInclusive.nanos;
		if(diffNanos < 0) {
			diffSeconds--;
			diffNanos += NANOS_PER_SECOND;
		}
		return new Duration(diffSeconds, diffNanos);
	}

	private static final long serialVersionUID = 1L;

	private final long seconds;
	private final int nanos;

	public Duration(long seconds, int nanos) {
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
		if(seconds == 0 && nanos == 0) return ZERO;
		return this;
	}

	@Override
	public String toString() {
		return Instant.toString(seconds, nanos);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Duration)) return false;
		return equals((Duration)obj);
	}

	public boolean equals(Duration other) {
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
	public int compareTo(Duration other) {
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
	 * For negative durations, this means the nanos goes up from zero to 1 billion,
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

	private static final long MINIMUM_NANO_DURATION_SECONDS = Long.MIN_VALUE / NANOS_PER_SECOND;
	private static final long MAXIMUM_NANO_DURATION_SECONDS = (Long.MAX_VALUE - NANOS_PER_SECOND) / NANOS_PER_SECOND;

	/**
	 * Gets this duration as a number of nanoseconds only.
	 * This covers a range around -292 years to +292 years.
	 *
	 * @throws ArithmeticException if duration is outside the range representable in nanoseconds
	 */
	public long getNanoDuration() throws ArithmeticException {
		if(
			seconds < MINIMUM_NANO_DURATION_SECONDS
			|| seconds > MAXIMUM_NANO_DURATION_SECONDS
		) {
			throw new IllegalArgumentException("seconds out of range " + MINIMUM_NANO_DURATION_SECONDS + "-" + MAXIMUM_NANO_DURATION_SECONDS);
		}
		return seconds * NANOS_PER_SECOND + nanos;
	}
}
