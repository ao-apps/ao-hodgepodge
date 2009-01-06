/*
 * Copyright 2008-2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.rmi;

import com.aoindustries.util.StringUtility;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

/**
 * TCP client factory (non-SSL).
 *
 * @author  AO Industries, Inc.
 */
public class RMIClientSocketFactoryTCP implements RMIClientSocketFactory, Serializable {
    
    private static final long serialVersionUID = 1L;

    final private String localAddress;

    /**
     * Will establish connections with the system default local address.
     */
    public RMIClientSocketFactoryTCP() {
        this.localAddress = null;
    }

    /**
     * Will establish connections with the provided local address.
     */
    public RMIClientSocketFactoryTCP(String localAddress) {
        this.localAddress = localAddress;
    }
    
    @Override
    public boolean equals(Object O) {
        return
            O!=null
            && (O instanceof RMIClientSocketFactoryTCP)
            && StringUtility.equals(localAddress, ((RMIClientSocketFactoryTCP)O).localAddress)
        ;
    }
    
    @Override
    public int hashCode() {
        return localAddress==null ? 0 : localAddress.hashCode();
    }

    public Socket createSocket(String host, int port) throws IOException {
        Socket socket=new Socket();
        socket.setKeepAlive(true);
        if(localAddress!=null) socket.bind(new InetSocketAddress(localAddress, 0));
        socket.connect(new InetSocketAddress(host, port), 30000);
        return socket;
    }
}
