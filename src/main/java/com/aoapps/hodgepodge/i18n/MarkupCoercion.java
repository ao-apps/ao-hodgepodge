/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.io.Writable;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import javax.swing.text.Segment;
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
public abstract class MarkupCoercion {

	/** Make no instances. */
	private MarkupCoercion() {throw new AssertionError();}

	/**
	 * Writes an object's String representation with markup enabled,
	 * supporting streaming for specialized types.
	 *
	 * @see  MarkupType
	 */
	public static void write(Object value, MarkupType markupType, Writer out) throws IOException {
		// Support Optional
		while(value instanceof Optional) {
			value = ((Optional<?>)value).orElse(null);
		}
		if(value != null) {
			BundleLookupThreadContext threadContext;
			if(
				markupType == null
				|| markupType == MarkupType.NONE
				|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
				// Avoid intermediate String from Writable
				|| (
					value instanceof Writable
					&& !((Writable)value).isFastToString()
				)
				// Other types that will not be converted to String for bundle lookups
				|| value instanceof Segment
				|| value instanceof char[]
				|| value instanceof Node
			) {
				Coercion.write(value, out);
			} else {
				String str = Coercion.toString(value);
				BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
				if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
				out.write(str);
				if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
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
	 *
	 * @param  encoder  no encoding performed when null
	 *
	 * @param  encoderPrefixSuffix  This includes the encoder {@linkplain Encoder#writePrefixTo(java.lang.Appendable) prefix}
	 *                              and {@linkplain Encoder#writeSuffixTo(java.lang.Appendable) suffix}.
	 *
	 * @see  MarkupType
	 */
	public static void write(Object value, MarkupType markupType, boolean encodeLookupMarkup, Encoder encoder, boolean encoderPrefixSuffix, Writer out) throws IOException {
		if(encoder == null) {
			write(value, markupType, out);
		} else {
			// Support Optional
			while(value instanceof Optional) {
				value = ((Optional<?>)value).orElse(null);
			}
			if(value != null) {
				BundleLookupThreadContext threadContext;
				if(
					markupType == null
					|| markupType == MarkupType.NONE
					|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
					// Avoid intermediate String from Writable
					|| (
						value instanceof Writable
						&& !((Writable)value).isFastToString()
					)
					// Other types that will not be converted to String for bundle lookups
					|| value instanceof Segment
					|| value instanceof char[]
					|| value instanceof Node
				) {
					if(encoderPrefixSuffix) encoder.writePrefixTo(out);
					Coercion.write(value, encoder, out);
					if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
				} else {
					String str = Coercion.toString(value);
					BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
					if(lookupMarkup != null && !encodeLookupMarkup) lookupMarkup.appendPrefixTo(markupType, out);
					if(encoderPrefixSuffix) encoder.writePrefixTo(out);
					if(lookupMarkup != null && encodeLookupMarkup) lookupMarkup.appendPrefixTo(markupType, encoder, out);
					encoder.write(str, out);
					if(lookupMarkup != null && encodeLookupMarkup) lookupMarkup.appendSuffixTo(markupType, encoder, out);
					if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
					if(lookupMarkup != null && !encodeLookupMarkup) lookupMarkup.appendSuffixTo(markupType, out);
				}
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
		if(out instanceof Writer) {
			write(value, markupType, (Writer)out);
		} else {
			// Support Optional
			while(value instanceof Optional) {
				value = ((Optional<?>)value).orElse(null);
			}
			if(value != null) {
				BundleLookupThreadContext threadContext;
				if(
					markupType == null
					|| markupType == MarkupType.NONE
					|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
					// Avoid intermediate String from Writable
					|| (
						value instanceof Writable
						&& !((Writable)value).isFastToString()
					)
					// Other types that will not be converted to String for bundle lookups
					|| value instanceof Segment
					|| value instanceof char[]
					|| value instanceof Node
				) {
					Coercion.append(value, out);
				} else {
					String str = Coercion.toString(value);
					BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
					if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
					assert out != null;
					out.append(str);
					if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
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
	 *
	 * @param  encoder  no encoding performed when null
	 *
	 * @param  encoderPrefixSuffix  This includes the encoder {@linkplain Encoder#writePrefixTo(java.lang.Appendable) prefix}
	 *                              and {@linkplain Encoder#writeSuffixTo(java.lang.Appendable) suffix}.
	 *
	 * @see  MarkupType
	 */
	public static void append(Object value, MarkupType markupType, boolean encodeLookupMarkup, Encoder encoder, boolean encoderPrefixSuffix, Appendable out) throws IOException {
		if(encoder == null) {
			append(value, markupType, out);
		} else if(out instanceof Writer) {
			write(value, markupType, encodeLookupMarkup, encoder, encoderPrefixSuffix, (Writer)out);
		} else {
			// Support Optional
			while(value instanceof Optional) {
				value = ((Optional<?>)value).orElse(null);
			}
			if(value != null) {
				BundleLookupThreadContext threadContext;
				if(
					markupType == null
					|| markupType == MarkupType.NONE
					|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
					// Avoid intermediate String from Writable
					|| (
						value instanceof Writable
						&& !((Writable)value).isFastToString()
					)
					// Other types that will not be converted to String for bundle lookups
					|| value instanceof Segment
					|| value instanceof char[]
					|| value instanceof Node
				) {
					if(encoderPrefixSuffix) encoder.writePrefixTo(out);
					Coercion.append(value, encoder, out);
					if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
				} else {
					String str = Coercion.toString(value);
					BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
					if(lookupMarkup != null && !encodeLookupMarkup) lookupMarkup.appendPrefixTo(markupType, out);
					if(encoderPrefixSuffix) encoder.writePrefixTo(out);
					if(lookupMarkup != null && encodeLookupMarkup) lookupMarkup.appendPrefixTo(markupType, encoder, out);
					encoder.append(str, out);
					if(lookupMarkup != null && encodeLookupMarkup) lookupMarkup.appendSuffixTo(markupType, encoder, out);
					if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
					if(lookupMarkup != null && !encodeLookupMarkup) lookupMarkup.appendSuffixTo(markupType, out);
				}
			}
		}
	}
}
