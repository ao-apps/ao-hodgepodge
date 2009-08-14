/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Adds compressed data transfer to DataInputStream.  This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class CompressedDataInputStream extends DataInputStream {
    
    public CompressedDataInputStream(InputStream in) {
        super(in);
        for(int c=0;c<64;c++) {
            lastStrings[c]="";
            lastCommonLengths[c]=0;
        }
    }

    /**
     * Reads a compressed integer from the stream.
     *
     * The 31 bit pattern is as follows:
     * <pre>
     * 5 bit   - 000SXXXX
     * 13 bit  - 001SXXXX XXXXXXXX
     * 22 bit  - 01SXXXXX XXXXXXXX XXXXXXXX
     * 31 bit  - 1SXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
     * </pre>
     *
     * @exception  EOFException if the end of file is reached
     */
    public static int readCompressedInt(InputStream in) throws IOException {
        int b1=in.read();
        if(b1==-1) throw new EOFException();
        if((b1&0x80)!=0) {
            // 31 bit
            int b2=in.read();
            if(b2==-1) throw new EOFException();
            int b3=in.read();
            if(b3==-1) throw new EOFException();
            int b4=in.read();
            if(b4==-1) throw new EOFException();
            return
                ((b1&0x40)==0 ? 0 : 0xc0000000)
                | ((b1&0x3f)<<24)
                | (b2<<16)
                | (b3<<8)
                | b4
            ;
        } else if((b1&0x40)!=0) {
            // 22 bit
            int b2=in.read();
            if(b2==-1) throw new EOFException();
            int b3=in.read();
            if(b3==-1) throw new EOFException();
            return
                ((b1&0x20)==0 ? 0 : 0xffe00000)
                | ((b1&0x1f)<<16)
                | (b2<<8)
                | b3
            ;
        } else if((b1&0x20)!=0) {
            // 13 bit
            int b2=in.read();
            if(b2==-1) throw new EOFException();
            return
                ((b1&0x10)==0 ? 0 : 0xfffff000)
                | ((b1&0x0f)<<8)
                | b2
            ;
        } else {
            // 5 bit
            return
                ((b1&0x10)==0 ? 0 : 0xfffffff0)
                | (b1&0x0f)
            ;
        }
    }

    /**
     * Reads a compressed integer from the stream.
     *
     * The 31 bit pattern is as follows:
     * <pre>
     * 5 bit   - 000SXXXX
     * 13 bit  - 001SXXXX XXXXXXXX
     * 22 bit  - 01SXXXXX XXXXXXXX XXXXXXXX
     * 31 bit  - 1SXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
     * </pre>
     *
     * @exception  EOFException if the end of file is reached
     */
    public int readCompressedInt() throws IOException {
        return readCompressedInt(in);
    }
    
    private final String[] lastStrings=new String[64];
    private final int[] lastCommonLengths=new int[64];

    /**
     * @exception  EOFException if the end of file is reached
     */
    public String readCompressedUTF() throws IOException {
        int b1=in.read();
        if(b1==-1) throw new EOFException();
        int slot=b1&0x3f;

        // Is there a difference to the common
        if((b1&0x80)!=0) {
            int diff=readCompressedInt();
            if(diff>=0) diff++;
            lastCommonLengths[slot]+=diff;
        }

        // Is there a suffix String
        int common=lastCommonLengths[slot];
        if((b1&0x40)!=0) {
            String suffix=readUTF();
            if(common==0) return lastStrings[slot]=suffix;
            else return lastStrings[slot]=lastStrings[slot].substring(0, common)+suffix;
        } else {
            String last=lastStrings[slot];
            if(common==last.length()) return last;
            else return lastStrings[slot]=lastStrings[slot].substring(0, common);
        }
    }

    public String readNullUTF() throws IOException {
        return readBoolean() ? readUTF() : null;
    }

    /**
     * Reads a string of any length.
     */
    public String readLongUTF() throws IOException {
        int length = readCompressedInt();
        StringBuilder SB = new StringBuilder(length);
        for(int position = 0; position<length; position+=20480) {
            int expectedLen = length - position;
            if(expectedLen>20480) expectedLen = 20480;
            String block = readUTF();
            if(block.length()!=expectedLen) throw new IOException("Block has unexpected length: expected "+expectedLen+", got "+block.length());
            SB.append(block);
        }
        if(SB.length()!=length) throw new IOException("StringBuilder has unexpected length: expected "+length+", got "+SB.length());
        return SB.toString();
    }

    /**
     * Reads a string of any length, supporting <code>null</code>.
     */
    public String readNullLongUTF() throws IOException {
        return readBoolean() ? readLongUTF() : null;
    }

    public Integer readNullInteger() throws IOException {
        return readBoolean() ? readInt() : null;
    }

    public <T extends Enum<T>> T readNullEnum(Class<T> enumType) throws IOException {
        try {
            return readBoolean() ? Enum.valueOf(enumType, readUTF()) : null;
        } catch(IllegalArgumentException err) {
            IOException ioErr = new IOException();
            ioErr.initCause(err);
            throw ioErr;
        }
    }

    public Long readNullLong() throws IOException {
        return readBoolean() ? readLong() : null;
    }
}
