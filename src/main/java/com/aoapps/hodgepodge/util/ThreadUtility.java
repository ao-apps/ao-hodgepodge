/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2016, 2021, 2022  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.util;

/**
 * @author  AO Industries, Inc.
 */
public final class ThreadUtility {

	/** Make no instances. */
	private ThreadUtility() {throw new AssertionError();}

	public static int getThreadCount() {
		return getTopLevelThreadGroup().activeCount();
	}

	public static ThreadGroup getTopLevelThreadGroup() {
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		ThreadGroup parent;
		while((parent = tg.getParent()) != null) {
			tg = parent;
		}
		return tg;
	}
}
