/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011  AO Industries, Inc.
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

import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Helper utility to get and set cookies
 */
public final class Cookies {

    private static final Logger logger = Logger.getLogger(Cookies.class.getName());

    private Cookies() {
    }

    /**
     * Adds a cookie.
     */
    public static void addCookie(
        HttpServletRequest request,
        HttpServletResponse response,
        String cookieName,
        String value,
        String comment,
        int maxAge,
        boolean secure,
        boolean contextOnlyPath
    ) {
        Cookie newCookie = new Cookie(cookieName, value);
        if(comment!=null) newCookie.setComment(comment);
        newCookie.setMaxAge(maxAge);
        newCookie.setSecure(secure && request.isSecure());
        String path;
        if(contextOnlyPath) {
            path = request.getContextPath() + "/";
            //if(path.length()==0) path = "/";
        } else {
            path = "/";
        }
        newCookie.setPath(path);
        response.addCookie(newCookie);
    }

    /**
     * Gets a cookie value given its name or <code>null</code> if not found.
     */
    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null) {
            for(int c=cookies.length-1;c>=0;c--) {
                Cookie cookie = cookies[c];
                if(cookie.getName().equals(cookieName)) return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Removes a cookie by adding it with maxAge of zero.
     */
    public static void removeCookie(
        HttpServletRequest request,
        HttpServletResponse response,
        String cookieName,
        boolean secure,
        boolean contextOnlyPath
    ) {
        addCookie(request, response, cookieName, "Removed", null, 0, secure, contextOnlyPath);
    }
}
