/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
package com.aoindustries.util.i18n;

import com.aoindustries.i18n.Resources;
import com.aoindustries.lang.LocalizedIllegalStateException;
import java.util.IdentityHashMap;
import java.util.Locale;

/**
 * <p>
 * Each thread has a markup context associated with it.  When set, bundle lookups
 * will be recorded with any markup-context appropriate prefixes and suffixes added.
 * This allows the use of a normal API while providing a mechanism for in-context
 * translation interfaces to better integrate with the underlying resource bundles.
 * </p>
 * <p>
 * Under concurrent programming, one context can end up being accessed concurrently
 * by multiple threads, thus BundleLookupThreadContext is a thread-safe implementation.
 * </p>
 * <p>
 * Bundle lookups are not guaranteed to be recorded, such as when in-context translation
 * is disabled (production mode).
 * </p>
 */
final public class BundleLookupThreadContext {

	private static final Resources RESOURCES = Resources.getResources(BundleLookupThreadContext.class);

	static final ThreadLocal<BundleLookupThreadContext> threadContext = new ThreadLocal<>();

	/**
	 * Gets the current context or <code>null</code> if none set and none created.
	 */
	public static BundleLookupThreadContext getThreadContext(boolean createIfMissing) {
		BundleLookupThreadContext context = threadContext.get();
		if(createIfMissing && context==null) {
			context = new BundleLookupThreadContext();
			threadContext.set(context);
		}
		return context;
	}

	/**
	 * Removes any current context.
	 */
	public static void removeThreadContext() {
		threadContext.set(null);
	}

	/**
	 * Register a listener on {@link Resources}
	 */
	static {
		Resources.addListener(
			(Resources _resources, Locale locale, String key, Object[] args, String resource, String result) -> {
				// Copy any lookup markup to the newly generated string
				BundleLookupThreadContext _threadContext = BundleLookupThreadContext.getThreadContext(false);
				if(_threadContext != null) {
					BundleLookupMarkup lookupMarkup = _threadContext.getLookupMarkup(resource);
					_threadContext.addLookupMarkup(
						result, // This string is already a new instance and therefore is already unique by identity
						lookupMarkup
					);
				}
			}
		);
	}

	private final IdentityHashMap<String,BundleLookupMarkup> lookupResults = new IdentityHashMap<>();;

	private BundleLookupThreadContext() {
	}

	/**
	 * @throws IllegalStateException   if the string has already been added to this context (as matched by identity)
	 */
	void addLookupMarkup(String lookupResult, BundleLookupMarkup lookupMarkup) throws IllegalStateException {
		synchronized(lookupResults) {
			if(lookupResults.put(lookupResult, lookupMarkup) != null) {
				throw new LocalizedIllegalStateException(RESOURCES, "addLookupMarkup.stringAlreadyAdded");
			}
		}
	}

	/**
	 * Removes all lookups stored in this context.
	 */
	public void reset() {
		synchronized(lookupResults) {
			lookupResults.clear();
		}
	}

	/**
	 * Gets the lookup markup for the given String or <code>null</code> if not found.
	 * <p>
	 * The string is looked-up by identity only: .equals() is not called.
	 * This is to give a more precise match to lookups.
	 * </p>
	 */
	public BundleLookupMarkup getLookupMarkup(String result) {
		synchronized(lookupResults) {
			return lookupResults.get(result);
		}
	}
}
