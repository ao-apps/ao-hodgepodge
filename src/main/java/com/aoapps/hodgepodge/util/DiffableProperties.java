/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2019, 2020, 2021, 2022, 2023, 2024  AO Industries, Inc.
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

import com.aoapps.lang.SysExits;
import com.aoapps.lang.io.IoUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Formats {@link Properties} files in a format to maximize the ability for
 * diff tools (like <code>git diff</code>) to represent changes.  This is
 * accomplished with the following steps:
 * <ol>
 * <li>Always use UNIX newlines ({@code '\n'}) on all platforms.</li>
 * <li>Change Unicode escapes <code>\\uHHHH</code> to lower-case <code>\\uhhhh</code>
 *     for compatibility with <code>native2ascii</code> and files saved by
 *     NetBeans.</li>
 * <li>Make all multi-line values span multiple lines.  Add an escaped newline
 *     after any "\n" that is not at the end of the line already.  The next line
 *     will be prefixed with a "\" if it starts with whitespace.</li>
 * </ol>
 *
 * <p>For maximum diffability, this should be used along with {@link com.aoapps.collections.SortedProperties}.</p>
 *
 * @see  Properties
 * @see  com.aoapps.collections.SortedProperties
 */
public final class DiffableProperties {

  /** Make no instances. */
  private DiffableProperties() {
    throw new AssertionError();
  }

  private static boolean isHex(char ch) {
    return
        (ch >= '0' && ch <= '9')
            || (ch >= 'a' && ch <= 'f')
            || (ch >= 'A' && ch <= 'F');
  }

  private static char lowerHex(char ch) {
    if (ch >= 'A' && ch <= 'F') {
      return (char) (ch + ('a' - 'A'));
    } else {
      return ch;
    }
  }

  public static class FormatResult {
    private final String before;
    private final String after;

    private FormatResult(String before, String after) {
      this.before = before;
      this.after = after;
    }

    public String getBefore() {
      return before;
    }

    public String getAfter() {
      return after;
    }
  }

  /**
   * Formats a properties file provided as a String.  The file may contain
   * Unicode values as supported by Java 9+.
   *
   * <p>The file encoding is not performed by this method.
   * For Java &lt;= 8, this should be stored in the ISO8859-1 encoding.
   * For Java &gt;= 9, this may be stored in the UTF-8 encoding.</p>
   */
  public static String formatProperties(String properties) {
    int len = properties.length();
    StringBuilder sb = new StringBuilder(len * 6 / 5); // Space for 20% increase before growing buffer
    boolean isLeadingWhitespace = true;
    boolean isComment = false;
    int pos = 0;
    while (pos < len) {
      char ch1 = properties.charAt(pos++);
      if (ch1 == '\r') {
        // Skip \r
      } else if (!isComment && ch1 == '\\' && pos < len) {
        isLeadingWhitespace = false;
        // Is escape
        char ch2 = properties.charAt(pos++);
        char ch3;
        char ch4;
        char ch5;
        char ch6;
        if (
            ch2 == 'u'
                && (pos + 4) <= len
                && isHex(ch3 = properties.charAt(pos))
                && isHex(ch4 = properties.charAt(pos + 1))
                && isHex(ch5 = properties.charAt(pos + 2))
                && isHex(ch6 = properties.charAt(pos + 3))
        ) {
          sb
              .append(ch1)
              .append(ch2)
              .append(lowerHex(ch3))
              .append(lowerHex(ch4))
              .append(lowerHex(ch5))
              .append(lowerHex(ch6));
          pos += 4;
        } else if (
            ch2 == 'n'
                // If already at end-of-line, do nothing
                && !(
                pos >= len
                    || (ch3 = properties.charAt(pos)) == '\r' || ch3 == '\n'
            )
                // If already followed by \(EOL) end-of-line, do nothing
                && !(
                ch3 == '\\'
                    && (
                    (pos + 1) >= len
                        || (ch4 = properties.charAt(pos + 1)) == '\r' || ch4 == '\n'
                )
            )
        ) {
          // Add \(EOL) escape, and possible \ if the next value is ' ', '\t', '\f'
          sb.append(ch1).append(ch2).append("\\\n");
          if (ch3 == ' ' || ch3 == '\t' || ch3 == '\f') {
            sb.append('\\');
          }
        } else {
          // Nothing to change
          sb.append(ch1);
          if (ch2 == '\r') {
            // Skip \r
          } else {
            sb.append(ch2);
            if (ch2 == '\n') {
              isLeadingWhitespace = true;
              isComment = false;
            }
          }
        }
      } else if (ch1 == '#') {
        // Possible start of comment
        sb.append(ch1);
        if (isLeadingWhitespace) {
          isLeadingWhitespace = false;
          isComment = true;
        }
      } else {
        // Nothing to change
        sb.append(ch1);
        if (ch1 == '\n') {
          isLeadingWhitespace = true;
          isComment = false;
        }
      }
    }
    return sb.toString();
  }

  /**
   * Rewrites the given file, if modified.  Reads and writes the files in ISO8859-1 encoding.
   *
   * @return  The result, with {@link FormatResult#getAfter()} {@code null}
   *          when the file unmodified
   */
  // Java 9: Support UTF-8 properties
  public static FormatResult formatProperties(File file) throws IOException {
    String before;
    try (Reader in = new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1)) {
      before = IoUtils.readFully(in);
    }
    String after = formatProperties(before);
    if (!after.equals(before)) {
      // Overwrites in-place to avoid altering file permissions or ownership,
      // at the risk of leaving an empty file if the disk is full or write fails.
      // This is assumed to be used in a version control environment so this risk is acceptable.
      // Java 8: Write in a way that is both atomic and preserves permissions in a cross-platform manner
      try (Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1)) {
        out.write(after);
      }
      return new FormatResult(before, after);
    } else {
      // Unchanged
      return new FormatResult(before, null);
    }
  }

  /**
   * Rewrites the given files, if modified.  Reads and writes the files in ISO8859-1 encoding.
   */
  // Java 9: Support UTF-8 properties
  @SuppressWarnings({"UseOfSystemOutOrSystemErr", "TooBroadCatch"})
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage: " + DiffableProperties.class.getName() + " file.properties [file.properties [...]]");
      System.exit(SysExits.EX_USAGE);
    } else {
      for (String filename : args) {
        try {
          FormatResult result = formatProperties(new File(filename));
          String after = result.getAfter();
          if (after != null) {
            // Modified, write output
            System.out.println(filename + ": " + result.getBefore().length() + " → " + after.length());
          }
        } catch (ThreadDeath td) {
          throw td;
        } catch (IOException e) {
          System.err.println(filename + ": " + e.getMessage());
          //e.printStackTrace(System.err);
          System.exit(SysExits.getSysExit(e));
        } catch (Throwable t) {
          System.err.println(filename + ": " + t.getMessage());
          t.printStackTrace(System.err);
          System.exit(SysExits.getSysExit(t));
        }
      }
    }
  }
}
