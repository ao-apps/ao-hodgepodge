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

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

/**
 * Extends {@link JTextField} with the following features.
 *
 * <ol>
 * <li>Automatic selection when gains focus.</li>
 * </ol>
 *
 * @author  AO Industries, Inc.
 */
public class AoTextField extends JTextField {

  private static final long serialVersionUID = 1L;

  public AoTextField() {
    super();
    init();
  }

  public AoTextField(String text) {
    super(text);
    init();
  }

  public AoTextField(int columns) {
    super(columns);
    init();
  }

  public AoTextField(String text, int columns) {
    super(text, columns);
    init();
  }

  public AoTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    init();
  }

  private void init() {
    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        // Do in later event so other FocusListener may modify content before focus is determined.
        SwingUtilities.invokeLater(() -> {
          if (isFocusOwner() && getSelectedText() == null) {
            selectAll();
          }
        });
      }
    });
  }
}
