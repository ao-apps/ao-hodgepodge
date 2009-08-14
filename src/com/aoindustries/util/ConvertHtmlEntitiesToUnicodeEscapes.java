/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Filters for HTML unicode entities, in either &amp;#ddddd; or &amp;#xhhhh; format and converts them to escaped Java unicode characters.
 * Because <code>write(int[],int,int)</code> is not implemented, this is not a high performance implementation.
 *
 * @author  AO Industries, Inc.
 */
public final class ConvertHtmlEntitiesToUnicodeEscapes extends FilterOutputStream {

    private static final int BUFFER_SIZE = 7;
    final private byte[] buffer = new byte[BUFFER_SIZE];
    private int bufferUsed = 0;

    /**
     * Converts HTML Entities to Unicode from <code>System.in</code> to <code>System.out</code>.
     */
    public static void main(String[] args) {
        try {
            InputStream in = new BufferedInputStream(System.in);
            OutputStream out = new ConvertHtmlEntitiesToUnicodeEscapes(new BufferedOutputStream(System.out));
            int b;
            while((b=in.read())!=-1) out.write(b);
            out.flush();
        } catch(IOException err) {
            ErrorPrinter.printStackTraces(err);
        }
    }

    public ConvertHtmlEntitiesToUnicodeEscapes(OutputStream out) {
        super(out);
    }

    public void write(int b) throws IOException {
        synchronized(buffer) {
            switch(bufferUsed) {
                case 0:
                    if(b=='&') {
                        buffer[0]='&';
                        bufferUsed = 1;
                    } else {
                        out.write(b);
                    }
                    break;
                case 1:
                    if(b=='#') {
                        buffer[1]='#';
                        bufferUsed = 2;
                    } else {
                        out.write('&');
                        out.write(b);
                        bufferUsed = 0;
                    }
                    break;
                case 2:
                    if(b=='x') {
                        buffer[2]='x';
                        bufferUsed = 3;
                    } else if(b>='0' && b<='9') {
                        buffer[2]=(byte)b;
                        bufferUsed = 3;
                    } else {
                        out.write(buffer, 0, 2);
                        out.write(b);
                        bufferUsed = 0;
                    }
                    break;
                case 3:
                    if(b==';') {
                        if(buffer[2]=='x') {
                            // was &#x;
                            out.write('&');
                            out.write('#');
                            out.write('x');
                            out.write(';');
                            bufferUsed = 0;
                        } else {
                            // was &#d;
                            int unicode = buffer[2]-'0';
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        }
                    } else if(
                        (b>='0' && b<='9')
                        || (
                            buffer[2]=='x'
                            && (
                                (b>='a' && b<='f')
                                || (b>='A' && b<='F')
                            )
                        )
                    ) {
                        buffer[3]=(byte)b;
                        bufferUsed = 4;
                    } else {
                        out.write(buffer, 0, 3);
                        out.write(b);
                        bufferUsed = 0;
                    }
                    break;
                case 4:
                    if(b==';') {
                        if(buffer[2]=='x') {
                            // was &#xh;
                            int unicode = StringUtility.getHex((char)buffer[3]);
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        } else {
                            // was &#dd;
                            int unicode = (buffer[2]-'0')*10 + (buffer[3]-'0');
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        }
                    } else if(
                        (b>='0' && b<='9')
                        || (
                            buffer[2]=='x'
                            && (
                                (b>='a' && b<='f')
                                || (b>='A' && b<='F')
                            )
                        )
                    ) {
                        buffer[4]=(byte)b;
                        bufferUsed = 5;
                    } else {
                        out.write(buffer, 0, 4);
                        out.write(b);
                        bufferUsed = 0;
                    }
                    break;
                case 5:
                    if(b==';') {
                        if(buffer[2]=='x') {
                            // was &#xhh;
                            int unicode = StringUtility.getHex((char)buffer[3])<<4 | StringUtility.getHex((char)buffer[4]);
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        } else {
                            // was &#ddd;
                            int unicode = (buffer[2]-'0')*100 + (buffer[3]-'0')*10 + (buffer[4]-'0');
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        }
                    } else if(
                        (b>='0' && b<='9')
                        || (
                            buffer[2]=='x'
                            && (
                                (b>='a' && b<='f')
                                || (b>='A' && b<='F')
                            )
                        )
                    ) {
                        buffer[5]=(byte)b;
                        bufferUsed = 6;
                    } else {
                        out.write(buffer, 0, 5);
                        out.write(b);
                        bufferUsed = 0;
                    }
                    break;
                case 6:
                    if(b==';') {
                        if(buffer[2]=='x') {
                            // was &#xhhh;
                            int unicode = StringUtility.getHex((char)buffer[3])<<8 | StringUtility.getHex((char)buffer[4])<<4 | StringUtility.getHex((char)buffer[5]);
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        } else {
                            // was &#dddd;
                            int unicode = (buffer[2]-'0')*1000 + (buffer[3]-'0')*100 + (buffer[4]-'0')*10 + (buffer[5]-'0');
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        }
                    } else if(
                        (b>='0' && b<='9')
                        || (
                            buffer[2]=='x'
                            && (
                                (b>='a' && b<='f')
                                || (b>='A' && b<='F')
                            )
                        )
                    ) {
                        buffer[6]=(byte)b;
                        bufferUsed = 7;
                    } else {
                        out.write(buffer, 0, 6);
                        out.write(b);
                        bufferUsed = 0;
                    }
                    break;
                case 7:
                    if(b==';') {
                        if(buffer[2]=='x') {
                            // was &#xhhhh;
                            int unicode = StringUtility.getHex((char)buffer[3])<<12 | StringUtility.getHex((char)buffer[4])<<8 | StringUtility.getHex((char)buffer[5])<<4 | StringUtility.getHex((char)buffer[6]);
                            writeUnicodeEscape(unicode);
                            bufferUsed = 0;
                        } else {
                            // was &#ddddd;
                            int unicode = (buffer[2]-'0')*10000 + (buffer[3]-'0')*1000 + (buffer[4]-'0')*100 + (buffer[5]-'0')*10 + (buffer[6]-'0');
                            if(unicode>0xffff) {
                                out.write(buffer, 0, 7);
                                out.write(b);
                            } else {
                                writeUnicodeEscape(unicode);
                            }
                            bufferUsed = 0;
                        }
                    } else {
                        out.write(buffer, 0, 7);
                        out.write(b);
                        bufferUsed = 0;
                    }
                    break;
                default:
                    throw new IOException("bufferUsed has unexpected value: "+bufferUsed);
            }
        }
    }

    private void writeUnicodeEscape(int unicode) throws IOException {
        out.write('\\');
        out.write('u');
        writeHex((unicode>>>12)&15);
        writeHex((unicode>>>8)&15);
        writeHex((unicode>>>4)&15);
        writeHex(unicode&15);
    }

    private void writeHex(int value) throws IOException {
        if(value<10) out.write('0'+value);
        else out.write('A'+value-10);
    }

    private void flushBuffer() throws IOException {
        synchronized(buffer) {
            if(bufferUsed>0) {
                out.write(buffer, 0, bufferUsed);
                bufferUsed=0;
            }
        }
    }

    public void flush() throws IOException {
        flushBuffer();
        out.flush();
    }
    
    public void close() throws IOException {
        flushBuffer();
        out.close();
    }
}
