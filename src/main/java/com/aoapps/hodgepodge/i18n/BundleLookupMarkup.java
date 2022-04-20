/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.io.Encoder;
import java.io.IOException;

/**
 * The result of a bundle lookup, with possible prefix and suffix values that
 * represent markup to be added for any in-context translation editors.
 *
 * @author  AO Industries, Inc.
 */
public interface BundleLookupMarkup {

  /**
   * Appends the prefix to the given out using the given markup type.
   */
  void appendPrefixTo(MarkupType markupType, Appendable out) throws IOException;

  /**
   * Appends the prefix to the given out using the given encoder and markup type.
   */
  void appendPrefixTo(MarkupType markupType, Encoder encoder, Appendable out) throws IOException;

  /**
   * Appends the suffix to the given out using the given markup type.
   */
  void appendSuffixTo(MarkupType markupType, Appendable out) throws IOException;

  /**
   * Appends the suffix to the given out using the given encoder and markup type.
   */
  void appendSuffixTo(MarkupType markupType, Encoder encoder, Appendable out) throws IOException;
}
