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

import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.messaging.AbstractSocket;
import com.aoindustries.messaging.Message;
import com.aoindustries.util.concurrent.ExecutorService;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * One established connection over a socket.
 */
public class TcpSocket extends AbstractSocket {

	private final Queue<Message> sendQueue = new LinkedList<Message>();

	public TcpSocket(InetAddress address, int port) throws IOException {
		this(new Socket(address, port));
	}

	public TcpSocket(String host, int port) throws IOException {
		this(new Socket(host, port));
	}

	public TcpSocket(Socket socket) {
		super(System.currentTimeMillis(), socket.getLocalSocketAddress(), socket.getRemoteSocketAddress());
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			executorService.dispose();
		}
	}

	@Override
	public void sendMessages(Collection<? extends Message> messages) throws ClosedChannelException {
		synchronized(sendQueue) {
			sendQueue.addAll(messages);
		}
	}
}
