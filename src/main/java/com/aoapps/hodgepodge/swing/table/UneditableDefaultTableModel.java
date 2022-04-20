/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011, 2016, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.swing.table;

import javax.swing.table.DefaultTableModel;

/**
 * Makes an uneditable version of DefaultTableModel.
 *
 * @author  AO Industries, Inc.
 */
public class UneditableDefaultTableModel extends DefaultTableModel {

  private static final long serialVersionUID = 1L;

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
