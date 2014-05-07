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

import com.aoindustries.nio.charset.Charsets;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * A message that is a String.
 */
public class StringMessage implements Message {

	private static final Charset CHARSET = Charsets.UTF_8;

	private final String message;
	
	public StringMessage(String message) {
		this.message = message;
	}

	public StringMessage(byte[] encodedMessage) {
		this.message = new String(encodedMessage, CHARSET);
	}

	@Override
	public String toString() {
		return "StringMessage(\"" + message + "\")";
	}

	@Override
	public SerializationType getPreferredSerializationType() {
		return SerializationType.TEXT;
	}

	@Override
	public String getMessageAsString() {
		return message;
	}

	/**
	 * UTF-8 encodes the message.
	 */
	@Override
	public ByteBuffer getMessageAsByteBuffer() {
		return ByteBuffer.wrap(
			message.getBytes(CHARSET)
		);
	}
}
