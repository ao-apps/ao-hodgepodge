/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.io.stream;

import com.aoapps.lang.io.NoClose;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Adds compressed data transfer to DataOutputStream.  This class is not
 * thread safe.
 *
 * @see StreamableInput
 *
 * @author  AO Industries, Inc.
 */
public class StreamableOutput extends DataOutputStream implements NoClose {

  public StreamableOutput(OutputStream out) {
    super(out);
  }

  @Override
  public boolean isNoClose() {
    return (out instanceof NoClose) && ((NoClose) out).isNoClose();
  }

  /**
   * The minimum value of int that can be written via {@link #writeCompressedInt(int)}
   * or {@link #writeCompressedInt(int, java.io.OutputStream)}.
   */
  public static final int MIN_COMPRESSED_INT_VALUE = -0x40000000;

  /**
   * The maximum value of int that can be written via {@link #writeCompressedInt(int)}
   * or {@link #writeCompressedInt(int, java.io.OutputStream)}.
   */
  public static final int MAX_COMPRESSED_INT_VALUE = 0x3fffffff;

  /**
   * Verifies a value is in the acceptable range for a compressed int.
   *
   * @see  #writeCompressedInt(int)
   * @see  #MIN_COMPRESSED_INT_VALUE
   * @see  #MAX_COMPRESSED_INT_VALUE
   */
  public static void checkCompressedInt(int i) throws IOException {
    if (i < MIN_COMPRESSED_INT_VALUE || i > MAX_COMPRESSED_INT_VALUE) {
      throw new IOException("Value out of range (" + MIN_COMPRESSED_INT_VALUE + " to " + MAX_COMPRESSED_INT_VALUE + "): " + i);
    }
  }

  /**
   * @see StreamableInput#readCompressedInt()
   */
  public static void writeCompressedInt(int i, OutputStream out) throws IOException {
    int t;
    if (
        (t = i & 0xfffffff0) == 0
            || t == 0xfffffff0
    ) {
      // 5 bit
      out.write(i & 0x1f);
    } else if (
        (t = i & 0xfffff000) == 0
            || t == 0xfffff000
    ) {
      // 13 bit
      out.write(0x20 | ((i & 0x1f00) >>> 8));
      out.write(i & 0xff);
    } else if (
        (t = i & 0xffe00000) == 0
            || t == 0xffe00000
    ) {
      // 22 bit
      out.write(0x40 | ((i & 0x3f0000) >>> 16));
      out.write((i & 0xff00) >>> 8);
      out.write(i & 0xff);
    } else if (
        (t = i & 0xc0000000) == 0
            || t == 0xc0000000
    ) {
      // 31 bit
      out.write(0x80 | ((i & 0x7f000000) >>> 24));
      out.write((i & 0xff0000) >>> 16);
      out.write((i & 0xff00) >>> 8);
      out.write(i & 0xff);
    } else {
      checkCompressedInt(i);
      throw new AssertionError("Must have already been out of range");
    }
  }

  public void writeCompressedInt(int i) throws IOException {
    writeCompressedInt(i, out);
  }

  public void writeCompressedUTF(String str) throws IOException {
    writeCompressedUTF(str, 0);
  }

  private String[] lastStrings;
  private int[] lastCommonLengths;

  /**
   * Writes a String to the stream while using prefix compression.
   *
   * <pre>
   * The first byte has these bits:
   *
   * X X X X X X X X
   * | | +-+-+-+-+-+ Slot number (0-63)
   * | +------------ 1 = Suffix UTF follows, 0 = No suffix UTF exists
   * +-------------- 1 = Common length difference follows, 0 = Common length not changed
   *
   * Second, if common length difference is not zero, the common length change follows
   *                 one less for positive differences because 0 is handled in first byte
   *
   * Third, if suffix UTF follows, writeUTF of all the string after common length
   * </pre>
   */
  public void writeCompressedUTF(String str, int slot) throws IOException {
    if (slot < 0 || slot > 0x3f) {
      throw new IOException("Slot out of range (0-63): " + slot);
    }
    if (lastStrings == null) {
      lastStrings = new String[64];
    }
    String last = lastStrings[slot];
    if (last == null) {
      last = "";
    }
    int strLen = str.length();
    int lastLen = last.length();
    int maxCommon = Math.min(strLen, lastLen);
    int common = 0;
    for (; common < maxCommon; common++) {
      if (str.charAt(common) != last.charAt(common)) {
        break;
      }
    }
    if (lastCommonLengths == null) {
      lastCommonLengths = new int[64];
    }
    int commonDifference = common - lastCommonLengths[slot];

    // Write the header byte
    out.write(
        (commonDifference == 0 ? 0 : 0x80)
            | (common == strLen ? 0 : 0x40)
            | slot
    );

    // Write the common difference
    if (commonDifference > 0) {
      writeCompressedInt(commonDifference - 1);
    } else if (commonDifference < 0) {
      writeCompressedInt(commonDifference);
    }

    // Write the suffix
    if (common != strLen) {
      writeUTF(str.substring(common));
    }

    // Get ready for the next call
    lastStrings[slot] = str;
    lastCommonLengths[slot] = common;
  }

  public void writeNullUTF(String str) throws IOException {
    writeBoolean(str != null);
    if (str != null) {
      writeUTF(str);
    }
  }

  /**
   * Writes a string of any length.
   */
  public void writeLongUTF(String str) throws IOException {
    int length = str.length();
    writeCompressedInt(length);
    for (int position = 0; position < length; position += 20480) {
      int blockLength = length - position;
      if (blockLength > 20480) {
        blockLength = 20480;
      }
      String block = str.substring(position, position + blockLength);
      writeUTF(block);
    }
  }

  /**
   * Writes a string of any length, supporting <code>null</code>.
   */
  public void writeNullLongUTF(String str) throws IOException {
    writeBoolean(str != null);
    if (str != null) {
      writeLongUTF(str);
    }
  }

  public void writeNullByte(Byte b) throws IOException {
    writeBoolean(b != null);
    if (b != null) {
      writeByte(b);
    }
  }

  public void writeNullShort(Short s) throws IOException {
    writeBoolean(s != null);
    if (s != null) {
      writeShort(s);
    }
  }

  public void writeNullInteger(Integer i) throws IOException {
    writeBoolean(i != null);
    if (i != null) {
      writeInt(i);
    }
  }

  public void writeNullLong(Long l) throws IOException {
    writeBoolean(l != null);
    if (l != null) {
      writeLong(l);
    }
  }

  /**
   * Writes an {@link Enum}, represented by its {@link Enum#name()}.
   */
  public void writeEnum(Enum<?> e) throws IOException {
    writeUTF(e.name());
  }

  /**
   * Writes an {@link Enum}, represented by its {@link Enum#name()},
   * supporting {@code null}.
   */
  public void writeNullEnum(Enum<?> e) throws IOException {
    writeBoolean(e != null);
    if (e != null) {
      writeUTF(e.name());
    }
  }

  public void writeNullBoolean(Boolean b) throws IOException {
    writeByte(
        b == null ? (byte) -1
            : b ? 1
            : 0
    );
  }
}
