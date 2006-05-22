package com.aoindustries.servlet.filter;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */

import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 * Filters the output and removes extra white space at the beginning of lines and completely removes blank lines.
 * TEXTAREAs are automatically detected as long as they start with exact "&lt;textarea" and end with exactly "&lt;/textarea" (case insensitive).
 * The reason for the specific tag format is to simplify the implementation
 * for maximum performance.  Careful attention has been paid to minimize the internal buffering in this class.  As many write/print operations as possible
 * are passed directly to the wrapped <code>ServletOutputStream</code>.  Please note that these methods are not synchronized, as servlet output is normally written
 * by the thread allocated for the request.  If synchronization is required it should be provided externally.
 *
 * TODO: Don't trim inside PRE tags.
 * 
 * @author  AO Industries, Inc.
 */
public class TrimFilterOutputStream extends ServletOutputStream {

    private static String lineSeparator = System.getProperty("line.separator");

    private ServletOutputStream wrapped;
    boolean inTextArea = false;
    private boolean atLineStart = true;

    private int readCharMatchCount = 0;
    private static final int OUPUT_BUFFER_SIZE=4096;
    private byte[] outputBuffer = new byte[OUPUT_BUFFER_SIZE];
    private int outputBufferUsed = 0;

    public TrimFilterOutputStream(ServletOutputStream wrapped) {
        this.wrapped = wrapped;
    }

    public void close() throws IOException {
        wrapped.close();
        readCharMatchCount = 0;
        inTextArea = false;
        atLineStart = true;
    }
    
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
                return true;
            }
        }
    }

    public void write(int b) throws IOException {
        if(processChar((char)b)) wrapped.write(b);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
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
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void print(boolean b) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(b);
    }

    public void print(char c) throws IOException {
        if(processChar(c)) wrapped.print(c);
    }

    public void print(double d) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(d);
    }

    public void print(float f) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(f);
    }

    public void print(int i) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(i);
    }

    public void print(long l) throws IOException {
        atLineStart = false;
        readCharMatchCount = 0;
        wrapped.print(l);
    }

    public void print(String s) throws IOException {
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
    }

    public void println() throws IOException {
        print(lineSeparator);
    }

    public void println(boolean b) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(b);
    }

    public void println(char c) throws IOException {
        if(processChar(c)) wrapped.print(c);
        print(lineSeparator);
    }

    public void println(double d) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(d);
    }
    
    public void println(float f) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(f);
    }
    
    public void println(int i) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(i);
    }
    
    public void println(long l) throws IOException {
        atLineStart = true;
        readCharMatchCount = 0;
        wrapped.println(l);
    }

    public void println(String s) throws IOException {
        print(s);
        print(lineSeparator);
    }
}
