/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2016, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.collections.AoArrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Copies one file to another, only writing the blocks of the destination
 * file if they either didn't already exist or contain different content.
 * This is to update flash media where reads are much faster than reads.
 *
 * @author  AO Industries, Inc.
 */
public class SyncFile {

	/**
	 * Debug flags.
	 */
	private static final boolean DEBUG = true;
	private static final boolean DRY_RUN = false;

	private static final int BLOCK_SIZE = 1048576;

	public SyncFile() {
	}

	public static void main(String[] args) {
		if(args.length!=2) {
			System.err.println("usage: "+SyncFile.class.getName()+" <from> <to>");
			System.exit(1);
		} else {
			try {
				long bytesWritten;
				File from = new File(args[0]);
				File to = new File(args[1]);
				if(DEBUG) System.err.println("Opening " + from);
				FileInputStream in = new FileInputStream(from);
				try {
					if(DEBUG) System.err.println("Opening " + to);
					RandomAccessFile out = new RandomAccessFile(to, DRY_RUN ? "r" : "rw");
					try {
						bytesWritten = syncFile(in, out);
					} finally {
						if(DEBUG) System.err.println("Closing " + to);
						out.close();
					}
				} finally {
					if(DEBUG) System.err.println("Closing " + from);
					in.close();
				}
				if(DEBUG) System.err.println("Wrote " + bytesWritten + " bytes");
			} catch(IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Synchronized the input to the provided output, only writing data that
	 * doesn't already match the input.
	 * Returns the number of bytes written.
	 */
	public static long syncFile(InputStream in, RandomAccessFile out) throws IOException {
		byte[] inBuff = new byte[BLOCK_SIZE];
		byte[] outBuff = new byte[BLOCK_SIZE];
		long pos = 0;
		long bytesWritten = 0;
		int numBytes;
		while((numBytes=in.read(inBuff, 0, BLOCK_SIZE))!=-1) {
			if(DEBUG) System.err.println(pos+": Read " + numBytes + " bytes of input");
			out.seek(pos);
			long blockEnd = pos + numBytes;
			if(out.length()>=blockEnd) {
				// Read block from out
				if(DEBUG) System.err.println(pos+": Reading " + numBytes + " bytes of output");
				out.readFully(outBuff, 0, numBytes);
				if(!AoArrays.equals(inBuff, outBuff, 0, numBytes)) {
					if(DEBUG) System.err.println(pos+": Updating " + numBytes + " bytes of output");
					out.seek(pos);
					if(!DRY_RUN) out.write(inBuff, 0, numBytes);
					bytesWritten += numBytes;
				} else {
					if(DEBUG) System.err.println(pos+": Data matches, not writing");
				}
			} else {
				// At end, write entire block
				if(DEBUG) System.err.println(pos+": Appending " + numBytes +" bytes to output");
				if(!DRY_RUN) out.write(inBuff, 0, numBytes);
				bytesWritten += numBytes;
			}
			pos = blockEnd;
		}
		if(out.length()!=pos) {
			if(DEBUG) System.err.println(pos+": Truncating output to " + pos + " bytes");
			assert out.length()>pos;
			if(!DRY_RUN) {
				try {
					out.setLength(pos);
				} catch(IOException e) {
					System.err.println("Warning: Unable to truncate output to " + pos +" bytes");
				}
			}
		}
		return bytesWritten;
	}
}
