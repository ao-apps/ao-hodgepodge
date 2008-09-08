package com.aoindustries.swing;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
//import com.aoindustries.util.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import javax.swing.*;

/**
 * @version  1.0
 *
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