/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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
package com.aoindustries.io.buffer;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;

/**
 * A simple implementation of BufferedWriter internally based on CharArrayWriter.
 *
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class CharArrayBufferWriter extends BufferWriter {

	/**
	 * The buffer used to capture data before switching to file-backed storage.
	 * Once closed, this buffer will not be modified.
	 */
    private CharArrayWriter buffer;

	/**
	 * Once closed, no further information may be written.
	 * Manipulations are only active once closed.
	 */
	private boolean isClosed = false;

	public CharArrayBufferWriter(int initialSize) {
        this.buffer = new CharArrayWriter(initialSize);
    }

    @Override
    public void write(int c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.write(c);
    }

    @Override
    public void write(char cbuf[]) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.write(cbuf);
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.write(cbuf, off, len);
    }

    @Override
    public void write(String str) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.write(str);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.write(str, off, len);
    }

    @Override
    public CharArrayBufferWriter append(CharSequence csq) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.append(csq);
        return this;
    }

    @Override
    public CharArrayBufferWriter append(CharSequence csq, int start, int end) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.append(csq, start, end);
        return this;
    }

    @Override
    public CharArrayBufferWriter append(char c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        buffer.append(c);
        return this;
    }

    @Override
    public void flush() throws IOException {
		buffer.flush();
    }

    @Override
    public void close() throws IOException {
		buffer.close();
		isClosed = true;
    }

	@Override
    public long getLength() {
        return buffer.size();
    }

    @Override
    public String toString() {
		return "CharArrayBufferWriter(length=" + buffer.size() + ")";
    }

	// The result is cached after first created
	private BufferResult result;

	@Override
	public BufferResult getResult() throws IllegalStateException {
		if(!isClosed) throw new IllegalStateException();
		if(result==null) {
			result =
				buffer.size()==0
				? EmptyResult.getInstance()
				: new CharArrayBufferResult(buffer)
			;
		}
		return result;
	}
}
