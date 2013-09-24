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
	 * Replaces one value with another.
	 * If the oldValue is a ReferenceCount, decrements its reference count.
	 * If the newValue is a ReferenceCount, increments its reference count.
	 */
	public static Object replace(Object oldValue, Object newValue) throws ReferenceException {
		release(oldValue);
		if(newValue instanceof ReferenceCount<?>) {
			try {
				((ReferenceCount<?>)newValue).incReferenceCount();
			} catch(Exception e) {
				throw new ReferenceException(e);
			}
		}
		return newValue;
	}

	/**
	 * Decrements a reference count if the value is a ReferenceCount.
	 */
	public static void release(Object value) throws ReferenceException {
		if(value instanceof ReferenceCount<?>) {
			try {
				((ReferenceCount<?>)value).decReferenceCount();
			} catch(Exception e) {
				throw new ReferenceException(e);
			}
		}
	}

	/**
     * Make no instances.
     */
    private ReferenceUtils() {
    }
}
