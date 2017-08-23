/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2016, 2017  AO Industries, Inc.
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
package com.aoindustries.servlet;

import com.aoindustries.cache.BackgroundCache;
import com.aoindustries.cache.BackgroundCache.Refresher;
import com.aoindustries.cache.BackgroundCache.Result;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 * ServletContext methods can be somewhat slow, this offers a cache that refreshes
 * the recently used values in the background.  Importantly, it caches misses as well.
 *
 * @author  AO Industries, Inc.
 */
final public class ServletContextCache {

	private static final Logger logger = Logger.getLogger(ServletContextCache.class.getName());

	private static final long REFRESH_INTERVAL = 5 * 1000;

	private static final long EXPIRATION_AGE = 60 * 1000;

	static final String ATTRIBUTE_KEY = ServletContextCache.class.getName();

	/**
	 * Gets or creates the cache for the provided servlet context.
	 */
	public static ServletContextCache getCache(ServletContext servletContext) {
		ServletContextCache cache = (ServletContextCache)servletContext.getAttribute(ATTRIBUTE_KEY);
		if(cache == null) {
			// It is possible this is called during context initialization before the listener
			cache = new ServletContextCache(servletContext);
			servletContext.setAttribute(ATTRIBUTE_KEY, cache);
			//throw new IllegalStateException("ServletContextCache not active in the provided ServletContext.  Add context listener to web.xml?");
		} else {
			assert cache.servletContext == servletContext;
		}
		return cache;
	}

	final ServletContext servletContext;

	ServletContextCache(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	void stop() {
		getResourceCache.stop();
		getRealPathCache.stop();
	}

	// <editor-fold defaultstate="collapsed" desc="getResource">
	private final BackgroundCache<String,URL,MalformedURLException> getResourceCache = new BackgroundCache<String,URL,MalformedURLException>(
		ServletContextCache.class.getName() + ".getResource",
		MalformedURLException.class,
		REFRESH_INTERVAL,
		EXPIRATION_AGE,
		logger
	);

	private final Refresher<String,URL,MalformedURLException> getResourceRefresher = new Refresher<String,URL,MalformedURLException>() {
		@Override
		public URL call(String path) throws MalformedURLException {
			return servletContext.getResource(path);
		}
	};

	/**
	 * Gets the possibly cached URL.  This URL is not copied and caller should not fiddle with
	 * its state.  Thank you Java for this not being immutable.
	 *
	 * @see  ServletContext#getResource(java.lang.String)
	 */
	public URL getResource(String path) throws MalformedURLException {
		Result<URL,MalformedURLException> result = getResourceCache.get(path, getResourceRefresher);
		MalformedURLException exception = result.getException();
		if(exception != null) throw exception;
		return result.getValue();
	}

	/**
	 * @see  #getResource(java.lang.String)
	 */
	public static URL getResource(ServletContext servletContext, String path) throws MalformedURLException {
		return getCache(servletContext).getResource(path);
	}
	// </editor-fold>

	// TODO: getRequestDispatcher?

	// <editor-fold defaultstate="collapsed" desc="getRealPath">
	private final BackgroundCache<String,String,RuntimeException> getRealPathCache = new BackgroundCache<String,String,RuntimeException>(
		ServletContextCache.class.getName() + ".getRealPath",
		RuntimeException.class,
		REFRESH_INTERVAL,
		EXPIRATION_AGE,
		logger
	);

	private final Refresher<String,String,RuntimeException> getRealPathRefresher = new Refresher<String,String,RuntimeException>() {
		@Override
		public String call(String path) {
			return servletContext.getRealPath(path);
		}
	};

	/**
	 * @see  ServletContext#getRealPath(java.lang.String)
	 */
	public String getRealPath(String path) {
		Result<String,RuntimeException> result = getRealPathCache.get(path, getRealPathRefresher);
		RuntimeException exception = result.getException();
		if(exception != null) throw exception;
		return result.getValue();
	}

	/**
	 * @see  #getRealPath(java.lang.String)
	 */
	public static String getRealPath(ServletContext servletContext, String path) {
		return getCache(servletContext).getRealPath(path);
	}
	// </editor-fold>
}
