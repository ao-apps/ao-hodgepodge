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

import com.aoindustries.reflect.*;
import com.aoindustries.table.*;
import com.aoindustries.util.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * @author  AO Industries, Inc.
 */
public class FilteredTableModel<T extends Row> extends AbstractTableModel implements AncestorListener, TableListener {

    private final Table<T> table;
    private final String[] columnHeaders;
    private final Type[] columnTypes;
    private final MethodCall[] getValueMethods;
    private final MethodCall[] setValueMethods;
    private final Table[] invalidateTables;

    private final String[] filters;
    private final DataFilter[] dataFilters;
    
    private List<T> filteredCache;

    public FilteredTableModel(
        Table<T> table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods,
        Table[] invalidateTables
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

    public synchronized List<T> getFilteredRows() throws IOException, SQLException {
        boolean isFiltered=false;
        for(int c=0;c<filters.length;c++) {
            if(filters[c]!=null) {
                isFiltered=true;
                break;
            }
        }
        List<T> objs=table.getRows();
        if(!isFiltered) return objs;
        
        if(filteredCache!=null) return filteredCache;
        
        for(int c=0;c<dataFilters.length;c++) {
            dataFilters[c]=DataFilter.getDataFilter(columnTypes[c].getTypeClass(), filters[c]);
        }

        List<T> matches=new ArrayList<T>();
        int len=objs.size();
        for(int c=0;c<len;c++) {
            T row=objs.get(c);
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
        try {
            return getFilteredRows().size()+1;
        } catch(IOException err) {
            throw new WrappedException(err);
        } catch(SQLException err) {
            throw new WrappedException(err);
        }
    }

    public String getColumnName(int col) {
        return columnHeaders[col];
    }

    public Class getColumnClass(int col) {
        return columnTypes[col].getTypeClass();
    }

    public Type getType(int col) {
        return columnTypes[col];
    }

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
            try {
                row--;
                List rows=getFilteredRows();
                return columnTypes[col].getDisplay(getRowValue((Row)rows.get(row), col));
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        }
    }

    /**
     * Gets the object used for sorting and stuff.
     */
    public Object getObjectAt(int row, int col) {
        if(row==0) return filters[col];
        else {
            try {
                row--;
                List rows=getFilteredRows();
                return getRowValue((Row)rows.get(row), col);
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
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
            try {
                if(setValueMethods==null) throw new RuntimeException("setValueAt(Object,int,int) should not have been called because setValueMethods is null");
                MethodCall setMethod=setValueMethods[col];
                if(setMethod==null) throw new RuntimeException("setValueAt(Object,int,int) should not have been called for column "+col+" because setValueMethods["+col+"] is null");

                Row R=(Row)getFilteredRows().get(row);
                setMethod.invokeOn(R, new Object[] {value});
            } catch(IOException err) {
                throw new WrappedException(err);
            } catch(SQLException err) {
                throw new WrappedException(err);
            }
        }
    }

    public void ancestorAdded(AncestorEvent e) {
        if(invalidateTables!=null) {
            for(int c=0;c<invalidateTables.length;c++) {
                invalidateTables[c].addTableListener(this, 0);
            }
            tableUpdated(null);
        }
    }

    public void ancestorMoved(AncestorEvent e) {
    }

    public void ancestorRemoved(AncestorEvent e) {
        if(invalidateTables!=null) {
            for(int c=0;c<invalidateTables.length;c++) {
                invalidateTables[c].removeTableListener(this);
            }
        }
    }

    final public void tableUpdated(Table table) {
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
