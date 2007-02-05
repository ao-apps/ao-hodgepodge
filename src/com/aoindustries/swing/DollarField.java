package com.aoindustries.swing;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class DollarField extends AutoSelectTextField {

    private final boolean allowNull, positiveOnly;

    public DollarField(boolean allowNull, boolean positiveOnly) {
        this(allowNull, positiveOnly, 7);
    }

    public DollarField(boolean allowNull, boolean positiveOnly, int columns) {
        super(columns);
        this.allowNull=allowNull;
        this.positiveOnly=positiveOnly;
        setInputVerifier(new DollarInputVerifier(allowNull, positiveOnly));
    }
    
    public void setDollar(int pennies) throws IllegalArgumentException {
        if(allowNull && pennies==-1) setText("");
        else if(positiveOnly && pennies<0) throw new IllegalArgumentException("Amount must be greater than or equal to zero when configured as positive only, pennies="+pennies);
        else setText(SQLUtility.getDecimal(pennies));
    }
    
    public int getDollar() throws IllegalArgumentException {
        String S=getText();
        while(S.length()>0) {
            S.trim();
            if(S.length()>0 && S.charAt(0)=='$') S=S.substring(1);
            else break;
        }
        if(S.length()==0) {
            if(allowNull) return -1;
            throw new IllegalArgumentException("Dollar value may not be empty.");
        }
        int pennies=SQLUtility.getPennies(S);
        if(pennies<0) {
            if(allowNull && pennies==-1) throw new IllegalArgumentException("Dollar value not allowed because field is nullable: "+S);
            if(positiveOnly) throw new IllegalArgumentException("Dollar value may not be negative.");
        }
        return pennies;
    }
}
