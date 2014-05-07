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

import com.aoindustries.util.Base64Coder;
import java.nio.ByteBuffer;

/**
 * A message that is a byte[].
 */
public class ByteArrayMessage implements Message {

	private final byte[] message;
	private final int length;
	
	/**
	 * base-64 decodes the message.
	 */
	public ByteArrayMessage(String encodedMessage) {
		byte[] decoded = Base64Coder.decode(encodedMessage);
		this.message = decoded;
		this.length = decoded.length;
	}

	public ByteArrayMessage(byte[] message) {
		this(message, message.length);
	}

	public ByteArrayMessage(byte[] message, int length) {
		this.message = message;
		this.length = length;
	}

	@Override
	public String toString() {
		return "ByteArrayMessage(byte[" + length + "])";
	}

	@Override
	public MessageType getPreferredSerializationType() {
		return MessageType.BINARY;
	}

	/**
	 * base-64 encodes the message.
	 */
	@Override
	public String getMessageAsString() {
		return new String(Base64Coder.encode(message, length));
	}

	@Override
	public ByteBuffer getMessageAsByteBuffer() {
		return ByteBuffer.wrap(message, 0, length);
	}

	@Override
	public void close() {
		// Nothing to do
	}
}
