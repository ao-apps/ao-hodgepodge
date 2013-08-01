/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013  AO Industries, Inc.
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
package com.aoindustries.lang;

/**
 * Utilities that help when working with objects.
 *
 * @author  AO Industries, Inc.
 */
public final class ObjectUtils {

    /**
     * Make no instances.
     */
    private ObjectUtils() {
    }

    /**
     * Gets the hashCode for an object or <code>0</code> when <code>null</code>.
	 * 
	 * @see Objects#hashCode(java.lang.Object) as of Java 1.7
	 * 
	 * @ Java 1.7: deprecated  use Objects#hashCode(Object) as of Java 1.7
     */
	// Java 1.7: @Deprecated
    public static int hashCode(Object obj) {
        return obj != null ? obj.hashCode() : 0;
		// Java 1.7: return Objects.hashCode(obj);
    }

    /**
     * Compares the equality of two objects, including their null states.
	 * 
	 * @see Objects#equals(java.lang.Object, java.lang.Object) as of Java 1.7
	 * 
	 * @ Java 1.7: deprecated  use Objects#equals(Object, Object) as of Java 1.7
     */
	// Java 1.7: @Deprecated
    public static boolean equals(Object obj1, Object obj2) {
		return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
		// Java 1.7: return Objects.equals(obj1, obj2);
    }

    /**
     * Calls toString if non-null, returns null when null.
     */
    public static String toString(Object obj) {
        return obj==null ? null : obj.toString();
    }
}
