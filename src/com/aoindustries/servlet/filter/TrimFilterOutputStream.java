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

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

/**
 * Filters the output and removes extra white space at the beginning of lines and completely removes blank lines.
 * TEXTAREAs are automatically detected as long as they start with exact "&lt;textarea" and end with exactly "&lt;/textarea" (case insensitive).
 * PREs are automatically detected as long as they start with exact "&lt;pre" and end with exactly "&lt;/pre" (case insensitive).
 * The reason for the specific tag format is to simplify the implementation
 * for maximum performance.  Careful attention has been paid to minimize the internal buffering in this class.  As many write/print operations as possible
 * are passed directly to the wrapped <code>ServletOutputStream</code>.  Please note that these methods are not synchronized, as servlet output is normally written
 * by the thread allocated for the request.  If synchronization is required it should be provided externally.
 * 
 * @author  AO Industries, Inc.
 */
public class TrimFilterOutputStream extends ServletOutputStream {

    private static String lineSeparator = System.getProperty("line.separator");

    private final ServletOutputStream wrapped;
    private final ServletResponse response;
    boolean inTextArea = false;
    boolean inPre = false;
    private boolean atLineStart = true;

    private int readCharMatchCount = 0;
    private int preReadCharMatchCount = 0;
    private static final int OUPUT_BUFFER_SIZE=4096;
    private byte[] outputBuffer = new byte[OUPUT_BUFFER_SIZE];
    private int outputBufferUsed = 0;

    public TrimFilterOutputStream(ServletOutputStream wrapped, ServletResponse response) {
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
    public void close() throws IOException {
        wrapped.close();
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        inTextArea = false;
        inPre = false;
        atLineStart = true;
    }
    
    @Override
    public void flush() throws IOException {
        wrapped.flush();
    }

    /**
     * Processes one character and returns true if the character should be outputted.
     */
    private boolean processChar(char c) {
        if(inTextArea) {
            if(
                c==TrimFilterWriter.textarea_close[readCharMatchCount]
                || c==TrimFilterWriter.TEXTAREA_CLOSE[readCharMatchCount]
            ) {
                readCharMatchCount++;
                if(readCharMatchCount>=TrimFilterWriter.textarea_close.length) {
                    inTextArea=false;
                    readCharMatchCount=0;
                }
            } else {
                readCharMatchCount=0;
            }
            return true;
        } else if(inPre) {
            if(
                c==TrimFilterWriter.pre_close[preReadCharMatchCount]
                || c==TrimFilterWriter.PRE_CLOSE[preReadCharMatchCount]
            ) {
                preReadCharMatchCount++;
                if(preReadCharMatchCount>=TrimFilterWriter.pre_close.length) {
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
                    c==TrimFilterWriter.textarea[readCharMatchCount]
                    || c==TrimFilterWriter.TEXTAREA[readCharMatchCount]
                ) {
                    readCharMatchCount++;
                    if(readCharMatchCount>=TrimFilterWriter.textarea.length) {
                        inTextArea=true;
                        readCharMatchCount=0;
                    }
                } else {
                    readCharMatchCount=0;
                }
                if(
                    c==TrimFilterWriter.pre[preReadCharMatchCount]
                    || c==TrimFilterWriter.PRE[preReadCharMatchCount]
                ) {
                    preReadCharMatchCount++;
                    if(preReadCharMatchCount>=TrimFilterWriter.pre.length) {
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
    public void write(int b) throws IOException {
        if(!isTrimEnabled() || processChar((char)b)) wrapped.write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        if(isTrimEnabled()) {
            outputBufferUsed = 0;
            // If len > OUPUT_BUFFER_SIZE, process in blocks
            while(len>0) {
                int blockLen = len<=OUPUT_BUFFER_SIZE ? len : OUPUT_BUFFER_SIZE;
                int blockEnd = off + blockLen;
                for(int index = off; index<blockEnd ; index++) {
                    byte b = buf[index];
                    if(processChar((char)b)) outputBuffer[outputBufferUsed++]=b;
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
    public void write(byte[] b) throws IOException {
        if(isTrimEnabled()) write(b, 0, b.length);
        else wrapped.write(b);
    }

    @Override
    public void print(boolean b) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(b);
    }

    @Override
    public void print(char c) throws IOException {
        if(!isTrimEnabled() || processChar(c)) wrapped.print(c);
    }

    @Override
    public void print(double d) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(d);
    }

    @Override
    public void print(float f) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(f);
    }

    @Override
    public void print(int i) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(i);
    }

    @Override
    public void print(long l) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.print(l);
    }

    @Override
    public void print(String s) throws IOException {
        if(isTrimEnabled()) {
            outputBufferUsed = 0;
            // If len > OUPUT_BUFFER_SIZE, process in blocks
            int off = 0;
            int len = s.length();
            while(len>0) {
                int blockLen = len<=OUPUT_BUFFER_SIZE ? len : OUPUT_BUFFER_SIZE;
                int blockEnd = off + blockLen;
                for(int index = off; index<blockEnd ; index++) {
                    char c = s.charAt(index);
                    if(processChar(c)) outputBuffer[outputBufferUsed++]=(byte)c;
                }
                if(outputBufferUsed>0) {
                    if(outputBufferUsed==OUPUT_BUFFER_SIZE) wrapped.write(outputBuffer);
                    else wrapped.write(outputBuffer, 0, outputBufferUsed);
                    outputBufferUsed = 0;
                }
                off+=blockLen;
                len-=blockLen;
            }
        } else wrapped.print(s);
    }

    @Override
    public void println() throws IOException {
        if(isTrimEnabled()) print(lineSeparator);
        else wrapped.println();
    }

    @Override
    public void println(boolean b) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(b);
    }

    @Override
    public void println(char c) throws IOException {
        if(isTrimEnabled()) {
            if(processChar(c)) wrapped.print(c);
            print(lineSeparator);
        } else {
            wrapped.println(c);
        }
    }

    @Override
    public void println(double d) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(d);
    }
    
    @Override
    public void println(float f) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(f);
    }
    
    @Override
    public void println(int i) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(i);
    }
    
    @Override
    public void println(long l) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        preReadCharMatchCount = 0;
        wrapped.println(l);
    }

    @Override
    public void println(String s) throws IOException {
        if(isTrimEnabled()) {
            print(s);
            print(lineSeparator);
        } else {
            wrapped.println(s);
        }
    }
}
