package com.aoindustries.io;

/*
 * Copyright 2004-2006 by AO Industries, Inc.,
 * 2200 Dogwood Ct N, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.profiler.*;
import com.aoindustries.util.*;
import java.io.*;

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
    final int blockSize;

    public FifoFile(String filename, long maxFifoLength) throws IOException {
        this(new File(filename), maxFifoLength);
        Profiler.startProfile(Profiler.FAST, FifoFile.class, "<init>(String,long)", null);
        Profiler.endProfile(Profiler.FAST);
    }
    
    public FifoFile(File file, long maxFifoLength) throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFile.class, "<init>(File,long)", null);
        try {
            if(maxFifoLength<1) throw new IllegalArgumentException("The FIFO must be at least one byte long");
            
            this.maxFifoLength=maxFifoLength;
            this.fileLength=maxFifoLength+16;
            this.file=new RandomAccessFile(file, "rw");
            this.in=new FifoFileInputStream(this);
            this.out=new FifoFileOutputStream(this);
            long blockSize=maxFifoLength>>8;
            this.blockSize=blockSize>=BufferManager.BUFFER_SIZE?BufferManager.BUFFER_SIZE:blockSize<=0?1:(int)blockSize;
            if(this.file.length()!=fileLength) reset();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public FifoFileInputStream getInputStream() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FifoFile.class, "getInputStream()", null);
        try {
            return in;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }
    
    public FifoFileOutputStream getOutputStream() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FifoFile.class, "getOutputStream()", null);
        try {
            return out;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getMaximumFifoLength() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FifoFile.class, "getMaximumFifoLength()", null);
        try {
            return maxFifoLength;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public long getFileLength() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FifoFile.class, "getFileLength()", null);
        try {
            return fileLength;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    public int getBlockSize() {
        Profiler.startProfile(Profiler.INSTANTANEOUS, FifoFile.class, "getBlockSize()", null);
        try {
            return blockSize;
        } finally {
            Profiler.endProfile(Profiler.INSTANTANEOUS);
        }
    }

    /**
     * Resets this <code>FifoFile</code> to contain no contents and start writing at the beginning of the file.
     */
    public void reset() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFile.class, "reset()", null);
        try {
            synchronized(this) {
                file.setLength(fileLength);
                // A setLength of 0 triggers a setFirstIndex of 0
                setLength(0);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    public void close() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFile.class, "close()", null);
        try {
            file.close();
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Gets the data index of the next value that will be read.
     */
    protected long getFirstIndex() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFile.class, "getFirstIndex()", null);
        try {
            synchronized(this) {
                file.seek(0);
                return file.readLong();
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Sets the data index of the next value that will be read.
     */
    protected void setFirstIndex(long index) throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFile.class, "setFirstIndex(long)", null);
        try {
            synchronized(this) {
                file.seek(0);
                file.writeLong(index);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Gets the number of bytes currently contained by the FIFO.
     */
    public long getLength() throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFile.class, "getLength()", null);
        try {
            synchronized(this) {
                file.seek(8);
                return file.readLong();
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }

    /**
     * Sets the number of bytes currently contained by the FIFO.
     */
    protected void setLength(long length) throws IOException {
        Profiler.startProfile(Profiler.IO, FifoFile.class, "setLength(long)", null);
        try {
            if(length<0) throw new IllegalArgumentException("Invalid length: "+length);
            synchronized(this) {
                file.seek(8);
                file.writeLong(length);
                if(length==0) setFirstIndex(0);
            }
        } finally {
            Profiler.endProfile(Profiler.IO);
        }
    }
}
