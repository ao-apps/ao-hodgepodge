/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2016, 2021, 2022  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.net;

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
  //private String connectAddress;
  //private int connectPort;
  //private Long upstreamBandwidth;
  //private Long downstreamBandwidth;
  //private Socket socket;

  public BandwidthLimitingTunnelHandler(
      boolean verbose,
      String connectAddress,
      int connectPort,
      Long upstreamBandwidth,
      Long downstreamBandwidth,
      Socket socket
  ) throws IOException {
    //this.verbose = verbose;
    //this.connectAddress = connectAddress;
    //this.connectPort = connectPort;
    //this.upstreamBandwidth = upstreamBandwidth;
    //this.downstreamBandwidth = downstreamBandwidth;
    //this.socket = socket;

    Socket connectSocket = new Socket(InetAddress.getByName(connectAddress), connectPort);

    new BandwidthLimitingTunnelHandlerUpstreamThread(verbose, upstreamBandwidth, socket, connectSocket).start();
    new BandwidthLimitingTunnelHandlerDownstreamThread(verbose, downstreamBandwidth, socket, connectSocket).start();
  }
}
