/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.servlet.filter;

import com.aoindustries.util.StringUtility;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

/**
 * Prevents sessions from being created.  Without sessions, minimal information
 * should be stored as cookies.  In the event cookies are disabled, this filter
 * also adds the cookie values during URL rewriting.  Any cookies added to the
 * URLs through rewriting will have a parameter name beginning with
 * <code>cookie:</code>
 */
public class NoSessionFilter implements Filter {

    public static final String COOKIE_URL_PARAM_PREFIX = "cookie:";

    private static final String REQUEST_ATTRIBUTE_KEY = NoSessionFilter.class.getName()+".filter_applied";

    private final SortedSet<String> cookieNames = new TreeSet<String>();

    /**
     * Adds the values for any new cookies to the URL.  This handles session
     * management through URL rewriting.
     */
    private String addCookieValues(HttpServletRequest request, Map<String,Cookie> newCookies, String url) {
        // Split the anchor
        int poundPos = url.lastIndexOf('#');
        String anchor;
        if(poundPos==-1) anchor = null;
        else {
            anchor = url.substring(poundPos);
            url = url.substring(0, poundPos);
        }
        // Don't add for certains file types
        int questionPos = url.lastIndexOf('?');
        String lowerPath = (questionPos==-1 ? url : url.substring(0, questionPos)).toLowerCase(Locale.ENGLISH);
        if(
            !lowerPath.endsWith(".css")
            && !lowerPath.endsWith(".gif")
            && !lowerPath.endsWith(".ico")
            && !lowerPath.endsWith(".jpeg")
            && !lowerPath.endsWith(".jpg")
            && !lowerPath.endsWith(".js")
            && !lowerPath.endsWith(".png")
            && !lowerPath.endsWith(".txt")
            && !lowerPath.endsWith(".zip")
        ) {
            try {
                StringBuilder urlSB = new StringBuilder(url);
                boolean hasParam = questionPos!=-1;
                for(String cookieName : cookieNames) {
                    if(newCookies.containsKey(cookieName)) {
                        Cookie newCookie = newCookies.get(cookieName);
                        if(newCookie!=null) {
                            if(hasParam) urlSB.append('&');
                            else {
                                urlSB.append('?');
                                hasParam = true;
                            }
                            urlSB
                                .append(URLEncoder.encode(COOKIE_URL_PARAM_PREFIX+cookieName, "UTF-8"))
                                .append('=')
                                .append(URLEncoder.encode(newCookie.getValue(), "UTF-8"))
                            ;
                        } else {
                            // Cookie was removed - do not add to URL
                        }
                    } else {
                        // Add each of the cookie values that were passed-in on the URL, were not removed or added,
                        // and were not included as a request cookie.
                        String paramName = COOKIE_URL_PARAM_PREFIX+cookieName;
                        String[] values = request.getParameterValues(paramName);
                        if(values!=null && values.length>0) {
                            boolean found = false;
                            Cookie[] oldCookies = request.getCookies();
                            if(oldCookies!=null) {
                                for(Cookie oldCookie : oldCookies) {
                                    if(oldCookie.getName().equals(cookieName)) {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if(!found) {
                                if(hasParam) urlSB.append('&');
                                else {
                                    urlSB.append('?');
                                    hasParam = true;
                                }
                                urlSB
                                    .append(URLEncoder.encode(paramName, "UTF-8"))
                                    .append('=')
                                    .append(URLEncoder.encode(values[values.length-1], "UTF-8"))
                                ;
                            }
                        }
                    }
                }
                url = urlSB.toString();
            } catch(UnsupportedEncodingException err) {
                throw new WrappedException(err);
            }
        }
        if(anchor!=null) url = url + anchor;
        return url;
    }

    @Override
    public void init(FilterConfig config) {
        cookieNames.clear();
        cookieNames.addAll(StringUtility.splitStringCommaSpace(config.getInitParameter("cookieNames")));
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        // Makes sure only one trim filter is applied per request
        if(
            request.getAttribute(REQUEST_ATTRIBUTE_KEY)==null
            && (request instanceof HttpServletRequest)
            && (response instanceof HttpServletResponse)
        ) {
            request.setAttribute(REQUEST_ATTRIBUTE_KEY, Boolean.TRUE);
            try {
                final HttpServletRequest httpRequest = (HttpServletRequest)request;
                final HttpServletResponse httpResponse = (HttpServletResponse)response;
                final Map<String,Cookie> newCookies = new HashMap<String,Cookie>(cookieNames.size()*4/3+1);
                chain.doFilter(
                    new HttpServletRequestWrapper(httpRequest) {
                        @Override
                        public HttpSession getSession() {
                            throw new RuntimeException("Sessions are disabled");
                        }
                        @Override
                        public HttpSession getSession(boolean create) {
                            if(create) throw new RuntimeException("Sessions are disabled");
                            return null;
                        }
                    },
                    new HttpServletResponseWrapper(httpResponse) {
                        @Override
                        @Deprecated
                        public String encodeRedirectUrl(String url) {
                            return encodeRedirectURL(url);
                        }

                        @Override
                        public String encodeRedirectURL(String url) {
                            // If starts with http:// or https:// parse out the first part of the URL, encode the path, and reassemble.
                            String protocol;
                            String remaining;
                            if(url.length()>7 && (protocol=url.substring(0, 7)).equalsIgnoreCase("http://")) {
                                remaining = url.substring(7);
                            } else if(url.length()>8 && (protocol=url.substring(0, 8)).equalsIgnoreCase("https://")) {
                                remaining = url.substring(8);
                            } else if(url.startsWith("javascript:")) {
                                return url;
                            } else if(url.startsWith("mailto:")) {
                                return url;
                            } else {
                                return addCookieValues(httpRequest, newCookies, url);
                            }
                            int slashPos = remaining.indexOf('/');
                            if(slashPos==-1) {
                                return addCookieValues(httpRequest, newCookies, url);
                            }
                            String hostPort = remaining.substring(0, slashPos);
                            int colonPos = hostPort.indexOf(':');
                            String host = colonPos==-1 ? hostPort : hostPort.substring(0, colonPos);
                            String encoded;
                            if(host.equalsIgnoreCase(httpRequest.getServerName())) {
                                encoded = protocol + hostPort + addCookieValues(httpRequest, newCookies, remaining.substring(slashPos));
                            } else {
                                // Going to an different hostname, do not add request parameters
                                encoded = url;
                            }
                            return encoded;
                        }

                        @Override
                        @Deprecated
                        public String encodeUrl(String url) {
                            return encodeURL(url);
                        }

                        @Override
                        public String encodeURL(String url) {
                            // If starts with http:// or https:// parse out the first part of the URL, encode the path, and reassemble.
                            String protocol;
                            String remaining;
                            if(url.length()>7 && (protocol=url.substring(0, 7)).equalsIgnoreCase("http://")) {
                                remaining = url.substring(7);
                            } else if(url.length()>8 && (protocol=url.substring(0, 8)).equalsIgnoreCase("https://")) {
                                remaining = url.substring(8);
                            } else if(url.startsWith("javascript:")) {
                                return url;
                            } else if(url.startsWith("mailto:")) {
                                return url;
                            } else {
                                return addCookieValues(httpRequest, newCookies, url);
                            }
                            int slashPos = remaining.indexOf('/');
                            if(slashPos==-1) {
                                return addCookieValues(httpRequest, newCookies, url);
                            }
                            String hostPort = remaining.substring(0, slashPos);
                            int colonPos = hostPort.indexOf(':');
                            String host = colonPos==-1 ? hostPort : hostPort.substring(0, colonPos);
                            String encoded;
                            if(host.equalsIgnoreCase(httpRequest.getServerName())) {
                                encoded = protocol + hostPort + addCookieValues(httpRequest, newCookies, remaining.substring(slashPos));
                            } else {
                                // Going to an different hostname, do not add request parameters
                                encoded = url;
                            }
                            return encoded;
                        }

                        @Override
                        public void addCookie(Cookie newCookie) {
                            super.addCookie(newCookie);
                            String cookieName = newCookie.getName();
                            if(!cookieNames.contains(cookieName)) throw new AssertionError("Unexpected cookie name, add to cookieNames init parameter: "+cookieName);
                            if(newCookie.getMaxAge()==0) newCookies.put(cookieName, null);
                            else {
                                boolean found = false;
                                Cookie[] oldCookies = httpRequest.getCookies();
                                if(oldCookies!=null) {
                                    for(Cookie oldCookie : oldCookies) {
                                        if(oldCookie.getName().equals(cookieName)) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                                if(!found) newCookies.put(cookieName, newCookie);
                            }
                        }
                    }
                );
            } finally {
                request.removeAttribute(REQUEST_ATTRIBUTE_KEY);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
