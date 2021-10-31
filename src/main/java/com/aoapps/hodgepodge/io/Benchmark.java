/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.hodgepodge.io;

import com.aoapps.lang.Strings;
import com.aoapps.lang.math.Statistics;
import com.aoapps.lang.util.BufferManager;
import com.aoapps.lang.util.ErrorPrinter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple disk concurrency scalability benchmark.  Pass in one or more parameters indicating the files or devices to test, such as <code>/dev/md0</code>
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class Benchmark {

	private static final long MAX_READ_BYTES = 4L * 1024L * 1024L * 1024L;

	private static final int[] blockSizes = {512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576};
	private static final int[] concurrencies = {1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64};

	private Benchmark() {
	}

	private static void benchmark(int pass, String[] args, List<List<List<Double>>> throughputs, List<List<List<Double>>> seekRates, NumberFormat numberFormat) {
		System.out.print("Pass #");
		System.out.println(pass);
		System.out.println();
		for(int c=1;c<args.length;c++) {
			List<List<Double>> fileThroughputs = throughputs.get(c-1);
			List<List<Double>> fileSeekRates = seekRates.get(c-1);
			final String filename=args[c];
			final File file = new File(filename);
			System.out.print("Sequential Read by Block Size (");
			System.out.print(Strings.getApproximateSize(MAX_READ_BYTES));
			System.out.println(" Total)");
			for(int ci=0; ci<blockSizes.length; ci++) {
				List<Double> blockSizeThroughputs = fileThroughputs.get(ci);
				int blockSize = blockSizes[ci];
				byte[] buff = new byte[blockSize];
				long bytesRead = 0;
				long startTime = System.currentTimeMillis();
				try {
					try (InputStream in = new FileInputStream(file)) {
						while(bytesRead<MAX_READ_BYTES) {
							long bytesLeft = MAX_READ_BYTES - bytesRead;
							int len = bytesLeft < blockSize ? (int)bytesLeft : blockSize;
							int ret = in.read(buff, 0, len);
							if(ret==-1) break;
							bytesRead += ret;
						}
					}
				} catch(IOException err) {
					ErrorPrinter.printStackTraces(err, System.err);
				} finally {
					long timeSpan = System.currentTimeMillis() - startTime;
					if(bytesRead!=MAX_READ_BYTES) System.err.println("Incorrect number of bytes read.  Expected "+MAX_READ_BYTES+", got "+bytesRead);
					double throughput = (double)bytesRead*1000D/((double)timeSpan*1048576D);
					blockSizeThroughputs.add(throughput);
					System.out.print("    ");
					System.out.print(Strings.getApproximateSize(blockSize));
					System.out.print(": ");
					System.out.print(numberFormat.format(throughput));
					System.out.print(" MB/sec");
					if(blockSizeThroughputs.size()>1) {
						System.out.print(" (");
						System.out.print(numberFormat.format(Collections.min(blockSizeThroughputs)));
						System.out.print(", ");
						double mean = Statistics.mean(blockSizeThroughputs);
						System.out.print(numberFormat.format(mean));
						System.out.print('±');
						System.out.print(numberFormat.format(Statistics.standardDeviation(mean, blockSizeThroughputs)));
						System.out.print(", ");
						System.out.print(numberFormat.format(Collections.max(blockSizeThroughputs)));
						System.out.print(')');
					}
					System.out.println();
				}
			}

			System.out.println("Random Seek by Concurrency (4 kB Blocks)");
			final byte[] buff = BufferManager.getBytes();
			try {
				for(int ci=0;ci<concurrencies.length;ci++) {
					List<Double> concurrencySeekRates = fileSeekRates.get(ci);
					int concurrency=concurrencies[ci];
					final int[] counter=new int[1];
					Thread[] threads=new Thread[concurrency];
					for(int d=0;d<concurrency;d++) {
						Thread t = threads[d] = new Thread() {
							@Override
							public void run() {
								int count=0;
								try {
									RandomAccessFile raf=new RandomAccessFile(file, "r");
									long length=raf.length();
									try {
										long endTime = System.currentTimeMillis() + (30L * 1000);
										while(System.currentTimeMillis()<endTime) {
											raf.seek((long)(Math.random()*(length-4096)));
											raf.read(buff, 0, 4096);
											count++;
										}
									} finally {
										raf.close();
									}
								} catch(IOException err) {
									ErrorPrinter.printStackTraces(err, System.err);
								}
								synchronized(counter) {
									counter[0]+=count;
								}
							}
						};
						t.start();
					}
					for(int d = 0; d < concurrency; d++) {
						try {
							threads[d].join();
						} catch(InterruptedException err) {
							ErrorPrinter.printStackTraces(err, System.err);
							// Restore the interrupted status
							Thread.currentThread().interrupt();
							return;
						}
					}
					double seekRate = (double)counter[0]/30L;
					concurrencySeekRates.add(seekRate);
					System.out.print("    ");
					System.out.print(concurrency);
					System.out.print(": ");
					System.out.print(numberFormat.format(seekRate));
					System.out.print(" seeks/sec");
					if(concurrencySeekRates.size()>1) {
						System.out.print(" (");
						System.out.print(numberFormat.format(Collections.min(concurrencySeekRates)));
						System.out.print(", ");
						double mean = Statistics.mean(concurrencySeekRates);
						System.out.print(numberFormat.format(mean));
						System.out.print('±');
						System.out.print(numberFormat.format(Statistics.standardDeviation(mean, concurrencySeekRates)));
						System.out.print(", ");
						System.out.print(numberFormat.format(Collections.max(concurrencySeekRates)));
						System.out.print(')');
					}
					System.out.println();
					//System.out.println("    "+concurrency+": "+seekRate+" seeks/sec, "+SQLUtility.formatDecimal2((int)(1000000d*concurrency/counter[0]))+" ms/seek");
				}
			} finally {
				BufferManager.release(buff, false);
			}
		}
	}

	public static void main(String[] args) {
		if(args.length>=2) {
			NumberFormat numberFormat = NumberFormat.getNumberInstance();
			numberFormat.setMinimumFractionDigits(3);
			numberFormat.setMaximumFractionDigits(3);
			int numPasses = Integer.parseInt(args[0]);
			int numFiles = args.length-1;
			List<List<List<Double>>> throughputs = new ArrayList<>(numFiles);
			List<List<List<Double>>> seekRates = new ArrayList<>(numFiles);
			for(int c=0; c<numFiles; c++) {
				List<List<Double>> fileThroughputs = new ArrayList<>(blockSizes.length);
				for(int d = 0; d < blockSizes.length; d++) {
					fileThroughputs.add(new ArrayList<>(numPasses));
				}
				throughputs.add(fileThroughputs);
				List<List<Double>> fileSeekRates = new ArrayList<>(concurrencies.length);
				for(int d = 0; d < concurrencies.length; d++) {
					fileSeekRates.add(new ArrayList<>(numPasses));
				}
				seekRates.add(fileSeekRates);
			}
			for(int pass=1; pass<=numPasses; pass++) {
				if(pass>1) System.out.println();
				benchmark(pass, args, throughputs, seekRates, numberFormat);
			}
		} else {
			System.err.println("Usage: Benchmark num_passes filename [filename] [...]");
			System.exit(1);
		}
	}
}
