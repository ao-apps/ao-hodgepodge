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
        incrementCount(getClass());
    }

    /**
     * Automatically decrements the counter for this class on finalize call.  Please
     * note that finalize and actual memory freeing are separate steps so heap
     * use could still be high even if the object count is low.
     */
    protected void finalize() throws Throwable {
        decrementCount(getClass());
        super.finalize();
    }
    
    /**
     * Each element contains a <code>long[]</code> of length 2 (num_inc, num_dec), to avoid the overhead of Long instantiation for each update.
     */
    private static final Map<Class,long[]> countsPerClass=new HashMap<Class,long[]>();

    /**
     * Increments the counter for the class of the provided object.
     */
    public static void incrementCount(Object O) {
        incrementCount(O.getClass());
    }

    /**
     * Increments the counter for the provided class.
     */
    public static void incrementCount(Class C) {
        synchronized(countsPerClass) {
            long[] la=countsPerClass.get(C);
            if(la==null) countsPerClass.put(C, new long[] {1, 0});
            else la[0]++;
        }
    }

    /**
     * Decrements the counter for the class of the provided object.
     */
    public static void decrementCount(Object O) {
        decrementCount(O.getClass());
    }

    /**
     * Decrements the counter for the provided class.
     */
    public static void decrementCount(Class C) {
        synchronized(countsPerClass) {
            long[] la=countsPerClass.get(C);
            if(la!=null) la[1]++;
        }
    }
    
    /**
     * Gets all of the classes with counts
     */
    public static Class[] getClasses() {
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
    }

    /**
     * Gets the total constructed count for the provided class.
     */
    public static long getConstructorCount(Class C) {
        synchronized(countsPerClass) {
            long[] counts=countsPerClass.get(C);
            if(counts==null) return 0;
            return counts[0];
        }
    }

    /**
     * Gets the total finalized count for the provided class.
     */
    public static long getFinalizerCount(Class C) {
        synchronized(countsPerClass) {
            long[] counts=countsPerClass.get(C);
            if(counts==null) return 0;
            return counts[1];
        }
    }

    /**
     * Gets the current instance count for the provided class.
     */
    public static long getCurrentCount(Class C) {
        synchronized(countsPerClass) {
            long[] counts=countsPerClass.get(C);
            if(counts==null) return 0;
            return counts[0]-counts[1];
        }
    }
}