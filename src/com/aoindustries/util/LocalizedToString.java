/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
package com.aoindustries.util;

import java.util.Locale;

/**
 * Any object that provides a <code>Locale</code>-specific <code>toString</code>
 * method may indicate so by implementing this interface.  When providing the
 * localized <code>toString</code>, please override <code>toString()</code> as
 * follows:
 * <pre>
 * public String toString() {
 *     return toString(Locale.getDefault());
 * }
 * </pre>
 *
 * @author  AO Industries, Inc.
 */
public interface LocalizedToString {

    String toString(Locale userLocale);
}
