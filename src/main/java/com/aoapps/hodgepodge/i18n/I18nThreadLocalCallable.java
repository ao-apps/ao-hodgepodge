/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2016, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.i18n;

import com.aoapps.lang.concurrent.ThreadLocalsCallable;
import com.aoapps.lang.i18n.ThreadLocale;
import java.util.concurrent.Callable;

/**
 * Invokes the provided callable in the same internationalization context.
 *
 * @see  ThreadLocale
 * @see  BundleLookupThreadContext
 */
public class I18nThreadLocalCallable<T> extends ThreadLocalsCallable<T> {

	public I18nThreadLocalCallable(Callable<T> task) {
		super(task, I18nThreadLocalRunnable.i18nThreadLocals);
	}
}