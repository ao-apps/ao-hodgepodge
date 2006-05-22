package com.aoindustries.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * A primitive collection.
 *
 * @see  Collection
 */
public interface IntCollection extends Collection<Integer> {

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     *
     * @param elem element whose presence in this List is to be tested.
     * @return  <code>true</code> if the specified element is present;
     *		<code>false</code> otherwise.
     */
    boolean contains(int elem);

    /**
     * Returns an array containing all of the elements in this list
     * in the correct order.
     *
     * @return an array containing all of the elements in this list
     * 	       in the correct order.
     */
    int[] toArrayInt();

    // Positional Access Operations

    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    boolean add(int o);

    /**
     * Removes a single instance of the specified element from this
     * list, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that <tt>(o==null ? e==null :
     * o.equals(e))</tt>, if the list contains one or more such
     * elements.  Returns <tt>true</tt> if the list contained the
     * specified element (or equivalently, if the list changed as a
     * result of the call).<p>
     *
     * @param value element to be removed from this list, if present.
     * @return <tt>true</tt> if the list contained the specified element.
     */
    boolean removeByValue(int value);
}
