/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.swing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author  AO Industries, Inc.
 */
public class EnterJButton extends JButton implements KeyListener {

	private static final long serialVersionUID = 1L;

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

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar()=='\n') fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getText()));
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
