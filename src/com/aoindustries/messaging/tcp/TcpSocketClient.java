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

import com.aoindustries.io.CompressedDataInputStream;
import com.aoindustries.io.CompressedDataOutputStream;
import com.aoindustries.messaging.AbstractSocketContext;
import com.aoindustries.security.Identifier;
import com.aoindustries.util.concurrent.Callback;
import com.aoindustries.util.concurrent.ExecutorService;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Client component for bi-directional messaging over TCP.
 */
public class TcpSocketClient extends AbstractSocketContext<TcpSocket> {

	private static final boolean KEEPALIVE = true;

	private static final boolean SOCKET_SO_LINGER_ENABLED = true;
	private static final int SOCKET_SO_LINGER_SECONDS = 15;

	private static final boolean TCP_NO_DELAY = true;

	private static final int CONNECT_TIMEOUT = 15;

	private final ExecutorService executor = ExecutorService.newInstance();

	public TcpSocketClient() {
	}

	@Override
	public void close() {
		try {
			super.close();
		} finally {
			executor.dispose();
		}
	}

	/**
	 * Asynchronously connects.
	 */
	public void connect(
		final SocketAddress endpoint,
		final Callback<? super TcpSocket> onConnect,
		final Callback<? super Throwable> onError
	) {
		executor.submitUnbounded(
			new Runnable() {
				@Override
				public void run() {
					try {
						Socket socket = new Socket();
						socket.setKeepAlive(KEEPALIVE);
						socket.setSoLinger(SOCKET_SO_LINGER_ENABLED, SOCKET_SO_LINGER_SECONDS);
						socket.setTcpNoDelay(TCP_NO_DELAY);
						long connectTime = System.currentTimeMillis();
						socket.connect(endpoint, CONNECT_TIMEOUT);
						boolean successful = false;
						try {
							CompressedDataInputStream in = new CompressedDataInputStream(socket.getInputStream());
							CompressedDataOutputStream out = new CompressedDataOutputStream(socket.getOutputStream());
							Identifier id = new Identifier(in.readLong(), in.readLong());
							TcpSocket tcpSocket = new TcpSocket(
								TcpSocketClient.this,
								id,
								connectTime,
								socket,
								in,
								out
							);
							addSocket(tcpSocket);
							onConnect.call(tcpSocket);
							successful = true;
						} finally {
							if(!successful) {
								socket.close();
							}
						}
					} catch(ThreadDeath td) {
						throw td;
					} catch(Throwable t) {
						onError.call(t);
					}
				}
			}
		);
	}
}
