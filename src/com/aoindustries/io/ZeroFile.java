/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012  AO Industries, Inc.
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

import java.io.File;
import java.io.IOException;
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
     * Debug flags.
     */
    private static final boolean DEBUG = true;
    private static final boolean DRY_RUN = false;

    /**
     * Must be power of two.
     */
    private static final int BLOCK_SIZE = 1048576;

    public ZeroFile() {
    }

    public static void main(String[] args) {
        if(args.length!=1) {
            System.err.println("usage: "+ZeroFile.class.getName()+" <path>");
            System.exit(1);
        } else {
            try {
                long bytesWritten;
                File file = new File(args[0]);
                if(DEBUG) System.err.println("Opening " + file);
                RandomAccessFile raf = new RandomAccessFile(file, DRY_RUN ? "r" : "rw");
                try {
                    bytesWritten = zeroFile(raf);
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

    /**
     * Zeroes the provided random access file, only writing blocks that contain
     * non-zero.
     * Returns the number of bytes written.
     */
    public static long zeroFile(RandomAccessFile raf) throws IOException {
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
        // Pass one: read for non zeros
        byte[] buff = new byte[BLOCK_SIZE];
        int blockIndex = 0;
        for(long pos=0; pos<len; pos+=BLOCK_SIZE, blockIndex++) {
            int blockSize;
            {
                long blockSizeLong = len-pos;
                blockSize = blockSizeLong>BLOCK_SIZE ? BLOCK_SIZE : (int)blockSizeLong;
            }
            raf.seek(pos);
            raf.readFully(buff, 0, blockSize);
            boolean allZero = true;
            for(int i=0; i<blockSize; i++) {
                if(buff[i]!=0) {
                    allZero = false;
                    break;
                }
            }
            if(!allZero) dirtyBlocks.set(blockIndex);
            if(DEBUG) System.err.println("0x"+Long.toString(pos, 16)+"-0x"+Long.toString(pos+blockSize-1, 16)+": "+(allZero ? "Already zero" : "Dirty"));
        }
        // Pass two: write dirty blocks
        long bytesWritten = 0;
        blockIndex = 0;
        Arrays.fill(buff, (byte)0);
        for(long pos=0; pos<len; pos+=BLOCK_SIZE, blockIndex++) {
            if(dirtyBlocks.get(blockIndex)) {
                int blockSize;
                {
                    long blockSizeLong = len-pos;
                    blockSize = blockSizeLong>BLOCK_SIZE ? BLOCK_SIZE : (int)blockSizeLong;
                }
                if(DEBUG) System.err.println("0x"+Long.toString(pos, 16)+"-0x"+Long.toString(pos+blockSize-1, 16)+": Clearing");
                if(!DRY_RUN) {
                    raf.seek(pos);
                    raf.write(buff, 0, blockSize);
                    bytesWritten += blockSize;
                }
            }
        }
        return bytesWritten;
    }
}
