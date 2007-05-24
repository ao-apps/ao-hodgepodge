package com.aoindustries.util.sort;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
import com.aoindustries.profiler.*;
import java.util.*;

/**
 * Generalized structure for sort algorithms.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
abstract public class SortAlgorithm {

    protected SortAlgorithm() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, SortAlgorithm.class, "<init>()", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public abstract <T> void sort(List<T> list);
    
    public abstract <T> void sort(T[] array);

    public abstract <T> void sort(List<T> list, SortStatistics stats);

    public abstract <T> void sort(T[] array, SortStatistics stats);

    public abstract <T> void sort(List<T> list, Comparator<T> comparator);
    
    public abstract <T> void sort(T[] array, Comparator<T> comparator);

    public abstract <T> void sort(List<T> list, Comparator<T> comparator, SortStatistics stats);

    public abstract <T> void sort(T[] array, Comparator<T> comparator, SortStatistics stats);

    @SuppressWarnings({"unchecked"})
    protected static <T> int compare(List<T> list, int i, int j, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "compare(List<T>,int,int,Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortInListComparing();

            T O1=list.get(i);
            T O2=list.get(j);
            
            if(O1==null) {
                if(O2==null) return 0;
                else return -1;
            } else {
                if(O2==null) return 1;
                else {
                    if(comparator!=null) return comparator.compare(O1, O2);
                    else if(O1 instanceof Comparable) {
                        Comparable<T> comp1 = (Comparable)O1;
                        return comp1.compareTo(O2);
                    } else throw new RuntimeException("Must either provide a Comparator or the objects must be Comparable");
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    @SuppressWarnings({"unchecked"})
    protected static <T> int compare(T[] array, int i, int j, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "compare(T[],int,int,Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortInListComparing();

            T O1=array[i];
            T O2=array[j];
            
            if(O1==null) {
                if(O2==null) return 0;
                else return -1;
            } else {
                if(O2==null) return 1;
                else {
                    if(comparator!=null) return comparator.compare(O1, O2);
                    else if(O1 instanceof Comparable) {
                        Comparable<T> comp1 = (Comparable)O1;
                        return comp1.compareTo(O2);
                    } else throw new RuntimeException("Must either provide a Comparator or the objects must be Comparable");
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    @SuppressWarnings({"unchecked"})
    protected static <T> int compare(T O1, T O2, Comparator<T> comparator, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "compare(T,T,Comparator<T>,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortObjectComparing();

            if(O1==null) {
                if(O2==null) return 0;
                else return -1;
            } else {
                if(O2==null) return 1;
                else {
                    if(comparator!=null) return comparator.compare(O1, O2);
                    else if(O1 instanceof Comparable) {
                        Comparable<T> comp1 = (Comparable)O1;
                        return comp1.compareTo(O2);
                    } else throw new RuntimeException("Must either provide a Comparator or the objects must be Comparable");
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected static <T> T get(List<T> list, int i, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "get(List<T>,int,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortGetting();
            return list.get(i);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected static <T> T get(T[] array, int i, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "get(T[],int,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortGetting();
            return array[i];
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected static <T> T set(List<T> list, int i, T O, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "set(List<T>,int,T,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortSetting();
            return list.set(i, O);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected static <T> T set(T[] array, int i, T O, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "set(T[],int,T,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortSetting();
            return array[i]=O;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected static <T> void swap(List<T> list, int i, int j, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "swap(List<T>,int,int,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortSwapping();

            if(list instanceof FileList) ((FileList)list).swap(i, j);
            else {
                T T=list.get(i);
                list.set(i, list.get(j));
                list.set(j, T);
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    protected static <T> void swap(T[] array, int i, int j, SortStatistics stats) {
        Profiler.startProfile(Profiler.FAST, SortAlgorithm.class, "swap(T[],int,int,SortStatistics)", null);
        try {
            if(stats!=null) stats.sortSwapping();

            T T=array[i];
            array[i]=array[j];
            array[j]=T;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}