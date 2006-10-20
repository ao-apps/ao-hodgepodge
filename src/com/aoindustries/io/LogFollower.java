package com.aoindustries.io;

/*
 * Copyright 2002-2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.unix.*;
import java.io.*;

/**
 * Reads data as is it appended to a log file.  If the log file
 * is closed and recreated, which is typical during log rotations,
 * the new file is opened and read from the beginning.  The file
 * closure is identified by occasionally statting and comparing inode
 * numbers.
 * <p>
 * This class will block on read.  If end of file is reached, it will continue
 * to block until data becomes available.  End of file is never returned from
 * this class, it will wait indefinitely for data.
 *
 * @author  AO Industries, Inc.
 */
public class LogFollower extends InputStream {

    public static final int DEFAULT_POLL_INTERVAL=60*1000;

    private final String path;
    private final int pollInterval;
    private final UnixFile unixFile;

    private boolean isClosed;

    private RandomAccessFile randomAccess;
    private long filePos;
    private long currentInode;

    public LogFollower(String path) {
        this(path, DEFAULT_POLL_INTERVAL);
    }
    
    public LogFollower(String path, int pollInterval) {
        this.path=path;
        this.pollInterval=pollInterval;
        this.unixFile=new UnixFile(path);
    }

    private void openIfNeeded() throws IOException {
        if(isClosed) throw new IOException("LogFollower has been closed: "+path);

        if(randomAccess==null) {
            randomAccess=new RandomAccessFile(path, "r");
            filePos=randomAccess.length();
            currentInode=unixFile.getStat().getInode();
        }
    }

    synchronized public int available() throws IOException {
        openIfNeeded();
        long available=randomAccess.length()-filePos;
        if(available<0) available=0;
        else if(available>Integer.MAX_VALUE) available=Integer.MAX_VALUE;
        return (int)available;
    }

    public void close() throws IOException {
        isClosed=true;
        RandomAccessFile R=randomAccess;
        randomAccess=null;
        if(R!=null) R.close();
        filePos=0;
        currentInode=0;
    }

    public int getPollInterval() {
        return pollInterval;
    }

    public void finalize() throws Throwable {
        close();
        super.finalize();
    }

    synchronized public int read() throws IOException {
        openIfNeeded();
        while(!isClosed) {
            // Read to the end of the file
            long ral=randomAccess.length();
            if(ral>filePos) {
                randomAccess.seek(filePos++);
                return randomAccess.read();
            } else filePos=ral;

            // Reopen if the inode has changed
            Stat stat = unixFile.getStat();
            if(stat.exists()) {
                long newInode=stat.getInode();
                if(newInode!=currentInode) {
                    randomAccess.close();
                    randomAccess=new RandomAccessFile(path, "r");
                    filePos=0;
                    currentInode=newInode;

                    // Return a byte of the new file if available
                    if(randomAccess.length()>filePos) {
                        randomAccess.seek(filePos++);
                        return randomAccess.read();
                    }
                }
            }

            // Sleep and try again
            try {
                Thread.sleep(pollInterval);
            } catch(InterruptedException err) {
                InterruptedIOException ioErr=new InterruptedIOException();
                ioErr.initCause(err);
                throw ioErr;
            }
        }
        throw new IOException("LogFollower has been closed: "+path);
    }

    synchronized public int read(byte[] b, int offset, int len) throws IOException {
        openIfNeeded();
        while(!isClosed) {
            // Read to the end of the file
            long ral=randomAccess.length();
            if(ral>filePos) {
                randomAccess.seek(filePos);
                long avail=randomAccess.length()-filePos;
                if(avail>len) avail=len;
                int actual=randomAccess.read(b, offset, (int)avail);
                filePos+=actual;
                return actual;
            } else filePos=ral;

            // Reopen if the inode has changed
            Stat stat = unixFile.getStat();
            if(stat.exists()) {
                long newInode=stat.getInode();
                if(newInode!=currentInode) {
                    randomAccess.close();
                    randomAccess=new RandomAccessFile(path, "r");
                    filePos=0;
                    currentInode=newInode;

                    // Read from the file if available
                    if(randomAccess.length()>filePos) {
                        randomAccess.seek(filePos);
                        long avail=randomAccess.length()-filePos;
                        if(avail>len) avail=len;
                        int actual=randomAccess.read(b, offset, (int)avail);
                        filePos+=actual;
                        return actual;
                    }
                }
            }

            // Sleep and try again
            try {
                Thread.sleep(pollInterval);
            } catch(InterruptedException err) {
                InterruptedIOException ioErr=new InterruptedIOException();
                ioErr.initCause(err);
                throw ioErr;
            }
        }
        throw new IOException("LogFollower has been closed: "+path);
    }

    synchronized public long skip(long n) throws IOException {
        openIfNeeded();
        while(!isClosed) {
            // Skip to the end of the file
            long ral=randomAccess.length();
            if(ral>filePos) {
                randomAccess.seek(filePos);
                long avail=randomAccess.length()-filePos;
                if(avail>n) avail=n;
                int actual=randomAccess.skipBytes((int)avail);
                filePos+=actual;
                return actual;
            } else filePos=ral;

            // Reopen if the inode has changed
            Stat stat = unixFile.getStat();
            if(stat.exists()) {
                long newInode=stat.getInode();
                if(newInode!=currentInode) {
                    randomAccess.close();
                    randomAccess=new RandomAccessFile(path, "r");
                    filePos=0;
                    currentInode=newInode;

                    // Read from the file if available
                    if(randomAccess.length()>filePos) {
                        randomAccess.seek(filePos);
                        long avail=randomAccess.length()-filePos;
                        if(avail>n) avail=n;
                        int actual=randomAccess.skipBytes((int)avail);
                        filePos+=actual;
                        return actual;
                    }
                }
            }

            // Sleep and try again
            try {
                Thread.sleep(pollInterval);
            } catch(InterruptedException err) {
                InterruptedIOException ioErr=new InterruptedIOException();
                ioErr.initCause(err);
                throw ioErr;
            }
        }
        throw new IOException("LogFollower has been closed: "+path);
    }
}