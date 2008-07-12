package com.aoindustries.util.sort;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.*;
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
    }

    @SuppressWarnings({"unchecked"})
    protected static <T> int compare(T[] array, int i, int j, Comparator<T> comparator, SortStatistics stats) {
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
    }

    @SuppressWarnings({"unchecked"})
    protected static <T> int compare(T O1, T O2, Comparator<T> comparator, SortStatistics stats) {
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
    }

    protected static <T> T get(List<T> list, int i, SortStatistics stats) {
        if(stats!=null) stats.sortGetting();
        return list.get(i);
    }

    protected static <T> T get(T[] array, int i, SortStatistics stats) {
        if(stats!=null) stats.sortGetting();
        return array[i];
    }

    protected static <T> T set(List<T> list, int i, T O, SortStatistics stats) {
        if(stats!=null) stats.sortSetting();
        return list.set(i, O);
    }

    protected static <T> T set(T[] array, int i, T O, SortStatistics stats) {
        if(stats!=null) stats.sortSetting();
        return array[i]=O;
    }

    protected static <T> void swap(List<T> list, int i, int j, SortStatistics stats) {
        if(stats!=null) stats.sortSwapping();

        if(list instanceof FileList) ((FileList)list).swap(i, j);
        else {
            T T=list.get(i);
            list.set(i, list.get(j));
            list.set(j, T);
        }
    }

    protected static <T> void swap(T[] array, int i, int j, SortStatistics stats) {
        if(stats!=null) stats.sortSwapping();

        T T=array[i];
        array[i]=array[j];
        array[j]=T;
    }
}