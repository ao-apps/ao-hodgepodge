/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2013, 2016, 2018, 2019  AO Industries, Inc.
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
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * SQL utilities.
 *
 * @author  AO Industries, Inc.
 */
public class SQLUtility {

	private SQLUtility() {
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatDate(java.util.Calendar)
	 */
	public static String formatDate(long time, TimeZone timeZone) {
		GregorianCalendar gcal = timeZone == null ? new GregorianCalendar() : new GregorianCalendar(timeZone);
		gcal.setTimeInMillis(time);
		return CalendarUtils.formatDate(gcal);
	}

	/**
	 * @see  CalendarUtils#formatDate(java.util.Calendar)
	 */
	public static String formatDate(long time) {
		return formatDate(time, null);
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatDate(java.util.Calendar)
	 */
	public static String formatDate(Long time, TimeZone timeZone) {
		return time == null ? null : formatDate(time.longValue(), timeZone);
	}

	/**
	 * @see  CalendarUtils#formatDate(java.util.Calendar)
	 */
	public static String formatDate(Long time) {
		return time == null ? null : formatDate(time.longValue());
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatDate(java.util.Calendar)
	 */
	public static String formatDate(java.util.Date date, TimeZone timeZone) {
		return date == null ? null : formatDate(date.getTime(), timeZone);
	}

	/**
	 * @see  CalendarUtils#formatDate(java.util.Calendar)
	 */
	public static String formatDate(java.util.Date date) {
		return date == null ? null : formatDate(date.getTime());
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#parseDate(java.lang.String, java.util.TimeZone)
	 */
	public static Date parseDate(String yyyy_mm_dd, TimeZone timeZone) throws IllegalArgumentException {
		if(yyyy_mm_dd == null) return null;
		return new Date(CalendarUtils.parseDate(yyyy_mm_dd, timeZone).getTimeInMillis());
	}

	/**
	 * @see  CalendarUtils#parseDate(java.lang.String)
	 */
	public static Date parseDate(String yyyy_mm_dd) throws IllegalArgumentException {
		return parseDate(yyyy_mm_dd, null);
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatDateTime(java.util.Calendar)
	 */
	public static String formatDateTime(long time, TimeZone timeZone) {
		GregorianCalendar gcal = timeZone == null ? new GregorianCalendar() : new GregorianCalendar(timeZone);
		gcal.setTimeInMillis(time);
		return CalendarUtils.formatDateTime(gcal);
	}

	/**
	 * @see  CalendarUtils#formatDateTime(java.util.Calendar)
	 */
	public static String formatDateTime(long time) {
		return formatDateTime(time, null);
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatDateTime(java.util.Calendar)
	 */
	public static String formatDateTime(Long time, TimeZone timeZone) {
		return time == null ? null : formatDateTime(time.longValue(), timeZone);
	}

	/**
	 * @see  CalendarUtils#formatDateTime(java.util.Calendar)
	 */
	public static String formatDateTime(Long time) {
		return time == null ? null : formatDateTime(time.longValue());
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatDateTime(java.util.Calendar)
	 */
	public static String formatDateTime(java.util.Date date, TimeZone timeZone) {
		return date == null ? null : formatDateTime(date.getTime(), timeZone);
	}

	/**
	 * @see  CalendarUtils#formatDateTime(java.util.Calendar)
	 */
	public static String formatDateTime(java.util.Date date) {
		return date == null ? null : formatDateTime(date.getTime());
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#parseDateTime(java.lang.String, java.util.TimeZone, com.aoindustries.util.CalendarUtils.DateTimeProducer)
	 */
	public static Timestamp parseDateTime(String dateTime, TimeZone timeZone) throws IllegalArgumentException {
		if(dateTime == null) return null;
		return CalendarUtils.parseDateTime(
			dateTime,
			timeZone,
			new CalendarUtils.DateTimeProducer<Timestamp>() {
				@Override
				public Timestamp createDateTime(GregorianCalendar gcal, int nanos) {
					long millis = gcal.getTimeInMillis();
					long seconds = millis / 1000;
					if((millis % 1000) < 0) seconds--;
					return newTimestamp(seconds, nanos);
				}
			}
		);
	}

	/**
	 * @see  CalendarUtils#parseDateTime(java.lang.String)
	 */
	public static Timestamp parseDateTime(String dateTime) throws IllegalArgumentException {
		return parseDateTime(dateTime, null);
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatTime(java.util.Calendar)
	 */
	public static String formatTime(long time, TimeZone timeZone) {
		GregorianCalendar gcal = timeZone == null ? new GregorianCalendar() : new GregorianCalendar(timeZone);
		gcal.setTimeInMillis(time);
		return CalendarUtils.formatTime(gcal);
	}

	/**
	 * @see  CalendarUtils#formatTime(java.util.Calendar)
	 */
	public static String formatTime(long time) {
		return formatTime(time, null);
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatTime(java.util.Calendar)
	 */
	public static String formatTime(Long time, TimeZone timeZone) {
		return time == null ? null : formatTime(time.longValue(), timeZone);
	}

	/**
	 * @see  CalendarUtils#formatTime(java.util.Calendar)
	 */
	public static String formatTime(Long time) {
		return time == null ? null : formatTime(time.longValue());
	}

	/**
	 * @param timeZone  The time zone to use or {@code null} to use the default time zone
	 *
	 * @see  CalendarUtils#formatTime(java.util.Calendar)
	 */
	public static String formatTime(java.util.Date date, TimeZone timeZone) {
		return date == null ? null : formatTime(date.getTime(), timeZone);
	}

	/**
	 * @see  CalendarUtils#formatTime(java.util.Calendar)
	 */
	public static String formatTime(java.util.Date date) {
		return date == null ? null : formatTime(date.getTime());
	}

	/**
	 * Converts a number of pennies into decimal representation.
	 */
	public static String formatDecimal2(int pennies) {
		StringBuilder SB=new StringBuilder(12);
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
	public static String formatDecimal2(long pennies) {
		StringBuilder SB=new StringBuilder(21);
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
	 * Gets the number of pennies represented by a <code>String</code> containing a decimal(?,2) type.
	 */
	// TODO: Parse in a way with less/no internal string concatenation
	public static int parseDecimal2(String decimal2) {
		// Get the sign first, treat as negative, then apply the sign
		boolean isNegative;
		if(decimal2.length()>0 && decimal2.charAt(0)=='-') {
			isNegative=true;
			decimal2=decimal2.substring(1);
		} else isNegative=false;

		// Add zero to beginning if starts with .
		if(decimal2.length()>0 && decimal2.charAt(0)=='.') decimal2='0'+decimal2;

		// Allow for incomplete data like 2, 2., and 2.3
		if(decimal2.indexOf('.')==-1) decimal2 += ".00";
		else if(decimal2.charAt(decimal2.length()-1)=='.') decimal2 += "00";
		else if(decimal2.length()>=2 && decimal2.charAt(decimal2.length()-2)=='.') decimal2 += '0';

		int len = decimal2.length();
		int dollars = Integer.parseInt(decimal2.substring(0, len - 3));
		int pennies = Integer.parseInt(decimal2.substring(len - 2));
		long result = (isNegative?-1l:1l)*(dollars * 100l + pennies);
		if(result<Integer.MIN_VALUE || result>Integer.MAX_VALUE) throw new NumberFormatException("Out of range during conversion");
		return (int)result;
	}

	/**
	 * Gets the number of pennies represented by a <code>String</code> containing a decimal(?,2) type.
	 */
	// TODO: Parse in a way with less/no internal string concatenation
	public static long parseLongDecimal2(String decimal2) {
		// Get the sign first, treat as negative, then apply the sign
		boolean isNegative;
		if(decimal2.length()>0 && decimal2.charAt(0)=='-') {
			isNegative=true;
			decimal2=decimal2.substring(1);
		} else isNegative=false;

		// Add zero to beginning if starts with .
		if(decimal2.length()>0 && decimal2.charAt(0)=='.') decimal2='0'+decimal2;

		// Allow for incomplete data like 2, 2., and 2.3
		if(decimal2.indexOf('.')==-1) decimal2 += ".00";
		else if(decimal2.charAt(decimal2.length()-1)=='.') decimal2 += "00";
		else if(decimal2.length()>=2 && decimal2.charAt(decimal2.length()-2)=='.') decimal2 += '0';

		int len = decimal2.length();
		long dollars = Long.parseLong(decimal2.substring(0, len - 3));
		int pennies = Integer.parseInt(decimal2.substring(len - 2));
		return (isNegative?-1:1)*(dollars * 100 + pennies);
	}

	/**
	 * Converts a number of millis into decimal representation.
	 */
	public static String formatDecimal3(int millis) {
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
	public static String formatDecimal3(long millis) {
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
	 * Gets the number of millis represented by a <code>String</code> containing a decimal(?,3) type.
	 */
	// TODO: Parse in a way with less/no internal string concatenation
	public static int parseDecimal3(String decimal3) {
		// Get the sign first, treat as negative, then apply the sign
		boolean isNegative;
		if (decimal3.length() > 0 && decimal3.charAt(0) == '-') {
			isNegative = true;
			decimal3 = decimal3.substring(1);
		} else isNegative = false;

		// Add zero to beginning if starts with .
		if(decimal3.length()>0 && decimal3.charAt(0)=='.') decimal3='0'+decimal3;

		// Allow for incomplete data like 2, 2., 2.3, and 2.34
		if(decimal3.indexOf('.')==-1) decimal3 += ".000";
		else if(decimal3.charAt(decimal3.length()-1)=='.') decimal3 += "000";
		else if(decimal3.length()>=2 && decimal3.charAt(decimal3.length()-2)=='.') decimal3 += "00";
		else if(decimal3.length()>=3 && decimal3.charAt(decimal3.length()-3)=='.') decimal3 += '0';

		int len = decimal3.length();
		int whole = Integer.parseInt(decimal3.substring(0, len - 4));
		int millis = Integer.parseInt(decimal3.substring(len - 3));
		long result = (isNegative?-1L:1L)*(whole * 1000L + millis);
		if(result<Integer.MIN_VALUE || result>Integer.MAX_VALUE) throw new NumberFormatException("Out of range during conversion");
		return (int)result;
	}

	/**
	 * Gets the number of millis represented by a <code>String</code> containing a decimal(?,3) type.
	 */
	// TODO: Parse in a way with less/no internal string concatenation
	public static long parseLongDecimal3(String decimal3) {
		// Get the sign first, treat as negative, then apply the sign
		boolean isNegative;
		if (decimal3.length() > 0 && decimal3.charAt(0) == '-') {
			isNegative = true;
			decimal3 = decimal3.substring(1);
		} else isNegative = false;

		// Add zero to beginning if starts with .
		if(decimal3.length()>0 && decimal3.charAt(0)=='.') decimal3='0'+decimal3;

		// Allow for incomplete data like 2, 2., 2.3, and 2.34
		if(decimal3.indexOf('.')==-1) decimal3 += ".000";
		else if(decimal3.charAt(decimal3.length()-1)=='.') decimal3 += "000";
		else if(decimal3.length()>=2 && decimal3.charAt(decimal3.length()-2)=='.') decimal3 += "00";
		else if(decimal3.length()>=3 && decimal3.charAt(decimal3.length()-3)=='.') decimal3 += '0';

		int len = decimal3.length();
		long whole = Long.parseLong(decimal3.substring(0, len - 4));
		int millis = Integer.parseInt(decimal3.substring(len - 3));
		return (isNegative?-1L:1L)*(whole * 1000L + millis);
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

	/**
	 * The maximum number of seconds in a {@link Timestamp}.
	 */
	public static final long MAX_TIMESTAMP_SECONDS = Long.MAX_VALUE / 1000;

	/**
	 * The minimum number of seconds in a {@link Timestamp}.
	 */
	public static final long MIN_TIMESTAMP_SECONDS = Long.MIN_VALUE / 1000;

	/**
	 * Converts a number of seconds and nanoseconds into a given {@link Timestamp}.
	 */
	// TODO: Experimental
	public static <E extends Throwable> void toTimestamp(long seconds, int nanos, Timestamp ts, Class<E> exceptionType) throws E {
		// Avoid underflow or overflow on conversion to millis
		String message;
		if(seconds > MAX_TIMESTAMP_SECONDS) {
			message = "seconds overflow: " + seconds + " > " + MAX_TIMESTAMP_SECONDS;
		} else if(seconds < MIN_TIMESTAMP_SECONDS) {
			message = "seconds underflow: " + seconds + " < " + MAX_TIMESTAMP_SECONDS;
		} else {
			ts.setTime(seconds * 1000);
			ts.setNanos(nanos);
			return;
		}
		try {
			throw exceptionType.getConstructor(String.class).newInstance(message);
		} catch(ReflectiveOperationException e) {
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * Converts a number of seconds and nanoseconds into a given {@link Timestamp}.
	 */
	public static void toTimestamp(long seconds, int nanos, Timestamp ts) {
		// TODO: Experimental
		toTimestamp(seconds, nanos, ts, IllegalArgumentException.class);
		/*
		// Avoid underflow or overflow on conversion to millis
		if(seconds > MAX_TIMESTAMP_SECONDS) throw new IllegalArgumentException("seconds overflow: " + seconds + " > " + MAX_TIMESTAMP_SECONDS);
		if(seconds < MIN_TIMESTAMP_SECONDS) throw new IllegalArgumentException("seconds underflow: " + seconds + " < " + MAX_TIMESTAMP_SECONDS);
		ts.setTime(seconds * 1000);
		ts.setNanos(nanos);
		 */
	}

	/**
	 * Converts a number of seconds and nanoseconds into a new {@link Timestamp}.
	 */
	// TODO: Experimental
	public static <E extends Throwable> Timestamp newTimestamp(long seconds, int nanos, Class<E> exceptionType) throws E {
		Timestamp ts = new Timestamp(0);
		toTimestamp(seconds, nanos, ts, exceptionType);
		return ts;
	}

	/**
	 * Converts a number of seconds and nanoseconds into a new {@link Timestamp}.
	 */
	public static Timestamp newTimestamp(long seconds, int nanos) throws IllegalArgumentException {
		// TODO: Experimental
		return newTimestamp(seconds, nanos, IllegalArgumentException.class);
		/*
		// Avoid underflow or overflow on conversion to millis
		if(seconds > MAX_TIMESTAMP_SECONDS) throw new IllegalArgumentException("seconds overflow: " + seconds + " > " + MAX_TIMESTAMP_SECONDS);
		if(seconds < MIN_TIMESTAMP_SECONDS) throw new IllegalArgumentException("seconds underflow: " + seconds + " < " + MAX_TIMESTAMP_SECONDS);
		Timestamp ts = new Timestamp(seconds * 1000);
		ts.setNanos(nanos);
		return ts;
		 */
	}
}
