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

import com.aoindustries.reflect.MethodCall;
import com.aoindustries.table.Column;
import com.aoindustries.table.Row;
import com.aoindustries.table.Table;
import com.aoindustries.table.Type;
import java.awt.Dimension;
import java.util.Collections;
import java.util.List;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * TODO: Make the columns automatically big enough for the table contents whenever date reloaded, growing but not shrinking.
 * TODO: selection is lost on data reload
 *
 * @author  AO Industries, Inc.
 */
public class FilteredJTable<C extends Column, T extends Row> extends JTable {

    private static final long serialVersionUID = 1L;

    public FilteredJTable(
        Table<C,T> table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods,
        List<Table<C,T>> invalidateTables
    ) {
        FilteredTableModel tableModel=new FilteredTableModel<C,T>(
            table,
            columnHeaders,
            columnTypes,
            getValueMethods,
            setValueMethods,
            invalidateTables
        );
        TableSorter sorter=new TableSorter(1, tableModel);
        setModel(sorter);
        setFirstRowHeight();

        setAutoResizeMode(AUTO_RESIZE_OFF);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        addAncestorListener(tableModel);
        sorter.addMouseListenerToHeaderInTable(this);

        JTableHeader header=getTableHeader();
        TableCellRenderer defaultRenderer=tableHeader.getDefaultRenderer();
        tableHeader.setDefaultRenderer(new SortTableCellRenderer(defaultRenderer, sorter));
    }

    public FilteredJTable(
        Table<C,T> table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods
    ) {
        this(
            table,
            columnHeaders,
            columnTypes,
            getValueMethods,
            setValueMethods,
            Collections.singletonList(table)
        );
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if(row==0) return new BeveledTableCellRenderer(getDefaultRenderer(Object.class), BevelBorder.LOWERED);
        return super.getCellRenderer(row, column);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        setFirstRowHeight();
    }
    
    public void setFirstRowHeight() {
        setRowHeight(0, getRowHeight()+4);
    }
    
    @Override
    public int getSelectedRow() {
        int selectedRow=super.getSelectedRow();

        if(selectedRow<=0) return selectedRow;

        TableSorter sorter=(TableSorter)getModel();
        return sorter.convertToInnerIndex(selectedRow);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension D1=getUI().getPreferredSize(this);
        JScrollBar tempSB=new JScrollBar(JScrollBar.VERTICAL);
        Dimension D2=tempSB.getUI().getPreferredSize(tempSB);
        Dimension D=new Dimension(D1.width+D2.width, D1.height);
        return D;
    }
}
