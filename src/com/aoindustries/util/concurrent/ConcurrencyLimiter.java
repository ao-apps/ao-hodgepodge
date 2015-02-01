/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015  AO Industries, Inc.
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Limits the concurrency to a resource identified by any arbitrary key object.
 * When a second thread tries to access the same resource as a previous thread,
 * it will share the results that are obtained by the previous thread.
 */
final public class ConcurrencyLimiter<K,R> {

    //private static final Logger logger = Logger.getLogger(ConcurrencyLimiter.class.getName());

	private static class ResultsCache<R> {
		private int threadCount;
		private boolean finished;
		private R result;
		private Throwable throwable;
	}

	private final Map<K,ResultsCache<R>> executeSerializedStatus = new HashMap<K,ResultsCache<R>>();

	public ConcurrencyLimiter() {
	}

	/**
	 * <p>
	 * Executes a callable at most once for the given key.  If the callable is
	 * in the process of being executed by a different thread (determined by key,
	 * not the callable instance), the current thread will wait and use the
	 * results obtained by the other thread.
	 * </p>
	 * <p>
	 * Consider the following scenario:
	 * <ol>
	 *   <li>Thread A invokes MySQL: "CHECK TABLE example FAST QUICK"</li>
	 *   <li>Thread B invokes MySQL: "CHECK TABLE example FAST QUICK" before Thread A has finished</li>
	 *   <li>Thread B wait for results determined by Thread A</li>
	 *   <li>Thread A completes, passes results to Thread B</li>
	 *   <li>Threads A and B both return the results obtained only by Thread A</li>
	 * </ol>
	 * </p>
	 */
	public R executeSerialized(K key, Callable<? extends R> callable) throws InterruptedException, ExecutionException {
		final boolean isFirstThread;
		final ResultsCache<R> resultsCache;
		synchronized(executeSerializedStatus) {
			// Look for any existing entry for this key
			ResultsCache<R>resultsCacheT = executeSerializedStatus.get(key);
			if(resultsCacheT==null) {
				executeSerializedStatus.put(key, resultsCacheT = new ResultsCache<R>());
			}
			resultsCache = resultsCacheT;
			isFirstThread = resultsCache.threadCount==0;
			if(resultsCache.threadCount==Integer.MAX_VALUE) throw new IllegalStateException("threadCount==Integer.MAX_VALUE");
			resultsCache.threadCount++;
		}
		try {
			synchronized(resultsCache) {
				if(isFirstThread) {
					// Invoke callable
					try {
						resultsCache.result = callable.call();
					} catch(Throwable throwable) {
						resultsCache.throwable = throwable;
					}
					resultsCache.finished = true;
					resultsCache.notifyAll();
				} else {
					// Wait for results from the first thread, including any exception
					while(!resultsCache.finished) {
						resultsCache.wait();
					}
				}
				assert resultsCache.finished;
				if(resultsCache.throwable!=null) throw new ExecutionException(resultsCache.throwable);
				return resultsCache.result;
			}
		} finally {
			synchronized(executeSerializedStatus) {
				assert resultsCache.threadCount > 0;
				resultsCache.threadCount--;
				if(resultsCache.threadCount==0) {
					executeSerializedStatus.remove(key);
				}
			}
		}
	}
}
