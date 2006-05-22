package com.aoindustries.swing.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.swing.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * @author  AO Industries, Inc.
 */
class SortTableCellRenderer implements TableCellRenderer {

    private TableCellRenderer defaultRenderer;
    private TableSorter sorter;

    public SortTableCellRenderer(
        TableCellRenderer defaultRenderer,
        TableSorter sorter
    ) {
        this.defaultRenderer=defaultRenderer;
        this.sorter=sorter;
    }

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        JPanel panel=new JPanel(new BorderLayout());

        Component defaultComponent=defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        panel.add(defaultComponent, BorderLayout.CENTER);
        
        int direction=sorter.getLastSortColumn()!=column?Arrow.NONE:sorter.isLastAscending()?Arrow.DOWN:Arrow.UP;
        Border border=((JComponent)defaultComponent).getBorder();
        panel.add(new Arrow(direction, border), BorderLayout.EAST);

        return panel;
    }
}
