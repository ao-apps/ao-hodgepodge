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

import com.aoindustries.sql.SQLUtility;

/**
 * @author  AO Industries, Inc.
 */
public class DollarField extends AutoSelectTextField {

    private final boolean allowNull, positiveOnly;

    public DollarField(boolean allowNull, boolean positiveOnly) {
        this(allowNull, positiveOnly, 7);
    }

    public DollarField(boolean allowNull, boolean positiveOnly, int columns) {
        super(columns);
        this.allowNull=allowNull;
        this.positiveOnly=positiveOnly;
        setInputVerifier(new DollarInputVerifier(allowNull, positiveOnly));
    }
    
    public void setDollar(int pennies) throws IllegalArgumentException {
        if(allowNull && pennies==-1) setText("");
        else if(positiveOnly && pennies<0) throw new IllegalArgumentException("Amount must be greater than or equal to zero when configured as positive only, pennies="+pennies);
        else setText(SQLUtility.getDecimal(pennies));
    }
    
    public int getDollar() throws IllegalArgumentException {
        String S=getText();
        while(S.length()>0) {
            S.trim();
            if(S.length()>0 && S.charAt(0)=='$') S=S.substring(1);
            else break;
        }
        if(S.length()==0) {
            if(allowNull) return -1;
            throw new IllegalArgumentException("Dollar value may not be empty.");
        }
        int pennies=SQLUtility.getPennies(S);
        if(pennies<0) {
            if(allowNull && pennies==-1) throw new IllegalArgumentException("Dollar value not allowed because field is nullable: "+S);
            if(positiveOnly) throw new IllegalArgumentException("Dollar value may not be negative.");
        }
        return pennies;
    }
}
