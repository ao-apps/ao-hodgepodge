package com.aoindustries.swing;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.awt.MultiBorderLayout;
import com.aoindustries.util.ErrorPrinter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
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
        CharArrayWriter cout=new CharArrayWriter();
        PrintWriter pout=new PrintWriter(cout);
        ErrorPrinter.printStackTraces(T, pout, extraInfo);
        pout.flush();
        String errorText=cout.toString();

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