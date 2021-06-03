/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2018, 2019, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-hodgepodge.
 *
 * ao-hodgepodge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-hodgepodge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-hodgepodge.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.io;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

/**
 * @see FifoFile
 *
 * @author  AO Industries, Inc.
 */
public class FifoFileOutputStream extends OutputStream {

	private final FifoFile file;
	private static class StatsLock {}
	private final StatsLock statsLock=new StatsLock();
	private long fifoWriteCount=0;
	private long fifoWriteBytes=0;

	FifoFileOutputStream(FifoFile file) {
		this.file=file;
	}

	/**
	 * Gets the number of writes performed on this stream.
	 */
	public long getWriteCount() {
		synchronized(statsLock) {
			return fifoWriteCount;
		}
	}

	/**
	 * Gets the number of bytes written to this stream.
	 */
	public long getWriteBytes() {
		synchronized(statsLock) {
			return fifoWriteBytes;
		}
	}

	/**
	 * Adds to the stats of this stream.
	 */
	protected void addStats(long bytes) {
		synchronized(statsLock) {
			fifoWriteCount++;
			fifoWriteBytes+=bytes;
		}
	}

	@Override
	public void write(int b) throws IOException {
		// Write to the queue
		synchronized(file) {
			while(true) {
				long len=file.getLength();
				if(len<file.maxFifoLength) {
					long pos=file.getFirstIndex()+len;
					while(pos>=file.maxFifoLength) pos-=file.maxFifoLength;
					file.file.seek(pos+16);
					file.file.write(b);
					addStats(1);
					file.setLength(len+1);
					file.notify();
					return;
				}
				try {
					file.wait();
				} catch(InterruptedException err) {
					InterruptedIOException ioErr=new InterruptedIOException();
					ioErr.initCause(err);
					throw ioErr;
				}
			}
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		// Write to the queue
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		// Write blocks until all bytes have been written
		while(len>0) {
			// Write to the queue
			synchronized(file) {
				while(true) {
					long fileLen=file.getLength();
					long maxBlockSize=file.maxFifoLength-fileLen;
					if(maxBlockSize>0) {
						long pos=file.getFirstIndex()+fileLen;
						while(pos>=file.maxFifoLength) pos-=file.maxFifoLength;
						int blockSize=maxBlockSize>len?len:(int)maxBlockSize;
						// When at the end of the file, write the remaining bytes
						if((pos+blockSize)>file.maxFifoLength) blockSize=(int)(file.maxFifoLength-pos);
						file.file.seek(pos+16);
						file.file.write(b, off, blockSize);
						addStats(blockSize);
						file.setLength(fileLen+blockSize);
						off+=blockSize;
						len-=blockSize;
						file.notify();
						break;
					}
					try {
						file.wait();
					} catch(InterruptedException err) {
						InterruptedIOException ioErr=new InterruptedIOException();
						ioErr.initCause(err);
						throw ioErr;
					}
				}
			}
		}
	}

	/**
	 * Flushes all updates to this file to the underlying storage device.  This is performed by
	 * <code>RandomAccessFile.getChannel().force(true)</code>.
	 *
	 * @see  FifoFile#flush()
	 */
	@Override
	public void flush() throws IOException {
		file.flush();
	}

	/**
	 * Gets the number of bytes that may be written before the
	 * queue is full and blocks writes.
	 */
	public long available() throws IOException {
		synchronized(file) {
			return file.maxFifoLength-file.getLength();
		}
	}

	/**
	 * @see  FifoFile#close()
	 */
	@Override
	public void close() throws IOException {
		synchronized(file) {
			file.close();
		}
	}
}
