/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016  AO Industries, Inc.
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
package com.aoindustries.servlet.http;

import com.aoindustries.io.Encoder;
import com.aoindustries.lang.NullArgumentException;
import com.aoindustries.net.UrlUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Static utilities that may be useful by servlet/JSP/taglib environments.
 *
 * @author  AO Industries, Inc.
 */
public class ServletUtil {

	private ServletUtil() {
	}

	private static final String DEFAULT_REQUEST_ENCODING = "ISO-8859-1";

	/**
	 * Gets the request encoding or ISO-8859-1 when not available.
	 */
	public static String getRequestEncoding(ServletRequest request) {
		String requestEncoding = request.getCharacterEncoding();
		return requestEncoding != null ? requestEncoding : DEFAULT_REQUEST_ENCODING;
	}

	/**
	 * Converts a possibly-relative path to a context-relative absolute path.
	 * Resolves ./ and ../ at the beginning of the URL but not in the middle of the URL.
	 * If the URL begins with http:, https:, file:, mailto:, telnet:, tel:, or cid:, it is not altered.
	 * 
	 * @param  servletPath  Required when path might be altered.
	 */
	public static String getAbsolutePath(String servletPath, String relativeUrlPath) throws MalformedURLException {
		char firstChar;
		if(
			relativeUrlPath.length() > 0
			&& (firstChar=relativeUrlPath.charAt(0)) != '/'
			&& firstChar != '#' // Skip anchor-only paths
			&& !relativeUrlPath.startsWith("http:")
			&& !relativeUrlPath.startsWith("https:")
			&& !relativeUrlPath.startsWith("file:")
			&& !relativeUrlPath.startsWith("mailto:")
			&& !relativeUrlPath.startsWith("telnet:")
			&& !relativeUrlPath.startsWith("tel:")
			&& !relativeUrlPath.startsWith("cid:")
		) {
			NullArgumentException.checkNotNull(servletPath, "servletPath");
			int slashPos = servletPath.lastIndexOf('/');
			if(slashPos==-1) throw new MalformedURLException("No slash found in servlet path: "+servletPath);
			final String newPath = relativeUrlPath;
			final int newPathLen = newPath.length();
			int newPathStart = 0;
			boolean modified;
			do {
				modified = false;
				if(
					newPathLen >= (newPathStart+2)
					&& newPath.regionMatches(newPathStart, "./", 0, 2)
				) {
					newPathStart += 2;
					modified = true;
				}
				if(
					newPathLen >= (newPathStart+3)
					&& newPath.regionMatches(newPathStart, "../", 0, 3)
				) {
					slashPos = servletPath.lastIndexOf('/', slashPos-1);
					if(slashPos==-1) throw new MalformedURLException("Too many ../ in relativeUrlPath: "+relativeUrlPath);

					newPathStart += 3;
					modified = true;
				}
			} while(modified);
			relativeUrlPath =
				new StringBuilder((slashPos+1) + (newPathLen-newPathStart))
				.append(servletPath, 0, slashPos+1)
				.append(newPath, newPathStart, newPathLen)
				.toString();
		}
		return relativeUrlPath;
	}

	/**
	 * @see  #getAbsolutePath(java.lang.String, java.lang.String)
	 */
	public static String getAbsolutePath(HttpServletRequest request, String path) throws MalformedURLException {
		return getAbsolutePath(request.getServletPath(), path);
	}

	/**
	 * Gets the URL for the provided absolute path or <code>null</code> if no resource
	 * is mapped to the path.
	 */
	public static URL getResource(ServletContext servletContext, String path) throws MalformedURLException {
		return servletContext.getResource(path);
	}

	/**
	 * Gets the URL for the provided possibly-relative path or <code>null</code> if no resource
	 * is mapped to the path.
	 */
	public static URL getResource(ServletContext servletContext, HttpServletRequest request, String relativeUrlPath) throws MalformedURLException {
		return servletContext.getResource(getAbsolutePath(request, relativeUrlPath));
	}

	/**
	 * Checks if a resource with the possibly-relative path exists.
	 */
	public static boolean resourceExists(ServletContext servletContext, String path) throws MalformedURLException {
		return getResource(servletContext, path)!=null;
	}

	/**
	 * Checks if a resource with the possibly-relative path exists.
	 */
	public static boolean resourceExists(ServletContext servletContext, HttpServletRequest request, String relativeUrlPath) throws MalformedURLException {
		return getResource(servletContext, request, relativeUrlPath)!=null;
	}

