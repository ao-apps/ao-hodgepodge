package com.aoindustries.io;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * Adds compressed data transfer to DataOutputStream
 *
 * @see CompressedDataInputStream
 *
 * @author  AO Industries, Inc.
 */
public class CompressedDataOutputStream extends DataOutputStream {
    
    public CompressedDataOutputStream(OutputStream out) {
        super(out);
        Profiler.startProfile(Profiler.FAST, CompressedDataOutputStream.class, "<init>(OutputStream)", null);
        try {
            for(int c=0;c<64;c++) {
                lastStrings[c]="";
                lastCommonLengths[c]=0;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
    
    public void writeCompressedInt(int i) throws IOException {
        Profiler.startProfile(Profiler.IO, CompressedDataOutputStream.class, "writeCompressedInt(int)", null);
        try {
            synchronized(this) {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void writeCompressedUTF(String str) throws IOException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, CompressedDataOutputStream.class, "writeCompressedUTF(String)", null);
        try {
            writeCompressedUTF(str, 0);
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
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
        Profiler.startProfile(Profiler.IO, CompressedDataOutputStream.class, "writeCompressedUTF(String,int)", null);
        try {
            if(slot<0 || slot>0x3f) throw new IOException("Slot out of range (0-63): "+slot);
            synchronized(this) {
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
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    public void writeNullUTF(String str) throws IOException {
        writeBoolean(str!=null);
        if(str!=null) writeUTF(str);
    }
}
