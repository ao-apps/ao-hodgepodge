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
import com.aoindustries.util.concurrent.ConcurrentListenerManager;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base implementation of socket.
 */
abstract public class AbstractSocket implements Socket {

	private static final Logger logger = Logger.getLogger(AbstractSocket.class.getName());

	private final SocketContext socketContext;

	private final Identifier id;

	private final long connectTime;

	private final    SocketAddress connectLocalSocketAddress;
	private final    Object        localSocketAddressLock = new Object();
	private          SocketAddress localSocketAddress;

	private final    SocketAddress connectRemoteSocketAddress;
	private final    Object        remoteSocketAddressLock = new Object();
	private          SocketAddress remoteSocketAddress;

	private final Object closeLock = new Object();
	private Long closeTime;

	private final ConcurrentListenerManager<SocketListener> listenerManager = new ConcurrentListenerManager<SocketListener>();

	protected AbstractSocket(
		SocketContext socketContext,
		Identifier id,
		long connectTime,
		SocketAddress localSocketAddress,
		SocketAddress remoteSocketAddress
	) {
		this.socketContext              = socketContext;
		this.id                         = id;
		this.connectTime                = connectTime;
		this.connectLocalSocketAddress  = localSocketAddress;
		this.localSocketAddress         = localSocketAddress;
		this.connectRemoteSocketAddress = remoteSocketAddress;
		this.remoteSocketAddress        = remoteSocketAddress;
	}

	@Override
	public String toString() {
		return getRemoteSocketAddress().toString();
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
		synchronized(localSocketAddressLock) {
			return localSocketAddress;
		}
	}

	/**
	 * Sets the most recently seen local address.
	 * If the provided value is different than the previous, will notify all listeners.
	 */
	protected void setLocalSocketAddress(final SocketAddress newLocalSocketAddress) {
		synchronized(localSocketAddressLock) {
			final SocketAddress oldLocalSocketAddress = this.localSocketAddress;
			if(!newLocalSocketAddress.equals(oldLocalSocketAddress)) {
				this.localSocketAddress = newLocalSocketAddress;
				listenerManager.enqueueEvent(
					new ConcurrentListenerManager.Event<SocketListener>() {
						@Override
						public Runnable createCall(final SocketListener listener) {
							return new Runnable() {
								@Override
								public void run() {
									listener.onLocalSocketAddressChange(
										AbstractSocket.this,
										oldLocalSocketAddress,
										newLocalSocketAddress
									);
								}
							};
						}
					}
				);
			}
		}
	}

	@Override
	public SocketAddress getConnectRemoteSocketAddress() {
		return connectRemoteSocketAddress;
	}

	@Override
	public SocketAddress getRemoteSocketAddress() {
		synchronized(remoteSocketAddressLock) {
			return remoteSocketAddress;
		}
	}

	/**
	 * Sets the most recently seen remote address.
	 * If the provided value is different than the previous, will notify all listeners.
	 */
	protected void setRemoteSocketAddress(final SocketAddress newRemoteSocketAddress) {
		synchronized(remoteSocketAddressLock) {
			final SocketAddress oldRemoteSocketAddress = this.remoteSocketAddress;
			if(!newRemoteSocketAddress.equals(oldRemoteSocketAddress)) {
				this.remoteSocketAddress = newRemoteSocketAddress;
				listenerManager.enqueueEvent(
					new ConcurrentListenerManager.Event<SocketListener>() {
						@Override
						public Runnable createCall(final SocketListener listener) {
							return new Runnable() {
								@Override
								public void run() {
									listener.onRemoteSocketAddressChange(
										AbstractSocket.this,
										oldRemoteSocketAddress,
										newRemoteSocketAddress
									);
								}
							};
						}
					}
				);
			}
		}
	}

	/**
	 * Any overriding implementation must call super.close() first.
	 */
	@Override
	public void close() throws IOException {
		boolean enqueueOnSocketClose;
		synchronized(closeLock) {
			if(closeTime == null) {
				closeTime = System.currentTimeMillis();
				enqueueOnSocketClose = true;
			} else {
				enqueueOnSocketClose = false;
			}
		}
		if(enqueueOnSocketClose) {
			// TODO: One per type: socketContext.onClose(this);
			Future<?> future = listenerManager.enqueueEvent(
				new ConcurrentListenerManager.Event<SocketListener>() {
					@Override
					public Runnable createCall(final SocketListener listener) {
						return new Runnable() {
							@Override
							public void run() {
								listener.onSocketClose(AbstractSocket.this);
							}
						};
					}
				}
			);
			try {
				future.get();
			} catch(ExecutionException e) {
				logger.log(Level.SEVERE, null, e);
			} catch(InterruptedException e) {
				logger.log(Level.SEVERE, null, e);
			}
		}
		listenerManager.close();
	}

	@Override
	public boolean isClosed() {
		return getCloseTime() != null;
	}

	@Override
	public void addSocketListener(SocketListener listener) throws IllegalStateException {
		listenerManager.addListener(listener);
	}

	@Override
	public boolean removeSocketListener(SocketListener listener) {
		return listenerManager.removeListener(listener);
	}

	/**
	 * When one or more new messages have arrived, call this to distribute to all of the listeners.
	 * If need to wait until all of the listeners have handled the messages, can call Future.get()
	 * or Future.isDone().
	 *
	 * @throws  IllegalStateException  if this socket is closed
	 */
	protected Future<?> callOnMessages(final List<String> messages) throws IllegalStateException {
		if(isClosed()) throw new IllegalStateException("Socket is closed");
		if(messages.isEmpty()) throw new IllegalArgumentException("messages may not be empty");
		return listenerManager.enqueueEvent(
			new ConcurrentListenerManager.Event<SocketListener>() {
				@Override
				public Runnable createCall(final SocketListener listener) {
					return new Runnable() {
						@Override
						public void run() {
							listener.onMessages(
								AbstractSocket.this,
								messages
							);
						}
					};
				}
			}
		);
	}

	@Override
	public void sendMessage(Message message) throws IllegalStateException {
		if(isClosed()) throw new IllegalStateException("Socket is closed");
		Collections.singletonList(message);
	}

	@Override
	public void sendMessages(Collection<? extends Message> messages) throws IllegalStateException {
		if(isClosed()) throw new IllegalStateException("Socket is closed");
		if(!messages.isEmpty()) sendMessagesImpl(messages);
	}

	/**
	 * Implementation to actually enqueue and send messages.
	 * This must never block.
	 */
	abstract protected void sendMessagesImpl(Collection<? extends Message> messages) throws IllegalStateException;
}