	/**
	 * Determines if the requestor is Googlebot as described at:
	 * http://www.google.com/support/webmasters/bin/answer.py?answer=80553
	 */
	public static boolean isGooglebot(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Enumeration<String> headers = request.getHeaders("User-Agent");
		while(headers.hasMoreElements()) {
			String userAgent = headers.nextElement();
			if(userAgent.contains("Googlebot")) {
				// Verify through reverse then forward DNS lookups
				String remoteAddr = request.getRemoteAddr();
				String remoteHost = request.getRemoteHost();
				try {
					InetAddress remoteIp = InetAddress.getByName(remoteAddr);
					// Do reverse lookup if container didn't do so
					if(remoteAddr.equals(remoteHost)) remoteHost = remoteIp.getCanonicalHostName();
					// Reverse DNS result must be in the googlebot.com domain
					if(remoteHost.endsWith(".googlebot.com") || remoteHost.endsWith(".googlebot.com.")) {
						// Forward DNS must resolve back to the original IP
						for(InetAddress actualIp : InetAddress.getAllByName(remoteHost)) {
							System.out.println("DEBUG: ServletUtil: Googlebot verified: userAgent=\""+userAgent+"\", remoteAddr=\""+remoteAddr+"\", remoteHost=\""+remoteHost+"\"");
							if(actualIp.equals(remoteIp)) return true;
						}
						System.out.println("DEBUG: ServletUtil: Googlebot agent with valid reverse DNS failed forward lookup: userAgent=\""+userAgent+"\", remoteAddr=\""+remoteAddr+"\", remoteHost=\""+remoteHost+"\"");
					}
					System.out.println("DEBUG: ServletUtil: Googlebot agent failed valid reverse DNS lookup: userAgent=\""+userAgent+"\", remoteAddr=\""+remoteAddr+"\", remoteHost=\""+remoteHost+"\"");
				} catch(UnknownHostException exception) {
					// Ignored
					System.out.println("DEBUG: ServletUtil: Googlebot agent verification failed due to exception: userAgent=\""+userAgent+"\", remoteAddr=\""+remoteAddr+"\", remoteHost=\""+remoteHost+"\", exception=\""+exception+"\"");
				}
				break; // Only check the first Googlebot User-Agent header (there should normally only be one anyway)
			}
		}
		return false;
	}

