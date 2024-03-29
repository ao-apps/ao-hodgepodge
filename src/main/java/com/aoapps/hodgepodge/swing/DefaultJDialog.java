/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2021, 2022  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author  AO Industries, Inc.
 */
public class DefaultJDialog extends JDialog implements WindowListener, ComponentListener {

  public static final int DEFAULT_MIN_WIDTH = 800;
  public static final int DEFAULT_MIN_HEIGHT = 600;

  private static final long serialVersionUID = 1L;

  private final int minWidth;
  private final int minHeight;

  public DefaultJDialog(JFrame parent, String title, boolean modal) {
    this(parent, title, modal, DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT);
  }

  public DefaultJDialog(JFrame parent, String title, boolean modal, int minWidth, int minHeight) {
    super(parent, title, modal);
    this.minWidth = minWidth;
    this.minHeight = minHeight;
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(this);
    addComponentListener(this);
  }

  public final void center(Component parent) {
    Rectangle parentBounds = parent.getBounds();
    Dimension size = getSize();
    setBounds(
        parentBounds.x + (parentBounds.width - size.width) / 2,
        parentBounds.y + (parentBounds.height - size.height) / 2,
        size.width,
        size.height
    );
  }

  @Override
  public void windowClosing(WindowEvent e) {
    Object source = e.getSource();
    if (source == this) {
      closeWindow();
    }
  }

  public final void closeWindow() {
    setVisible(false);
    dispose();
  }

  @Override
  public void windowOpened(WindowEvent e) {
    // Do nothing
  }

  @Override
  public void windowClosed(WindowEvent e) {
    // Do nothing
  }

  @Override
  public void windowIconified(WindowEvent e) {
    // Do nothing
  }

  @Override
  public void windowDeiconified(WindowEvent e) {
    // Do nothing
  }

  @Override
  public void windowActivated(WindowEvent e) {
    // Do nothing
  }

  @Override
  public void windowDeactivated(WindowEvent e) {
    // Do nothing
  }

  @Override
  public void componentShown(ComponentEvent e) {
    // Do nothing
  }

  @Override
  public void componentResized(ComponentEvent e) {
    checkMin();
  }

  private void checkMin() {
    int width = getWidth();
    int height = getHeight();
    if (width < minWidth || height < minHeight) {
      setSize(Math.max(width, minWidth), Math.max(height, minHeight));
    }
  }

  @Override
  public void componentMoved(ComponentEvent e) {
    // Do nothing
  }

  @Override
  public void componentHidden(ComponentEvent e) {
    // Do nothing
  }

  @Override
  public void pack() {
    super.pack();
    checkMin();
  }
}
