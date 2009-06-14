package com.aoindustries.swing;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
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
public class DefaultJDialog extends JDialog implements WindowListener, ComponentListener {

    public static final int
        DEFAULT_MIN_WIDTH=800,
        DEFAULT_MIN_HEIGHT=600
    ;

    private final int minWidth, minHeight;

    public DefaultJDialog(JFrame parent, String title, boolean modal) {
        this(parent, title, modal, DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
    }
    
    public DefaultJDialog(JFrame parent, String title, boolean modal, int minWidth, int minHeight) {
        super(parent, title, modal);
        this.minWidth=minWidth;
        this.minHeight=minHeight;
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        addComponentListener(this);
    }

    final public void center(Component parent) {
        Rectangle parentBounds=parent.getBounds();
        Dimension size=getSize();
        setBounds(
            parentBounds.x+(parentBounds.width-size.width)/2, 
            parentBounds.y+(parentBounds.height-size.height)/2, 
            size.width,
            size.height
        );
    }

    public void windowClosing(WindowEvent e) {
        Object source=e.getSource();
        if(source==this) closeWindow();
    }
    
    final public void closeWindow() {
        setVisible(false);
        dispose();
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
    
    public void componentShown(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        checkMin();
    }

    private void checkMin() {
        int width=getWidth();
        int height=getHeight();
        if(width<minWidth || height<minHeight) setSize(Math.max(width, minWidth), Math.max(height, minHeight));
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }
    
    @Override
    public void pack() {
        super.pack();
        checkMin();
    }
}