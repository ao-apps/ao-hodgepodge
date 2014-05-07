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
package com.aoindustries.net.httpsocket;

import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.security.Identifier;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One established connection.
 */
public class HttpSocket<HS extends HttpSocket<HS>> implements Closeable {

	private final HttpSocketContext<HS> socketContext;

	private final Identifier id;

	private final long connectTime;

	private final    InetSocketAddress localSocketAddress;
	private volatile InetSocketAddress mostRecentLocalSocketAddress;

	private final    InetSocketAddress remoteSocketAddress;
	private volatile InetSocketAddress mostRecentRemoteSocketAddress;

	private final List<HttpSocketListener<? super HS>> listeners = new ArrayList<HttpSocketListener<? super HS>>();

	protected HttpSocket(
		HttpSocketContext<HS> socketContext,
		Identifier id,
		long connectTime,
		InetSocketAddress localSocketAddress,
		InetSocketAddress remoteSocketAddress
	) {
		this.socketContext = socketContext;
		this.id = id;
		this.connectTime = connectTime;
		this.localSocketAddress = localSocketAddress;
		this.mostRecentLocalSocketAddress = localSocketAddress;
		this.remoteSocketAddress = remoteSocketAddress;
		this.mostRecentRemoteSocketAddress = remoteSocketAddress;
	}

	@Override
	public String toString() {
		return mostRecentRemoteSocketAddress.toString();
	}

	/**
	 * Gets this socket's unique identifier.  This identifier should remain secret
	 * as compromising an identifier may allow hijacking a connection.
	 */
	public Identifier getId() {
		return id;
	}

	/**
	 * Gets the time this connection was established.
	 */
	public long getConnectTime() {
		return connectTime;
	}

	/**
	 * Gets the time this connection closed or null if still connected.
	 */
	public Long getCloseTime() {
		throw new NotImplementedException("TODO");
	}

	/**
	 * Gets the local address at connection time.  This value will not change.
	 */
	public InetSocketAddress getLocalSocketAddress() {
		return localSocketAddress;
	}

	/**
	 * Gets the most recently seen local address.  This value may change.
	 */
	public InetSocketAddress getMostRecentLocalSocketAddress() {
		return mostRecentLocalSocketAddress;
	}

	/**
	 * Gets the remote address at connection time.  This value will not change.
	 */
	public InetSocketAddress getRemoteSocketAddress() {
		return remoteSocketAddress;
	}

	/**
	 * Gets the most recently seen remote address.  This value may change.
	 */
	public InetSocketAddress getMostRecentRemoteSocketAddress() {
		return mostRecentRemoteSocketAddress;
	}

	@Override
	public void close() throws IOException {
		throw new NotImplementedException("TODO");
	}

	public boolean isClosed() {
		throw new NotImplementedException("TODO");
	}

	/**
	 * Adds a listener.
	 *
	 * @throws IllegalStateException  If the listener has already been added
	 */
	public void addSocketListener(HttpSocketListener<? super HS> listener) throws IllegalStateException {
		synchronized(listener) {
			for(HttpSocketListener<? super HS> existing : listeners) {
				if(existing == listener) throw new IllegalStateException("listener already added");
			}
			listeners.add(listener);
		}
	}

	/**
	 * Removes a listener.
	 *
	 * @return true if the listener was found
	 */
	public boolean removeSocketListener(HttpSocketListener<? super HS> listener) {
		synchronized(listener) {
			for(int i=0, size=listeners.size(); i<size; i++) {
				HttpSocketListener<? super HS> existing = listeners.get(i);
				if(existing == listener) {
					listeners.remove(i);
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Sends a single message.  This will never block.
	 */
	public void sendMessage(String message) throws ClosedChannelException {
		Collections.singletonList(message);
	}

	/**
	 * Sends a set of messages.  This will never block.
	 * If the messages are empty, the request is ignored.
	 */
	public void sendMessages(Iterable<String> messages) throws ClosedChannelException {
		throw new NotImplementedException("TODO");
	}
}
