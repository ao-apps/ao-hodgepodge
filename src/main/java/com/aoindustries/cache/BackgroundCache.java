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

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * A cache that is refreshed in the background, implementing only get, put, getOrPut, and size.
 * There is no remove, background cleaning is performed on the underlying map.
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
	 * Temporary debugging output where we want zero runtime costs.
	 */
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_STOP = DEBUG;
	private static final boolean DEBUG_EXTEND = DEBUG;
	private static final boolean DEBUG_RUN_REFRESHER = false;
	private static final boolean DEBUG_PUT = DEBUG;
	private static final boolean DEBUG_TIMER_TASK = false;
	private static final boolean DEBUG_TIMER_TASK_DROPPING = DEBUG;
	private static final boolean DEBUG_TIMER_TASK_REPLACED = DEBUG;

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
	static class CacheEntry<K,V,E extends Exception> {

		final Refresher<? super K, ? extends V, ? extends E> refresher;

		/**
		 * The last obtained result.
		 */
		Result<V,E> result;

		/**
		 * The time the entry was last refreshed.
		 */
		long refreshed;

		/**
		 * Has this entry been accessed since the last refresh.
		 * Used to know when to extend expiration.
		 */
		boolean accessedSinceRefresh;

		/**
		 * The time this entry will expire.
		 * When an entry is accessed, its expiration time is updated to be
		 * based on its refreshed time.
		 */
		long expiration;


		/**
		 * A cached result.
		 */
		CacheEntry(
			Refresher<? super K, ? extends V, ? extends E> refresher,
			Result<V,E> result,
			long refreshed,
			long expiration
		) {
			this.refresher = refresher;
			this.result = result;
			this.refreshed = refreshed;
			this.accessedSinceRefresh = true; // Do not refresh immediately after creation
			this.expiration = expiration;
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

	final ReadWriteLock lock = new ReentrantReadWriteLock();

	// TODO: ConcurrentMap + per-entry readwritelocks?
	final Map<K,CacheEntry<K,V,E>> map = new HashMap<K,CacheEntry<K,V,E>>();

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
		if(DEBUG_STOP) System.err.println("BackgroundCache(" + name + ").stop()");
		timer.cancel();
		Lock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			map.clear();
		} finally {
			writeLock.unlock();
		}
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
		Refresher<? super K, ? extends V, ? extends E> refresher
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
		CacheEntry<K,V,E> entry;
		Result<V,E> result;
		boolean extendExpiration;
		{
			Lock readLock = lock.readLock();
			readLock.lock();
			try {
				entry = map.get(key);
				if(entry != null) {
					result = entry.result;
					extendExpiration = !entry.accessedSinceRefresh;
				} else {
					result = null;
					extendExpiration = false;
				}
			} finally {
				readLock.unlock();
			}
		}
		if(extendExpiration) {
			assert entry != null;
			Lock writeLock = lock.writeLock();
			writeLock.lock();
			try {
				// Make sure another thread didn't already extend it
				if(!entry.accessedSinceRefresh) {
					if(DEBUG_EXTEND) System.err.println("BackgroundCache(" + name + ").get(" + key + "): Extending expiration");
					entry.accessedSinceRefresh = true;
					entry.expiration = entry.refreshed + expirationAge;
				}
			} finally {
				writeLock.unlock();
			}
		}
		return result;
	}

	Result<V,E> runRefresher(
		Refresher<? super K, ? extends V, ? extends E> refresher,
		K key
	) throws IllegalStateException {
		if(DEBUG_RUN_REFRESHER) System.err.println("BackgroundCache(" + name + ").runRefresher(" + refresher + ", " + key + ")");
		try {
			V value = refresher.call(key);
			if(DEBUG_RUN_REFRESHER) System.err.println("BackgroundCache(" + name + ").runRefresher(" + refresher + ", " + key + "): value = " + value);
			return new Result<V,E>(value);
		} catch(Exception e) {
			if(DEBUG_RUN_REFRESHER) System.err.println("BackgroundCache(" + name + ").runRefresher(" + refresher + ", " + key + "): e = " + e);
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
		Refresher<? super K, ? extends V, ? extends E> refresher
	) {
		if(DEBUG_PUT) System.err.println("BackgroundCache(" + name + ").put(" + key + ", " + refresher + ")");
		Result<V,E> result = runRefresher(refresher, key);
		put(key, refresher, result);
		return result;
	}

	/**
	 * Places a result into the cache, replacing any existing entry under this key.
	 */
	public void put(
		K key,
		Refresher<? super K, ? extends V, ? extends E> refresher,
		V value
	) {
		if(DEBUG_PUT) System.err.println("BackgroundCache(" + name + ").put(" + key + ", " + refresher + ", " + value + ")");
		put(key, refresher, new Result<V,E>(value));
	}

	/**
	 * Places a result into the cache, replacing any existing entry under this key.
	 */
	public void put(
		K key,
		Refresher<? super K, ? extends V, ? extends E> refresher,
		E exception
	) {
		if(DEBUG_PUT) System.err.println("BackgroundCache(" + name + ").put(" + key + ", " + refresher + ", " + exception + ")");
		put(key, refresher, new Result<V,E>(exception));
	}

	/**
	 * Puts a new entry, replacing any existing.  Schedules refresh on the timer.
	 */
	private void put(
		final K key,
		final Refresher<? super K, ? extends V, ? extends E> refresher,
		Result<V,E> result
	) {
		long currentTime = System.currentTimeMillis();
		final CacheEntry<K,V,E> entry = new CacheEntry<K,V,E>(
			refresher,
			result,
			currentTime,
			currentTime + expirationAge
		);
		Lock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			map.put(key, entry);
		} finally {
			writeLock.unlock();
		}
		timer.schedule(
			new TimerTask() {
				@Override
				public void run() {
					if(DEBUG_TIMER_TASK) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run()");
					Thread currentThread = Thread.currentThread();
					if(currentThread.getPriority() != TIMER_THREAD_PRIORITY) {
						currentThread.setPriority(TIMER_THREAD_PRIORITY);
						if(DEBUG_TIMER_TASK) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Set thread priority");
					}
					long currentTime;
					boolean dropFromCache;
					Lock readLock = lock.readLock();
					readLock.lock();
					try {
						if(entry != map.get(key)) {
							// This has been replaced, nothing to do
							if(DEBUG_TIMER_TASK_REPLACED) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Replaced");
							// Cancel this timer task
							cancel();
							return;
						}
						currentTime = System.currentTimeMillis();
						dropFromCache =
							// Expired expired
							currentTime >= entry.expiration
							// System time set to the past
							|| currentTime < entry.refreshed
						;
						if(DEBUG_TIMER_TASK_DROPPING && dropFromCache) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Dropping due to time");
					} finally {
						readLock.unlock();
					}
					Result<V,E> newResult;
					if(dropFromCache) {
						newResult = null;
					} else {
						try {
							newResult = runRefresher(refresher, key);
						} catch(Throwable t) {
							// Drop from cache when any unexpected exception happens
							if(DEBUG_TIMER_TASK_DROPPING) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Dropping due to unexpected throwable: " + t);
							dropFromCache = true;
							newResult = null;
							// Log unexpected exception
							logger.log(Level.WARNING, "Unexpected exception in background cache refresh, dropping from cache", t);
						}
					}
					Lock writeLock = lock.writeLock();
					writeLock.lock();
					try {
						if(dropFromCache) {
							// Make sure this has not already been replaced
							CacheEntry<K,V,E> removed = map.remove(key);
							if(removed != entry) {
								// Whoops, removed what had replaced this key, put it back!
								// (this should happen rarely so not checking first)
								if(DEBUG_TIMER_TASK_REPLACED) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Found replacement during drop, putting it back: " + removed);
								map.put(key, removed);
							} else {
								if(DEBUG_TIMER_TASK_DROPPING) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Dropped, size = " + map.size());
							}
							// Cancel this timer task
							cancel();
						} else {
							// Update entry
							assert newResult != null;
							if(DEBUG_TIMER_TASK) System.err.println("BackgroundCache(" + name + ").TimerTask(" + key + ").run(): Updating entry with new result");
							entry.result = newResult;
							entry.refreshed = currentTime;
							entry.accessedSinceRefresh = false;
						}
					} finally {
						writeLock.unlock();
					}
				}
			},
			refreshInterval,
			refreshInterval
		);
	}

	/**
	 * Gets the current size of the cache.
	 */
	public int size() {
		Lock readLock = lock.readLock();
		readLock.lock();
		try {
			return map.size();
		} finally {
			readLock.unlock();
		}
	}
}
