/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015, 2016, 2019  AO Industries, Inc.
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
package com.aoindustries.net;

import com.aoindustries.io.Encoder;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * URL helper utilities.
 * <p>
 * TODO: Have variants of encode (and possibly decode) that accepts and object
 * and works similarly (streaming) to Coercion.  This class might have to
 * move to ao-encoding to do so.  Might also become a full streaming
 * implementation, then.
 * </p>
 * <p>
 * TODO: Find something that does this well already. 
 * <a href="https://jena.apache.org/documentation/notes/iri.html">jena-iri</a>?
 * <a href="https://github.com/xbib/net>org.xbib:net-url</a>?
 * <a href="https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/org/apache/http/client/utils/URIBuilder.html">URIBuilder</a>?
 * </p>
 * 
 * @see SplitUrl
 * @see UriComponent
 *
 * @author  AO Industries, Inc.
 */
public class UrlUtils {

	private UrlUtils() {
	}

	/**
	 * The default encoding is <code>{@link StandardCharsets#UTF_8}</code> per
	 * <a href="https://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">B.2.1 Non-ASCII characters in URI attribute values</a>.
	 */
	public static final Charset ENCODING = StandardCharsets.UTF_8;

	/**
	 * The characters defined in <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 */
	private static final char[] rfc3986ReservedCharacters = {
		// gen-delims
		':', '/', '?', '#', '[', ']', '@',
		// sub-delims
		'!', '$', '&', '\'', '(',  ')',
		'*', '+', ',', ';', '='
	};

	/**
	 * The characters defined in <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (and '%' for already percent-encoded).
	 */
	private static final char[] rfc3986ReservedCharacters_and_percent = {
		// gen-delims
		':', '/', '?', '#', '[', ']', '@',
		// sub-delims
		'!', '$', '&', '\'', '(',  ')',
		'*', '+', ',', ';', '=',
		// already percent-encoded
		'%'
	};

	/**
	 * Checks if a URI starts with the given scheme.
	 *
	 * @param scheme  The scheme to look for, not including colon.
	 *                For example {@code "http"}.
	 *
	 * @throws IllegalArgumentException when {@code scheme} is determined to be invalid.
	 *         Please note that this determination is not guaranteed as shortcuts may
	 *         skip individual character comparisons.
	 */
	// TODO: Redundant with SplitUrl.isScheme
	public static boolean isScheme(String uri, String scheme) throws IllegalArgumentException {
		if(uri == null) return false;
		int len = scheme.length();
		if(len == 0) {
			throw new IllegalArgumentException("Invalid scheme: " + scheme);
		}
		if((len + 1) > uri.length()) return false;
		for(int i = 0; i < len; i++) {
			char ch1 = scheme.charAt(i);
			boolean isValid = (i == 0) ? RFC3986.isSchemeBeginning(ch1) : RFC3986.isSchemeRemaining(ch1);
			if(!isValid) {
				throw new IllegalArgumentException("Invalid scheme: " + scheme);
			}
			char ch2 = uri.charAt(i);
			// Convert to lower-case, ASCII-only
			ch1 = RFC3986.normalizeScheme(ch1);
			ch2 = RFC3986.normalizeScheme(ch2);
			if(ch1 != ch2) return false;
		}
		// Must be followed by a colon
		return uri.charAt(len) == ':';
	}

	/**
	 * Checks if a URI has a scheme, not including any empty scheme (starts with ':')
	 */
	public static boolean hasScheme(String uri) {
		if(uri == null) return false;
		int len = uri.length();
		if(len == 0) return false;
		// First character
		if(!RFC3986.isSchemeBeginning(uri.charAt(0))) return false;
		// Remaining characters
		for(int i = 1; i < len; i++) {
			char ch = uri.charAt(i);
			if(ch == ':') {
				return true;
			} else if(!RFC3986.isSchemeRemaining(ch)) {
				return false;
			}
		}
		// No colon found
		return false;
	}

	/**
	 * Gets the scheme for a URI, or {@code null} when no scheme found.
	 * The scheme must start the URI, and match {@code ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )}
	 * before the first colon (:) found.  The scheme is normalized to lower-case.
	 * An empty scheme will never be returned (if the URI starts with ':').
	 *
	 * @return  The scheme, not including colon, or {@code null} when not found.
	 *          For example {@code "http"}.
	 */
	// TODO: Redundant with SplitUrl.getScheme except SplitUrl doesn't normalize
	public static final String getScheme(String uri) {
		if(uri == null) return null;
		int len = uri.length();
		if(len == 0) return null;
		// First character
		if(!RFC3986.isSchemeBeginning(uri.charAt(0))) return null;

		// Find the colon, returning null if any non-A-Z,a-z is found on the way
		int colonPos = -1;
		for(int i = 1; i < len; i++) {
			char ch = uri.charAt(i);
			if(ch == ':') {
				colonPos = i;
				break;
			} else if(!RFC3986.isSchemeRemaining(ch)) {
				return null;
			}
		}
		// No colon found
		if(colonPos == -1) return null;
		// No empty scheme
		assert colonPos != 0 : "No empty scheme";
		// Normalize to lower-case
		char[] scheme = new char[colonPos];
		for(int i = 0; i < colonPos; i++) {
			// Convert to lower-case, ASCII-only
			scheme[i] = RFC3986.normalizeScheme(uri.charAt(i));
		}
		return String.valueOf(scheme);
	}

