package com.aoindustries.io;

/*
 * Copyright 2000-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;

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

    private final PrintWriter out;

    private static final char[] hexChars={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

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
        Profiler.startProfile(Profiler.INSTANTANEOUS, ChainWriter.class, "<init>(OutputStream)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
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
        Profiler.startProfile(Profiler.INSTANTANEOUS, ChainWriter.class, "<init>(OutputStream,boolean)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
    }

    public ChainWriter(PrintWriter out) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ChainWriter.class, "<init>(PrintWriter)", null);
        try {
            this.out=out;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Create a new PrintWriter, without automatic line flushing.
     *
     * @param  out        A character-output stream
     */
    public ChainWriter(Writer out) {
	this(new PrintWriter(out));
        Profiler.startProfile(Profiler.INSTANTANEOUS, ChainWriter.class, "<init>(Writer)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
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
        Profiler.startProfile(Profiler.INSTANTANEOUS, ChainWriter.class, "<init>(Writer,boolean)", null);
        Profiler.endProfile(Profiler.INSTANTANEOUS);
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "checkError()", null);
        try {
            return out.checkError();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /** Close the stream. */
    public ChainWriter close() {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "close()", null);
        try {
            out.close();
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /** Flush the stream. */
    public ChainWriter flush() {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "flush()", null);
        try {
            out.flush();
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    private static char getHex(int value) {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ChainWriter.class, "getHex(int)", null);
        try {
            return hexChars[value & 15];
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public PrintWriter getPrintWriter() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, ChainWriter.class, "getPrintWriter()", null);
        try {
            return out;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Prints a <code>byte[]</code> to a <code>ChainWriter</code>.
     */
    public ChainWriter print(byte[] bytes) throws IOException {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(byte[])", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(char[])", null);
        try {
            out.print(s);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(char)", null);
        try {
            out.print(c);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(double)", null);
        try {
            out.print(d);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(float)", null);
        try {
            out.print(f);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(int)", null);
        try {
            out.print(i);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(long)", null);
        try {
            out.print(l);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(Object)", null);
        try {
            out.print(obj);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(String)", null);
        try {
            out.print(s);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
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
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "print(boolean)", null);
        try {
            out.print(b);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a JavaScript script tag that shows a date in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     */
    public ChainWriter printDateJS(long date) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printDateJS(long)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a JavaScript script tag that shows a date and time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     */
    public ChainWriter printDateTimeJS(long date) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printDateTimeJS(long)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter printEH(String S) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printEH(String)", null);
        try {
            return printEH(S, true, true);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     * @param make_br  will write &lt;BR&gt; tags for every newline character
     */
    public ChainWriter printEH(String S, boolean make_br, boolean make_nbsp) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printEH(String,boolean,boolean)", null);
        try {
            if (S != null) {
                int len = S.length();
                for (int c = 0; c < len; c++) {
                    char ch = S.charAt(c);
                    if(ch==' ') {
                        if(make_nbsp && c<(len-1) && S.charAt(c+1)==' ') out.print("&nbsp;");
                        else out.print(' ');
                    } else if (ch == '<') out.print("&#60;");
                    else if (ch == '&') out.print("&#38;");
                    else if (ch == '"') out.print("&#34;");
                    else if (ch == '\'') out.print("&#39;");
                    else if (ch == '\r'); // skip '\r'
                    else if (ch == '\n') {
                        if(make_br) out.print("<BR>");
                        out.print('\n');
                    } else out.print(ch);
                }
            }
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Escapes HTML for displaying in browsers and writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter printEI(String S) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printEI(String)", null);
        try {
            if (S != null) {
                int len = S.length();
                for (int c = 0; c < len; c++) {
                    char ch = S.charAt(c);
                    if (ch == '<') out.print("&#60;");
                    else if (ch == '&') out.print("&#38;");
                    else if (ch == '"') out.print("&#34;");
                    else if (ch == '\'') out.print("&#39;");
                    else out.print(ch);
                }
            }
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Escapes the specified <code>String</code> so that it can be put in a JavaScript string.
     * Writes to the internal <code>PrintWriter</code>.
     *
     * @param S the string to be escaped.
     */
    public ChainWriter printEJ(String S) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printEJ(String)", null);
        try {
            if (S != null) {
                int len = S.length();
                for (int c = 0; c < len; c++) {
                    char ch = S.charAt(c);
                    if (ch == '"') out.print("\\\"");
                    else if (ch == '\'') out.print("\\'");
                    else if (ch == '\n') out.print("\\n");
                    else if (ch == '\t') out.print("\\t");
                    else out.print(ch);
                }
            }
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Prints a value that may be placed in a URL.
     */
    public ChainWriter printEU(String value) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printEU(String)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Escapes XML attribute and writes to the internal <code>PrintWriter</code>, this is parsed data.
     *
     * @param S the string to be escaped
     */
    public ChainWriter printXmlAttribute(String S) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printXmlAttribute(String)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Escapes XML body and writes to the internal <code>PrintWriter</code>, this is unparsed data.
     *
     * @param S the string to be escaped
     */
    public ChainWriter printXmlBody(String S) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printXmlBody(String)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Prints a JavaScript script that will preload the image at the provided URL.
     */
    public ChainWriter printImagePreloadJS(String url) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printImagePreloadJS(String)", null);
        try {
            out.print("<SCRIPT language='JavaScript1.2'><!--\n"
                    + "  var img=new Image();\n"
                    + "  img.src='");
            printEI(url);
            out.print("';\n"
                    + "  // --></SCRIPT>");
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Terminate the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public ChainWriter println() {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println()", null);
        try {
            out.println();
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print an array of characters and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(char[])}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(char x[]) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(char[])", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a character and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
     * #println()}</code>.
     */
    public ChainWriter println(char x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(char)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a double-precision floating-point number and then terminate the
     * line.  This method behaves as though it invokes <code>{@link
     * #print(double)}</code> and then <code>{@link #println()}</code>.
     */
    public ChainWriter println(double x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(double)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a floating-point number and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(float)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(float x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(float)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print an integer and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
     * #println()}</code>.
     */
    public ChainWriter println(int x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(int)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a long integer and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(long)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(long x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(long)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print an Object and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(Object)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(Object x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(Object)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a String and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(String x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(String)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a boolean value and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public ChainWriter println(boolean x) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "println(boolean)", null);
        try {
            out.println(x);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Print a JavaScript script tag that a time in the user's locale.  Prints <code>&amp;nbsp;</code>
     * if the date is <code>-1</code>.
     * Writes to the internal <code>PrintWriter</code>.
     */
    public ChainWriter printTimeJS(long date) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printTimeJS(long)", null);
        try {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Write an array of characters.  This method cannot be inherited from the
     * Writer class because it must suppress I/O exceptions.
     */
    public ChainWriter write(char buf[]) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "write(char[])", null);
        try {
            out.write(buf);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /** Write a portion of an array of characters. */
    public ChainWriter write(char buf[], int off, int len) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "write(char[],int,int)", null);
        try {
            out.write(buf, off, len);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /*
     * Exception-catching, synchronized output operations,
     * which also implement the write() methods of Writer
     */

    /** Write a single character. */
    public ChainWriter write(int c) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "write(int)", null);
        try {
            out.write(c);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Write a string.  This method cannot be inherited from the Writer class
     * because it must suppress I/O exceptions.
     */
    public ChainWriter write(String s) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "write(String)", null);
        try {
            out.write(s);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /** Write a portion of a string. */
    public ChainWriter write(String s, int off, int len) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "write(String,int,int)", null);
        try {
            out.write(s, off, len);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    /**
     * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
     */
    public ChainWriter printHTMLColor(int color) {
        Profiler.startProfile(Profiler.IO, ChainWriter.class, "printHTMLColor(int)", null);
        try {
            out.write('#');
            out.write(hexChars[(color>>>20)&15]);
            out.write(hexChars[(color>>>16)&15]);
            out.write(hexChars[(color>>>12)&15]);
            out.write(hexChars[(color>>>8)&15]);
            out.write(hexChars[(color>>>4)&15]);
            out.write(hexChars[color&15]);
            return this;
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
