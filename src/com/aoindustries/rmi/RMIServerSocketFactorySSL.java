/*
 * Copyright 2008 by AO Industries, Inc.,
 * 816 Azalea Rd, Mobile, Alabama, 36693, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.rmi;

import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * SSL server factory.
 *
 * @author  AO Industries, Inc.
 */
public class RMIServerSocketFactorySSL implements RMIServerSocketFactory {

    final private String listenAddress;

    /**
     * Will listen on the default listen address.
     */
    public RMIServerSocketFactorySSL() {
        this.listenAddress = null;
    }

    /**
     * Will listen on the provided listen address.
     */
    public RMIServerSocketFactorySSL(String listenAddress) {
        this.listenAddress = listenAddress;
    }
    
    @Override
    public boolean equals(Object O) {
        return
            O!=null
            && (O instanceof RMIServerSocketFactorySSL)
            && StringUtility.equals(listenAddress, ((RMIServerSocketFactorySSL)O).listenAddress)
        ;
    }
    
    @Override
    public int hashCode() {
        return listenAddress==null ? 0 : listenAddress.hashCode();
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
        if(listenAddress==null) {
            return factory.createServerSocket(port, 50);
        } else {
            InetAddress address=InetAddress.getByName(listenAddress);
            return factory.createServerSocket(port, 50, address);
        }
    }
}
