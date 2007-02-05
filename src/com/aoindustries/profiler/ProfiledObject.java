package com.aoindustries.profiler;

/*
 * Copyright 2005-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Keeps track of the number of instances of objects of certain classes.  Subclasses of this class are
 * automatically tracked.
 *
 * @author  AO Industries, Inc.
 */
public class ProfiledObject {

    /**
     * Automatically increments the counter for this class on construction.
     */
    public ProfiledObject() {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "<init>()", null);
        try {
            incrementCount(getClass());
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Automatically decrements the counter for this class on finalize call.  Please
     * note that finalize and actual memory freeing are separate steps so heap
     * use could still be high even if the object count is low.
     */
    protected void finalize() throws Throwable {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "finalize()", null);
        try {
            decrementCount(getClass());
            super.finalize();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    /**
     * Each element contains a <code>long[]</code> of length 2 (num_inc, num_dec), to avoid the overhead of Long instantiation for each update.
     */
    private static final Map<Class,long[]> countsPerClass=new HashMap<Class,long[]>();

    /**
     * Increments the counter for the class of the provided object.
     */
    public static void incrementCount(Object O) {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "incrementCount(Object)", null);
        try {
            incrementCount(O.getClass());
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Increments the counter for the provided class.
     */
    public static void incrementCount(Class C) {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "incrementCount(Class)", null);
        try {
            synchronized(countsPerClass) {
                long[] la=countsPerClass.get(C);
                if(la==null) countsPerClass.put(C, new long[] {1, 0});
                else la[0]++;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Decrements the counter for the class of the provided object.
     */
    public static void decrementCount(Object O) {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "decrementCount(Object)", null);
        try {
            decrementCount(O.getClass());
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Decrements the counter for the provided class.
     */
    public static void decrementCount(Class C) {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "decrementCount(Class)", null);
        try {
            synchronized(countsPerClass) {
                long[] la=countsPerClass.get(C);
                if(la!=null) la[1]++;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    /**
     * Gets all of the classes with counts
     */
    public static Class[] getClasses() {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "getClasses()", null);
        try {
            List<Class> list=new ArrayList<Class>();
            synchronized(countsPerClass) {
                Iterator<Class> I=countsPerClass.keySet().iterator();
                while(I.hasNext()) list.add(I.next());
                // Sort by number of instances and then classname
                Collections.sort(
                    list,
                    new Comparator<Class>() {
                        public int compare(Class C1, Class C2) {
                            long[] counts1=countsPerClass.get(C1);
                            long count1=counts1[1]-counts1[0];
                            long[] counts2=countsPerClass.get(C2);
                            long count2=counts2[1]-counts2[0];
                            // Count first
                            if(count1<count2) return -1;
                            if(count1>count2) return 1;
                            // Then classname
                            return C1.getName().compareTo(C2.getName());
                        }
                    }
                );
            }
            Class[] classes=new Class[list.size()];
            list.toArray(classes);
            return classes;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the total constructed count for the provided class.
     */
    public static long getConstructorCount(Class C) {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "getConstructorCount(Class)", null);
        try {
            synchronized(countsPerClass) {
                long[] counts=countsPerClass.get(C);
                if(counts==null) return 0;
                return counts[0];
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the total finalized count for the provided class.
     */
    public static long getFinalizerCount(Class C) {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "getFinalizerCount(Class)", null);
        try {
            synchronized(countsPerClass) {
                long[] counts=countsPerClass.get(C);
                if(counts==null) return 0;
                return counts[1];
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the current instance count for the provided class.
     */
    public static long getCurrentCount(Class C) {
        Profiler.startProfile(Profiler.FAST, ProfiledObject.class, "getCurrentCount(Class)", null);
        try {
            synchronized(countsPerClass) {
                long[] counts=countsPerClass.get(C);
                if(counts==null) return 0;
                return counts[0]-counts[1];
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}