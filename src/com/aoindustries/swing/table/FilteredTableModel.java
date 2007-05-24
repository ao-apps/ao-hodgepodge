package com.aoindustries.swing.table;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
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

    private final ErrorHandler errorHandler;
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
        ErrorHandler errorHandler,
        Table<T> table,
        String[] columnHeaders,
        Type[] columnTypes,
        MethodCall[] getValueMethods,
        MethodCall[] setValueMethods,
        Table[] invalidateTables
    ) {
        this.errorHandler=errorHandler;
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
            errorHandler.reportError(err, null);
        } catch(SQLException err) {
            errorHandler.reportError(err, null);
        }
        return 1;
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
                errorHandler.reportError(err, null);
            } catch(SQLException err) {
                errorHandler.reportError(err, null);
            }
            return null;
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
                errorHandler.reportError(err, null);
                return null;
            } catch(SQLException err) {
                errorHandler.reportError(err, null);
                return null;
            }
        }
    }

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
                errorHandler.reportError(err, null);
            } catch(SQLException err) {
                errorHandler.reportError(err, null);
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
