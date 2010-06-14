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

import com.aoindustries.awt.MultiBorderLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * @author  AO Industries, Inc.
 */
public class DateField extends JPanel implements DocumentListener {

    private final AutoSelectTextField monthField;
    private final AutoSelectTextField dayField;
    private final AutoSelectTextField yearField;

    public DateField() {
        super(new MultiBorderLayout());

        monthField=new AutoSelectTextField(2);
        monthField.setDocument(new LimitedDocument(2, true));
        monthField.setInputVerifier(new NumberInputVerifier(1, 12));
        monthField.getDocument().addDocumentListener(this);
        add(monthField, BorderLayout.WEST);
        
        add(new JLabel("/"), BorderLayout.WEST);

        dayField=new AutoSelectTextField(2);
        dayField.setDocument(new LimitedDocument(2,true));
        dayField.setInputVerifier(new NumberInputVerifier(1, 31));
        dayField.getDocument().addDocumentListener(this);
        add(dayField, BorderLayout.WEST);
        
        add(new JLabel("/"), BorderLayout.WEST);
        
        yearField=new AutoSelectTextField(4);
        yearField.setDocument(new LimitedDocument(4, true));
        yearField.setInputVerifier(new NumberInputVerifier(1000, 9999));
        add(yearField, BorderLayout.WEST);
    }
    
    public void setEditable(boolean editable) {
        monthField.setEditable(editable);
        dayField.setEditable(editable);
        yearField.setEditable(editable);
    }
    
    public void setDate(long date) {
        if(date==-1) {
            monthField.setText("");
            dayField.setText("");
            yearField.setText("");
        } else {
            Calendar cal=Calendar.getInstance();
            cal.setTimeInMillis(date);
            int month=cal.get(Calendar.MONTH)+1;
            monthField.setText(month<10?("0"+month):Integer.toString(month));
            int day=cal.get(Calendar.DAY_OF_MONTH);
            dayField.setText(day<10?("0"+day):Integer.toString(day));
            yearField.setText(Integer.toString(cal.get(Calendar.YEAR)));
        }
    }
    
    public long getDate() throws IllegalArgumentException {
        String M=monthField.getText().trim();
        String D=dayField.getText().trim();
        String Y=yearField.getText().trim();
        if(
            M.length()==0
            && D.length()==0
            && Y.length()==0
        ) return -1;
        
        if(
            M.length()==0
            || D.length()==0
            || Y.length()==0
        ) throw new IllegalArgumentException("Month, day, and year of a date must be either all blank or all filled-in.");
        
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(Y));
        cal.set(Calendar.MONTH, Integer.parseInt(M)-1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(D));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public void removeUpdate(DocumentEvent e) {
    }

    public void insertUpdate(DocumentEvent e) {
        Document document=e.getDocument();
        int offset=e.getOffset();
        if(document==monthField.getDocument()) {
            if(offset==1) {
                dayField.requestFocus();
            }
        } else if(document==dayField.getDocument()) {
            if(offset==1) {
                yearField.requestFocus();
            }
        }
    }

    public void changedUpdate(DocumentEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("DateField");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new DateField());
        frame.pack();
        frame.setVisible(true);
    }
}
