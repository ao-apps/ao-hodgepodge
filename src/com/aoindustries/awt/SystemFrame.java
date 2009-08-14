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

/**
 * Defaults to the SystemColor's
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
