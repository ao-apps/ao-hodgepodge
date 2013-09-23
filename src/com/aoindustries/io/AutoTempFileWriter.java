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
package com.aoindustries.io;

import com.aoindustries.nio.charset.Charsets;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.ref.ReferenceCount;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.persistent.PersistentCollections;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.ClosedChannelException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes to a StringBuilder then switches to a temp file when the
 * threshold is reached.  This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class AutoTempFileWriter
	extends Writer
	implements ReferenceCount<IOException>
{

    private static final Logger logger = Logger.getLogger(AutoTempFileWriter.class.getName());

    private final int tempFileThreshold;

    private long length;

    private StringBuilder sb;

	private boolean isClosed = false;
	// The temp file is in UTF16-BE encoding
    private File tempFile;
    private Writer fileWriter;

	// Set to true the first time this is trimmed
	private boolean trimmed = false;

	private int referenceCount = 1;

	public AutoTempFileWriter(int initialCapacity, int tempFileThreshold) {
        if(tempFileThreshold<=initialCapacity) throw new IllegalArgumentException("tempFileThreshold must be > initialCapacity");
        this.tempFileThreshold = tempFileThreshold;
        length = 0;
        sb = new StringBuilder(initialCapacity);
    }

    private void switchIfNeeded(long newLength) throws IOException {
        if(sb!=null && newLength>=tempFileThreshold) {
            tempFile = File.createTempFile("AutoTempFileWriter", null/*, new File("/dev/shm")*/);
            tempFile.deleteOnExit();
            if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Switching to temp file: {0}", tempFile);
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), Charsets.UTF_16BE));
            fileWriter.write(sb.toString());
            sb = null;
        }
    }

    @Override
    public void write(int c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+1;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append((char)c);
        else fileWriter.write(c);
        length = newLength;
    }

    @Override
    public void write(char cbuf[]) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+cbuf.length;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(cbuf);
        else fileWriter.write(cbuf);
        length = newLength;
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+len;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(cbuf, off, len);
        else fileWriter.write(cbuf, off, len);
        length = newLength;
    }

    @Override
    public void write(String str) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+str.length();
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(str);
        else fileWriter.write(str);
        length = newLength;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+len;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(str, off, off+len);
        else fileWriter.write(str, off, len);
        length = newLength;
    }

    @Override
    public AutoTempFileWriter append(CharSequence csq) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+csq.length();
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(csq);
        else fileWriter.append(csq);
        length = newLength;
        return this;
    }

    @Override
    public AutoTempFileWriter append(CharSequence csq, int start, int end) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+(end-start);
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(csq, start, end);
        else fileWriter.append(csq, start, end);
        length = newLength;
        return this;
    }

    @Override
    public AutoTempFileWriter append(char c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+1;
        switchIfNeeded(newLength);
        if(sb!=null) sb.append(c);
        else fileWriter.append(c);
        length = newLength;
        return this;
    }

    @Override
    public void flush() throws IOException {
        if(fileWriter!=null) fileWriter.flush();
    }

    @Override
    public void close() throws IOException {
        if(fileWriter!=null) {
            fileWriter.close();
            fileWriter = null;
		}
		isClosed = true;
    }

    /**
     * Gets the current length of the buffer in characters.
     */
    public long getLength() {
        return length;
    }

    /**
     * Gets the captured body as a string.  For larger amounts of data, especially when
     * in excess of <code>tempFileThreshold</code>, it is much more efficient to call
     * the <code>writeTo</code> method.
     *
     * @see  #tempFileThreshold
     * @see  #writeTo(java.io.Writer)
	 * @see  #trim()
     */
    @Override
    public String toString() {
		String str;
        if(sb!=null) {
            str = sb.toString();
        } else {
            try {
                logger.info("Creating String from temp file - benefits of AutoTempFileWriter negated.");
                if(length>Integer.MAX_VALUE) throw new RuntimeException("Buffer too large to convert to String: length="+length);
                StringBuilder toStringResult = new StringBuilder((int)length);
                flush();
                Reader in = new InputStreamReader(new FileInputStream(tempFile), Charsets.UTF_16BE);
                try {
                    IoUtils.copy(in, toStringResult);
                    if(toStringResult.length()!=length) throw new AssertionError("toStringResult.length()!=length: "+toStringResult.length()+"!="+length);
                } finally {
                    in.close();
                }
                str = toStringResult.toString();
            } catch(IOException err) {
                throw new WrappedException(err);
            }
        }
		if(trimmed) str = str.trim();
		return str;
    }

    /**
     * Writes the captured body to the provided writer.
     */
    public void writeTo(Writer out) throws IOException {
        if(sb!=null) {
			String str = sb.toString();
			if(trimmed) str = str.trim();
            out.write(str);
        } else {
			// TODO: If copying to another AutoTempFileWriter, we have a chance here for disk-to-disk block level copying instead of going through all the conversions.
            flush();
			if(trimmed) {
				RandomAccessFile raf = new RandomAccessFile(tempFile, "r");
				try {
					long start = 0;
					long end = raf.length();
					if(end!=(length*2)) throw new AssertionError("end!=(length*2): "+end+"!=("+length+"*2)");
					// Skip past the beginning whitespace characters
					raf.seek(0);
					while(start<end) {
						char ch = raf.readChar();
						if(ch>' ') break;
						start += 2;
					}
					// Skip past the ending whitespace characters
					while(end>start) {
						raf.seek(end-2);
						char ch = raf.readChar();
						if(ch>' ') break;
						end -= 2;
					}
					// Convert remaining block
					if(start<end) {
						byte[] bytes = BufferManager.getBytes();
						try {
							char[] chars = BufferManager.getChars();
							try {
								raf.seek(start);
								while(start<end) {
									// Read a block
									long blockSizeLong = end - start;
									int blockSize = blockSizeLong > BufferManager.BUFFER_SIZE ? BufferManager.BUFFER_SIZE : (int)blockSizeLong;
									assert (blockSize&1) == 0 : "Must be an even number for UTF-16 conversion";
									raf.readFully(bytes, 0, blockSize);
									// Convert to characters
									for(
										int bpos=0, cpos=0;
										bpos<blockSize;
										bpos+=2, cpos++
									) {
										chars[cpos] = PersistentCollections.bufferToChar(bytes, bpos);
									}
									// Write to output
									out.write(chars, 0, blockSize>>1);
								}
							} finally {
								BufferManager.release(chars, false);
							}
						} finally {
							BufferManager.release(bytes, false);
						}
					}
				} finally {
					raf.close();
				}
			} else {
				Reader in = new InputStreamReader(new FileInputStream(tempFile), Charsets.UTF_16BE);
				try {
					long totalRead = IoUtils.copy(in, out);
					if(totalRead!=length) throw new AssertionError("totalRead!=length: "+totalRead+"!="+length);
				} finally {
					in.close();
				}
			}
        }
    }

    /**
     * Deletes the internal buffers.  This object should not be used after this call.
	 * When using reference counting, this is called when referenceCount becomes zero.
     */
    public void delete() throws IOException {
        sb = null;
        close();
        if(tempFile!=null) {
            FileUtils.delete(tempFile);
            tempFile = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            delete();
        } finally {
            super.finalize();
        }
    }

	@Override
	public void incReferenceCount() throws IllegalStateException {
		if(referenceCount==0) throw new IllegalStateException();
		// Catch overflow
		if(referenceCount==Integer.MAX_VALUE) throw new ArithmeticException();
		referenceCount++;
	}

	@Override
	public void decReferenceCount() throws IllegalStateException, IOException {
		if(referenceCount==0) throw new IllegalStateException();
		if(--referenceCount == 0) {
			delete();
		}
	}
	
	/**
	 * Trims the contents of this writer.
	 */
	public void trim() {
		trimmed = true;
	}
}
