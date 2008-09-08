package com.aoindustries.io;

/*
 * Copyright 2007-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.ErrorPrinter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Times how long a counter block device takes to scan all counters.
 * Uses hard-coded 1 MB chunks and 512 byte sectors.
 *
 * @author  AO Industries, Inc.
 */
public class BenchmarkCounterBlockDevice {

    public static void main(String[] args) {
        try {
            if(args.length>0) {
                final byte[] buff=new byte[512];
                for(int c=0;c<args.length;c++) {
                    final String filename=args[c];
                    long startTime = System.currentTimeMillis();
                    RandomAccessFile raf=new RandomAccessFile(filename, "r");
                    long length=raf.length();
                    try {
                        for(long pos=0;pos<length;pos+=(64*512+512)) {
                            raf.seek(pos);
                            raf.read(buff, 0, 512);
                        }
                    } finally {
                        raf.close();
                    }
                    System.out.println(filename+" scanned in "+SQLUtility.getMilliDecimal(System.currentTimeMillis()-startTime)+" seconds");
                }
            } else {
                System.err.println("Usage: BenchmarkCounterBlockDevice filename [filename] [...]");
            }
        } catch(IOException err) {
            ErrorPrinter.printStackTraces(err);
        }
    }
}
