/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013  AO Industries, Inc.
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
package com.aoindustries.nio.charset;

import java.nio.charset.Charset;

/**
 * Defines some constants for handling standard character sets.
 *
 * @author  AO Industries, Inc.
 */
public class Charsets {

    public static final Charset
        US_ASCII   = Charset.forName("US-ASCII"),
        ISO_8859_1 = Charset.forName("ISO-8859-1"),
        UTF_8      = Charset.forName("UTF-8"),
        UTF_16BE   = Charset.forName("UTF-16BE"),
        UTF_16LE   = Charset.forName("UTF-16LE"),
        UTF_16     = Charset.forName("UTF-16")
    ;

    /**
     * Make no instances.
     */
    private Charsets() {
    }
}