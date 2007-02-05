package com.aoindustries.util;

/*
 * Copyright 2001-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.sort.*;
import java.util.*;

/**
 * Sorting utilities.
 *
 * @version  1.0
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
        Profiler.startProfile(Profiler.INSTANTANEOUS, Sort.class, "sort(String[])", null);
        try {
            AutoSort.sortStatic(list);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Sorts the given <code>ArrayList</code> of <code>Integer</code>s in ascending lexical order.
     *
     * @deprecated  Please use new SortAlgorithm classes
     * @see  com.aoindustries.util.sort.AutoSort#sortStatic(List)
     */
    public static void sortInteger(List<?> list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, Sort.class, "sortInteger(List<?>)", null);
        try {
            AutoSort.sortStatic(list);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Sorts the given <code>ArrayList</code> of <code>Integer</code>s in ascending lexical order.
     * Also sorts the other <code>ArrayList</code> such that the elements remain at the same relative
     * index as the first <code>ArrayList</code>.
     */
    /*
    public static void sortInteger(List list, List optList) {
        Profiler.startProfile(Profiler.FAST, Sort.class, "sortInteger(List,List)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }*/

    /**
     * Sorts the given <code>ArrayList</code> in descending lexical order.  Each item is expected to
     * be contained in the elements, <code>Object</code> and then <code>Float</code>.
     */
    /*public static void sortObjectFloatDescending(List list) {
        Profiler.startProfile(Profiler.FAST, Sort.class, "sortObjectFloatDescending(List)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }*/
}