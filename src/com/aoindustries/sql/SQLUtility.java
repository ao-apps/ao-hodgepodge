/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2013  AO Industries, Inc.
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

import com.aoindustries.util.CalendarUtils;
import com.aoindustries.util.EncodingUtils;
import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;

/**
 * SQL utilities.
 *
 * @author  AO Industries, Inc.
 */
public class SQLUtility {

    private SQLUtility() {
    }

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

    private static final char[] hexChars={
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f'
    };

    public static String encodeString(String S) {
        if(S==null) return null;

        StringBuilder SB=null;
        int len=S.length();
        for(int c=0;c<len;c++) {
            char ch=S.charAt(c);
            if(ch<' ' || ch>'~' || ch=='\\') {
                if(SB==null) {
                    SB=new StringBuilder();
                    if(c>0) SB.append(S, 0, c);
                }
                if(ch=='\\') SB.append("\\\\");
                else if(ch=='\b') SB.append("\\b");
                else if(ch=='\f') SB.append("\\f");
                else if(ch=='\n') SB.append('\n');
                else if(ch=='\r') SB.append("\\r");
                else if(ch=='\t') SB.append("\\t");
                else {
                    int ich=(int)ch;
                    SB
                        .append("\\u")
                        .append(hexChars[(ich>>>12)&15])
                        .append(hexChars[(ich>>>8)&15])
                        .append(hexChars[(ich>>>4)&15])
                        .append(hexChars[ich&15])
                    ;
                }
            } else {
                if(SB!=null) SB.append(ch);
            }
        }
        return SB==null?S:SB.toString();
    }

