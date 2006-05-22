package com.aoindustries.swing.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * @author  AO Industries, Inc.
 */
class BeveledTableCellRenderer implements TableCellRenderer {

    private TableCellRenderer defaultRenderer;
    private int type;

    public BeveledTableCellRenderer(TableCellRenderer defaultRenderer, int type) {
        this.defaultRenderer=defaultRenderer;
        this.type=type;
    }
    
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        JComponent component=(JComponent)defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        JPanel panel=new JPanel(new BorderLayout());
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }
}
