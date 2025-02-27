/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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

import com.aoapps.hodgepodge.i18n.BundleLookupMarkup;
import com.aoapps.hodgepodge.i18n.BundleLookupThreadContext;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.lang.Coercion;
import java.io.IOException;

/**
 * Provides encoding and escaping for various type of data.
 *
 * @author  AO Industries, Inc.
 *
 * @deprecated  Use new encoding package instead.
 */
@Deprecated
public final class EncodingUtils {

  /** Make no instances. */
  private EncodingUtils() {
    throw new AssertionError();
  }

  /**
   * @deprecated  Use {@link Coercion#toString(java.lang.Object)} instead.
   */
  @Deprecated
  public static String toString(Object value) {
    return Coercion.toString(value);
  }

  // <editor-fold defaultstate="collapsed" desc="(X)HTML">
  /**
   * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
   * In addition to the standard XML Body encoding, it turns newlines into &lt;br /&gt;, tabs to &amp;#x9;, and spaces to &amp;#160;
   *
   * <p>See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.</p>
   *
   * @param  value  the object to be escaped.  If value is <code>null</code>, nothing is written.
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static void encodeHtml(Object value, Appendable out, boolean isXhtml) throws IOException {
    encodeHtml(value, true, true, out, isXhtml);
  }

  /**
   * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
   * In addition to the standard XML Body encoding, it turns newlines into &lt;br /&gt; and spaces to &amp;#160;
   *
   * <p>See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.</p>
   *
   * @param cs the string to be escaped.  If S is <code>null</code>, nothing is written.
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static void encodeHtml(CharSequence cs, int start, int end, Appendable out, boolean isXhtml) throws IOException {
    encodeHtml(cs, start, end, true, true, out, isXhtml);
  }

  /**
   * See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static String encodeHtml(Object value, boolean isXhtml) throws IOException {
    if (value == null) {
      return null;
    }
    StringBuilder result = new StringBuilder();
    encodeHtml(value, result, isXhtml);
    return result.toString();
  }

  /**
   * See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static void encodeHtml(char ch, Appendable out, boolean isXhtml) throws IOException {
    encodeHtml(ch, true, true, out, isXhtml);
  }

  /**
   * See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static void encodeHtml(Object value, boolean makeBr, boolean makeNbsp, Appendable out, boolean isXhtml) throws IOException {
    if (value != null) {
      String str = Coercion.toString(value);
      BundleLookupMarkup lookupMarkup;
      BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext();
      if (threadContext != null) {
        lookupMarkup = threadContext.getLookupMarkup(str);
      } else {
        lookupMarkup = null;
      }
      if (lookupMarkup != null) {
        lookupMarkup.appendPrefixTo(MarkupType.XHTML, out);
      }
      encodeHtml(str, 0, str.length(), makeBr, makeNbsp, out, isXhtml);
      if (lookupMarkup != null) {
        lookupMarkup.appendSuffixTo(MarkupType.XHTML, out);
      }
    }
  }

  /**
   * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
   * Optionally, it turns newlines into &lt;br /&gt; and spaces to &amp;#160;
   * Any characters less than 0x1f that are not \t, \r, or \n are completely filtered.
   *
   * <p>See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.</p>
   *
   * @param cs the string to be escaped.  If S is <code>null</code>, nothing is written.
   * @param makeBr  will write &lt;br /&gt; tags for every newline character
   * @param makeNbsp  will write &amp;#160; for a space when another space follows
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static void encodeHtml(CharSequence cs, int start, int end, boolean makeBr, boolean makeNbsp, Appendable out, boolean isXhtml) throws IOException {
    if (cs != null) {
      int toPrint = 0;
      for (int c = start; c < end; c++) {
        char ch = cs.charAt(c);
        switch (ch) {
          // Standard XML escapes
          case '<':
            {
              if (toPrint > 0) {
                out.append(cs, c - toPrint, c);
                toPrint = 0;
              }
              out.append("&lt;");
              break;
            }
          case '>':
            {
              if (toPrint > 0) {
                out.append(cs, c - toPrint, c);
                toPrint = 0;
              }
              out.append("&gt;");
              break;
            }
          case '&':
            {
              if (toPrint > 0) {
                out.append(cs, c - toPrint, c);
                toPrint = 0;
              }
              out.append("&amp;");
              break;
            }
          // Special (X)HTML options
          case ' ':
            {
              if (makeNbsp) {
                if (toPrint > 0) {
                  out.append(cs, c - toPrint, c);
                  toPrint = 0;
                }
                out.append("&#160;");
              } else {
                toPrint++;
              }
              break;
            }
          case '\t':
            {
              if (toPrint > 0) {
                out.append(cs, c - toPrint, c);
                toPrint = 0;
              }
              out.append("&#x9;");
              break;
            }
          case '\r':
            {
              if (toPrint > 0) {
                out.append(cs, c - toPrint, c);
                toPrint = 0;
              }
              // skip '\r'
              break;
            }
          case '\n':
            {
              if (makeBr) {
                if (toPrint > 0) {
                  out.append(cs, c - toPrint, c);
                  toPrint = 0;
                }
                out.append(isXhtml ? "<br />" : "<br>").append('\n');
              } else {
                toPrint++;
              }
              break;
            }
          default:
            {
              if (ch < ' ') {
                if (toPrint > 0) {
                  out.append(cs, c - toPrint, c);
                  toPrint = 0;
                }
                // skip the character
              } else {
                toPrint++;
              }
            }
        }
      }
      if (toPrint > 0) {
        out.append(cs, end - toPrint, end);
      }
    }
  }

  /**
   * See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.
   *
   * @param  value  the string to be escaped.
   *
   * @return if value is null then null otherwise value escaped
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static String encodeHtml(Object value, boolean makeBr, boolean makeNbsp, boolean isXhtml) throws IOException {
    if (value == null) {
      return null;
    }
    StringBuilder result = new StringBuilder();
    encodeHtml(value, makeBr, makeNbsp, result, isXhtml);
    return result.toString();
  }

  /**
   * See <a href="https://oss.aoapps.com/encoding/apidocs/com.aoapps.encoding/com/aoapps/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>.
   *
   * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
   */
  @Deprecated
  public static void encodeHtml(char ch, boolean makeBr, boolean makeNbsp, Appendable out, boolean isXhtml) throws IOException {
    switch (ch) {
      // Standard XML escapes
      case '<':
        {
          out.append("&lt;");
          break;
        }
      case '>':
        {
          out.append("&gt;");
          break;
        }
      case '&':
        {
          out.append("&amp;");
          break;
        }
      // Special (X)HTML options
      case ' ':
        {
          if (makeNbsp) {
            out.append("&#160;");
          } else {
            out.append(' ');
          }
          break;
        }
      case '\t':
        {
          out.append("&#x9;");
          break;
        }
      case '\r':
        {
          // skip '\r'
          break;
        }
      case '\n':
        {
          if (makeBr) {
            out.append(isXhtml ? "<br />" : "<br>").append('\n');
          } else {
            out.append('\n');
          }
          break;
        }
      default:
        {
          if (ch < ' ') {
            // skip the character
          } else {
            out.append(ch);
          }
        }
    }
  }
  // </editor-fold>
}
