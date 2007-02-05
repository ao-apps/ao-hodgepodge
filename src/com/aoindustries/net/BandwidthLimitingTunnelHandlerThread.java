package com.aoindustries.net;

/*
 * Copyright 2006-2007 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.BitRateOutputStream;
import com.aoindustries.io.BitRateProvider;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.ErrorPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Handles one direction of bandwidth for one connection for a <code>BandwidthLimitingTunnel</code>.
 *
 * @see  BandwidthLimitingTunnelHandler
 * @see  BandwidthLimitingTunnel
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
abstract public class BandwidthLimitingTunnelHandlerThread implements Runnable, BitRateProvider {

    /**
     * The number of seconds between verbose bits/second output.
     */
    private static final long VERBOSE_REPORT_INTERVAL = (long)10*1000;

    private boolean verbose;
    private int bandwidth;
    private Socket listenSocket;
    private Socket connectSocket;
    private Thread thread;

    public BandwidthLimitingTunnelHandlerThread(
        boolean verbose,
        int bandwidth,
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
                if(bandwidth != UNLIMITED_BANDWIDTH) out = new BitRateOutputStream(out, this);
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
    
    public int getBitRate() {
        return bandwidth;
    }

    abstract protected String getDirection();

    abstract protected OutputStream getOutputStream(Socket listenSocket, Socket connectSocket) throws IOException;

    abstract protected InputStream getInputStream(Socket listenSocket, Socket connectSocket) throws IOException;
}
