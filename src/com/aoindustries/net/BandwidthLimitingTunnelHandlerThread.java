/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009  AO Industries, Inc.
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
package com.aoindustries.net;

import com.aoindustries.io.BitRateOutputStream;
import com.aoindustries.io.BitRateProvider;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.ErrorPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Handles one direction of bandwidth for one connection for a <code>BandwidthLimitingTunnel</code>.
 *
 * @see  BandwidthLimitingTunnelHandler
 * @see  BandwidthLimitingTunnel
 *
 * @author  AO Industries, Inc.
 */
abstract public class BandwidthLimitingTunnelHandlerThread implements Runnable, BitRateProvider {

    /**
     * The number of seconds between verbose bits/second output.
     */
    private static final long VERBOSE_REPORT_INTERVAL = (long)10*1000;

    private boolean verbose;
    private Long bandwidth;
    private Socket listenSocket;
    private Socket connectSocket;
    private Thread thread;

    public BandwidthLimitingTunnelHandlerThread(
        boolean verbose,
        Long bandwidth,
        Socket listenSocket,
        Socket connectSocket
    ) {
        this.verbose = verbose;
        this.bandwidth = bandwidth;
        this.listenSocket = listenSocket;
        this.connectSocket = connectSocket;
        (this.thread=new Thread(this)).start();
    }
    
    public void run() {
        try {
            long totalBytes = 0;
            long startTime = verbose ? System.currentTimeMillis() : 0;
            byte[] buff = BufferManager.getBytes();
            try {
                OutputStream out = getOutputStream(listenSocket, connectSocket);
                if(bandwidth != null) out = new BitRateOutputStream(out, this);
                try {
                    InputStream in = getInputStream(listenSocket, connectSocket);
                    try {
                        long blockStartTime = verbose ? System.currentTimeMillis() : 0;
                        long blockByteCount = 0;
                        int ret;
                        while((ret=in.read(buff, 0, BufferManager.BUFFER_SIZE))!=-1) {
                            out.write(buff, 0, ret);
                            totalBytes+=ret;
                            if(verbose) {
                                blockByteCount+=ret;
                                long currentTime = System.currentTimeMillis();
                                long blockTime = currentTime - blockStartTime;
                                if(blockTime < 0) {
                                    // System time updated
                                    blockStartTime = currentTime;
                                    blockByteCount = 0;
                                } else if(blockTime >= VERBOSE_REPORT_INTERVAL) {
                                    System.out.println(getDirection()+" "+totalBytes+" bytes sent in "+SQLUtility.getMilliDecimal(currentTime-startTime)+" seconds, "+(blockByteCount * 8000 / blockTime)+" bits/second");
                                    blockStartTime = currentTime;
                                    blockByteCount = 0;
                                }
                            }
                        }
                    } catch(SocketException err) {
                        // Normal socket closure
                        if(!"Socket closed".equals(err.getMessage())) throw err;
                    } finally {
                        in.close();
                    }
                } finally {
                    out.close();
                }
            } finally {
                BufferManager.release(buff);
            }
            if(verbose) {
                long endTime = System.currentTimeMillis();
                System.out.println(getDirection()+" Connection closing: "+totalBytes+" bytes sent in "+SQLUtility.getMilliDecimal(endTime-startTime)+" seconds, "+(totalBytes * 8000 / (endTime - startTime))+" bits/second average");
            }
        } catch(IOException err) {
            ErrorPrinter.printStackTraces(err);
        }
    }
    
    public int getBlockSize() {
        return BufferManager.BUFFER_SIZE;
    }
    
    public Long getBitRate() {
        return bandwidth;
    }

    abstract protected String getDirection();

    abstract protected OutputStream getOutputStream(Socket listenSocket, Socket connectSocket) throws IOException;

    abstract protected InputStream getInputStream(Socket listenSocket, Socket connectSocket) throws IOException;
}
