/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.util;

import com.aoindustries.io.Writable;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

/**
 * Provides encoding and escaping for various type of data.
 *
 * @author  AO Industries, Inc.
 * 
 * @deprecated  Use new encoding package instead.
 */
@Deprecated
public final class EncodingUtils {

	private EncodingUtils() {
	}

	/**
	 * Converts an object to a string.
	 * 
	 * @deprecated  Use <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/Coercion.html#toString-java.lang.Object-">Coercion.toString(Object)</a> instead.
	 */
	@Deprecated
	public static String toString(Object value) {
		// If A is a string, then the result is A.
		if(value instanceof String) return (String)value;
		// Otherwise, if A is null, then the result is "".
		if(value == null) return "";
		// Otherwise, if is a Writable, support optimizations
		if(value instanceof Writable) {
			// Note: This is only optimal for Writable that are "isFastToString()",
			//       but we don't have much better option since a String is required.
			//       Keeping this here, instead of falling-through to toString() below,
			//       so behavior is consistent in the odd change a class is a Node and implements Writable.
			//       This should keep it consistent with other coercions.
			return value.toString();
		}
		// Otherwise, support CharSequence
		if(value instanceof CharSequence) return value.toString();
		// Otherwise, if is a DOM node, serialize the output
		if(value instanceof Node) {
			try {
				// Can use thread-local or pooled transformers if performance is ever an issue
				TransformerFactory transFactory = TransformerFactory.newInstance();
				Transformer transformer = transFactory.newTransformer();
				StringWriter buffer = new StringWriter();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
				transformer.transform(
					new DOMSource((Node)value),
					new StreamResult(buffer)
				);
				return buffer.toString();
			} catch(TransformerException e) {
				throw new WrappedException(e);
			}
		}
		// Otherwise, if A.toString() throws an exception, then raise an error
		String str = value.toString();
		// Otherwise, the result is A.toString();
		return str;
	}

	private static final String EOL = System.getProperty("line.separator");
	private static final String BR_EOL = "<br />"+EOL; // TODO: HTML/XHTML serialization

	// <editor-fold defaultstate="collapsed" desc="(X)HTML">
	/**
	 * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
	 * In addition to the standard XML Body encoding, it turns newlines into &lt;br /&gt;, tabs to &amp;#x9;, and spaces to &amp;#160;
	 *
	 * @param  value  the object to be escaped.  If value is <code>null</code>, nothing is written.
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static void encodeHtml(Object value, Appendable out) throws IOException {
		encodeHtml(value, true, true, out);
	}

	/**
	 * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
	 * In addition to the standard XML Body encoding, it turns newlines into &lt;br /&gt; and spaces to &amp;#160;
	 *
	 * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static void encodeHtml(CharSequence S, int start, int end, Appendable out) throws IOException {
		encodeHtml(S, start, end, true, true, out);
	}

	/**
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static String encodeHtml(Object value) throws IOException {
		if(value==null) return null;
		StringBuilder result = new StringBuilder();
		encodeHtml(value, result);
		return result.toString();
	}

	/**
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static void encodeHtml(char ch, Appendable out) throws IOException {
		encodeHtml(ch, true, true, out);
	}

	/**
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static void encodeHtml(Object value, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
		if(value!=null) {
			String str = toString(value);
			BundleLookupMarkup lookupMarkup;
			BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
			if(threadContext!=null) {
				lookupMarkup = threadContext.getLookupMarkup(str);
			} else {
				lookupMarkup = null;
			}
			if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(MarkupType.XHTML, out);
			encodeHtml(str, 0, str.length(), make_br, make_nbsp, out);
			if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(MarkupType.XHTML, out);
		}
	}

	/**
	 * Escapes for use in a (X)HTML document and writes to the provided <code>Appendable</code>.
	 * Optionally, it turns newlines into &lt;br /&gt; and spaces to &amp;#160;
	 * Any characters less than 0x1f that are not \t, \r, or \n are completely filtered.
	 *
	 * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
	 * @param make_br  will write &lt;br /&gt; tags for every newline character
	 * @param make_nbsp  will write &amp;#160; for a space when another space follows
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static void encodeHtml(CharSequence S, int start, int end, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
		if (S != null) {
			int toPrint = 0;
			for (int c = start; c < end; c++) {
				char ch = S.charAt(c);
				switch(ch) {
					// Standard XML escapes
					case '<':
						if(toPrint>0) {
							out.append(S, c-toPrint, c);
							toPrint=0;
						}
						out.append("&lt;");
						break;
					case '>':
						if(toPrint>0) {
							out.append(S, c-toPrint, c);
							toPrint=0;
						}
						out.append("&gt;");
						break;
					case '&':
						if(toPrint>0) {
							out.append(S, c-toPrint, c);
							toPrint=0;
						}
						out.append("&amp;");
						break;
					// Special (X)HTML options
					case ' ':
						if(make_nbsp) {
							if(toPrint>0) {
								out.append(S, c-toPrint, c);
								toPrint=0;
							}
							out.append("&#160;");
						} else {
							toPrint++;
						}
						break;
					case '\t':
						if(toPrint>0) {
							out.append(S, c-toPrint, c);
							toPrint=0;
						}
						out.append("&#x9;");
						break;
					case '\r':
						if(toPrint>0) {
							out.append(S, c-toPrint, c);
							toPrint=0;
						}
						// skip '\r'
						break;
					case '\n':
						if(make_br) {
							if(toPrint>0) {
								out.append(S, c-toPrint, c);
								toPrint=0;
							}
							out.append("<br />\n"); // TODO: HTML/XHTML serialization
						} else {
							toPrint++;
						}
						break;
					default:
						if(ch<' ') {
							if(toPrint>0) {
								out.append(S, c-toPrint, c);
								toPrint=0;
							}
							// skip the character
						} else {
							toPrint++;
						}
				}
			}
			if(toPrint>0) out.append(S, end-toPrint, end);
		}
	}

	/**
	 * @param  value  the string to be escaped.
	 *
	 * @return if value is null then null otherwise value escaped
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static String encodeHtml(Object value, boolean make_br, boolean make_nbsp) throws IOException {
		if(value==null) return null;
		StringBuilder result = new StringBuilder();
		encodeHtml(value, make_br, make_nbsp, result);
		return result.toString();
	}

	/**
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * <p>
	 * See <a href="https://aoindustries.com/ao-encoding/apidocs/com/aoindustries/encoding/TextInXhtmlEncoder.html">TextInXhtmlEncoder</a>
	 * </p>
	 */
	@Deprecated
	public static void encodeHtml(char ch, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
		switch(ch) {
			// Standard XML escapes
			case '<':
				out.append("&lt;");
				break;
			case '>':
				out.append("&gt;");
				break;
			case '&':
				out.append("&amp;");
				break;
			// Special (X)HTML options
			case ' ':
				if(make_nbsp) {
					out.append("&#160;");
				} else {
					out.append(' ');
				}
				break;
			case '\t':
				out.append("&#x9;");
				break;
			case '\r':
				// skip '\r'
				break;
			case '\n':
				if(make_br) {
					out.append(BR_EOL);
				} else {
					out.append('\n');
				}
				break;
			default:
				if(ch<' ') {
					// skip the character
				} else {
					out.append(ch);
				}
		}
	}
	// </editor-fold>
}
