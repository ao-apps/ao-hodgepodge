package com.aoindustries.swing;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import javax.swing.text.*;

/**
 * @version  1.0
 *
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
