/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2017, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.tempfiles.TempFileContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Holds a reference to a temp file.  Automatically deletes the file on JVM shutdown
 * or when <code>delete()</code> is explicitly called.
 *
 * @see  TempFileList  for a way to confine instance lifetime to a narrower scope than automatic garbage collection
 *
 * @author  AO Industries, Inc.
 *
 * @deprecated  Please use {@link TempFileContext}
 *              as {@link File#deleteOnExit()} is prone to memory leaks in long-running applications.
 */
@Deprecated
public class TempFile {

  private volatile File tempFile;
  private final String path;

  public TempFile(String prefix) throws IOException {
    this(prefix, null, null);
  }

  public TempFile(String prefix, String suffix) throws IOException {
    this(prefix, suffix, null);
  }

  /**
   * Creates the temp directory if missing.
   */
  public TempFile(String prefix, String suffix, File directory) throws IOException {
    File checkDir = directory;
    if (checkDir == null) {
      checkDir = new File(System.getProperty("java.io.tmpdir"));
    }
    if (!checkDir.exists()) {
      Files.createDirectories(checkDir.toPath());
    }
    tempFile = File.createTempFile(prefix, suffix, directory);
    tempFile.deleteOnExit();
    path = tempFile.getPath();
  }

  @Override
  public String toString() {
    return path;
  }

  /**
   * Deletes the underlying temp file immediately.
   * Subsequent calls will not delete the temp file, even if another file has the same path.
   * If already deleted, has no effect.
   */
  public void delete() throws IOException {
    File f = tempFile;
    if (f != null) {
      Files.delete(f.toPath());
      tempFile = null;
    }
  }

  /**
   * Gets the temp file.
   *
   * @exception  IllegalStateException  if already deleted
   */
  public File getFile() throws IllegalStateException {
    File f = tempFile;
    if (f == null) {
      throw new IllegalStateException();
    }
    return f;
  }
}
