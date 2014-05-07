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

import com.aoindustries.io.AoByteArrayInputStream;
import com.aoindustries.io.AoByteArrayOutputStream;
import com.aoindustries.util.AoArrays;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * A message that is a combination of multiple messages.
 */
public class MultiMessage implements Message {

	private static final char DELIMITER = ',';

	private final Collection<? extends Message> messages;
	
	public MultiMessage(Collection<? extends Message> messages) {
		this.messages = messages;
	}

	/**
	 * Decodes the messages.
	 */
	MultiMessage(String encodedMessages) throws IOException {
		if(encodedMessages.isEmpty()) {
			messages = Collections.emptyList();
		} else {
			int pos = encodedMessages.indexOf(DELIMITER);
			if(pos == -1) throw new IllegalArgumentException("Delimiter not found");
			final int size = Integer.parseInt(encodedMessages.substring(0, pos++));
			List<Message> decodedMessages = new ArrayList<Message>(size);
			for(int i=0; i<size; i++) {
				MessageType type = MessageType.getFromTypeChar(encodedMessages.charAt(pos++));
				int nextPos = encodedMessages.indexOf(DELIMITER, pos);
				if(nextPos == -1) throw new IllegalArgumentException("Delimiter not found");
				final int capacity = Integer.parseInt(encodedMessages.substring(pos, nextPos++));
				pos = nextPos + capacity;
				decodedMessages.add(type.decode(encodedMessages.substring(nextPos, pos)));
			}
			if(pos != encodedMessages.length()) throw new IllegalArgumentException("pos != encodedMessages.length()");
			this.messages = Collections.unmodifiableList(decodedMessages);
		}
	}

	/**
	 * Decodes the messages.
	 */
	MultiMessage(byte[] encodedMessages) throws IOException {
		if(encodedMessages.length==0) {
			messages = Collections.emptyList();
		} else {
			DataInputStream in = new DataInputStream(new AoByteArrayInputStream(encodedMessages));
			try {
				final int size = in.readInt();
				List<Message> decodedMessages = new ArrayList<Message>(size);
				for(int i=0; i<size; i++) {
					MessageType type = MessageType.getFromTypeByte(in.readByte());
					final int capacity = in.readInt();
					byte[] encodedMessage = new byte[capacity];
					in.readFully(encodedMessage, 0, capacity);
					decodedMessages.add(type.decode(encodedMessage));
				}
				this.messages = Collections.unmodifiableList(decodedMessages);
			} finally {
				in.close();
			}
		}
	}

	@Override
	public String toString() {
		return "MultiMessage(" + messages.size() + ")";
	}

	@Override
	public MessageType getMessageType() {
		return MessageType.MULTI;
	}

	/**
	 * Encodes the messages into a single string.
	 */
	@Override
	public String getMessageAsString() throws IOException {
		final int size = messages.size();
		if(size == 0) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(size).append(DELIMITER);
		int count = 0;
		for(Message message : messages) {
			count++;
			String str = message.getMessageAsString();
			sb
				.append(message.getMessageType().getTypeChar())
				.append(str.length())
				.append(DELIMITER)
				.append(str)
			;
		}
		if(count != size) throw new ConcurrentModificationException();
		return sb.toString();
	}

	/**
	 * Encodes the messages into a single ByteBuffer.
	 * There is likely a more efficient implementation that reads-through, but this
	 * is a simple implementation.
	 */
	@Override
	public ByteBuffer getMessageAsByteBuffer() throws IOException {
		final int size = messages.size();
		if(size == 0) return ByteBuffer.wrap(AoArrays.EMPTY_BYTE_ARRAY);
		AoByteArrayOutputStream bout = new AoByteArrayOutputStream();
		try {
			DataOutputStream out = new DataOutputStream(bout);
			try {
				WritableByteChannel channel = Channels.newChannel(out);
				try {
					out.writeInt(size);
					int count = 0;
					for(Message message : messages) {
						count++;
						ByteBuffer bytes = message.getMessageAsByteBuffer();
						out.writeByte(message.getMessageType().getTypeByte());
						final int capacity = bytes.capacity();
						out.writeInt(capacity);
						int totalWritten = 0;
						while(totalWritten < capacity) {
							int written = channel.write(bytes);
							totalWritten += written;
						}
						if(totalWritten != capacity) throw new ConcurrentModificationException();
					}
					if(count != size) throw new ConcurrentModificationException();
				} finally {
					channel.close();
				}
			} finally {
				out.close();
			}
		} finally {
			bout.close();
		}
		return ByteBuffer.wrap(bout.getInternalByteArray(), 0, bout.size());
	}

	/**
	 * Closes each of the underlying messages.
	 */
	@Override
	public void close() throws IOException {
		for(Message message : messages) {
			message.close();
		}
	}

	public Collection<? extends Message> getMessages() {
		return messages;
	}
}
