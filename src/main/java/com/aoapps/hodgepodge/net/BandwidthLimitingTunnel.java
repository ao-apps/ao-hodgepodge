/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016, 2018, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.util.ErrorPrinter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Provides a tunnel for TCP sockets that limits the bandwidth for both upstream and downstream bandwidth.
 * This was developed for the purpose of limiting the upload bandwidth consumed during CVS operations from
 * our main office in order to keep the network responsive.  However, it is general purpose and might find additional
 * uses.
 *
 * @author  AO Industries, Inc.
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class BandwidthLimitingTunnel implements Runnable {

  private final boolean verbose;
  private final String listenAddress;
  private final int listenPort;
  private final String connectAddress;
  private final int connectPort;
  private final Long upstreamBandwidth;
  private final Long downstreamBandwidth;
  private final Thread thread;

  @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
  public BandwidthLimitingTunnel(
      boolean verbose,
      String listenAddress,
      int listenPort,
      String connectAddress,
      int connectPort,
      Long upstreamBandwidth,
      Long downstreamBandwidth
  ) {
    this.verbose = verbose;
    this.listenAddress = listenAddress;
    this.listenPort = listenPort;
    this.connectAddress = connectAddress;
    this.connectPort = connectPort;
    this.upstreamBandwidth = upstreamBandwidth;
    this.downstreamBandwidth = downstreamBandwidth;
    (this.thread = new Thread(this)).start();
  }

  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  public static void main(String[] args) {
    // Listen address
    // Listen port
    // Connect address
    // Connect port
    // Upstream max bandwidth
    // Downstream max bandwidth
    if (args.length == 6 || args.length == 7) {
      int pos = 0;
      boolean verbose;
      if (args.length == 7) {
        if ("-v".equals(args[pos++])) {
          verbose = true;
        } else {
          printUsage();
          System.exit(1);
          return;
        }
      } else {
        verbose = false;
      }
      String listenAddress = args[pos++];
      int listenPort = Integer.parseInt(args[pos++]);
      String connectAddress = args[pos++];
      int connectPort = Integer.parseInt(args[pos++]);
      String s = args[pos++];
      Long upstreamBandwidth = s.length() == 0 ? null : Long.parseLong(s);
      s = args[pos++];
      Long downstreamBandwidth = s.length() == 0 ? null : Long.parseLong(s);
      new BandwidthLimitingTunnel(verbose, listenAddress, listenPort, connectAddress, connectPort, upstreamBandwidth, downstreamBandwidth);
    } else {
      printUsage();
      System.exit(1);
    }
  }

  private static void printUsage() {
    System.err.println("usage: " + BandwidthLimitingTunnel.class.getName()
        + " [-v] {listen_address|*}"
        + " <listen_port>"
        + " {connect_address}"
        + " {connect_port}"
        + " {upstream_bandwidth|\"\"}"
        + " {downstream_bandwidth|\"\"}");
    System.err.println("        -v                   - switch to display status of each connection on standard output");
    System.err.println("        listen_address       - the hostname or IP address to listen to or * to listen on all local IP addresses");
    System.err.println("        listen_port          - the port to listen to");
    System.err.println("        connect_address      - the hostname or IP address to connect to for each tunnel");
    System.err.println("        connect_port         - the port to connect to for each tunnel");
    System.err.println("        upstream_bandwidth   - the maximum number of bits per second for upstream bandwidth or \"\" for unlimited");
    System.err.println("        downstream_bandwidth - the maximum number of bits per second for downstream bandwidth or \"\" for unlimited");
  }

  @Override
  @SuppressWarnings({"ResultOfObjectAllocationIgnored", "UseSpecificCatch", "TooBroadCatch", "SleepWhileInLoop"})
  public void run() {
    while (thread == Thread.currentThread() && !Thread.currentThread().isInterrupted()) {
      try {
        if (verbose) {
          System.out.println("Accepting connections on " + listenAddress + ":" + listenPort);
        }
        try (ServerSocket serverSocket = new ServerSocket(
            listenPort,
            50,
            "*".equals(listenAddress) ? null : InetAddress.getByName(listenAddress)
        )) {
          while (!Thread.currentThread().isInterrupted()) {
            Socket socket = serverSocket.accept();
            if (verbose) {
              System.out.println("New connection from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            }
            new BandwidthLimitingTunnelHandler(verbose, connectAddress, connectPort, upstreamBandwidth, downstreamBandwidth, socket);
          }
        }
      } catch (ThreadDeath td) {
        throw td;
      } catch (Throwable t) {
        ErrorPrinter.printStackTraces(t, System.err);
        try {
          Thread.sleep(10000);
        } catch (InterruptedException err) {
          ErrorPrinter.printStackTraces(err, System.err);
          // Restore the interrupted status
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
