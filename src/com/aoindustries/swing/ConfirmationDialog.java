/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author  AO Industries, Inc.
 */
public class ConfirmationDialog extends DefaultJDialog implements ActionListener {

    public static final int
        YES=1,
        NO=2,
        OK=4,
        CANCEL=8,
        CLOSE=16
    ;

    private EnterJButton
        yesButton,
        noButton,
        okButton,
        cancelButton,
        closeButton
    ;

    private int choice=0;

    public ConfirmationDialog(JFrame parent, String title, String message, int buttons) {
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

        JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        if((buttons&YES)!=0) {
            yesButton=new EnterJButton("Yes");
            buttonPanel.add(yesButton);
            yesButton.addActionListener(this);
        }
        if((buttons&NO)!=0) {
            noButton=new EnterJButton("No");
            buttonPanel.add(noButton);
            noButton.addActionListener(this);
        }
        if((buttons&OK)!=0) {
            okButton=new EnterJButton("OK");
            buttonPanel.add(okButton);
            okButton.addActionListener(this);
        }
        if((buttons&CANCEL)!=0) {
            cancelButton=new EnterJButton("Cancel");
            buttonPanel.add(cancelButton);
            cancelButton.addActionListener(this);
        }
        if((buttons&CLOSE)!=0) {
            closeButton=new EnterJButton("Close");
            buttonPanel.add(closeButton);
            closeButton.addActionListener(this);
        }

        pack();
        center(parent);
        
        if(yesButton!=null) yesButton.requestFocus();
        else if(noButton!=null) noButton.requestFocus();
        else if(okButton!=null) okButton.requestFocus();
        else if(cancelButton!=null) cancelButton.requestFocus();
        else if(closeButton!=null) closeButton.requestFocus();
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source=e.getSource();
        if(source!=null) {
            if(source==yesButton) {
                choice=YES;
                closeWindow();
            } else if(source==noButton) {
                choice=NO;
                closeWindow();
            } else if(source==okButton) {
                choice=OK;
                closeWindow();
            } else if(source==cancelButton) {
                choice=CANCEL;
                closeWindow();
            } else if(source==closeButton) {
                choice=CLOSE;
                closeWindow();
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        Object source=e.getSource();
        if(source==this) {
            choice=CLOSE;
            closeWindow();
        }
    }
    
    public int getChoice() {
        return choice;
    }
}