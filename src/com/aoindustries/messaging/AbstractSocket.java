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
package com.aoindustries.messaging;

import com.aoindustries.security.Identifier;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation of socket.
 */
abstract public class AbstractSocket implements Socket {

	private final SocketContext socketContext;

	private final Identifier id;

	private final long connectTime;

	private final    SocketAddress connectLocalSocketAddress;
	private volatile SocketAddress localSocketAddress;

	private final    SocketAddress connectRemoteSocketAddress;
	private volatile SocketAddress remoteSocketAddress;

	private final List<SocketListener> listeners = new ArrayList<SocketListener>();

	private final Object closeLock = new Object();
	private Long closeTime;

	protected AbstractSocket(
		SocketContext socketContext,
		Identifier id,
		long connectTime,
		SocketAddress localSocketAddress,
		SocketAddress remoteSocketAddress
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

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public long getConnectTime() {
		return connectTime;
	}

	@Override
	public Long getCloseTime() {
		synchronized(closeLock) {
			return closeTime;
		}
	}

	@Override
	public SocketAddress getConnectSocketAddress() {
		return connectLocalSocketAddress;
	}

	@Override
	public SocketAddress getLocalSocketAddress() {
		return localSocketAddress;
	}

	@Override
	public SocketAddress getConnectRemoteSocketAddress() {
		return connectRemoteSocketAddress;
	}

	@Override
	public SocketAddress getRemoteSocketAddress() {
		return remoteSocketAddress;
	}

	/**
	 * Any overriding implementation must call super.close() first.
	 */
	@Override
	public void close() throws IOException {
		synchronized(closeLock) {
			if(closeTime == null) {
				closeTime = System.currentTimeMillis();
				socketContext.onClose(this);
			}
		}
	}

	@Override
	public boolean isClosed() {
		return getCloseTime() != null;
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
	abstract public void sendMessages(Collection<? extends Message> messages) throws ClosedChannelException;
}
