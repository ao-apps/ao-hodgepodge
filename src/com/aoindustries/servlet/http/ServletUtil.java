package com.aoindustries.servlet.http;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Static utilities that may be useful by servlet/JSP/taglib environments.
 *
 * @author  AO Industries, Inc.
 */
public class ServletUtil {

    /**
     * Converts a possibly-relative path to a context-relative absolute path.
     */
    public static String getAbsolutePath(String servletPath, String path) throws MalformedURLException {
        if(path.length()>0 && path.charAt(0)!='/') {
            int slashPos = servletPath.lastIndexOf('/');
            if(slashPos==-1) throw new MalformedURLException("No slash found in servlet path: "+servletPath);
            String newPath = path;
            boolean modified;
            do {
                modified = false;
                if(newPath.startsWith("./")) {
                    newPath = newPath.substring(2);
                    modified = true;
                }
                if(newPath.startsWith("../")) {
                    slashPos = servletPath.lastIndexOf('/', slashPos-1);
                    if(slashPos==-1) throw new MalformedURLException("Too many ../ in path: "+path);

                    newPath = newPath.substring(3);
                    modified = true;
                }
            } while(modified);
            path = servletPath.substring(0, slashPos+1) + newPath;
        }
        return path;
    }

    /**
     * Converts a possibly-relative path to a context-relative absolute path.
     */
    public static String getAbsolutePath(HttpServletRequest request, String path) throws MalformedURLException {
        return getAbsolutePath(request.getServletPath(), path);
    }

    /**
     * Gets the URL for the provided possibly-relative path or <code>null</code> if no resource
     * is mapped to the path.
     */
    public static URL getResource(ServletContext servletContext, HttpServletRequest request, String path) throws MalformedURLException {
        return servletContext.getResource(getAbsolutePath(request, path));
    }

    /**
     * Checks if a resource with the possibly-relative path exists.
     */
    public static boolean resourceExists(ServletContext servletContext, HttpServletRequest request, String path) throws MalformedURLException {
        return getResource(servletContext, request, path)!=null;
    }
}
