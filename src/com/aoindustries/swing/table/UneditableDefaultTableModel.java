package com.aoindustries.swing.table;

/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import javax.swing.table.DefaultTableModel;

/**
 * Makes an uneditable version of DefaultTableModel.
 *
 * @author  AO Industries, Inc.
 */
public class UneditableDefaultTableModel extends DefaultTableModel {

    public UneditableDefaultTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public UneditableDefaultTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}
