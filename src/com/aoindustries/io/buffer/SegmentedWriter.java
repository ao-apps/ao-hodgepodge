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
 * Buffers all writes while switching to a temp file when the
 * threshold is reached.
 *
 * TODO: If writing to another segmented buffer, the segments will be shared between
 * the two instances.
 *
 * This class is not thread safe.
 * 
 * @see  AutoTempFileWriter  This draws heavily from experienced gained in <code>AutoTempFileWriter</code>, but with the addition of segments.
 *
 * @author  AO Industries, Inc.
 */
public class SegmentedWriter extends BufferWriter {

    private static final Logger logger = Logger.getLogger(SegmentedWriter.class.getName());

	/**
	 * The number of starting elements in segment arrays.
	 */
	private static final int START_LEN = 16;

	private static final byte[] EMPTY_BYTES = new byte[0];
	private static final int[] EMPTY_INTS = new int[0];
	private static final Object[] EMPTY_OBJECTS = new Object[0];

	/**
	 * The set of internal types supported.
	 */
	static final byte
		TYPE_STRING = 1,
		TYPE_CHAR_NEWLINE = 2,
		TYPE_CHAR_QUOTE = 3,
		TYPE_CHAR_APOS = 4,
		TYPE_CHAR_OTHER = 5
	;

