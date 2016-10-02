/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016  AO Industries, Inc.
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
package com.aoindustries.util;

import com.aoindustries.lang.ObjectUtils;

/**
 * Two objects combined into a single.  Useful for returning two values combined.
 * This tuple is not comparable.
 *
 * @author  AO Industries, Inc.
 */
public class Tuple2<E1,E2> {

	private final E1 element1;
	private final E2 element2;

	public Tuple2(
		E1 element1,
		E2 element2
	) {
		this.element1 = element1;
		this.element2 = element2;
	}

	@Override
	public String toString() {
		return
			"("
			+ element1
			+ ',' + element2
		+ ')';
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof Tuple2<?,?>)) return false;
		Tuple2<?,?> other = (Tuple2<?,?>)obj;
		return
			ObjectUtils.equals(element1, other.element1)
			&& ObjectUtils.equals(element2, other.element2)
		;
	}

	private int hash;
	@Override
	public int hashCode() {
		int h = this.hash;
		if(h == 0) {
			h = ObjectUtils.hashCode(element1);
			h = h * 31 + ObjectUtils.hashCode(element2);
			this.hash = h;
		}
		return h;
	}

	public E1 getElement1() {
		return element1;
	}

	public E2 getElement2() {
		return element2;
	}
}
