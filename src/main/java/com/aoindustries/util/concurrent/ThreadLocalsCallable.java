/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2016  AO Industries, Inc.
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
package com.aoindustries.util.concurrent;

import java.util.concurrent.Callable;

/**
 * Copies any number of ThreadLocal values from the current thread onto the thread
 * that runs the provided callable.
 *
 * @see  ThreadLocal
 */
public class ThreadLocalsCallable<T> implements Callable<T> {

	private final Callable<T> task;
	private final ThreadLocal<?>[] threadLocals;
	private final Object[] values;

	public ThreadLocalsCallable(Callable<T> task, ThreadLocal<?> ... threadLocals) {
		this.task = task;
		this.threadLocals = threadLocals;
		int len = threadLocals.length;
		Object[] vals = new Object[len];
		for(int i=0; i<len; i++) {
			vals[i] = threadLocals[i].get();
		}
		this.values = vals;
	}

	@Override
	public T call() throws Exception {
		ThreadLocal<?>[] tls = this.threadLocals;
		int len = tls.length;
		Object[] oldValues = new Object[len];
		for(int i=0; i<len; i++) {
			oldValues[i] = tls[i].get();
		}
		try {
			Object[] vals = this.values;
			for(int i=0; i<len; i++) {
				@SuppressWarnings("unchecked")
				ThreadLocal<Object> tl = (ThreadLocal<Object>)tls[i];
				tl.set(vals[i]);
			}
			return task.call();
		} finally {
			for(int i=0; i<len; i++) {
				@SuppressWarnings("unchecked")
				ThreadLocal<Object> tl = (ThreadLocal<Object>)tls[i];
				tl.set(oldValues[i]);
			}
		}
	}
}
