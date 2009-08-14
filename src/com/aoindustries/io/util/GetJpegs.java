/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2005, 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.io.util;

import java.io.*;

/**
 * Recovers JPG images from corrupted windows fat16 filesystems.  Assumes the files
 * are contiguous on the disk or disk image.
 *
 * @author  AO Industries, Inc.
 */
public class GetJpegs {

    public static final int BLOCK_SIZE=10; // TODO: Make 512
    public static final int BLOCK_COUNT=1000000; // TODO: Make 20480

    public static void main(String[] args) {
        if(args.length!=2) {
            System.err.println("Usage: GetJpegs <image_filename> <destination_directory>");
        } else {
            try {
                String imageFilename=args[0];
                String destinationDirectory=args[1];
                RandomAccessFile in=new RandomAccessFile(imageFilename, "r");
                try {
                    byte[] buff=new byte[BLOCK_SIZE];
                    int pos=0;
                    long fileLength=in.length();
                    for(int startPos=0;startPos<fileLength;startPos+=1) { // TODO: Make BLOCK_SIZE, not 1
                        in.seek(startPos);
                        in.readFully(buff, 0, BLOCK_SIZE);
                        if(
                            //buff[0]==0xFF
                            //&& buff[1]==0xD8
                            buff[5]==0x45
                            && buff[6]==0x45
                            && buff[7]==0x78
                            && buff[8]==0x69
                            && buff[9]==0x66
                        ) {
                            String filename=destinationDirectory+"/recovered_"+startPos+".jpg"; // TODO: Make startPos/512
                            System.out.println("Found file at "+startPos+", saving to "+filename);
                            FileOutputStream out=new FileOutputStream(filename);
                            try {
                                in.seek(startPos);
                                for(int c=0;c<BLOCK_COUNT;c++) {
                                    in.readFully(buff, 0, BLOCK_SIZE);
                                    out.write(buff, 0, BLOCK_SIZE);
                                }
                            } finally {
                                out.close();
                            }
                        }
                    }
                } finally {
                    in.close();
                }
            } catch(IOException err) {
                err.printStackTrace();
            }
        }
    }
}
