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
package com.aoindustries.util.i18n;

import com.aoindustries.util.concurrent.ThreadLocalsRunnable;

/**
 * Invokes the provided runnable in the same internationalization context.
 *
 * @see  ThreadLocale
 * @see  BundleLookupThreadContext
 */
public class I18nThreadLocalRunnable extends ThreadLocalsRunnable {

	/**
	 * The set of thread locals that are copied to maintain internationalization context.
	 */
	static final ThreadLocal[] i18nThreadLocals = {
		ThreadLocale.locale,
		BundleLookupThreadContext.threadContext,
		EditableResourceBundle.currentThreadSettings
	};

	public I18nThreadLocalRunnable(Runnable task) {
		super(task, i18nThreadLocals);
	}
} 
