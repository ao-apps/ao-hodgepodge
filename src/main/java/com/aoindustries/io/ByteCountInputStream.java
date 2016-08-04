/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013  AO Industries, Inc.
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an InputStream to count the number of bytes transferred.
 * The counter is not synchronized.  Any necessary synchronization
 * should be externally applied.
 *
 * @author  AO Industries, Inc.
 */
public class ByteCountInputStream extends FilterInputStream {

    private long count = 0;

    public ByteCountInputStream(InputStream in) {
        super(in);
    }

	@Override
    public int read() throws IOException {
	int b = in.read();
        count++;
        return b;
    }

	@Override
    public int read(byte b[]) throws IOException {
	int bytes = in.read(b);
        count+=bytes;
        return bytes;
    }

	@Override
    public int read(byte b[], int off, int len) throws IOException {
	int bytes = in.read(b, off, len);
        count+=bytes;
        return bytes;
    }
    
    public long getCount() {
        return count;
    }
}
