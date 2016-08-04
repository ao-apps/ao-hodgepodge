/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015  AO Industries, Inc.
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

import com.aoindustries.util.ErrorPrinter;
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
public class BandwidthLimitingTunnel implements Runnable {

    private final boolean verbose;
    private final String listen_address;
    private final int listen_port;
    private final String connect_address;
    private final int connect_port;
    private final Long upstream_bandwidth;
    private final Long downstream_bandwidth;
    private final Thread thread;

    public BandwidthLimitingTunnel(
        boolean verbose,
        String listen_address,
        int listen_port,
        String connect_address,
        int connect_port,
        Long upstream_bandwidth,
        Long downstream_bandwidth
    ) {
        this.verbose = verbose;
        this.listen_address = listen_address;
        this.listen_port = listen_port;
        this.connect_address = connect_address;
        this.connect_port = connect_port;
        this.upstream_bandwidth = upstream_bandwidth;
        this.downstream_bandwidth = downstream_bandwidth;
        (this.thread = new Thread(this)).start();
    }

    public static void main(String[] args) {
        // Listen address
        // Listen port
        // Connect address
        // Connect port
        // Upstream max bandwidth
        // Downstream max bandwidth
        if(args.length==6 || args.length==7) {
            int pos = 0;
            boolean verbose;
            if(args.length==7) {
                if(args[pos++].equals("-v")) {
                    verbose = true;
                } else {
                    printUsage();
                    System.exit(1);
                    return;
                }
            } else {
                verbose = false;
            }
            String listen_address=args[pos++];
            int listen_port=Integer.parseInt(args[pos++]);
            String connect_address=args[pos++];
            int connect_port=Integer.parseInt(args[pos++]);
            String S = args[pos++];
            Long upstream_bandwidth=S.length()==0 ? null : Long.parseLong(S);
            S = args[pos++];
            Long downstream_bandwidth=S.length()==0 ? null : Long.parseLong(S);
            new BandwidthLimitingTunnel(verbose, listen_address, listen_port, connect_address, connect_port, upstream_bandwidth, downstream_bandwidth);
        } else {
            printUsage();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.err.println("usage: "+BandwidthLimitingTunnel.class.getName()+" [-v] {listen_address|*} <listen_port> {connect_address} {connect_port} {upstream_bandwidth|\"\"} {downstream_bandwidth|\"\"}");
        System.err.println("        -v                   - switch to display status of each connection on standard output");
        System.err.println("        listen_address       - the hostname or IP address to listen to or * to listen on all local IP addresses");
        System.err.println("        listen_port          - the port to listen to");
        System.err.println("        connect_address      - the hostname or IP address to connect to for each tunnel");
        System.err.println("        connect_port         - the port to connect to for each tunnel");
        System.err.println("        upstream_bandwidth   - the maximum number of bits per second for upstream bandwidth or \"\" for unlimited");
        System.err.println("        downstream_bandwidth - the maximum number of bits per second for downstream bandwidth or \"\" for unlimited");
    }

	@Override
    public void run() {
        while(thread==Thread.currentThread()) {
            try {
                while(thread==Thread.currentThread()) {
                    if(verbose) System.out.println("Accepting connections on "+listen_address+":"+listen_port);
                    ServerSocket serverSocket = new ServerSocket(listen_port, 50, listen_address.equals("*") ? null : InetAddress.getByName(listen_address));
                    while(true) {
                        Socket socket = serverSocket.accept();
                        if(verbose) System.out.println("New connection from "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
                        new BandwidthLimitingTunnelHandler(verbose, connect_address, connect_port, upstream_bandwidth, downstream_bandwidth, socket);
                    }
                }
            } catch(ThreadDeath TD) {
                throw TD;
            } catch(Throwable T) {
                ErrorPrinter.printStackTraces(T);
                try {
                    Thread.sleep(10000);
                } catch(InterruptedException err) {
                    ErrorPrinter.printStackTraces(err);
                }
            }
        }
    }
}