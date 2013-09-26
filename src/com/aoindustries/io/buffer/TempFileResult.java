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
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.io.TempFile;
import com.aoindustries.util.BufferManager;
import com.aoindustries.util.WrappedException;
import com.aoindustries.util.persistent.PersistentCollections;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * {@inheritDoc}
 *
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class TempFileResult implements BufferResult {

	private static final Logger logger = Logger.getLogger(TempFileResult.class.getName());

	private final TempFile tempFile;

	private final long start;
	private final long end;

	protected TempFileResult(
		TempFile tempFile,
		long start,
		long end
	) {
		this.tempFile = tempFile;
		this.start = start;
		this.end = end;
    }

	@Override
    public long getLength() {
        return end - start;
    }

	private String toStringCache;

    @Override
    public String toString() {
		if(toStringCache==null) {
			try {
				logger.info("Creating String from temp file - benefits of buffering negated.");
				final long length = end - start;
				if(length>Integer.MAX_VALUE) throw new RuntimeException("Buffer too large to convert to String: length="+length);
				StringBuilder sb = new StringBuilder((int)length);
				RandomAccessFile raf = new RandomAccessFile(tempFile.getFile(), "r");
				try {
					byte[] bytes = BufferManager.getBytes();
					try {
						long index = this.start;
						raf.seek(index<<1);
						while(index<end) {
							// Read a block
							long blockSizeLong = (end - index)<<1;
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
				assert sb.length()==length : "sb.length()!=length: "+sb.length()+"!="+length;
				toStringCache = sb.toString();
			} catch(IOException err) {
				throw new WrappedException(err);
			}
		}
		return toStringCache;
    }

	@Override
    public void writeTo(MediaEncoder encoder, Writer out) throws IOException {
		writeTo(
			encoder!=null
				? new MediaWriter(encoder, out)
				: out
		);
	}

	@Override
    public void writeTo(Writer out) throws IOException {
		// TODO: If copying to another SegmentedBufferedWriter or AutoTempFileWriter, we have a chance here for disk-to-disk block level copying instead of going through all the conversions.
		RandomAccessFile raf = new RandomAccessFile(tempFile.getFile(), "r");
		try {
			byte[] bytes = BufferManager.getBytes();
			try {
				char[] chars = BufferManager.getChars();
				try {
					long index = this.start;
					raf.seek(index<<1);
					while(index<end) {
						// Read a block
						long blockSizeLong = (end - index)<<1;
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
    }

	@Override
	public BufferResult trim() throws IOException {
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
			while(newEnd>newStart) {
				raf.seek((newEnd-1)<<1);
				char ch = raf.readChar();
				if(ch>' ') break;
				newEnd--;
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
					return new TempFileResult(
						tempFile,
						newStart,
						newEnd
					);
				}
			}
		} finally {
			raf.close();
		}
	}
}
