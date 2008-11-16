package com.aoindustries.io;

/*
 * Copyright 2003-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * Adds compressed data transfer to DataOutputStream.  This class is not
 * thread safe.
 *
 * @see CompressedDataInputStream
 *
 * @author  AO Industries, Inc.
 */
public class CompressedDataOutputStream extends DataOutputStream {
    
    public CompressedDataOutputStream(OutputStream out) {
        super(out);
        for(int c=0;c<64;c++) {
            lastStrings[c]="";
            lastCommonLengths[c]=0;
        }
    }

    public void writeCompressedInt(int i, OutputStream out) throws IOException {
        int t;
        if(
            (t=i&0xfffffff0)==0
            || t==0xfffffff0
        ) {
            // 5 bit
            out.write(i&0x1f);
        } else if(
            (t=i&0xfffff000)==0
            || t==0xfffff000
        ) {
            // 13 bit
            out.write(0x20|((i&0x1f00)>>>8));
            out.write(i&0xff);
        } else if(
            (t=i&0xffe00000)==0
            || t==0xffe00000
        ) {
            // 22 bit
            out.write(0x40|((i&0x3f0000)>>>16));
            out.write((i&0xff00)>>>8);
            out.write(i&0xff);
        } else if(
            (t=i&0xc0000000)==0
            || t==0xc0000000
        ) {
            // 31 bit
            out.write(0x80|((i&0x7f000000)>>>24));
            out.write((i&0xff0000)>>>16);
            out.write((i&0xff00)>>>8);
            out.write(i&0xff);
        } else {
            throw new IOException("Value out of range ("+(-0x40000000)+" to "+0x3fffffff+"): "+i);
        }
    }

    public void writeCompressedInt(int i) throws IOException {
        writeCompressedInt(i, out);
    }

    public void writeCompressedUTF(String str) throws IOException {
        writeCompressedUTF(str, 0);
    }

    private final String[] lastStrings=new String[64];
    private final int[] lastCommonLengths=new int[64];
    
    /**
     * Writes a String to the stream while using prefix compression.
     *
     * <pre>
     * The first byte has these bits:
     *
     * X X X X X X X X
     * | | +-+-+-+-+-+ Slot number (0-63)
     * | +------------ 1 = Suffix UTF follows, 0 = No suffix UTF exists
     * +-------------- 1 = Common length difference follows, 0 = Common length not changed
     *
     * Second, if common length difference is not zero, the common length change follows
     *                 one less for positive differences because 0 is handled in first byte
     *
     * Third, if suffix UTF follows, writeUTF of all the string after common length
     * </pre>
     */
    public void writeCompressedUTF(String str, int slot) throws IOException {
        if(slot<0 || slot>0x3f) throw new IOException("Slot out of range (0-63): "+slot);
        String last=lastStrings[slot];
        int strLen=str.length();
        int lastLen=last.length();
        int maxCommon=Math.min(strLen, lastLen);
        int common=0;
        for(;common<maxCommon;common++) {
            if(str.charAt(common)!=last.charAt(common)) break;
        }
        int commonDifference=common-lastCommonLengths[slot];

        // Write the header byte
        out.write(
            (commonDifference==0?0:0x80)
            | (common==strLen?0:0x40)
            | slot
        );

        // Write the common difference
        if(commonDifference>0) writeCompressedInt(commonDifference-1);
        else if(commonDifference<0) writeCompressedInt(commonDifference);

        // Write the suffix
        if(common!=strLen) writeUTF(str.substring(common));

        // Get ready for the next call
        lastStrings[slot]=str;
        lastCommonLengths[slot]=common;
    }
    
    public void writeNullUTF(String str) throws IOException {
        writeBoolean(str!=null);
        if(str!=null) writeUTF(str);
    }
}
