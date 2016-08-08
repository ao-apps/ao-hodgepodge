/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2016  AO Industries, Inc.
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

import com.aoindustries.io.FileUtils;
import com.aoindustries.io.IoUtils;
import com.aoindustries.lang.ObjectUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sets the modified time to that of the file itself and all dependencies.
 * Currently only *.css supported.
 * <p>
 * When mapped to handle *.css files, parses the underlying file from the file
 * system and automatically adds lastModified=#### URL parameters.  This allows
 * the replacement of files to be immediately visible to browsers while still
 * efficiently caching when nothing changed.
 * </p>
 * <p>
 * The current CSS parser is extremely simple and may not catch all URLs.
 * Specifically, it only looks for URLs on a line-by-line basis and does not
 * support blackslash (\) escapes.
 * </p>
 * <p>
 * All files must be in the UTF-8 encoding.
 * </p>
 */
public class LastModifiedServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(LastModifiedServlet.class.getName());

	private static final long serialVersionUID = 1L;

	/**
	 * The number of milliseconds to keep cache entries.
	 */
	private static final long CACHE_MAX_AGE = 1000L;

	/**
	 * Encoding used on reading files and writing output.
	 */
	private static final String ENCODING = "UTF-8";

	/**
	 * The extension that will be parsed as CSS file.
	 */
	private static final String CSS_EXTENSION = "css";

	/**
	 * The name of the last modified parameter that is optionally added.
	 * The value is URL-safe and does not need to be passed through URLEncoder.
	 */
	public static final String LAST_MODIFIED_PARAMETER_NAME = "lastModified";

	/**
	 * The header that may be used to disable automatic lastModified parameters.
	 */
	public static final String LAST_MODIFIED_HEADER_NAME = "X-com-aoindustries-servlet-http-LastModifiedServlet-lastModified";

	/**
	 * Encodes a last modified value.
	 * The value is URL-safe and does not need to be passed through URLEncoder.
	 */
	public static String encodeLastModified(long lastModified) {
		return Long.toString(lastModified / 1000, 32);
	}

	private static class HeaderAndPath {

		private final Boolean header;
		private final String path;

		private HeaderAndPath(Boolean header, String path) {
			this.header = header;
			this.path = path;
		}

		private HeaderAndPath(HttpServletRequest request, String path) {
			String headerS = request.getHeader(LAST_MODIFIED_HEADER_NAME);
			if("true".equalsIgnoreCase(headerS)) header = Boolean.TRUE;
			else if("false".equalsIgnoreCase(headerS)) header = Boolean.FALSE;
			else header = null;
			this.path = path;
		}

		@Override
		public String toString() {
			return "(" + header + ", " + path + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof HeaderAndPath)) return false;
			HeaderAndPath other = (HeaderAndPath)obj;
			return
				ObjectUtils.equals(header, other.header)
				&& path.equals(other.path)
			;
		}

		@Override
		public int hashCode() {
			int hash = ObjectUtils.hashCode(header);
			hash = hash * 31 + path.hashCode();
			return hash;
		}
	}

	private static class ParsedCssFile {

		/**
		 * The attribute name used to store the cache.
		 */
		private static final String PARSE_CSS_FILE_CACHE_ATTRIBUTE_NAME = ParsedCssFile.class.getName()+".parseCssFile.cache";

		private static final Pattern urlPattern = Pattern.compile(
			"url\\s*\\(\\s*['\"]?(\\S+)['\"]?\\s*\\)",
			Pattern.CASE_INSENSITIVE
		);

		private static ParsedCssFile parseCssFile(ServletContext servletContext, HeaderAndPath hap) throws FileNotFoundException, IOException {
			// Get the cache
			@SuppressWarnings("unchecked")
			Map<HeaderAndPath,ParsedCssFile> cache = (Map<HeaderAndPath,ParsedCssFile>)servletContext.getAttribute(PARSE_CSS_FILE_CACHE_ATTRIBUTE_NAME);
			if(cache == null) {
				// Create new cache
				cache = new HashMap<HeaderAndPath,ParsedCssFile>();
				servletContext.setAttribute(PARSE_CSS_FILE_CACHE_ATTRIBUTE_NAME, cache);
			}
			synchronized(cache) {
				// Check the cache
				final long lastModified = getCachedLastModified(servletContext, hap);
				ParsedCssFile parsedCssFile = cache.get(hap);
				if(
					parsedCssFile != null
					&& parsedCssFile.lastModified == lastModified
					&& !parsedCssFile.hasModifiedUrl(servletContext)
				) {
					return parsedCssFile;
				} else {
					// (Re)parse the file
					String cssContent;
					{
						InputStream resourceIn = servletContext.getResourceAsStream(hap.path);
						if(resourceIn==null) throw new FileNotFoundException(hap.path);
						BufferedReader in = new BufferedReader(new InputStreamReader(resourceIn, ENCODING));
						try {
							cssContent = IoUtils.readFully(in);
						} finally {
							in.close();
						}
					}
					// Replace values while capturing URLs
					StringBuilder newContent = new StringBuilder(cssContent.length() << 1);
					Map<HeaderAndPath,Long> referencedPaths = new HashMap<HeaderAndPath,Long>();
					Matcher matcher = urlPattern.matcher(cssContent);
					int lastEnd = 0;
					while(matcher.find()) {
						int start = matcher.start(1);
						int end = matcher.end(1);
						if(start!=lastEnd) newContent.append(cssContent, lastEnd, start);
						String url = matcher.group(1);
						// The regular expression leaves ' or " at end of URL, strip here
						String addAfterUrl = null;
						if(url.endsWith("'")) {
							url = url.substring(0, url.length()-1);
							addAfterUrl = "'";
						} else if(url.endsWith("\"")) {
							url = url.substring(0, url.length()-1);
							addAfterUrl = "\"";
						}
						//System.err.println("url=" + url);
						newContent.append(url);
						// Check for header disabling auto last modified
						if(hap.header==null || hap.header) {
							// Get the resource path relative to the CSS file
							String resourcePath = ServletUtil.getAbsolutePath(hap.path, url);
							if(resourcePath.startsWith("/")) {
								HeaderAndPath resourceHap = new HeaderAndPath(hap.header, resourcePath);
								long resourceModified = getCachedLastModified(servletContext, resourceHap);
								if(resourceModified != 0) {
									referencedPaths.put(resourceHap, resourceModified);
									int questionPos = url.lastIndexOf('?');
									newContent
										.append(questionPos==-1 ? '?' : '&')
										.append(LAST_MODIFIED_PARAMETER_NAME)
										.append('=')
										.append(encodeLastModified(resourceModified))
									;
								}
							}
						}
						if(addAfterUrl!=null) newContent.append(addAfterUrl);
						lastEnd = end;
					}
					if(lastEnd < cssContent.length()) newContent.append(cssContent, lastEnd, cssContent.length());
					parsedCssFile = new ParsedCssFile(
						servletContext,
						lastModified,
						newContent.toString().getBytes(ENCODING),
						referencedPaths
					);
					cache.put(hap, parsedCssFile);
					return parsedCssFile;
				}
			}
		}

		/**
		 * The last modified time of the file that was parsed.
		 */
		private final long lastModified;

		/**
		 * The CSS file with all URLs modified.
		 */
		private final byte[] rewrittenCssFile;

		/**
		 * The list of paths that need to be checked to get the new modified time.
		 */
		private final Map<HeaderAndPath,Long> referencedPaths;

		/**
		 * The most recent last modified of the CSS file itself and all dependencies.
		 */
		private final long newestLastModified;

		private ParsedCssFile(
			ServletContext servletContext,
			long lastModified,
			byte[] rewrittenCssFile,
			Map<HeaderAndPath,Long> referencedPaths
		) {
			this.lastModified = lastModified;
			this.referencedPaths = referencedPaths;
			this.rewrittenCssFile = rewrittenCssFile;
			long newest = lastModified;
			for(Map.Entry<HeaderAndPath,Long> entry : referencedPaths.entrySet()) {
				long modified = getCachedLastModified(servletContext, entry.getKey());
				if(modified > newest) newest = modified;
			}
			this.newestLastModified = newest;
		}

		/**
		 * Checks if any of the referencedPaths have been modified.
		 */
		private boolean hasModifiedUrl(ServletContext servletContext) {
			for(Map.Entry<HeaderAndPath,Long> entry : referencedPaths.entrySet()) {
				if(getCachedLastModified(servletContext, entry.getKey()) != entry.getValue()) {
					return true;
				}
			}
			return false;
		}
	}

	private static class GetLastModifiedCacheValue {
		private long cacheTime = Long.MIN_VALUE;
		private long lastModified = Long.MIN_VALUE;

		private GetLastModifiedCacheValue() {
		}

		/**
		 * Determines if this cache value is valid for the given moment in time.
		 */
		private boolean isValid(long currentTime) {
			long timeSince = currentTime - cacheTime;
			return
				 -CACHE_MAX_AGE <= timeSince
				&& timeSince <= CACHE_MAX_AGE
			;
		}
	}

	/**
	 * The attribute name used to store the cache.
	 */
	private static final String GET_LAST_MODIFIED_CACHE_ATTRIBUTE_NAME = LastModifiedServlet.class.getName()+".getLastModified.cache";

	/**
	 * Gets a modified time from either a file or URL.
	 * Caches results for up to a second.
	 */
	private static long getCachedLastModified(ServletContext servletContext, HeaderAndPath hap) {
		// Get the cache
		@SuppressWarnings("unchecked")
		Map<HeaderAndPath,GetLastModifiedCacheValue> cache = (Map<HeaderAndPath,GetLastModifiedCacheValue>)servletContext.getAttribute(GET_LAST_MODIFIED_CACHE_ATTRIBUTE_NAME);
		if(cache == null) {
			// Create new cache
			cache = new HashMap<HeaderAndPath,GetLastModifiedCacheValue>();
			servletContext.setAttribute(GET_LAST_MODIFIED_CACHE_ATTRIBUTE_NAME, cache);
		}
		GetLastModifiedCacheValue cacheValue;
		synchronized(cache) {
			// Get the cache entry
			cacheValue = cache.get(hap);
			if(cacheValue==null) {
				cacheValue = new GetLastModifiedCacheValue();
				cache.put(hap, cacheValue);
			}
		}
		synchronized(cacheValue) {
			final long currentTime = System.currentTimeMillis();
			if(cacheValue.isValid(currentTime)) {
				return cacheValue.lastModified;
			} else {
				long lastModified = 0;
				String realPath = servletContext.getRealPath(hap.path);
				if(realPath != null) {
					// Use File first
					lastModified = new File(realPath).lastModified();
				}
				if(lastModified == 0) {
					// Try URL
					try {
						URL resourceUrl = servletContext.getResource(hap.path);
						if(resourceUrl != null) {
							URLConnection conn = resourceUrl.openConnection();
							conn.setAllowUserInteraction(false);
							conn.setConnectTimeout(10);
							conn.setDoInput(false);
							conn.setDoOutput(false);
							conn.setReadTimeout(10);
							conn.setUseCaches(false);
							lastModified = conn.getLastModified();
						}
					} catch(IOException e) {
						// lastModified stays unmodified
					}
				}
				// Store in cache
				cacheValue.cacheTime = currentTime;
				cacheValue.lastModified = lastModified;
				return lastModified;
			}
		}
	}

	/**
	 * <p>
	 * Gets a last modified time given a context-relative path starting with a
	 * slash (/).
	 * </p>
	 * <p>
	 * Any file ending in ".css" (case-insensitive) will be parsed and will have
	 * a modified time that is equal to the greatest of itself or any referenced
	 * URL.
	 * </p>
	 *
	 * @return  the modified time or <code>0</code> when unknown.
	 */
	public static long getLastModified(ServletContext servletContext, HttpServletRequest request, String path, String extension) {
		HeaderAndPath hap = new HeaderAndPath(request, path);
		if(CSS_EXTENSION.equals(extension)) {
			try {
				// Parse CSS file, finding all dependencies.
				// Don't re-parse when CSS file not changed, but still check
				// dependencies.
				return ParsedCssFile.parseCssFile(servletContext, hap).newestLastModified;
			} catch(IOException e) {
				return 0;
			}
		} else {
			return getCachedLastModified(servletContext, hap);
		}
	}

	/**
	 * Automatically determines extension from path.
	 * 
	 * @see  #getLastModified(javax.servlet.ServletContext, java.lang.String, java.lang.String) 
	 */
	public static long getLastModified(ServletContext servletContext, HttpServletRequest request, String path) {
		return getLastModified(
			servletContext,
			request,
			path,
			FileUtils.getExtension(path)
		);
	}

	public enum AddLastModifiedWhen {
		/**
		 * Always tries to add last modified time.
		 */
		TRUE("true"),
		/**
		 * Never tries to add last modified time.
		 */
		FALSE("false"),
		/**
		 * Only tries to add last modified time to URLs that match expected
		 * static resource files, by extension.  This list is for the
		 * paths generally used for distributing web content and may not
		 * include every possible static file type.
		 */
		AUTO("auto");

		public static AddLastModifiedWhen valueOfLowerName(String lowerName) {
			// Quick identity checks first
			if("true"==lowerName) return TRUE;
			if("false"==lowerName) return FALSE;
			if("auto"==lowerName) return AUTO;
			// .equals checks
			if("true".equals(lowerName)) return TRUE;
			if("false".equals(lowerName)) return FALSE;
			if("auto".equals(lowerName)) return AUTO;
			// No match
			throw new IllegalArgumentException(lowerName);
		}

		private final String lowerName;

		private AddLastModifiedWhen(String lowerName) {
			this.lowerName = lowerName;
		}

		public String getLowerName() {
			return lowerName;
		}
	}

	/**
	 * Fetched some {@link from http://en.wikipedia.org/wiki/List_of_file_formats}
	 */
	private static final Set<String> staticExtensions = new HashSet<String>(
		Arrays.asList(
			// CSS
			"css",
			// Diagrams
			"dia",
			// Java
			"jar",
			"class",
			// JavaScript
			"js",
			"spt",
			"jsfl",
			// Image types
			"bmp",
			"exif",
			"gif",
			"ico",
			"jfif",
			"jpg",
			"jpeg",
			"jpe",
			"mng",
			"nitf",
			"png",
			"svg",
			"tif",
			"tiff",
			// HTML document
			"htm",
			"html",
			"xhtml",
			"mhtml",
			// PDF document
			"pdf",
			// XML document
			"xml",
			"rss"
		)
	);

	/**
	 * <p>
	 * Adds a last modified time (to the nearest second) to a URL if the resource is directly available
	 * as a local resource.  Only applies to relative URLs (./, ../) or URLs that begin with a slash (/).
	 * </p>
	 * <p>
	 * This implementation assume anchors (#) are always after the last question mark (?).
	 * </p>
	 */
	public static String addLastModified(ServletContext servletContext, HttpServletRequest request, String servletPath, String url, AddLastModifiedWhen when) throws MalformedURLException {
		// Never try to add if when==falsee
		if(when != AddLastModifiedWhen.FALSE) {
			// Get the context-relative path (resolves relative paths)
			String resourcePath = ServletUtil.getAbsolutePath(
				servletPath,
				url
			);
			if(resourcePath.startsWith("/")) {
				// Strip parameters from resourcePath
				{
					int questionPos = resourcePath.lastIndexOf('?');
					resourcePath = questionPos==-1 ? resourcePath : resourcePath.substring(0, questionPos);
				}
				String extension = FileUtils.getExtension(resourcePath).toLowerCase(Locale.ROOT);
				final boolean doAdd;
				if(when == AddLastModifiedWhen.TRUE) {
					// Always try to add
					doAdd = true;
				} else {
					// Check for header disabling auto last modified
					if("false".equalsIgnoreCase(request.getHeader(LAST_MODIFIED_HEADER_NAME))) {
						doAdd = false;
					} else {
						// Conditionally try to add based on file extension
						doAdd = staticExtensions.contains(
							extension
						);
					}
				}
				if(doAdd) {
					long lastModified = getLastModified(servletContext, request, resourcePath, extension);
					if(lastModified != 0) {
						int questionPos = url.lastIndexOf('?');
						int anchorStart = url.lastIndexOf('#');
						if(anchorStart == -1) {
							// No anchor
							url =
								url
								+ (questionPos==-1 ? '?' : '&')
								+ LAST_MODIFIED_PARAMETER_NAME + "="
								+ encodeLastModified(lastModified)
							;
						} else {
							// With anchor
							url =
								url.substring(0, anchorStart)
								+ (questionPos==-1 ? '?' : '&')
								+ LAST_MODIFIED_PARAMETER_NAME + "="
								+ encodeLastModified(lastModified)
								+ url.substring(anchorStart)
							;
						}
					}
				}
			}
		}
		return url;
	}

	@Override
	protected long getLastModified(HttpServletRequest request) {
		// Find the underlying file
		long lastModified = getLastModified(getServletContext(), request, request.getServletPath());
		return lastModified==0 ? -1 : lastModified;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Find the underlying file
			HeaderAndPath hap = new HeaderAndPath(request, request.getServletPath());
			String extension = FileUtils.getExtension(hap.path);
			if(CSS_EXTENSION.equalsIgnoreCase(extension)) {
				// Special case for CSS files
				byte[] rewrittenCss = ParsedCssFile.parseCssFile(getServletContext(), hap).rewrittenCssFile;
				response.setContentType("text/css");
				response.setCharacterEncoding(ENCODING);
				response.setContentLength(rewrittenCss.length);
				OutputStream out = response.getOutputStream();
				out.write(rewrittenCss);
			} else {
				throw new ServletException("Unsupported file type: " + extension);
			}
		} catch(FileNotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch(IOException e) {
			logger.log(Level.SEVERE, null, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch(RuntimeException e) {
			logger.log(Level.SEVERE, null, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
