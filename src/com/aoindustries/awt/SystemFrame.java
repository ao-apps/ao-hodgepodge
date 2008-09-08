package com.aoindustries.awt;

/*
 * Copyright 2000-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.*;

/**
 * Defaults to the SystemColor's
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class SystemFrame extends Frame {
public SystemFrame() {
	setBackground(SystemColor.text);
	setForeground(SystemColor.textText);
}
public SystemFrame(String title) {
	this();
	setTitle(title);
}
/**
 * Shows the default cursor
 */
public void cursorOff() {
	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}
/**
 * Shows the wait cursor
 */
public void cursorOn() {
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
}
/**
 * Makes the window take all the available space on the screen.  This is an approximation and will not
 * necessarily be exact in all environments.
 */
public void maximize() {
	Dimension D=Toolkit.getDefaultToolkit().getScreenSize();
	Insets I=getInsets();
	setBounds(0,0,D.width-I.left-I.right,D.height-I.top-I.bottom-48);
}
}
