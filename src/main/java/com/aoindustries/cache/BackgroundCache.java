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
package com.aoindustries.cache;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * A cache that is refreshed in the background, implementing only get, put, and size.
 * There is no remove; background cleaning is performed on the underlying map.
 * </p>
 * <p>
 * When the system is sitting idle, all cache entries are expired and there is
 * zero overhead.  Background refreshes only happen on recently used keys.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class BackgroundCache<K,V,E extends Exception> {

	/**
	 * The thread priority used for the timer.
	 */
	private static final int TIMER_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;

	/**
	 * A callable used to refresh the cache.
	 */
	public static interface Refresher<K,V,E extends Exception> {
		V call(K key) throws E;
	}

	/**
	 * The result of a refresh.
	 */
	public static class Result<V,E extends Exception> {

		private final V value;
		private final E exception;

		Result(V value) {
			this.value = value;
			this.exception = null;
		}

		Result(E exception) {
			this.exception = exception;
			this.value = null;
		}

		/**
		 * Gets the value for this entry, is always null when there is an exception.
		 */
		public V getValue() {
			return value;
		}

		/**
		 * Gets the exception that occurred during refresh.
		 */
		public E getException() {
			return exception;
		}
	}

	/**
	 * The background-verified and updated cache entries.
	 *
	 * @see  #lock  All read/write access must be under the lock
	 */
	class CacheEntry extends TimerTask {

		private final K key;

		private final Refresher<? super K,? extends V,? extends E> refresher;

		/**
		 * The last obtained result.
		 */
		private volatile Result<V,E> result;

		/**
		 * The time the entry was last refreshed.
		 */
		private volatile long refreshed;

		/**
		 * Has this entry been accessed since the last refresh?
		 * Used to know when to extend expiration.
		 */
		private volatile boolean accessedSinceRefresh;

		/**
		 * The time this entry will expire.
		 * When an entry is accessed, its expiration time is updated to be
		 * based on its refreshed time.
		 */
		private volatile long expiration;

		/**
		 * A cached result.
		 */
		CacheEntry(
			K key,
			Refresher<? super K,? extends V,? extends E> refresher,
			Result<V,E> result
		) {
			long currentTime = System.currentTimeMillis();
			this.key = key;
			this.refresher = refresher;
			this.result = result;
			this.refreshed = currentTime;
			this.accessedSinceRefresh = true; // Do not refresh immediately after creation
			this.expiration = currentTime + expirationAge;
		}

		/**
		 * Gets the most recently obtained result.
		 * Updates the expiration time as-needed.
		 */
		Result<V,E> getResult() {
			if(!accessedSinceRefresh) {
				accessedSinceRefresh = true;
				expiration = refreshed + expirationAge;
			}
			return result;
		}

		@Override
		public void run() {
			Thread currentThread = Thread.currentThread();
			if(currentThread.getPriority() != TIMER_THREAD_PRIORITY) {
				currentThread.setPriority(TIMER_THREAD_PRIORITY);
			}
			if(this != map.get(key)) {
				// This has been replaced, cancel this timer task
				cancel();
			} else {
				long currentTime = System.currentTimeMillis();
				if(
					// Expired expired
					currentTime >= expiration
					// System time set to the past
					|| currentTime < refreshed
				) {
					// Make sure this has not already been replaced
					map.remove(key, this);
					// Cancel this timer task
					cancel();
				} else {
					try {
						// Update entry
						result = runRefresher(refresher, key);
						refreshed = currentTime;
						accessedSinceRefresh = false;
					} catch(Throwable t) {
						// Drop from cache when any unexpected exception happens
						map.remove(key, this);
						// Cancel this timer task
						cancel();
						// Log unexpected exception
						if(logger.isLoggable(Level.WARNING)) {
							logger.log(
								Level.WARNING,
								"BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Unexpected exception in background cache refresh, dropped from cache",
								t
							);
						}
					}
				}
			}
		}
	}

	private final String name;
	private final Class<E> exceptionClass;
	final long refreshInterval;
	private final long expirationAge;
	final Logger logger;

	/**
	 * Timer used for background refreshing and cleaning.
	 * This uses daemon threads.
	 */
	final Timer timer;

	final ConcurrentMap<K,CacheEntry> map = new ConcurrentHashMap<K,CacheEntry>();

	/**
	 * @param name             The name resources are based on, such as background thread names.
	 *
	 * @param exceptionClass   The class object used to verify exception types are runtime
	 *
	 * @param refreshInterval  The time between cache entry refreshes
	 *
	 * @param expirationAge    The time the a cache entry will expire if it has not been accessed,
	 *                         the actual expiration may happen after this time as it is only checked
	 *                         during refreshes.
	 */
	public BackgroundCache(
		String name,
		Class<E> exceptionClass,
		long refreshInterval,
		long expirationAge,
		Logger logger
	) {
		this.name = name;
		this.exceptionClass = exceptionClass;
		this.refreshInterval = refreshInterval;
		this.expirationAge = expirationAge;
		this.logger = logger;
		this.timer = new Timer(name + "-backgroundTimer", true);
	}

	/**
	 * Uses the default logger.
	 *
	 * @see  #BackgroundCache(java.lang.String, java.lang.Class, long, long, java.util.logging.Logger)
	 */
	public BackgroundCache(
		String name,
		Class<E> exceptionClass,
		long refreshInterval,
		long expirationAge
	) {
		this(
			name,
			exceptionClass,
			refreshInterval,
			expirationAge,
			Logger.getLogger(BackgroundCache.class.getName())
		);
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Stops this cache.  This cache should not be used after this is called.
	 * Repeated calls to stop are allowed.
	 */
	public void stop() {
		timer.cancel();
		map.clear();
	}

	/**
	 * Gets the value if currently in the cache.  If not,
	 * Runs the refresher immediately to obtain the result, then
	 * places an entry into the cache.
	 *
	 * @return  The result obtained from either the cache or this refresher
	 *
	 * @see  #get(java.lang.Object)
	 * @see  #put(java.lang.Object, com.aoindustries.cache.BackgroundCache.Refresher)
	 */
	public Result<V,E> get(
		K key,
		Refresher<? super K,? extends V,? extends E> refresher
	) {
		Result<V,E> result = get(key);
		if(result == null) result = put(key, refresher);
		return result;
	}

	/**
	 * Gets a cached result for the given key, null if not cached.
	 * Extends the expiration of the cache entry.
	 */
	public Result<V,E> get(K key) {
		CacheEntry entry = map.get(key);
		if(entry == null) {
			return null;
		} else {
			return entry.getResult();
		}
	}

	Result<V,E> runRefresher(
		Refresher<? super K,? extends V,? extends E> refresher,
		K key
	) throws IllegalStateException {
		try {
			return new Result<V,E>(refresher.call(key));
		} catch(Exception e) {
			if(exceptionClass.isInstance(e)) {
				return new Result<V,E>(exceptionClass.cast(e));
			} else if(e instanceof RuntimeException) {
				throw (RuntimeException)e;
			} else {
				AssertionError ae = new AssertionError("Unexpected exception type");
				ae.initCause(e);
				throw ae;
			}
		}
	}

	/**
	 * Runs the refresher immediately to obtain the result, then
	 * places an entry into the cache, replacing any existing entry under this key.
	 *
	 * @return  The result obtained from this refresher
	 */
	public Result<V,E> put(
		K key,
		Refresher<? super K,? extends V,? extends E> refresher
	) {
		Result<V,E> result = runRefresher(refresher, key);
		put(key, refresher, result);
		return result;
	}

	/**
	 * Places a result into the cache, replacing any existing entry under this key.
	 */
	public void put(
		K key,
		Refresher<? super K,? extends V,? extends E> refresher,
		V value
	) {
		put(key, refresher, new Result<V,E>(value));
	}

	/**
	 * Places a result into the cache, replacing any existing entry under this key.
	 */
	public void put(
		K key,
		Refresher<? super K,? extends V,? extends E> refresher,
		E exception
	) {
		put(key, refresher, new Result<V,E>(exception));
	}

	/**
	 * Puts a new entry, replacing any existing.  Schedules refresh on the timer.
	 * <p>
	 * Any formerly scheduled timer is not canceled.  It will detect it has been
	 * replaced when it is called and cancel itself.  This puts more of the load
	 * on the background thread.
	 * </p>
	 */
	private void put(
		K key,
		Refresher<? super K,? extends V,? extends E> refresher,
		Result<V,E> result
	) {
		CacheEntry entry = new CacheEntry(key, refresher, result);
		map.put(key, entry);
		timer.schedule(entry, refreshInterval, refreshInterval);
	}

	/**
	 * Gets the current size of the cache.
	 */
	public int size() {
		return map.size();
	}
}