	/**
	 * Appends the segment with the given offset and length to the given writer.
	 */
	static void writeSegment(byte type, Object value, int off, int len, Writer out) throws IOException {
		switch(type) {
			case TYPE_STRING :
				out.write((String)value, off, len);
				break;
			case TYPE_CHAR_NEWLINE :
				assert off==0;
				assert len==1;
				out.write('\n');
				break;
			case TYPE_CHAR_QUOTE :
				assert off==0;
				assert len==1;
				out.write('"');
				break;
			case TYPE_CHAR_APOS :
				assert off==0;
				assert len==1;
				out.write('\'');
				break;
			case TYPE_CHAR_OTHER :
				assert off==0;
				assert len==1;
				out.write(((Character)value).charValue());
				break;
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
	private int[] segmentOffsets;
	private int[] segmentLengths;
	private int segmentCount;

	/**
	 * Once closed, no further information may be written.
	 * Manipulations are only active once closed.
	 */
	private boolean isClosed;

	// The temp file is in UTF16-BE encoding
    private TempFile tempFile;
    private Writer fileWriter;

	public SegmentedWriter(int tempFileThreshold) {
        this.tempFileThreshold = tempFileThreshold;
        this.length = 0;
		this.segmentTypes = EMPTY_BYTES;
		this.segmentValues = EMPTY_OBJECTS;
		this.segmentOffsets = EMPTY_INTS;
		this.segmentLengths = EMPTY_INTS;
		this.segmentCount = 0;
		this.isClosed = false;
		this.tempFile = null;
		this.fileWriter = null;
    }

	private void switchIfNeeded(long newLength) throws IOException {
		final Object[] sValues = this.segmentValues;
        if(sValues!=null && newLength>=tempFileThreshold) {
            tempFile = new TempFile("SegmentedWriter"/*, null, new File("/dev/shm")*/);
            if(logger.isLoggable(Level.FINE)) logger.log(Level.FINE, "Switching to temp file: {0}", tempFile);
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile.getFile()), Charsets.UTF_16BE));
			// Write all segments to file
			final byte[] sTypes = this.segmentTypes;
			final int[] sOffsets = this.segmentOffsets;
			final int[] sLengths = this.segmentLengths;
			Writer out = fileWriter;
			for(int i=0, count=segmentCount; i<count; i++) {
				writeSegment(sTypes[i], sValues[i], sOffsets[i], sLengths[i], out);
			}
			this.segmentTypes = null;
            this.segmentValues = null;
			this.segmentOffsets = null;
			this.segmentLengths = null;
			this.segmentCount = 0;
        }
    }

	/**
	 * Makes sure the segments have room for one more element.
	 */
	private void addSegment(byte type, Object value, int off, int len) {
		assert !isClosed;
		assert len>0 : "Empty segments should never be added";
		final int arraylen = segmentValues.length;
		if(segmentCount==arraylen) {
			// Need to grow
			if(arraylen==0) {
				this.segmentTypes = new byte[START_LEN];
				this.segmentValues = new Object[START_LEN];
				this.segmentOffsets = new int[START_LEN];
				this.segmentLengths = new int[START_LEN];
			} else {
				// Double capacity and copy
				int newLen = arraylen<<1;
				byte[] newTypes = new byte[newLen];
				System.arraycopy(segmentTypes, 0, newTypes, 0, arraylen);
				this.segmentTypes = newTypes;
				Object[] newValues = new Object[newLen];
				System.arraycopy(segmentValues, 0, newValues, 0, arraylen);
				this.segmentValues = newValues;
				int[] newOffsets = new int[newLen];
				System.arraycopy(segmentOffsets, 0, newOffsets, 0, arraylen);
				this.segmentOffsets = newOffsets;
				int[] newLengths = new int[newLen];
				System.arraycopy(segmentLengths, 0, newLengths, 0, arraylen);
				this.segmentLengths = newLengths;
			}
		}
		segmentTypes[segmentCount] = type;
		segmentValues[segmentCount] = value;
		segmentOffsets[segmentCount] = off;
		segmentLengths[segmentCount++] = len;
	}

	@Override
    public void write(int c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + 1;
        switchIfNeeded(newLength);
        if(segmentValues!=null) {
			char ch = (char)c;
			switch(ch) {
				case '\n' :
					addSegment(TYPE_CHAR_NEWLINE, null, 0, 1);
					break;
				case '\'' :
					addSegment(TYPE_CHAR_APOS, null, 0, 1);
					break;
				case '"' :
					addSegment(TYPE_CHAR_QUOTE, null, 0, 1);
					break;
				default :
					addSegment(TYPE_CHAR_OTHER, Character.valueOf(ch), 0, 1);
			}
		} else {
	        fileWriter.write(c);
		}
        length = newLength;
    }

    @Override
    public void write(char cbuf[]) throws IOException {
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
						new String(cbuf),
						0,
						len
					);
				} else {
					fileWriter.write(cbuf, 0, len);
				}
				length = newLength;
			}
		}
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
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
						new String(cbuf, off, len),
						0,
						len
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
							addSegment(TYPE_CHAR_NEWLINE, null, 0, 1);
							break;
						case '\'' :
							addSegment(TYPE_CHAR_APOS, null, 0, 1);
							break;
						case '"' :
							addSegment(TYPE_CHAR_QUOTE, null, 0, 1);
							break;
						default :
							addSegment(TYPE_STRING, str, 0, 1);
					}
				} else {
					addSegment(TYPE_STRING, str, 0, len);
				}
			} else {
				fileWriter.write(str, 0, len);
			}
			length = newLength;
		}
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
		if(len>0) {
			if(isClosed) throw new ClosedChannelException();
			long newLength = length + len;
			switchIfNeeded(newLength);
			if(segmentValues!=null) {
				if(len==1) {
					// Prefer character shortcuts
					switch(str.charAt(off)) {
						case '\n' :
							addSegment(TYPE_CHAR_NEWLINE, null, 0, 1);
							break;
						case '\'' :
							addSegment(TYPE_CHAR_APOS, null, 0, 1);
							break;
						case '"' :
							addSegment(TYPE_CHAR_QUOTE, null, 0, 1);
							break;
						default :
							addSegment(TYPE_STRING, str, off, 1);
					}
				} else {
					addSegment(TYPE_STRING, str, off, len);
				}
			} else {
				fileWriter.write(str, off, len);
			}
			length = newLength;
		}
    }

    @Override
    public SegmentedWriter append(CharSequence csq) throws IOException {
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
								addSegment(TYPE_CHAR_NEWLINE, null, 0, 1);
								break;
							case '\'' :
								addSegment(TYPE_CHAR_APOS, null, 0, 1);
								break;
							case '"' :
								addSegment(TYPE_CHAR_QUOTE, null, 0, 1);
								break;
							default :
								addSegment(TYPE_STRING, csq.toString(), 0, 1);
						}
					} else {
						addSegment(TYPE_STRING, csq.toString(), 0, len);
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
    public SegmentedWriter append(CharSequence csq, int start, int end) throws IOException {
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
						char ch = csq.charAt(start);
						switch(ch) {
							case '\n' :
								addSegment(TYPE_CHAR_NEWLINE, null, 0, 1);
								break;
							case '\'' :
								addSegment(TYPE_CHAR_APOS, null, 0, 1);
								break;
							case '"' :
								addSegment(TYPE_CHAR_QUOTE, null, 0, 1);
								break;
							default :
								if(
									ch <= 127 // Always cached
									|| !(csq instanceof String) // Use Character for all non-Strings
								) {
									addSegment(
										TYPE_CHAR_OTHER,
										Character.valueOf(ch),
										0,
										1
									);
								} else {
									// Use offset for String
									addSegment(
										TYPE_STRING,
										(String)csq,
										start,
										1
									);
								}
						}
					} else {
						if(csq instanceof String) {
							// Use offset for String
							addSegment(
								TYPE_STRING,
								(String)csq,
								start,
								len
							);
						} else {
							// Use subSequence().toString() for all non-Strings
							addSegment(
								TYPE_STRING,
								csq.subSequence(start, end).toString(),
								0,
								len
							);
						}
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
    public SegmentedWriter append(char c) throws IOException {
		if(isClosed) throw new ClosedChannelException();
        long newLength = length + 1;
        switchIfNeeded(newLength);
        if(segmentValues!=null) {
			switch(c) {
				case '\n' :
					addSegment(TYPE_CHAR_NEWLINE, null, 0, 1);
					break;
				case '\'' :
					addSegment(TYPE_CHAR_APOS, null, 0, 1);
					break;
				case '"' :
					addSegment(TYPE_CHAR_QUOTE, null, 0, 1);
					break;
				default :
					addSegment(TYPE_CHAR_OTHER, Character.valueOf(c), 0, 1);
			}
		} else {
			fileWriter.append(c);
		}
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

	@Override
    public long getLength() {
        return length;
    }

    @Override
    public String toString() {
		return "SegmentedWriter(length=" + length + ")";
    }

	// The result is cached after first created
	private BufferResult result;

	@Override
	public BufferResult getResult() throws IllegalStateException {
		if(!isClosed) throw new IllegalStateException();
		if(result==null) {
			if(length==0) {
				result = EmptyResult.getInstance();
			} else if(segmentValues!=null) {
				assert segmentCount>0 : "When not empty and using segments, must have at least one segment";
				int endSegmentIndex = segmentCount - 1;
				result = new SegmentedResult(
					length,
					segmentTypes,
					segmentValues,
					segmentOffsets,
					segmentLengths,
					segmentCount,
					0, // start
					0, // startSegmentIndex
					segmentOffsets[0],
					segmentLengths[0],
					length, // end
					endSegmentIndex,
					segmentOffsets[endSegmentIndex],
					segmentLengths[endSegmentIndex]
				);
			} else if(tempFile!=null) {
				result = new TempFileResult(tempFile, 0, length);
			} else {
				throw new AssertionError();
			}
		}
		return result;
	}
}
