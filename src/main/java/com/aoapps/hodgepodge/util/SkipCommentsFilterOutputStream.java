/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2019, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.util;

import com.aoapps.lang.io.NoClose;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;

/**
 * Skips any lines that begin with #.  This is designed for the simple task
 * of filtering out the default comment written by
 * {@link Properties#store(java.io.OutputStream, java.lang.String)}.
 * <p>
 * This class is optimized for writing {@link Properties} files and assumes
 * ISO-8859-1 encoding.
 * </p>
 * <p>
 * Java 1.9: Writer properties files via {@link Writer} in UTF-8 format
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class SkipCommentsFilterOutputStream extends FilterOutputStream implements NoClose {

  public SkipCommentsFilterOutputStream(OutputStream out) {
    super(out);
  }

  @Override
  public boolean isNoClose() {
    return (out instanceof NoClose) && ((NoClose) out).isNoClose();
  }

  private boolean lastCharNewline = true;
  private boolean isCommentLine;

  @Override
  public void write(int ch) throws IOException {
    if (lastCharNewline) {
      isCommentLine = ch == '#';
    }
    lastCharNewline = ch == '\n';
    if (!isCommentLine) {
      out.write(ch);
    }
  }
}
