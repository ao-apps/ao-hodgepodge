/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.io;

import java.io.FilterOutputStream;
import java.io.OutputStream;

/**
 * Wraps an OutputStream to protect it from close calls.  This is useful for at least
 * GZIPOutputStream where the native resources of the GZIPOutputStream need to be released
 * using the close call while the underlying stream is left intact.
 *
 * @author  AO Industries, Inc.
 */
public class DontCloseOutputStream extends FilterOutputStream {

    public DontCloseOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Does nothing on close to protect the wrapped OutputStream.
     */
    public void close() {
    }
}
