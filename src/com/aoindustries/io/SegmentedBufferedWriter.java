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
package com.aoindustries.io;

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.lang.NotImplementedException;
import com.aoindustries.nio.charset.Charsets;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.persistent.PersistentCollections;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.channels.ClosedChannelException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Buffers all writes while switching to a temp file when the
 * threshold is reached.
 *
 * Manipulations (like trim) may be performed on the buffer only after it is closed.
 *
 * For efficiency, the buffer should write it's contents to a Writer via <code>writeTo</code>
 * (with optional encoder).  If writing to another segmented buffer, the segments will be
 * shared between the two instances.
 *
 * This class is not thread safe.
 * 
 * @see  AutoTempFileWriter  This draws heavily from experienced gained in <code>AutoTempFileWriter</code>, but with the addition of segments.
 *
 * @author  AO Industries, Inc.
 */
public class SegmentedBufferedWriter extends AoBufferedWriter {

    private static final Logger logger = Logger.getLogger(SegmentedBufferedWriter.class.getName());

	private static final boolean DEBUG = true;

	/**
	 * This debug flag forces all operations to be on temp files.
	 */
	private static final boolean DEBUG_FILE = false;

	private static final byte[] EMPTY_BYTES = new byte[0];
	private static final Object[] EMPTY_OBJECTS = new Object[0];

	/**
	 * The set of internal types supported.
	 */
	private static final byte
		TYPE_STRING = 1,
		TYPE_CHAR_NEWLINE = 2,
		TYPE_CHAR_QUOTE = 3,
		TYPE_CHAR_APOS = 4,
		TYPE_CHAR_OTHER = 5
	;

	private static String toString(byte type, Object value) {
		switch(type) {
			case TYPE_STRING :
				return (String)value;
			case TYPE_CHAR_NEWLINE :
				return "\n";
			case TYPE_CHAR_QUOTE :
				return "\"";
			case TYPE_CHAR_APOS :
				return "'";
			case TYPE_CHAR_OTHER :
				return String.valueOf(((Character)value).charValue());
			default :
				throw new AssertionError();
		}
	}

	private static void append(byte type, Object value, StringBuilder buffer) {
		switch(type) {
			case TYPE_STRING :
				buffer.append((String)value);
				break;
			case TYPE_CHAR_NEWLINE :
				buffer.append('\n');
				break;
			case TYPE_CHAR_QUOTE :
				buffer.append('"');
				break;
			case TYPE_CHAR_APOS :
				buffer.append('\'');
				break;
			case TYPE_CHAR_OTHER :
				buffer.append(((Character)value).charValue());
				break;
			default :
				throw new AssertionError();
		}
	}

	private static void writeSegment(byte type, Object value, Writer out) throws IOException {
		switch(type) {
			case TYPE_STRING :
				out.write((String)value);
				break;
			case TYPE_CHAR_NEWLINE :
				out.write('\n');
				break;
			case TYPE_CHAR_QUOTE :
				out.write('"');
				break;
			case TYPE_CHAR_APOS :
				out.write('\'');
				break;
			case TYPE_CHAR_OTHER :
				out.write(((Character)value).charValue());
				break;
			default :
				throw new AssertionError();
		}
	}

	private static void writeSegment(byte type, Object value, MediaEncoder encoder, Writer out) throws IOException {
		switch(type) {
			case TYPE_STRING :
				encoder.write((String)value, out);
				break;
			case TYPE_CHAR_NEWLINE :
				encoder.write('\n', out);
				break;
			case TYPE_CHAR_QUOTE :
				encoder.write('"', out);
				break;
			case TYPE_CHAR_APOS :
				encoder.write('\'', out);
				break;
			case TYPE_CHAR_OTHER :
				encoder.write(((Character)value).charValue(), out);
				break;
			default :
				throw new AssertionError();
		}
	}

	/**
	 * Gets the length of a segment.
	 */
	private static int getLength(byte type, Object value) {
		switch(type) {
			case TYPE_STRING :
				return ((String)value).length();
			case TYPE_CHAR_NEWLINE :
				return 1;
			case TYPE_CHAR_QUOTE :
				return 1;
			case TYPE_CHAR_APOS :
				return 1;
			case TYPE_CHAR_OTHER :
				return 1;
			default :
				throw new AssertionError();
		}
	}