	/**
	 * @see #getAbsoluteURL(javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.Appendable)
	 */
	public static String getAbsoluteURL(HttpServletRequest request, String relPath) {
		try {
			StringBuilder buffer = new StringBuilder();
			getAbsoluteURL(request, relPath, buffer);
			return buffer.toString();
		} catch(IOException e) {
			// Should never get IOException from StringBuilder.
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets an absolute URL for the given context-relative path.  This includes
	 * protocol, port, context path, and relative path.
	 * No URL rewriting is performed.
	 */
	public static void getAbsoluteURL(HttpServletRequest request, String relPath, Appendable out) throws IOException {
		out.append(request.isSecure() ? "https://" : "http://");
		out.append(request.getServerName());
		int port = request.getServerPort();
		if(port!=(request.isSecure() ? 443 : 80)) out.append(':').append(Integer.toString(port));
		out.append(request.getContextPath());
		out.append(relPath);
	}

	/**
	 * Gets an absolute URL for the given context-relative path.  This includes
	 * protocol, port, context path, and relative path.
	 * No URL rewriting is performed.
	 */
	public static void getAbsoluteURL(HttpServletRequest request, String relPath, Encoder encoder, Appendable out) throws IOException {
		if(encoder==null) {
			getAbsoluteURL(request, relPath, out);
		} else {
			encoder.append(request.isSecure() ? "https://" : "http://", out);
			encoder.append(request.getServerName(), out);
			int port = request.getServerPort();
			if(port!=(request.isSecure() ? 443 : 80)) encoder.append(':', out).append(Integer.toString(port), out);
			encoder.append(request.getContextPath(), out);
			encoder.append(relPath, out);
		}
	}

	/**
	 * Gets the absolute URL that should be used for a redirect.
	 * 
	 * @param  href  The absolute, context-relative, or page-relative path to redirect to.
	 *               The following actions are performed on the provided href:
	 *               <ol>
	 *                 <li>Convert page-relative paths to context-relative path, resolving ./ and ../</li>
	 *                 <li>Encode URL path elements (like Japanese filenames)</li>
	 *                 <li>Perform URL rewriting (response.encodeRedirectURL)</li>
	 *                 <li>Convert to absolute URL if needed.  This will also add the context path.</li>
	 *               </ol>
	 *
	 * @see  #sendRedirect(javax.servlet.http.HttpServletResponse, java.lang.String, int)
	 */
	public static String getRedirectLocation(
		HttpServletRequest request,
		HttpServletResponse response,
		String servletPath,
		String href
	) throws MalformedURLException, UnsupportedEncodingException {
		// Convert page-relative paths to context-relative path, resolving ./ and ../
		href = getAbsolutePath(servletPath, href);

		// Encode URL path elements (like Japanese filenames)
		href = UrlUtils.encodeUrlPath(href, response.getCharacterEncoding());

		// Perform URL rewriting
		href = response.encodeRedirectURL(href);

		// Convert to absolute URL if needed.  This will also add the context path.
		if(href.startsWith("/")) href = getAbsoluteURL(request, href);

		return href;
	}

	/**
	 * Sends a redirect to the provided absolute URL location.
	 * 
	 * @see  #getRedirectLocation(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String, java.lang.String) 
	 */
	public static void sendRedirect(
		HttpServletResponse response,
		String location,
		int status
	) throws IllegalStateException, IOException {
		// Response must not be committed
		if(response.isCommitted()) throw new IllegalStateException("Unable to redirect: Response already committed");

		response.setHeader("Location", location);
		response.sendError(status);
	}

	/**
	 * Sends a redirect with relative paths determined from the request servlet path.
	 * 
	 * @see  #getRedirectLocation(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String, java.lang.String)  for transformations applied to the href
	 */
	public static void sendRedirect(
		HttpServletRequest request,
		HttpServletResponse response,
		String href,
		int status
	) throws IllegalStateException, IOException {
		sendRedirect(
			response,
			getRedirectLocation(
				request,
				response,
				request.getServletPath(),
				href
			),
			status
		);
	}

	/**
	 * Gets the current request URI in context-relative form.  The contextPath stripped.
	 */
	public static String getContextRequestUri(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		int cpLen = contextPath.length();
		if(cpLen > 0) {
			assert requestUri.startsWith(contextPath);
			return requestUri.substring(cpLen);
		} else {
			return requestUri;
		}
	}

	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_TRACE = "TRACE";

	public static Method[] getAllDeclaredMethods(Class<?> stopClass, Class<?> c) {
		if (c.equals(stopClass)) {
			return null;
		}
		Method[] parentMethods = getAllDeclaredMethods(stopClass, c.getSuperclass());
		Method[] thisMethods = c.getDeclaredMethods();
		if ((parentMethods != null) && (parentMethods.length > 0)) {
			Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
			System.arraycopy(
				parentMethods, 0,
				allMethods, 0,
				parentMethods.length
			);
			System.arraycopy(
				thisMethods, 0,
				allMethods, parentMethods.length,
				thisMethods.length
			);
			thisMethods = allMethods;
		}
		return thisMethods;
	}

	/**
	 * A reusable doOptions implementation for servlets.
	 */
	public static <S extends HttpServlet> void doOptions(
		HttpServletResponse response,
		Class<S> stopClass,
		Class<? extends S> thisClass,
		String doGet,
		String doPost,
		String doPut,
		String doDelete,
		Class<?>[] paramTypes
	) {
		boolean ALLOW_GET = false;
		boolean ALLOW_HEAD = false;
		boolean ALLOW_POST = false;
		boolean ALLOW_PUT = false;
		boolean ALLOW_DELETE = false;
		boolean ALLOW_TRACE = true;
		boolean ALLOW_OPTIONS = true;
		for (
			Method method
			: getAllDeclaredMethods(stopClass, thisClass)
		) {
			if(Arrays.equals(paramTypes, method.getParameterTypes())) {
				String methodName = method.getName();
				if (doGet.equals(methodName)) {
					ALLOW_GET = true;
					ALLOW_HEAD = true;
				} else if (doPost.equals(methodName)) {
					ALLOW_POST = true;
				} else if (doPut.equals(methodName)) {
					ALLOW_PUT = true;
				} else if (doDelete.equals(methodName)) {
					ALLOW_DELETE = true;
				}
			}
		}
		StringBuilder allow = new StringBuilder();
		if (ALLOW_GET) {
			// if(allow.length() != 0) allow.append(", ");
			allow.append(METHOD_GET);
		}
		if (ALLOW_HEAD) {
			if(allow.length() != 0) allow.append(", ");
			allow.append(METHOD_HEAD);
		}
		if (ALLOW_POST) {
			if(allow.length() != 0) allow.append(", ");
			allow.append(METHOD_POST);
		}
		if (ALLOW_PUT) {
			if(allow.length() != 0) allow.append(", ");
			allow.append(METHOD_PUT);
		}
		if (ALLOW_DELETE) {
			if(allow.length() != 0) allow.append(", ");
			allow.append(METHOD_DELETE);
		}
		if (ALLOW_TRACE) {
			if(allow.length() != 0) allow.append(", ");
			allow.append(METHOD_TRACE);
		}
		if (ALLOW_OPTIONS) {
			if(allow.length() != 0) allow.append(", ");
			allow.append(METHOD_OPTIONS);
		}
		response.setHeader("Allow", allow.toString());
	}
}
