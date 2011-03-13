/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011  AO Industries, Inc.
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
package com.aoindustries.servlet.filter;

import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;

/**
 * Filters the output and removes extra white space at the beginning of lines and completely removes blank lines.
 * TEXTAREAs are automatically detected as long as they start with exact "&lt;textarea" and end with exactly "&lt;/textarea" (case insensitive).
 * The reason for the specific tag format is to simplify the implementation
 * for maximum performance.  Careful attention has been paid to minimize the internal buffering in this class.  As many write/print operations as possible
 * are passed directly to the wrapped <code>PrintWriter</code>.  Please note that these methods are not synchronized, as servlet output is normally written
 * by the thread allocated for the request.  If synchronization is required it should be provided externally.
 * 
 * @author  AO Industries, Inc.
 */
public class TrimFilterWriter extends PrintWriter {

    private static String lineSeparator = System.getProperty("line.separator");

    private final PrintWriter wrapped;
    private final ServletResponse response;
    boolean inTextArea = false;
    boolean inPre = false;
    private boolean atLineStart = true;

    private int readCharMatchCount = 0;
    private int preReadCharMatchCount = 0;
    private static final int OUPUT_BUFFER_SIZE=4096;
    private char[] outputBuffer = new char[OUPUT_BUFFER_SIZE];
    private int outputBufferUsed = 0;

    public TrimFilterWriter(PrintWriter wrapped, ServletResponse response) {
        super(wrapped);
        this.wrapped = wrapped;
        this.response = response;
    }

    /**
     * Determines if trimming is enabled based on the output content type.
     */
    private boolean isTrimEnabled() {
        String contentType = response.getContentType();
        return
            contentType==null
            || contentType.equals("text/html")
            || contentType.startsWith("text/html;")
            || contentType.equals("application/xhtml+xml")
            || contentType.startsWith("application/xhtml+xml;")
        ;
    }

    @Override
    public void flush() {
        wrapped.flush();
    }
    
    @Override
    public void close() {
        wrapped.close();
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        inTextArea = false;
        inPre = false;
        atLineStart = true;
    }

    @Override
    public boolean checkError() {
        return wrapped.checkError();
    }

    static final char[] textarea = {'<', 't', 'e', 'x', 't', 'a', 'r', 'e', 'a'};
    static final char[] TEXTAREA = {'<', 'T', 'E', 'X', 'T', 'A', 'R', 'E', 'A'};

    static final char[] textarea_close = {'<', '/', 't', 'e', 'x', 't', 'a', 'r', 'e', 'a'};
    static final char[] TEXTAREA_CLOSE = {'<', '/', 'T', 'E', 'X', 'T', 'A', 'R', 'E', 'A'};

    static final char[] pre = {'<', 'p', 'r', 'e'};
    static final char[] PRE = {'<', 'P', 'R', 'E'};

    static final char[] pre_close = {'<', '/', 'p', 'r', 'e'};
    static final char[] PRE_CLOSE = {'<', '/', 'P', 'R', 'E'};

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
        } else if(inPre) {
            if(
                c==pre_close[preReadCharMatchCount]
                || c==PRE_CLOSE[preReadCharMatchCount]
            ) {
                preReadCharMatchCount++;
                if(preReadCharMatchCount>=pre_close.length) {
                    inPre=false;
                    preReadCharMatchCount=0;
                }
            } else {
                preReadCharMatchCount=0;
            }
            return true;
        } else {
            if(c=='\r') {
                readCharMatchCount = 0;
                preReadCharMatchCount = 0;
                // Carriage return only output when no longer at the beginning of the line
                return !atLineStart;
            } else if(c=='\n') {
                readCharMatchCount = 0;
                preReadCharMatchCount = 0;
                // Newline only output when no longer at the beginning of the line
                if(!atLineStart) {
                    atLineStart = true;
                    return true;
                } else {
                    return false;
                }
            } else if(c==' ' || c=='\t') {
                readCharMatchCount = 0;
                preReadCharMatchCount = 0;
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
                if(
                    c==pre[preReadCharMatchCount]
                    || c==PRE[preReadCharMatchCount]
                ) {
                    preReadCharMatchCount++;
                    if(preReadCharMatchCount>=pre.length) {
                        inPre=true;
                        preReadCharMatchCount=0;
                    }
                } else {
                    preReadCharMatchCount=0;
                }
                return true;
            }
        }
    }

    @Override
    public void write(int c) {
        if(!isTrimEnabled() || processChar((char)c)) wrapped.write(c);
    }

    @Override
    public void write(char buf[], int off, int len) {
        if(isTrimEnabled()) {
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
        } else {
            wrapped.write(buf, off, len);
        }
    }

    @Override
    public void write(char buf[]) {
        if(isTrimEnabled()) write(buf, 0, buf.length);
        else wrapped.write(buf);
    }

    @Override
    public void write(String s, int off, int len) {
        if(isTrimEnabled()) {
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
        } else wrapped.write(s, off, len);
    }

    @Override
    public void print(boolean b) {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(b);
    }

    @Override
    public void print(char c) {
        if(!isTrimEnabled() || processChar(c)) wrapped.print(c);
    }

    @Override
    public void print(int i) {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(i);
    }

    @Override
    public void print(long l) {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(l);
    }

    @Override
    public void print(float f) {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(f);
    }

    @Override
    public void print(double d) {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(d);
    }

    @Override
    public void println() {
        if(isTrimEnabled()) write(lineSeparator);
        else wrapped.println();
    }

    @Override
    public void println(boolean b) {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(b);
    }

    @Override
    public void println(char x) {
        if(isTrimEnabled()) {
            if(processChar(x)) wrapped.print(x);
            write(lineSeparator);
        } else wrapped.println(x);
    }

    @Override
    public void println(int i) {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(i);
    }

    @Override
    public void println(long l) {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(l);
    }

    @Override
    public void println(float f) {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(f);
    }

    @Override
    public void println(double d) {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(d);
    }
    
    @Override
    public void println(char x[]) {
        if(isTrimEnabled()) {
            write(x);
            write(lineSeparator);
        } else wrapped.println(x);
    }

    @Override
    public void println(String x) {
        if(isTrimEnabled()) {
            write(x);
            write(lineSeparator);
        } else wrapped.println(x);
    }
    
    @Override
    public void println(Object x) {
        if(isTrimEnabled()) {
            print(x);
            write(lineSeparator);
        } else wrapped.println(x);
    }

    @Override
    public PrintWriter format(String format, Object ... args) {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.format(format, args);
        return this;
    }

    @Override
    public PrintWriter format(Locale l, String format, Object ... args) {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.format(l, format, args);
        return this;
    }
}
