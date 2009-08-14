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
