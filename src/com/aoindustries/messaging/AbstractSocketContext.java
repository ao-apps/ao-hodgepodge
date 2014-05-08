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
import com.aoindustries.util.concurrent.ExecutorService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base implementation of socket context.
 */
abstract public class AbstractSocketContext<S extends Socket> implements SocketContext {

	private static final Logger logger = Logger.getLogger(AbstractSocketContext.class.getName());

	private final Map<Identifier,S> sockets = new LinkedHashMap<Identifier,S>();

	private final Object closeLock = new Object();
	private boolean closed;

	private final ExecutorService executor = ExecutorService.newInstance();

	private final ConcurrentListenerManager<SocketContextListener> listenerManager = new ConcurrentListenerManager<SocketContextListener>(executor);

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
	public void close() {
		boolean enqueueOnSocketContextClose;
		synchronized(closeLock) {
			if(!closed) {
				closed = true;
				enqueueOnSocketContextClose = true;
			} else {
				enqueueOnSocketContextClose = false;
			}
		}
		List<S> socketsToClose;
		synchronized(sockets) {
			// Gets a copy of the sockets to avoid concurrent modification exception and avoid holding lock
			socketsToClose = new ArrayList<S>(sockets.values());
		}
		for(S socket : socketsToClose) {
			try {
				socket.close();
			} catch(ThreadDeath td) {
				throw td;
			} catch(Throwable t) {
				logger.log(Level.SEVERE, null, t);
			}
		}
		if(enqueueOnSocketContextClose) {
			listenerManager.enqueueEvent(
				new ConcurrentListenerManager.Event<SocketContextListener>() {
					@Override
					public Runnable createCall(final SocketContextListener listener) {
						return new Runnable() {
							@Override
							public void run() {
								listener.onSocketContextClose(AbstractSocketContext.this);
							}
						};
					}
				}
			);
		}
		executor.dispose();
	}

	@Override
	public boolean isClosed() {
		synchronized(closeLock) {
			return closed;
		}
	}

	@Override
	public void addSocketContextListener(SocketContextListener listener) throws IllegalStateException {
		listenerManager.addListener(listener);
	}

	@Override
	public boolean removeSocketContextListener(SocketContextListener listener) {
		return listenerManager.removeListener(listener);
	}

	/**
	 * Adds a new socket to this context, sockets must be added to the context
	 * before they create any of their own events.  This gives context listeners
	 * a chance to register per-socket listeners in "onNewSocket".
	 *
	 * First, adds to the list of sockets.
	 * Second, calls all listeners notifying them of new socket.
	 * Third, waits for all listeners to handle the event before returning.
	 */
	protected void addSocket(final S newSocket) {
		Future<?> future;
		synchronized(sockets) {
			Identifier id = newSocket.getId();
			if(sockets.containsKey(id)) throw new IllegalStateException("Socket with the same ID has already been added");
			sockets.put(id, newSocket);
			future = listenerManager.enqueueEvent(
				new ConcurrentListenerManager.Event<SocketContextListener>() {
					@Override
					public Runnable createCall(final SocketContextListener listener) {
						return new Runnable() {
							@Override
							public void run() {
								listener.onNewSocket(AbstractSocketContext.this, newSocket);
							}
						};
					}
				}
			);
		}
		try {
			future.get();
		} catch(ExecutionException e) {
			logger.log(Level.SEVERE, null, e);
		} catch(InterruptedException e) {
			logger.log(Level.SEVERE, null, e);
		}
	}
}
