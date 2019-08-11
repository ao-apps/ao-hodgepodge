/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2016, 2017, 2019  AO Industries, Inc.
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
package com.aoindustries.io.stream;

import com.aoindustries.security.Identifier;
import com.aoindustries.security.SmallIdentifier;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.sql.UnmodifiableTimestamp;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

/**
 * Adds compressed data transfer to DataInputStream.  This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class StreamableInput extends DataInputStream {

	public StreamableInput(InputStream in) {
		super(in);
	}

	/**
	 * Reads a compressed integer from the stream.
	 *
	 * The 31 bit pattern is as follows:
	 * <pre>
	 * 5 bit   - 000SXXXX
	 * 13 bit  - 001SXXXX XXXXXXXX
	 * 22 bit  - 01SXXXXX XXXXXXXX XXXXXXXX
	 * 31 bit  - 1SXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
	 * </pre>
	 *
	 * @exception  EOFException if the end of file is reached
	 */
	public static int readCompressedInt(InputStream in) throws IOException {
		int b1=in.read();
		if(b1==-1) throw new EOFException();
		if((b1&0x80)!=0) {
			// 31 bit
			int b2=in.read();
			if(b2==-1) throw new EOFException();
			int b3=in.read();
			if(b3==-1) throw new EOFException();
			int b4=in.read();
			if(b4==-1) throw new EOFException();
			return
				((b1&0x40)==0 ? 0 : 0xc0000000)
				| ((b1&0x3f)<<24)
				| (b2<<16)
				| (b3<<8)
				| b4
			;
		} else if((b1&0x40)!=0) {
			// 22 bit
			int b2=in.read();
			if(b2==-1) throw new EOFException();
			int b3=in.read();
			if(b3==-1) throw new EOFException();
			return
				((b1&0x20)==0 ? 0 : 0xffe00000)
				| ((b1&0x1f)<<16)
				| (b2<<8)
				| b3
			;
		} else if((b1&0x20)!=0) {
			// 13 bit
			int b2=in.read();
			if(b2==-1) throw new EOFException();
			return
				((b1&0x10)==0 ? 0 : 0xfffff000)
				| ((b1&0x0f)<<8)
				| b2
			;
		} else {
			// 5 bit
			return
				((b1&0x10)==0 ? 0 : 0xfffffff0)
				| (b1&0x0f)
			;
		}
	}

	/**
	 * Reads a compressed integer from the stream.
	 *
	 * The 31 bit pattern is as follows:
	 * <pre>
	 * 5 bit   - 000SXXXX
	 * 13 bit  - 001SXXXX XXXXXXXX
	 * 22 bit  - 01SXXXXX XXXXXXXX XXXXXXXX
	 * 31 bit  - 1SXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX
	 * </pre>
	 *
	 * @exception  EOFException if the end of file is reached
	 */
	public int readCompressedInt() throws IOException {
		return readCompressedInt(in);
	}

	private String[] lastStrings;
	private int[] lastCommonLengths;

	/**
	 * @exception  EOFException if the end of file is reached
	 */
	public String readCompressedUTF() throws IOException {
		int b1=in.read();
		if(b1==-1) throw new EOFException();
		int slot=b1&0x3f;

		// Is there a difference to the common
		if(lastCommonLengths==null) lastCommonLengths=new int[64];
		if((b1&0x80)!=0) {
			int diff=readCompressedInt();
			if(diff>=0) diff++;
			lastCommonLengths[slot]+=diff;
		}

		// Is there a suffix String
		int common=lastCommonLengths[slot];
		if(lastStrings==null) lastStrings=new String[64];
		if((b1&0x40)!=0) {
			String suffix=readUTF();
			if(common==0) return lastStrings[slot]=suffix;
			else {
				String last = lastStrings[slot];
				if(last==null) last="";
				String combined =
					new StringBuilder(common + suffix.length())
					.append(last, 0, common)
					.append(suffix)
					.toString();
				lastStrings[slot] = combined;
				return combined;
			}
		} else {
			String last=lastStrings[slot];
			if(last==null) last="";
			if(common==last.length()) {
				return last;
			} else {
				return lastStrings[slot]=last.substring(0, common);
			}
		}
	}

	public String readNullUTF() throws IOException {
		return readBoolean() ? readUTF() : null;
	}

	/**
	 * Reads a string of any length.
	 */
	public String readLongUTF() throws IOException {
		int length = readCompressedInt();
		StringBuilder SB = new StringBuilder(length);
		for(int position = 0; position<length; position+=20480) {
			int expectedLen = length - position;
			if(expectedLen>20480) expectedLen = 20480;
			String block = readUTF();
			if(block.length()!=expectedLen) throw new IOException("Block has unexpected length: expected "+expectedLen+", got "+block.length());
			SB.append(block);
		}
		if(SB.length()!=length) throw new IOException("StringBuilder has unexpected length: expected "+length+", got "+SB.length());
		return SB.toString();
	}

	/**
	 * Reads a string of any length, supporting <code>null</code>.
	 */
	public String readNullLongUTF() throws IOException {
		return readBoolean() ? readLongUTF() : null;
	}

	public Byte readNullByte() throws IOException {
		return readBoolean() ? readByte() : null;
	}

	public Short readNullShort() throws IOException {
		return readBoolean() ? readShort() : null;
	}

	public Integer readNullInteger() throws IOException {
		return readBoolean() ? readInt() : null;
	}

	public Long readNullLong() throws IOException {
		return readBoolean() ? readLong() : null;
	}

	/**
	 * Reads an {@link Enum}, represented by its {@link Enum#name()}.
	 */
	public <T extends Enum<T>> T readEnum(Class<T> enumType) throws IOException {
		try {
			return Enum.valueOf(enumType, readUTF());
		} catch(IllegalArgumentException err) {
			throw new IOException(err);
		}
	}

	/**
	 * Reads an {@link Enum}, represented by its {@link Enum#name()},
	 * supporting {@code null}.
	 */
	public <T extends Enum<T>> T readNullEnum(Class<T> enumType) throws IOException {
		try {
			return readBoolean() ? Enum.valueOf(enumType, readUTF()) : null;
		} catch(IllegalArgumentException err) {
			throw new IOException(err);
		}
	}

	public Boolean readNullBoolean() throws IOException {
		byte b = readByte();
		if(b == -1) return null;
		if(b == 1) return Boolean.TRUE;
		if(b == 0) return Boolean.FALSE;
		throw new IOException("Invalid value for nullable boolean: " + b);
	}

	/**
	 * Reads a {@link Timestamp}, maintaining the full nanosecond precision.
	 * Time zone offset is not maintained.
	 * <p>
	 * See  {@link StreamableOutput#writeTimestamp(java.sql.Timestamp, java.io.DataOutputStream)} for wire protocol details.
	 * </p>
	 */
	public static Timestamp readTimestamp(DataInputStream in) throws IOException {
		long seconds = in.readLong();
		int nanos = readCompressedInt(in);
		return SQLUtility.newTimestamp(seconds, nanos, IOException.class);
	}

	/**
	 * Reads a {@link Timestamp}.
	 *
	 * @see  #readTimestamp(java.io.DataInputStream)
	 */
	public Timestamp readTimestamp() throws IOException {
		return readTimestamp(this);
	}

	/**
	 * Reads a possibly-{@code null} {@link Timestamp}.
	 *
	 * @see  #readTimestamp()
	 */
	public Timestamp readNullTimestamp() throws IOException {
		return readBoolean() ? readTimestamp() : null;
	}

	/**
	 * Reads an {@link UnmodifiableTimestamp}, maintaining the full nanosecond precision.
	 * Time zone offset is not maintained.
	 * <p>
	 * See  {@link StreamableOutput#writeTimestamp(java.sql.Timestamp, java.io.DataOutputStream)} for wire protocol details.
	 * </p>
	 */
	public static UnmodifiableTimestamp readUnmodifiableTimestamp(DataInputStream in) throws IOException {
		long seconds = in.readLong();
		int nanos = readCompressedInt(in);
		return SQLUtility.newUnmodifiableTimestamp(seconds, nanos, IOException.class);
	}

	/**
	 * Reads an {@link UnmodifiableTimestamp}.
	 *
	 * @see  #readUnmodifiableTimestamp(java.io.DataInputStream)
	 */
	public UnmodifiableTimestamp readUnmodifiableTimestamp() throws IOException {
		return readUnmodifiableTimestamp(this);
	}

	/**
	 * Reads a possibly-{@code null} {@link UnmodifiableTimestamp}.
	 *
	 * @see  #readUnmodifiableTimestamp()
	 */
	public UnmodifiableTimestamp readNullUnmodifiableTimestamp() throws IOException {
		return readBoolean() ? readUnmodifiableTimestamp() : null;
	}

	/**
	 * Reads an {@link Identifier}.
	 */
	public Identifier readIdentifier() throws IOException {
		return new Identifier(readLong(), readLong());
	}

	/**
	 * Reads a possibly-{@code null} {@link Identifier}.
	 */
	public Identifier readNullIdentifier() throws IOException {
		return readBoolean() ? readIdentifier() : null;
	}

	/**
	 * Reads a {@link SmallIdentifier}.
	 */
	public SmallIdentifier readSmallIdentifier() throws IOException {
		return new SmallIdentifier(readLong());
	}

	/**
	 * Reads a possibly-{@code null} {@link Identifier}.
	 */
	public SmallIdentifier readNullSmallIdentifier() throws IOException {
		return readBoolean() ? readSmallIdentifier() : null;
	}
}
