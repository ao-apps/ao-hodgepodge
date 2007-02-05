package com.aoindustries.util;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.sql.*;
import com.aoindustries.util.sort.*;
import java.util.*;

/**
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public final class StringUtility {

    private static final String[] MONTHS = {
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    };

    public static String getMonth(int month) {
        return MONTHS[month];
    }

    private static final Calendar calendar = Calendar.getInstance();

    private static final char[] wordWrapChars = { ' ', '\t', '-', '=', ',', ';' };

    /**
     * StringUtilitly constructor comment.
     */
    private StringUtility() {
    }

    /**
     * Constructs a comma seperated list from a <code>String[]</code>.
     */
    public static String buildEmailList(String[] list) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "buildEmailList(String[])", null);
        try {
            StringBuilder SB=new StringBuilder();
            int len=list.length;
            for(int c=0;c<len;c++) {
                if(c==0) SB.append('<'); else SB.append(",<");
                SB.append(list[c]).append('>');
            }
            return SB.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Constructs a comma seperated list from a <code>String[]</code>.
     */
    public static String buildList(String[] list) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "buildList(String[])", null);
        try {
            StringBuilder SB=new StringBuilder();
            int len=list.length;
            for(int c=0;c<len;c++) {
                if(c>0) SB.append(", ");
                SB.append(list[c]);
            }
            return SB.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Constructs a comma seperated list from an <code>Object[]</code>.
     */
    public static String buildList(Object[] list) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "buildList(Object[])", null);
        try {
            StringBuilder SB=new StringBuilder();
            int len=list.length;
            for(int c=0;c<len;c++) {
                if(c>0) SB.append(", ");
                SB.append(list[c]);
            }
            return SB.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static String buildList(List<?> V) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "buildList(List<?>)", null);
        try {
            StringBuilder SB=new StringBuilder();
            int len=V.size();
            for(int c=0;c<len;c++) {
                if(c>0) SB.append(",");
                SB.append(V.get(c));
            }
            return SB.toString();	
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Compare one date to another, must be in the DDMMYYYY format.
     *
     * @return  <0  if the first date is before the second<br>
     *          0   if the dates are the same or the format is invalid<br>
     *          >0  if the first date is after the second
     */
    public static int compareToDDMMYYYY(String date1, String date2) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "compareToDDMMYYYY(String,String)", null);
        try {
            if(date1.length()!=8 || date2.length()!=8) return 0;
            return compareToDDMMYYYY0(date1)-compareToDDMMYYYY0(date2);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private static int compareToDDMMYYYY0(String date) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "compareToDDMMYYYY0(String)", null);
        try {
            return
                (date.charAt(4)-'0')*10000000
                +(date.charAt(5)-'0')*1000000
                +(date.charAt(6)-'0')*100000
                +(date.charAt(7)-'0')*10000
                +(date.charAt(0)-'0')*1000
                +(date.charAt(1)-'0')*100
                +(date.charAt(2)-'0')*10
                +(date.charAt(3)-'0')
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static boolean containsIgnoreCase(String line, String word) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "containsIgnoreCase(String,String)", null);
        try {
            int word_len=word.length();
            int line_len=line.length();
            int end_pos=line_len-word_len;
            Loop:
            for(int c=0;c<=end_pos;c++) {
                for(int d=0;d<word_len;d++) {
                    char ch1=line.charAt(c+d);
                    char ch2=word.charAt(d);
                    if(ch1>='A'&&ch1<='Z') ch1+='a'-'A';
                    if(ch2>='A'&&ch2<='Z') ch2+='a'-'A';
                    if(ch1!=ch2) continue Loop;
                }
                return true;
            }
            return false;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static long convertStringDateToTime(String date) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "convertStringDateToTime(String)", null);
        try {
	    synchronized(StringUtility.class) {
		if(date.length()<9) throw new IllegalArgumentException("Invalid date");
		int day=Integer.parseInt(date.substring(0,2));
		if(day<0||day>31) throw new IllegalArgumentException("Invalid date");
		String monthString=date.substring(2,5);
		int month=-1;
		for(int c=0;c<MONTHS.length;c++) {
		    if(MONTHS[c].equalsIgnoreCase(monthString)) {
			month=c;
			break;
		    }
		}
		if(month==-1) throw new IllegalArgumentException("Invalid month: "+monthString);
		if(day>30 && (month==1||month==3||month==5||month==8||month==10))
		    throw new IllegalArgumentException("Invalid date");
		int year=Integer.parseInt(date.substring(5,9));
		if(month==1) {
		    if(day>29) throw new IllegalArgumentException("Invalid date");	
		    if(day==29 && !leapYear(year)) throw new IllegalArgumentException("Invalid date");
		}	
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return calendar.getTime().getTime();
	    }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Copies the contents of a String into a char[]
     *
     * @deprecated  Please use String.getChars(int,int,char[],int)
     *
     * @see String#getChars(int,int,char[],int)
     */
    public static void copyString(String S, char[] ch, int pos) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "copyString(String,char[],int)", null);
        try {
            S.getChars(0, S.length(), ch, pos);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Counts how many times a word appears in a line.  Case insensitive matching.
     */
    public static int countOccurances(byte[] buff, int len, String word) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "countOccurances(byte[],int,String)", null);
        try {
            int wordlen=word.length();
            int end=len-wordlen;
            int count=0;
            Loop:
            for(int c=0;c<=end;c++) {
                for(int d=0;d<wordlen;d++) {
                    char ch1=(char)buff[c+d];
                    if(ch1<='Z' && ch1>='A') ch1+='a'-'A';
                    char ch2=word.charAt(d);
                    if(ch2<='Z' && ch2>='A') ch2+='a'-'A';
                    if(ch1!=ch2) continue Loop;
                }
                c+=wordlen-1;
                count++;
            }
            return count;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Counts how many times a word appears in a line.  Case insensitive matching.
     */
    public static int countOccurances(byte[] buff, String word) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "countOccurances(byte[],String)", null);
        try {
            int wordlen=word.length();
            int end=buff.length-wordlen;
            int count=0;
            Loop:
            for(int c=0;c<=end;c++) {
                for(int d=0;d<wordlen;d++) {
                    char ch1=(char)buff[c+d];
                    if(ch1<='Z' && ch1>='A') ch1+='a'-'A';
                    char ch2=word.charAt(d);
                    if(ch2<='Z' && ch2>='A') ch2+='a'-'A';
                    if(ch1!=ch2) continue Loop;
                }
                c+=wordlen-1;
                count++;
            }
            return count;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Counts how many times a word appears in a line.  Case insensitive matching.
     */
    public static int countOccurances(String line, String word) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "countOccurances(String,String)", null);
        try {
            int wordlen=word.length();
            int end=line.length()-wordlen;
            int count=0;
            Loop:
            for(int c=0;c<=end;c++) {
                for(int d=0;d<wordlen;d++) {
                    char ch1=line.charAt(c+d);
                    if(ch1<='Z' && ch1>='A') ch1+='a'-'A';
                    char ch2=word.charAt(d);
                    if(ch2<='Z' && ch2>='A') ch2+='a'-'A';
                    if(ch1!=ch2) continue Loop;
                }
                c+=wordlen-1;
                count++;
            }
            return count;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Decodes a string that was encoded for a form.
     *
     * @deprecated  Please find another means of performing this task, this code is not well-tested
     *              and is no longer supported
     */
    public static String decodeFormData(String string) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "decodeFormData(String)", null);
        try {
            // Shortcut if no special characters
            if(string.indexOf('+')==-1 && string.indexOf('%')==-1) return string;

            // Decode each character
            int len=string.length();
            StringBuilder result=new StringBuilder(len);
            int pos=0;
            while(pos<len) {
                char ch=string.charAt(pos++);
                if(ch=='+') result.append(' ');
                else if(ch=='%') {
                    result.append(
                        (char)(
                            (pos<len?getHex(string.charAt(pos++)):0)<<4
                            | (pos<len?getHex(string.charAt(pos++)):0)
                        )
                    );
                } else result.append(ch);
            }
            return result.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use SQLUtility.escapeSQL(s.replace('*', '%'))
     *
     * @see  SQLUtility#escapeSQL(String)
     */
    public static final String escapeSQL(String s) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "escapeSQL(String)", null);
        try {
            return SQLUtility.escapeSQL(s.replace('*', '%'));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Compares the equality of two objects, including their null states.
     */
    public static boolean equals(Object O1, Object O2) {
        return O1==null?O2==null:O1.equals(O2); 
    }

    /**
     * Converts a date in a the format MMDDYYYY to a <code>Date</code>.
     *
     * @param  date  a <code>String</code> containing the date in MMDDYYYY format.
     *
     * @return  <code>null</code> if <code>date</code> is <code>null</code>, a <code>java.sql.Date</code>
     *          otherwise
     */
    public static java.sql.Date getDateMMDDYYYY(String date) throws NumberFormatException, IllegalArgumentException {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getDateMMDDYYYY(String)", null);
        try {
            synchronized(StringUtility.class) {
                int len = date.length();
                if (len == 0) return null;
                if (len != 8) throw new IllegalArgumentException("Date must be in MMDDYYYY format: " + date);
                return new java.sql.Date(
                    new GregorianCalendar(
                        Integer.parseInt(date.substring(4, 8)),
                        Integer.parseInt(date.substring(0, 2))-1,
                        Integer.parseInt(date.substring(2, 4))
                    ).getTime().getTime()
                );
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use SQLUtility.getDate(long)
     *
     * @see  SQLUtility#getDate(long)
     */
    public static String getDateString(long time) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getDateString(long)", null);
        try {
            return getDateString(new Date(time));
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use SQLUtility.getDate(date.getTime())
     *
     * @see  SQLUtility#getDate(long)
     */
    public static String getDateString(Date date) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getDateString(Date)", null);
        try {
	    synchronized(StringUtility.class) {
		calendar.setTime(date);
		int day=calendar.get(Calendar.DATE);
		return (day>=0 && day<=9 ? "0":"")+String.valueOf(calendar.get(Calendar.DATE))+MONTHS[calendar.get(Calendar.MONTH)]+calendar.get(Calendar.YEAR);
	    }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use SQLUtility.getDate(date.getTime())
     *
     * @see  SQLUtility#getDate(long)
     */
    public static String getDateStringMMDDYYYY(Date date) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getDateStringMMDDYYYY(Date)", null);
        try {
            if(date==null) return "";
            Calendar C=Calendar.getInstance();
            C.setTime(date);
            int day=C.get(Calendar.DATE);
            int month=C.get(Calendar.MONTH)+1;
            return
                (month>=0 && month<=9 ? "0":"")
                +month
                +(day>=0 && day<=9 ? "0":"")
                +day
                +C.get(Calendar.YEAR)
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use SQLUtility.getDateTime(long)
     *
     * @see  SQLUtility#getDateTime(long)
     */
    public static String getDateStringSecond(long time) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getDateStringSecond(long)", null);
        try {
            Date date=new Date(time);
            Calendar C=Calendar.getInstance();
            C.setTime(date);
            int day=C.get(Calendar.DATE);
            int hour=C.get(Calendar.HOUR_OF_DAY);
            int minute=C.get(Calendar.MINUTE);
            int second=C.get(Calendar.SECOND);
            return
                (day>=0 && day<=9 ? "0":"")
                +day
                +MONTHS[C.get(Calendar.MONTH)]
                +C.get(Calendar.YEAR)
                +' '
                +(hour>=0 && hour<=9 ? "0":"")
                +hour
                +':'
                +(minute>=0 && minute<=9 ? "0":"")
                +minute
                +':'		
                +(second>=0 && second<=9 ? "0":"")
                +second
            ;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use SQLUtility.getDateTime(long)
     *
     * @see  SQLUtility#getDateTime(long)
     */
    public static String getDateStringSecond(String time) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getDateStringSecond(String)", null);
        try {
            return
                time.substring(6,8)
                +MONTHS[Integer.parseInt(time.substring(4,6))]
                +time.substring(0,4)
                +' '
                +time.substring(8,10)
                +':'
                +time.substring(10,12)
                +':'
                +time.substring(12,14)
            ;        
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Converts one hex digit to an integer
     */
    public static int getHex(char ch) throws IllegalArgumentException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, StringUtility.class, "getHex()", null);
        try {
            switch(ch) {
                case '0': return 0x00;
                case '1': return 0x01;
                case '2': return 0x02;
                case '3': return 0x03;
                case '4': return 0x04;
                case '5': return 0x05;
                case '6': return 0x06;
                case '7': return 0x07;
                case '8': return 0x08;
                case '9': return 0x09;
                case 'a': case 'A': return 0x0a;
                case 'b': case 'B': return 0x0b;
                case 'c': case 'C': return 0x0c;
                case 'd': case 'D': return 0x0d;
                case 'e': case 'E': return 0x0e;
                case 'f': case 'F': return 0x0f;
                default: throw new IllegalArgumentException("Invalid hex character: "+ch);
            }
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Creates a <code>String[]</code> by calling the toString() method of each object in a list.
     *
     * @deprecated  Please use List.toArray(Object[])
     *
     * @see  List#toArray(Object[])
     */
    public static String[] getStringArray(List V) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getStringArray(List)", null);
        try {
            if(V==null) return null;
            int len = V.size();
            String[] SA = new String[len];
            for (int c = 0; c < len; c++) {
                Object O=V.get(c);
                SA[c]=O==null?null:O.toString();
            }
            return SA;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static String getTimeLengthString(long time) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getTimeLengthString(long)", null);
        try {
            StringBuilder SB=new StringBuilder();
            if(time<0) {
                SB.append('-');
                time=-time;
            }

            long days=time/86400000;
            time-=days*86400000;
            int hours=(int)(time/3600000);
            time-=hours*3600000;
            int minutes=(int)(time/60000);
            time-=minutes*60000;
            int seconds=(int)(time/1000);
            time-=seconds*1000;
            if(days==0) {
                if(hours==0) {
                    if(minutes==0) {
                        if(seconds==0) {
                            if(time==0) SB.append("0 minutes");
                            else SB.append(time).append(time==1?" millisecond":" milliseconds");
                        } else SB.append(seconds).append(seconds==1?" second":" seconds");
                    } else SB.append(minutes).append(minutes==1?" minute":" minutes");
                } else {
                    if(minutes==0) SB.append(hours).append(hours==1?" hour":" hours");
                    else SB.append(hours).append(hours==1?" hour and ":" hours and ").append(minutes).append(minutes==1?" minute":" minutes");
                }
            } else {
                if(hours==0) {
                    if(minutes==0) SB.append(days).append(days==1?" day":" days");
                    else SB.append(days).append(days==1?" day and ":" days and ").append(minutes).append(minutes==1?" minute":" minutes");
                } else {
                    if(minutes==0) SB.append(days).append(days==1?" day and ":" days and ").append(hours).append(hours==1?" hour":" hours");
                    else SB.append(days).append(days==1?" day, ":" days, ").append(hours).append(hours==1?" hour and ":" hours and ").append(minutes).append(minutes==1?" minute":" minutes");
                }
            }
            return SB.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static String getDecimalTimeLengthString(long time) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getDecimalTimeLengthString(long)", null);
        try {
            StringBuilder SB=new StringBuilder();
            if(time<0) {
                SB.append('-');
                time=-time;
            }

            long days=time/86400000;
            time-=days*86400000;
            int hours=(int)(time/3600000);
            time-=hours*3600000;
            int minutes=(int)(time/60000);
            time-=minutes*60000;
            int seconds=(int)(time/1000);
            time-=seconds*1000;

            if(days>0) SB.append(days).append(days==1?" day, ":" days, ");
            SB.append(hours).append(':');
            if(minutes<10) SB.append('0');
            SB.append(minutes).append(':');
            if(seconds<10) SB.append('0');
            SB.append(seconds).append('.');
            if(time<10) SB.append("00");
            else if(time<100) SB.append('0');
            SB.append(time);
            return SB.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Finds the first occurance of any of the supplied characters
     *
     * @param  S  the <code>String</code> to search
     * @param  chars  the characters to look for
     *
     * @return  the index of the first occurance of <code>-1</code> if none found
     */
    public static int indexOf(String S, char[] chars) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "indexOf(String,char[])", null);
        try {
            int Slen=S.length();
            int clen=chars.length;
            for(int c=0;c<Slen;c++) {
                char ch=S.charAt(c);
                for(int d=0;d<clen;d++) if(ch==chars[d]) return c;
            }
            return -1;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use Calendar class instead.
     *
     * @see  Calendar
     */
    public static boolean isValidDate(String date) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "isValidDate(String)", null);
        try {
            try {
                convertStringDateToTime(date);
                return true;
            } catch (IllegalArgumentException err) {
                return false;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use Calendar class instead.
     *
     * @see  Calendar
     */
    public static boolean leapYear(int year) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, StringUtility.class, "leapYear(int)", null);
        try {
            return year%4==0 && year%400==0;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Removes all occurances of a <code>char</code> from a <code>String</code>
     *
     * @deprecated  this method is slow and no longer supported
     */
    public static String removeChars(String S, char[] chars) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "removeChars(S,char[])", null);
        try {
            int pos;
            while((pos=indexOf(S, chars))!=-1) S=S.substring(0,pos)+S.substring(pos+1);
            return S;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Removes all occurances of a <code>char</code> from a <code>String</code>
     *
     * @deprecated  this method is slow and no longer supported
     */
    public static String removeChars(String S, char ch) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "removeChars(String,char)", null);
        try {
            int pos;
            while((pos=S.indexOf(ch))!=-1) S=S.substring(0,pos)+S.substring(pos+1);
            return S;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Replaces all occurances of a character with a String
     */
    public static String replace(String string, char ch, String replacement) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "replace(String,char,String)", null);
        try {
            int pos = string.indexOf(ch);
            if (pos == -1) return string;
            StringBuilder SB = new StringBuilder();
            int lastpos = 0;
            do {
                SB.append(string.substring(lastpos, pos)).append(replacement);
                lastpos = pos + 1;
                pos = string.indexOf(ch, lastpos);
            } while (pos != -1);
            return SB.append(string.substring(lastpos)).toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Replaces all occurances of a character with a String
     */
    public static String replace(String string, String find, String replacement) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "replace(String,String,String)", null);
        try {
            int pos = string.indexOf(find);
            //System.out.println(string+": "+find+" at "+pos);
            if (pos == -1) return string;
            StringBuilder SB = new StringBuilder();
            int lastpos = 0;
            do {
                SB.append(string.substring(lastpos, pos)).append(replacement);
                lastpos = pos + find.length();
                pos = string.indexOf(find, lastpos);
            } while (pos != -1);
            return SB.append(string.substring(lastpos)).toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sorts a list of strings in ascending order.
     *
     * @deprecated  Please use new SortAlgorithm classes
     *
     * @see  com.aoindustries.util.sort.AutoSort#sortStatic(Object[])
     */
    public static void sort(String[] list) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, StringUtility.class, "sort(String[])", null);
        try {
            AutoSort.sortStatic(list);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    private static int compareTo(String S1, String S2) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "compareTo(String,String)", null);
        try {
            if(S1==null) {
                return S2==null?0:-1;
            }
            return S2==null?1:S1.compareTo(S2);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sorts a list of filenames in ascending numerical order.
     *
     * @param  filenames        the filenames to be sorted
     * @param  extensionLength  the number of characters that will be removed from the filenames before
     *                          converting to a <code>long</code> for the comparisons.
     *
     * @exception  NullPointerException  if <code>filenames</code> is <code>null</code> or
     *                          any element of <code>filenames</code> is <code>null</code>
     * @exception  StringIndexOutOfBoundsException  if <code>extensionLength</code> is less than zero or
     *                          the length of any element of <code>filenames</code> is less than
     *                          <code>extensionLength</code>
     * @exception  NumberFormatException  if any element of <code>filenames</code> with the extension removed
     *                          cannot be converted to a <code>long</code>
     */
    public static void sortNumericalFilenames(String[] filenames, int extensionLength) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "sortNumericalFilenames(String[],int)", null);
        try {
            int len=filenames.length;
            for(int c=1;c<len;c++) {
                // Insert the word at the appropriate place in the array
                String word=filenames[c];
                long wordValue=Long.valueOf(word.substring(0, word.length()-extensionLength)).longValue();
                int bottom=0;
                int range=c;

                while(range>0) {
                    int half=range>>>1;
                    int pos=bottom+half;
                    int res;
                    String filename=filenames[pos];
                    long filenameValue=Long.valueOf(filename.substring(0, filename.length()-extensionLength)).longValue();
                    if(wordValue>=filenameValue) {
                        if(half==0) {
                            if(wordValue>filenameValue) bottom++;
                            break;
                        }
                        bottom=pos;
                        range-=half;
                    } else range=half;
                }
                if(bottom!=c) {
                    System.arraycopy(filenames,bottom,filenames,bottom+1,c-bottom);
                    filenames[bottom]=word;
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sorts the given <code>List</code> in descending lexical order.  Each item is expected to
     * be contained in the elements, <code>Object</code> and then <code>Float</code>.
     */
    /*
    public static void sortObjectFloatDescending(List list) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "sortObjectFloatDescending(List)", null);
        try {
            int len=list.size();
            for(int c=2;c<len;c+=2) {
                // Insert the object and float at the appropriate place in the array
                Float F=(Float)list.get(c+1);
                float f=F.floatValue();

                int bottom=0;
                int range=c;

                while(range>0) {
                    int half=(range>>>1)&0xfffffffe;
                    int pos=bottom+half;
                    float res=((Float)list.get(pos+1)).floatValue()-f;
                    if(res>=0) {
                        if(half==0) {
                            if(res>0) bottom+=2;
                            break;
                        }
                        bottom=pos;
                        range-=half;
                    } else range=half;
                }
                if(bottom!=c) {
                    Object O=list.get(c);
                    list.remove(c);
                    list.remove(c);
                    list.add(bottom, O);
                    list.add(bottom+1, F);
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    */
    public static <T> void sortObjects(List<T> list, String[] strings) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "sortObjects(List<T>,String[])", null);
        try {
            int len=strings.length;
            for(int c=1;c<len;c++) {
                // Insert the word at the appropriate place in the array
                String word=strings[c];
                int bottom=0;
                int range=c;

                while(range>0) {
                    int half=range>>>1;
                    int pos=bottom+half;
                    int res;
                    if((res=compareTo(word, strings[pos]))>=0) {
                        if(half==0) {
                            if(res>0) bottom++;
                            break;
                        }
                        bottom=pos;
                        range-=half;
                    } else range=half;
                }

                if(bottom!=c) {
                    // Shift the strings
                    System.arraycopy(strings,bottom,strings,bottom+1,c-bottom);
                    strings[bottom]=word;
                    // Shift the objects
                    T O=list.get(c);
                    list.remove(c);
                    list.add(bottom, O);
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Sorts the given <code>List</code> in descending order using <code>Float</code> values.
     *
     * @param  list   the <code>List</code> that is to be sorted
     * @param  index  the index that the <code>Float</code> is located within each set of values
     * @param  width  the number of values per sort item
     *
     * @deprecated  Please encapsulate multiple values into objects that are Comparable and use
     *              standard sort routines.
     */
    public static <T> void sortObjectsAndFloatDescending(List<T> list, int index, int width) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "sortObjectsAndFloatDescending(List<T>,int,int)", null);
        try {
            int len=list.size();
            for(int c=width;c<len;c+=width) {
                // Insert the object and float at the appropriate place in the array
                Float F=(Float)list.get(c+index);
                float f=F.floatValue();

                int bottom=0;
                int range=c;

                while(range>0) {
                    int half=((range>>>1)/width)*width;
                    int pos=bottom+half;
                    float res=((Float)list.get(pos+index)).floatValue()-f;
                    if(res>=0) {
                        if(half==0) {
                            if(res>0) bottom+=width;
                            break;
                        }
                        bottom=pos;
                        range-=half;
                    } else range=half;
                }
                if(bottom!=c) {
                    for(int d=0;d<width;d++) {
                        T O=list.get(c+d);
                        list.remove(c+d);
                        list.add(bottom+d, O);
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static <T> void sortObjectsMMDDYYYY(List<T> list, String[] strings) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "sortObjectsMMDDYYYY(List<T>,String[])", null);
        try {
            int len=strings.length;
            for(int c=1;c<len;c++) {
                // Insert the word at the appropriate place in the array
                String word=strings[c];
                int bottom=0;
                int range=c;

                while(range>0) {
                    int half=range>>>1;
                    int pos=bottom+half;
                    int res;
                    if((res=compareToDDMMYYYY(word, strings[pos]))>=0) {
                        if(half==0) {
                            if(res>0) bottom++;
                            break;
                        }
                        bottom=pos;
                        range-=half;
                    } else range=half;
                }

                if(bottom!=c) {
                    // Shift the strings
                    System.arraycopy(strings,bottom,strings,bottom+1,c-bottom);
                    strings[bottom]=word;
                    // Shift the objects
                    T O=list.get(c);
                    list.remove(c);
                    list.add(bottom, O);
                }
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static List<String> splitLines(String S) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "splitLines(String)", null);
        try {
            List<String> V=new ArrayList<String>();
            int start=0;
            int pos;
            while((pos=S.indexOf('\n', start))!=-1) {
                V.add(S.substring(start, pos));
                start=pos+1;
            }
            if(start<S.length()) V.add(S.substring(start));
            return V;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Splits a <code>String</code> into a <code>String[]</code>.
     */
    public static String[] splitString(String line) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "splitString(String)", null);
        try {
            int len=line.length();
            int wordCount=0;
            int pos=0;
            while(pos<len) {
                // Skip past blank space
                while(pos<len&&line.charAt(pos)<=' ') pos++;
                int start=pos;
                // Skip to the next blank space
                while(pos<len&&line.charAt(pos)>' ') pos++;
                if(pos>start) wordCount++;
            }

            String[] words=new String[wordCount];

            int wordPos=0;
            pos=0;
            while(pos<len) {
                // Skip past blank space
                while(pos<len&&line.charAt(pos)<=' ') pos++;
                int start=pos;
                // Skip to the next blank space
                while(pos<len&&line.charAt(pos)>' ') pos++;
                if(pos>start) words[wordPos++]=line.substring(start,pos);
            }

            return words;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Splits a <code>String</code> into a <code>String[]</code>.
     */
    public static int splitString(String line, char[][][] buff) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "splitString(String,char[][][])", null);
        try {
            int len=line.length();
            int wordCount=0;
            int pos=0;
            while(pos<len) {
                // Skip past blank space
                while(pos<len&&line.charAt(pos)<=' ') pos++;
                int start=pos;
                // Skip to the next blank space
                while(pos<len&&line.charAt(pos)>' ') pos++;
                if(pos>start) wordCount++;
            }

            char[][] words=buff[0];
            if(words==null || words.length<wordCount) buff[0]=words=new char[wordCount][];

            int wordPos=0;
            pos=0;
            while(pos<len) {
                // Skip past blank space
                while(pos<len&&line.charAt(pos)<=' ') pos++;
                int start=pos;
                // Skip to the next blank space
                while(pos<len&&line.charAt(pos)>' ') pos++;
                if(pos>start) {
                    int chlen=pos-start;
                    char[] tch=words[wordPos++]=new char[chlen];
                    System.arraycopy(line.toCharArray(), start, tch, 0, chlen);
                }
            }

            return wordCount;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Splits a <code>String</code> into a <code>String[]</code>.
     */
    public static int splitString(String line, String[][] buff) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "splitString(String,String[][])", null);
        try {
            int len=line.length();
            int wordCount=0;
            int pos=0;
            while(pos<len) {
                // Skip past blank space
                while(pos<len&&line.charAt(pos)<=' ') pos++;
                int start=pos;
                // Skip to the next blank space
                while(pos<len&&line.charAt(pos)>' ') pos++;
                if(pos>start) wordCount++;
            }

            String[] words=buff[0];
            if(words==null || words.length<wordCount) buff[0]=words=new String[wordCount];

            int wordPos=0;
            pos=0;
            while(pos<len) {
                // Skip past blank space
                while(pos<len&&line.charAt(pos)<=' ') pos++;
                int start=pos;
                // Skip to the next blank space
                while(pos<len&&line.charAt(pos)>' ') pos++;
                if(pos>start) words[wordPos++]=line.substring(start, pos);
            }

            return wordCount;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public static String[] splitString(String line, char delim) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "splitString(String,char)", null);
        try {
            List<String> words = new ArrayList<String>();
            int len = line.length();
            int pos = 0;
            while (pos < len) {
                int start = pos;
                pos = line.indexOf(delim, pos);
                if (pos == -1)
                        pos = len;
                words.add(line.substring(start, pos));
                pos++;
            }
            // If ending in a delimeter, add the empty string
            if(len>0 && line.charAt(len-1)==delim) words.add("");

            // Copy the ArrayList into a String[]
            return getStringArray(words);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Splits a string into multiple words on either whitespace or commas
     * @return java.lang.String[]
     * @param line java.lang.String
     */
    public static List<String> splitStringCommaSpace(String line) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "splitStringCommaSpace(String)", null);
        try {
            List<String> words=new ArrayList<String>();
            int len=line.length();
            int pos=0;
            while(pos<len) {
                // Skip past blank space
                char ch;
                while(pos<len && ((ch=line.charAt(pos))<=' ' || ch==',')) pos++;
                int start=pos;
                // Skip to the next blank space
                while(pos<len && (ch=line.charAt(pos))>' ' && ch!=',') pos++;
                if(pos>start) words.add(line.substring(start,pos));
            }
            return words;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  Please use more standard String manipulation
     */
    public static String substring(char[] chars, int start, int end) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, StringUtility.class, "substring(char[],int,int)", null);
        try {
            return substring(chars, chars.length, start, end);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * @deprecated  Please use more standard String manipulation
     */
    public static String substring(char[] chars, int len, int start, int end) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "substring(char[],int,int,int)", null);
        try {
            if(end>len) end=len;
            return (end<=start) ? "" : new String(chars, start, end-start);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  No longer supported.
     */
    public static String titleCase(String input) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "titleCase(String)", null);
        try {
            input = input.trim();
            StringBuilder buff = new StringBuilder();
            boolean lastWasSpace=true;
            int len=input.length();
            for(int i=0;i<len;i++) {
                char ch=input.charAt(i);
                if(ch<=' ') {
                    buff.append(ch);
                    lastWasSpace=true;
                } else {
                    if(lastWasSpace) buff.append( (ch>='a' && ch<='z') ? ((char)(ch+'A'-'a')) : ch );
                    else buff.append( (ch>='A' && ch<='Z') ? ((char)(ch+'a'-'A')) : ch );
                    lastWasSpace = (ch<=' ');
                }
            }
            return buff.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Creates a <code>String</code> from a <code>char[]</code> while trimming.
     *
     * @see  java.lang.String#trim
     *
     * @deprecated  Please use more standard String manipulation
     */
    public static String trim(char[] ch, int start, int len) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "trim(char[],int,int)", null);
        try {
            int st=0;
            while( st<len && ch[start+st]<=' ') st++;
            while( st<len && ch[start+len-1]<=' ') len--;
            return new String(ch, start+st, len-st);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Creates a <code>char[]</code> from a <code>char[]</code> while trimming.
     *
     * @see  java.lang.String#trim
     *
     * @deprecated  Please use more standard String manipulation
     */
    public static char[] trimCharArray(char[] ch, int start, int len) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "trimCharArray(char[],int,int)", null);
        try {
            int st=0;
            while( st<len && ch[start+st]<=' ') st++;
            while( st<len && ch[start+len-1]<=' ') len--;

            char[] ret=new char[len-st];
            System.arraycopy(ch, start+st, ret, 0, len-st);
            return ret;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Word wraps a <code>String</code> to be no longer than the provided number of characters wide.
     */
    public static String wordWrap(String string, int width) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "wordWrap(String,int)", null);
        try {
            width++;
            StringBuilder SB = new StringBuilder();
            boolean useCR = false;
            do {
                int pos = string.indexOf('\n');
                if (!useCR && pos > 0 && string.charAt(pos - 1) == '\r') useCR = true;
                int linelength = pos == -1 ? string.length() : pos + 1;
                if ((pos==-1?linelength-1:pos) <= width) {
                    // No wrap required
                    SB.append(string.substring(0, linelength));
                    string = string.substring(linelength);
                } else {
                    // Word wrap required

                    // Search for the beginning of the first word that is past the <code>width</code> column
                    // The wrap character must be on the same line as the outputted line.
                    int lastBreakChar = 0;

                    for (int c = 0; c < width; c++) {
                        // Check to see if it is a break character
                        char ch = string.charAt(c);
                        boolean isBreak = false;
                        for (int d = 0; d < wordWrapChars.length; d++) {
                            if (ch == wordWrapChars[d]) {
                                isBreak = true;
                                break;
                            }
                        }
                        if (isBreak) lastBreakChar = c + 1;
                    }

                    // If no break has been found, keep searching until a break is found
                    if (lastBreakChar == 0) {
                        for (int c = width; c < linelength; c++) {
                            char ch = string.charAt(c);
                            boolean isBreak = false;
                            for (int d = 0; d < wordWrapChars.length; d++) {
                                if (ch == wordWrapChars[d]) {
                                    isBreak = true;
                                    break;
                                }
                            }
                            if (isBreak) {
                                lastBreakChar = c + 1;
                                break;
                            }
                        }
                    }

                    if (lastBreakChar == 0) {
                        // Take the whole line
                        SB.append(string.substring(0, linelength));
                        string = string.substring(linelength);
                    } else {
                        // Break out the section
                        SB.append(string.substring(0, lastBreakChar));
                        if (useCR) SB.append('\r');
                        SB.append('\n');
                        string = string.substring(lastBreakChar);
                    }
                }
            } while (string.length() > 0);
            return SB.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * @deprecated  No longer supported.
     */
    public static String[] getSortedStrings(Iterator I) {
        List<String> V=new ArrayList<String>();
        while(I.hasNext()) {
            Object O=I.next();
            V.add(O==null?null:O.toString());
        }
        return getStringArray(V);
    }

    public static String convertToHex(byte[] bytes) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "covertToHex(byte[])", null);
        try {
            if(bytes==null) return null;
            int len=bytes.length;
            StringBuilder SB=new StringBuilder(len*2);
            for(int c=0;c<len;c++) {
                int b=bytes[c]&255;
                int b1=b>>4;
                SB.append((char)((b1<=9)?('0'+b1):('a'+b1-10)));
                b&=0xf;
                SB.append((char)((b<=9)?('0'+b):('a'+b-10)));
            }
            return SB.toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the approximate size of a file in this format:
     *
     * x byte(s)
     * xx bytes
     * xxx bytes
     * x.x k
     * xx k
     * xxx k
     * x.x M
     * xx M
     * xxx M
     * x.x G
     * xx G
     * xxx G
     * x.x T
     * xx T
     * xxx T
     * xxx... T
     */
    public static String getApproximateSize(long size) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "getApproximateSize(long)", null);
        try {
            if(size==1) return "1 byte";
            if(size<1024) return new StringBuilder().append((int)size).append(" bytes").toString();
            String unitName;
            long unitSize;
            if(size<(1024*1024)) {
                unitName=" k";
                unitSize=1024;
            } else if(size<((long)1024*1024*1024)) {
                unitName=" M";
                unitSize=1024*1024;
            } else if(size<((long)1024*1024*1024*1024)) {
                unitName=" G";
                unitSize=(long)1024*1024*1024;
            } else {
                unitName=" T";
                unitSize=(long)1024*1024*1024*1024;
            }
            long whole=size/unitSize;
            if(whole<10) {
                int fraction=(int)(((size%unitSize)*10)/unitSize);
                return new StringBuilder().append(whole).append('.').append(fraction).append(unitName).toString();
            } else return new StringBuilder().append(whole).append(unitName).toString();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Compares to strings in a case insensitive manner.  However, if they are considered equals in a case
     * insensitive manner, the case sensitive comparison is done.
     */
    public static int compareToIgnoreCaseCarefulEquals(String S1, String S2) {
        Profiler.startProfile(Profiler.FAST, StringUtility.class, "compareToIgnoreCaseCarefulEquals(String,String)", null);
        try {
            int diff=S1.compareToIgnoreCase(S2);
            if(diff==0) diff=S1.compareTo(S2);
            return diff;
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}
