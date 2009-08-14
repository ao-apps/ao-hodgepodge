/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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

import com.aoindustries.util.sort.*;
import java.util.*;

/**
 * Sorting utilities.
 *
 * @author  AO Industries, Inc.
 */
public final class Sort {

    /**
     * Make no instances.
     */
    private Sort() {
    }

    /**
     * Sorts the given <code>String[]</code> in ascending lexical order.
     *
     * @deprecated  Please use new SortAlgorithm classes
     * @see  com.aoindustries.util.sort.AutoSort#sortStatic(Object[])
     */
    public static void sort(String[] list) {
        AutoSort.sortStatic(list);
    }

    /**
     * Sorts the given <code>ArrayList</code> of <code>Integer</code>s in ascending lexical order.
     *
     * @deprecated  Please use new SortAlgorithm classes
     * @see  com.aoindustries.util.sort.AutoSort#sortStatic(List)
     */
    public static void sortInteger(List<?> list) {
        AutoSort.sortStatic(list);
    }

    /**
     * Sorts the given <code>ArrayList</code> of <code>Integer</code>s in ascending lexical order.
     * Also sorts the other <code>ArrayList</code> such that the elements remain at the same relative
     * index as the first <code>ArrayList</code>.
     */
    /*
    public static void sortInteger(List list, List optList) {
        int len=list.size();
        for(int c=1;c<len;c++) {
            // Insert the word at the appropriate place in the array
            Integer I=(Integer)list.get(c);
            int i=I.intValue();
            Object O=optList==null?null:optList.get(c);
            int bottom=0;
            int range=c;

            while(range>0) {
                int half=range>>>1;
                int pos=bottom+half;
                int compare=((Integer)list.get(pos)).intValue();
                if(i>=compare) {
                    if(half==0) {
                        if(i>compare) bottom++;
                        break;
                    }
                    bottom=pos;
                    range-=half;
                } else range=half;
            }
            if(bottom!=c) {
                list.remove(c);
                list.add(bottom, I);
                if(optList!=null) {
                    optList.remove(c);
                    optList.add(bottom, O);
                }
            }
        }
    }*/

    /**
     * Sorts the given <code>ArrayList</code> in descending lexical order.  Each item is expected to
     * be contained in the elements, <code>Object</code> and then <code>Float</code>.
     */
    /*public static void sortObjectFloatDescending(List list) {
        int len=list.size();
        for(int c=2;c<len;c+=2) {
            // Insert the object and float at the appropriate place in the array
            Float F=(Float)list.get(c+1);
            float f=F.floatValue();

            int bottom=0;
            int range=c;

            while(range>0) {
                int half=(range>>>1)&0xfffffffe;
                int pos=bottom+half;
                float res=((Float)list.get(pos+1)).floatValue()-f;
                if(res>=0) {
                    if(half==0) {
                        if(res>0) bottom+=2;
                        break;
                    }
                    bottom=pos;
                    range-=half;
                } else range=half;
            }
            if(bottom!=c) {
                Object O=list.get(c);
                list.remove(c);
                list.remove(c);
                list.add(bottom, O);
                list.add(bottom+1, F);
            }
        }
    }*/
}