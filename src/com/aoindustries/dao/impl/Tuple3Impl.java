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
package com.aoindustries.dao.impl;

import com.aoindustries.dao.AbstractTuple;
import com.aoindustries.dao.Tuple3;
import java.text.Collator;

/**
 * A compound key with three columns.
 *
 * @author  AO Industries, Inc.
 */
public class Tuple3Impl<
	C1 extends Comparable<? super C1>,
	C2 extends Comparable<? super C2>,
	C3 extends Comparable<? super C3>
>
	extends AbstractTuple<Tuple3Impl<C1,C2,C3>>
	implements
		Tuple3<C1,C2,C3,Tuple3Impl<C1,C2,C3>>,
		Comparable<Tuple3Impl<C1,C2,C3>>
{

	private final C1 column1;
	private final C2 column2;
	private final C3 column3;

	public Tuple3Impl(Collator collator, C1 column1, C2 column2, C3 column3) {
		super(collator);
		this.column1 = column1;
		this.column2 = column2;
		this.column3 = column3;
    }

	@Override
	public Comparable<?>[] getColumns() {
		return new Comparable<?>[] {
			column1,
			column2,
			column3
		};
	}
	
	@Override
	public C1 getColumn1() {
		return column1;
	}
	
	@Override
	public C2 getColumn2() {
		return column2;
	}
	
	@Override
	public C3 getColumn3() {
		return column3;
	}
}
