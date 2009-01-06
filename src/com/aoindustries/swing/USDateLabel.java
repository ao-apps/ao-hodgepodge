package com.aoindustries.swing;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * @version  1.0
 *
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
}
