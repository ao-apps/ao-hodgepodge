package com.aoindustries.net;

/*
 * Copyright 2006-2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles the downstream bandwidth for one connection for a <code>BandwidthLimitingTunnel</code>.
 *
 * @see  BandwidthLimitingTunnelHandler
 * @see  BandwidthLimitingTunnel
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class BandwidthLimitingTunnelHandlerDownstreamThread extends BandwidthLimitingTunnelHandlerThread {

    public BandwidthLimitingTunnelHandlerDownstreamThread(
        boolean verbose,
        int downstream_bandwidth,
        Socket listenSocket,
        Socket connectSocket
    ) {
        super(verbose, downstream_bandwidth, listenSocket, connectSocket);
    }
    
    protected String getDirection() {
        return "downstream";
    }

    protected OutputStream getOutputStream(Socket listenSocket, Socket connectSocket) throws IOException {
        return listenSocket.getOutputStream();
    }

    protected InputStream getInputStream(Socket listenSocket, Socket connectSocket) throws IOException {
        return connectSocket.getInputStream();
    }
}
