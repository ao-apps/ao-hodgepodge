package com.aoindustries.awt;

/*
 * Copyright 2000-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import java.awt.event.*;

/**
 * Subclasses SystemFrame to automatically close the window.  Provides a
 * means of checking if the window was canceled.
 *
 * @version  1.0
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