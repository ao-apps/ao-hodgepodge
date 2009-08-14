/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
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