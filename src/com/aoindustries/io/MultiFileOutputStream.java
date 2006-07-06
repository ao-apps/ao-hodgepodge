package com.aoindustries.io;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;
import java.util.*;

/**
 * A <code>MultiFileOutputStream</code> writes to multiple <code>File</code>s as
 * if they were one contiguous file.
 *
 * @author  AO Industries, Inc.
 */
public class MultiFileOutputStream extends OutputStream {

    public static final long DEFAULT_FILE_SIZE=(long)1024*1024*1024;

    private final File parent;
    private final String prefix;
    private final String suffix;
    private final long fileSize;

    private List<File> files=new ArrayList<File>();
    private FileOutputStream out=null;
    private long bytesOut=0;

    public MultiFileOutputStream(File parent, String prefix, String suffix) {
        this(parent, prefix, suffix, DEFAULT_FILE_SIZE);
    }

    public MultiFileOutputStream(File parent, String prefix, String suffix, long fileSize) {
        this.parent=parent;
        this.prefix=prefix;
        this.suffix=suffix;
        this.fileSize=fileSize;
    }

    synchronized public void close() throws IOException {
        FileOutputStream tempOut=out;
        if(tempOut!=null) {
            out=null;
            tempOut.flush();
            tempOut.close();
        }
    }
    
    synchronized public void flush() throws IOException {
        if(out!=null) out.flush();
    }
    
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }
    
    synchronized public void write(byte[] b, int off, int len) throws IOException {
        while(off<len) {
            if(out==null) makeNewFile();
            int blockLen=len;
            long newBytesOut=bytesOut+blockLen;
            if(newBytesOut>fileSize) {
                blockLen=(int)(fileSize-newBytesOut);
            }
            out.write(b, off, blockLen);
            off+=blockLen;
            len-=blockLen;
            bytesOut+=blockLen;
            if(bytesOut>=fileSize) {
                FileOutputStream tempOut=out;
                out=null;
                tempOut.flush();
                tempOut.close();
            }
        }
    }

    synchronized public void write(int b) throws IOException {
        out.write(b);
        bytesOut+=1;
        if(bytesOut>=fileSize) {
            FileOutputStream tempOut=out;
            out=null;
            tempOut.flush();
            tempOut.close();
        }
    }

    /**
     * All accesses are already synchronized.
     */
    private void makeNewFile() throws IOException {
        String filename=prefix+(files.size()+1)+suffix;
        File file=new File(parent, filename);
        out=new FileOutputStream(file);
        bytesOut=0;
        files.add(file);
    }
    
    public File[] getFiles() {
        File[] FA=new File[files.size()];
        files.toArray(FA);
        return FA;
    }
}
