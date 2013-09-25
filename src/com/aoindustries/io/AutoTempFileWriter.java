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

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.nio.charset.Charsets;
import com.aoindustries.util.WrappedException;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.ClosedChannelException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes to a StringBuilder then switches to a temp file when the
 * threshold is reached.
 *
 * Manipulations (like trim) may be performed on the buffer only after it is closed.
 *
 * For efficiency, the buffer should write it's contents to a Writer via <code>writeTo</code>
 * (with optional encoder).
 *
 * This class is not thread safe.
 *
 * @see  SegmentedBufferedWriter  for a possibly more efficient implementation.
 *
 * @author  AO Industries, Inc.
 */
public class AutoTempFileWriter extends AoBufferedWriter {

    private static final Logger logger = Logger.getLogger(AutoTempFileWriter.class.getName());

	private static final boolean DEBUG = false;

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
    private CharArrayWriter buffer;

	/**
	 * Once closed, no further information may be written.
	 * Manipulations are only active once closed.
	 */
	private boolean isClosed = false;

	// The temp file is in UTF16-BE encoding
    private TempFile tempFile;
    private Writer fileWriter;

	public AutoTempFileWriter(int initialSize, int tempFileThreshold) {
        if(tempFileThreshold<=initialSize) throw new IllegalArgumentException("tempFileThreshold must be > initialSize");
        this.tempFileThreshold = tempFileThreshold;
        this.length = 0;
        this.buffer = new CharArrayWriter(initialSize);
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): new");
    }

    private void switchIfNeeded(long newLength) throws IOException {
        if(buffer!=null && newLength>=tempFileThreshold) {
            tempFile = new TempFile("AutoTempFileWriter"/*, null, new File("/dev/shm")*/);
			if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): Switching to temp file: " + tempFile);
            if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Switching to temp file: {0}", tempFile);
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile.getFile()), Charsets.UTF_16BE));
			// Write buffer to file
			buffer.writeTo(fileWriter);
            buffer = null;
        }
    }

    @Override
    public void write(int c) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): write(int): " + (char)c);
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + 1;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(c);
        length = newLength;
    }

    @Override
    public void write(char cbuf[]) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): write(char[]): " + String.copyValueOf(cbuf));
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + cbuf.length;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(cbuf);
        length = newLength;
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): write(char[],int,int): " + String.copyValueOf(cbuf, off, len));
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + len;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(cbuf, off, len);
        length = newLength;
    }

    @Override
    public void write(String str) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): write(String): " + str);
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + str.length();
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(str);
        length = newLength;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): write(String,int,int): " + str.substring(off, off+len));
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + len;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).write(str, off, len);
        length = newLength;
    }

    @Override
    public AutoTempFileWriter append(CharSequence csq) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): append(CharSequence): " + csq);
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + csq.length();
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).append(csq);
        length = newLength;
        return this;
    }

    @Override
    public AutoTempFileWriter append(CharSequence csq, int start, int end) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): append(CharSequence,int,int): " + csq.subSequence(start, end));
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + (end-start);
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).append(csq, start, end);
        length = newLength;
        return this;
    }

    @Override
    public AutoTempFileWriter append(char c) throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): append(char): " + c);
		if(isClosed) throw new ClosedChannelException();
        long newLength = length+1;
        switchIfNeeded(newLength);
        (buffer!=null ? buffer : fileWriter).append(c);
        length = newLength;
        return this;
    }

    @Override
    public void flush() throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): flush()");
        if(fileWriter!=null) fileWriter.flush();
    }

    @Override
    public void close() throws IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): close(): length=" + length);
        if(fileWriter!=null) {
            fileWriter.close();
            fileWriter = null;
		}
		isClosed = true;
    }

	@Override
    public long getLength() {
        return length;
    }

	// When this is closed, the string representation is stored after first use.
	private String toStringCache;

    @Override
    public String toString() {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): toString()");
		if(!isClosed) {
			// When buffering, do not convert to strings
			return "AutoTempFileWriter(length=" + length + ")";
		} else {
			if(toStringCache==null) {
				if(buffer!=null) {
					toStringCache = buffer.toString();
				} else {
					try {
						logger.info("Creating String from temp file - benefits of AutoTempFileWriter negated.");
						if(length>Integer.MAX_VALUE) throw new RuntimeException("Buffer too large to convert to String: length="+length);
						StringBuilder sb = new StringBuilder((int)length);
						Reader in = new InputStreamReader(new FileInputStream(tempFile.getFile()), Charsets.UTF_16BE);
						try {
							IoUtils.copy(in, sb);
						} finally {
							in.close();
						}
						assert sb.length()==length : "sb.length()!=length: "+sb.length()+"!="+length;
						toStringCache = sb.toString();
					} catch(IOException err) {
						throw new WrappedException(err);
					}
				}
			}
			return toStringCache;
		}
    }

	@Override
    public void writeTo(MediaEncoder encoder, Writer out) throws IllegalStateException, IOException {
		if(DEBUG) {
			if(out instanceof AutoTempFileWriter) {
				System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): writeTo(MediaEncoder,AutoTempFileWriter)");
			} else if(out instanceof MediaWriter) {
				System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): writeTo(MediaEncoder,MediaWriter)");
			} else {
				System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): writeTo(MediaEncoder,Writer)");
			}
		}
		if(encoder==null) {
			writeTo(out);
		} else {
			if(!isClosed) throw new IllegalStateException();
			if(buffer!=null) {
				// TODO: If copying to another AutoTempFileWriter, we have a chance here to share segment list (current the StringBuilder)
				buffer.writeTo(new MediaWriter(encoder, out));
			} else {
				writeTo(new MediaWriter(encoder, out));
			}
		}
	}

	@Override
    public void writeTo(Writer out) throws IllegalStateException, IOException {
		if(DEBUG) {
			if(out instanceof AutoTempFileWriter) {
				System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): writeTo(AutoTempFileWriter)");
			} else if(out instanceof MediaWriter) {
				System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): writeTo(MediaWriter)");
			} else {
				System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): writeTo(Writer)");
			}
		}
		if(!isClosed) throw new IllegalStateException();
        if(buffer!=null) {
			// TODO: If copying to another AutoTempFileWriter, we have a chance here to share segment list (current the StringBuilder)
			buffer.writeTo(out);
        } else {
			// TODO: If copying to another AutoTempFileWriter, we have a chance here for disk-to-disk block level copying instead of going through all the conversions.
			Reader in = new InputStreamReader(new FileInputStream(tempFile.getFile()), Charsets.UTF_16BE);
			try {
				long totalRead = IoUtils.copy(in, out);
				assert totalRead==length : "totalRead!=length: "+totalRead+"!="+length;
			} finally {
				in.close();
			}
        }
    }

	@Override
	public AutoTempFileWriter trim() throws IllegalStateException, IOException {
		if(DEBUG) System.err.println("DEBUG: AutoTempFileWriter(" + System.identityHashCode(this) + "): trim()");
		if(!isClosed) throw new IllegalStateException();
		throw new NotImplementedException("TODO");
		/*
				RandomAccessFile raf = new RandomAccessFile(tempFile.getFile(), "r");
				try {
					long start = 0;
					long end = raf.length();
					assert end==(length*2)) : "end!=(length*2): "+end+"!=("+length+"*2)";
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
		*/
	}
}
