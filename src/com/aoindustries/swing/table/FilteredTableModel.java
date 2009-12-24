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
import com.aoindustries.table.Row;
import com.aoindustries.table.Table;
import com.aoindustries.table.TableListener;
import com.aoindustries.table.Type;
import com.aoindustries.util.DataFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.AbstractTableModel;

/**
 * @author  AO Industries, Inc.
 */
public class FilteredTableModel<T extends Row> extends AbstractTableModel implements AncestorListener, TableListener<T> {

    private static final long serialVersionUID = 1L;

    private final Table<T> table;
    private final String[] columnHeaders;
    private final Type[] columnTypes;
    private final MethodCall[] getValueMethods;
    private final MethodCall[] setValueMethods;
    private final List<Table<T>> invalidateTables;

    private final String[] filters;
    private final DataFilter[] dataFilters;
    
    private List<T> filteredCache;

    public FilteredTableModel(
        Table<T> table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods,
        List<Table<T>> invalidateTables
    ) {
        this.table=table;
        this.columnHeaders=columnHeaders;
        this.columnTypes=columnTypes;
        this.getValueMethods=getValueMethods;
        this.setValueMethods=setValueMethods;
        this.invalidateTables=invalidateTables;
        
        int cols=columnHeaders.length;
        if(columnTypes.length!=cols) throw new IllegalArgumentException("(columnHeaders.length="+cols+")!=(columnTypes.length="+columnTypes.length+")");
        if(getValueMethods.length!=cols) throw new IllegalArgumentException("(columnHeaders.length="+cols+")!=(getValueMethods.length="+getValueMethods.length+")");
        if(setValueMethods!=null && setValueMethods.length!=cols) throw new IllegalArgumentException("(columnHeaders.length="+cols+")!=(setValueMethods.length="+setValueMethods.length+")");

        filters=new String[cols];
        dataFilters=new DataFilter[cols];
    }

    public synchronized List<T> getFilteredRows() {
        boolean isFiltered=false;
        for(int c=0;c<filters.length;c++) {
            if(filters[c]!=null) {
                isFiltered=true;
                break;
            }
        }
        Iterator<? extends T> iter=table.getRows();
        if(!isFiltered) {
            List<T> objs = new ArrayList<T>();
            while(iter.hasNext()) objs.add(iter.next());
            return objs;
        }

        if(filteredCache!=null) return filteredCache;
        
        for(int c=0;c<dataFilters.length;c++) {
            dataFilters[c]=DataFilter.getDataFilter(columnTypes[c].getTypeClass(), filters[c]);
        }

        List<T> matches=new ArrayList<T>();
        while(iter.hasNext()) {
            T row=iter.next();
            boolean isMatch=true;
            for(int d=0;d<dataFilters.length;d++) {
                DataFilter filter=dataFilters[d];
                if(filter!=null && !filter.matches(getRowValue(row, d))) {
                    isMatch=false;
                    break;
                }
            }
            if(isMatch) matches.add(row);
        }
        filteredCache=matches;

        return matches;
    }

    public Object getRowValue(Row row, int col) {
        return getValueMethods[col].invokeOn(row);
    }

    public int getColumnCount() {
        return columnHeaders.length;
    }

    public int getRowCount() {
        return getFilteredRows().size()+1;
    }

    @Override
    public String getColumnName(int col) {
        return columnHeaders[col];
    }

    @Override
    public Class getColumnClass(int col) {
        return columnTypes[col].getTypeClass();
    }

    public Type getType(int col) {
        return columnTypes[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        if(row==0) return true;
        return setValueMethods!=null && setValueMethods[col]!=null;
    }

    /**
     * Gets the value used for display.
     */
    public Object getValueAt(int row, int col) {
        if(row==0) {
            return filters[col];
        } else {
            row--;
            List rows=getFilteredRows();
            return columnTypes[col].getDisplay(getRowValue((Row)rows.get(row), col));
        }
    }

    /**
     * Gets the object used for sorting and stuff.
     */
    public Object getObjectAt(int row, int col) {
        if(row==0) return filters[col];
        else {
            row--;
            List rows=getFilteredRows();
            return getRowValue((Row)rows.get(row), col);
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if(row==0) {
            if(value==null) filters[col]=null;
            else {
                String S=value.toString();
                if(S.length()==0) filters[col]=null;
                else filters[col]=S;
            }
            filteredCache=null;
            fireTableDataChanged();
        } else {
            row--;
            if(setValueMethods==null) throw new RuntimeException("setValueAt(Object,int,int) should not have been called because setValueMethods is null");
            MethodCall setMethod=setValueMethods[col];
            if(setMethod==null) throw new RuntimeException("setValueAt(Object,int,int) should not have been called for column "+col+" because setValueMethods["+col+"] is null");

            Row R=(Row)getFilteredRows().get(row);
            setMethod.invokeOn(R, new Object[] {value});
        }
    }

    public void ancestorAdded(AncestorEvent e) {
        if(invalidateTables!=null) {
            for(int c=0;c<invalidateTables.size();c++) {
                invalidateTables.get(c).addTableListener(this, 0);
            }
            tableUpdated(null);
        }
    }

    public void ancestorMoved(AncestorEvent e) {
    }

    public void ancestorRemoved(AncestorEvent e) {
        if(invalidateTables!=null) {
            for(int c=0;c<invalidateTables.size();c++) {
                invalidateTables.get(c).removeTableListener(this);
            }
        }
    }

    final public void tableUpdated(Table<T> table) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    filteredCache=null;
                    fireTableDataChanged();
                }
            }
        );
    }
}
