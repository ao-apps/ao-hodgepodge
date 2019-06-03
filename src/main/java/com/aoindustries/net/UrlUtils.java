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
 * TODO: Rename to EncodingUtils once com.aoindustries.util.EncodingUtils has been eliminated.
 *
 * @author  AO Industries, Inc.
 */
public class UrlUtils {

	private UrlUtils() {
	}

	private static final char[] noEncodeCharacters = {
		'?', ':', '/', ';', '#', '+'
	};

	/**
	 * Encodes the URL up to trailing '?' or '#' (the first found of the two).
	 * To avoid ambiguity, parameters and anchors must have been correctly encoded by the caller.
	 * Does not encode any characters in the set { '?', ':', '/', ';', '#', '+' }.
	 *
	 * Encodes tel: (case-sensitive) urls by relacing spaces with hyphens.
	 *
	 * @see  #decodeUrlPath(java.lang.String, java.lang.String)
	 */
	public static String encodeUrlPath(String href, String encoding) throws UnsupportedEncodingException {
		if(href.startsWith("tel:")) return href.replace(' ', '-');
		int len = href.length();
		int pos = 0;
		int stopAt;
		{
			int anchorAt = href.lastIndexOf('#');
			int paramsAt = href.lastIndexOf('?', (anchorAt==-1 ? len : anchorAt) - 1);
			if(paramsAt == -1) {
				stopAt = anchorAt == -1 ? len : anchorAt;
			} else {
				stopAt = paramsAt;
			}
		}
		StringBuilder SB = new StringBuilder(href.length()*2); // Leave a little room for encoding
		while(pos < stopAt) {
			int nextPos = StringUtility.indexOf(href, noEncodeCharacters, pos);
			if(nextPos == -1) {
				SB.append(URLEncoder.encode(href.substring(pos, stopAt), encoding));
				pos = len;
			} else {
				if(nextPos > stopAt) nextPos = stopAt;
				SB.append(URLEncoder.encode(href.substring(pos, nextPos), encoding));
				if(nextPos < stopAt) {
					char nextChar = href.charAt(nextPos++);
					SB.append(nextChar);
				}
				pos = nextPos;
			}
		}
		SB.append(href, stopAt, len);
		return SB.toString();
	}

	/**
	 * Decodes the URL up to the first ?, if present.  Does not decode
	 * any characters in the set { '?', ':', '/', ';', '#', '+' }.
	 *
	 * Does not decode tel: urls (case-sensitive).
	 * 
	 * @see  #encodeUrlPath(java.lang.String, java.lang.String)
	 */
	public static String decodeUrlPath(String href, String encoding) throws UnsupportedEncodingException {
		if(href.startsWith("tel:")) return href;
		int len = href.length();
		int pos = 0;
		StringBuilder SB = new StringBuilder(href.length()*2); // Leave a little room for encoding
		while(pos<len) {
			int nextPos = StringUtility.indexOf(href, noEncodeCharacters, pos);
			if(nextPos==-1) {
				SB.append(URLDecoder.decode(href.substring(pos, len), encoding));
				pos = len;
			} else {
				SB.append(URLDecoder.decode(href.substring(pos, nextPos), encoding));
				char nextChar = href.charAt(nextPos);
				if(nextChar=='?') {
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
	 *   <li>Encoding any non-ASCII characters in the URL path</li>
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
