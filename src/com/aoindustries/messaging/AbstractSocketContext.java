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
import com.aoindustries.util.AoCollections;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of socket context.
 */
abstract public class AbstractSocketContext<S extends Socket> implements SocketContext {

	private final Map<Identifier,S> sockets = new LinkedHashMap<Identifier,S>();

	private final List<SocketContextListener> listeners = new ArrayList<SocketContextListener>();

	private volatile boolean closed;

	protected AbstractSocketContext() {
	}

	@Override
	public Map<Identifier,S> getSockets() {
		synchronized(sockets) {
			return AoCollections.unmodifiableCopyMap(sockets);
		}
	}

	/**
	 * Any overriding implementation must call super.close() first.
	 */
	@Override
	public void close() throws IOException {
		closed = true;
		synchronized(sockets) {
			for(S socket : sockets.values()) socket.close();
			sockets.clear();
		}
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void addSocketContextListener(SocketContextListener listener) throws IllegalStateException {
		synchronized(listener) {
			for(SocketContextListener existing : listeners) {
				if(existing == listener) throw new IllegalStateException("listener already added");
			}
			listeners.add(listener);
		}
	}

	@Override
	public boolean removeSocketContextListener(SocketContextListener listener) {
		synchronized(listener) {
			for(int i=0, size=listeners.size(); i<size; i++) {
				SocketContextListener existing = listeners.get(i);
				if(existing == listener) {
					listeners.remove(i);
					return true;
				}
			}
			return false;
		}
	}
}
