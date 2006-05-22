package com.aoindustries.swing.table;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.reflect.*;
import com.aoindustries.table.*;
import com.aoindustries.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * TODO: Make the columns automatically big enough for the table contents whenever date reloaded, growing but not shrinking.
 * TODO: selection is lost on data reload
 *
 * @author  AO Industries, Inc.
 */
public class FilteredJTable extends JTable {

    public FilteredJTable(
        ErrorHandler errorHandler,
        Table table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods,
        Table[] invalidateTables
    ) {
        FilteredTableModel tableModel=new FilteredTableModel(
            errorHandler,
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
        ErrorHandler errorHandler,
        Table table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods
    ) {
        this(
            errorHandler,
            table,
            columnHeaders,
            columnTypes,
            getValueMethods,
            setValueMethods,
            new Table[] {table}
        );
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if(row==0) return new BeveledTableCellRenderer(getDefaultRenderer(Object.class), BevelBorder.LOWERED);
        return super.getCellRenderer(row, column);
    }

    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        setFirstRowHeight();
    }
    
    public void setFirstRowHeight() {
        setRowHeight(0, getRowHeight()+4);
    }
    
    public int getSelectedRow() {
        int selectedRow=super.getSelectedRow();

        if(selectedRow<=0) return selectedRow;

        TableSorter sorter=(TableSorter)getModel();
        return sorter.convertToInnerIndex(selectedRow);
    }

    private static final JScrollBar tempSB=new JScrollBar(JScrollBar.VERTICAL);

    public Dimension getPreferredScrollableViewportSize() {
        Dimension D1=getUI().getPreferredSize(this);
        Dimension D2=tempSB.getUI().getPreferredSize(tempSB);
        Dimension D=new Dimension(D1.width+D2.width, D1.height);
        return D;
    }
}
