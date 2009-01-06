package com.aoindustries.net;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles the upstream bandwidth for one connection for a <code>BandwidthLimitingTunnel</code>.
 *
 * @see  BandwidthLimitingTunnelHandler
 * @see  BandwidthLimitingTunnel
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class BandwidthLimitingTunnelHandlerUpstreamThread extends BandwidthLimitingTunnelHandlerThread {

    public BandwidthLimitingTunnelHandlerUpstreamThread(
        boolean verbose,
        int upstream_bandwidth,
        Socket listenSocket,
        Socket connectSocket
    ) {
        super(verbose, upstream_bandwidth, listenSocket, connectSocket);
    }

    protected String getDirection() {
        return "upstream";
    }

    protected OutputStream getOutputStream(Socket listenSocket, Socket connectSocket) throws IOException {
        return connectSocket.getOutputStream();
    }

    protected InputStream getInputStream(Socket listenSocket, Socket connectSocket) throws IOException {
        return listenSocket.getInputStream();
    }
}
