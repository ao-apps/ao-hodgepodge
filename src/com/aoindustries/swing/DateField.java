package com.aoindustries.swing;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.awt.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * @version  1.0
 *
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
}
