/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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

import java.awt.FlowLayout;
import java.util.Calendar;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author  AO Industries, Inc.
 */
public class USDateLabel extends JLabel {

    public USDateLabel() {
        super();
    }

    public USDateLabel(long date) {
        super(getDate(date));
    }

    public USDateLabel(long date, int horizontalAlignment) {
        super(getDate(date), horizontalAlignment);
    }

    public USDateLabel(long date, Icon icon, int horizontalAlignment) {
        super(getDate(date), icon, horizontalAlignment);
    }

    public USDateLabel(Icon icon) {
        super(icon);
    }

    public USDateLabel(Icon icon, int horizontalAlignment) {
        super(icon, horizontalAlignment);
    }
    
    public static String getDate(long date) {
        return getDate(date, new StringBuilder()).toString();
    }

    public static StringBuilder getDate(long date, StringBuilder SB) {
        if(date==-1) return SB;

        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(date);
        int month=cal.get(Calendar.MONTH)+1;
        if(month<10) SB.append('0');
        SB.append(month).append('/');
        int day=cal.get(Calendar.DAY_OF_MONTH);
        if(day<10) SB.append('0');
        SB.append(day).append('/').append(cal.get(Calendar.YEAR));
        return SB;
    }

    public static String getDateTime(long date) {
        return getDateTime(date, new StringBuilder()).toString();
    }

    public static StringBuilder getDateTime(long date, StringBuilder SB) {
        if(date==-1) return SB;

        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(date);
        int month=cal.get(Calendar.MONTH)+1;
        if(month<10) SB.append('0');
        SB.append(month).append('/');
        int day=cal.get(Calendar.DAY_OF_MONTH);
        if(day<10) SB.append('0');
        SB.append(day).append('/').append(cal.get(Calendar.YEAR)).append(' ').append(cal.get(Calendar.HOUR)).append(':');
        int minute=cal.get(Calendar.MINUTE);
        if(minute<10) SB.append('0');
        SB.append(minute).append(' ').append(cal.get(Calendar.AM_PM)==Calendar.PM?"PM":"AM");
        return SB;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("USDateLabel");
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new USDateLabel(System.currentTimeMillis()));
        frame.pack();
        frame.setVisible(true);
    }
}
