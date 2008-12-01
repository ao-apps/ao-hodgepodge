package com.aoindustries.io;

/*
 * Copyright 2004-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

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

    FifoFileOutputStream(FifoFile file) {
        this.file=file;
    }

    /**
     * Gets the number of writes performed on this stream.
     */
    public long getWriteCount() {
        synchronized(statsLock) {
            return fifoWriteCount;
        }
    }

    /**
     * Gets the number of bytes written to this stream.
     */
    public long getWriteBytes() {
        synchronized(statsLock) {
            return fifoWriteBytes;
        }
    }

    /**
     * Adds to the stats of this stream.
     */
    protected void addStats(long bytes) {
        synchronized(statsLock) {
            fifoWriteCount++;
            fifoWriteBytes+=bytes;
        }
    }

    public void write(int b) throws IOException {
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
    }

    @Override
    public void write(byte[] b) throws IOException {
        // Write to the queue
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
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
    }

    /**
     * Flushes all updates to this file to the underlying storage device.  This is performed by
     * <code>RandomAccessFile.getChannel().force(true)</code>.
     */
    @Override
    public void flush() throws IOException {
        synchronized(file) {
            file.file.getChannel().force(true);
        }
    }

    /**
     * Gets the number of bytes that may be written before the
     * queue is full and blocks writes.
     */
    public long available() throws IOException {
        synchronized(file) {
            return file.maxFifoLength-file.getLength();
        }
    }

    /**
     * @see  FifoFile#close()
     */
    @Override
    public void close() throws IOException {
        synchronized(file) {
            file.close();
        }
    }
}
