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

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Receives messages as they come in from the sockets.
 * Also notified on other important socket events.
 * <p>
 * None of the messages will be triggered concurrently on this listener;
 * however, different listeners may be notified in parallel.
 * This means, for example, that onClose will not happen while onMessages is being invoked.
 * </p>
 * <p>
 * The given socket will always represent the current state, while the events are
 * delivered in-order.  Thus, newLocalSocketAddress may not necessarily be the
 * same as the HttpSocket.getMostRecentLocalSocketAddress.
 * </p>
 */
public interface SocketListener {

	/**
	 * Called when one or more new messages arrive.
	 * Messages are always delivered in-order.
	 * At least one message will be provided.
	 * Subsequent messages will not be sent until this onMessage completes.
	 * 
	 * @param  messages  The unmodifiable list of messages in the order received
	 */
	void onMessages(Socket socket, List<String> messages);

	/**
	 * Called when a new local address is seen.
	 */
	void onLocalSocketAddressChange(
		Socket socket,
		InetSocketAddress oldLocalSocketAddress,
		InetSocketAddress newLocalSocketAddress
	);

	/**
	 * Called when a new remote address is seen.
	 */
	void onRemoteSocketAddressChange(
		Socket socket,
		InetSocketAddress oldRemoteSocketAddress,
		InetSocketAddress newRemoteSocketAddress
	);

	/**
	 * Called when a socket is closed.
	 * This will only be called once.
	 */
	void onSocketClose(Socket socket);
}
