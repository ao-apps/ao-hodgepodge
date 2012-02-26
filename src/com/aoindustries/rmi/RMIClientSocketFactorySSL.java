/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011, 2012  AO Industries, Inc.
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
package com.aoindustries.rmi;

import com.aoindustries.lang.ObjectUtils;
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
            && ObjectUtils.equals(localAddress, ((RMIClientSocketFactorySSL)O).localAddress)
        ;
    }
    
    @Override
    public int hashCode() {
        return localAddress==null ? 0 : localAddress.hashCode();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        SSLSocketFactory sslFact=(SSLSocketFactory)SSLSocketFactory.getDefault();
        Socket regSocket = new Socket();
        if(localAddress!=null) regSocket.bind(new InetSocketAddress(localAddress, 0));
        regSocket.connect(new InetSocketAddress(host, port), 30000);
        regSocket.setKeepAlive(true);
        regSocket.setTcpNoDelay(true);
        return sslFact.createSocket(regSocket, host, port, true);
    }
}