    public static String decodeString(String S) {
        if(S==null) return null;

        StringBuilder SB=null;
        int len=S.length();
        for(int c=0;c<len;c++) {
            char ch=S.charAt(c);
            if(ch=='\\') {
                if(SB==null) {
                    SB=new StringBuilder();
                    if(c>0) SB.append(S, 0, c);
                }
                if(++c<len) {
                    ch=S.charAt(c);
                    if(ch=='\\') SB.append('\\');
                    else if(ch=='b' || ch=='B') SB.append('\b');
                    else if(ch=='f' || ch=='F') SB.append('\f');
                    else if(ch=='r' || ch=='R') SB.append('\r');
                    else if(ch=='t' || ch=='T') SB.append('\t');
                    else if(ch=='u' || ch=='U') {
                        if(++c<len) {
                            char ch1=S.charAt(c);
                            if(
                                (ch1>='0' && ch1<='9')
                                || (ch1>='a' && ch1<='f')
                                || (ch1>='A' && ch1<='F')
                            ) {
                                if(++c<len) {
                                    char ch2=S.charAt(c);
                                    if(
                                        (ch2>='0' && ch2<='9')
                                        || (ch2>='a' && ch2<='f')
                                        || (ch2>='A' && ch2<='F')
                                    ) {
                                        if(++c<len) {
                                            char ch3=S.charAt(c);
                                            if(
                                                (ch3>='0' && ch3<='9')
                                                || (ch3>='a' && ch3<='f')
                                                || (ch3>='A' && ch3<='F')
                                            ) {
                                                if(++c<len) {
                                                    char ch4=S.charAt(c);
                                                    if(
                                                        (ch4>='0' && ch4<='9')
                                                        || (ch4>='a' && ch4<='f')
                                                        || (ch4>='A' && ch4<='F')
                                                    ) {
                                                        SB.append(
                                                            (char)(
                                                                (StringUtility.getHex(ch1)<<12)
                                                                | (StringUtility.getHex(ch2)<<8)
                                                                | (StringUtility.getHex(ch3)<<4)
                                                                | StringUtility.getHex(ch4)
                                                            )
                                                        );
                                                    } else SB.append('\\').append(ch).append(ch1).append(ch2).append(ch3).append(ch4);
                                                } else SB.append('\\').append(ch).append(ch1).append(ch2).append(ch3);
                                            } else SB.append('\\').append(ch).append(ch1).append(ch2).append(ch3);
                                        } else SB.append('\\').append(ch).append(ch1).append(ch2);
                                    } else SB.append('\\').append(ch).append(ch1).append(ch2);
                                } else SB.append('\\').append(ch).append(ch1);
                            } else SB.append('\\').append(ch).append(ch1);
                        } else SB.append('\\').append(ch);
                    } else SB.append('\\').append(ch);
                } else SB.append('\\');
            } else {
                if(SB!=null) SB.append(ch);
            }
        }
        return SB==null?S:SB.toString();
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
	 * @see  CalendarUtils#parseDate(java.lang.String)
     */
    public static java.sql.Date getDate(String yyyy_mm_dd) throws IllegalArgumentException {
        return new java.sql.Date(CalendarUtils.parseDate(yyyy_mm_dd).getTimeInMillis());
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

    /**
     * Gets the number of days from epoch
     */
    public static long getDaysFromMillis(long time) {
        return time/(24*60*60);
    }

    /**
     * Gets the number of millis from epoch
     */
    public static long getMillisFromDays(long time) {
        return time*(24*60*60);
    }

    /**
     * Rounds the time to an exact day.
     */
    public static long roundToDay(long time) {
        return time/(24*60*60)*(24*60*60);
    }

    /**
     * Converts a number of pennies into decimal representation.
     */
    public static String getDecimal(int pennies) {
        StringBuilder SB=new StringBuilder(10);
        if(pennies<0) {
            SB.append('-');
            pennies=-pennies;
        }
        SB
            .append(pennies/100)
            .append('.')
        ;
        pennies%=100;
        if(pennies<10) SB.append('0');
        return SB
            .append(pennies)
            .toString()
        ;
    }

    /**
     * Converts a number of pennies into decimal representation.
     */
    public static String getDecimal(long pennies) {
        StringBuilder SB=new StringBuilder(10);
        if(pennies<0) {
            SB.append('-');
            pennies=-pennies;
        }
        SB
            .append(pennies/100)
            .append('.')
        ;
        int i=(int)(pennies%100);
        if(i<10) SB.append('0');
        return SB
            .append(i)
            .toString()
        ;
    }

    /**
     * Converts a number of millis into decimal representation.
     */
    public static String getMilliDecimal(int millis) {
        StringBuilder SB=new StringBuilder(10);
        if(millis<0) {
            SB.append('-');
            millis=-millis;
        }
        SB
            .append(millis/1000)
            .append('.')
        ;
        millis%=1000;
        if(millis<10) SB.append("00");
        else if(millis<100) SB.append('0');
        return SB
            .append(millis)
            .toString()
        ;
    }

    /**
     * Converts a number of millis into decimal representation.
     */
    public static String getMilliDecimal(long millis) {
        StringBuilder SB=new StringBuilder(10);
        if(millis<0) {
            SB.append('-');
            millis=-millis;
        }
        SB
            .append(millis/1000)
            .append('.')
        ;
        millis%=1000;
        if(millis<10) SB.append("00");
        else if(millis<100) SB.append('0');
        return SB
            .append(millis)
            .toString()
        ;
    }

    /**
     * Gets the number of millis represented by a <code>String</code> containing a decimal(8,3) type.
     */
    public static int getMillis(String decimal) {
        // Get the sign first, treat as negative, then apply the sign
        boolean isNegative;
        if (decimal.length() > 0 && decimal.charAt(0) == '-') {
            isNegative = true;
            decimal = decimal.substring(1);
        } else isNegative = false;

        // Add zero to beginning if starts with .
        if(decimal.length()>0 && decimal.charAt(0)=='.') decimal='0'+decimal;

        // Allow for incomplete data like 2, 2., 2.3, and 2.34
        if(decimal.indexOf('.')==-1) decimal=decimal+".000";
        else if(decimal.charAt(decimal.length()-1)=='.') decimal=decimal+"000";
        else if(decimal.length()>=2 && decimal.charAt(decimal.length()-2)=='.') decimal=decimal+"00";
        else if(decimal.length()>=3 && decimal.charAt(decimal.length()-3)=='.') decimal=decimal+'0';

        int len = decimal.length();
        int whole = Integer.parseInt(decimal.substring(0, len - 4));
        int millis = Integer.parseInt(decimal.substring(len - 3));
        long result = (isNegative?-1L:1L)*(whole * 1000L + millis);
        if(result<Integer.MIN_VALUE || result>Integer.MAX_VALUE) throw new NumberFormatException("Out of range during conversion");
        return (int)result;
    }

    /**
     * Gets the number of pennies represented by a <code>String</code> containing a decimal type.
     */
    public static int getPennies(String decimal) {
        // Get the sign first, treat as negative, then apply the sign
        boolean isNegative;
        if(decimal.length()>0 && decimal.charAt(0)=='-') {
            isNegative=true;
            decimal=decimal.substring(1);
        } else isNegative=false;

        // Add zero to beginning if starts with .
        if(decimal.length()>0 && decimal.charAt(0)=='.') decimal='0'+decimal;

        // Allow for incomplete data like 2, 2., and 2.3
        if(decimal.indexOf('.')==-1) decimal=decimal+".00";
        else if(decimal.charAt(decimal.length()-1)=='.') decimal=decimal+"00";
        else if(decimal.length()>=2 && decimal.charAt(decimal.length()-2)=='.') decimal=decimal+'0';

        int len = decimal.length();
        int dollars = Integer.parseInt(decimal.substring(0, len - 3));
        int pennies = Integer.parseInt(decimal.substring(len - 2));
        long result = (isNegative?-1l:1l)*(dollars * 100l + pennies);
        if(result<Integer.MIN_VALUE || result>Integer.MAX_VALUE) throw new NumberFormatException("Out of range during conversion");
        return (int)result;
    }

    /**
     * Gets the number of pennies represented by a <code>String</code> containing a decimal type.
     */
    public static long getPenniesLong(String decimal) {
        // Get the sign first, treat as negative, then apply the sign
        boolean isNegative;
        if(decimal.length()>0 && decimal.charAt(0)=='-') {
            isNegative=true;
            decimal=decimal.substring(1);
        } else isNegative=false;

        // Add zero to beginning if starts with .
        if(decimal.length()>0 && decimal.charAt(0)=='.') decimal='0'+decimal;

        // Allow for incomplete data like 2, 2., and 2.3
        if(decimal.indexOf('.')==-1) decimal=decimal+".00";
        else if(decimal.charAt(decimal.length()-1)=='.') decimal=decimal+"00";
        else if(decimal.length()>=2 && decimal.charAt(decimal.length()-2)=='.') decimal=decimal+'0';

        int len = decimal.length();
        long dollars = Long.parseLong(decimal.substring(0, len - 3));
        int pennies = Integer.parseInt(decimal.substring(len - 2));
        return (isNegative?-1:1)*(dollars * 100 + pennies);
    }

    /**
     * Removes the milliseconds from a time, rounds down every time.
     */
    public static int negIntIfEmpty(String s) {
        s = nullIfEmpty(s);
        return (s==null?-1:Integer.parseInt(s));
    }

    /**
     * Removes the milliseconds from a time, rounds down every time.
     */
    public static long negLongIfEmpty(String s) {
        s = nullIfEmpty(s);
        return (s==null?-1l:Long.parseLong(s));
    }

    /**
     * Returns null if the string is null or empty.
     */
    public static String nullIfEmpty(String S) {
        if(S==null || S.length()==0) return null;
        return S;
    }

    public static void printResultSetHTMLTable(ResultSet results, Appendable out, String title, boolean wordWrap) throws SQLException, IOException {
        // Figure out the number of columns in the result set
        ResultSetMetaData metaData=results.getMetaData();
        int columnCount=metaData.getColumnCount();

        out.append("<table style='border:1px;' cellspacing='0' cellpadding='2'>\n");
        if(title!=null) {
            out.append("  <tr><th colspan='").append(Integer.toString(columnCount)).append("'>");
            EncodingUtils.encodeHtml(title, out);
            out.append("</th></tr>\n");
        }
        out.append("  <tr>\n");
        for(int c=0;c<columnCount;c++) {
            out.append("    <th>");
            EncodingUtils.encodeHtml(metaData.getColumnLabel(c+1), out);
            out.append("</th>\n");
        }
        out.append("  </tr>\n");
        while(results.next()) {
            out.append("  <tr>\n");
            for(int c=0;c<columnCount;c++) {
                String S=results.getString(c+1);
                out.append(wordWrap?"    <td>":"    <td style='white-space:nowrap'>");
                if(S!=null) {
                    EncodingUtils.encodeHtml(S, out);
                }
                out.append("</td>\n");
            }
            out.append("  </tr>\n");
        }
        out.append("</table>\n");
    }

    private static final String EOL = System.getProperty("line.separator");

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
            out.append(EOL);

            // Print the spacer lines
            for(int c=0;c<columns;c++) {
                int width=widest[c];
                for(int d=-2;d<width;d++) out.append('-');
                if(c<(columns-1)) out.append('+');
            }
            out.append(EOL);

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
                    out.append(EOL);
                }
                valuePos+=columns;
            }
            out.append("(");
            out.append(Integer.toString(rows));
            out.append(rows==1?" row)":" rows)");
            out.append(EOL);
            out.append(EOL);
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
                out.append(EOL);
            }
        }
    }

    public static void printTable(Object[] titles, Collection<Object> values, Appendable out, boolean isInteractive, boolean[] alignRights) throws IOException {
        int size=values.size();
        Object[] oa=new Object[size];
        values.toArray(oa);
        printTable(titles, oa, out, isInteractive, alignRights);
    }
}
