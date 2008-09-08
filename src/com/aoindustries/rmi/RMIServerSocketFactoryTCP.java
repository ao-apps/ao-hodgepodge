/*
 * Copyright 2008 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.rmi;

import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

/**
 * TCP server factory (non-SSL).
 *
 * @author  AO Industries, Inc.
 */
public class RMIServerSocketFactoryTCP implements RMIServerSocketFactory {

    final private String listenAddress;

    /**
     * Will listen on the default listen address.
     */
    public RMIServerSocketFactoryTCP() {
        this.listenAddress = null;
    }

    /**
     * Will listen on the provided listen address.
     */
    public RMIServerSocketFactoryTCP(String listenAddress) {
        this.listenAddress = listenAddress;
    }
    
    @Override
    public boolean equals(Object O) {
        return
            O!=null
            && (O instanceof RMIServerSocketFactoryTCP)
            && StringUtility.equals(listenAddress, ((RMIServerSocketFactoryTCP)O).listenAddress)
        ;
    }
    
    @Override
    public int hashCode() {
        return listenAddress==null ? 0 : listenAddress.hashCode();
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        if(listenAddress==null) {
            return new ServerSocket(port, 50);
        } else {
            InetAddress address=InetAddress.getByName(listenAddress);
            return new ServerSocket(port, 50, address);
        }
    }
}
