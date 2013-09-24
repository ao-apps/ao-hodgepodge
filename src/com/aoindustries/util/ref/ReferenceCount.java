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
 * Any object that maintains a reference count to know when to release its
 * heavyweight resources.
 *
 * Objects start with a reference count of one.  When they are decremented to
 * zero, the cleanup will be performed.
 */
public interface ReferenceCount<E extends Exception> {

	/**
	 * Increments the reference count.
	 */
	void incReferenceCount();

	/**
	 * Decrements the reference count.
	 * Performs cleanup when decremented to zero.
	 *
	 * @throws IllegalStateException if count already zero
	 */
	void decReferenceCount() throws IllegalStateException, E;
}
