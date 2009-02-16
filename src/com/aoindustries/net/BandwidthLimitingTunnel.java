package com.aoindustries.net;

/*
 * Copyright 2006-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.io.BitRateProvider;
import com.aoindustries.util.ErrorPrinter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Provides a tunnel for TCP sockets that limits the bandwidth for both upstream and downstream bandwidth.
 * This was developed for the purpose of limiting the upload bandwidth consumed during CVS operating from
 * our main office in order to keep the network responsive.  However, it is general purpose and might find additional
 * uses.
 *
 * @version  1.0
 *
 * @author  AO Industries, Inc.
 */
public class BandwidthLimitingTunnel implements Runnable {

    private boolean verbose;
    private String listen_address;
    private int listen_port;
    private String connect_address;
    private int connect_port;
    private int upstream_bandwidth;
    private int downstream_bandwidth;
    private Thread thread;

    public BandwidthLimitingTunnel(
        boolean verbose,
        String listen_address,
        int listen_port,
        String connect_address,
        int connect_port,
        int upstream_bandwidth,
        int downstream_bandwidth
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
            int upstream_bandwidth=S.length()==0 ? BitRateProvider.UNLIMITED_BANDWIDTH : Integer.parseInt(S);
            S = args[pos++];
            int downstream_bandwidth=S.length()==0 ? BitRateProvider.UNLIMITED_BANDWIDTH : Integer.parseInt(S);
            new BandwidthLimitingTunnel(verbose, listen_address, listen_port, connect_address, connect_port, upstream_bandwidth, downstream_bandwidth);
        } else {
            printUsage();
            System.exit(1);
            return;
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