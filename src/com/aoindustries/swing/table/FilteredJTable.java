package com.aoindustries.swing.table;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.reflect.MethodCall;
import com.aoindustries.table.Row;
import com.aoindustries.table.Table;
import com.aoindustries.table.Type;
import java.awt.Dimension;
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
public class FilteredJTable<T extends Row> extends JTable {

    private static final long serialVersionUID = 1L;

    public FilteredJTable(
        Table<T> table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods,
        Table[] invalidateTables
    ) {
        FilteredTableModel tableModel=new FilteredTableModel<T>(
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
        Table<T> table,
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
            new Table[] {table}
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

    private static final JScrollBar tempSB=new JScrollBar(JScrollBar.VERTICAL);

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension D1=getUI().getPreferredSize(this);
        Dimension D2=tempSB.getUI().getPreferredSize(tempSB);
        Dimension D=new Dimension(D1.width+D2.width, D1.height);
        return D;
    }
}
