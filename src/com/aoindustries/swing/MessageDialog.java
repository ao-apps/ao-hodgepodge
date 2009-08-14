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

import com.aoindustries.util.StringUtility;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author  AO Industries, Inc.
 */
public class MessageDialog extends DefaultJDialog implements ActionListener {

    private static final long serialVersionUID = 2L;

    private EnterJButton okButton;

    public MessageDialog(JFrame parent, String title, String message) {
        super(parent, title, true, -1, -1);

        Container contentPane=getContentPane();
        contentPane.setLayout(new BorderLayout());
        List<String> lines=StringUtility.splitLines(message);
        JPanel linesPanel=new JPanel(new GridLayout(lines.size(), 1));
        contentPane.add(linesPanel, BorderLayout.NORTH);
        for(int c=0;c<lines.size();c++) {
            String line=lines.get(c);
            linesPanel.add(new JLabel(line));
        }

        JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        okButton=new EnterJButton("OK");
        buttonPanel.add(okButton);
        okButton.addActionListener(this);
        okButton.setToolTipText("Close this window.");

        pack();
        center(parent);

        okButton.requestFocus();
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source=e.getSource();
        if(source==okButton) closeWindow();
    }
}