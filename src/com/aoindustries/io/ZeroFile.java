/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2013, 2014  AO Industries, Inc.
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
import com.aoindustries.util.StringUtility;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Zeros-out a file, only writing the blocks of the destination that contain any
 * non-zero value.  This is to clear flash media with minimal writes.
 *
 * @author  AO Industries, Inc.
 */
public class ZeroFile {

    /**
     * Flags - these should become commandline switches.
     */
    private static final boolean DEBUG = false;
    private static final boolean PROGRESS = true;
    private static final boolean DRY_RUN = false;

    /**
     * Must be power of two.
     */
    private static final int BLOCK_SIZE = 1048576;

    public ZeroFile() {
    }

    public static void main(String[] args) {
        if(args.length!=2) {
            System.err.println("usage: "+ZeroFile.class.getName()+" <mb_per_sec>[/<mb_per_sec_write>] <path>");
            System.exit(1);
        } else {
            try {
				int bpsIn, bpsOut;
				String bpsArg = args[0];
				int slashPos = bpsArg.indexOf('/');
				if(slashPos == -1) {
					bpsIn = bpsOut = Integer.parseInt(bpsArg);
				} else {
					bpsIn = Integer.parseInt(bpsArg.substring(0, slashPos));
					bpsOut = Integer.parseInt(bpsArg.substring(slashPos+1));
				}
                long bytesWritten;
                File file = new File(args[1]);
                if(DEBUG) System.err.println("Opening " + file);
                RandomAccessFile raf = new RandomAccessFile(file, DRY_RUN ? "r" : "rw");
                try {
                    bytesWritten = zeroFile(bpsIn, bpsOut, raf);
                } finally {
                    if(DEBUG) System.err.println("Closing " + file);
                    raf.close();
                }
                if(DEBUG) System.err.println("Wrote " + bytesWritten + " bytes");
            } catch(IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private static long sleep(int bps, long lastTime) throws IOException {
        try {
            long millisPerBlock = 1000 / bps;
            long sleepUntil = lastTime + millisPerBlock;
            long currentTime = System.currentTimeMillis();
            long sleepyTime = sleepUntil - currentTime;
            if(sleepyTime<=0) {
                // IO too slow or system time set to future
                return currentTime;
            } if(sleepyTime>millisPerBlock) {
                // System time set to past
                Thread.sleep(millisPerBlock);
                return currentTime + millisPerBlock;
            } else {
                // Normal case
                Thread.sleep(sleepyTime);
                return currentTime + sleepyTime;
            }
        } catch(InterruptedException e) {
            InterruptedIOException ioExc = new InterruptedIOException(e.getMessage());
            ioExc.initCause(e);
            throw ioExc;
        }
    }

	/**
     * Zeroes the provided random access file, only writing blocks that contain non-zero.
	 * Reads at the maximum provided bpsIn blocks per second.
	 * Writes at the maximum provided bpsOut blocks per second.
     * Returns the number of bytes written.
     */
    public static long zeroFile(int bpsIn, int bpsOut, RandomAccessFile raf) throws IOException {
        // Initialize bitset
        final long len = raf.length();
        final int blocks;
        {
            long blocksLong = len / BLOCK_SIZE;
            if((len&(BLOCK_SIZE-1))!=0) blocksLong++;
            if(blocksLong>Integer.MAX_VALUE) throw new IOException("File too large: " + len);
            blocks = (int)blocksLong;
        }
        BitSet dirtyBlocks = new BitSet(blocks);
		int numDirtyBlocks = 0;
        // Pass one: read for non zeros
        long lastTime = System.currentTimeMillis();
        byte[] buff = new byte[BLOCK_SIZE];
        int blockIndex = 0;
		String lastVerboseString = "";
		int block = 0;
        for(long pos=0; pos<len; pos+=BLOCK_SIZE, blockIndex++) {
            int blockSize;
            {
                long blockSizeLong = len-pos;
                blockSize = blockSizeLong>BLOCK_SIZE ? BLOCK_SIZE : (int)blockSizeLong;
            }
            raf.seek(pos);
            raf.readFully(buff, 0, blockSize);
			block++;
            lastTime = sleep(bpsIn, lastTime);
            boolean allZero = true;
            for(int i=0; i<blockSize; i++) {
                if(buff[i]!=0) {
                    allZero = false;
                    break;
                }
            }
            if(!allZero) {
				dirtyBlocks.set(blockIndex);
				numDirtyBlocks++;
			}
            if(PROGRESS) {
				lastVerboseString = TerminalWriter.progressOutput(
					lastVerboseString,
					StringUtility.getApproximateSize(pos+blockSize)
					+ ": "
					+ SQLUtility.getDecimal((long)block * 10000L / (long)blocks)
					+ "% read, "
					+ SQLUtility.getDecimal((long)numDirtyBlocks * 10000L / (long)block)
					+ "% dirty",
					System.err
				);
				//System.err.println("0x"+Long.toString(pos, 16)+"-0x"+Long.toString(pos+blockSize-1, 16)+": "+(allZero ? "Already zero" : "Dirty"));
			}
        }
		if(PROGRESS) {
			System.err.println();
			lastVerboseString = "";
		}
        // Pass two: write dirty blocks
        long bytesWritten = 0;
        blockIndex = 0;
        Arrays.fill(buff, (byte)0);
		int written = 0;
        for(long pos=0; pos<len; pos+=BLOCK_SIZE, blockIndex++) {
            if(dirtyBlocks.get(blockIndex)) {
                int blockSize;
                {
                    long blockSizeLong = len-pos;
                    blockSize = blockSizeLong>BLOCK_SIZE ? BLOCK_SIZE : (int)blockSizeLong;
                }
                if(!DRY_RUN) {
                    raf.seek(pos);
                    raf.write(buff, 0, blockSize);
                    lastTime = sleep(bpsOut, lastTime);
                    bytesWritten += blockSize;
                }
				written++;
                if(PROGRESS) {
					lastVerboseString = TerminalWriter.progressOutput(
						lastVerboseString,
						StringUtility.getApproximateSize(bytesWritten)
						+ ": "
						+ SQLUtility.getDecimal((long)written * 10000L / (long)numDirtyBlocks)
						+ "% written",
						System.err
					);
					// System.err.println("0x"+Long.toString(pos, 16)+"-0x"+Long.toString(pos+blockSize-1, 16)+": Cleared");
				}
            }
        }
		if(PROGRESS) {
			System.err.println();
			lastVerboseString = "";
		}
        return bytesWritten;
    }
}
