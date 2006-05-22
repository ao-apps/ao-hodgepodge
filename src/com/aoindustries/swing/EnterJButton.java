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
public class EnterJButton extends JButton implements KeyListener {
    
    public EnterJButton() {
        super();
        init();
    }
    
    public EnterJButton(String text) {
        super(text);
        init();
    }
    
    public EnterJButton(String text, Icon icon) {
        super(text, icon);
        init();
    }
    
    public EnterJButton(Action action) {
        super(action);
        init();
    }
    
    public EnterJButton(Icon icon) {
        super(icon);
        init();
    }

    public void init() {
        addKeyListener(this);
    }
    
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar()=='\n') fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText()));
    }

    public void keyPressed(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
    }
}