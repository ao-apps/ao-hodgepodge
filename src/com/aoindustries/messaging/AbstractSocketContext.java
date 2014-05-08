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
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base implementation of socket context.
 */
abstract public class AbstractSocketContext<S extends Socket> implements SocketContext {

	private static final Logger logger = Logger.getLogger(AbstractSocketContext.class.getName());

	private final Map<Identifier,S> sockets = new LinkedHashMap<Identifier,S>();

	/**
	 * A queue of events per listener.  When the queue is null, no executors are running.
	 * When the queue is non-null, even when empty, an executor is running.
	 */
	private final Map<SocketContextListener,Queue<Runnable>> listeners = new IdentityHashMap<SocketContextListener,Queue<Runnable>>();

	private volatile boolean closed;

	private final ExecutorService executor = ExecutorService.newInstance();

	protected AbstractSocketContext() {
		// On startup get the executor
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
		executor.dispose();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void addSocketContextListener(SocketContextListener listener) throws IllegalStateException {
		synchronized(listeners) {
			if(listeners.containsKey(listener)) throw new IllegalStateException("listener already added");
			listeners.put(listener, null);
		}
	}

	@Override
	public boolean removeSocketContextListener(SocketContextListener listener) {
		synchronized(listeners) {
			if(!listeners.containsKey(listener)) {
				return false;
			} else {
				listeners.remove(listener);
				return true;
			}
		}
	}

	/**
	 * Enqueues calls to onNewSocket on all listeners.
	 */
	protected void enqueueOnNewSocket(final S newSocket) {
		synchronized(listeners) {
			for(Map.Entry<SocketContextListener,Queue<Runnable>> entry : listeners.entrySet()) {
				final SocketContextListener listener = entry.getKey();
				Queue<Runnable> queue = entry.getValue();
				boolean isFirst = queue == null;
				if(isFirst) {
					queue = new LinkedList<Runnable>();
					entry.setValue(queue);
				}
				queue.add(
					new Runnable() {
						@Override
						public void run() {
							listener.onNewSocket(AbstractSocketContext.this, newSocket);
						}
					}
				);
				if(isFirst) {
					executor.submitUnbounded(
						new Runnable() {
							@Override
							public void run() {
								while(true) {
									// Invoke each of the events until the queue is empty
									Runnable event;
									synchronized(listeners) {
										Queue<Runnable> queue = listeners.get(listener);
										if(queue.isEmpty()) {
											// Remove the empty queue so a new executor will be submitted on next event
											listeners.remove(listener);
											return;
										} else {
											event = queue.remove();
										}
									}
									// Run the event without holding the listeners lock
									try {
										event.run();
									} catch(ThreadDeath TD) {
										throw TD;
									} catch(Throwable t) {
										logger.log(Level.SEVERE, null, t);
									}
								}
							}
						}
					);
				}
			}
		}
	}
}
