/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2008, 2009, 2010, 2011, 2012, 2016, 2019, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.hodgepodge.rmi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Objects;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * SSL server factory.
 *
 * @author  AO Industries, Inc.
 */
public class RMIServerSocketFactorySSL implements RMIServerSocketFactory {

	private final String listenAddress;

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
	public boolean equals(Object obj) {
		return
			(obj instanceof RMIServerSocketFactorySSL)
			&& Objects.equals(listenAddress, ((RMIServerSocketFactorySSL)obj).listenAddress)
		;
	}

	@Override
	public int hashCode() {
		return listenAddress==null ? 0 : listenAddress.hashCode();
	}

	@Override
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
