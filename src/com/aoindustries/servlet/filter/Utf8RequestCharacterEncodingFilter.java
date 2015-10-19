/*
 * Copyright 2007-2011, 2015 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.servlet.filter;

import com.aoindustries.util.i18n.ThreadLocale;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p>
 * Sets the request encoding to "UTF-8".
 * Also restores the ThreadLocale to the system default upon request completion.
 * </p>
 * <p>
 * This should be used for the REQUEST dispatcher only.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class Utf8RequestCharacterEncodingFilter implements Filter {

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        try {
            chain.doFilter(request, response);
        } finally {
            ThreadLocale.set(Locale.getDefault());
        }
    }
    
    @Override
    public void destroy() {
    }
}
