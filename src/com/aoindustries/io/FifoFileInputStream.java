package com.aoindustries.io;

/*
 * Copyright 2004-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
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
        this.file=file;
    }

    /**
     * Gets the number of reads performed on this stream.
     */
    public long getReadCount() {
        synchronized(statsLock) {
            return fifoReadCount;
        }
    }

    /**
     * Gets the number of bytes read from this stream.
     */
    public long getReadBytes() {
        synchronized(statsLock) {
            return fifoReadBytes;
        }
    }

    /**
     * Adds to the stats of this stream.
     */
    protected void addStats(long bytes) {
        synchronized(statsLock) {
            fifoReadCount++;
            fifoReadBytes+=bytes;
        }
    }

    /**
     * Reads data from the file, blocks until the data is available.
     */
    public int read() throws IOException {
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
    }

    /**
     * Reads data from the file, blocks until at least one byte is available.
     */
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads data from the file, blocks until at least one byte is available.
     */
    public int read(byte[] b, int off, int len) throws IOException {
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
    }
    
    /**
     * Skips data in the queue, blocks until at least one byte is skipped.
     */
    public long skip(long n) throws IOException {
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
    }

    /**
     * Determines the number of bytes that may be read without blocking.
     */
    public int available() throws IOException {
        synchronized(file) {
            long len=file.getLength();
            return len>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)len;
        }
    }

    /**
     * @see  FifoFile#close()
     */
    public void close() throws IOException {
        file.close();
    }
}
