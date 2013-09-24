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
package com.aoindustries.util.ref;

/**
 * Helps keep track of reference counts.
 */
public final class ReferenceUtils {

	/**
	 * Acquires a reference count if the value is a ReferenceCount.
	 */
	public static <V> V acquire(V value) throws ReferenceException {
		if(value instanceof ReferenceCount<?>) {
			try {
				acquire((ReferenceCount<?>)value);
			} catch(Exception e) {
				throw new ReferenceException(e);
			}
		}
		return value;
	}

	/**
	 * Acquires a reference count.
	 */
	public static <V extends ReferenceCount<E>,E extends Exception> V acquire(V value) throws E {
		if(value!=null) value.incReferenceCount();
		return value;
	}

	/**
	 * Decrements a reference count if the value is a ReferenceCount.
	 *
	 * @return  Always returns null
	 */
	public static <V> V release(V value) throws ReferenceException {
		if(value instanceof ReferenceCount<?>) {
			try {
				release((ReferenceCount<?>)value);
			} catch(Exception e) {
				throw new ReferenceException(e);
			}
		}
		return null;
	}

	/**
	 * Decrements a reference count.
	 *
	 * @return  Always returns null
	 */
	public static <V extends ReferenceCount<E>,E extends Exception> V release(V value) throws E {
		if(value!=null) value.decReferenceCount();
		return null;
	}

	/**
	 * Replaces one value with another.
	 * If the newValue is a ReferenceCount, increments its reference count.
	 * If the oldValue is a ReferenceCount, decrements its reference count.
	 */
	public static <V> V replace(V oldValue, V newValue) throws ReferenceException {
		acquire(newValue);
		release(oldValue);
		return newValue;
	}

	/**
	 * Replaces one value with another.
	 * Increments its reference count for newValue.
	 * Decrements its reference count for oldValue.
	 */
	public static <V extends ReferenceCount<E>,E extends Exception> V replace(V oldValue, V newValue) throws E {
		acquire(newValue);
		release(oldValue);
		return newValue;
	}

	/**
     * Make no instances.
     */
    private ReferenceUtils() {
    }
}
