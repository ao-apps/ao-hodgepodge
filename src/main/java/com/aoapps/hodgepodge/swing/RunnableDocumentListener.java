/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2025  AO Industries, Inc.
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

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Implements {@link DocumentListener} sending all events to the given {@link Runnable} on the
 * swing event thread via {@link SwingUtilities#invokeLater(java.lang.Runnable)}.
 */
public class RunnableDocumentListener implements DocumentListener {

  private final Runnable doRun;

  public RunnableDocumentListener(Runnable doRun) {
    this.doRun = doRun;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    SwingUtilities.invokeLater(doRun);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    SwingUtilities.invokeLater(doRun);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    SwingUtilities.invokeLater(doRun);
  }
}
