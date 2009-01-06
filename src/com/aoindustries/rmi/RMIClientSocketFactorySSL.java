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
import javax.net.ssl.SSLSocketFactory;

/**
 * SSL client factory.
 *
 * @author  AO Industries, Inc.
 */
public class RMIClientSocketFactorySSL implements RMIClientSocketFactory, Serializable {
    
    private static final long serialVersionUID = 1;

    final private String localAddress;

    /**
     * Will establish connections with the system default local address.
     */
    public RMIClientSocketFactorySSL() {
        this.localAddress = null;
    }

    /**
     * Will establish connections with the provided local address.
     */
    public RMIClientSocketFactorySSL(String localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public boolean equals(Object O) {
        return
            O!=null
            && (O instanceof RMIClientSocketFactorySSL)
            && StringUtility.equals(localAddress, ((RMIClientSocketFactorySSL)O).localAddress)
        ;
    }
    
    @Override
    public int hashCode() {
        return localAddress==null ? 0 : localAddress.hashCode();
    }

    public Socket createSocket(String host, int port) throws IOException {
        SSLSocketFactory sslFact=(SSLSocketFactory)SSLSocketFactory.getDefault();
        Socket regSocket = new Socket();
        if(localAddress!=null) regSocket.bind(new InetSocketAddress(localAddress, 0));
        regSocket.connect(new InetSocketAddress(host, port), 30000);
        regSocket.setKeepAlive(true);
        return sslFact.createSocket(regSocket, host, port, true);
    }
}
