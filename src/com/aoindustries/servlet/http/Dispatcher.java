/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2015  AO Industries, Inc.
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

    private Dispatcher() {
    }

	/**
	 * Tracks the first servlet path seen, before any include/forward.
	 */
	private static final ThreadLocal<String> originalPage = new ThreadLocal<String>();

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
	 * Tracks if the request has been forwarded.
	 */
	protected static final ThreadLocal<Boolean> requestForwarded = new ThreadLocal<Boolean>();

	/**
	 * Checks if the request has been forwarded.
	 */
	public static boolean isForwarded() {
		Boolean forwarded = requestForwarded.get();
		return forwarded != null && forwarded;
	}

	/**
	 * Performs a forward, allowing page-relative paths and setting all values
	 * compatible with &lt;ao:forward&gt; tag.
	 */
	public static void forward(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response
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
				// Perform dispatch
				dispatcher.forward(request, response);
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
	 * Performs an include, allowing page-relative paths and setting all values
	 * compatible with &lt;ao:include&gt; tag.
	 * 
	 * @throws SkipPageException when the included page has been skipped due to a redirect.
	 */
	public static void include(
		ServletContext servletContext,
		String page,
		HttpServletRequest request,
		HttpServletResponse response
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
				// Perform dispatch
				Includer.dispatchInclude(dispatcher, request, response);
			} finally {
				dispatchedPage.set(oldDispatchPage);
			}
		} finally {
			if(oldOriginal==null) {
				originalPage.set(null);
			}
		}
	}
}