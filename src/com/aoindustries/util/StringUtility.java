package com.aoindustries.util;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
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
        StringBuilder SB=new StringBuilder();
        int len=list.length;
        for(int c=0;c<len;c++) {
            if(c==0) SB.append('<'); else SB.append(",<");
            SB.append(list[c]).append('>');
        }
        return SB.toString();
    }

    /**
     * Constructs a comma seperated list from a <code>String[]</code>.
     */
    public static String buildList(String[] list) {
        StringBuilder SB=new StringBuilder();
        int len=list.length;
        for(int c=0;c<len;c++) {
            if(c>0) SB.append(", ");
            SB.append(list[c]);
        }
        return SB.toString();
    }

    /**
     * Constructs a comma seperated list from an <code>Object[]</code>.
     */
    public static String buildList(Object[] list) {
        StringBuilder SB=new StringBuilder();
        int len=list.length;
        for(int c=0;c<len;c++) {
            if(c>0) SB.append(", ");
            SB.append(list[c]);
        }
        return SB.toString();
    }

    public static String buildList(List<?> V) {
        StringBuilder SB=new StringBuilder();
        int len=V.size();
        for(int c=0;c<len;c++) {
            if(c>0) SB.append(",");
            SB.append(V.get(c));
        }
        return SB.toString();	
    }

    /**
     * Compare one date to another, must be in the DDMMYYYY format.
     *
     * @return  <0  if the first date is before the second<br>
     *          0   if the dates are the same or the format is invalid<br>
     *          >0  if the first date is after the second
     */
    public static int compareToDDMMYYYY(String date1, String date2) {
        if(date1.length()!=8 || date2.length()!=8) return 0;
        return compareToDDMMYYYY0(date1)-compareToDDMMYYYY0(date2);
    }

    private static int compareToDDMMYYYY0(String date) {
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
    }

    public static boolean containsIgnoreCase(String line, String word) {
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
    }

    public static long convertStringDateToTime(String date) throws IllegalArgumentException {
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
    }

    /**
     * Copies the contents of a String into a char[]
     *
     * @deprecated  Please use String.getChars(int,int,char[],int)
     *
     * @see String#getChars(int,int,char[],int)
     */
    public static void copyString(String S, char[] ch, int pos) {
        S.getChars(0, S.length(), ch, pos);
    }

    /**
     * Counts how many times a word appears in a line.  Case insensitive matching.
     */
    public static int countOccurances(byte[] buff, int len, String word) {
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
    }

    /**
     * Counts how many times a word appears in a line.  Case insensitive matching.
     */
    public static int countOccurances(byte[] buff, String word) {
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
    }

    /**
     * Counts how many times a word appears in a line.  Case insensitive matching.
     */
    public static int countOccurances(String line, String word) {
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
    }

    /**
     * Decodes a string that was encoded for a form.
     *
     * @deprecated  Please find another means of performing this task, this code is not well-tested
     *              and is no longer supported
     */
    public static String decodeFormData(String string) {
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
    }

    /**
     * @deprecated  Please use SQLUtility.escapeSQL(s.replace('*', '%'))
     *
     * @see  SQLUtility#escapeSQL(String)
     */
    public static final String escapeSQL(String s) {
        return SQLUtility.escapeSQL(s.replace('*', '%'));
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
    }

    /**
     * @deprecated  Please use SQLUtility.getDate(long)
     *
     * @see  SQLUtility#getDate(long)
     */
    public static String getDateString(long time) {
        return getDateString(new Date(time));
    }

    /**
     * @deprecated  Please use SQLUtility.getDate(date.getTime())
     *
     * @see  SQLUtility#getDate(long)
     */
    public static String getDateString(Date date) {
        synchronized(StringUtility.class) {
            calendar.setTime(date);
            int day=calendar.get(Calendar.DATE);
            return (day>=0 && day<=9 ? "0":"")+String.valueOf(calendar.get(Calendar.DATE))+MONTHS[calendar.get(Calendar.MONTH)]+calendar.get(Calendar.YEAR);
        }
    }

    /**
     * @deprecated  Please use SQLUtility.getDate(date.getTime())
     *
     * @see  SQLUtility#getDate(long)
     */
    public static String getDateStringMMDDYYYY(Date date) {
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
    }

    /**
     * @deprecated  Please use SQLUtility.getDateTime(long)
     *
     * @see  SQLUtility#getDateTime(long)
     */
    public static String getDateStringSecond(long time) {
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
    }

    /**
     * @deprecated  Please use SQLUtility.getDateTime(long)
     *
     * @see  SQLUtility#getDateTime(long)
     */
    public static String getDateStringSecond(String time) {
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
    }

    /**
     * Converts one hex digit to an integer
     */
    public static int getHex(char ch) throws IllegalArgumentException {
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
    }

    /**
     * Creates a <code>String[]</code> by calling the toString() method of each object in a list.
     *
     * @deprecated  Please use List.toArray(Object[])
     *
     * @see  List#toArray(Object[])
     */
    public static String[] getStringArray(List V) {
        if(V==null) return null;
        int len = V.size();
        String[] SA = new String[len];
        for (int c = 0; c < len; c++) {
            Object O=V.get(c);
            SA[c]=O==null?null:O.toString();
        }
        return SA;
    }

    public static String getTimeLengthString(long time) {
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
    }

    public static String getDecimalTimeLengthString(long time) {
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
        int Slen=S.length();
        int clen=chars.length;
        for(int c=0;c<Slen;c++) {
            char ch=S.charAt(c);
            for(int d=0;d<clen;d++) if(ch==chars[d]) return c;
        }
        return -1;
    }

    /**
     * @deprecated  Please use Calendar class instead.
     *
     * @see  Calendar
     */
    public static boolean isValidDate(String date) {
        try {
            convertStringDateToTime(date);
            return true;
        } catch (IllegalArgumentException err) {
            return false;
        }
    }

    /**
     * @deprecated  Please use Calendar class instead.
     *
     * @see  Calendar
     */
    public static boolean leapYear(int year) {
        return year%4==0 && year%400==0;
    }

    /**
     * Removes all occurances of a <code>char</code> from a <code>String</code>
     *
     * @deprecated  this method is slow and no longer supported
     */
    public static String removeChars(String S, char[] chars) {
        int pos;
        while((pos=indexOf(S, chars))!=-1) S=S.substring(0,pos)+S.substring(pos+1);
        return S;
    }

    /**
     * Removes all occurances of a <code>char</code> from a <code>String</code>
     *
     * @deprecated  this method is slow and no longer supported
     */
    public static String removeChars(String S, char ch) {
        int pos;
        while((pos=S.indexOf(ch))!=-1) S=S.substring(0,pos)+S.substring(pos+1);
        return S;
    }

    /**
     * Replaces all occurances of a character with a String
     */
    public static String replace(String string, char ch, String replacement) {
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
    }

    /**
     * Replaces all occurances of a character with a String
     */
    public static String replace(String string, String find, String replacement) {
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
    }

    /**
     * Sorts a list of strings in ascending order.
     *
     * @deprecated  Please use new SortAlgorithm classes
     *
     * @see  com.aoindustries.util.sort.AutoSort#sortStatic(Object[])
     */
    public static void sort(String[] list) {
        AutoSort.sortStatic(list);
    }

    private static int compareTo(String S1, String S2) {
        if(S1==null) {
            return S2==null?0:-1;
        }
        return S2==null?1:S1.compareTo(S2);
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
    }

    /**
     * Sorts the given <code>List</code> in descending lexical order.  Each item is expected to
     * be contained in the elements, <code>Object</code> and then <code>Float</code>.
     */
    /*
    public static void sortObjectFloatDescending(List list) {
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
    }
    */
    public static <T> void sortObjects(List<T> list, String[] strings) {
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
    }

    public static <T> void sortObjectsMMDDYYYY(List<T> list, String[] strings) {
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
    }

    /**
     * Splits a String into lines on any '\n' characters.  Also removes any ending '\r' characters if present
     */
    public static List<String> splitLines(String S) {
        List<String> V=new ArrayList<String>();
        int start=0;
        int pos;
        while((pos=S.indexOf('\n', start))!=-1) {
            String line = S.substring(start, pos);
            if(line.endsWith("\r")) line=line.substring(0, line.length()-1);
            V.add(line);
            start=pos+1;
        }
        if(start<S.length()) {
            String line = S.substring(start);
            if(line.endsWith("\r")) line=line.substring(0, line.length()-1);
            V.add(line);
        }
        return V;
    }

    /**
     * Splits a <code>String</code> into a <code>String[]</code>.
     */
    public static String[] splitString(String line) {
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
    }

    /**
     * Splits a <code>String</code> into a <code>String[]</code>.
     */
    public static int splitString(String line, char[][][] buff) {
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
    }

    /**
     * Splits a <code>String</code> into a <code>String[]</code>.
     */
    public static int splitString(String line, String[][] buff) {
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
    }

    public static String[] splitString(String line, char delim) {
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
    }

    public static List<String> splitString(String line, String delim) {
        int delimLen = delim.length();
        if(delimLen==0) throw new IllegalArgumentException("Delimiter may not be empty");
        List<String> words = new ArrayList<String>();
        int len = line.length();
        int pos = 0;
        while (pos < len) {
            int start = pos;
            pos = line.indexOf(delim, pos);
            if (pos == -1) {
                words.add(line.substring(start, len));
                pos = len;
            } else {
                words.add(line.substring(start, pos));
                pos += delimLen;
            }
        }
        // If ending in a delimeter, add the empty string
        if(len>=delimLen && line.substring(len-delimLen).equals(delim)) words.add("");

        return words;
    }

    /**
     * Splits a string into multiple words on either whitespace or commas
     * @return java.lang.String[]
     * @param line java.lang.String
     */
    public static List<String> splitStringCommaSpace(String line) {
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
    }

    /**
     * @deprecated  Please use more standard String manipulation
     */
    public static String substring(char[] chars, int start, int end) {
        return substring(chars, chars.length, start, end);
    }

    /**
     * @deprecated  Please use more standard String manipulation
     */
    public static String substring(char[] chars, int len, int start, int end) {
        if(end>len) end=len;
        return (end<=start) ? "" : new String(chars, start, end-start);
    }

    /**
     * @deprecated  No longer supported.
     */
    public static String titleCase(String input) {
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
    }

    /**
     * Creates a <code>String</code> from a <code>char[]</code> while trimming.
     *
     * @see  java.lang.String#trim
     *
     * @deprecated  Please use more standard String manipulation
     */
    public static String trim(char[] ch, int start, int len) {
        int st=0;
        while( st<len && ch[start+st]<=' ') st++;
        while( st<len && ch[start+len-1]<=' ') len--;
        return new String(ch, start+st, len-st);
    }

    /**
     * Creates a <code>char[]</code> from a <code>char[]</code> while trimming.
     *
     * @see  java.lang.String#trim
     *
     * @deprecated  Please use more standard String manipulation
     */
    public static char[] trimCharArray(char[] ch, int start, int len) {
        int st=0;
        while( st<len && ch[start+st]<=' ') st++;
        while( st<len && ch[start+len-1]<=' ') len--;

        char[] ret=new char[len-st];
        System.arraycopy(ch, start+st, ret, 0, len-st);
        return ret;
    }

    /**
     * Word wraps a <code>String</code> to be no longer than the provided number of characters wide.
     */
    public static String wordWrap(String string, int width) {
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
    }

    /**
     * Gets the approximate size (where k=1024) of a file in this format:
     *
     * x byte(s)
     * xx bytes
     * xxx bytes
     * x.x k
     * xx.x k
     * xxx k
     * x.x M
     * xx.x M
     * xxx M
     * x.x G
     * xx.x G
     * xxx G
     * x.x T
     * xx.x T
     * xxx T
     * xxx... T
     */
    public static String getApproximateSize(long size) {
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
        if(whole<100) {
            int fraction=(int)(((size%unitSize)*10)/unitSize);
            return new StringBuilder().append(whole).append('.').append(fraction).append(unitName).toString();
        } else return new StringBuilder().append(whole).append(unitName).toString();
    }

    /**
     * Gets the approximate bit rate (where k=1000) in this format:
     *
     * x
     * xx
     * xxx
     * x.x k
     * xx.x k
     * xxx k
     * x.x M
     * xx.x M
     * xxx M
     * x.x G
     * xx.x G
     * xxx G
     * x.x T
     * xx.x T
     * xxx T
     * xxx... T
     */
    public static String getApproximateBitRate(long bit_rate) {
        if(bit_rate<1000) return Integer.toString((int)bit_rate);
        String unitName;
        long unitSize;
        if(bit_rate<(1000*1000)) {
            unitName=" k";
            unitSize=1000;
        } else if(bit_rate<((long)1000*1000*1000)) {
            unitName=" M";
            unitSize=1000*1000;
        } else if(bit_rate<((long)1000*1000*1000*1000)) {
            unitName=" G";
            unitSize=(long)1000*1000*1000;
        } else {
            unitName=" T";
            unitSize=(long)1000*1000*1000*1000;
        }
        long whole=bit_rate/unitSize;
        if(whole<100) {
            int fraction=(int)(((bit_rate%unitSize)*10)/unitSize);
            return new StringBuilder().append(whole).append('.').append(fraction).append(unitName).toString();
        } else return new StringBuilder().append(whole).append(unitName).toString();
    }

    /**
     * Compares to strings in a case insensitive manner.  However, if they are considered equals in a case
     * insensitive manner, the case sensitive comparison is done.
     */
    public static int compareToIgnoreCaseCarefulEquals(String S1, String S2) {
        int diff=S1.compareToIgnoreCase(S2);
        if(diff==0) diff=S1.compareTo(S2);
        return diff;
    }
    
    /**
     * Null-safe intern: interns a String if it is not null, returns null if parameter is null.
     */
    public static String intern(String S) {
        if(S==null) return null;
        return S.intern();
    }
    
    /**
     * Finds the next of a substring like regular String.indexOf, but stops at a certain maximum index.
     * Like substring, will look up to the character one before toIndex.
     */
    public static int indexOf(String source, String target, int fromIndex, int toIndex) {
        if(fromIndex>toIndex) throw new IllegalArgumentException("fromIndex>toIndex: fromIndex="+fromIndex+", toIndex="+toIndex);

        int sourceCount = source.length();

        // This line makes it different than regular String indexOf method.
        if(toIndex<sourceCount) sourceCount = toIndex;

        int targetCount = target.length();

        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
	}
    	if (fromIndex < 0) {
    	    fromIndex = 0;
    	}
	if (targetCount == 0) {
	    return fromIndex;
	}

        char first  = target.charAt(0);
        int max = sourceCount - targetCount;

        for (int i = fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source.charAt(i) != first) {
                while (++i <= max && source.charAt(i) != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = 1; j < end && source.charAt(j) ==
                         target.charAt(k); j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    return i;
                }
            }
        }
        return -1;
    }
}
