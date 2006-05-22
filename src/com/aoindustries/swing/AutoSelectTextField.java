package com.aoindustries.swing;

/*
 * Copyright 2003-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class AutoSelectTextField extends JTextField implements FocusListener {

    public AutoSelectTextField(int columns) {
        super(columns);
        addFocusListener(this);
    }

    public void focusGained(FocusEvent e) {
        selectAll();
    }

    public void focusLost(FocusEvent e) {
    }
    
    public Dimension getPreferredSize() {
        Dimension pref=super.getPreferredSize();
        return new Dimension(pref.width, pref.height+2);
    }
}