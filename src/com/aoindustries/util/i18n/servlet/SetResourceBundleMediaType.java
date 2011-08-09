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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.util.i18n.ModifiableResourceBundle;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sets the resource bundle media type.  Used by ResourceEditorTag.
 */
public class SetResourceBundleMediaType extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public SetResourceBundleMediaType() {
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
            String baseName = SetResourceBundleValue.getUTF8Parameter(request, "baseName");
            String key = SetResourceBundleValue.getUTF8Parameter(request, "key");
            String mediaTypeParam = SetResourceBundleValue.getUTF8Parameter(request, "mediaType");
            // Determine the MediaType and isBlockElement
            MediaType mediaType = null;
            Boolean isBlockElement = null;
            //System.out.println("DEBUG: mediaTypeParam="+mediaTypeParam);
            for(MediaType mt : MediaType.values()) {
                String mtVal = mt.getMediaType(); //.replace('+', ' '); // Losing + sign from XMLHttpRequest call
                if(mt==MediaType.XHTML) {
                    // Special treatment for isBlockElement
                    if((mtVal+" (inline)").equals(mediaTypeParam)) {
                        mediaType = mt;
                        isBlockElement = false;
                        break;
                    } else if((mtVal+" (block)").equals(mediaTypeParam)) {
                        mediaType = mt;
                        isBlockElement = true;
                        break;
                    }
                } else {
                    if(mtVal.equals(mediaTypeParam)) {
                        mediaType = mt;
                        // isBlockElement remains null
                        break;
                    }
                }
            }
            //System.out.println("DEBUG: mediaType="+mediaType);
            if(mediaType==null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                // Find the bundle
                Locale locale = Locale.ROOT; // Media type is always set on the ROOT locale
                ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
                if(!resourceBundle.getLocale().equals(locale)) throw new AssertionError("resourceBundle.locale!=locale");
                if(!(resourceBundle instanceof ModifiableResourceBundle)) throw new AssertionError("resourceBundle is not a ModifiableResourceBundle");
                ((ModifiableResourceBundle)resourceBundle).setMediaType(key, mediaType, isBlockElement);

                // Set request parameters
                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("  <head><title>Media Type Successfully Set</title></head>");
                out.println("  <body>Media Type Successfully Set</body>");
                out.println("</html>");
            }
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
