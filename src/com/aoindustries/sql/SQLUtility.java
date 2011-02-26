/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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
package com.aoindustries.sql;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * SQL utilities.
 *
 * @author  AO Industries, Inc.
 */
final public class SQLUtility {

    private SQLUtility() {
    }

    private static final String eol = System.getProperty("line.separator");

    /**
     * Escapes SQL so that it can be used safely in queries.
     *
     * @param S the string to be escaped.
     *
     * @deprecated  use PreparedStatement instead
     */
    @Deprecated
    public static String escapeSQL(String S) {
        int i;
        StringBuilder B = new StringBuilder();
        escapeSQL(S, B);
        return B.toString();
    }

    /**
     * Escapes SQL so that it can be used safely in queries.
     *
     * @param S the string to be escaped.
     * @param B the <code>StringBuilder</code> to append to.
     *
     * @deprecated  use PreparedStatement instead
     */
    @Deprecated
    public static void escapeSQL(String S, StringBuilder B) {
        int i;
        for (i = 0; i < S.length(); i++) {
            char c = S.charAt(i);

            if (c == '\\' || c == '\'' || c == '"' || c == '%' || c == '_') {
                B.append('\\');
            }
            B.append(c);
        }
    }

    /**
     * Gets the date in the YYYY-MM-DD format.
     */
    public static String getDate(long time) {
        Calendar cal=Calendar.getInstance();
        cal.setTime(new java.util.Date(time));
        StringBuilder SB=new StringBuilder();

        // year
        SB.append(cal.get(Calendar.YEAR));

        SB.append('-');

        // Month
        int month=cal.get(Calendar.MONTH)+1;
        if(month<10) SB.append('0');
        SB.append(month);

        SB.append('-');

        // Day
        int day=cal.get(Calendar.DAY_OF_MONTH);
        if(day<10) SB.append('0');
        SB.append(day);

        return SB.toString();
    }

