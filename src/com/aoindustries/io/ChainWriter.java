package com.aoindustries.io;

/*
 * Copyright 2000-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.BufferManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A chain writer encapsulates a <code>PrintWriter</code> and returns the <code>ChainWriter</code>
 * instance on most methods.  This gives the ability to call code like
 * <code>out.print("Hi ").print(name).print('!');</code>
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
final public class ChainWriter {

    private static final char[] hexChars={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Escapes for use in a HTML attribute and writes to the provided <code>Writer</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     */
    public static void writeHtmlAttribute(String S, Writer out) throws IOException {
        if (S != null) {
            int len = S.length();
            int toPrint = 0;
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                switch(ch) {
                    case '\r':
                        break;
                    case '\n':
                        if(toPrint>0) {
                            out.write(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.write(' ');
                        break;
                    case '<':
                        if(toPrint>0) {
                            out.write(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.write("&#60;");
                        break;
                    case '>':
                        if(toPrint>0) {
                            out.write(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.write("&#62;");
                        break;
                    case '&':
                        if(toPrint>0) {
                            out.write(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.write("&#38;");
                        break;
                    case '"':
                        if(toPrint>0) {
                            out.write(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.write("&#34;");
                        break;
                    case '\'':
                        if(toPrint>0) {
                            out.write(S, c-toPrint, toPrint);
                            toPrint=0;
                        }
                        out.write("&#39;");
                        break;
                    default:
                        toPrint++;
                }
            }
            if(toPrint>0) {
                out.write(S, len-toPrint, toPrint);
            }
        }
    }

    /**
     * Escapes for use in a HTML document and writes to the provided <code>Writer</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     */
    public static void writeHtml(String S, Writer out) throws IOException {
        writeHtml(S, true, true, out);
    }

    /**
     * Escapes for use in a HTML document and writes to the provided <code>Writer</code>.
     *
     * @param S the string to be escaped.  If S is <code>null</code>, nothing is written.
     * @param make_br  will write &lt;BR&gt; tags for every newline character
     * @param make_nbsp  will write &amp;nbsp; for a space when another space follows
     */
    public static void writeHtml(String S, boolean make_br, boolean make_nbsp, Writer out) throws IOException {
        if (S != null) {
            try {
                int len = S.length();
                int toPrint = 0;
                for (int c = 0; c < len; c++) {
                    char ch = S.charAt(c);
                    switch(ch) {
                        case ' ':
                            if(make_nbsp && c<(len-1) && S.charAt(c+1)==' ') {
                                if(toPrint>0) {
                                    out.write(S, c-toPrint, toPrint);
                                    toPrint=0;
                                }
                                out.write("&nbsp;");
                            } else {
                                toPrint++;
                            }
                            break;
                        case '<':
                            if(toPrint>0) {
                                out.write(S, c-toPrint, toPrint);
                                toPrint=0;
                            }
                            out.write("&#60;");
                            break;
                        case '&':
                            if(toPrint>0) {
                                out.write(S, c-toPrint, toPrint);
                                toPrint=0;
                            }
                            out.write("&#38;");
                            break;
                        case '"':
                            if(toPrint>0) {
                                out.write(S, c-toPrint, toPrint);
                                toPrint=0;
                            }
                            out.write("&#34;");
                            break;
                        case '\'':
                            if(toPrint>0) {
                                out.write(S, c-toPrint, toPrint);
                                toPrint=0;
                            }
                            out.write("&#39;");
                            break;
                        case '\r':
                            if(toPrint>0) {
                                out.write(S, c-toPrint, toPrint);
                                toPrint=0;
                            }
                            // skip '\r'
                            break;
                        case '\n':
                            if(make_br) {
                                if(toPrint>0) {
                                    out.write(S, c-toPrint, toPrint);
                                    toPrint=0;
                                }
                                out.write("<BR>\n");
                            } else {
                                toPrint++;
                            }
                            break;
                        default:
                            toPrint++;
                    }
                }
                if(toPrint>0) {
                    out.write(S, len-toPrint, toPrint);
                }
            } catch(StringIndexOutOfBoundsException err) {
                System.err.println("ERROR: ChainWriter: writeHtml: S=\""+S+"\"");
                throw err;
            }
        }
    }

    /**
     * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
     */
    public static void writeHtmlColor(int color, Writer out) throws IOException {
        out.write('#');
        out.write(hexChars[(color>>>20)&15]);
        out.write(hexChars[(color>>>16)&15]);
        out.write(hexChars[(color>>>12)&15]);
        out.write(hexChars[(color>>>8)&15]);
        out.write(hexChars[(color>>>4)&15]);
        out.write(hexChars[color&15]);
    }

    /**
     * Prints a JavaScript script that will preload the image at the provided URL.
     */
    public static void writeHtmlImagePreloadJavaScript(String url, Writer out) throws IOException {
        out.write("<SCRIPT language='JavaScript1.2'><!--\n"
                + "  var img=new Image();\n"
                + "  img.src='");
        writeHtmlAttribute(url, out);
        out.write("';\n"
                + "  // --></SCRIPT>");
    }

    private final PrintWriter out;
    private boolean error = false;

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

    /**
     * Flush the stream and check its error state.  Errors are cumulative;
     * once the stream encounters an error, this routine will return true on
     * all successive calls.
     *
     * @return True if the print stream has encountered an error, either on the
     * underlying output stream or during a format conversion.
     */
    public boolean checkError() {
        return error || out.checkError();
    }

    /** Close the stream. */
    public ChainWriter close() {
        out.close();
        return this;
    }

    /** Flush the stream. */
    public ChainWriter flush() {
        out.flush();
        return this;
    }

    private static char getHex(int value) {
        return hexChars[value & 15];
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    /**
     * Prints a <code>byte[]</code> to a <code>ChainWriter</code>.  The characters
     * are assumed to be ASCII.
     * 
     * @deprecated  use OutputStreamWriter because this method doesn't do any decoding of the byte[]
     */
    @Deprecated
    public ChainWriter print(byte[] bytes) {
        int len=bytes.length;
        int pos=0;
        char[] buffer=BufferManager.getChars();
        try {
            while(pos<len) {
                int blockSize=len-pos;
                if(blockSize>BufferManager.BUFFER_SIZE) blockSize=BufferManager.BUFFER_SIZE;
                for(int c=0;c<blockSize;c++) buffer[c]=(char)bytes[pos++];
                out.write(buffer, 0, blockSize);
            }
        } finally {
            BufferManager.release(buffer);
        }
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

    /* Methods that do not terminate lines */

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
     * Print a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     */
    public ChainWriter printDateJS(long date) {
        if(date==-1) out.print("&nbsp;");
        else {
            out.print("<SCRIPT language='JavaScript1.3'><!--\n"
                      +"  var date=new Date(");
            out.print(date);
            out.print(");\n"
                    + "  document.write(date.getFullYear());\n"
                    + "  document.write('-');\n"
                    + "  var month=date.getMonth()+1;\n"
                    + "  if(month<10) document.write('0');\n"
                    + "  document.write(month);\n"
                    + "  document.write('-');\n"
                    + "  var day=date.getDate();\n"
                    + "  if(day<10) document.write('0');\n"
                    + "  document.write(day);\n"
                    + "// --></SCRIPT>");
        }
        return this;
    }

    /**
     * Print a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     */
    public ChainWriter printDateTimeJS(long date) {
        if(date==-1) out.print("&nbsp;");
        else {
            out.print("<SCRIPT language='JavaScript1.1'><!--\n"
                    + "  var date=new Date(");
            out.print(date);
            out.print(");\n"
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
                    + "// --></SCRIPT>");
        }
        return this;
    }

    /**
     * @deprecated  Please use writeHtml instead.
     */
    public ChainWriter printEH(String S) {
        try {
            writeHtml(S, true, true, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }
    
    /**
     * @deprecated  Please use writeHtml instead.
     */
    public ChainWriter printEH(String S, boolean make_br, boolean make_nbsp) {
        try {
            writeHtml(S, make_br, make_nbsp, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }

    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter writeHtml(String S) {
        try {
            writeHtml(S, true, true, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }
    
    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     * @param make_br  will write &lt;BR&gt; tags for every newline character
     */
    public ChainWriter writeHtml(String S, boolean make_br, boolean make_nbsp) {
        try {
            writeHtml(S, make_br, make_nbsp, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }

    /**
     * @deprecated  Please use writeHtmlAttribute instead.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter printEI(String S) {
        try {
            writeHtmlAttribute(S, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }

    /**
     * Escapes for use in a HTML attribute and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter writeHtmlAttribute(String S) {
        try {
            writeHtmlAttribute(S, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }

    /**
     * Escapes the specified <code>String</code> so that it can be put in a JavaScript string.
     * Writes to the provided <code>Writer</code>.
     *
     * @param S the string to be escaped.
     */
    public static void printEJ(String S, Writer out) throws IOException {
        if (S != null) {
            int len = S.length();
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                if (ch == '"') out.write("\\\"");
                else if (ch == '\'') out.write("\\'");
                else if (ch == '\n') out.write("\\n");
                else if (ch == '\t') out.write("\\t");
                else out.write(ch);
            }
        }
    }

    /**
     * Escapes the specified <code>String</code> so that it can be put in a JavaScript string.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter printEJ(String S) throws IOException {
        printEJ(S, out);
        return this;
    }

    /**
     * Prints a value that may be placed in a URL.
     */
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

    /**
     * Escapes XML attribute and writes to the internal <code>PrintWriter</code>, this is parsed data.
     *
     * @param S the string to be escaped
     */
    public ChainWriter printXmlAttribute(String S) {
        if (S != null) {
            int len = S.length();
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                if(ch=='"') out.print("&quot;");
                else if(ch=='\'') out.print("&apos;");
                else if(ch=='&') out.print("&amp;");
                else if(ch=='<') out.print("&lt;");
                else if(ch=='>') out.print("&gt;");
                else out.print(ch);
            }
        }
        return this;
    }

    /**
     * Escapes XML body and writes to the internal <code>PrintWriter</code>, this is unparsed data.
     *
     * @param S the string to be escaped
     */
    public ChainWriter printXmlBody(String S) {
        if (S != null) {
            int len = S.length();
            for (int c = 0; c < len; c++) {
                char ch = S.charAt(c);
                if(ch=='"') out.print("\\\"");
                else if(ch=='\'') out.print("&apos;");
                else if(ch=='&') out.print("&amp;");
                else if(ch=='<') out.print("&lt;");
                else if(ch=='>') out.print("&gt;");
                else if(ch=='\\') out.print("\\\\");
                else out.print(ch);
            }
        }
        return this;
    }

    /**
     * @deprecated  Please use writeHtmlImagePreloadJavaScript instead.
     */
    public ChainWriter printImagePreloadJS(String url) {
        try {
            writeHtmlImagePreloadJavaScript(url, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }

    /**
     * Prints a JavaScript script that will preload the image at the provided URL.
     */
    public ChainWriter writeHtmlImagePreloadJavaScript(String url) {
        try {
            writeHtmlImagePreloadJavaScript(url, out);
        } catch(IOException err) {
            error = true;
        }
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
     * Print an array of characters and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(char[])}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(char x[]) {
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
     * Print a double-precision floating-point number and then terminate the
     * line.  This method behaves as though it invokes <code>{@link
     * #print(double)}</code> and then <code>{@link #println()}</code>.
     */
    public ChainWriter println(double x) {
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
     * Print an Object and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(Object)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(Object x) {
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
     * Print a boolean value and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(boolean x) {
        out.println(x);
        return this;
    }

    /**
     * Print a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     */
    public ChainWriter printTimeJS(long date) {
        if(date==-1) out.print("&nbsp;");
        else {
            out.print("<SCRIPT language='JavaScript1.3'><!--\n"
                    + "  var date=new Date(");
            out.print(date);
            out.print(");\n"
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
                    + "// --></SCRIPT>");
        }
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

    /** Write a portion of an array of characters. */
    public ChainWriter write(char buf[], int off, int len) {
        out.write(buf, off, len);
        return this;
    }

    /*
     * Exception-catching, synchronized output operations,
     * which also implement the write() methods of Writer
     */

    /** Write a single character. */
    public ChainWriter write(int c) {
        out.write(c);
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

    /** Write a portion of a string. */
    public ChainWriter write(String s, int off, int len) {
        out.write(s, off, len);
        return this;
    }
    
    /**
     * @deprecated  Please use writeHtmlColor instead.
     */
    public ChainWriter printHTMLColor(int color) {
        try {
            writeHtmlColor(color, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }

    /**
     * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
     */
    public ChainWriter writeHtmlColor(int color) {
        try {
            writeHtmlColor(color, out);
        } catch(IOException err) {
            error = true;
        }
        return this;
    }
}
