package com.aoindustries.swing;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
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
public class USPhoneNumberField extends JPanel implements DocumentListener {

    private final AutoSelectTextField field1;
    private final AutoSelectTextField field2;
    private final AutoSelectTextField field3;

    public USPhoneNumberField() {
        super(new MultiBorderLayout());

        add(new JLabel("("), BorderLayout.WEST);

        field1=new AutoSelectTextField(3);
        field1.setDocument(new LimitedDocument(3, true));
        field1.setInputVerifier(new NumberDigitInputVerifier(3, 3));
        field1.getDocument().addDocumentListener(this);
        add(field1, BorderLayout.WEST);
        
        add(new JLabel(")"), BorderLayout.WEST);

        field2=new AutoSelectTextField(3);
        field2.setDocument(new LimitedDocument(3, true));
        field2.setInputVerifier(new NumberDigitInputVerifier(3, 3));
        field2.getDocument().addDocumentListener(this);
        add(field2, BorderLayout.WEST);
        
        add(new JLabel("-"), BorderLayout.WEST);
        
        field3=new AutoSelectTextField(4);
        field3.setDocument(new LimitedDocument(4, true));
        field3.setInputVerifier(new NumberDigitInputVerifier(4, 4));
        add(field3, BorderLayout.WEST);
    }
    
    public void setEditable(boolean editable) {
        field1.setEditable(editable);
        field2.setEditable(editable);
        field3.setEditable(editable);
    }
    
    public void setPhoneNumber(String number) throws IllegalArgumentException {
        if(number==null) {
            field1.setText("");
            field2.setText("");
            field3.setText("");
        } else {
            if(
                !(
                    (
                        number.length()==14
                        && number.charAt(0)=='('
                        && number.charAt(4)==')'
                        && number.charAt(5)==' '
                        && number.charAt(9)=='-'
                    ) || (
                        number.length()==8
                        && number.charAt(3)=='-'
                    )
                )
            ) throw new IllegalArgumentException("Invalid U.S. phone number: "+number);

            if(number.length()==14) {
                field1.setText(number.substring(1, 4));
                field2.setText(number.substring(6, 9));
                field3.setText(number.substring(10, 14));
            } else {
                field1.setText("");
                field2.setText(number.substring(0, 3));
                field3.setText(number.substring(4, 8));
            }
        }
    }
    
    public String getPhoneNumber() throws IllegalArgumentException {
        String S1=field1.getText().trim();
        String S2=field2.getText().trim();
        String S3=field3.getText().trim();
        if(
            !NumberDigitInputVerifier.verify(S1, 3, 3)
            || !NumberDigitInputVerifier.verify(S2, 3, 3)
            || !NumberDigitInputVerifier.verify(S3, 4, 4)
        ) throw new IllegalArgumentException("Invalid U.S. phone number.");

        if(
            S1.length()==0
            && S2.length()==0
            && S3.length()==0
        ) return null;
        
        if(
            S1.length()>0
            && (
                S2.length()==0
                || S3.length()==0
            )
        ) throw new IllegalArgumentException("U.S. phone number area code provided without the rest of the number.");

        if(
            S2.length()==0
            || S3.length()==0
        ) throw new IllegalArgumentException("U.S. phone number incomplete.");

        if(S1.length()==0) return S2+'-'+S3;
        return "("+S1+") "+S2+'-'+S3;
    }
    
    public void removeUpdate(DocumentEvent e) {
    }

    public void insertUpdate(DocumentEvent e) {
        Document document=e.getDocument();
        int offset=e.getOffset();
        if(document==field1.getDocument()) {
            if(offset==2) {
                field2.requestFocus();
            }
        } else if(document==field2.getDocument()) {
            if(offset==2) {
                field3.requestFocus();
            }
        }
    }

    public void changedUpdate(DocumentEvent e) {
    }
}
