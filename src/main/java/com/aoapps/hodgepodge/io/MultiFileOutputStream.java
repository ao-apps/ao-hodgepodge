/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2019, 2021, 2022  AO Industries, Inc.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>MultiFileOutputStream</code> writes to multiple <code>File</code>s as
 * if they were one contiguous file.
 *
 * @author  AO Industries, Inc.
 */
public class MultiFileOutputStream extends OutputStream {

  public static final long DEFAULT_FILE_SIZE = 1024L * 1024 * 1024;

  private final File parent;
  private final String prefix;
  private final String suffix;
  private final long fileSize;

  private final List<File> files = new ArrayList<>();
  private FileOutputStream out;
  private long bytesOut;

  public MultiFileOutputStream(File parent, String prefix, String suffix) {
    this(parent, prefix, suffix, DEFAULT_FILE_SIZE);
  }

  public MultiFileOutputStream(File parent, String prefix, String suffix, long fileSize) {
    this.parent = parent;
    this.prefix = prefix;
    this.suffix = suffix;
    this.fileSize = fileSize;
  }

  @Override
  public synchronized void close() throws IOException {
    FileOutputStream tempOut = out;
    if (tempOut != null) {
      out = null;
      tempOut.flush();
      tempOut.close();
    }
  }

  @Override
  public synchronized void flush() throws IOException {
    if (out != null) {
      out.flush();
    }
  }

  @Override
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  @Override
  public synchronized void write(byte[] b, int off, int len) throws IOException {
    while (off < len) {
      if (out == null) {
        makeNewFile();
      }
      int blockLen = len;
      long newBytesOut = bytesOut + blockLen;
      if (newBytesOut > fileSize) {
        blockLen = (int) (fileSize - newBytesOut);
      }
      out.write(b, off, blockLen);
      off += blockLen;
      len -= blockLen;
      bytesOut += blockLen;
      if (bytesOut >= fileSize) {
        try (FileOutputStream tempOut = out) {
          out = null;
          tempOut.flush();
        }
      }
    }
  }

  @Override
  public synchronized void write(int b) throws IOException {
    out.write(b);
    bytesOut += 1;
    if (bytesOut >= fileSize) {
      try (FileOutputStream tempOut = out) {
        out = null;
        tempOut.flush();
      }
    }
  }

  /**
   * All accesses are already synchronized.
   */
  private void makeNewFile() throws IOException {
    String filename = prefix + (files.size() + 1) + suffix;
    File file = new File(parent, filename);
    out = new FileOutputStream(file);
    bytesOut = 0;
    files.add(file);
  }

  public File[] getFiles() {
    return files.toArray(new File[files.size()]);
  }
}
