package com.aoindustries.io;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import java.io.*;

/**
 * @see FifoFile
 *
 * @author  AO Industries, Inc.
 */
public class FifoFileOutputStream extends OutputStream {

    private final FifoFile file;
    private final Object statsLock=new Object();
    private long fifoWriteCount=0;
    private long fifoWriteBytes=0;

    FifoFileOutputStream(FifoFile file) throws FileNotFoundException {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FifoFileOutputStream.class, "<init>(FifoFile)", null);
        try {
            this.file=file;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Gets the number of writes performed on this stream.
     */
    public long getWriteCount() {
        Profiler.startProfile(Profiler.FAST, FifoFileOutputStream.class, "getWriteCount()", null);
        try {
            synchronized(statsLock) {
                return fifoWriteCount;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Gets the number of bytes written to this stream.
     */
    public long getWriteBytes() {
        Profiler.startProfile(Profiler.FAST, FifoFileOutputStream.class, "getWriteBytes()", null);
        try {
            synchronized(statsLock) {
                return fifoWriteBytes;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    /**
     * Adds to the stats of this stream.
     */
    protected void addStats(long bytes) {
        Profiler.startProfile(Profiler.FAST, FifoFileOutputStream.class, "addStats(long)", null);
        try {
            synchronized(statsLock) {
                fifoWriteCount++;
                fifoWriteBytes+=bytes;
            }
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void write(int b) throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileOutputStream.class, "write(int)", null);
        try {
            // Write to the queue
            synchronized(file) {
                while(true) {
                    long len=file.getLength();
                    if(len<file.maxFifoLength) {
                        long pos=file.getFirstIndex()+len;
                        while(pos>=file.maxFifoLength) pos-=file.maxFifoLength;
                        file.file.seek(pos+16);
                        file.file.write(b);
                        addStats(1);
                        file.setLength(len+1);
                        file.notify();
                        return;
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

    public void write(byte[] b) throws IOException {
        Profiler.startProfile(Profiler.FAST, FifoFileOutputStream.class, "write(byte[])", null);
        try {
            // Write to the queue
            write(b, 0, b.length);
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileOutputStream.class, "write(byte[],int,int)", null);
        try {
            // Write blocks until all bytes have been written
            while(len>0) {
                // Write to the queue
                synchronized(file) {
                    while(true) {
                        long fileLen=file.getLength();
                        long maxBlockSize=file.maxFifoLength-fileLen;
                        if(maxBlockSize>0) {
                            long pos=file.getFirstIndex()+fileLen;
                            while(pos>=file.maxFifoLength) pos-=file.maxFifoLength;
                            int blockSize=maxBlockSize>len?len:(int)maxBlockSize;
                            // When at the end of the file, write the remaining bytes
                            if((pos+blockSize)>file.maxFifoLength) blockSize=(int)(file.maxFifoLength-pos);
                            file.file.seek(pos+16);
                            file.file.write(b, off, blockSize);
                            addStats(blockSize);
                            file.setLength(fileLen+blockSize);
                            off+=blockSize;
                            len-=blockSize;
                            file.notify();
                            break;
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
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Flushes all updates to this file to the underlying storage device.  This is performed by
     * <code>RandomAccessFile.getChannel().force(true)</code>.
     */
    public void flush() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileOutputStream.class, "flush()", null);
        try {
            file.file.getChannel().force(true);
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Gets the number of bytes that may be written before the
     * queue is full and blocks writes.
     */
    public long available() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFileOutputStream.class, "available()", null);
        try {
            synchronized(file) {
                return file.maxFifoLength-file.getLength();
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * @see  FifoFile#close()
     */
    public void close() throws IOException {
        Profiler.startProfile(Profiler.FAST, FifoFileOutputStream.class, "close()", null);
        try {
            file.close();
        } finally {
            Profiler.endProfile(Profiler.FAST);
        }
    }
}
