/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.i18n;

import com.aoapps.lang.Coercion;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.io.EncoderWriter;
import com.aoapps.lang.io.Writable;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.ResourceBundle;
import org.w3c.dom.Node;

/**
 * Coerces objects to String compatible with JSP Expression Language (JSP EL)
 * and the Java Standard Taglib (JSTL).  Also adds support for seamless output
 * of XML DOM nodes.
 *
 * @author  AO Industries, Inc.
 *
 * @see  Coercion
 */
public final class MarkupCoercion {

  /** Make no instances. */
  private MarkupCoercion() {
    throw new AssertionError();
  }

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, MarkupCoercion.class);

  /**
   * Writes an object's String representation with markup enabled,
   * supporting streaming for specialized types.
   *
   * @see  MarkupType
   */
  public static void write(Object value, MarkupType markupType, Writer out) throws IOException {
    write(value, markupType, out, false);
  }

  /**
   * Writes an object's String representation with markup enabled,
   * supporting streaming for specialized types.
   *
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   *                       (with {@code encoder = null})?
   *
   * @see  MarkupType
   */
  public static void write(Object value, MarkupType markupType, Writer out, boolean outOptimized) throws IOException {
    // Support Optional
    while (value instanceof Optional) {
      value = ((Optional<?>) value).orElse(null);
    }
    if (value != null) {
      if (out instanceof EncoderWriter) {
        // Unwrap media writer and use encoder directly
        EncoderWriter encoderWriter = (EncoderWriter) out;
        write(
            value,
            markupType,
            true, // Must run markup through encoder
            encoderWriter.getEncoder(),
            false, // No prefix/suffix
            encoderWriter.getOut(),
            true // EncoderWriter always optimizes out
        );
      } else {
        // Optimize output
        Writer optimized;
        if (outOptimized) {
          optimized = out;
          assert optimized == Coercion.optimize(out, null);
        } else {
          optimized = Coercion.optimize(out, null);
        }
        BundleLookupThreadContext threadContext;
        if (
            markupType == null
                || markupType == MarkupType.NONE
                || (threadContext = BundleLookupThreadContext.getThreadContext()) == null
                // Avoid intermediate String from Writable
                || (
                value instanceof Writable
                    && !((Writable) value).isFastToString()
            )
                // Other types that will not be converted to String for bundle lookups
                || (value instanceof CharSequence && !(value instanceof String))
                || value instanceof char[]
                || value instanceof Node
        ) {
          Coercion.write(value, optimized, true);
        } else {
          String str = Coercion.toString(value);
          BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
          if (lookupMarkup != null) {
            lookupMarkup.appendPrefixTo(markupType, optimized);
          }
          optimized.write(str);
          if (lookupMarkup != null) {
            lookupMarkup.appendSuffixTo(markupType, optimized);
          }
        }
      }
    }
  }

  /**
   * Encodes an object's String representation with markup enabled using the provided encoder,
   * supporting streaming for specialized types.
   *
   * @param  encodeLookupMarkup  <p>Does the lookup markup need to be encoded?</p>
   *                             <p>When {@code encodeLookupMarkup = true}:</p>
   *                             <ol>
   *                               <li>Write markup prefix without encoding</li>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                               <li>Write markup suffix without encoding</li>
   *                             </ol>
   *                             <p>When {@code encodeLookupMarkup = false}:</p>
   *                             <ol>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write markup prefix with encoding</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write markup suffix with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                             </ol>
   *                             <p>
   *                               Value is not used when {@code encoder == null}.
   *                             </p>
   *
   * @param  encoder  no encoding performed when null, and values of {@code encodeLookupMarkup} and
   *                  {@code encoderPrefixSuffix} are not used.
   *
   * @param  encoderPrefixSuffix  This includes the encoder {@linkplain Encoder#writePrefixTo(java.lang.Appendable) prefix}
   *                              and {@linkplain Encoder#writeSuffixTo(java.lang.Appendable, boolean) suffix}.
   *                              <p>
   *                                Value is not used when {@code encoder == null}.
   *                              </p>
   *
   * @see  MarkupType
   */
  public static void write(Object value, MarkupType markupType, boolean encodeLookupMarkup, Encoder encoder, boolean encoderPrefixSuffix, Writer out) throws IOException {
    write(value, markupType, encodeLookupMarkup, encoder, encoderPrefixSuffix, out, false);
  }

  /**
   * Encodes an object's String representation with markup enabled using the provided encoder,
   * supporting streaming for specialized types.
   *
   * @param  encodeLookupMarkup  <p>Does the lookup markup need to be encoded?</p>
   *                             <p>When {@code encodeLookupMarkup = true}:</p>
   *                             <ol>
   *                               <li>Write markup prefix without encoding</li>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                               <li>Write markup suffix without encoding</li>
   *                             </ol>
   *                             <p>When {@code encodeLookupMarkup = false}:</p>
   *                             <ol>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write markup prefix with encoding</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write markup suffix with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                             </ol>
   *                             <p>
   *                               Value is not used when {@code encoder == null}.
   *                             </p>
   *
   * @param  encoder  no encoding performed when null, and values of {@code encodeLookupMarkup} and
   *                  {@code encoderPrefixSuffix} are not used.
   *
   * @param  encoderPrefixSuffix  This includes the encoder {@linkplain Encoder#writePrefixTo(java.lang.Appendable) prefix}
   *                              and {@linkplain Encoder#writeSuffixTo(java.lang.Appendable, boolean) suffix}.
   *                              <p>
   *                                Value is not used when {@code encoder == null}.
   *                              </p>
   *
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   *
   * @see  MarkupType
   */
  public static void write(Object value, MarkupType markupType, boolean encodeLookupMarkup, Encoder encoder, boolean encoderPrefixSuffix, Writer out, boolean outOptimized) throws IOException {
    if (encoder == null) {
      if (!(out instanceof EncoderWriter)) {
        write(value, markupType, out, outOptimized);
        return;
      }
      // Unwrap media writer and use encoder directly
      EncoderWriter encoderWriter = (EncoderWriter) out;
      encodeLookupMarkup = true; // Must run markup through encoder
      encoder = encoderWriter.getEncoder();
      encoderPrefixSuffix = false; // No prefix/suffix
      out = encoderWriter.getOut();
      outOptimized = true; // EncoderWriter always optimizes out
    } else if (
        // Do not allow bypassing a buffered encoder
        !encoderPrefixSuffix                                   // encoder.writeSuffixTo is not called
            && !encodeLookupMarkup                                 // markup not sent through encoder
            && markupType != null && markupType != MarkupType.NONE // there is potentially markup
            && encoder.isBuffered()                                // the encoder is buffered, and thus would be bypassed
    ) {
      throw new LocalizedIllegalArgumentException(
          RESOURCES,
          "notAllowedToBypassBufferedEncoder",
          markupType.name(),
          encoder.getClass().getName(),
          out.getClass().getName()
      );
    }
    // Support Optional
    while (value instanceof Optional) {
      value = ((Optional<?>) value).orElse(null);
    }
    if (value != null || encoderPrefixSuffix) {
      // Optimize output
      Writer optimized;
      if (outOptimized) {
        optimized = out;
        assert optimized == Coercion.optimize(out, encoder);
      } else {
        optimized = Coercion.optimize(out, encoder);
      }
      if (value != null) {
        BundleLookupThreadContext threadContext;
        if (
            markupType == null
                || markupType == MarkupType.NONE
                || (threadContext = BundleLookupThreadContext.getThreadContext()) == null
                // Avoid intermediate String from Writable
                || (
                value instanceof Writable
                    && !((Writable) value).isFastToString()
            )
                // Other types that will not be converted to String for bundle lookups
                || (value instanceof CharSequence && !(value instanceof String))
                || value instanceof char[]
                || value instanceof Node
        ) {
          if (encoderPrefixSuffix) {
            encoder.writePrefixTo(optimized);
          }
          Coercion.write(value, encoder, optimized, true);
          if (encoderPrefixSuffix) {
            encoder.writeSuffixTo(optimized, false);
          }
        } else {
          String str = Coercion.toString(value);
          BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
          if (lookupMarkup != null && !encodeLookupMarkup) {
            lookupMarkup.appendPrefixTo(markupType, optimized);
          }
          if (encoderPrefixSuffix) {
            encoder.writePrefixTo(optimized);
          }
          if (lookupMarkup != null && encodeLookupMarkup) {
            lookupMarkup.appendPrefixTo(markupType, encoder, optimized);
          }
          encoder.write(str, optimized);
          if (lookupMarkup != null && encodeLookupMarkup) {
            lookupMarkup.appendSuffixTo(markupType, encoder, optimized);
          }
          if (encoderPrefixSuffix) {
            encoder.writeSuffixTo(optimized, false);
          }
          if (lookupMarkup != null && !encodeLookupMarkup) {
            lookupMarkup.appendSuffixTo(markupType, optimized);
          }
        }
      } else {
        // Always write prefix/suffix even when value is null, for consistency.
        // For example, writing null text in JavaScript should still be "", since writing null is equivalent to "".
        encoder.writePrefixTo(optimized);
        encoder.writeSuffixTo(optimized, false);
      }
    }
  }

  /**
   * Appends an object's String representation with markup enabled,
   * supporting streaming for specialized types.
   *
   * @see  MarkupType
   */
  public static void append(Object value, MarkupType markupType, Appendable out) throws IOException {
    append(value, markupType, out, false);
  }

  /**
   * Appends an object's String representation with markup enabled,
   * supporting streaming for specialized types.
   *
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   *                       (with {@code encoder = null})?
   *
   * @see  MarkupType
   */
  public static void append(Object value, MarkupType markupType, Appendable out, boolean outOptimized) throws IOException {
    if (out instanceof Writer) {
      write(value, markupType, (Writer) out, outOptimized);
    } else {
      // Support Optional
      while (value instanceof Optional) {
        value = ((Optional<?>) value).orElse(null);
      }
      if (value != null) {
        // Optimize output
        Appendable optimized;
        if (outOptimized) {
          optimized = out;
          assert optimized == Coercion.optimize(out, null);
        } else {
          optimized = Coercion.optimize(out, null);
        }
        BundleLookupThreadContext threadContext;
        if (
            markupType == null
                || markupType == MarkupType.NONE
                || (threadContext = BundleLookupThreadContext.getThreadContext()) == null
                // Avoid intermediate String from Writable
                || (
                value instanceof Writable
                    && !((Writable) value).isFastToString()
            )
                // Other types that will not be converted to String for bundle lookups
                || (value instanceof CharSequence && !(value instanceof String))
                || value instanceof char[]
                || value instanceof Node
        ) {
          Coercion.append(value, optimized, true);
        } else {
          String str = Coercion.toString(value);
          BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
          if (lookupMarkup != null) {
            lookupMarkup.appendPrefixTo(markupType, optimized);
          }
          assert optimized != null;
          optimized.append(str);
          if (lookupMarkup != null) {
            lookupMarkup.appendSuffixTo(markupType, optimized);
          }
        }
      }
    }
  }

  /**
   * Encodes an object's String representation with markup enabled using the provided encoder,
   * supporting streaming for specialized types.
   *
   * @param  encodeLookupMarkup  <p>Does the lookup markup need to be encoded?</p>
   *                             <p>When {@code encodeLookupMarkup = true}:</p>
   *                             <ol>
   *                               <li>Write markup prefix without encoding</li>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                               <li>Write markup suffix without encoding</li>
   *                             </ol>
   *                             <p>When {@code encodeLookupMarkup = false}:</p>
   *                             <ol>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write markup prefix with encoding</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write markup suffix with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                             </ol>
   *                             <p>
   *                               Value is not used when {@code encoder == null}.
   *                             </p>
   *
   * @param  encoder  no encoding performed when null, and values of {@code encodeLookupMarkup} and
   *                  {@code encoderPrefixSuffix} are not used.
   *
   * @param  encoderPrefixSuffix  This includes the encoder {@linkplain Encoder#writePrefixTo(java.lang.Appendable) prefix}
   *                              and {@linkplain Encoder#writeSuffixTo(java.lang.Appendable, boolean) suffix}.
   *                              <p>
   *                                Value is not used when {@code encoder == null}.
   *                              </p>
   *
   * @see  MarkupType
   */
  public static void append(Object value, MarkupType markupType, boolean encodeLookupMarkup, Encoder encoder, boolean encoderPrefixSuffix, Appendable out) throws IOException {
    append(value, markupType, encodeLookupMarkup, encoder, encoderPrefixSuffix, out, false);
  }

  /**
   * Encodes an object's String representation with markup enabled using the provided encoder,
   * supporting streaming for specialized types.
   *
   * @param  encodeLookupMarkup  <p>Does the lookup markup need to be encoded?</p>
   *                             <p>When {@code encodeLookupMarkup = true}:</p>
   *                             <ol>
   *                               <li>Write markup prefix without encoding</li>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                               <li>Write markup suffix without encoding</li>
   *                             </ol>
   *                             <p>When {@code encodeLookupMarkup = false}:</p>
   *                             <ol>
   *                               <li>Write any encoder prefix</li>
   *                               <li>Write markup prefix with encoding</li>
   *                               <li>Write value with encoding</li>
   *                               <li>Write markup suffix with encoding</li>
   *                               <li>Write any encoder suffix</li>
   *                             </ol>
   *                             <p>
   *                               Value is not used when {@code encoder == null}.
   *                             </p>
   *
   * @param  encoder  no encoding performed when null, and values of {@code encodeLookupMarkup} and
   *                  {@code encoderPrefixSuffix} are not used.
   *
   * @param  encoderPrefixSuffix  This includes the encoder {@linkplain Encoder#writePrefixTo(java.lang.Appendable) prefix}
   *                              and {@linkplain Encoder#writeSuffixTo(java.lang.Appendable, boolean) suffix}.
   *                              <p>
   *                                Value is not used when {@code encoder == null}.
   *                              </p>
   *
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   *
   * @see  MarkupType
   */
  public static void append(Object value, MarkupType markupType, boolean encodeLookupMarkup, Encoder encoder, boolean encoderPrefixSuffix, Appendable out, boolean outOptimized) throws IOException {
    if (encoder == null) {
      append(value, markupType, out, outOptimized);
    } else if (out instanceof Writer) {
      write(value, markupType, encodeLookupMarkup, encoder, encoderPrefixSuffix, (Writer) out, outOptimized);
    } else {
      // Do not allow bypassing a buffered encoder
      assert out != null;
      if (
          !encoderPrefixSuffix                                   // encoder.writeSuffixTo is not called
              && !encodeLookupMarkup                                 // markup not sent through encoder
              && markupType != null && markupType != MarkupType.NONE // there is potentially markup
              && encoder.isBuffered()                                // the encoder is buffered, and thus would be bypassed
      ) {
        throw new LocalizedIllegalArgumentException(
            RESOURCES,
            "notAllowedToBypassBufferedEncoder",
            markupType.name(),
            encoder.getClass().getName(),
            out.getClass().getName()
        );
      }
      // Support Optional
      while (value instanceof Optional) {
        value = ((Optional<?>) value).orElse(null);
      }
      if (value != null || encoderPrefixSuffix) {
        // Optimize output
        Appendable optimized;
        if (outOptimized) {
          optimized = out;
          assert optimized == Coercion.optimize(out, encoder);
        } else {
          optimized = Coercion.optimize(out, encoder);
        }
        if (value != null) {
          BundleLookupThreadContext threadContext;
          if (
              markupType == null
                  || markupType == MarkupType.NONE
                  || (threadContext = BundleLookupThreadContext.getThreadContext()) == null
                  // Avoid intermediate String from Writable
                  || (
                  value instanceof Writable
                      && !((Writable) value).isFastToString()
              )
                  // Other types that will not be converted to String for bundle lookups
                  || (value instanceof CharSequence && !(value instanceof String))
                  || value instanceof char[]
                  || value instanceof Node
          ) {
            if (encoderPrefixSuffix) {
              encoder.writePrefixTo(optimized);
            }
            Coercion.append(value, encoder, optimized, true);
            if (encoderPrefixSuffix) {
              encoder.writeSuffixTo(optimized, false);
            }
          } else {
            String str = Coercion.toString(value);
            BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
            if (lookupMarkup != null && !encodeLookupMarkup) {
              lookupMarkup.appendPrefixTo(markupType, optimized);
            }
            if (encoderPrefixSuffix) {
              encoder.writePrefixTo(optimized);
            }
            if (lookupMarkup != null && encodeLookupMarkup) {
              lookupMarkup.appendPrefixTo(markupType, encoder, optimized);
            }
            encoder.append(str, optimized);
            if (lookupMarkup != null && encodeLookupMarkup) {
              lookupMarkup.appendSuffixTo(markupType, encoder, optimized);
            }
            if (encoderPrefixSuffix) {
              encoder.writeSuffixTo(optimized, false);
            }
            if (lookupMarkup != null && !encodeLookupMarkup) {
              lookupMarkup.appendSuffixTo(markupType, optimized);
            }
          }
        } else {
          // Always write prefix/suffix even when value is null, for consistency.
          // For example, writing null text in JavaScript should still be "", since writing null is equivalent to "".
          encoder.writePrefixTo(optimized);
          encoder.writeSuffixTo(optimized, false);
        }
      }
    }
  }
}