	/**
	 * Gets the character at the given index in a segment.
	 */
	private static char charAt(byte type, Object value, int index) {
		switch(type) {
			case TYPE_STRING :
				return ((String)value).charAt(index);
			case TYPE_CHAR_NEWLINE :
				return '\n';
			case TYPE_CHAR_QUOTE :
				return '"';
			case TYPE_CHAR_APOS :
				return '\'';
			case TYPE_CHAR_OTHER :
				return ((Character)value).charValue();
			default :
				throw new AssertionError();
		}
	}

	private final int tempFileThreshold;

	/**
	 * The length of the writer is the sum of the length of all its segments.
	 * Once closed, this length will not be modified.
	 */
    private long length;

	/**
	 * The set of segments are maintained in an array.
	 * Once closed, these arrays will not be modified.
	 */
	private byte[] segmentTypes;
	private Object[] segmentValues;
	private int segmentCount;

	/**
	 * Once closed, no further information may be written.
	 * Manipulations are only active once closed.
	 */
	private boolean isClosed;

	// The temp file is in UTF16-BE encoding
    private TempFile tempFile;
    private Writer fileWriter;

	/**
	 * When segments are trimmed (or other types of substring operations), they
	 * share the underlying segment data.  This is similar to the pre Java 1.7
	 * implementation of String.substring.
	 * 
	 * These values are only used after the buffer is closed, since the buffer
	 * must be closed before any manipulation is allowed.
	 */
	private final long start;
	private final int startSegmentIndex;
	private final int startSegmentStart;
	// The end values are set on close or by the substring constructor
	private long end;
	private int endSegmentIndex;
	private int endSegmentEnd;

