/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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
package com.aoindustries.messaging.tcp;

import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.util.concurrent.Callback;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Client component for bi-directional messaging over TCP.
 */
public class TcpSocketClient implements Closeable {

	private final HttpSocketContext socketContext = new HttpSocketContext() {
		@Override
		public void onClose(HttpSocket socket) {
			throw new NotImplementedException("TODO");
		}
	};

	/**
	 * Asynchronously connects.
	 */
	public void connect(
		String url,
		Callback<HttpSocket> onConnect,
		Callback<Throwable> onError
	) {
		throw new NotImplementedException("TODO");
	}

	protected HttpSocket newSocket(
		Identifier id,
		long connectTime,
		InetSocketAddress localSocketAddress,
		InetSocketAddress remoteSocketAddress
	) {
		return new HttpSocket(
			socketContext,
			id,
			connectTime,
			localSocketAddress,
			remoteSocketAddress
		);
	}

	/**
	 * Closes this client.  When the client is closed, all active sockets are
	 * also closed.
	 */
	@Override
	public void close() throws IOException {
		throw new NotImplementedException("TODO");
	}
}
