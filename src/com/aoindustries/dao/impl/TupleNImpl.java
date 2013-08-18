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
import com.aoindustries.dao.TupleN;
import java.text.Collator;
import java.util.Arrays;

/**
 * A compound key with any number of columns of the same type.
 *
 * @author  AO Industries, Inc.
 */
public class TupleNImpl<
	C extends Comparable<? super C>
>
	extends AbstractTuple<TupleNImpl<C>>
	implements
		TupleN<C,TupleNImpl<C>>,
		Comparable<TupleNImpl<C>>
{

	private final C[] columns;

	public TupleNImpl(Collator collator, C ... columns) {
		super(collator);
		this.columns = Arrays.copyOf(columns, columns.length); // Defensive copy
    }

	@Override
	public C[] getColumns() {
		return Arrays.copyOf(columns, columns.length); // Defensive copy
	}
}
