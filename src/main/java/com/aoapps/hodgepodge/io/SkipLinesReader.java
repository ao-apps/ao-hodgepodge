/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2016, 2021, 2022  AO Industries, Inc.
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Skips the specified number of lines at the beginning of a reader.
 *
 * @author  AO Industries, Inc.
 */
public class SkipLinesReader extends BufferedReader implements NoClose {

  private final Reader in;

  public SkipLinesReader(Reader in, int skipLines) throws IOException {
    super(in);
    for (int i = 0; i < skipLines; i++) {
      String line = readLine();
      if (line == null) {
        break;
      }
    }
    this.in = in;
  }

  @Override
  public boolean isNoClose() {
    return (in instanceof NoClose) && ((NoClose) in).isNoClose();
  }
}
