/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.awt;

import java.awt.*;
import java.awt.event.*;

/**
 * Subclasses SystemFrame to automatically close the window.  Provides a
 * means of checking if the window was canceled.
 *
 * @author  AO Industries, Inc.
 */
public class AutoFrame extends SystemFrame implements WindowListener {
    private static int frameCount=0;

    public AutoFrame() {
	addWindowListener(this);
    }

    public AutoFrame(String title) {
	this();
	setTitle(title);
    }

    /**
     * Centers the <code>Window</code> on the screen.
     */
    public void center() {
	Dimension screen=getToolkit().getScreenSize();
	pack();
	Dimension size=getSize();
	setLocation(
            (screen.width-size.width)/2,
            (screen.height-size.height)/2
	);
    }

    private static synchronized void decCount() {
	if(--frameCount<=0) {
            try {
                System.exit(0);
            } catch(SecurityException e) {
            }
	}
    }

    synchronized private static void incCount() {
	frameCount++;
    }

    public void setVisible(boolean vis) {
	if(vis) {
            if(!isVisible()) incCount();
	} else {
            if(isVisible()) decCount();
	}
	super.setVisible(vis);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
	setVisible(false);
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }
}