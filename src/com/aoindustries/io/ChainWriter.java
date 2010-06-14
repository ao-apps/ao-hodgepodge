/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.io;

import com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.TextInJavaScriptEncoder;
import com.aoindustries.encoding.TextInXhtmlEncoder;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.EncodingUtils;
import com.aoindustries.util.Sequence;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * A chain writer encapsulates a <code>PrintWriter</code> and returns the <code>ChainWriter</code>
 * instance on most methods.  This gives the ability to call code like
 * <code>out.print("Hi ").print(name).print('!');</code>
 *
 * @author  AO Industries, Inc.
 */
final public class ChainWriter implements Appendable {

    // <editor-fold defaultstate="collapsed" desc="PrintWriter wrapping">
    private final PrintWriter out;

    /**
     * Create a new PrintWriter, without automatic line flushing, from an
     * existing OutputStream.  This convenience constructor creates the
     * necessary intermediate OutputStreamWriter, which will convert characters
     * into bytes using the default character encoding.
     *
     * @param  out        An output stream
     */
    public ChainWriter(OutputStream out) {
        this(new PrintWriter(out));
    }

    /**
     * Create a new PrintWriter from an existing OutputStream.  This
     * convenience constructor creates the necessary intermediate
     * OutputStreamWriter, which will convert characters into bytes using the
     * default character encoding.
     *
     * @param  out        An output stream
     * @param  autoFlush  A boolean; if true, the println() methods will flush
     *                    the output buffer
     */
    public ChainWriter(OutputStream out, boolean autoFlush) {
        this(new PrintWriter(out, autoFlush));
    }

    public ChainWriter(PrintWriter out) {
        this.out=out;
    }

    /**
     * Create a new PrintWriter, without automatic line flushing.
     *
     * @param  out        A character-output stream
     */
    public ChainWriter(Writer out) {
        this(new PrintWriter(out));
    }

    /**
     * Create a new PrintWriter.
     *
     * @param  out        A character-output stream
     * @param  autoFlush  A boolean; if true, the println() methods will flush
     *                    the output buffer
     */
    public ChainWriter(Writer out, boolean autoFlush) {
        this(new PrintWriter(out, autoFlush));
    }

