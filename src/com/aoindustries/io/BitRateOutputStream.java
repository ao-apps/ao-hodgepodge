package com.aoindustries.io;

/*
 * Copyright 2003-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.*;

/**
 * A <code>BitRateOutputStream</code> regulates an
 * <code>OutputStream</code> to a specific bit rate.
 * Please note that this class is not synchronized
 * so it should only be used from a single Thread
 * or should be synchronized enternally.
 *
 * @author  AO Industries, Inc.
 */
public class BitRateOutputStream extends FilterOutputStream {

    public final static long MAX_CATCHUP_TIME=2*1000;

    final private BitRateProvider provider;

    private long blockStart=-1;
    private long catchupTime;
    private int byteCount;

    public BitRateOutputStream(OutputStream out, BitRateProvider provider) {
        super(out);
        this.provider=provider;
    }

    @Override
    public void write(int b) throws IOException {
        if(blockStart==-1) blockStart=System.currentTimeMillis();
        out.write(b);
        byteCount++;
        sleepIfNeeded();
    }
    
    @Override
    public void write(byte[] b) throws IOException {
        if(blockStart==-1) blockStart=System.currentTimeMillis();
        out.write(b, 0, b.length);
        byteCount+=b.length;
        sleepIfNeeded();
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if(blockStart==-1) blockStart=System.currentTimeMillis();
        out.write(b, off, len);
        byteCount+=len;
        sleepIfNeeded();
    }

    @Override
    public void flush() throws IOException {
        if(blockStart==-1) blockStart=System.currentTimeMillis();
        out.flush();
        sleep();
    }
    
    @Override
    public void close() throws IOException {
        out.flush();
        out.close();
        blockStart=-1;
        catchupTime=0;
        byteCount=0;
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