/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.swing.table;

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
