package com.aoindustries.swing;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

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