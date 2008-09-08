package com.aoindustries.swing;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class NumberInputVerifier extends InputVerifier {
    
    private int min;
    private int max;
    
    public NumberInputVerifier(int min, int max) {
        this.min=min;
        this.max=max;
    }

    public boolean verify(JComponent input) {
        try {
            String S=((AutoSelectTextField)input).getText().trim();
            if(S.length()==0) return true;
            int number=Integer.parseInt(S);
            return number>=min && number<=max;
        } catch(NumberFormatException err) {
            return false;
        }
    }
}