    public PrintWriter getPrintWriter() {
        return out;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Nearly PrintWriter source compatible">

    /** Flush the stream. */
    public ChainWriter flush() {
        out.flush();
        return this;
    }

    /** Close the stream. */
    public ChainWriter close() {
        out.close();
        return this;
    }

    /** Write a single character. */
    public ChainWriter write(int c) {
        out.write(c);
        return this;
    }

    /** Write a portion of an array of characters. */
    public ChainWriter write(char buf[], int off, int len) {
        out.write(buf, off, len);
        return this;
    }

    /**
     * Write an array of characters.  This method cannot be inherited from the
     * Writer class because it must suppress I/O exceptions.
     */
    public ChainWriter write(char buf[]) {
        out.write(buf);
        return this;
    }

    /** Write a portion of a string. */
    public ChainWriter write(String s, int off, int len) {
        out.write(s, off, len);
        return this;
    }

    /**
     * Write a string.  This method cannot be inherited from the Writer class
     * because it must suppress I/O exceptions.
     */
    public ChainWriter write(String s) {
        out.write(s);
        return this;
    }

    /**
     * Print a boolean value.  The string produced by <code>{@link
     * java.lang.String#valueOf(boolean)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      b   The <code>boolean</code> to be printed
     */
    public ChainWriter print(boolean b) {
        out.print(b);
        return this;
    }

    /**
     * Print a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      c   The <code>char</code> to be printed
     */
    public ChainWriter print(char c) {
        out.print(c);
        return this;
    }

    /**
     * Print an integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(int)}</code> is translated into bytes according
     * to the platform's default character encoding, and these bytes are
     * written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      i   The <code>int</code> to be printed
     * @see        java.lang.Integer#toString(int)
     */
    public ChainWriter print(int i) {
        out.print(i);
        return this;
    }

    /**
     * Print a long integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      l   The <code>long</code> to be printed
     */
    public ChainWriter print(long l) {
        out.print(l);
        return this;
    }

    /**
     * Print a floating-point number.  The string produced by <code>{@link
     * java.lang.String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      f   The <code>float</code> to be printed
     * @see        java.lang.Float#toString(float)
     */
    public ChainWriter print(float f) {
        out.print(f);
        return this;
    }

    /**
     * Print a double-precision floating-point number.  The string produced by
     * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      d   The <code>double</code> to be printed
     */
    public ChainWriter print(double d) {
        out.print(d);
        return this;
    }

    /**
     * Print an array of characters.  The characters are converted into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      s   The array of chars to be printed
     *
     * @throws  NullPointerException  If <code>s</code> is <code>null</code>
     */
    public ChainWriter print(char s[]) {
        out.print(s);
        return this;
    }

    /**
     * Print a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      s   The <code>String</code> to be printed
     */
    public ChainWriter print(String s) {
        out.print(s);
        return this;
    }

    /**
     * Print an object.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      obj   The <code>Object</code> to be printed
     * @see        java.lang.Object#toString()
     */
    public ChainWriter print(Object obj) {
        out.print(obj);
        return this;
    }

    /**
     * Terminate the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public ChainWriter println() {
        out.println();
        return this;
    }

    /**
     * Print a boolean value and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(boolean x) {
        out.println(x);
        return this;
    }

    /**
     * Print a character and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
     * #println()}</code>.
     */
    public ChainWriter println(char x) {
        out.println(x);
        return this;
    }

    /**
     * Print an integer and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
     * #println()}</code>.
     */
    public ChainWriter println(int x) {
        out.println(x);
        return this;
    }

    /**
     * Print a long integer and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(long)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(long x) {
        out.println(x);
        return this;
    }

    /**
     * Print a floating-point number and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(float)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(float x) {
        out.println(x);
        return this;
    }


    /**
     * Print a double-precision floating-point number and then terminate the
     * line.  This method behaves as though it invokes <code>{@link
     * #print(double)}</code> and then <code>{@link #println()}</code>.
     */
    public ChainWriter println(double x) {
        out.println(x);
        return this;
    }

    /**
     * Print an array of characters and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(char[])}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(char x[]) {
        out.println(x);
        return this;
    }

    /**
     * Print a String and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(String x) {
        out.println(x);
        return this;
    }

    /**
     * Print an Object and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(Object)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(Object x) {
        out.println(x);
        return this;
    }

    public ChainWriter printf(String format, Object ... args) {
    	out.printf(format, args);
        return this;
    }

    public ChainWriter printf(Locale l, String format, Object ... args) {
        out.printf(l, format, args);
        return this;
    }

    public ChainWriter format(String format, Object ... args) {
        out.format(format, args);
        return this;
    }

    public ChainWriter format(Locale l, String format, Object ... args) {
        out.format(l, format, args);
        return this;
    }

    @Override
    public ChainWriter append(CharSequence csq) {
        out.append(csq);
        return this;
    }

    @Override
    public ChainWriter append(CharSequence csq, int start, int end) {
        out.append(csq, start, end);
        return this;
    }

    @Override
    public ChainWriter append(char c) {
        out.append(c);
        return this;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Encoding Methods">
    /**
     * @deprecated  Please use encodeXmlAttribute instead.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter printEI(String S) throws IOException {
        return encodeXmlAttribute(S);
    }

    /**
     * @see  EncodingUtils#encodeXmlAttribute(String, Appendable)
     *
     * @param S the string to be escaped
     */
    public ChainWriter encodeXmlAttribute(String S) throws IOException {
        EncodingUtils.encodeXmlAttribute(S, out);
        return this;
    }

    /**
     * @see  EncodingUtils#encodeXml(String, Appendable)
     *
     * @param S the string to be escaped
     */
    public ChainWriter encodeXhtml(String S) throws IOException {
        TextInXhtmlEncoder.encodeTextInXhtml(S, out);
        return this;
    }

    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter encodeHtml(String S) throws IOException {
        EncodingUtils.encodeHtml(S, true, true, out);
        return this;
    }

    /**
     * @deprecated  Please use encodeHtml instead.
     */
    public ChainWriter printEH(String S) throws IOException {
        return encodeHtml(S);
    }

    /**
     * @deprecated  Please use encodeHtml instead.
     */
    public ChainWriter printEH(String S, boolean make_br, boolean make_nbsp) throws IOException {
        return encodeHtml(S, make_br, make_nbsp);
    }

    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     * @param make_br  will write &lt;BR&gt; tags for every newline character
     */
    public ChainWriter encodeHtml(String S, boolean make_br, boolean make_nbsp) throws IOException {
        EncodingUtils.encodeHtml(S, make_br, make_nbsp, out);
        return this;
    }

    /**
     * @see TextInJavaScriptEncoder#encodeTextInJavaScript(java.lang.String, Appendable)
     *
     * @deprecated
     */
    @Deprecated
    public ChainWriter encodeJavaScriptString(String S) throws IOException {
        TextInJavaScriptEncoder.encodeTextInJavaScript(S, out);
        return this;
    }

    /**
     * @see EncodingUtils#encodeJavaScriptStringInXml(java.lang.String, Appendable)
     */
    public ChainWriter encodeJavaScriptStringInXml(String text) throws IOException {
        // Escape for javascript
        StringBuilder javascript = new StringBuilder(text.length());
        TextInJavaScriptEncoder.encodeTextInJavaScript(text, javascript);
        // Encode for XML attribute
        JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(javascript, out);
        return this;
    }

    /**
     * Prints a value that may be placed in a URL.
     *
     * @deprecated  Use URLEncoder instead.
     * @see URLEncoder
     */
    @Deprecated
    public ChainWriter printEU(String value) {
        int len = value.length();
        for (int c = 0; c < len; c++) {
            char ch = value.charAt(c);
            if (ch == ' ') out.print('+');
            else {
                if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) out.print(ch);
                else {
                    out.print('%');
                    out.print(getHex(ch >>> 4));
                    out.print(getHex(ch));
                }
            }
        }
        return this;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="HTML Utilities">
    private static final char[] hexChars={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static char getHex(int value) {
        return hexChars[value & 15];
    }

    /**
     * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
     */
    public static void writeHtmlColor(int color, Appendable out) throws IOException {
        out.append('#');
        out.append(getHex(color>>>20));
        out.append(getHex(color>>>16));
        out.append(getHex(color>>>12));
        out.append(getHex(color>>>8));
        out.append(getHex(color>>>4));
        out.append(getHex(color));
    }

    /**
     * @deprecated  Please use writeHtmlColor instead.
     */
    public ChainWriter printHTMLColor(int color) throws IOException {
        return writeHtmlColor(color);
    }

    /**
     * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
     */
    public ChainWriter writeHtmlColor(int color) throws IOException {
        writeHtmlColor(color, out);
        return this;
    }

    /**
     * Prints a JavaScript script that will preload the image at the provided URL.
     *
     * @param url This should be the URL-encoded URL, but with only a standalone ampersand (&amp;) as parameter separator
     *             (not &amp;amp;)
     */
    public static void writeHtmlImagePreloadJavaScript(String url, Appendable out) throws IOException {
        out.append("<script type='text/javascript'>\n"
                + "  var img=new Image();\n"
                + "  img.src=\"");
        // Escape for javascript
        StringBuilder javascript = new StringBuilder(url.length());
        TextInJavaScriptEncoder.encodeTextInJavaScript(url, javascript);
        // Encode for XML attribute
        JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(javascript, out);
        out.append("\";\n"
                + "</script>");
    }

    /**
     * @deprecated  Please use writeHtmlImagePreloadJavaScript instead.
     */
    public ChainWriter printImagePreloadJS(String url) throws IOException {
        return writeHtmlImagePreloadJavaScript(url);
    }

    /**
     * Prints a JavaScript script that will preload the image at the provided URL.
     */
    public ChainWriter writeHtmlImagePreloadJavaScript(String url) throws IOException {
        writeHtmlImagePreloadJavaScript(url, out);
        return this;
    }

    /**
     * Writes a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     *
     * Because this needs to modify the DOM it can lead to poor performance or large data sets.
     * To provide more performance options, the JavaScript is written to scriptOut.  This could
     * then be buffered into one long script to execute at once or using body.onload.
     *
     * The provided sequence should start at one for any given HTML page because parts of the
     * script will only be written when the sequence is equal to one.
     */
    public static void writeDateJavaScript(long date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
        if(date==-1) out.append("&#160;");
        else {
            String dateString = SQLUtility.getDate(date);
            long id = sequence.getNextSequenceValue();
            String idString = Long.toString(id);
            // Write the element
            out.append("<span id=\"chainWriterDate");
            out.append(idString);
            out.append("\">");
            EncodingUtils.encodeHtml(dateString, out);
            out.append("</span>");
            // Write the shared script only on first sequence
            if(id==1) {
                scriptOut.append("  function chainWriterUpdateDate(id, millis, serverValue) {\n"
                               + "    if(document.getElementById) {\n"
                               + "      var date=new Date(millis);\n"
                               + "      var clientValue=date.getFullYear() + \"-\";\n"
                               + "      var month=date.getMonth()+1;\n"
                               + "      if(month<10) clientValue+=\"0\";\n"
                               + "      clientValue+=month+\"-\";\n"
                               + "      var day=date.getDate();\n"
                               + "      if(day<10) clientValue+=\"0\";\n"
                               + "      clientValue+=day;\n"
                               + "      if(clientValue!=serverValue) document.getElementById(\"chainWriterDate\"+id).firstChild.nodeValue=clientValue;\n"
                               + "    }\n"
                               + "  }\n");
            }
            scriptOut.append("  chainWriterUpdateDate(");
            scriptOut.append(idString);
            scriptOut.append(", ");
            scriptOut.append(Long.toString(date));
            scriptOut.append(", \"");
            EncodingUtils.encodeHtml(dateString, scriptOut);
            scriptOut.append("\");\n");
        }
    }

    /**
     * Prints a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @deprecated
     * @see  #writeDateJavaScript(long)
     */
    @Deprecated
    final public ChainWriter printDateJS(long date, Sequence sequence, Appendable scriptOut) throws IOException {
        return writeDateJavaScript(date, sequence, scriptOut);
    }

    /**
     * Writes a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @see  #writeDateJavaScript(long,Appendable)
     */
    public ChainWriter writeDateJavaScript(long date, Sequence sequence, Appendable scriptOut) throws IOException {
        writeDateJavaScript(date, sequence, out, scriptOut);
        return this;
    }

    /**
     * Writes a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     *
     * Because this needs to modify the DOM it can lead to poor performance or large data sets.
     * To provide more performance options, the JavaScript is written to scriptOut.  This could
     * then be buffered into one long script to execute at once or using body.onload.
     *
     * The provided sequence should start at one for any given HTML page because parts of the
     * script will only be written when the sequence is equal to one.
     */
    public static void writeDateTimeJavaScript(long date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
        if(date==-1) out.append("&#160;");
        else {
            String dateTimeString = SQLUtility.getDateTime(date);
            long id = sequence.getNextSequenceValue();
            String idString = Long.toString(id);
            // Write the element
            out.append("<span id=\"chainWriterDateTime");
            out.append(idString);
            out.append("\">");
            EncodingUtils.encodeHtml(dateTimeString, out);
            out.append("</span>");
            // Write the shared script only on first sequence
            if(id==1) {
                scriptOut.append("  function chainWriterUpdateDateTime(id, millis, serverValue) {\n"
                               + "    if(document.getElementById) {\n"
                               + "      var date=new Date(millis);\n"
                               + "      var clientValue=date.getFullYear() + \"-\";\n"
                               + "      var month=date.getMonth()+1;\n"
                               + "      if(month<10) clientValue+=\"0\";\n"
                               + "      clientValue+=month+\"-\";\n"
                               + "      var day=date.getDate();\n"
                               + "      if(day<10) clientValue+=\"0\";\n"
                               + "      clientValue+=day+\" \";\n"
                               + "      var hour=date.getHours();\n"
                               + "      if(hour<10) clientValue+=\"0\";\n"
                               + "      clientValue+=hour+\":\";\n"
                               + "      var minute=date.getMinutes();\n"
                               + "      if(minute<10) clientValue+=\"0\";\n"
                               + "      clientValue+=minute+\":\";\n"
                               + "      var second=date.getSeconds();\n"
                               + "      if(second<10) clientValue+=\"0\";\n"
                               + "      clientValue+=second;\n"
                               + "      if(clientValue!=serverValue) document.getElementById(\"chainWriterDateTime\"+id).firstChild.nodeValue=clientValue;\n"
                               + "    }\n"
                               + "  }\n");
            }
            scriptOut.append("  chainWriterUpdateDateTime(");
            scriptOut.append(idString);
            scriptOut.append(", ");
            scriptOut.append(Long.toString(date));
            scriptOut.append(", \"");
            EncodingUtils.encodeHtml(dateTimeString, scriptOut);
            scriptOut.append("\");\n");
        }
    }

    /**
     * Writes a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @deprecated
     * @see #writeDateTimeJavaScript(long)
     */
    @Deprecated
    final public ChainWriter printDateTimeJS(long date, Sequence sequence, Appendable scriptOut) throws IOException {
        return writeDateTimeJavaScript(date, sequence, scriptOut);
    }

    /**
     * Writes a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     * @see #writeDateTimeJavaScript(long, Appendable)
     */
    public ChainWriter writeDateTimeJavaScript(long date, Sequence sequence, Appendable scriptOut) throws IOException {
        writeDateTimeJavaScript(date, sequence, out, scriptOut);
        return this;
    }

    /**
     * Writes a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     *
     * Because this needs to modify the DOM it can lead to poor performance or large data sets.
     * To provide more performance options, the JavaScript is written to scriptOut.  This could
     * then be buffered into one long script to execute at once or using body.onload.
     *
     * The provided sequence should start at one for any given HTML page because parts of the
     * script will only be written when the sequence is equal to one.
     */
    public static void writeTimeJavaScript(long date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
        if(date==-1) out.append("&#160;");
        else {
            String timeString = SQLUtility.getTime(date);
            long id = sequence.getNextSequenceValue();
            String idString = Long.toString(id);
            // Write the element
            out.append("<span id=\"chainWriterTime");
            out.append(idString);
            out.append("\">");
            EncodingUtils.encodeHtml(timeString, out);
            out.append("</span>");
            // Write the shared script only on first sequence
            if(id==1) {
                scriptOut.append("  function chainWriterUpdateTime(id, millis, serverValue) {\n"
                               + "    if(document.getElementById) {\n"
                               + "      var date=new Date(millis);\n"
                               + "      var hour=date.getHours();\n"
                               + "      var clientValue=(hour<10)?\"0\":\"\";\n"
                               + "      clientValue+=hour+\":\";\n"
                               + "      var minute=date.getMinutes();\n"
                               + "      if(minute<10) clientValue+=\"0\";\n"
                               + "      clientValue+=minute+\":\";\n"
                               + "      var second=date.getSeconds();\n"
                               + "      if(second<10) clientValue+=\"0\";\n"
                               + "      clientValue+=second;\n"
                               + "      if(clientValue!=serverValue) document.getElementById(\"chainWriterTime\"+id).firstChild.nodeValue=clientValue;\n"
                               + "    }\n"
                               + "  }\n");
            }
            scriptOut.append("  chainWriterUpdateTime(");
            scriptOut.append(idString);
            scriptOut.append(", ");
            scriptOut.append(Long.toString(date));
            scriptOut.append(", \"");
            EncodingUtils.encodeHtml(timeString, scriptOut);
            scriptOut.append("\");\n");
        }
    }

    /**
     * Writes a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @deprecated
     * @see #writeTimeJavaScript(long)
     */
    @Deprecated
    final public ChainWriter printTimeJS(long date, Sequence sequence, Appendable scriptOut) throws IOException {
        return writeTimeJavaScript(date, sequence, scriptOut);
    }

    /**
     * Writes a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;#160;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @see #writeTimeJavaScript(long,Appendable)
     */
    public ChainWriter writeTimeJavaScript(long date, Sequence sequence, Appendable scriptOut) throws IOException {
        writeTimeJavaScript(date, sequence, out, scriptOut);
        return this;
    }
    // </editor-fold>
}
