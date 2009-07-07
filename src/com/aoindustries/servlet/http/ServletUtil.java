package com.aoindustries.servlet.http;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Logger;
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
        Enumeration headers = request.getHeaders("User-Agent");
        while(headers.hasMoreElements()) {
            String userAgent = (String)headers.nextElement();
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
}
