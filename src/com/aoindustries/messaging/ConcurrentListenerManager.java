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

import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.util.concurrent.ExecutorService;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides per-listener event queues, and fires off events concurrently across
 * listeners, but in-order per listener.
 */
class ConcurrentListenerManager<L> {

	private static final Logger logger = Logger.getLogger(ConcurrentListenerManager.class.getName());

	static interface Event<L> {
		/**
		 * Creates the Runnable that will call the event callback on the listener.
		 */
		Runnable createCall(L listener);
	}

	static class EventCall<L> {
		final Map<L,Boolean> unfinishedCalls;
		final Runnable call;
		EventCall(Map<L,Boolean> unfinishedCalls, Runnable call) {
			this.unfinishedCalls = unfinishedCalls;
			this.call = call;
		}
	}

	/**
	 * A queue of events per listener.  When the queue is null, no executors are running.
	 * When the queue is non-null, even when empty, an executor is running.
	 */
	private final Map<L,Queue<EventCall<L>>> listeners = new IdentityHashMap<L,Queue<EventCall<L>>>();

	private final ExecutorService executor;

	ConcurrentListenerManager(ExecutorService executor) {
		this.executor = executor;
	}

	void addListener(L listener) throws IllegalStateException {
		synchronized(listeners) {
			if(listeners.containsKey(listener)) throw new IllegalStateException("listener already added");
			listeners.put(listener, null);
		}
	}

	boolean removeListener(L listener) {
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
	 * Enqueues a new event to on all listeners event queues.
	 * If the caller needs to wait until the event has been handled by each
	 * of the listeners, can call .get() on the returned Future.
	 */
	Future<?> enqueueEvent(Event<? super L> event) {
		synchronized(listeners) {
			// The future is not finished until all individual calls have removed themselves from this map
			final Map<L,Boolean> unfinishedCalls = new IdentityHashMap<L,Boolean>();
			for(L listener : listeners.keySet()) {
				unfinishedCalls.put(listener, Boolean.TRUE);
			}
			for(Map.Entry<L,Queue<EventCall<L>>> entry : listeners.entrySet()) {
				final L listener = entry.getKey();
				Queue<EventCall<L>> queue = entry.getValue();
				boolean isFirst;
				if(queue == null) {
					queue = new LinkedList<EventCall<L>>();
					entry.setValue(queue);
					isFirst = true;
				} else {
					isFirst = false;
				}
				queue.add(new EventCall<L>(unfinishedCalls, event.createCall(listener)));
				if(isFirst) {
					executor.submitUnbounded(
						new Runnable() {
							@Override
							public void run() {
								while(true) {
									// Invoke each of the events until the queue is empty
									EventCall<L> eventCall;
									synchronized(listeners) {
										Queue<EventCall<L>> queue = listeners.get(listener);
										if(queue.isEmpty()) {
											// Remove the empty queue so a new executor will be submitted on next event
											listeners.remove(listener);
											return;
										} else {
											eventCall = queue.remove();
										}
									}
									// Run the event without holding the listeners lock
									try {
										eventCall.call.run();
									} catch(ThreadDeath TD) {
										throw TD;
									} catch(Throwable t) {
										logger.log(Level.SEVERE, null, t);
									}
									// Remove this listener from unfinished calls
									synchronized(eventCall.unfinishedCalls) {
										Boolean removedValue = eventCall.unfinishedCalls.remove(listener);
										// Notify when the last call completes
										if(eventCall.unfinishedCalls.isEmpty()) {
											eventCall.unfinishedCalls.notify();
										}
										if(removedValue == null) throw new AssertionError();
									}
								}
							}
						}
					);
				}
			}
			// This future will wait until unfinishedCalls is empty
			return new Future<Object>() {
				@Override
				public boolean cancel(boolean mayInterruptIfRunning) {
					// Not supported
					return false;
				}

				@Override
				public boolean isCancelled() {
					// Not supported
					return false;
				}

				@Override
				public boolean isDone() {
					synchronized(unfinishedCalls) {
						return unfinishedCalls.isEmpty();
					}
				}

				@Override
				public Object get() throws InterruptedException {
					synchronized(unfinishedCalls) {
						while(!unfinishedCalls.isEmpty()) {
							unfinishedCalls.wait();
						}
						return null;
					}
				}

				@Override
				public Object get(long timeout, TimeUnit unit) throws TimeoutException {
					throw new NotImplementedException();
				}
			};
		}
	}
}
