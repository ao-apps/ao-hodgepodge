package com.aoindustries.io;

/*
 * Copyright 2003-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * A <code>BitRateInputStream</code> regulates an
 * <code>InputStream</code> to a specific bit rate.
 * Please note that this class is not synchronized
 * so it should only be used from a single Thread
 * or should be synchronized enternally.
 *
 * @author  AO Industries, Inc.
 */
public class BitRateInputStream extends FilterInputStream {

    public final static long MAX_CATCHUP_TIME=2*1000;

    final private BitRateProvider provider;

    private long blockStart=-1;
    private long catchupTime;
    private int byteCount;

    public BitRateInputStream(InputStream out, BitRateProvider provider) {
        super(out);
        this.provider=provider;
    }

    @Override
    public void close() throws IOException {
        in.close();
        sleep();
    }
    
    @Override
    public int read() throws IOException {
        if(blockStart==-1) blockStart=System.currentTimeMillis();
        int b=in.read();
        if(b!=-1) byteCount++;
        sleepIfNeeded();
        return b;
    }
    
    @Override
    public int read(byte[] buff) throws IOException {
        if(blockStart==-1) blockStart=System.currentTimeMillis();
        int count=in.read(buff);
        if(count!=-1) byteCount+=count;
        sleepIfNeeded();
        return count;
    }
    
    @Override
    public int read(byte[] buff, int off, int len) throws IOException {
        if(blockStart==-1) blockStart=System.currentTimeMillis();
        int count=in.read(buff, off, len);
        if(count!=-1) byteCount+=count;
        sleepIfNeeded();
        return count;
    }

    private void sleepIfNeeded() throws IOException {
        if(byteCount>provider.getBlockSize()) sleep();
    }
    
    private void sleep() throws IOException {
        if(byteCount>0) {
            int bps=provider.getBitRate();
            if(bps>0) {
                // Figure out the number of millis to sleep
                long blockTime=(byteCount*8*1000)/bps;
                long sleepyTime=blockTime-(System.currentTimeMillis()-blockStart);

                if(sleepyTime>0) {
                    if(catchupTime>sleepyTime) catchupTime-=sleepyTime;
                    else {
                        sleepyTime-=catchupTime;
                        catchupTime=0;
                        try {
                            // Birdie - ti ger, nnnnnggggaaaa - sleeepy time
                            Thread.sleep(sleepyTime);
                        } catch(InterruptedException err) {
                            InterruptedIOException ioErr=new InterruptedIOException();
                            ioErr.initCause(err);
                            throw ioErr;
                        }
                    }
                } else {
                    // Can't sleep the clowns will eat me.
                    catchupTime-=sleepyTime;
                    if(catchupTime>=MAX_CATCHUP_TIME) catchupTime=MAX_CATCHUP_TIME;
                }
            } else {
                // currently flagged as unlimited bandwidth
                catchupTime=0;
            }
            blockStart=System.currentTimeMillis();
            byteCount=0;
        }
    }
}