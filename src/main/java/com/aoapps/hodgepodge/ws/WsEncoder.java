/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2016, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.ws;

import com.aoapps.lang.Strings;

/**
 * SOAP web services cannot send invalid whitespace characters.  In order to not
 * have problems and not lose data, these methods will encode and decode the
 * special whitespace characters using the standard \ Java escape sequences.
 * At the cost of perfect interoperability on unexpected characters, this
 * results in binary transparency when both sides use these encode and
 * decode methods.
 *
 * @author  AO Industries, Inc.
 */
public final class WsEncoder {

  /** Make no instances. */
  private WsEncoder() {
    throw new AssertionError();
  }

  private static final char[] hexChars = {
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f'
  };

  /**
   * Encodes string for binary transparency over SOAP.
   */
  public static String encode(String value) {
    if (value == null) {
      return null;
    }

    StringBuilder encoded = null;
    int len = value.length();
    for (int c = 0; c < len; c++) {
      char ch = value.charAt(c);
      if (
          (ch < ' ' && ch != '\n' && ch != '\r')
              || ch == '\\'
      ) {
        if (encoded == null) {
          encoded = new StringBuilder();
          if (c > 0) {
            encoded.append(value, 0, c);
          }
        }
        if (ch == '\\') {
          encoded.append("\\\\");
        } else if (ch == '\b') {
          encoded.append("\\b");
        } else if (ch == '\f') {
          encoded.append("\\f");
        } else if (ch == '\t') {
          encoded.append("\\t");
        } else {
          int ich = ch;
          encoded
              .append("\\u")
              .append(hexChars[(ich >>> 12) & 15])
              .append(hexChars[(ich >>> 8) & 15])
              .append(hexChars[(ich >>> 4) & 15])
              .append(hexChars[ich & 15]);
        }
      } else {
        if (encoded != null) {
          encoded.append(ch);
        }
      }
    }
    return encoded == null ? value : encoded.toString();
  }

  @SuppressWarnings({"AssignmentToForLoopParameter", "deprecation"})
  public static String decode(String encoded) {
    if (encoded == null) {
      return null;
    }

    StringBuilder value = null;
    int len = encoded.length();
    for (int c = 0; c < len; c++) {
      char ch = encoded.charAt(c);
      if (ch == '\\') {
        if (value == null) {
          value = new StringBuilder();
          if (c > 0) {
            value.append(encoded, 0, c);
          }
        }
        if (++c < len) {
          ch = encoded.charAt(c);
          if (ch == '\\') {
            value.append('\\');
          } else if (ch == 'b' || ch == 'B') {
            value.append('\b');
          } else if (ch == 'f' || ch == 'F') {
            value.append('\f');
          } else if (ch == 't' || ch == 'T') {
            value.append('\t');
          } else if (ch == 'u' || ch == 'U') {
            if (++c < len) {
              char ch1 = encoded.charAt(c);
              if (
                  (ch1 >= '0' && ch1 <= '9')
                      || (ch1 >= 'a' && ch1 <= 'f')
                      || (ch1 >= 'A' && ch1 <= 'F')
              ) {
                if (++c < len) {
                  char ch2 = encoded.charAt(c);
                  if (
                      (ch2 >= '0' && ch2 <= '9')
                          || (ch2 >= 'a' && ch2 <= 'f')
                          || (ch2 >= 'A' && ch2 <= 'F')
                  ) {
                    if (++c < len) {
                      char ch3 = encoded.charAt(c);
                      if (
                          (ch3 >= '0' && ch3 <= '9')
                              || (ch3 >= 'a' && ch3 <= 'f')
                              || (ch3 >= 'A' && ch3 <= 'F')
                      ) {
                        if (++c < len) {
                          char ch4 = encoded.charAt(c);
                          if (
                              (ch4 >= '0' && ch4 <= '9')
                                  || (ch4 >= 'a' && ch4 <= 'f')
                                  || (ch4 >= 'A' && ch4 <= 'F')
                          ) {
                            value.append(
                                (char) (
                                    (Strings.getHex(ch1) << 12)
                                        | (Strings.getHex(ch2) << 8)
                                        | (Strings.getHex(ch3) << 4)
                                        | Strings.getHex(ch4)
                                )
                            );
                          } else {
                            value.append('\\').append(ch).append(ch1).append(ch2).append(ch3).append(ch4);
                          }
                        } else {
                          value.append('\\').append(ch).append(ch1).append(ch2).append(ch3);
                        }
                      } else {
                        value.append('\\').append(ch).append(ch1).append(ch2).append(ch3);
                      }
                    } else {
                      value.append('\\').append(ch).append(ch1).append(ch2);
                    }
                  } else {
                    value.append('\\').append(ch).append(ch1).append(ch2);
                  }
                } else {
                  value.append('\\').append(ch).append(ch1);
                }
              } else {
                value.append('\\').append(ch).append(ch1);
              }
            } else {
              value.append('\\').append(ch);
            }
          } else {
            value.append('\\').append(ch);
          }
        } else {
          value.append('\\');
        }
      } else {
        if (value != null) {
          value.append(ch);
        }
      }
    }
    return value == null ? encoded : value.toString();
  }
}
