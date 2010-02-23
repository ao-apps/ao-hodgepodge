/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010  AO Industries, Inc.
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

import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.StringUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Concatenates files similar to the Unix cat command, but with a limited throughput.
 * This is useful to avoid heavy disk or network I/O when immediately transfer
 * is not required.
 *
 * @author  AO Industries, Inc.
 */
final public class RateLimitCat {

    private RateLimitCat() {
    }

    private static void usage() {
        System.err.println("Usage: "+RateLimitCat.class.getName()+" bits_per_second [--progress] [--] [FILE]...");
        System.err.println();
        System.err.println("\tWhen FILE is not provided, reads from standard input.");
        System.err.println("\tWhen FILE is -, will read from standard input.");
        System.err.println("\tWhen -- is first found, all subsequent arguments will be treated as filenames, including any - and --.");
        System.err.println();
        System.err.println("\t--progress  Displays the progress of the files once every 60 seconds.");
        System.err.println();
        System.err.println("\tWrites to standard output.");
        System.err.flush();
    }

    private static void report(String filename, long byteCount, long timespan) {
        System.err.print(filename);
        System.err.print(": ");
        System.err.print(byteCount);
        System.err.print(" bytes (");
        System.err.print(StringUtility.getApproximateSize(byteCount));
        System.err.print(") transferred in ");
        System.err.print(SQLUtility.getMilliDecimal(timespan));
        System.err.print(" seconds (");
        System.err.print(StringUtility.getTimeLengthString(timespan));
        System.err.println(')');
        System.err.flush();
    }

    private static void transfer(String filename, InputStream in, OutputStream out, byte[] buff, boolean showProgress) throws IOException {
        long startTime = System.currentTimeMillis();
        long lastReportByteCount = -1;
        long lastReportTime = startTime;
        long byteCount = 0;
        try {
            int ret;
            while((ret=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                out.write(buff, 0, ret);
                byteCount += ret;
                if(showProgress && lastReportByteCount!=byteCount) {
                    long currentTime = System.currentTimeMillis();
                    long timeSinceReport = currentTime - lastReportTime;
                    if(timeSinceReport<0) {
                        // System time reset
                        lastReportTime = currentTime;
                    } else if(timeSinceReport>=60000) {
                        report(filename, byteCount, currentTime - startTime);
                        lastReportByteCount = byteCount;
                        lastReportTime = currentTime;
                    }
                }
            }
        } finally {
            if(showProgress && lastReportByteCount!=byteCount) report(filename, byteCount, System.currentTimeMillis() - startTime);
        }
    }

    public static void main(String[] args) {
        int retval;
        if(args.length==0) {
            usage();
            retval = 1;
        } else {
            try {
                final int bitsPerSecond = Integer.parseInt(args[0]);
                if(bitsPerSecond<=0) {
                    System.err.println("Invalid bits_per_second: "+bitsPerSecond);
                    System.err.println();
                    usage();
                    retval = 3;
                } else {
                    boolean showProgress = false;
                    boolean hasError = false;
                    boolean allowStdin = true;
                    int fileCount = 0;
                    for(int c=1; c<args.length; c++) {
                        String arg = args[c];
                        if(allowStdin && "--".equals(arg)) allowStdin = false;
                        else if(allowStdin && "-".equals(arg)) fileCount++;
                        else if(allowStdin && "--progress".equals(arg)) {
                            showProgress = true;
                        } else {
                            File file = new File(arg);
                            if(!file.exists()) {
                                System.err.println("File not found: "+arg);
                                hasError = true;
                            } else if(file.isDirectory()) {
                                System.err.println("Directories not supported: "+arg);
                                hasError = true;
                            } else if(!file.canRead()) {
                                System.err.println("Unable to read file: "+arg);
                                hasError = true;
                            } else {
                                fileCount++;
                            }
                        }
                    }
                    if(hasError) {
                        System.err.flush();
                        retval = 4;
                    } else {
                        try {
                            OutputStream out = new BitRateOutputStream(
                                new PrintStreamOutputStream(System.out),
                                new BitRateProvider() {
                                    public int getBitRate() throws IOException {
                                        return bitsPerSecond;
                                    }
                                    public int getBlockSize() {
                                        return BufferManager.BUFFER_SIZE;
                                    }
                                }
                            );
                            try {
                                byte[] buff = BufferManager.getBytes();
                                if(fileCount==0) {
                                    transfer("-", System.in, out, buff, showProgress);
                                } else {
                                    allowStdin = true;
                                    for(int c=1; c<args.length; c++) {
                                        String arg = args[c];
                                        if(allowStdin && "--".equals(arg)) {
                                            allowStdin = false;
                                        } else if(allowStdin && "--progress".equals(arg)) {
                                            // Skipped option
                                        } else if(allowStdin && "-".equals(arg)) {
                                            transfer("-", System.in, out, buff, showProgress);
                                        } else {
                                            FileInputStream in = new FileInputStream(new File(arg));
                                            try {
                                                transfer(arg, in, out, buff, showProgress);
                                            } finally {
                                                in.close();
                                            }
                                        }
                                    }
                                }
                            } finally {
                                out.flush();
                                out.close();
                            }
                            retval = 0;
                        } catch(IOException err) {
                            System.err.println("IO Exception: "+err.toString());
                            System.err.flush();
                            retval = 5;
                        }
                    }
                }
            } catch(NumberFormatException err) {
                System.err.println("Unable to parse bits_per_second: "+err.toString());
                System.err.println();
                usage();
                retval = 2;
            }
        }
        System.exit(retval);
    }
}
