/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010  AO Industries, Inc.
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Handles one connection for a <code>BandwidthLimitingTunnel</code>.
 *
 * @see  BandwidthLimitingTunnel
 *
 * @author  AO Industries, Inc.
 */
public class BandwidthLimitingTunnelHandler {

    //private boolean verbose;
    //private String connect_address;
    //private int connect_port;
    //private Long upstream_bandwidth;
    //private Long downstream_bandwidth;
    //private Socket socket;

    public BandwidthLimitingTunnelHandler(
        boolean verbose,
        String connect_address,
        int connect_port,
        Long upstream_bandwidth,
        Long downstream_bandwidth,
        Socket socket
    ) throws IOException {
        //this.verbose = verbose;
        //this.connect_address = connect_address;
        //this.connect_port = connect_port;
        //this.upstream_bandwidth = upstream_bandwidth;
        //this.downstream_bandwidth = downstream_bandwidth;
        //this.socket = socket;
        
        Socket connectSocket = new Socket(InetAddress.getByName(connect_address), connect_port);

        new BandwidthLimitingTunnelHandlerUpstreamThread(verbose, upstream_bandwidth, socket, connectSocket);
        new BandwidthLimitingTunnelHandlerDownstreamThread(verbose, downstream_bandwidth, socket, connectSocket);
    }
}
