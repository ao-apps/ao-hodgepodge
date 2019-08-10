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

import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

/**
 * Encoding helper utilities.
 *
 * @author  AO Industries, Inc.
 */
public class UrlUtils {

	private UrlUtils() {
	}

	/**
	 * Checks if a URL starts with the given scheme.
	 *
	 * @param scheme  The scheme to look for, not including colon.
	 *                For example {@code "http"}.
	 */
	public static boolean isScheme(String href, String scheme) {
		int len = scheme.length();
		if((len + 1) > href.length()) return false;
		for(int i = 0; i < len; i++) {
			char ch1 = href.charAt(i);
			char ch2 = href.charAt(i);
			// Convert to lower-case, ASCII-only
			if(ch1 >= 'A' && ch1 <= 'Z') ch1 += 'a' - 'A';
			if(ch2 >= 'A' && ch2 <= 'Z') ch2 += 'a' - 'A';
			if(ch1 != ch2) return false;
		}
		// Must be followed by a colon
		return href.charAt(len) == ':';
	}

	/**
	 * Gets the scheme for a URL, or {@code null} when no scheme found.
	 * The scheme must start the URL, and contain only (A-Z, a-z) before the first colon (:)
	 * found.  The scheme is normalized to lower-case.  An empty scheme will not be returned.
	 *
	 * @return  The scheme, not including colon, or {@code null} when not found.
	 *          For example {@code "http"}.
	 */
	public static final String getScheme(String href) {
		int len = href.length();
		// Find the colon, returning null if any non-A-Z,a-z is found on the way
		int colonPos = -1;
		for(int i = 0; i < len; i++) {
			char ch = href.charAt(i);
			if(ch == ':') {
				colonPos = i;
				break;
			} else if(
				(ch < 'a' || ch > 'z')
				&& (ch < 'A' || ch > 'Z')
			) {
				return null;
			}
		}
		// Empty scheme
		if(colonPos == 0) return null;
		// Normalize to lower-case
		char[] scheme = new char[colonPos];
		for(int i = 0; i < colonPos; i++) {
			char ch = href.charAt(i);
			// Convert to lower-case, ASCII-only
			if(ch >= 'A' && ch <= 'Z') {
				ch += 'a' - 'A';
			} else {
				assert ch >= 'a' && ch <= 'z';
			}
			scheme[i] = ch;
		}
		return String.valueOf(scheme);
	}

	/**
	 * The characters defined in <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 *
	 * @deprecated  Only used by {@link #decodeUrlPath(java.lang.String, java.lang.String)}
	 */
	@Deprecated
	private static final char[] rfc3986ReservedCharacters = {
		// gen-delims
		':', '/', '?', '#', '[', ']', '@',
		// sub-delims
		'!', '$', '&', '\'', '(',  ')',
		'*', '+', ',', ';', '='
	};

	/**
	 * The characters defined in <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * along with a percent (%)
	 */
	private static final char[] rfc3986ReservedCharacters_and_percent;
	static {
		rfc3986ReservedCharacters_and_percent = new char[rfc3986ReservedCharacters.length + 1];
		System.arraycopy(rfc3986ReservedCharacters, 0, rfc3986ReservedCharacters_and_percent, 0, rfc3986ReservedCharacters.length);
		// percent-encoded itself
		rfc3986ReservedCharacters_and_percent[rfc3986ReservedCharacters.length] = '%';
	};

	/**
	 * Encodes the characters in the URL up to trailing '?' or '#' (the first found of the two),
	 * not including any characters defined in <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * To avoid ambiguity, parameters and anchors must have been correctly encoded by the caller.
	 * </p>
	 * <p>
	 * Additionally, for <code>tel:</code> (case-insensitive) urls, replaces spaces (and non-breaking spaces) with hyphens.
	 * </p>
	 *
	 * @see  #decodeUrlPath(java.lang.String, java.lang.String)
	 */
	public static String encodeUrlPath(String href, String encoding) throws UnsupportedEncodingException {
		if(isScheme(href, "tel")) {
			href = href.replace(' ', '-');
			href = href.replace('\u00A0', '-'); // non-breaking space
		}
		int len = href.length();
		int pos = 0;
		int stopAt;
		{
			int paramsAt = href.indexOf('?');
			if(paramsAt != -1) {
				stopAt = paramsAt;
			} else {
				int anchorAt = href.indexOf('#');
				if(anchorAt != -1) {
					stopAt = anchorAt;
				} else {
					stopAt = len;
				}
			}
		}
		try {
			StringBuilder SB = new StringBuilder(href.length() + 16);
			while(pos < stopAt) {
				int nextPos = StringUtility.indexOf(href, rfc3986ReservedCharacters_and_percent, pos);
				if(nextPos == -1) {
					StringUtility.replace(URLEncoder.encode(href.substring(pos, stopAt), encoding), "+", "%20", SB);
					pos = len;
				} else {
					if(nextPos > stopAt) nextPos = stopAt;
					if(nextPos != pos) {
						StringUtility.replace(URLEncoder.encode(href.substring(pos, nextPos), encoding), "+", "%20", SB);
					}
					if(nextPos < stopAt) {
						SB.append(href.charAt(nextPos++));
					}
					pos = nextPos;
				}
			}
			SB.append(href, stopAt, len);
			return SB.toString();
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuilder", e);
		}
	}

