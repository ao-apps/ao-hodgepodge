package com.aoindustries.io.util;

/*
 * Copyright 2005-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * Recovers JPG images from corrupted windows fat16 filesystems.  Assumes the files
 * are contiguous on the disk or disk image.
 *
 * @author  AO Industries, Inc.
 */
public class GetJpegs {

    public static final int BLOCK_SIZE=512;
    public static final int BLOCK_COUNT=20480;

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
                    for(int startPos=0;startPos<fileLength;startPos+=BLOCK_SIZE) {
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
                            String filename=destinationDirectory+"/recovered_"+(startPos/512)+".jpg";
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