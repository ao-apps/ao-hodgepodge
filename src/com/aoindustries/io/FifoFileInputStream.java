package com.aoindustries.io;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * @see FifoFile
 *
 * @author  AO Industries, Inc.
 */
public class FifoFileInputStream extends InputStream {

    private final FifoFile file;
    private final Object statsLock=new Object();
    private long fifoReadCount=0;
    private long fifoReadBytes=0;

    FifoFileInputStream(FifoFile file) throws FileNotFoundException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FifoFileInputStream.class, "<init>(FifoFile)", null);
        try {
            this.file=file;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the number of reads performed on this stream.
     */
    public long getReadCount() {
        Profiler.startProfile(Profiler.FAST, FifoFileInputStream.class, "getReadCount()", null);
        try {
            synchronized(statsLock) {
                return fifoReadCount;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the number of bytes read from this stream.
     */
    public long getReadBytes() {
        Profiler.startProfile(Profiler.FAST, FifoFileInputStream.class, "getReadBytes()", null);
        try {
            synchronized(statsLock) {
                return fifoReadBytes;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds to the stats of this stream.
     */
    protected void addStats(long bytes) {
        Profiler.startProfile(Profiler.FAST, FifoFileInputStream.class, "addStats(long)", null);
        try {
            synchronized(statsLock) {
                fifoReadCount++;
                fifoReadBytes+=bytes;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Reads data from the file, blocks until the data is available.
     */
    public int read() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileInputStream.class, "read()", null);
        try {
            // Read from the queue
            synchronized(file) {
                while(true) {
                    long len=file.getLength();
                    if(len>=1) {
                        long pos=file.getFirstIndex();
                        file.file.seek(pos+16);
                        int b=file.file.read();
                        if(b==-1) throw new EOFException("Unexpected EOF");
                        addStats(1);
                        long newFirstIndex=pos+1;
                        while(newFirstIndex>=file.maxFifoLength) newFirstIndex-=file.maxFifoLength;
                        file.setFirstIndex(newFirstIndex);
                        file.setLength(len-1);
                        file.notify();
                        return b;
                    }
                    try {
                        file.wait();
                    } catch(InterruptedException err) {
                        InterruptedIOException ioErr=new InterruptedIOException();
                        ioErr.initCause(err);
                        throw ioErr;
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Reads data from the file, blocks until at least one byte is available.
     */
    public int read(byte[] b) throws IOException {
        Profiler.startProfile(Profiler.FAST, FifoFileInputStream.class, "read(byte[])", null);
        try {
            return read(b, 0, b.length);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Reads data from the file, blocks until at least one byte is available.
     */
    public int read(byte[] b, int off, int len) throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileInputStream.class, "read(byte[],int,int)", null);
        try {
            // Read from the queue
            synchronized(file) {
                while(true) {
                    long fileLen=file.getLength();
                    if(fileLen>=1) {
                        long pos=file.getFirstIndex();
                        file.file.seek(pos+16);
                        int readSize=fileLen>len?len:(int)fileLen;
                        // When at the end of the file, read the remaining bytes
                        if((pos+readSize)>file.maxFifoLength) readSize=(int)(file.maxFifoLength-pos);

                        // Read as many bytes as currently available
                        int totalRead=file.file.read(b, off, readSize);
                        if(totalRead==-1) throw new EOFException("Unexpected EOF");
                        addStats(totalRead);
                        long newFirstIndex=pos+totalRead;
                        while(newFirstIndex>=file.maxFifoLength) newFirstIndex-=file.maxFifoLength;
                        file.setFirstIndex(newFirstIndex);
                        file.setLength(fileLen-totalRead);
                        file.notify();
                        return totalRead;
                    }
                    try {
                        file.wait();
                    } catch(InterruptedException err) {
                        InterruptedIOException ioErr=new InterruptedIOException();
                        ioErr.initCause(err);
                        throw ioErr;
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
    
    /**
     * Skips data in the queue, blocks until at least one byte is skipped.
     */
    public long skip(long n) throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileInputStream.class, "skip(long)", null);
        try {
            // Skip in the queue
            synchronized(file) {
                while(true) {
                    long fileLen=file.getLength();
                    if(fileLen>=1) {
                        long pos=file.getFirstIndex();
                        long skipSize=fileLen>n?n:fileLen;
                        // When at the end of the file, skip the remaining bytes
                        if((pos+skipSize)>file.maxFifoLength) skipSize=file.maxFifoLength-pos;

                        // Skip as many bytes as currently available
                        long totalSkipped=skipSize;
                        long newFirstIndex=pos+skipSize;
                        while(newFirstIndex>=file.maxFifoLength) newFirstIndex-=file.maxFifoLength;
                        file.setFirstIndex(newFirstIndex);
                        file.setLength(fileLen-skipSize);
                        file.notify();
                        return totalSkipped;
                    }
                    try {
                        file.wait();
                    } catch(InterruptedException err) {
                        InterruptedIOException ioErr=new InterruptedIOException();
                        ioErr.initCause(err);
                        throw ioErr;
                    }
                }
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Determines the number of bytes that may be read without blocking.
     */
    public int available() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileInputStream.class, "available()", null);
        try {
            synchronized(file) {
                long len=file.getLength();
                return len>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)len;
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @see  FifoFile#close()
     */
    public void close() throws IOException {
        Profiler.startProfile(Profiler.FAST, FifoFileInputStream.class, "close()", null);
        try {
            file.close();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}
