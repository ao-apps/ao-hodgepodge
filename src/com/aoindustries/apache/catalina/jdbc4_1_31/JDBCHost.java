/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.apache.catalina.jdbc4_1_31;

/*
 * This code is partially derived from org.apache.catalina.core.StandardHost.
 * For this reason we make this code available to everybody in the aocode-public
 * package.  Ultimately, we would like to submit this code to Apache for inclusion
 * in their Tomcat distribution.
 */
import org.apache.catalina.core.StandardHost;

/**
 * Overrides StandardHost in order to use the JDBCHostValve instead of StandardHostValve.
 *
 * @see  JDBCHostValve
 *
 * @author  AO Industries, Inc.
 */
public class JDBCHost extends StandardHost {

    public JDBCHost() {
        super();
        pipeline.setBasic(new JDBCHostValve());
    }
}
