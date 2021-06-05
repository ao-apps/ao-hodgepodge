/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2007, 2008, 2009, 2013, 2016, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.hodgepodge.io;

import com.aoapps.lang.util.BufferManager;
import com.aoapps.lang.util.ErrorPrinter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;

/**
 * Times how long a counter block device takes to scan all counters.
 * Uses hard-coded 4096 byte sectors.
 *
 * @author  AO Industries, Inc.
 */
public class BenchmarkCounterBlockDevice {

	public BenchmarkCounterBlockDevice() {
	}

	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args) {
		try {
			if(args.length>0) {
				final byte[] buff = BufferManager.getBytes();
				try {
					for (String filename : args) {
						long startTime = System.currentTimeMillis();
						RandomAccessFile raf=new RandomAccessFile(filename, "r");
						long length=raf.length();
						try {
							for(long pos=1;pos<length;pos+=(1024*4096+4096)) {
								raf.seek(pos);
								raf.readFully(buff, 0, BufferManager.BUFFER_SIZE);
							}
						} finally {
							raf.close();
						}
						System.out.println(filename+" scanned in "+BigDecimal.valueOf(System.currentTimeMillis()-startTime, 3)+" seconds");
					}
				} finally {
					BufferManager.release(buff, false);
				}
			} else {
				System.err.println("Usage: BenchmarkCounterBlockDevice filename [filename] [...]");
			}
		} catch(IOException err) {
			ErrorPrinter.printStackTraces(err, System.err);
		}
	}
}
