/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
package com.aoindustries.util;

import com.aoindustries.io.LocalizedIOException;
import static com.aoindustries.util.ApplicationResources.accessor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContext;

/**
 * Property utilities.
 */
final public class PropertiesUtils {

    /**
     * Make no instances.
     */
    private PropertiesUtils() {}

	/**
	 * Loads properties from a classpath resource.
	 */
	public static Properties loadFromResource(Class<?> clazz, String resource) throws IOException {
		Properties props = new Properties();
		InputStream in = clazz.getResourceAsStream(resource);
        if(in==null) throw new LocalizedIOException(accessor, "PropertiesUtils.readProperties.resourceNotFound", resource);
		try {
			props.load(in);
		} finally {
			in.close();
		}
		return props;
	}

	/**
	 * Loads properties from a web resource.
	 */
	public static Properties loadFromResource(ServletContext servletContext, String resource) throws IOException {
		Properties props = new Properties();
		InputStream in = servletContext.getResourceAsStream(resource);
        if(in==null) throw new LocalizedIOException(accessor, "PropertiesUtils.readProperties.resourceNotFound", resource);
		try {
			props.load(in);
		} finally {
			in.close();
		}
		return props;
	}
}
