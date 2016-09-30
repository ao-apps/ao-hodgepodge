/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016  AO Industries, Inc.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;

/**
 * Reads data as is it appended to a log file.  If the log file
 * is closed and recreated, which is typical during log rotations,
 * the new file is opened and read from the beginning.  The file is assumed
 * to have been replaced when its length is smaller than before.
 * <p>
 * This class will block on read.  If end of file is reached, it will continue
 * to block until data becomes available.  End of file is never returned from
 * this class, it will wait indefinitely for data.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class LogFollower extends InputStream {

	public static final int DEFAULT_POLL_INTERVAL=60*1000;

	private final int pollInterval;
	private final File file;

	private volatile boolean closed;

	private static class FilePosLock {}
	private final FilePosLock filePosLock = new FilePosLock();
	private long filePos;

	public LogFollower(String path) {
		this(new File(path), DEFAULT_POLL_INTERVAL);
	}

	public LogFollower(File file) {
		this(file, DEFAULT_POLL_INTERVAL);
	}

	public LogFollower(String path, int pollInterval) {
		this(new File(path), pollInterval);
	}

	public LogFollower(File file, int pollInterval) {
		this.pollInterval = pollInterval;
		this.file = file;
	}

	private void checkClosed() throws IOException {
		if(closed) throw new IOException("LogFollower has been closed: "+file.getPath());
	}

	/**
	 * Checks the current position in the file.
	 * Detects if the file has changed in length.
	 * If closed, throws an exception.
	 * If file doesn't exist, waits until it does exist.
	 */
	private void detectFileChange() throws IOException {
		checkClosed();
		assert Thread.holdsLock(filePosLock);
		while(!file.exists()) {
			try {
				System.err.println("File not found, waiting: " + file.getPath());
				Thread.sleep(pollInterval);
			} catch(InterruptedException e) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException newExc = new InterruptedIOException(e.getMessage());
				newExc.initCause(e);
				throw newExc;
			}
			checkClosed();
		}
		long fileLen = file.length();
		if(fileLen<filePos) filePos = 0;
	}

	@Override
	public int available() throws IOException {
		checkClosed();
		synchronized(filePosLock) {
			detectFileChange();
			long available = file.length() - filePos;
			if(available < 0) available = 0;
			else if(available>Integer.MAX_VALUE) available = Integer.MAX_VALUE;
			return (int)available;
		}
	}

	@Override
	public void close() throws IOException {
		closed = true;
	}

	public int getPollInterval() {
		return pollInterval;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}

	@Override
	public int read() throws IOException {
		checkClosed();
		while(true) {
			synchronized(filePosLock) {
				detectFileChange();
				// Read to the end of the file
				long ral = file.length();
				if(ral>filePos) {
					RandomAccessFile randomAccess = new RandomAccessFile(file, "r");
					try {
						randomAccess.seek(filePos++);
						return randomAccess.read();
					} finally {
						randomAccess.close();
					}
				}
			}

			// Sleep and try again
			try {
				Thread.sleep(pollInterval);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr=new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
		}
	}

	@Override
	public int read(byte[] b, int offset, int len) throws IOException {
		checkClosed();
		while(true) {
			synchronized(filePosLock) {
				detectFileChange();
				// Read to the end of the file
				long ral = file.length();
				if(ral>filePos) {
					RandomAccessFile randomAccess = new RandomAccessFile(file, "r");
					try {
						randomAccess.seek(filePos);
						long avail = randomAccess.length() - filePos;
						if(avail>len) avail = len;
						int actual = randomAccess.read(b, offset, (int)avail);
						filePos += actual;
						return actual;
					} finally {
						randomAccess.close();
					}
				}
			}

			// Sleep and try again
			try {
				Thread.sleep(pollInterval);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr=new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
		}
	}

	@Override
	public long skip(long n) throws IOException {
		checkClosed();
		while(true) {
			synchronized(filePosLock) {
				detectFileChange();
				// Skip to the end of the file
				long ral = file.length();
				if(ral>filePos) {
					RandomAccessFile randomAccess = new RandomAccessFile(file, "r");
					try {
						randomAccess.seek(filePos);
						long avail = randomAccess.length()-filePos;
						if(avail>n) avail = n;
						int actual = randomAccess.skipBytes((int)avail);
						filePos += actual;
						return actual;
					} finally {
						randomAccess.close();
					}
				}
			}

			// Sleep and try again
			try {
				Thread.sleep(pollInterval);
			} catch(InterruptedException err) {
				// Restore the interrupted status
				Thread.currentThread().interrupt();
				InterruptedIOException ioErr=new InterruptedIOException();
				ioErr.initCause(err);
				throw ioErr;
			}
		}
	}
}
