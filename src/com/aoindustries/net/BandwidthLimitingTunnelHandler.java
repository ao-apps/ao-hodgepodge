package com.aoindustries.net;

/*
 * Copyright 2006 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Handles one connection for a <code>BandwidthLimitingTunnel</code>.
 *
 * @see  BandwidthLimitingTunnel
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class BandwidthLimitingTunnelHandler {

    private boolean verbose;
    private String connect_address;
    private int connect_port;
    private int upstream_bandwidth;
    private int downstream_bandwidth;
    private Socket socket;

    public BandwidthLimitingTunnelHandler(
        boolean verbose,
        String connect_address,
        int connect_port,
        int upstream_bandwidth,
        int downstream_bandwidth,
        Socket socket
    ) throws IOException {
        this.verbose = verbose;
        this.connect_address = connect_address;
        this.connect_port = connect_port;
        this.upstream_bandwidth = upstream_bandwidth;
        this.downstream_bandwidth = downstream_bandwidth;
        this.socket = socket;
        
        Socket connectSocket = new Socket(InetAddress.getByName(connect_address), connect_port);

        new BandwidthLimitingTunnelHandlerUpstreamThread(verbose, upstream_bandwidth, socket, connectSocket);
        new BandwidthLimitingTunnelHandlerDownstreamThread(verbose, downstream_bandwidth, socket, connectSocket);
    }
}
