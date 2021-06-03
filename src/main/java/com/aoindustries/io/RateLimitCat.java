/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013, 2016, 2019, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.io;

import com.aoindustries.lang.Strings;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.GetOpt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
		System.err.println("Usage: "+RateLimitCat.class.getName()+" [--blocksize=BLOCK_SIZE[unit]] [--bwlimit=BANDWIDTH_LIMIT[unit]] [--limit=MAXIMUM_BYTES[unit]] [--output=OUTPUT_FILE] [--progress[={true|false}]] [--] [FILE]...");
		System.err.println();
		System.err.println("\tWhen FILE is not provided, reads from standard input.");
		System.err.println("\tWhen FILE is -, will read from standard input.");
		System.err.println("\tWhen -- is first found, all subsequent arguments will be treated as filenames, including any - and --.");
		System.err.println();
		System.err.println("\t--blocksize  Reads and writes at most BLOCK_SIZE bytes at a time.  Allows an optional unit.");
		System.err.println("\t             If not provided, defaults to "+BufferManager.BUFFER_SIZE+" bytes.  Unit supports IEC_60027");
		System.err.println("\t             prefixes on the unit byte, such as kbyte for 1000 bytes or Kibyte for 1024 bytes.");
		System.err.println("\t--bwlimit    The maximum bandwidth for reads and writes.  Allows an optional unit.");
		System.err.println("\t             If not provided, defaults to bits per second.  Unit supports IEC_60027");
		System.err.println("\t             prefixes on the units bit or byte, such as kbyte for 1000 bytes per second");
		System.err.println("\t             or Kibit for 1024 bits per second.");
		System.err.println("\t--limit      Reads and writes at most MAXIMUM_BYTES bytes.  Allows an optional unit.");
		System.err.println("\t             If not provided, defaults to bytes.  Unit supports IEC_60027 prefixes");
		System.err.println("\t             on the unit byte, such as kbyte for 1000 bytes or Kibyte for 1024 bytes.");
		System.err.println("\t--output     Writes the output to OUTPUT_FILE instead of standard output.");
		System.err.println("\t--progress   Displays the progress of the files once every 60 seconds.");
		System.err.flush();
	}

	private static void report(String filename, long byteCount, long timespan, long[] bytesRemaining) {
		System.err.print(filename);
		System.err.print(": ");
		System.err.print(byteCount);
		System.err.print(" bytes (");
		System.err.print(Strings.getApproximateSize(byteCount));
		System.err.print(") transferred in ");
		System.err.print(BigDecimal.valueOf(timespan, 3));
		System.err.print(" seconds (");
		System.err.print(Strings.getTimeLengthString(timespan));
		System.err.print(')');
		if(bytesRemaining!=null && bytesRemaining[0]>0) {
			System.err.print(", ");
			System.err.print(bytesRemaining[0]);
			System.err.print(" bytes (");
			System.err.print(Strings.getApproximateSize(bytesRemaining[0]));
			System.err.print(") remaining");
		}
		System.err.println();
		System.err.flush();
	}

	private static void transfer(String filename, InputStream in, OutputStream out, byte[] buff, boolean progress, long[] bytesRemaining) throws IOException {
		long startTime = System.currentTimeMillis();
		long lastReportByteCount = -1;
		long lastReportTime = startTime;
		long byteCount = 0;
		try {
			while(true) {
				int blockSize = buff.length;
				if(bytesRemaining!=null) {
					if(bytesRemaining[0]<=0) break;
					if(bytesRemaining[0]<blockSize) blockSize = (int)bytesRemaining[0];
				}
				int bytesRead=in.read(buff, 0, blockSize);
				if(bytesRead==-1) break; // End of file
				out.write(buff, 0, bytesRead);
				byteCount += bytesRead;
				if(bytesRemaining!=null) bytesRemaining[0] -= bytesRead;
				if(progress && lastReportByteCount!=byteCount) {
					long currentTime = System.currentTimeMillis();
					long timeSinceReport = currentTime - lastReportTime;
					if(timeSinceReport<0) {
						// System time reset
						lastReportTime = currentTime;
					} else if(timeSinceReport>=60000) {
						report(filename, byteCount, currentTime - startTime, bytesRemaining);
						lastReportByteCount = byteCount;
						lastReportTime = currentTime;
					}
				}
			}
		} finally {
			if(progress && lastReportByteCount!=byteCount) report(filename, byteCount, System.currentTimeMillis() - startTime, bytesRemaining);
		}
	}

	public static void main(String[] args) {
		int retval = 0;
		try {
			// Parse the arguments - set retval!=0 on error
			ByteCount blocksizeParam = GetOpt.getOpt(args, "blocksize", ByteCount.class);
			final int blockSize;
			if(blocksizeParam==null) blockSize = BufferManager.BUFFER_SIZE;
			else {
				long longBlockSize = blocksizeParam.getByteCount();
				if(longBlockSize>Integer.MAX_VALUE) throw new IllegalArgumentException("blocksize>Integer.MAX_VALUE: "+blocksizeParam);
				if(longBlockSize<1) throw new IllegalArgumentException("blocksize<1: "+blocksizeParam);
				blockSize = (int)longBlockSize;
			}
			final BitRate bwlimit = GetOpt.getOpt(args, "bwlimit", BitRate.class);
			if(bwlimit!=null && bwlimit.getBitRate()<1) throw new IllegalArgumentException("bwlimit<1: "+bwlimit);
			ByteCount limit = GetOpt.getOpt(args, "limit", ByteCount.class);
			long[] bytesRemaining;
			if(limit==null) bytesRemaining = null;
			else {
				long temp = limit.getByteCount();
				if(temp<0) throw new IllegalArgumentException("limit<0: "+limit);
				bytesRemaining = new long[] {temp};
			}
			File output = GetOpt.getOpt(args, "output", File.class); // null for standard output
			Boolean progressParam = GetOpt.getOpt(args, "progress", Boolean.TYPE);
			boolean progress = progressParam!=null ? progressParam : false;
			List<String> sourcePaths = GetOpt.getArguments(args);
			List<File> sourceFiles = new ArrayList<>(sourcePaths.size()+1);
			boolean allowStdin = true;
			boolean hasError = false;
			for(String sourcePath : sourcePaths) {
				if(allowStdin && "--".equals(sourcePath)) allowStdin = false;
				else if(allowStdin && "-".equals(sourcePath)) sourceFiles.add(null);
				else {
					File sourceFile = new File(sourcePath);
					if(!sourceFile.exists()) {
						System.err.println("File not found: "+sourcePath);
						hasError = true;
					} else if(sourceFile.isDirectory()) {
						System.err.println("Directories not supported: "+sourcePath);
						hasError = true;
					} else if(!sourceFile.canRead()) {
						System.err.println("Unable to read file: "+sourcePath);
						hasError = true;
					} else {
						sourceFiles.add(sourceFile);
					}
				}
			}
			if(hasError) {
				System.err.flush();
				retval = 2;
			} else {
				if(sourceFiles.isEmpty()) sourceFiles.add(null); // Use System.in when no files specified
				try {
					OutputStream out = output==null ? new PrintStreamOutputStream(System.out) : new FileOutputStream(output);
					try {
						if(bwlimit!=null) out = new BitRateOutputStream(
							out,
							new BitRateProvider() {
								@Override
								public Long getBitRate() {
									return bwlimit.getBitRate();
								}
								@Override
								public int getBlockSize() {
									return blockSize;
								}
							}
						);
						byte[] buff = new byte[blockSize];
						for(File sourceFile : sourceFiles) {
							if(bytesRemaining!=null && bytesRemaining[0]<=0) break;
							if(sourceFile==null) {
								transfer("-", System.in, out, buff, progress, bytesRemaining);
							} else {
								try (InputStream in = new FileInputStream(sourceFile)) {
									transfer(sourceFile.getPath(), in, out, buff, progress, bytesRemaining);
								}
							}
						}
					} finally {
						out.flush();
						out.close();
					}
				} catch(IOException err) {
					System.err.println("IO Exception: "+err.toString());
					System.err.flush();
					retval = 3;
				}
			}
		} catch(IllegalArgumentException err) {
			System.err.println("Illegal Argument: "+err.toString());
			System.err.flush();
			usage();
			retval = 1;
		}
		System.exit(retval);
	}
}