    /**
     * Gets the date from the YYYY-MM-DD format.
     */
    public static java.sql.Date getDate(String s) throws IllegalArgumentException {
        if (s.length()!=10) throw new IllegalArgumentException("Invalid date: "+s);
        int year = Integer.parseInt(s.substring(0,4));
        int month = Integer.parseInt(s.substring(5,7));
        if (month>12) throw new IllegalArgumentException("Invalid date: "+s);
        int day = Integer.parseInt(s.substring(8,10));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        if (day>cal.getMaximum(Calendar.DATE)) throw new IllegalArgumentException("Invalid date: "+s);
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    /**
     * Gets the date and time from the YYYY-MM-DD HH:MM:SS format.
     */
    public static java.sql.Date getDateTime(String s) throws IllegalArgumentException {
        if (s.length()!=19) throw new IllegalArgumentException("Invalid date: "+s);
        Calendar cal = Calendar.getInstance();
        int year=Integer.parseInt(s.substring(0,4));
        cal.set(Calendar.YEAR, year);
        int month = Integer.parseInt(s.substring(5,7));
        if (month>12) throw new IllegalArgumentException("Invalid date: "+s);
        cal.set(Calendar.MONTH, month-1);
        int day = Integer.parseInt(s.substring(8,10));
        if (day>cal.getMaximum(Calendar.DATE)) throw new IllegalArgumentException("Invalid date: "+s);
        cal.set(Calendar.DATE, day);
        int hour=Integer.parseInt(s.substring(11, 13));
        if(hour<0 || hour>23) throw new IllegalArgumentException("Invalid hour: "+hour);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        int minute=Integer.parseInt(s.substring(14, 16));
        if(minute<0 || minute>59) throw new IllegalArgumentException("Invalid minute: "+minute);
        cal.set(Calendar.MINUTE, minute);
        int second=Integer.parseInt(s.substring(17, 19));
        if(second<0 || second>59) throw new IllegalArgumentException("Invalid second: "+second);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    /**
     * Gets the database format for a date/time field.
     * If the time is <code>-1</code>, returns <code>null</code>
     */
    public static String getDateTime(long time) {
        return time==-1 ? null : new Timestamp(time).toString().substring(0, 19);
    }

    /**
     * Gets the database format for a time field.
     * If the time is <code>-1</code>, returns <code>null</code>
     */
    public static String getTime(long time) {
        return time==-1 ? null : new Timestamp(time).toString().substring(11, 19);
    }

    public static void printTable(Object[] titles, Object[] values, Appendable out, boolean isInteractive, boolean[] alignRights) throws IOException {
        if(isInteractive) {
            // Find the widest for each column, taking the line wraps into account and skipping the '\r' characters
            int columns=titles.length;
            int[] widest=new int[columns];
            int rows=values.length/columns;
            for(int c=-1;c<rows;c++) {
                Object[] row=c==-1?titles:values;
                int valuePos=c==-1?0:c*columns;
                for(int d=0;d<columns;d++) {
                    Object r=row[d+valuePos];
                    if(r!=null) {
                        String S=r.toString();
                        int Slen=S.length();
                        int width=0;
                        int pos=0;
                        while(pos<Slen) {
                            char ch=S.charAt(pos++);
                            if(ch!='\r') {
                                if(ch=='\n') {
                                    if(width>widest[d]) widest[d]=width;
                                    width=0;
                                } else width++;
                            }
                        }
                        if(width>widest[d]) widest[d]=width;
                    }
                }
            }

            // The title is printed centered in its place
            for(int c=0;c<columns;c++) {
                String title=titles[c].toString();
                int titleLen=title.length();
                int width=widest[c];
                int before=(width-titleLen)/2;
                for(int d=0;d<=before;d++) out.append(' ');
                out.append(title);
                if(c<(columns-1)) {
                    int after=width-titleLen-before;
                    for(int d=0;d<=after;d++) out.append(' ');
                    out.append('|');
                }
            }
            out.append(eol);

            // Print the spacer lines
            for(int c=0;c<columns;c++) {
                int width=widest[c];
                for(int d=-2;d<width;d++) out.append('-');
                if(c<(columns-1)) out.append('+');
            }
            out.append(eol);

            // Print the values
            int[] lineCounts=new int[columns];
            int[] lineValueIndexes=new int[columns];
            int valuePos=0;
            for(int c=0;c<rows;c++) {
                // Figure out how many lines of output this row will be
                int maxLineCount=1;
                for(int d=0;d<columns;d++) {
                    int lineCount=1;
                    Object value=values[valuePos+d];
                    if(value!=null) {
                        String val=value.toString();
                        int valLen=val.length();
                        for(int e=0;e<valLen;e++) if(val.charAt(e)=='\n') lineCount++;
                    }
                    lineCounts[d]=lineCount;
                    lineValueIndexes[d]=0;
                    if(lineCount>maxLineCount) maxLineCount=lineCount;
                }

                for(int line=0;line<maxLineCount;line++) {
                    for(int d=0;d<columns;d++) {
                        int width=widest[d];
                        Object value=line<lineCounts[d]?values[valuePos+d]:null;
                        int printed;
                        if(value==null) printed=0;
                        else {
                            boolean rightAlign=alignRights[d];
                            String val=value.toString();
                            int valLen=val.length();
                            if(valLen==0) printed=0;
                            else {
                                // Find just this line of the output
                                int startPos=lineValueIndexes[d];
                                boolean trimmed=false;
                                int pos=startPos;
                                while(pos<valLen) {
                                    char ch=val.charAt(pos++);
                                    if(ch=='\n') {
                                        val=val.substring(startPos, pos-1);
                                        valLen=val.length();
                                        trimmed=true;
                                        break;
                                    }
                                }
                                if(!trimmed) {
                                    val=val.substring(startPos);
                                    valLen=val.length();
                                }
                                lineValueIndexes[d]=pos;
                                if(valLen==0) printed=0;
                                else {
                                    if(rightAlign) {
                                        int before=width-valLen+1;
                                        for(int e=0;e<before;e++) out.append(' ');
                                        out.append(val);
                                        printed=before+valLen;
                                    } else {
                                        out.append(' ');
                                        out.append(val);
                                        printed=valLen+1;
                                    }
                                }
                            }
                        }
                        if(d<(columns-1)) {
                            int after=width+2-printed;
                            for(int e=0;e<after;e++) out.append(' ');
                            out.append(line<lineCounts[d+1]?'|':' ');
                        }
                    }
                    out.append(eol);
                }
                valuePos+=columns;
            }
            out.append("(");
            out.append(Integer.toString(rows));
            out.append(rows==1?" row)":" rows)");
            out.append(eol);
            out.append(eol);
        } else {
            // This output simply prints stuff in a way that can be read back in, using single quotes
            // Find the widest for each column
            int columns=titles.length;
            int rows=values.length/columns;

            // Print the values
            int valuePos=0;
            for(int c=0;c<rows;c++) {
                for(int d=0;d<columns;d++) {
                    Object value=values[valuePos++];
                    String S=value==null?"":value.toString();
                    int vlen=S.length();

                    boolean needsQuotes=vlen==0;
                    if(!needsQuotes) {
                        for(int e=0;e<vlen;e++) {
                            char ch=S.charAt(e);
                            if(ch<=' ' || ch=='\\' || ch=='\'' || ch=='"') {
                                needsQuotes=true;
                                break;
                            }
                        }
                    }

                    if(needsQuotes) {
                        out.append('\'');
                        for(int e=0;e<vlen;e++) {
                            char ch=S.charAt(e);
                            if(ch=='\'') out.append('\\');
                            out.append(ch);
                        }
                        out.append('\'');
                    } else out.append(S);
                    if(d<(columns-1)) out.append(' ');
                }
                out.append(eol);
            }
        }
    }
}