	public SegmentedBufferedWriter(int tempFileThreshold) {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): new");
        this.tempFileThreshold = DEBUG_FILE ? 1 : tempFileThreshold;
        this.length = 0;
		this.segmentTypes = EMPTY_BYTES;
		this.segmentValues = EMPTY_OBJECTS;
		this.segmentCount = 0;
		this.isClosed = false;
		this.tempFile = null;
		this.fileWriter = null;
		// The original buffer has zero start offset
		this.start = 0;
		this.startSegmentIndex = 0;
		this.startSegmentStart = 0;
		// Values will be set on close
		this.end = -1;
		this.endSegmentIndex = 0;
		this.endSegmentEnd = 0;
    }

	/**
	 * Constructor that makes a substring of the buffer, sharing the underlying storage.
	 */
	private SegmentedBufferedWriter(
		SegmentedBufferedWriter original,
		long start,
		int startSegmentIndex,
		int startSegmentStart,
		long end,
		int endSegmentIndex,
		int endSegmentEnd
	) {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): new substring");
        this.tempFileThreshold = original.tempFileThreshold;
        this.length = original.length;
		this.segmentTypes = original.segmentTypes;
		this.segmentValues = original.segmentValues;
		this.segmentCount = original.segmentCount;
		assert original.isClosed;
		this.isClosed = true;
		this.tempFile = original.tempFile;
		assert original.fileWriter==null;
		this.fileWriter = null;
		// Substring of the original
		this.start = start;
		this.startSegmentIndex = startSegmentIndex;
		this.startSegmentStart = startSegmentStart;
		this.end = end;
		this.endSegmentIndex = endSegmentIndex;
		this.endSegmentEnd = endSegmentEnd;
    }

	private void switchIfNeeded(long newLength) throws IOException {
		final Object[] segs = this.segmentValues;
        if(segs!=null && newLength>=tempFileThreshold) {
            tempFile = new TempFile("SegmentedBufferedWriter"/*, null, new File("/dev/shm")*/);
			if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): Switching to temp file: " + tempFile);
            if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Switching to temp file: {0}", tempFile);
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile.getFile()), Charsets.UTF_16BE));
			// Write all segments to file
			final byte[] segTypes = this.segmentTypes;
			Writer out = fileWriter;
			for(int i=0, count=segmentCount; i<count; i++) {
				writeSegment(segTypes[i], segs[i], out);
			}
			this.segmentTypes = null;
            this.segmentValues = null;
			this.segmentCount = 0;
        }
    }

	/**
	 * Makes sure the segments have room for one more element.
	 */
	private void addSegment(byte type, Object value) {
		assert !isClosed;
		int len = segmentValues.length;
		if(segmentCount==len) {
			// Need to grow
			int newLen = len<<1; // Double capacity
			if(newLen<16) newLen = 16;
			byte[] newTypes = new byte[newLen];
			System.arraycopy(segmentTypes, 0, newTypes, 0, len);
			Object[] newValues = new Object[newLen];
			System.arraycopy(segmentValues, 0, newValues, 0, len);
			this.segmentTypes = newTypes;
			this.segmentValues = newValues;
		}
		segmentTypes[segmentCount] = type;
		segmentValues[segmentCount++] = value;
	}

	@Override
    public void write(int c) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): write(int): " + (char)c);
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + 1;
        switchIfNeeded(newLength);
        if(segmentValues!=null) {
			char ch = (char)c;
			switch(ch) {
				case '\n' :
					addSegment(TYPE_CHAR_NEWLINE, null);
					break;
				case '\'' :
					addSegment(TYPE_CHAR_APOS, null);
					break;
				case '"' :
					addSegment(TYPE_CHAR_QUOTE, null);
					break;
				default :
					addSegment(TYPE_CHAR_OTHER, Character.valueOf(ch));
			}
		} else {
	        fileWriter.write(c);
		}
        length = newLength;
    }

    @Override
    public void write(char cbuf[]) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): write(char[]): " + String.copyValueOf(cbuf));
		final int len = cbuf.length;
		if(len>0) {
			if(len==1) {
				write(cbuf[0]);
			} else {
				if(isClosed) throw new ClosedChannelException();
				long newLength = length + len;
				switchIfNeeded(newLength);
				if(segmentValues!=null) {
					addSegment(
						TYPE_STRING,
						String.copyValueOf(cbuf)
					);
				} else {
					fileWriter.write(cbuf);
				}
				length = newLength;
			}
		}
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): write(char[],int,int): " + String.copyValueOf(cbuf, off, len));
		if(len>0) {
			if(len==1) {
				write(cbuf[off]);
			} else {
				if(isClosed) throw new ClosedChannelException();
				long newLength = length + len;
				switchIfNeeded(newLength);
				if(segmentValues!=null) {
					addSegment(
						TYPE_STRING,
						String.copyValueOf(cbuf, off, len)
					);
				} else {
					fileWriter.write(cbuf, off, len);
				}
				length = newLength;
			}
		}
    }

    @Override
    public void write(String str) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): write(String): " + str);
		final int len = str.length();
		if(len>0) {
			if(isClosed) throw new ClosedChannelException();
			long newLength = length + len;
			switchIfNeeded(newLength);
			if(segmentValues!=null) {
				if(len==1) {
					// Prefer character shortcuts
					switch(str.charAt(0)) {
						case '\n' :
							addSegment(TYPE_CHAR_NEWLINE, null);
							break;
						case '\'' :
							addSegment(TYPE_CHAR_APOS, null);
							break;
						case '"' :
							addSegment(TYPE_CHAR_QUOTE, null);
							break;
						default :
							addSegment(TYPE_STRING, str);
					}
				} else {
					addSegment(TYPE_STRING, str);
				}
			} else {
				fileWriter.write(str);
			}
			length = newLength;
		}
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): write(String,int,int): " + str.substring(off, off+len));
		if(len>0) {
			if(isClosed) throw new ClosedChannelException();
			long newLength = length + len;
			switchIfNeeded(newLength);
			if(segmentValues!=null) {
				if(len==1) {
					// Prefer character shortcuts
					switch(str.charAt(off)) {
						case '\n' :
							addSegment(TYPE_CHAR_NEWLINE, null);
							break;
						case '\'' :
							addSegment(TYPE_CHAR_APOS, null);
							break;
						case '"' :
							addSegment(TYPE_CHAR_QUOTE, null);
							break;
						default :
							addSegment(TYPE_STRING, str);
					}
				} else {
					addSegment(TYPE_STRING, str.substring(off, off+len));
				}
			} else {
				fileWriter.write(str, off, len);
			}
			length = newLength;
		}
    }

    @Override
    public SegmentedBufferedWriter append(CharSequence csq) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): append(CharSequence): " + csq);
		if(csq==null) {
			write("null");
		} else {
			final int len = csq.length();
			if(len>0) {
				if(isClosed) throw new ClosedChannelException();
				long newLength = length + len;
				switchIfNeeded(newLength);
				if(segmentValues!=null) {
					if(len==1) {
						// Prefer character shortcuts
						switch(csq.charAt(0)) {
							case '\n' :
								addSegment(TYPE_CHAR_NEWLINE, null);
								break;
							case '\'' :
								addSegment(TYPE_CHAR_APOS, null);
								break;
							case '"' :
								addSegment(TYPE_CHAR_QUOTE, null);
								break;
							default :
								addSegment(TYPE_STRING, csq.toString());
						}
					} else {
						addSegment(TYPE_STRING, csq.toString());
					}
				} else {
					fileWriter.append(csq);
				}
				length = newLength;
			}
		}
		return this;
    }

    @Override
    public SegmentedBufferedWriter append(CharSequence csq, int start, int end) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): append(CharSequence,int,int): " + csq.subSequence(start, end));
		if(csq==null) {
			write("null");
		} else {
			final int len = end-start;
			if(len>0) {
				if(isClosed) throw new ClosedChannelException();
				long newLength = length + len;
				switchIfNeeded(newLength);
				if(segmentValues!=null) {
					if(len==1) {
						// Prefer character shortcuts
						switch(csq.charAt(start)) {
							case '\n' :
								addSegment(TYPE_CHAR_NEWLINE, null);
								break;
							case '\'' :
								addSegment(TYPE_CHAR_APOS, null);
								break;
							case '"' :
								addSegment(TYPE_CHAR_QUOTE, null);
								break;
							default :
								addSegment(TYPE_STRING, csq.subSequence(start, end).toString());
						}
					} else {
						addSegment(TYPE_STRING, csq.subSequence(start, end).toString());
					}
				} else {
					fileWriter.append(csq, start, end);
				}
				length = newLength;
			}
		}
        return this;
    }

    @Override
    public SegmentedBufferedWriter append(char c) throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): append(char): " + c);
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + 1;
        switchIfNeeded(newLength);
        if(segmentValues!=null) {
			switch(c) {
				case '\n' :
					addSegment(TYPE_CHAR_NEWLINE, null);
					break;
				case '\'' :
					addSegment(TYPE_CHAR_APOS, null);
					break;
				case '"' :
					addSegment(TYPE_CHAR_QUOTE, null);
					break;
				default :
					addSegment(TYPE_CHAR_OTHER, Character.valueOf(c));
			}
		} else {
			fileWriter.append(c);
		}
        length = newLength;
        return this;
    }

    @Override
    public void flush() throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): flush()");
        if(fileWriter!=null) fileWriter.flush();
    }

    @Override
    public void close() throws IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): close(): length=" + length + ", segmentCount=" + segmentCount);
        if(fileWriter!=null) {
            fileWriter.close();
            fileWriter = null;
		}
		if(!isClosed) {
			isClosed = true;
			assert end == -1;
			end = length;
			if(segmentValues!=null) {
				endSegmentIndex = segmentCount - 1;
				endSegmentEnd =
					endSegmentIndex==-1
					? 0
					: getLength(
						segmentTypes[endSegmentIndex],
						segmentValues[endSegmentIndex]
					)
				;
			}
		}
    }

	@Override
    public long getLength() {
        return end - start;
    }

	// When this is closed, the string representation is stored after first use.
	private String toStringCache;

    @Override
    public String toString() {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): toString()");
		if(!isClosed) {
			// When buffering, do not convert to strings
			return "SegmentedBufferedWriter(length=" + length + ")";
		} else {
			if(toStringCache==null) {
				final Object[] values = this.segmentValues;
				if(values!=null) {
					if(start!=0) throw new NotImplementedException("TODO: Handle start offset");
					if(end!=length) throw new NotImplementedException("TODO: Handle end offset");
					final int count = this.segmentCount;
					if(count==0) {
						toStringCache = "";
					} else {
						final byte[] types = this.segmentTypes;
						if(count==1) {
							toStringCache = toString(types[0], values[0]);
						} else {
							logger.fine("Creating String from segments - benefits of SegmentedBufferedWriter negated.");
							StringBuilder buffer = new StringBuilder((int)length);
							for(int i=0; i<count; i++) {
								append(types[i], values[i], buffer);
							}
							assert buffer.length()==length : "buffer.length()!=length: "+buffer.length()+"!="+length;
							toStringCache = buffer.toString();
						}
					}
				} else {
					try {
						logger.info("Creating String from temp file - benefits of SegmentedBufferedWriter negated.");
						long strLen = this.end - this.start;
						if(strLen>Integer.MAX_VALUE) throw new RuntimeException("Buffer too large to convert to String: strLen="+strLen);
						StringBuilder sb = new StringBuilder((int)strLen);
						RandomAccessFile raf = new RandomAccessFile(tempFile.getFile(), "r");
						try {
							byte[] bytes = BufferManager.getBytes();
							try {
								long index = this.start;
								long endIndex = this.end;
								raf.seek(index<<1);
								while(index<endIndex) {
									// Read a block
									long blockSizeLong = (endIndex - index)<<1;
									int blockSize = blockSizeLong > BufferManager.BUFFER_SIZE ? BufferManager.BUFFER_SIZE : (int)blockSizeLong;
									assert (blockSize&1) == 0 : "Must be an even number for UTF-16 conversion";
									raf.readFully(bytes, 0, blockSize);
									// Convert to characters in sb
									for(int bpos=0; bpos<blockSize; bpos+=2) {
										sb.append(PersistentCollections.bufferToChar(bytes, bpos));
									}
									// Update location
									index += blockSize>>1;
								}
							} finally {
								BufferManager.release(bytes, false);
							}
						} finally {
							raf.close();
						}
						/* Previous version that reads the entire file
						Reader in = new InputStreamReader(new FileInputStream(tempFile.getFile()), Charsets.UTF_16BE);
						try {
							IoUtils.copy(in, sb);
						} finally {
							in.close();
						}
						*/
						assert sb.length()==strLen : "sb.length()!=strLen: "+sb.length()+"!="+strLen;
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
			if(out instanceof SegmentedBufferedWriter) {
				System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): writeTo(MediaEncoder,SegmentedBufferedWriter)");
			} else if(out instanceof MediaWriter) {
				System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): writeTo(MediaEncoder,MediaWriter)");
			} else {
				System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): writeTo(MediaEncoder,Writer)");
			}
		}
		if(encoder==null) {
			writeTo(out);
		} else {
			if(!isClosed) throw new IllegalStateException();
			final Object[] values = this.segmentValues;
			if(values!=null) {
				if(start!=0) throw new NotImplementedException("TODO: Handle start offset");
				if(end!=length) throw new NotImplementedException("TODO: Handle end offset");
				// TODO: If copying to another SegmentedBufferedWriter, we have a chance here to share segment list (current the StringBuilder)
				final int count = this.segmentCount;
				final byte[] types = this.segmentTypes;
				for(int i=0; i<count; i++) {
					writeSegment(types[i], values[i], encoder, out);
				}
			} else {
				writeTo(new MediaWriter(encoder, out));
			}
		}
	}

	@Override
    public void writeTo(Writer out) throws IllegalStateException, IOException {
		if(DEBUG) {
			if(out instanceof SegmentedBufferedWriter) {
				System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): writeTo(SegmentedBufferedWriter)");
			} else if(out instanceof MediaWriter) {
				System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): writeTo(MediaWriter)");
			} else {
				System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): writeTo(Writer)");
			}
		}
		if(!isClosed) throw new IllegalStateException();
		final Object[] values = this.segmentValues;
        if(values!=null) {
			// TODO: If copying to another SegmentedBufferedWriter, we have a chance here to share segment list (current the StringBuilder)
			if(start!=0) throw new NotImplementedException("TODO: Handle start offset");
			if(end!=length) throw new NotImplementedException("TODO: Handle end offset");
			final int count = this.segmentCount;
			final byte[] types = this.segmentTypes;
			for(int i=0; i<count; i++) {
				writeSegment(types[i], values[i], out);
			}
        } else {
			// TODO: If copying to another SegmentedBufferedWriter, we have a chance here for disk-to-disk block level copying instead of going through all the conversions.
			RandomAccessFile raf = new RandomAccessFile(tempFile.getFile(), "r");
			try {
				byte[] bytes = BufferManager.getBytes();
				try {
					char[] chars = BufferManager.getChars();
					try {
						long index = this.start;
						long endIndex = this.end;
						raf.seek(index<<1);
						while(index<endIndex) {
							// Read a block
							long blockSizeLong = (endIndex - index)<<1;
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
							// Update location
							index += blockSize>>1;
						}
					} finally {
						BufferManager.release(chars, false);
					}
				} finally {
					BufferManager.release(bytes, false);
				}
			} finally {
				raf.close();
			}
			/* Old version copied entire file
			Reader in = new InputStreamReader(new FileInputStream(tempFile.getFile()), Charsets.UTF_16BE);
			try {
				long totalRead = IoUtils.copy(in, out);
				assert totalRead==length : "totalRead!=length: "+totalRead+"!="+length;
			} finally {
				in.close();
			}*/
        }
    }

	@Override
	public SegmentedBufferedWriter trim() throws IllegalStateException, IOException {
		if(DEBUG) System.err.println("DEBUG: SegmentedBufferedWriter(" + System.identityHashCode(this) + "): trim()");
		if(!isClosed) throw new IllegalStateException();
		final Object[] values = this.segmentValues;
		if(values!=null) {
			final byte[] types = this.segmentTypes;
			// Trim from the left
			long newStart = start;
			int newStartSegmentIndex = startSegmentIndex;
			int newStartSegmentStart = startSegmentStart;
			// Skip past the beginning whitespace characters
			TRIM_LEFT :
			while(newStart<end) {
				assert newStartSegmentIndex < segmentCount;
				// Work on one segment
				final byte type = types[newStartSegmentIndex];
				final Object value = values[newStartSegmentIndex];
				final int len = getLength(type, value);
				// do...while because segments are never empty
				do {
					char ch = charAt(type, value, newStartSegmentStart);
					if(ch>' ') break TRIM_LEFT;
					newStart++;
					newStartSegmentStart++;
				} while(newStart<end && newStartSegmentStart < len);
				// Move to next segment
				newStartSegmentIndex++;
				newStartSegmentStart = 0;
			}
			// Trim from the right
			long newEnd = end;
			int newEndSegmentIndex = endSegmentIndex;
			int newEndSegmentEnd = endSegmentEnd;
			TRIM_RIGHT :
			while(newEnd>newStart) {
				assert newEndSegmentIndex >= 0;
				// Work on one segment
				final byte type = types[newEndSegmentIndex];
				final Object value = values[newEndSegmentIndex];
				// do...while because segments are never empty
				do {
					char ch = charAt(type, value, newEndSegmentEnd-1);
					if(ch>' ') break TRIM_RIGHT;
					newEnd--;
					newEndSegmentEnd--;
				} while(newEnd>newStart && newEndSegmentEnd > 0);
				// Move to previous segment
				newEndSegmentIndex--;
				newEndSegmentEnd =
					newEndSegmentIndex==-1
						? 0
						: getLength(
							types[newEndSegmentIndex],
							values[newEndSegmentIndex]
						)
				;
			}

			// Keep this object if already trimmed
			if(
				start==newStart
				//&& startSegmentIndex==newStartSegmentIndex
				//&& startSegmentStart==newStartSegmentStart
				&& end==newEnd
				//&& endSegmentIndex==newEndSegmentIndex
				//&& endSegmentEnd==newEndSegmentEnd
			) {
				return this;
			} else {
				// Otherwise, return new substring
				return new SegmentedBufferedWriter(
					this,
					newStart,
					newStartSegmentIndex,
					newStartSegmentStart,
					newEnd,
					newEndSegmentIndex,
					newEndSegmentEnd
				);
			}
		} else {
			// Trim from temp file
			RandomAccessFile raf = new RandomAccessFile(tempFile.getFile(), "r");
			try {
				long newStart = this.start;
				// Skip past the beginning whitespace characters
				raf.seek(newStart<<1);
				while(newStart<end) {
					char ch = raf.readChar();
					if(ch>' ') break;
					newStart++;
				}
				// Skip past the ending whitespace characters
				long newEnd = end;
				while(end>newStart) {
					raf.seek((newEnd-1)<<1);
					char ch = raf.readChar();
					if(ch>' ') break;
					newEnd--;
				}
				// Keep this object if already trimmed
				if(
					start==newStart
					//&& startSegmentIndex==newStartSegmentIndex
					//&& startSegmentStart==newStartSegmentStart
					&& end==newEnd
					//&& endSegmentIndex==newEndSegmentIndex
					//&& endSegmentEnd==newEndSegmentEnd
				) {
					return this;
				} else {
					// Otherwise, return new substring
					return new SegmentedBufferedWriter(
						this,
						newStart,
						0,
						0,
						newEnd,
						0,
						0
					);
				}
			} finally {
				raf.close();
			}
		}
	}
}
