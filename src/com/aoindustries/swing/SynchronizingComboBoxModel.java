package com.aoindustries.swing;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.util.List;
import java.util.Locale;
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
public class SynchronizingComboBoxModel extends DefaultComboBoxModel {

    private final Object constantFirstRow;

    public SynchronizingComboBoxModel() {
        constantFirstRow = null;
    }

    public SynchronizingComboBoxModel(Object constantFirstRow) {
        this.constantFirstRow = constantFirstRow;
        addElement(constantFirstRow);
    }

    /**
     * Synchronizes the list, adding and removing only a minimum number of elements.
     * Comparisons are performed using .equals.  This must be called from the
     * Swing event dispatch thread.
     */
    public void synchronize(List<?> list) {
        assert SwingUtilities.isEventDispatchThread() : ApplicationResourcesAccessor.getMessage(Locale.getDefault(), "assert.notRunningInSwingEventThread");

        // Make sure the first element exists and matches
        int modelOffset;
        if(constantFirstRow!=null) {
            modelOffset = 1;
            if(getSize()==0) addElement(constantFirstRow);
            else if(!getElementAt(0).equals(constantFirstRow)) {
                insertElementAt(constantFirstRow, 0);
            }
        } else modelOffset = 0;

        // Synchronize the dynamic part of the list
        int size = list.size();
        for(int index=0; index<size; index++) {
            Object obj = list.get(index);
            if(index>=(getSize()-modelOffset)) addElement(obj);
            else if(!obj.equals(getElementAt(index+modelOffset))) {
                // Objects don't match
                // If this object is found further down the list, then delete up to that object
                int foundIndex = -1;
                for(int searchIndex = index+1; searchIndex<(getSize()-modelOffset); searchIndex++) {
                    if(obj.equals(getElementAt(searchIndex+modelOffset))) {
                        foundIndex = searchIndex;
                        break;
                    }
                }
                if(foundIndex!=-1) {
                    // No removeRange
                    // removeRange(index+modelOffset, foundIndex-1+modelOffset);
                    for(
                        int removeIndex = foundIndex-1+modelOffset, end = index+modelOffset;
                        removeIndex>=end;
                        removeIndex--
                    ) removeElementAt(removeIndex);
                } else {
                    // Otherwise, insert in the current index
                    insertElementAt(obj, index+modelOffset);
                }
            }
        }
        // Remove any extra
        while((getSize()-modelOffset) > size) removeElementAt(getSize()-1);
    }
}
