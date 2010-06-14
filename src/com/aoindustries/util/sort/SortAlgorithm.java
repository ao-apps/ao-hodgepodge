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
package com.aoindustries.util.sort;

import com.aoindustries.io.*;
import java.util.*;

/**
 * Generalized structure for sort algorithms.
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