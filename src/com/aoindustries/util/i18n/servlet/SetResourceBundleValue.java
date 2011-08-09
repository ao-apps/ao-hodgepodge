/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
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
package com.aoindustries.util.i18n.servlet;

import com.aoindustries.util.i18n.ModifiableResourceBundle;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sets the resource bundle value.  Used by ResourceEditorTag.
 */
public class SetResourceBundleValue extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Having trouble with XMLHttpRequest in Firefox 3 and UTF-8 encoding.  This is a workaround.
     */
    static String getUTF8Parameter(HttpServletRequest request, String name) throws UnsupportedEncodingException {
        String value = request.getParameter(name);
        //System.out.println("DEBUG: SetResourceBundleValue: value..............="+value);
        if(value==null) return null;
        //System.out.println("DEBUG: SetResourceBundleValue: Converted would be ="+new String(value.getBytes("iso-8859-1"), "UTF-8"));
        // return new String(value.getBytes("iso-8859-1"), "UTF-8");
        return value;
    }

    public SetResourceBundleValue() {
    }

    private String role;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        role = config.getInitParameter("role");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Must have the required role
        if("*".equals(role) || request.isUserInRole(role)) {
            String baseName = getUTF8Parameter(request, "baseName");
            Locale locale = new Locale(getUTF8Parameter(request, "locale")); // TODO: Parse country and variant, too.
            String key = getUTF8Parameter(request, "key");
            String value = getUTF8Parameter(request, "value");
            //for(int c=0;c<value.length();c++) System.out.println(Integer.toHexString(value.charAt(c)));
            boolean modified = "true".equals(request.getParameter("modified"));

            // Find the bundle
            ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
            if(!resourceBundle.getLocale().equals(locale)) throw new AssertionError("resourceBundle.locale!=locale");
            if(!(resourceBundle instanceof ModifiableResourceBundle)) throw new AssertionError("resourceBundle is not a ModifiableResourceBundle");
            ((ModifiableResourceBundle)resourceBundle).setString(key, value, modified);

            // Set request parameters
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("  <head><title>Value Successfully Set</title></head>");
            out.println("  <body>Value Successfully Set</body>");
            out.println("</html>");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
