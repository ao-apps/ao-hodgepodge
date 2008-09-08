package com.aoindustries.swing;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.*;
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class DollarInputVerifier extends InputVerifier {

    private final boolean allowNull, positiveOnly;
    
    public DollarInputVerifier(boolean allowNull, boolean positiveOnly) {
        this.allowNull=allowNull;
        this.positiveOnly=positiveOnly;
    }

    public boolean verify(JComponent input) {
        try {
            String S=((AutoSelectTextField)input).getText();
            while(S.length()>0) {
                S.trim();
                if(S.length()>0 && S.charAt(0)=='$') S=S.substring(1);
                else break;
            }
            if(S.length()==0) {
                if(allowNull) return true;
                return false;
            }
            int pennies=SQLUtility.getPennies(S);
            if(pennies<0) {
                if(allowNull && pennies==-1) return false;
                if(positiveOnly) return false;
            }
            return true;
        } catch(NumberFormatException err) {
            return false;
        }
    }
}