	/**
	 * Percent-encodes reserved characters (or percent) only.
	 */
	private static void encodeRfc3968ReservedCharacters_or_percent(String value, String encoding, StringBuilder SB) {
		int len = value.length();
		for(int i = 0; i < len; i++) {
			char ch = value.charAt(i);
			switch(ch) {
				// gen-delims
				case ':' :
					SB.append("%3A");
					break;
				case '/' :
					SB.append("%2F");
					break;
				case '?' :
					SB.append("%3F");
					break;
				case '#' :
					SB.append("%23");
					break;
				case '[' :
					SB.append("%5B");
					break;
				case ']' :
					SB.append("%5D");
					break;
				case '@' :
					SB.append("%40");
					break;
				// sub-delims
				case '!' :
					SB.append("%21");
					break;
				case '$' :
					SB.append("%24");
					break;
				case '&' :
					SB.append("%26");
					break;
				case '\'' :
					SB.append("%27");
					break;
				case '(' :
					SB.append("%28");
					break;
				case ')' :
					SB.append("%29");
					break;
				case '*' :
					SB.append("%2A");
					break;
				case '+' :
					SB.append("%2B");
					break;
				case ',' :
					SB.append("%2C");
					break;
				case ';' :
					SB.append("%3B");
					break;
				case '=' :
					SB.append("%3D");
					break;
				// percent-encoded itself
				case '%' :
					SB.append("%25");
					break;
				default :
					SB.append(ch);
			}
		}
	}

	/**
	 * Decodes the URL up to the first ?, if present.
	 * Does not decode any characters defined in <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>.
	 * <p>
	 * Characters that are percent-decoded into a reserve character are left percent-encoded to avoid ambiguity.
	 * </p>
	 *
	 * @see  #encodeUrlPath(java.lang.String, java.lang.String)
	 */
	public static String decodeUrlPath(String href, String encoding) throws UnsupportedEncodingException {
		int len = href.length();
		int pos = 0;
		StringBuilder SB = new StringBuilder(href.length());
		while(pos < len) {
			int nextPos = StringUtility.indexOf(href, rfc3986ReservedCharacters, pos);
			if(nextPos == -1) {
				// TODO: A specialized form of decode that skips decoding to reserved characters would be better than decode/re-encode.
				//       This implementation is less precise, such as converting lower-case percent-encoded to upper-case.
				encodeRfc3968ReservedCharacters_or_percent(URLDecoder.decode(href.substring(pos, len), encoding), encoding, SB);
				pos = len;
			} else {
				// TODO: A specialized form of decode that skips decoding to reserved characters would be better than decode/re-encode.
				//       This implementation is less precise, such as converting lower-case percent-encoded to upper-case.
				encodeRfc3968ReservedCharacters_or_percent(URLDecoder.decode(href.substring(pos, nextPos), encoding), encoding, SB);
				char nextChar = href.charAt(nextPos);
				if(nextChar == '?') {
					// End decoding
					SB.append(href, nextPos, len);
					pos = len;
				} else {
					SB.append(nextChar);
					pos = nextPos+1;
				}
			}
		}
		return SB.toString();
	}

	/**
	 * Performs all the proper URL conversions along with optionally adding a lastModified parameter.
	 * This includes:
	 * <ol>
	 *   <li>Converting any page-relative path to a context-relative path starting with a slash (/)</li>
	 *   <li>Adding any additional parameters</li>
	 *   <li>Optionally adding lastModified parameter</li>
	 *   <li>Converting any context-relative path to a site-relative path by prefixing contextPath</li>
	 *   <li>Encoding any URL path characters not defined in <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a></li>
	 *   <li>Rewrite with response.encodeURL</li>
	 * </ol>
	 */
	public static String buildUrl(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		String href,
		HttpParameters params,
		boolean hrefAbsolute,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException, UnsupportedEncodingException {
		String responseEncoding = response.getCharacterEncoding();
		String servletPath = Dispatcher.getCurrentPagePath(request);
		href = ServletUtil.getAbsolutePath(servletPath, href);
		href = HttpParametersUtils.addParams(href, params, responseEncoding);
		href = LastModifiedServlet.addLastModified(servletContext, request, servletPath, href, addLastModified);
		if(!hrefAbsolute && href.startsWith("/")) {
			String contextPath = request.getContextPath();
			if(contextPath.length()>0) href = contextPath + href;
		}
		href = encodeUrlPath(href, responseEncoding);
		href= response.encodeURL(href);
		if(hrefAbsolute && href.startsWith("/")) href = ServletUtil.getAbsoluteURL(request, href);
		return href;
	}

	/**
	 * @see  #buildUrl(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String, com.aoindustries.net.HttpParameters, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen)
	 */
	public static String buildUrl(
		PageContext pageContext,
		String href,
		HttpParameters params,
		boolean hrefAbsolute,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException, UnsupportedEncodingException {
		return buildUrl(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			(HttpServletResponse)pageContext.getResponse(),
			href,
			params,
			hrefAbsolute,
			addLastModified
		);
	}

	/**
	 * @see  #buildUrl(javax.servlet.jsp.PageContext, java.lang.String, com.aoindustries.net.HttpParameters, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen)
	 */
	public static String buildUrl(
		JspContext jspContext,
		String src,
		HttpParameters params,
		boolean srcAbsolute,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException, UnsupportedEncodingException {
		return buildUrl(
			(PageContext)jspContext,
			src,
			params,
			srcAbsolute,
			addLastModified
		);
	}
}
