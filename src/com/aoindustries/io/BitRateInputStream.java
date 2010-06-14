/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public.
 *
 * aocode-public is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

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
    private long byteCount;

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
            Long bps=provider.getBitRate();
            if(bps!=null && bps>0) {
                // Figure out the number of millis to sleep
                long blockTime=(byteCount*8L*1000L)/bps;
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