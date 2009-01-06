package com.aoindustries.servlet.filter;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Filters the output and removes extra white space at the beginning of lines and completely removes blank lines.
 * TEXTAREAs are automatically detected as long as they start with exact "&lt;textarea" and end with exactly "&lt;/textarea" (case insensitive).
 * PREs are automatically detected as long as they start with exact "&lt;pre" and end with exactly "&lt;/pre" (case insensitive).
 * The reason for the specific tag format is to simplify the implementation
 * for maximum performance.
 *
 * @author  AO Industries, Inc.
 */
public class TrimFilter implements Filter {

    private static final String REQUEST_ATTRIBUTE_KEY = TrimFilter.class.getName()+".filter_applied";

    public void init(FilterConfig config) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        // Makes sure only one trim filter is applied per request
        if(
            request.getAttribute(REQUEST_ATTRIBUTE_KEY)==null
            && (response instanceof HttpServletResponse)
        ) {
            request.setAttribute(REQUEST_ATTRIBUTE_KEY, Boolean.TRUE);
            try {
                chain.doFilter(request, new TrimFilterResponse((HttpServletResponse)response));
            } finally {
                request.removeAttribute(REQUEST_ATTRIBUTE_KEY);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
