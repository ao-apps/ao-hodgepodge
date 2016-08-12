/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015, 2016  AO Industries, Inc.
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

import com.aoindustries.servlet.LocalizedServletException;
import static com.aoindustries.servlet.http.ApplicationResources.accessor;
import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

/**
 * Static utilities that may be useful by servlet/JSP/taglib environments.
 *
 * @author  AO Industries, Inc.
 */
public class Dispatcher {

	/**
	 * The name of the request-scope Map that will contain the arguments for the current page.
	 */
	public static final String ARG_MAP_REQUEST_ATTRIBUTE_NAME = "arg";

	private Dispatcher() {
	}

	/**
	 * Tracks the first servlet path seen, before any include/forward.
	 */
	private static final ThreadLocal<String> originalPage = new ThreadLocal<String>();

	/**
	 * Gets the current-thread original page or null if not set.
	 */
	public static String getOriginalPage() {
		return originalPage.get();
	}

	/**
	 * Sets the current-thread original page.
	 */
	public static void setOriginalPage(String page) {
		originalPage.set(page);
	}

	/**
	 * Gets the original page path corresponding to the original request before any forward/include.
	 * Assumes all forward/include done with ao taglib.
	 */
	public static String getOriginalPagePath(HttpServletRequest request) {
		String original = originalPage.get();
		return original!=null ? original : request.getServletPath();
	}

	/**
	 * Tracks the current dispatch page for correct page-relative paths.
	 */
	private static final ThreadLocal<String> dispatchedPage = new ThreadLocal<String>();

	/**
	 * Gets the current-thread dispatched page or null if not set.
	 */
	public static String getDispatchedPage() {
		return dispatchedPage.get();
	}

	/**
	 * Sets the current-thread dispatched page.
	 */
	public static void setDispatchedPage(String page) {
		dispatchedPage.set(page);
	}

	/**
	 * Gets the current page path, including any effects from include/forward.
	 * This will be the path of the current page on forward or include.
	 * Assumes all forward/include done with ao taglib.
	 * This may be used as a substitute for HttpServletRequest.getServletPath() when the current page is needed instead of the originally requested servlet.
	 */
	public static String getCurrentPagePath(HttpServletRequest request) {
		String dispatched = dispatchedPage.get();
		return dispatched!=null ? dispatched : request.getServletPath();
	}

	/**
	 * Performs a forward, allowing page-relative paths and setting all values
	 * compatible with &lt;ao:forward&gt; tag.
	 *
	 * @param  args  The arguments for the page, accessible as request-scope var "arg"
	 */
	public static void forward(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response,
		Map<String,Object> args
	) throws ServletException, IOException {
		// Resolve the dispatcher
		String contextRelativePath = ServletUtil.getAbsolutePath(getCurrentPagePath(request), page);
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(contextRelativePath);
		if(dispatcher==null) throw new LocalizedServletException(accessor, "Dispatcher.dispatcherNotFound", contextRelativePath);
		// Track original page when first accessed
		final String oldOriginal = originalPage.get();
		try {
			// Set original request path if not already set
			if(oldOriginal==null) originalPage.set(request.getServletPath());
			// Keep old dispatch page to restore
			final String oldDispatchPage = dispatchedPage.get();
			try {
				// Store as new relative path source
				dispatchedPage.set(contextRelativePath);
				// Keep old arguments to restore
				final Object oldArgs = request.getAttribute(Dispatcher.ARG_MAP_REQUEST_ATTRIBUTE_NAME);
				try {
					// Set new arguments
					request.setAttribute(Dispatcher.ARG_MAP_REQUEST_ATTRIBUTE_NAME, args);
					// Perform dispatch
					dispatcher.forward(request, response);
				} finally {
					// Restore any previous args
					request.setAttribute(Dispatcher.ARG_MAP_REQUEST_ATTRIBUTE_NAME, oldArgs);
				}
			} finally {
				dispatchedPage.set(oldDispatchPage);
			}
		} finally {
			if(oldOriginal==null) {
				originalPage.set(null);
			}
		}
	}

	/**
	 * @see  #forward(javax.servlet.ServletContext, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	public static void forward(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response
	) throws ServletException, IOException {
		forward(servletContext, page, request, response, null);
	}

	/**
	 * Performs an include, allowing page-relative paths and setting all values
	 * compatible with &lt;ao:include&gt; tag.
	 *
	 * @param  args  The arguments for the page, accessible as request-scope var "arg"
	 * 
	 * @throws SkipPageException when the included page has been skipped due to a redirect.
	 */
	public static void include(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response,
		Map<String,Object> args
	) throws SkipPageException, ServletException, IOException {
		// Resolve the dispatcher
		String contextRelativePath = ServletUtil.getAbsolutePath(getCurrentPagePath(request), page);
		RequestDispatcher dispatcher = servletContext.getRequestDispatcher(contextRelativePath);
		if(dispatcher==null) throw new LocalizedServletException(accessor, "Dispatcher.dispatcherNotFound", contextRelativePath);
		// Track original page when first accessed
		final String oldOriginal = originalPage.get();
		try {
			// Set original request path if not already set
			if(oldOriginal==null) originalPage.set(request.getServletPath());
			// Keep old dispatch page to restore
			final String oldDispatchPage = dispatchedPage.get();
			try {
				// Store as new relative path source
				dispatchedPage.set(contextRelativePath);
				// Keep old arguments to restore
				final Object oldArgs = request.getAttribute(Dispatcher.ARG_MAP_REQUEST_ATTRIBUTE_NAME);
				try {
					// Set new arguments
					request.setAttribute(Dispatcher.ARG_MAP_REQUEST_ATTRIBUTE_NAME, args);
					// Perform dispatch
					Includer.dispatchInclude(dispatcher, request, response);
				} finally {
					// Restore any previous args
					request.setAttribute(Dispatcher.ARG_MAP_REQUEST_ATTRIBUTE_NAME, oldArgs);
				}
			} finally {
				dispatchedPage.set(oldDispatchPage);
			}
		} finally {
			if(oldOriginal==null) {
				originalPage.set(null);
			}
		}
	}

	/**
	 * @see  #include(javax.servlet.ServletContext, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	public static void include(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response
	) throws SkipPageException, ServletException, IOException {
		include(servletContext, page, request, response, null);
	}
}
