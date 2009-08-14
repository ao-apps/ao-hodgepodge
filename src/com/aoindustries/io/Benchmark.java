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

import com.aoindustries.sql.*;
import com.aoindustries.util.*;
import java.io.*;

/**
 * A simple disk concurrency scalability benchmark.  Pass in one or more parameters indicating the files or devices to test, such as <code>/dev/md0</code>
 *
 * @author  AO Industries, Inc.
 */
public class Benchmark {

    public static void main(String[] args) {
        final byte[] buff=new byte[4096];
        if(args.length>0) {
            for(int c=0;c<args.length;c++) {
                final String filename=args[c];
                int[] concurrencies={1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64};
                for(int ci=0;ci<concurrencies.length;ci++) {
                    int concurrency=concurrencies[ci];
                    final int[] counter=new int[1];
                    Thread[] threads=new Thread[concurrency];
                    for(int d=0;d<concurrency;d++) {
                        Thread T=threads[d]=new Thread() {
                            public void run() {
                                int count=0;
                                try {
                                    RandomAccessFile raf=new RandomAccessFile(filename, "r");
                                    long length=raf.length();
                                    try {
                                        long endTime=System.currentTimeMillis()+10*1000;
                                        while(System.currentTimeMillis()<endTime) {
                                            raf.seek((long)(Math.random()*(length-4096)));
                                            raf.read(buff, 0, 4096);
                                            count++;
                                        }
                                    } finally {
                                        raf.close();
                                    }
                                } catch(IOException err) {
                                    ErrorPrinter.printStackTraces(err);
                                }
                                synchronized(counter) {
                                    counter[0]+=count;
                                }
                            }
                        };
                        T.start();
                    }
                    for(int d=0;d<concurrency;d++) {
                        try {
                            threads[d].join();
                        } catch(InterruptedException err) {
                            ErrorPrinter.printStackTraces(err);
                        }
                    }
                    System.out.println(concurrency+": "+counter[0]/10+", "+SQLUtility.getDecimal((int)(1000000d*concurrency/counter[0])));
                }
            }
        } else {
            System.err.println("Usage: Benchmark filename [filename] [...]");
        }
    }
}
