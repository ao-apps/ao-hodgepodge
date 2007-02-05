package com.aoindustries.io;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * A <code>MultiFileInputStream</code> reads multiple <code>File</code>s as
 * if they were one contiguous file.
 *
 * @author  AO Industries, Inc.
 */
public class MultiFileInputStream extends InputStream {

    private final File[] files;
    private int nextFile=0;
    private FileInputStream in=null;

    public MultiFileInputStream(File[] files) {
        this.files=files;
    }

    synchronized public int available() throws IOException {
        if(in==null) {
            if(nextFile>=files.length) return 0;
            in=new FileInputStream(files[nextFile++]);
        }
        return in.available();
    }

    synchronized public void close() throws IOException {
        if(nextFile<files.length) nextFile=files.length;
        FileInputStream tempIn=in;
        if(tempIn!=null) {
            in=null;
            tempIn.close();
        }
    }

    synchronized public void mark(int readlimit) {
        try {
            if(in==null) {
                if(nextFile>=files.length) {
                    super.mark(readlimit);
                    return;
                }
                in=new FileInputStream(files[nextFile++]);
            }
            in.mark(readlimit);
        } catch(IOException err) {
            super.mark(readlimit);
        }
    }

    synchronized public boolean markSupported() {
        try {
            if(in==null) {
                if(nextFile>=files.length) return false;
                in=new FileInputStream(files[nextFile++]);
            }
            return in.markSupported();
        } catch(IOException err) {
            return false;
        }
    }

    synchronized public int read() throws IOException {
        if(in==null) {
            if(nextFile>=files.length) return -1;
            in=new FileInputStream(files[nextFile++]);
        }
        int value=-1;
        while(value==-1) {
            value=in.read();
            if(value==-1) {
                FileInputStream tempIn=in;
                in=null;
                tempIn.close();
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
        }
        return value;
    }
    
    synchronized public int read(byte[] buff) throws IOException {
        if(buff.length>0) {
            if(in==null) {
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
            int count=-1;
            while(count==-1) {
                count=in.read(buff);
                if(count==-1) {
                    FileInputStream tempIn=in;
                    in=null;
                    tempIn.close();
                    if(nextFile>=files.length) return -1;
                    in=new FileInputStream(files[nextFile++]);
                }
            }
            return count;
        }
        return 0;
    }
    
    synchronized public int read(byte[] buff, int off, int len) throws IOException {
        if(len>0) {
            if(in==null) {
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
            int count=-1;
            while(count==-1) {
                count=in.read(buff, off, len);
                if(count==-1) {
                    FileInputStream tempIn=in;
                    in=null;
                    tempIn.close();
                    if(nextFile>=files.length) return -1;
                    in=new FileInputStream(files[nextFile++]);
                }
            }
            return count;
        }
        return 0;
    }

    synchronized public void reset() throws IOException {
        if(in==null) {
            if(nextFile>=files.length) {
                super.reset();
                return;
            }
            in=new FileInputStream(files[nextFile++]);
        }
        in.reset();
    }

    synchronized public long skip(long n) throws IOException {
        if(n>0) {
            if(in==null) {
                if(nextFile>=files.length) return -1;
                in=new FileInputStream(files[nextFile++]);
            }
            long count=-1;
            while(count==-1) {
                count=in.skip(n);
                if(count==-1) {
                    FileInputStream tempIn=in;
                    in=null;
                    tempIn.close();
                    if(nextFile>=files.length) return -1;
                    in=new FileInputStream(files[nextFile++]);
                }
            }
            return count;
        }
        return 0;
    }
    
    /**
     * Gets the total number of bytes in all the files that will be read.
     */
    public long length() {
        long total=0;
        for(int c=0;c<files.length;c++) total+=files[c].length();
        return total;
    }
}