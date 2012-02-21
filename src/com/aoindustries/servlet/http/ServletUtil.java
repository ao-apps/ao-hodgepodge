/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012  AO Industries, Inc.
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

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
            String newPath = relativeUrlPath;
            boolean modified;
            do {
                modified = false;
                if(newPath.startsWith("./")) {
                    newPath = newPath.substring(2);
                    modified = true;
                }
                if(newPath.startsWith("../")) {
                    slashPos = servletPath.lastIndexOf('/', slashPos-1);
                    if(slashPos==-1) throw new MalformedURLException("Too many ../ in relativeUrlPath: "+relativeUrlPath);

                    newPath = newPath.substring(3);
                    modified = true;
                }
            } while(modified);
            relativeUrlPath = servletPath.substring(0, slashPos+1) + newPath;
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
     */
    public static void getAbsoluteURL(HttpServletRequest request, String relPath, Appendable out) throws IOException {
        out.append(request.isSecure() ? "https://" : "http://");
        out.append(request.getServerName());
        int port = request.getServerPort();
        if(port!=(request.isSecure() ? 443 : 80)) out.append(':').append(Integer.toString(port));
        out.append(request.getContextPath());
        out.append(relPath);
    }
}
