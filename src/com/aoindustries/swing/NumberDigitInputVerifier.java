package com.aoindustries.swing;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class NumberDigitInputVerifier extends InputVerifier {
    
    private int minDigits;
    private int maxDigits;
    
    public NumberDigitInputVerifier(int minDigits, int maxDigits) {
        this.minDigits=minDigits;
        this.maxDigits=maxDigits;
    }

    public boolean verify(JComponent input) {
        return verify(((AutoSelectTextField)input).getText().trim(), minDigits, maxDigits);
    }
    
    public static boolean verify(String S, int minDigits, int maxDigits) {
        try {
            if(S.length()==0) return true;
            
            // Every digit must be a number
            for(int c=0;c<S.length();c++) {
                char ch=S.charAt(c);
                if(ch<'0' || ch>'9') return false;
            }
            
            return S.length()>=minDigits && S.length()<=maxDigits;
        } catch(NumberFormatException err) {
            return false;
        }
    }
}