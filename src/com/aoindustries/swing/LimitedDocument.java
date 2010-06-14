/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author  AO Industries, Inc.
 */
public class LimitedDocument extends PlainDocument {

    private int limit;
    private boolean numbersOnly;
  
    public LimitedDocument(int limit) {
        this(limit, false);
    }

    public LimitedDocument(int limit, boolean numbersOnly) {
        this.limit=limit;
        this.numbersOnly=numbersOnly;
    }

    @Override
    public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
        if(str==null) return;
        if((getLength()+str.length())<=limit) {
            boolean insertOK=true;
            if(numbersOnly) {
                for(int c=0;c<str.length();c++) {
                    char ch=str.charAt(c);
                    if(ch<'0' || ch>'9') {
                        insertOK=false;
                        break;
                    }
                }
            }
            if(insertOK) super.insertString(offset, str, attr);
        }
    }
}
