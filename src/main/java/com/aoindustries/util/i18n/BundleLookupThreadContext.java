/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015, 2016  AO Industries, Inc.
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

import java.util.IdentityHashMap;

/**
 * Each thread has a markup context associated with it.  When set, bundle lookups
 * will be recorded with any markup-context appropriate prefixes and suffixes added.
 * This allows the use of a normal API while providing a mechanism for in-context
 * translation interfaces to better integrate with the underlying resource bundles.
 * <p>
 * Bundle lookups are not guaranteed to be recorded, such as when in-context translation
 * is disabled (production mode).
 * </p>
 */
final public class BundleLookupThreadContext {

	private static final ThreadLocal<BundleLookupThreadContext> threadContext = new InheritableThreadLocal<>();

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

	private IdentityHashMap<String,BundleLookupMarkup> lookupResults;

	private BundleLookupThreadContext() {
	}

	/**
	 * @throws IllegalStateException   if the string has already been added to this context (as matched by identity)
	 */
	void addLookupMarkup(String lookupResult, BundleLookupMarkup lookupMarkup) throws IllegalStateException {
		if(lookupResults==null) lookupResults = new IdentityHashMap<>();
		if(lookupResults.put(lookupResult, lookupMarkup)!=null) {
			throw new IllegalStateException(
				ApplicationResources.accessor.getMessage("BundleLookupThreadContext.addLookupMarkup.stringAlreadyAdded")
			);
		}
	}

	/**
	 * Removes all lookups stored in this context.
	 */
	public void reset() {
		if(lookupResults!=null) lookupResults.clear();
	}

	/**
	 * Gets the lookup markup for the given String or <code>null</code> if not found.
	 * <p>
	 * The string is looked-up by identity only: .equals() is not called.
	 * This is to give a more precise match to lookups.
	 * </p>
	 */
	public BundleLookupMarkup getLookupMarkup(String result) {
		return lookupResults==null ? null : lookupResults.get(result);
	}
}
