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
package com.aoindustries.messaging.http;

import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.messaging.Message;
import com.aoindustries.messaging.Socket;
import com.aoindustries.messaging.SocketListener;
import com.aoindustries.security.Identifier;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One established connection over HTTP(S).
 */
public class HttpSocket implements Socket {

	private final HttpSocketContext socketContext;

	private final Identifier id;

	private final long connectTime;

	private final    InetSocketAddress connectLocalSocketAddress;
	private volatile InetSocketAddress localSocketAddress;

	private final    InetSocketAddress connectRemoteSocketAddress;
	private volatile InetSocketAddress remoteSocketAddress;

	private final List<SocketListener> listeners = new ArrayList<SocketListener>();

	protected HttpSocket(
		HttpSocketContext socketContext,
		Identifier id,
		long connectTime,
		InetSocketAddress localSocketAddress,
		InetSocketAddress remoteSocketAddress
	) {
		this.socketContext = socketContext;
		this.id = id;
		this.connectTime = connectTime;
		this.connectLocalSocketAddress = localSocketAddress;
		this.localSocketAddress = localSocketAddress;
		this.connectRemoteSocketAddress = remoteSocketAddress;
		this.remoteSocketAddress = remoteSocketAddress;
	}

	@Override
	public String toString() {
		return remoteSocketAddress.toString();
	}

	/**
	 * Gets this socket's unique identifier.  This identifier should remain secret
	 * as compromising an identifier may allow hijacking a connection.
	 */
	Identifier getId() {
		return id;
	}

	@Override
	public long getConnectTime() {
		return connectTime;
	}

	@Override
	public Long getCloseTime() {
		throw new NotImplementedException("TODO");
	}

	@Override
	public InetSocketAddress getConnectSocketAddress() {
		return connectLocalSocketAddress;
	}

	@Override
	public InetSocketAddress getLocalSocketAddress() {
		return localSocketAddress;
	}

	@Override
	public InetSocketAddress getConnectRemoteSocketAddress() {
		return connectRemoteSocketAddress;
	}

	@Override
	public InetSocketAddress getRemoteSocketAddress() {
		return remoteSocketAddress;
	}

	@Override
	public void close() throws IOException {
		throw new NotImplementedException("TODO");
	}

	@Override
	public boolean isClosed() {
		throw new NotImplementedException("TODO");
	}

	@Override
	public void addSocketListener(SocketListener listener) throws IllegalStateException {
		synchronized(listener) {
			for(SocketListener existing : listeners) {
				if(existing == listener) throw new IllegalStateException("listener already added");
			}
			listeners.add(listener);
		}
	}

	@Override
	public boolean removeSocketListener(SocketListener listener) {
		synchronized(listener) {
			for(int i=0, size=listeners.size(); i<size; i++) {
				SocketListener existing = listeners.get(i);
				if(existing == listener) {
					listeners.remove(i);
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void sendMessage(Message message) throws ClosedChannelException {
		Collections.singletonList(message);
	}

	@Override
	public void sendMessages(Iterable<? extends Message> messages) throws ClosedChannelException {
		throw new NotImplementedException("TODO");
	}
}
