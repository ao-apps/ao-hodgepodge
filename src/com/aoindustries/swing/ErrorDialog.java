package com.aoindustries.swing;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.awt.*;
import com.aoindustries.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class ErrorDialog extends DefaultJDialog implements ActionListener {

    private EnterJButton closeButton;

    public ErrorDialog(Component parent, String title, Throwable T) {
        this(parent, title, T, null);
    }

    public ErrorDialog(Component parent, String title, Throwable T, Object[] extraInfo) {
        super((parent instanceof JFrame) ? (JFrame)parent : new JFrame(), title, true, 400, 300);

        Container contentPane=getContentPane();
        contentPane.setLayout(new MultiBorderLayout());
        contentPane.add(new JLabel(" "), BorderLayout.NORTH);
        contentPane.add(new JLabel("An application error has occurred.  Please provide a", JLabel.CENTER), BorderLayout.NORTH);
        contentPane.add(new JLabel("copy of this error to your system administrator.", JLabel.CENTER), BorderLayout.NORTH);
        contentPane.add(new JLabel(" "), BorderLayout.NORTH);

        // Convert the error
        ByteArrayOutputStream bout=new ByteArrayOutputStream();
        PrintWriter pout=new PrintWriter(bout);
        ErrorPrinter.printStackTraces(T, pout, extraInfo);
        pout.flush();
        String errorText=new String(bout.toByteArray());

        // Setup the GUI
        JTextArea textArea=new JTextArea(errorText, 25, 80);
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        contentPane.add(new JScrollPane(textArea));
        
        JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        closeButton=new EnterJButton("Close");
        buttonPanel.add(closeButton);
        closeButton.addActionListener(this);
        closeButton.setToolTipText("Close this window.");

        pack();
        center(parent);
        
        closeButton.requestFocus();
    }
    
    public void actionPerformed(ActionEvent e) {
        Object source=e.getSource();
        if(source==closeButton) closeWindow();
    }
}