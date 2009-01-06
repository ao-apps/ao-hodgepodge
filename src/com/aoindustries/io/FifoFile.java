package com.aoindustries.io;

/*
 * Copyright 2004-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.util.BufferManager;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A <code>FifoFile</code> allows code to read and write to an on-disk managed FIFO.
 * Objects of this class provide <code>InputStream</code> and <code>OutputStream</code>
 * implementations that may be used by any number of concurrent threads.  If using this
 * class to access a single file from multiple instances of <code>FifoFile</code>, whether
 * in a single JVM or different JVMs, due to limitations of the underlying
 * <code>RandomAccessFile</code>, only one instance may read and one instance may write.
 * However, race conditions still exists in this scenario, and it is strongly advised
 * to access a file through a single instance of <code>FifoFile</code>.
 * <p>
 * This class is best used for persistence or management of large
 * FIFO data sets.
 *
 * @author  AO Industries, Inc.
 */
public class FifoFile {

    final RandomAccessFile file;
    final FifoFileInputStream in;
    final FifoFileOutputStream out;
    final long maxFifoLength;
    final long fileLength;
    final int _blockSize;

    public FifoFile(String filename, long maxFifoLength) throws IOException {
        this(new File(filename), maxFifoLength);
    }
    
    public FifoFile(File file, long maxFifoLength) throws IOException {
        if(maxFifoLength<1) throw new IllegalArgumentException("The FIFO must be at least one byte long");

        this.maxFifoLength=maxFifoLength;
        this.fileLength=maxFifoLength+16;
        this.file=new RandomAccessFile(file, "rw");
        this.in=new FifoFileInputStream(this);
        this.out=new FifoFileOutputStream(this);
        long blockSize=maxFifoLength>>8;
        this._blockSize=blockSize>=BufferManager.BUFFER_SIZE?BufferManager.BUFFER_SIZE:blockSize<=0?1:(int)blockSize;
        if(this.file.length()!=fileLength) reset();
    }

    public FifoFileInputStream getInputStream() {
        return in;
    }
    
    public FifoFileOutputStream getOutputStream() {
        return out;
    }

    public long getMaximumFifoLength() {
        return maxFifoLength;
    }

    public long getFileLength() {
        return fileLength;
    }

    public int getBlockSize() {
        return _blockSize;
    }

    /**
     * Resets this <code>FifoFile</code> to contain no contents and start writing at the beginning of the file.
     */
    public void reset() throws IOException {
        synchronized(this) {
            file.setLength(fileLength);
            // A setLength of 0 triggers a setFirstIndex of 0
            setLength(0);
        }
    }

    public void close() throws IOException {
        synchronized(this) {
            file.close();
        }
    }

    /**
     * Gets the data index of the next value that will be read.
     */
    protected long getFirstIndex() throws IOException {
        synchronized(this) {
            file.seek(0);
            return file.readLong();
        }
    }

    /**
     * Sets the data index of the next value that will be read.
     */
    protected void setFirstIndex(long index) throws IOException {
        synchronized(this) {
            file.seek(0);
            file.writeLong(index);
        }
    }

    /**
     * Gets the number of bytes currently contained by the FIFO.
     */
    public long getLength() throws IOException {
        synchronized(this) {
            file.seek(8);
            return file.readLong();
        }
    }

    /**
     * Sets the number of bytes currently contained by the FIFO.
     */
    protected void setLength(long length) throws IOException {
        if(length<0) throw new IllegalArgumentException("Invalid length: "+length);
        synchronized(this) {
            file.seek(8);
            file.writeLong(length);
            if(length==0) setFirstIndex(0);
        }
    }
}
