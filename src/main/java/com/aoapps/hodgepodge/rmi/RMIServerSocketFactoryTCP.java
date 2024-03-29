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

/**
 * TCP server factory (non-SSL).
 *
 * @author  AO Industries, Inc.
 */
public class RMIServerSocketFactoryTCP implements RMIServerSocketFactory {

  private final String listenAddress;

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
  public boolean equals(Object obj) {
    return
        (obj instanceof RMIServerSocketFactoryTCP)
            && Objects.equals(listenAddress, ((RMIServerSocketFactoryTCP) obj).listenAddress);
  }

  @Override
  public int hashCode() {
    return listenAddress == null ? 0 : listenAddress.hashCode();
  }

  @Override
  public ServerSocket createServerSocket(int port) throws IOException {
    if (listenAddress == null) {
      return new ServerSocket(port, 50);
    } else {
      InetAddress address = InetAddress.getByName(listenAddress);
      return new ServerSocket(port, 50, address);
    }
  }
}
