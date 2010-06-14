/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.table;

/**
 * An abstract structure for column types in a table.
 *
 * @author  AO Industries, Inc.
 */
public interface Type {

    /**
     * Compare two objects lexically.  Return 0 if null,
     * <0 if O1 is less than O2, or >0 if O1 is greater than O2
     */
    int compare(Object O1, Object O2);
    
    /**
     * Gets the display value object for a type.
     */
    Object getDisplay(Object O1);
    
    /**
     * Gets the class for objects of this type.
     */
    Class getTypeClass();
}
