package com.aoindustries.swing;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.ErrorHandler;
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

    private final ErrorHandler errorHandler;

    private EnterJButton okButton;

    public MessageDialog(JFrame parent, ErrorHandler errorHandler, String title, String message) {
        super(parent, title, true, -1, -1);

        this.errorHandler=errorHandler;

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