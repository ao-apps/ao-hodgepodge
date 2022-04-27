/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2017, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.io;

import com.aoapps.lang.io.NoClose;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an InputStream to count the number of bytes transferred.
 * Does not count bytes skipped.
 * The counter is not synchronized.  Any necessary synchronization
 * should be externally applied.
 *
 * @author  AO Industries, Inc.
 */
public class ByteCountInputStream extends FilterInputStream implements NoClose {

  private long count;

  public ByteCountInputStream(InputStream in) {
    super(in);
  }

  @Override
  public boolean isNoClose() {
    return (in instanceof NoClose) && ((NoClose) in).isNoClose();
  }

  @Override
  public int read() throws IOException {
    int b = in.read();
    count++;
    return b;
  }

  @Override
  public int read(byte[] b) throws IOException {
    int bytes = in.read(b);
    if (bytes > 0) {
      count += bytes;
    }
    return bytes;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int bytes = in.read(b, off, len);
    if (bytes > 0) {
      count += bytes;
    }
    return bytes;
  }

  public long getCount() {
    return count;
  }
}
