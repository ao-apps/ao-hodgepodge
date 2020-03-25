/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2020  AO Industries, Inc.
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

	private static final long serialVersionUID = 1L;

	private EnterJButton closeButton;

	public ErrorDialog(Component parent, String title, Throwable T) {
		this(parent, title, T, (Object[])null);
	}

	public ErrorDialog(Component parent, String title, Throwable T, Object... extraInfo) {
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if(source==closeButton) closeWindow();
	}
}
