/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.sort;

import java.util.Comparator;
import java.util.List;

/**
 * Generalized structure for sort algorithms that are based on comparisons.
 *
 * @author  AO Industries, Inc.
 */
abstract class BaseComparisonSortAlgorithm<E> extends BaseSortAlgorithm<E> implements ComparisonSortAlgorithm<E> {

  protected BaseComparisonSortAlgorithm() {
    // Do nothing
  }

  @Override
  public <T extends E> void sort(List<T> list) {
    sort(list, null, null);
  }

  @Override
  public <T extends E> void sort(T[] array) {
    sort(array, null, null);
  }

  @Override
  public <T extends E> void sort(List<T> list, SortStatistics stats) {
    sort(list, null, stats);
  }

  @Override
  public <T extends E> void sort(T[] array, SortStatistics stats) {
    sort(array, null, stats);
  }

  @Override
  public <T extends E> void sort(List<T> list, Comparator<? super T> comparator) {
    sort(list, comparator, null);
  }

  @Override
  public <T extends E> void sort(T[] array, Comparator<? super T> comparator) {
    sort(array, comparator, null);
  }

  @Override
  public abstract <T extends E> void sort(List<T> list, Comparator<? super T> comparator, SortStatistics stats);

  @Override
  public abstract <T extends E> void sort(T[] array, Comparator<? super T> comparator, SortStatistics stats);

  @SuppressWarnings({"unchecked"})
  protected static <T> int compare(List<T> list, int i, int j, Comparator<? super T> comparator, SortStatistics stats) {
    if (stats != null) {
      stats.sortInListComparing();
    }

    T o1 = list.get(i);
    T o2 = list.get(j);

    if (o1 == null) {
      if (o2 == null) {
        return 0;
      } else {
        return -1;
      }
    } else {
      if (o2 == null) {
        return 1;
      } else {
        if (comparator != null) {
          return comparator.compare(o1, o2);
        } else if (o1 instanceof Comparable<?>) {
          Comparable<? super T> comp1 = (Comparable<? super T>) o1;
          return comp1.compareTo(o2);
        } else {
          throw new RuntimeException("Must either provide a Comparator or the objects must be Comparable");
        }
      }
    }
  }

  @SuppressWarnings({"unchecked"})
  protected static <T> int compare(T[] array, int i, int j, Comparator<? super T> comparator, SortStatistics stats) {
    if (stats != null) {
      stats.sortInListComparing();
    }

    T o1 = array[i];
    T o2 = array[j];

    if (o1 == null) {
      if (o2 == null) {
        return 0;
      } else {
        return -1;
      }
    } else {
      if (o2 == null) {
        return 1;
      } else {
        if (comparator != null) {
          return comparator.compare(o1, o2);
        } else if (o1 instanceof Comparable<?>) {
          Comparable<? super T> comp1 = (Comparable<? super T>) o1;
          return comp1.compareTo(o2);
        } else {
          throw new RuntimeException("Must either provide a Comparator or the objects must be Comparable");
        }
      }
    }
  }

  @SuppressWarnings({"unchecked"})
  protected static <T> int compare(T o1, T o2, Comparator<? super T> comparator, SortStatistics stats) {
    if (stats != null) {
      stats.sortObjectComparing();
    }

    if (o1 == null) {
      if (o2 == null) {
        return 0;
      } else {
        return -1;
      }
    } else {
      if (o2 == null) {
        return 1;
      } else {
        if (comparator != null) {
          return comparator.compare(o1, o2);
        } else if (o1 instanceof Comparable<?>) {
          Comparable<? super T> comp1 = (Comparable<? super T>) o1;
          return comp1.compareTo(o2);
        } else {
          throw new RuntimeException("Must either provide a Comparator or the objects must be Comparable");
        }
      }
    }
  }
}
