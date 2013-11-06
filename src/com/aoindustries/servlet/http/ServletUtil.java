/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.NewEncodingUtils;
import com.aoindustries.io.unix.UnixFile;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.ServletContext;
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

    /**
     * Converts a possibly-relative path to a context-relative absolute path.
     * Resolves ./ and ../ at the beginning of the URL but not in the middle of the URL.
     */
    public static String getAbsolutePath(String servletPath, String relativeUrlPath) throws MalformedURLException {
        if(relativeUrlPath.length()>0 && relativeUrlPath.charAt(0)!='/') {
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
     * Converts a possibly-relative path to a context-relative absolute path.
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
    public static void getAbsoluteURL(HttpServletRequest request, String relPath, MediaEncoder encoder, Appendable out) throws IOException {
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
        if(
            !href.startsWith("http://")
            && !href.startsWith("https://")
        ) href = getAbsolutePath(servletPath, href);

        // Encode URL path elements (like Japanese filenames)
        href = NewEncodingUtils.encodeUrlPath(href);

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
	 * The name of the last modified parameter that is optionally added.
	 * The value is URL-safe and does not need to be passed through URLEncoder.
	 */
	public static final String LAST_MODIFIED_PARAMETER_NAME = "lastModified";

	/**
	 * Encodes a last modified value.
	 * The value is URL-safe and does not need to be passed through URLEncoder.
	 */
	public static String encodeLastModified(long lastModified) {
		return Long.toString(lastModified / 1000, 32);
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
			// Quick identify checks first
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
	 * as a local resource.  Only applies to URLs that begin with a slash (/).
	 * </p>
	 * <p>
	 * This implementation assume anchors (#) are always after the last question mark (?).
	 * </p>
	 */
	public static String addLastModified(ServletContext servletContext, String url, AddLastModifiedWhen when) {
		if(
			when != AddLastModifiedWhen.FALSE // Never try to add
			&& url.startsWith("/")
		) {
			int questionPos = url.lastIndexOf('?');
			String path = questionPos==-1 ? url : url.substring(0, questionPos);
			boolean doAdd;
			if(when == AddLastModifiedWhen.TRUE) {
				// Always try to add
				doAdd = true;
			} else {
				// Conditionally try to add based on file extension
				doAdd = staticExtensions.contains(
					UnixFile.getExtension(path).toLowerCase(Locale.ENGLISH)
				);
			}
			if(doAdd) {
				long lastModified = 0;
				String realPath = servletContext.getRealPath(path);
				if(realPath != null) {
					// Use File first
					lastModified = new File(realPath).lastModified();
				}
				if(lastModified == 0) {
					// Try URL
					try {
						URL resourceUrl = servletContext.getResource(path);
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
				if(lastModified != 0) {
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
		return url;
	}
}
