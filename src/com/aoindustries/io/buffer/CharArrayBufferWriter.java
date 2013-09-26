/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2012, 2013  AO Industries, Inc.
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

import com.aoindustries.io.AoCharArrayWriter;
import com.aoindustries.io.TempFile;
import com.aoindustries.nio.charset.Charsets;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.ClosedChannelException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes to a CharArrayBuffer then switches to a temp file when the threshold is reached.
 *
 * This class is not thread safe.
 *
 * @see  SegmentedBufferedWriter  for a possibly more efficient implementation.
 *
 * @author  AO Industries, Inc.
 */
public class CharArrayBufferWriter extends BufferWriter {

    private static final Logger logger = Logger.getLogger(CharArrayBufferWriter.class.getName());

	private final int tempFileThreshold;

	/**
	 * The length of the writer is the sum of the data written to the buffer.
	 * Once closed, this length will not be modified.
	 */
    private long length;

	/**
	 * The buffer used to capture data before switching to file-backed storage.
	 * Once closed, this buffer will not be modified.
	 */
    private AoCharArrayWriter buffer;

	/**
	 * Once closed, no further information may be written.
	 * Manipulations are only active once closed.
	 */
	private boolean isClosed = false;

	// The temp file is in UTF16-BE encoding
    private TempFile tempFile;
    private Writer fileWriter;

	public CharArrayBufferWriter(int initialSize, int tempFileThreshold) {
        if(tempFileThreshold<=initialSize) throw new IllegalArgumentException("tempFileThreshold must be > initialSize");
        this.tempFileThreshold = tempFileThreshold;
        this.length = 0;
        this.buffer = new AoCharArrayWriter(initialSize);
    }

    private void switchIfNeeded(long newLength) throws IOException {
        if(buffer!=null && newLength>=tempFileThreshold) {
            tempFile = new TempFile("CharArrayBufferWriter"/*, null, new File("/dev/shm")*/);
            if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Switching to temp file: {0}", tempFile);
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile.getFile()), Charsets.UTF_16BE));
			// Write buffer to file
			buffer.writeTo(fileWriter);
            buffer = null;
        }
    }

    @Override
    public void write(int c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + 1;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(c);
        length = newLength;
    }

    @Override
    public void write(char cbuf[]) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + cbuf.length;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(cbuf);
        length = newLength;
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + len;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(cbuf, off, len);
        length = newLength;
    }

    @Override
    public void write(String str) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + str.length();
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(str);
        length = newLength;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + len;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(str, off, len);
        length = newLength;
    }

    @Override
    public CharArrayBufferWriter append(CharSequence csq) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + csq.length();
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).append(csq);
        length = newLength;
        return this;
    }

    @Override
    public CharArrayBufferWriter append(CharSequence csq, int start, int end) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + (end-start);
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).append(csq, start, end);
        length = newLength;
        return this;
    }

    @Override
    public CharArrayBufferWriter append(char c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+1;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).append(c);
        length = newLength;
        return this;
    }

    @Override
    public void flush() throws IOException {
        if(fileWriter!=null) fileWriter.flush();
    }

	private static long biggest = 0;
    @Override
    public void close() throws IOException {
        if(fileWriter!=null) {
            fileWriter.close();
            fileWriter = null;
		}
		isClosed = true;
		long heap = buffer.getInternalCharArray().length * Character.SIZE;
		if(heap>biggest) {
			biggest = heap;
			System.err.println("CharArrayBufferWriter: Biggest heap: " + biggest);
		}
    }

	@Override
    public long getLength() {
        return length;
    }

    @Override
    public String toString() {
		return "CharArrayBufferWriter(length=" + length + ")";
    }

	// The result is cached after first created
	private BufferResult result;

	@Override
	public BufferResult getResult() throws IllegalStateException {
		if(!isClosed) throw new IllegalStateException();
		if(result==null) {
			if(length==0) {
				result = EmptyResult.getInstance();
			} else if(buffer!=null) {
				assert length == buffer.size();
				result = new CharArrayBufferResult(buffer, 0, buffer.size());
			} else if(tempFile!=null) {
				result = new TempFileResult(tempFile, 0, length);
			} else {
				throw new AssertionError();
			}
		}
		return result;
	}
}
