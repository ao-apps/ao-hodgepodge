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

import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.lang.NotImplementedException;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * {@inheritDoc}
 *
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class SegmentedResult implements BufferResult {

    private static final Logger logger = Logger.getLogger(SegmentedResult.class.getName());

	/**
	 * @see  SegmentedWriter#segmentTypes
	 */
	private final long length; // TODO: Should no longer be required once everything uses start and end
	private final byte[] segmentTypes;
	private final Object[] segmentValues;
	private final int segmentCount;

	/**
	 * When segments are trimmed (or other types of substring operations), they
	 * share the underlying segment data.  This is similar to the pre Java 1.7
	 * implementation of String.substring.
	 */
	private final long start;
	private final int startSegmentIndex;
	private final int startSegmentStart;
	private final long end;
	private final int endSegmentIndex;
	private final int endSegmentEnd;

	protected SegmentedResult(
		long length,
		byte[] segmentTypes,
		Object[] segmentValues,
		int segmentCount,
		long start,
		int startSegmentIndex,
		int startSegmentStart,
		long end,
		int endSegmentIndex,
		int endSegmentEnd
	) {
		this.length = length;
		this.segmentTypes = segmentTypes;
		this.segmentValues = segmentValues;
		assert segmentCount>0;
		this.segmentCount = segmentCount;
		this.start = start;
		this.startSegmentIndex = startSegmentIndex;
		this.startSegmentStart = startSegmentStart;
		this.end = end;
		this.endSegmentIndex = endSegmentIndex;
		this.endSegmentEnd = endSegmentEnd;
    }

	@Override
    public long getLength() {
        return end - start;
    }

	private String toString(int segmentIndex) {
		switch(segmentTypes[segmentIndex]) {
			case SegmentedWriter.TYPE_STRING :
				return (String)segmentValues[segmentIndex];
			case SegmentedWriter.TYPE_CHAR_NEWLINE :
				return "\n";
			case SegmentedWriter.TYPE_CHAR_QUOTE :
				return "\"";
			case SegmentedWriter.TYPE_CHAR_APOS :
				return "'";
			case SegmentedWriter.TYPE_CHAR_OTHER :
				return String.valueOf(((Character)segmentValues[segmentIndex]).charValue());
			default :
				throw new AssertionError();
		}
	}

	private void append(int segmentIndex, StringBuilder buffer) {
		switch(segmentTypes[segmentIndex]) {
			case SegmentedWriter.TYPE_STRING :
				buffer.append((String)segmentValues[segmentIndex]);
				break;
			case SegmentedWriter.TYPE_CHAR_NEWLINE :
				buffer.append('\n');
				break;
			case SegmentedWriter.TYPE_CHAR_QUOTE :
				buffer.append('"');
				break;
			case SegmentedWriter.TYPE_CHAR_APOS :
				buffer.append('\'');
				break;
			case SegmentedWriter.TYPE_CHAR_OTHER :
				buffer.append(((Character)segmentValues[segmentIndex]).charValue());
				break;
			default :
				throw new AssertionError();
		}
	}

	private void writeSegment(int segmentIndex, MediaEncoder encoder, Writer out) throws IOException {
		switch(segmentTypes[segmentIndex]) {
			case SegmentedWriter.TYPE_STRING :
				encoder.write((String)segmentValues[segmentIndex], out);
				break;
			case SegmentedWriter.TYPE_CHAR_NEWLINE :
				encoder.write('\n', out);
				break;
			case SegmentedWriter.TYPE_CHAR_QUOTE :
				encoder.write('"', out);
				break;
			case SegmentedWriter.TYPE_CHAR_APOS :
				encoder.write('\'', out);
				break;
			case SegmentedWriter.TYPE_CHAR_OTHER :
				encoder.write(((Character)segmentValues[segmentIndex]).charValue(), out);
				break;
			default :
				throw new AssertionError();
		}
	}

	/**
	 * Gets the character at the given index in a segment.
	 */
	private static char charAt(byte type, Object value, int charIndex) {
		switch(type) {
			case SegmentedWriter.TYPE_STRING :
				return ((String)value).charAt(charIndex);
			case SegmentedWriter.TYPE_CHAR_NEWLINE :
				assert charIndex==0;
				return '\n';
			case SegmentedWriter.TYPE_CHAR_QUOTE :
				assert charIndex==0;
				return '"';
			case SegmentedWriter.TYPE_CHAR_APOS :
				assert charIndex==0;
				return '\'';
			case SegmentedWriter.TYPE_CHAR_OTHER :
				assert charIndex==0;
				return ((Character)value).charValue();
			default :
				throw new AssertionError();
		}
	}

	private String toStringCache;

    @Override
    public String toString() {
		if(toStringCache==null) {
			// TODO: if(start!=0) throw new NotImplementedException("TODO: Handle start offset");
			// TODO: if(end!=length) throw new NotImplementedException("TODO: Handle end offset");
			if(segmentCount==1) {
				toStringCache = toString(0);
			} else {
				logger.fine("Creating String from segments - benefits of SegmentedWriter negated.");
				StringBuilder buffer = new StringBuilder((int)length);
				for(int i=0; i<segmentCount; i++) {
					append(i, buffer);
				}
				assert buffer.length()==length : "buffer.length()!=length: "+buffer.length()+"!="+length;
				toStringCache = buffer.toString();
			}
		}
		return toStringCache;
    }

	@Override
    public void writeTo(MediaEncoder encoder, Writer out) throws IOException {
		if(encoder==null) {
			writeTo(out);
		} else {
			// TODO: if(start!=0) throw new NotImplementedException("TODO: Handle start offset");
			// TODO: if(end!=length) throw new NotImplementedException("TODO: Handle end offset");
			// TODO: If copying to another SegmentedWriter, we have a chance here to share segment list (current the StringBuilder)
			for(int i=0; i<segmentCount; i++) {
				writeSegment(i, encoder, out);
			}
		}
	}

	@Override
    public void writeTo(Writer out) throws IOException {
		// TODO: If copying to another SegmentedWriter, we have a chance here to share segment list (current the StringBuilder)
		if(start!=0) throw new NotImplementedException("TODO: Handle start offset");
		if(end!=length) throw new NotImplementedException("TODO: Handle end offset");
		for(int i=0; i<segmentCount; i++) {
			SegmentedWriter.writeSegment(segmentTypes[i], segmentValues[i], out);
		}
    }

	@Override
	public BufferResult trim() throws IOException {
		// Trim from the left
		long newStart = start;
		int newStartSegmentIndex = startSegmentIndex;
		int newStartSegmentStart = startSegmentStart;
		// Skip past the beginning whitespace characters
		TRIM_LEFT :
		while(newStart<end) {
			assert newStartSegmentIndex < segmentCount;
			// Work on one segment
			final byte type = segmentTypes[newStartSegmentIndex];
			final Object value = segmentValues[newStartSegmentIndex];
			final int len = SegmentedWriter.getLength(type, value);
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
		if(newEnd>newStart) {
			assert newEndSegmentIndex >= 0;
			// Work on one segment
			byte type = segmentTypes[newEndSegmentIndex];
			Object value = segmentValues[newEndSegmentIndex];
			TRIM_RIGHT :
			do {
				// do...while because segments are never empty
				do {
					char ch = charAt(type, value, newEndSegmentEnd-1);
					if(ch>' ') break TRIM_RIGHT;
					newEnd--;
					newEndSegmentEnd--;
					if(newEnd==newStart) break TRIM_RIGHT;
				} while(newEndSegmentEnd > 0);
				// Move to previous segment
				newEndSegmentIndex--;
				assert newEndSegmentIndex >= 0 : "Must be non-negative because we have not made it back to newStart yet";
				type = segmentTypes[newEndSegmentIndex];
				value = segmentValues[newEndSegmentIndex];
				newEndSegmentEnd = SegmentedWriter.getLength(type, value);
			} while(true);
		}

		// Keep this object if already trimmed
		if(
			start==newStart
			&& end==newEnd
		) {
			return this;
		} else {
			// Check if empty
			if(newStart==newEnd) {
				return EmptyResult.getInstance();
			} else {
				// Otherwise, return new substring
				return new SegmentedResult(
					length,
					segmentTypes,
					segmentValues,
					segmentCount,
					newStart,
					newStartSegmentIndex,
					newStartSegmentStart,
					newEnd,
					newEndSegmentIndex,
					newEndSegmentEnd
				);
			}
		}
	}
}
