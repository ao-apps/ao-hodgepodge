package com.aoindustries.io;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.SQLUtility;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * A chain writer encapsulates a <code>PrintWriter</code> and returns the <code>ChainWriter</code>
 * instance on most methods.  This gives the ability to call code like
 * <code>out.print("Hi ").print(name).print('!');</code>
 *
 * @version  1.0
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

    public ChainWriter append(CharSequence csq) {
        out.append(csq);
        return this;
    }

    public ChainWriter append(CharSequence csq, int start, int end) {
        out.append(csq, start, end);
        return this;
    }

    public ChainWriter append(char c) {
        out.append(c);
        return this;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Escape Methods">
    /**
     * Escapes for use in a XML attribute and writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     *
     * @see  #writeXml(java.lang.String, Appendable)
     */
    public static void writeXmlAttribute(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case '&':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&amp;");
                        break;
                    case '<':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&lt;");
                        break;
                    case '>':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&gt;");
                        break;
                    case '\r':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&#xD;");
                        break;
                    case '\t':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&#x9;");
                        break;
                    case '\n':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&#xA;");
                        break;
                    case '"':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&quot;");
                        break;
                    case '\'':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&apos;");
                        break;
                    default:
                        toPrint++;
                }
            }
            if(toPrint>0) {
                out.append(S, len-toPrint, toPrint);
            }
        }
    }

    /**
     * @see #writeXmlAttribute(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String escapeXmlAttribute(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        writeXmlAttribute(S, result);
        return result.toString();
    }

    /**
     * @deprecated  Please use writeXmlAttribute instead.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter printEI(String S) throws IOException {
        return writeXmlAttribute(S);
    }

    /**
     * @see  #writeXmlAttribute(String, Appendable)
     *
     * @param S the string to be escaped
     */
    public ChainWriter writeXmlAttribute(String S) throws IOException {
        writeXmlAttribute(S, out);
        return this;
    }

    /**
     * Escapes for use in a XML body and writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     *
     * @see  #writeXmlAttribute(java.lang.String, Appendable)
     */
    public static void writeXml(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case '&':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&amp;");
                        break;
                    case '<':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&lt;");
                        break;
                    case '>':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&gt;");
                        break;
                    case '\t':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&#x9;");
                        break;
                    default:
                        toPrint++;
                }
            }
            if(toPrint>0) {
                out.append(S, len-toPrint, toPrint);
            }
        }
    }

    /**
     * @see #writeXml(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String escapeXml(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        writeXml(S, result);
        return result.toString();
    }

    /**
     * @see  #writeXml(String, Appendable)
     *
     * @param S the string to be escaped
     */
    public ChainWriter writeXml(String S) throws IOException {
        writeXml(S, out);
        return this;
    }

    /**
     * Escapes for use in a HTML document and writes to the provided <code>Appendable</code>.
     * It turns newlines into &lt;br /&gt; and extra spaces to &amp;nbsp;
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     */
    public static void writeHtml(String S, Appendable out) throws IOException {
        writeHtml(S, true, true, out);
    }

    /**
     * @see #writeHtml(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String escapeHtml(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        writeHtml(S, result);
        return result.toString();
    }

    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter writeHtml(String S) throws IOException {
        writeHtml(S, true, true, out);
        return this;
    }

    /**
     * Escapes for use in a HTML document and writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     * @param make_br  will write &lt;BR&gt; tags for every newline character
     * @param make_nbsp  will write &amp;nbsp; for a space when another space follows
     */
    public static void writeHtml(String S, boolean make_br, boolean make_nbsp, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case ' ':
                        if(make_nbsp && c<(len-1) && S.charAt(c+1)==' ') {
                            if(toPrint>0) {
                                out.append(S, c-toPrint, toPrint);
                                toPrint=0;
                            }
                            out.append("&nbsp;");
                        } else {
                            toPrint++;
                        }
                        break;
                    case '<':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&lt;");
                        break;
                    case '>':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&gt;");
                        break;
                    case '&':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&amp;");
                        break;
                    case '"':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&quot;");
                        break;
                    case '\'':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.append("&apos;");
                        break;
                    case '\r':
                        if(toPrint>0) {
                            out.append(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        // skip '\r'
                        break;
                    case '\n':
                        if(make_br) {
                            if(toPrint>0) {
                                out.append(S, c-toPrint, toPrint);
                                toPrint=0;
                            }
                            out.append("<br />\n");
                        } else {
                            toPrint++;
                        }
                        break;
                    default:
                        toPrint++;
                }
            }
            if(toPrint>0) {
                out.append(S, len-toPrint, toPrint);
            }
        }
    }

    /**
     * @see #writeHtml(java.lang.String, boolean, boolean, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String escapeHtml(String S, boolean make_br, boolean make_nbsp) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        writeHtml(S, make_br, make_nbsp, result);
        return result.toString();
    }

    /**
     * @deprecated  Please use writeHtml instead.
     */
    public ChainWriter printEH(String S, boolean make_br, boolean make_nbsp) throws IOException {
        return writeHtml(S, make_br, make_nbsp);
    }

    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     * @param make_br  will write &lt;BR&gt; tags for every newline character
     */
    public ChainWriter writeHtml(String S, boolean make_br, boolean make_nbsp) throws IOException {
        writeHtml(S, make_br, make_nbsp, out);
        return this;
    }

    /**
     * @deprecated  Please use writeHtml instead.
     */
    public ChainWriter printEH(String S) throws IOException {
        writeHtml(S, true, true, out);
        return this;
    }

    /**
     * Escapes the specified <code>String</code> so that it can be put in a JavaScript string in
     * a XML CDATA area.  The string should be in double quotes, the quotes are not provided by
     * this call.
     *
     * Writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.
     */
    public static void writeJavaScriptString(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                if (ch == '"') out.append("\\\"");
                else if (ch == '\'') out.append("\\'");
                else if (ch == '\b') out.append("\\b");
                else if (ch == '\f') out.append("\\f");
                else if (ch == '\r') out.append("\\r");
                else if (ch == '\n') out.append("\\n");
                else if (ch == '\t') out.append("\\t");
                // Also escape any < > & and ] to avoid early end of XHTML CDATA sections
                // and to not allow data to interfere with the XML parser.
                else if (ch == '<') out.append("\\u003c");
                else if (ch == '>') out.append("\\u003e");
                else if (ch == '&') out.append("\\u0026");
                else if (ch == ']') out.append("\\u005d");
                else if (ch<' ') {
                    out.append("\\u00");
                    int chInt = ch;
                    out.append(getHex((chInt>>>4)&15));
                    out.append(getHex(chInt&15));
                } else {
                    out.append(ch);
                }
            }
        }
    }

    /**
     * @see #writeJavaScriptString(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped
     */
    public static String escapeJavaScriptString(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        writeJavaScriptString(S, result);
        return result.toString();
    }

    /**
     * @see #writeJavaScriptString(java.lang.String, Appendable)
     */
    public ChainWriter writeJavaScriptString(String S) throws IOException {
        writeJavaScriptString(S, out);
        return this;
    }

    /**
     * Escapes the specified <code>String</code> so that it can be put in a JavaScript string in
     * an HTML attribute.  The string should be in JavaScript quotes, the quotes
     * are not provided by this call.
     *
     * Writes to the provided <code>Appendable</code>.
     *
     * @param S the string to be escaped.
     */
    public static void writeJavaScriptStringInXmlAttribute(String S, Appendable out) throws IOException {
        if (S != null) {
            int len = S.length();
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                if (ch == '"') out.append("\\&quot;");
                else if (ch == '\'') out.append("\\&apos;");
                else if (ch == '\b') out.append("\\b");
                else if (ch == '\f') out.append("\\f");
                else if (ch == '\r') out.append("\\r");
                else if (ch == '\n') out.append("\\n");
                else if (ch == '\t') out.append("\\t");
                // Also escape any < > & and ] to avoid early end of XHTML CDATA sections
                // and to not allow data to interfere with the XML parser.
                else if (ch == '<') out.append("\\u003c");
                else if (ch == '>') out.append("\\u003e");
                else if (ch == '&') out.append("\\u0026");
                else if (ch == ']') out.append("\\u005d");
                else if (ch<' ') {
                    out.append("\\u00");
                    int chInt = ch;
                    out.append(getHex((chInt>>>4)&15));
                    out.append(getHex(chInt&15));
                } else {
                    out.append(ch);
                }
            }
        }
    }

    /**
     * @see #writeJavaScriptStringInXmlAttribute(java.lang.String, Appendable)
     *
     * @param S the string to be escaped.
     *
     * @return if S is null then null otherwise value escaped to be a JavaScript string in a XML attribute.
     */
    public static String encodeJavaScriptStringInXmlAttribute(String S) throws IOException {
        if(S==null) return null;
        StringBuilder result = new StringBuilder(S.length()*2);
        writeJavaScriptStringInXmlAttribute(S, result);
        return result.toString();
    }

    /**
     * @see #writeJavaScriptStringInXmlAttribute(java.lang.String, Appendable)
     */
    public ChainWriter writeJavaScriptStringInXmlAttribute(String S) throws IOException {
        writeJavaScriptStringInXmlAttribute(S, out);
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
        out.append(hexChars[(color>>>20)&15]);
        out.append(hexChars[(color>>>16)&15]);
        out.append(hexChars[(color>>>12)&15]);
        out.append(hexChars[(color>>>8)&15]);
        out.append(hexChars[(color>>>4)&15]);
        out.append(hexChars[color&15]);
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
     * @param url This should be the URL-encoded URL, but with &amp; as parameter separator
     *             (not &amp;amp;)
     */
    public static void writeHtmlImagePreloadJavaScript(String url, Appendable out) throws IOException {
        out.append("<script type='text/javascript'>\n"
                + "  // <![CDATA[\n"
                + "  var img=new Image();\n"
                + "  img.src='");
        writeJavaScriptString(url, out);
        out.append("';\n"
                + "  // ]]>\n"
                + "</script><noscript><!-- Do nothing --></noscript>");
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

    // <editor-fold defaultstate="collapsed" desc="JavaScript Date/Time functions">
    /**
     * Writes a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     */
    public static void writeDateJavaScript(long date, Appendable out) throws IOException {
        if(date==-1) out.append("&nbsp;");
        else {
            out.append("<script type='text/javascript'>\n"
                    + "  // <![CDATA[\n"
                    + "  var date=new Date(");
            out.append(Long.toString(date));
            out.append(");\n"
                    + "  document.write(date.getFullYear());\n"
                    + "  document.write('-');\n"
                    + "  var month=date.getMonth()+1;\n"
                    + "  if(month<10) document.write('0');\n"
                    + "  document.write(month);\n"
                    + "  document.write('-');\n"
                    + "  var day=date.getDate();\n"
                    + "  if(day<10) document.write('0');\n"
                    + "  document.write(day);\n"
                    + "  // ]]>\n"
                    + "</script><noscript>");
            writeHtml(SQLUtility.getDate(date), out);
            out.append("</noscript>");
        }
    }

    /**
     * Prints a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @deprecated
     * @see  #writeDateJavaScript(long)
     */
    @Deprecated
    final public ChainWriter printDateJS(long date) throws IOException {
        return writeDateJavaScript(date);
    }

    /**
     * Writes a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @see  #writeDateJavaScript(long,Appendable)
     */
    public ChainWriter writeDateJavaScript(long date) throws IOException {
        writeDateJavaScript(date, out);
        return this;
    }

    /**
     * Writes a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     */
    public static void writeDateTimeJavaScript(long date, Appendable out) throws IOException {
        if(date==-1) out.append("&nbsp;");
        else {
            out.append("<script type='text/javascript'>\n"
                    + "  // <![CDATA[\n"
                    + "  var date=new Date(");
            out.append(Long.toString(date));
            out.append(");\n"
                    + "  document.write(date.getFullYear());\n"
                    + "  document.write('-');\n"
                    + "  var month=date.getMonth()+1;\n"
                    + "  if(month<10) document.write('0');\n"
                    + "  document.write(month);\n"
                    + "  document.write('-');\n"
                    + "  var day=date.getDate();\n"
                    + "  if(day<10) document.write('0');\n"
                    + "  document.write(day);\n"
                    + "  document.write(' ');\n"
                    + "  var hour=date.getHours();\n"
                    + "  if(hour<10) document.write('0');\n"
                    + "  document.write(hour);\n"
                    + "  document.write(':');\n"
                    + "  var minute=date.getMinutes();\n"
                    + "  if(minute<10) document.write('0');\n"
                    + "  document.write(minute);\n"
                    + "  document.write(':');\n"
                    + "  var second=date.getSeconds();\n"
                    + "  if(second<10) document.write('0');\n"
                    + "  document.write(second);\n"
                    + "  // ]]>\n"
                    + "</script><noscript>");
            writeHtml(SQLUtility.getDateTime(date), out);
            out.append("</noscript>");
        }
    }

    /**
     * Writes a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @deprecated
     * @see #writeDateTimeJavaScript(long)
     */
    @Deprecated
    final public ChainWriter printDateTimeJS(long date) throws IOException {
        return writeDateTimeJavaScript(date);
    }

    /**
     * Writes a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     * @see #writeDateTimeJavaScript(long, Appendable)
     */
    public ChainWriter writeDateTimeJavaScript(long date) throws IOException {
        writeDateTimeJavaScript(date, out);
        return this;
    }

    /**
     * Writes a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     */
    public static void writeTimeJavaScript(long date, Appendable out) throws IOException {
        if(date==-1) out.append("&nbsp;");
        else {
            out.append("<script type='text/javascript'>\n"
                    + "  // <![CDATA[\n"
                    + "  var date=new Date(");
            out.append(Long.toString(date));
            out.append(");\n"
                    + "  var hour=date.getHours();\n"
                    + "  if(hour<10) document.write('0');\n"
                    + "  document.write(hour);\n"
                    + "  document.write(':');\n"
                    + "  var minute=date.getMinutes();\n"
                    + "  if(minute<10) document.write('0');\n"
                    + "  document.write(minute);\n"
                    + "  document.write(':');\n"
                    + "  var second=date.getSeconds();\n"
                    + "  if(second<10) document.write('0');\n"
                    + "  document.write(second);\n"
                    + "  // ]]>\n"
                    + "</script><noscript>");
            writeHtml(SQLUtility.getTime(date), out);
            out.append("</noscript>");
        }
    }

    /**
     * Writes a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @deprecated
     * @see #writeTimeJavaScript(long)
     */
    @Deprecated
    final public ChainWriter printTimeJS(long date) throws IOException {
        return writeTimeJavaScript(date);
    }

    /**
     * Writes a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @see #writeTimeJavaScript(long,Appendable)
     */
    public ChainWriter writeTimeJavaScript(long date) throws IOException {
        writeTimeJavaScript(date, out);
        return this;
    }
    // </editor-fold>
    // </editor-fold>
}
