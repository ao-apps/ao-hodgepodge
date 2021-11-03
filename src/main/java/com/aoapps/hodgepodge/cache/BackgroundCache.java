/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2016, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.hodgepodge.cache;

import java.util.Date;
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
 * @param  <Ex>  An arbitrary exception type that may be thrown
 *
 * @author  AO Industries, Inc.
 */
public class BackgroundCache<K, V, Ex extends Throwable> {

	/**
	 * The thread priority used for the timer.
	 */
	private static final int TIMER_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;

	/**
	 * A callable used to refresh the cache.
	 *
	 * @param  <Ex>  An arbitrary exception type that may be thrown
	 */
	@FunctionalInterface
	public static interface Refresher<K, V, Ex extends Throwable> {
		V call(K key) throws Ex;
	}

	/**
	 * The result of a refresh.
	 *
	 * @param  <Ex>  An arbitrary exception type that may be thrown
	 */
	public static class Result<V, Ex extends Throwable> {

		private final V value;
		private final Ex exception;

		Result(V value) {
			this.value = value;
			this.exception = null;
		}

		Result(Ex exception) {
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
		public Ex getException() {
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

		private final Refresher<? super K, ? extends V, ? extends Ex> refresher;

		/**
		 * The last obtained result.
		 */
		private volatile Result<V, Ex> result;

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
			Refresher<? super K, ? extends V, ? extends Ex> refresher,
			Result<V, Ex> result
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
		Result<V, Ex> getResult() {
			if(!accessedSinceRefresh) {
				accessedSinceRefresh = true;
				expiration = refreshed + expirationAge;
			}
			return result;
		}

		@Override
		@SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
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
								"BackgroundCache(" + name + ").CacheEntry(" + key + ").run(): Unexpected exception in background cache refresh, dropped from cache",
								t
							);
						}
					}
				}
			}
		}
	}

	private final String name;
	private final Class<? extends Ex> exceptionClass;
	final long refreshInterval;
	private final long expirationAge;
	@SuppressWarnings("NonConstantLogger")
	final Logger logger;

	/**
	 * Timer used for background refreshing and cleaning.
	 * This uses daemon threads.
	 *
	 * TODO: Timer has a very old bug where it does not handle system time resets very well.
	 *       Consider alternative.
	 */
	final Timer timer;

	final ConcurrentMap<K, CacheEntry> map = new ConcurrentHashMap<>();

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
		Class<? extends Ex> exceptionClass,
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
		Class<? extends Ex> exceptionClass,
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
	 * @see  #put(java.lang.Object, com.aoapps.hodgepodge.cache.BackgroundCache.Refresher)
	 */
	public Result<V, Ex> get(
		K key,
		Refresher<? super K, ? extends V, ? extends Ex> refresher
	) {
		Result<V, Ex> result = get(key);
		if(result == null) result = put(key, refresher);
		return result;
	}

	/**
	 * Gets a cached result for the given key, null if not cached.
	 * Extends the expiration of the cache entry.
	 */
	public Result<V, Ex> get(K key) {
		CacheEntry entry = map.get(key);
		if(entry == null) {
			return null;
		} else {
			return entry.getResult();
		}
	}

	Result<V, Ex> runRefresher(
		Refresher<? super K, ? extends V, ? extends Ex> refresher,
		K key
	) throws IllegalStateException {
		try {
			return new Result<>(refresher.call(key));
		} catch(Error | RuntimeException e) {
			throw e;
		} catch(Throwable t) {
			if(exceptionClass.isInstance(t)) {
				return new Result<>(exceptionClass.cast(t));
			} else {
				throw new AssertionError("Unexpected exception type", t);
			}
		}
	}

	/**
	 * Runs the refresher immediately to obtain the result, then
	 * places an entry into the cache, replacing any existing entry under this key.
	 *
	 * @return  The result obtained from this refresher
	 */
	public Result<V, Ex> put(
		K key,
		Refresher<? super K, ? extends V, ? extends Ex> refresher
	) {
		Result<V, Ex> result = runRefresher(refresher, key);
		put(key, refresher, result);
		return result;
	}

	/**
	 * Places a result into the cache, replacing any existing entry under this key.
	 */
	public void put(
		K key,
		Refresher<? super K, ? extends V, ? extends Ex> refresher,
		V value
	) {
		put(key, refresher, new Result<>(value));
	}

	/**
	 * Places a result into the cache, replacing any existing entry under this key.
	 */
	public void put(
		K key,
		Refresher<? super K, ? extends V, ? extends Ex> refresher,
		Ex exception
	) {
		put(key, refresher, new Result<>(exception));
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
		Refresher<? super K, ? extends V, ? extends Ex> refresher,
		Result<V, Ex> result
	) {
		CacheEntry entry = new CacheEntry(key, refresher, result);
		map.put(key, entry);
		timer.schedule(entry, new Date(System.currentTimeMillis() + refreshInterval), refreshInterval);
	}

	/**
	 * Gets the current size of the cache.
	 */
	public int size() {
		return map.size();
	}
}
