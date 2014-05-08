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

import java.io.IOException;

/**
 * The types of serializations.
 */
public enum MessageType {

	BYTE_ARRAY {
		@Override
		byte getTypeByte() {
			return 0;
		}

		@Override
		char getTypeChar() {
			return 'b';
		}

		@Override
		ByteArrayMessage decode(String encodedMessage) {
			return ByteArrayMessage.decode(encodedMessage);
		}

		@Override
		ByteArrayMessage decode(ByteArray encodedMessage) {
			return new ByteArrayMessage(encodedMessage);
		}
	},
	FILE {
		@Override
		byte getTypeByte() {
			return 1;
		}

		@Override
		char getTypeChar() {
			return 'f';
		}

		@Override
		FileMessage decode(String encodedMessage) throws IOException {
			return FileMessage.decode(encodedMessage);
		}

		@Override
		FileMessage decode(ByteArray encodedMessage) throws IOException {
			return FileMessage.decode(encodedMessage);
		}
	},
	STRING {
		@Override
		byte getTypeByte() {
			return 2;
		}

		@Override
		char getTypeChar() {
			return 's';
		}

		@Override
		StringMessage decode(String encodedMessage) {
			return new StringMessage(encodedMessage);
		}

		@Override
		StringMessage decode(ByteArray encodedMessage) {
			return StringMessage.decode(encodedMessage);
		}
	},
	MULTI {
		@Override
		byte getTypeByte() {
			return 3;
		}

		@Override
		char getTypeChar() {
			return 'm';
		}

		@Override
		MultiMessage decode(String encodedMessage) throws IOException {
			return MultiMessage.decode(encodedMessage);
		}

		@Override
		MultiMessage decode(ByteArray encodedMessage) throws IOException {
			return MultiMessage.decode(encodedMessage);
		}
	};

	public static MessageType getFromTypeByte(byte typeByte) {
		switch(typeByte) {
			case 0 : return BYTE_ARRAY;
			case 1 : return FILE;
			case 2 : return STRING;
			case 3 : return MULTI;
			default : throw new IllegalArgumentException("Invalid type byte: " + typeByte);
		}
	}

	public static MessageType getFromTypeChar(char typeChar) {
		switch(typeChar) {
			case 'b' : return BYTE_ARRAY;
			case 'f' : return FILE;
			case 's' : return STRING;
			case 'm' : return MULTI;
			default : throw new IllegalArgumentException("Invalid type char: " + typeChar);
		}
	}

	abstract byte getTypeByte();

	abstract char getTypeChar();

	/**
	 * Constructs a message of this type from its string encoding.
	 */
	abstract Message decode(String encodedMessage) throws IOException;

	/**
	 * Constructs a message of this type from its byte array encoding.
	 */
	abstract Message decode(ByteArray encodedMessage) throws IOException;
}
