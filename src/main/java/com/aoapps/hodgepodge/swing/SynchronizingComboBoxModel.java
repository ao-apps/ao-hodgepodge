/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.swing;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;

/**
 * When requested, it will synchronize its list of elements to an externally-
 * provided list.  The is useful when the data source is obtained elsewhere and
 * the list-based component needs to be synchronized.
 *
 * Since these updates may occur while a user is manipulating the components,
 * only the minimum number of changes to the elements is made.  Thus selections
 * and other aspects of the component remain intact.  Most importantly, if nothing
 * in the list has changed, the component is not changed.
 *
 * @author  AO Industries, Inc.
 */
public class SynchronizingComboBoxModel<E> extends DefaultComboBoxModel<E> {

  private static final long serialVersionUID = 2421298474426921512L;

  private final E constantFirstRow;

  public SynchronizingComboBoxModel() {
    constantFirstRow = null;
  }

  public SynchronizingComboBoxModel(E constantFirstRow) {
    this.constantFirstRow = constantFirstRow;
    addElement(constantFirstRow);
  }

  /**
   * Synchronizes the list, adding and removing only a minimum number of elements.
   * Comparisons are performed using .equals.  This must be called from the
   * Swing event dispatch thread.
   */
  public void synchronize(List<? extends E> list) {
    assert SwingUtilities.isEventDispatchThread() : Resources.PACKAGE_RESOURCES.getMessage("assert.notRunningInSwingEventThread");

    // Make sure the first element exists and matches
    int modelOffset;
    if (constantFirstRow != null) {
      modelOffset = 1;
      if (getSize() == 0) {
        addElement(constantFirstRow);
      } else if (!getElementAt(0).equals(constantFirstRow)) {
        insertElementAt(constantFirstRow, 0);
      }
    } else {
      modelOffset = 0;
    }

    // Synchronize the dynamic part of the list
    int size = list.size();
    for (int index=0; index<size; index++) {
      E obj = list.get(index);
      if (index >= (getSize()-modelOffset)) {
        addElement(obj);
      } else if (!obj.equals(getElementAt(index+modelOffset))) {
        // Objects don't match
        // If this object is found further down the list, then delete up to that object
        int foundIndex = -1;
        for (int searchIndex = index+1; searchIndex<(getSize()-modelOffset); searchIndex++) {
          if (obj.equals(getElementAt(searchIndex+modelOffset))) {
            foundIndex = searchIndex;
            break;
          }
        }
        if (foundIndex != -1) {
          // No removeRange
          // removeRange(index+modelOffset, foundIndex-1+modelOffset);
          for (
            int removeIndex = foundIndex-1+modelOffset, end = index+modelOffset;
            removeIndex >= end;
            removeIndex--
          ) {
            removeElementAt(removeIndex);
          }
        } else {
          // Otherwise, insert in the current index
          insertElementAt(obj, index+modelOffset);
        }
      }
    }
    // Remove any extra
    while ((getSize() - modelOffset) > size) {
      removeElementAt(getSize() - 1);
    }
  }
}
