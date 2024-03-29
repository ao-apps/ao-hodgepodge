/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2019, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.md5;

import com.aoapps.lang.util.BufferManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utilities that help when working with MD5 hashes.
 *
 * @author  AO Industries, Inc.
 */
public final class MD5Utils {

  /** Make no instances. */
  private MD5Utils() {
    throw new AssertionError();
  }

  /**
   * Gets the MD5 hashcode of a file.
   */
  public static byte[] md5(String filename) throws IOException {
    return md5(new File(filename));
  }

  /**
   * Gets the MD5 hashcode of a file.
   */
  public static byte[] md5(File file) throws IOException {
    try (InputStream in = new FileInputStream(file)) {
      return md5(in);
    }
  }

  /**
   * Gets the MD5 hashcode of an input stream.
   */
  public static byte[] md5(InputStream in) throws IOException {
    MD5InputStream md5in = new MD5InputStream(in);
    byte[] trashBuffer = BufferManager.getBytes();
    try {
      while (md5in.read(trashBuffer, 0, BufferManager.BUFFER_SIZE) != -1) {
        // Intentional empty block
      }
    } finally {
      BufferManager.release(trashBuffer, false);
    }
    return md5in.hash();
  }
}