	/**
	 * Finds the path end within a URI.  The path end is the index of the first '?' or '#', or the length of the URI
	 * when neither found.
	 */
	public static int getPathEnd(String uri) {
		int foundAt = StringUtility.indexOf(uri, new char[] {'?', '#'});
		return foundAt != -1 ? foundAt : uri.length();
	}

	/**
	 * @deprecated  Please use {@link com.aoindustries.servlet.http.ServletUtil#buildUrl(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String, com.aoindustries.net.HttpParameters, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen)} directly.
	 */
	@Deprecated
	public static String buildUrl(
		javax.servlet.ServletContext servletContext,
		javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response,
		String url,
		HttpParameters params,
		boolean urlAbsolute,
		com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException {
		return com.aoindustries.servlet.http.ServletUtil.buildUrl(servletContext, request, response, url, params, urlAbsolute, addLastModified);
	}

	/**
	 * @deprecated  Please use {@link com.aoindustries.servlet.http.ServletUtil#buildUrl(javax.servlet.jsp.PageContext, java.lang.String, com.aoindustries.net.HttpParameters, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen)} directly.
	 */
	@Deprecated
	public static String buildUrl(
		javax.servlet.jsp.PageContext pageContext,
		String url,
		HttpParameters params,
		boolean urlAbsolute,
		com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException {
		return com.aoindustries.servlet.http.ServletUtil.buildUrl(pageContext, url, params, urlAbsolute, addLastModified);
	}

	/**
	 * @deprecated  Please use {@link com.aoindustries.servlet.http.ServletUtil#buildUrl(javax.servlet.jsp.JspContext, java.lang.String, com.aoindustries.net.HttpParameters, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen)} directly.
	 */
	@Deprecated
	public static String buildUrl(
		javax.servlet.jsp.JspContext jspContext,
		String url,
		HttpParameters params,
		boolean srcAbsolute,
		com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException {
		return com.aoindustries.servlet.http.ServletUtil.buildUrl(jspContext, url, params, srcAbsolute, addLastModified);
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String)
	 */
	public static String encodeURIComponent(String s, String encoding) throws UnsupportedEncodingException {
		return (s == null) ? null : StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20");
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #decodeURIComponent(java.lang.String)
	 */
	public static String encodeURIComponent(String s) {
		try {
			return encodeURIComponent(s, ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURIComponent(String s, String encoding, Appendable out) throws UnsupportedEncodingException, IOException {
		if(s != null) StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", out);
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURIComponent(String s, Appendable out) throws IOException {
		try {
			encodeURIComponent(s, ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURIComponent(String s, String encoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(s != null) {
			if(encoder == null) {
				encodeURIComponent(s, encoding, out);
			} else {
				StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", out, encoder);
			}
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURIComponent(String s, Appendable out, Encoder encoder) throws IOException {
		try {
			encodeURIComponent(s, ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURIComponent(String s, String encoding, StringBuilder sb) throws UnsupportedEncodingException {
		if(s != null) {
			try {
				StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", sb);
			} catch(UnsupportedEncodingException e) {
				throw e;
			} catch(IOException e) {
				throw new AssertionError("IOException should not occur on StringBuilder", e);
			}
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURIComponent(String s, StringBuilder sb) {
		try {
			encodeURIComponent(s, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURIComponent(String s, String encoding, StringBuffer sb) throws UnsupportedEncodingException {
		if(s != null) {
			try {
				StringUtility.replace(URLEncoder.encode(s, encoding), '+', "%20", sb);
			} catch(UnsupportedEncodingException e) {
				throw e;
			} catch(IOException e) {
				throw new AssertionError("IOException should not occur on StringBuffer", e);
			}
		}
	}

	/**
	 * Encodes a value for use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLEncoder#encode(java.lang.String, java.lang.String)} then replaces
	 * '+' with "%20".
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent">encodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #decodeURIComponent(java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURIComponent(String s, StringBuffer sb) {
		try {
			encodeURIComponent(s, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String)
	 */
	public static String decodeURIComponent(String s, String encoding) throws UnsupportedEncodingException {
		return (s == null) ? null : URLDecoder.decode(s, encoding);
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String)
	 */
	public static String decodeURIComponent(String s) {
		try {
			return decodeURIComponent(s, ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURIComponent(String s, String encoding, Appendable out) throws UnsupportedEncodingException, IOException {
		if(s != null) out.append(URLDecoder.decode(s, encoding));
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURIComponent(String s, Appendable out) throws IOException {
		try {
			decodeURIComponent(s, ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURIComponent(String s, String encoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(s != null) {
			if(encoder == null) {
				decodeURIComponent(s, encoding, out);
			} else {
				encoder.append(URLDecoder.decode(s, encoding), out);
			}
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURIComponent(String s, Appendable out, Encoder encoder) throws IOException {
		try {
			decodeURIComponent(s, ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURIComponent(String s, String encoding, StringBuilder sb) throws UnsupportedEncodingException {
		if(s != null) sb.append(URLDecoder.decode(s, encoding));
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURIComponent(String s, StringBuilder sb) {
		try {
			decodeURIComponent(s, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a value from its use in a path component or fragment in a given encoding.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoding  The name of a supported {@linkplain Charset character encoding}.
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURIComponent(String s, String encoding, StringBuffer sb) throws UnsupportedEncodingException {
		if(s != null) sb.append(URLDecoder.decode(s, encoding));
	}

	/**
	 * Decodes a value from its use in a path component or fragment in the default encoding <code>{@link #ENCODING}</code>.
	 * <p>
	 * This uses {@link URLDecoder#decode(java.lang.String, java.lang.String)}.
	 * </p>
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURIComponent">decodeURIComponent() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURIComponent(java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURIComponent(String s, StringBuffer sb) {
		try {
			decodeURIComponent(s, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (and '%' for already percent-encoded).
	 * </p>
	 * <p>
	 * Additionally, for <code>tel:</code> (case-insensitive) scheme, transforms spaces to hyphens.
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @return  The encoded URI or {@code url} when not modified
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String)
	 *
	 * @deprecated  Please perform any <code>tel:</code> transformations elsewhere and use {@link #encodeURI(java.lang.String, java.lang.String)} directly.
	 */
	@Deprecated
	public static String encodeUrlPath(String uri, String documentEncoding) throws UnsupportedEncodingException {
		if(uri == null) return null;
		if(isScheme(uri, "tel")) {
			uri = uri.replace(' ', '-');
		}
		return encodeURI(uri, documentEncoding);
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (and '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @return  The encoded URI or {@code url} when not modified
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String)
	 */
	public static String encodeURI(String uri, String documentEncoding) throws UnsupportedEncodingException {
		if(uri == null) return null;
		StringBuilder sb = new StringBuilder(uri.length() + 16);
		encodeURI(uri, documentEncoding, sb);
		if(sb.length() == uri.length()) {
			assert uri.equals(sb.toString());
			return uri;
		} else {
			return sb.toString();
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (and '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @return  The encoded URI or {@code url} when not modified
	 *
	 * @see #decodeURI(java.lang.String)
	 */
	public static String encodeURI(String uri) {
		try {
			return encodeURI(uri, ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURI(String uri, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException {
		encodeURI(uri, documentEncoding, out, null);
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #decodeURI(java.lang.String, java.lang.Appendable)
	 */
	public static void encodeURI(String uri, Appendable out) throws IOException {
		try {
			encodeURI(uri, ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 * <p>
	 * TODO: Support <a href="https://tools.ietf.org/html/rfc2368">mailto:</a> scheme specifically?
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURI(String uri, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(uri != null) {
			int len = uri.length();
			int pos = 0;
			UriComponent stage = UriComponent.BASE;
			while(pos < len) {
				int nextPos = StringUtility.indexOf(uri, rfc3986ReservedCharacters_and_percent, pos);
				if(nextPos == -1) {
					stage.encodeUnreserved(uri, pos, len, documentEncoding, out, encoder);
					pos = len;
				} else {
					if(nextPos != pos) {
						stage.encodeUnreserved(uri, pos, nextPos, documentEncoding, out, encoder);
					}
					char reserved = uri.charAt(nextPos++);
					stage = stage.nextStage(reserved);
					if(encoder == null) {
						out.append(reserved);
					} else {
						encoder.append(reserved, out);
					}
					pos = nextPos;
				}
			}
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #decodeURI(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void encodeURI(String uri, Appendable out, Encoder encoder) throws IOException {
		try {
			encodeURI(uri, ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURI(String uri, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException {
		try {
			encodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuilder", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #decodeURI(java.lang.String, java.lang.StringBuilder)
	 */
	public static void encodeURI(String uri, StringBuilder sb) {
		try {
			encodeURI(uri, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in a given encoding.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #decodeURI(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURI(String uri, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException {
		try {
			encodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuffer", e);
		}
	}

	/**
	 * Encodes a URI to <a href="https://tools.ietf.org/html/rfc3986">RFC 3986 ASCII format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Encodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * (or '%' for already percent-encoded).
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURI">encodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #decodeURI(java.lang.String, java.lang.StringBuffer)
	 */
	public static void encodeURI(String uri, StringBuffer sb) {
		try {
			encodeURI(uri, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @deprecated  Please use {@link #decodeURI(java.lang.String, java.lang.String)} directly.
	 */
	@Deprecated
	public static String decodeUrlPath(String uri, String documentEncoding) throws UnsupportedEncodingException {
		return decodeURI(uri, documentEncoding);
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @return  The decoded URI or {@code url} when not modified
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String)
	 */
	public static String decodeURI(String uri, String documentEncoding) throws UnsupportedEncodingException {
		if(uri == null) return null;
		StringBuilder sb = new StringBuilder(uri.length());
		decodeURI(uri, documentEncoding, sb);
		if(sb.length() == uri.length()) {
			assert uri.equals(sb.toString());
			return uri;
		} else {
			return sb.toString();
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @return  The decoded URI or {@code url} when not modified
	 *
	 * @see #encodeURI(java.lang.String)
	 */
	public static String decodeURI(String uri) {
		try {
			return decodeURI(uri, ENCODING.name());
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURI(String uri, String documentEncoding, Appendable out) throws UnsupportedEncodingException, IOException {
		decodeURI(uri, documentEncoding, out, null);
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURI(java.lang.String, java.lang.Appendable)
	 */
	public static void decodeURI(String uri, Appendable out) throws IOException {
		try {
			decodeURI(uri, ENCODING.name(), out);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 * <p>
	 * TODO: Support <a href="https://tools.ietf.org/html/rfc2368">mailto:</a> scheme specifically?
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURI(String uri, String documentEncoding, Appendable out, Encoder encoder) throws UnsupportedEncodingException, IOException {
		if(uri != null) {
			int len = uri.length();
			int pos = 0;
			UriComponent stage = UriComponent.BASE;
			while(pos < len) {
				int nextPos = StringUtility.indexOf(uri, rfc3986ReservedCharacters, pos);
				if(nextPos == -1) {
					stage.decodeUnreserved(uri, pos, len, documentEncoding, out, encoder);
					pos = len;
				} else {
					if(nextPos != pos) {
						stage.decodeUnreserved(uri, pos, nextPos, documentEncoding, out, encoder);
					}
					char reserved = uri.charAt(nextPos++);
					stage = stage.nextStage(reserved);
					if(encoder == null) {
						out.append(reserved);
					} else {
						encoder.append(reserved, out);
					}
					pos = nextPos;
				}
			}
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param encoder  An optional encoder the output is applied through
	 *
	 * @see #encodeURI(java.lang.String, java.lang.Appendable, com.aoindustries.io.Encoder)
	 */
	public static void decodeURI(String uri, Appendable out, Encoder encoder) throws IOException {
		try {
			decodeURI(uri, ENCODING.name(), out, encoder);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURI(String uri, String documentEncoding, StringBuilder sb) throws UnsupportedEncodingException {
		try {
			decodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuilder", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURI(java.lang.String, java.lang.StringBuilder)
	 */
	public static void decodeURI(String uri, StringBuilder sb) {
		try {
			decodeURI(uri, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in a given encoding.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @param documentEncoding  The name of a supported {@linkplain Charset character encoding}, only used for the query.
	 *                          When any encoding other than {@link StandardCharsets#UTF_8},
	 *                          the query string is left unaltered.
	 *
	 * @see #encodeURI(java.lang.String, java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURI(String uri, String documentEncoding, StringBuffer sb) throws UnsupportedEncodingException {
		try {
			decodeURI(uri, documentEncoding, sb, null);
		} catch(UnsupportedEncodingException e) {
			throw e;
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuffer", e);
		}
	}

	/**
	 * Decodes a URI to <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 Unicode format</a> in the default encoding <code>{@link #ENCODING}</code>.
	 * Decodes the characters in the URI, not including any characters defined in
	 * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * Furthermore, characters that would decode to a reserved character are left percent-encoded to avoid ambiguity.
	 * <p>
	 * See <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/decodeURI">decodeURI() - JavaScript | MDN</a>
	 * </p>
	 *
	 * @see #encodeURI(java.lang.String, java.lang.StringBuffer)
	 */
	public static void decodeURI(String uri, StringBuffer sb) {
		try {
			decodeURI(uri, ENCODING.name(), sb);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("Standard encoding (" + ENCODING + ") should always exist", e);
		}
	}
}
