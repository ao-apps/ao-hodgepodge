package com.aoindustries.servlet.filter;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

import java.io.PrintWriter;
import java.util.Locale;

/**
 * Filters the output and removes extra white space at the beginning of lines and completely removes blank lines.
 * TEXTAREAs are automatically detected as long as they start with exact "&lt;textarea" and end with exactly "&lt;/textarea" (case insensitive).
 * The reason for the specific tag format is to simplify the implementation
 * for maximum performance.  Careful attention has been paid to minimize the internal buffering in this class.  As many write/print operations as possible
 * are passed directly to the wrapped <code>PrintWriter</code>.  Please note that these methods are not synchronized, as servlet output is normally written
 * by the thread allocated for the request.  If synchronization is required it should be provided externally.
 *
 * TODO: Don't trim inside PRE tags.
 * 
 * @author  AO Industries, Inc.
 */
public class TrimFilterWriter extends PrintWriter {

    private static String lineSeparator = System.getProperty("line.separator");

    private PrintWriter wrapped;
    boolean inTextArea = false;
    private boolean atLineStart = true;

    private int readCharMatchCount = 0;
    private static final int OUPUT_BUFFER_SIZE=4096;
    private char[] outputBuffer = new char[OUPUT_BUFFER_SIZE];
    private int outputBufferUsed = 0;

    public TrimFilterWriter(PrintWriter wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    public void flush() {
        wrapped.flush();
    }
    
    public void close() {
        wrapped.close();
        readCharMatchCount = 0;
        inTextArea = false;
        atLineStart = true;
    }

    public boolean checkError() {
        return wrapped.checkError();
    }

    static final char[] textarea = {'<', 't', 'e', 'x', 't', 'a', 'r', 'e', 'a'};
    static final char[] TEXTAREA = {'<', 'T', 'E', 'X', 'T', 'A', 'R', 'E', 'A'};

    static final char[] textarea_close = {'<', '/', 't', 'e', 'x', 't', 'a', 'r', 'e', 'a'};
    static final char[] TEXTAREA_CLOSE = {'<', '/', 'T', 'E', 'X', 'T', 'A', 'R', 'E', 'A'};

    /**
     * Processes one character and returns true if the character should be outputted.
     */
    private boolean processChar(char c) {
        if(inTextArea) {
            if(
                c==textarea_close[readCharMatchCount]
                || c==TEXTAREA_CLOSE[readCharMatchCount]
            ) {
                readCharMatchCount++;
                if(readCharMatchCount>=textarea_close.length) {
                    inTextArea=false;
                    readCharMatchCount=0;
                }
            } else {
                readCharMatchCount=0;
            }
            return true;
        } else {
            if(c=='\r') {
                readCharMatchCount = 0;
                // Carriage return only output when no longer at the beginning of the line
                return !atLineStart;
            } else if(c=='\n') {
                readCharMatchCount = 0;
                // Newline only output when no longer at the beginning of the line
                if(!atLineStart) {
                    atLineStart = true;
                    return true;
                } else {
                    return false;
                }
            } else if(c==' ' || c=='\t') {
                readCharMatchCount = 0;
                // Space and tab only output when no longer at the beginning of the line
                return !atLineStart;
            } else {
                atLineStart = false;
                if(
                    c==textarea[readCharMatchCount]
                    || c==TEXTAREA[readCharMatchCount]
                ) {
                    readCharMatchCount++;
                    if(readCharMatchCount>=textarea.length) {
                        inTextArea=true;
                        readCharMatchCount=0;
                    }
                } else {
                    readCharMatchCount=0;
                }
                return true;
            }
        }
    }

    public void write(int c) {
        if(processChar((char)c)) wrapped.write(c);
    }

    public void write(char buf[], int off, int len) {
        outputBufferUsed = 0;
        // If len > OUPUT_BUFFER_SIZE, process in blocks
        while(len>0) {
            int blockLen = len<=OUPUT_BUFFER_SIZE ? len : OUPUT_BUFFER_SIZE;
            int blockEnd = off + blockLen;
            for(int index = off; index<blockEnd ; index++) {
                char c = buf[index];
                if(processChar(c)) outputBuffer[outputBufferUsed++]=c;
            }
            if(outputBufferUsed>0) {
                if(outputBufferUsed==OUPUT_BUFFER_SIZE) wrapped.write(outputBuffer);
                else wrapped.write(outputBuffer, 0, outputBufferUsed);
                outputBufferUsed = 0;
            }
            off+=blockLen;
            len-=blockLen;
        }
    }

    public void write(char buf[]) {
        write(buf, 0, buf.length);
    }

    public void write(String s, int off, int len) {
        outputBufferUsed = 0;
        // If len > OUPUT_BUFFER_SIZE, process in blocks
        while(len>0) {
            int blockLen = len<=OUPUT_BUFFER_SIZE ? len : OUPUT_BUFFER_SIZE;
            int blockEnd = off + blockLen;
            for(int index = off; index<blockEnd ; index++) {
                char c = s.charAt(index);
                if(processChar(c)) outputBuffer[outputBufferUsed++]=c;
            }
            if(outputBufferUsed>0) {
                if(outputBufferUsed==OUPUT_BUFFER_SIZE) wrapped.write(outputBuffer);
                else wrapped.write(outputBuffer, 0, outputBufferUsed);
                outputBufferUsed = 0;
            }
            off+=blockLen;
            len-=blockLen;
        }
    }

    public void print(boolean b) {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(b);
    }

    public void print(char c) {
        if(processChar(c)) wrapped.print(c);
    }

    public void print(int i) {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(i);
    }

    public void print(long l) {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(l);
    }

    public void print(float f) {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(f);
    }

    public void print(double d) {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(d);
    }

    public void println() {
        write(lineSeparator);
    }

    public void println(boolean b) {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(b);
    }

    public void println(char x) {
        if(processChar(x)) wrapped.print(x);
        write(lineSeparator);
    }

    public void println(int i) {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(i);
    }

    public void println(long l) {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(l);
    }

    public void println(float f) {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(f);
    }

    public void println(double d) {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(d);
    }
    
    public void println(char x[]) {
        write(x);
        write(lineSeparator);
    }

    public void println(String x) {
        write(x);
        write(lineSeparator);
    }
    
    public void println(Object x) {
        print(x);
        write(lineSeparator);
    }

    public PrintWriter format(String format, Object ... args) {
        throw new RuntimeException("TODO: Method not implemented");
        //System.err.println("DEBUG: TrimFilterWriter: format(String format, Object ... args): TODO: Not implemented");
        //wrapped.format(format, args);
        //return this;
    }

    public PrintWriter format(Locale l, String format, Object ... args) {
        throw new RuntimeException("TODO: Method not implemented");
        //System.err.println("DEBUG: TrimFilterWriter: format(Locale l, String format, Object ... args): TODO: Not implemented");
        //wrapped.format(l, format, args);
        //return this;
    }
}
