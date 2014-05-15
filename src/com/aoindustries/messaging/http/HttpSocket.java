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

import com.aoindustries.messaging.AbstractSocket;
import com.aoindustries.messaging.AbstractSocketContext;
import com.aoindustries.messaging.Message;
import com.aoindustries.messaging.Socket;
import com.aoindustries.security.Identifier;
import com.aoindustries.util.concurrent.Callback;
import com.aoindustries.util.concurrent.ExecutorService;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * One established connection over HTTP.
 */
public class HttpSocket extends AbstractSocket {

	private static final Logger logger = Logger.getLogger(HttpSocket.class.getName());

	private static final boolean DEBUG = true;

	private final Object sendQueueLock = new Object();
	private Queue<Message> sendQueue;

	private final ExecutorService executor = ExecutorService.newInstance();

	private final URL endpoint;

	HttpSocket(
		AbstractSocketContext<? extends AbstractSocket> socketContext,
		Identifier id,
		long connectTime,
		URL endpoint
	) {
		super(
			socketContext,
			id,
			connectTime,
			new UrlSocketAddress(endpoint)
		);
		this.endpoint = endpoint;
	}

	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			executor.dispose();
		}
	}

	@Override
	protected void startImpl(
		final Callback<? super Socket> onStart,
		final Callback<? super Exception> onError
	) throws IllegalStateException {
		/* TODO
		synchronized(lock) {
			if(socket==null || in==null || out==null) throw new IllegalStateException();
			executor.submitUnbounded(
				new Runnable() {
					@Override
					public void run() {
						try {
							java.net.Socket socket;
							synchronized(lock) {
								socket = HttpSocket.this.socket;
							}
							if(socket==null) {
								if(onError!=null) onError.call(new SocketException("Socket is closed"));
							} else {
								// Handle incoming messages in a Thread, can try nio later
								executor.submitUnbounded(
									new Runnable() {
										@Override
										public void run() {
											try {
												while(true) {
													CompressedDataInputStream in;
													synchronized(lock) {
														// Check if closed
														in = HttpSocket.this.in;
														if(in==null) break;
													}
													final int size = in.readCompressedInt();
													List<Message> messages = new ArrayList<Message>(size);
													for(int i=0; i<size; i++) {
														MessageType type = MessageType.getFromTypeByte(in.readByte());
														int arraySize = in.readCompressedInt();
														byte[] array = new byte[arraySize];
														IoUtils.readFully(in, array, 0, arraySize);
														messages.add(
															type.decode(
																new ByteArray(
																	array,
																	arraySize
																)
															)
														);
													}
													callOnMessages(Collections.unmodifiableList(messages));
												}
											} catch(Exception exc) {
												if(!isClosed()) callOnError(exc);
											} finally {
												try {
													close();
												} catch(IOException e) {
													logger.log(Level.SEVERE, null, e);
												}
											}
										}
									}
								);
							}
							if(onStart!=null) onStart.call(HttpSocket.this);
						} catch(Exception exc) {
							if(onError!=null) onError.call(exc);
						}
					}
				}
			);
		}
		 */
	}

	@Override
	protected void sendMessagesImpl(Collection<? extends Message> messages) {
		if(DEBUG) System.err.println("DEBUG: HttpSocket: sendMessagesImpl: enqueuing " + messages.size() + " messages");
		synchronized(sendQueueLock) {
			// Enqueue asynchronous write
			boolean isFirst;
			if(sendQueue == null) {
				sendQueue = new LinkedList<Message>();
				isFirst = true;
			} else {
				isFirst = false;
			}
			sendQueue.addAll(messages);
			if(isFirst) {
				/* TODO
				if(DEBUG) System.err.println("DEBUG: HttpSocket: sendMessagesImpl: submitting runnable");
				// When the queue is first created, we submit the queue runner to the executor for queue processing
				// There is only one executor per queue, and on queue per socket
				executor.submitUnbounded(
					new Runnable() {
						@Override
						public void run() {
							try {
								final List<Message> messages = new ArrayList<Message>();
								while(true) {
									CompressedDataOutputStream out;
									synchronized(lock) {
										// Check if closed
										out = HttpSocket.this.out;
										if(out==null) break;
									}
									// Get all of the messages until the queue is empty
									synchronized(sendQueueLock) {
										if(sendQueue.isEmpty()) {
											if(DEBUG) System.err.println("DEBUG: HttpSocket: sendMessagesImpl: run: queue empty, flushing and returning");
											out.flush();
											// Remove the empty queue so a new executor will be submitted on next event
											sendQueue = null;
											break;
										} else {
											messages.addAll(sendQueue);
											sendQueue.clear();
										}
									}
									// Write the messages without holding the queue lock
									final int size = messages.size();
									if(DEBUG) System.err.println("DEBUG: HttpSocket: sendMessagesImpl: run: writing " + size + " messages");
									out.writeCompressedInt(size);
									for(int i=0; i<size; i++) {
										Message message = messages.get(i);
										out.writeByte(message.getMessageType().getTypeByte());
										ByteArray data = message.encodeAsByteArray();
										out.writeCompressedInt(data.size);
										out.write(data.array, 0, data.size);
									}
									messages.clear();
								}
							} catch(Exception exc) {
								if(!isClosed()) {
									if(DEBUG) System.err.println("DEBUG: HttpSocket: sendMessagesImpl: run: calling onError");
									callOnError(exc);
									try {
										close();
									} catch(IOException e) {
										logger.log(Level.SEVERE, null, e);
									}
								}
							}
						}
					}
				);
				 */
			}
		}
	}
}
