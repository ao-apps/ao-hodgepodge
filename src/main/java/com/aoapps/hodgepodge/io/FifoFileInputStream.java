/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2016, 2018, 2021, 2022  AO Industries, Inc.
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
 * along with ao-hodgepodge.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.hodgepodge.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

/**
 * @see FifoFile
 *
 * @author  AO Industries, Inc.
 */
public class FifoFileInputStream extends InputStream {

  private final FifoFile file;

  private static class StatsLock {
    // Empty lock class to help heap profile
  }
  private final StatsLock statsLock = new StatsLock();
  private long fifoReadCount;
  private long fifoReadBytes;

  FifoFileInputStream(FifoFile file) {
    this.file = file;
  }

  /**
   * Gets the number of reads performed on this stream.
   */
  public long getReadCount() {
    synchronized (statsLock) {
      return fifoReadCount;
    }
  }

  /**
   * Gets the number of bytes read from this stream.
   */
  public long getReadBytes() {
    synchronized (statsLock) {
      return fifoReadBytes;
    }
  }

  /**
   * Adds to the stats of this stream.
   */
  protected void addStats(long bytes) {
    synchronized (statsLock) {
      fifoReadCount++;
      fifoReadBytes += bytes;
    }
  }

  /**
   * Reads data from the file, blocks until the data is available.
   */
  @Override
  public int read() throws IOException {
    // Read from the queue
    synchronized (file) {
      while (true) {
        if (Thread.currentThread().isInterrupted()) {
          throw new InterruptedIOException();
        }
        long len = file.getLength();
        if (len >= 1) {
          long pos = file.getFirstIndex();
          file.file.seek(pos + 16);
          int b = file.file.read();
          if (b == -1) {
            throw new EOFException("Unexpected EOF");
          }
          addStats(1);
          long newFirstIndex = pos + 1;
          while (newFirstIndex >= file.maxFifoLength) {
            newFirstIndex -= file.maxFifoLength;
          }
          file.setFirstIndex(newFirstIndex);
          file.setLength(len - 1);
          file.notifyAll();
          return b;
        }
        try {
          file.wait();
        } catch (InterruptedException err) {
          // Restore the interrupted status
          Thread.currentThread().interrupt();
          InterruptedIOException ioErr = new InterruptedIOException();
          ioErr.initCause(err);
          throw ioErr;
        }
      }
    }
  }

  /**
   * Reads data from the file, blocks until at least one byte is available.
   */
  @Override
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Reads data from the file, blocks until at least one byte is available.
   */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    // Read from the queue
    synchronized (file) {
      while (true) {
        if (Thread.currentThread().isInterrupted()) {
          throw new InterruptedIOException();
        }
        long fileLen = file.getLength();
        if (fileLen >= 1) {
          long pos = file.getFirstIndex();
          file.file.seek(pos + 16);
          int readSize = fileLen > len ? len : (int) fileLen;
          // When at the end of the file, read the remaining bytes
          if ((pos + readSize) > file.maxFifoLength) {
            readSize = (int) (file.maxFifoLength - pos);
          }

          // Read as many bytes as currently available
          int totalRead = file.file.read(b, off, readSize);
          if (totalRead == -1) {
            throw new EOFException("Unexpected EOF");
          }
          addStats(totalRead);
          long newFirstIndex = pos + totalRead;
          while (newFirstIndex >= file.maxFifoLength) {
            newFirstIndex -= file.maxFifoLength;
          }
          file.setFirstIndex(newFirstIndex);
          file.setLength(fileLen - totalRead);
          file.notifyAll();
          return totalRead;
        }
        try {
          file.wait();
        } catch (InterruptedException err) {
          // Restore the interrupted status
          Thread.currentThread().interrupt();
          InterruptedIOException ioErr = new InterruptedIOException();
          ioErr.initCause(err);
          throw ioErr;
        }
      }
    }
  }

  /**
   * Skips data in the queue, blocks until at least one byte is skipped.
   */
  @Override
  public long skip(long n) throws IOException {
    // Skip in the queue
    synchronized (file) {
      while (true) {
        if (Thread.currentThread().isInterrupted()) {
          throw new InterruptedIOException();
        }
        long fileLen = file.getLength();
        if (fileLen >= 1) {
          long pos = file.getFirstIndex();
          long skipSize = fileLen > n ? n : fileLen;
          // When at the end of the file, skip the remaining bytes
          if ((pos + skipSize) > file.maxFifoLength) {
            skipSize = file.maxFifoLength - pos;
          }

          // Skip as many bytes as currently available
          long totalSkipped = skipSize;
          long newFirstIndex = pos + skipSize;
          while (newFirstIndex >= file.maxFifoLength) {
            newFirstIndex -= file.maxFifoLength;
          }
          file.setFirstIndex(newFirstIndex);
          file.setLength(fileLen - skipSize);
          file.notifyAll();
          return totalSkipped;
        }
        try {
          file.wait();
        } catch (InterruptedException err) {
          // Restore the interrupted status
          Thread.currentThread().interrupt();
          InterruptedIOException ioErr = new InterruptedIOException();
          ioErr.initCause(err);
          throw ioErr;
        }
      }
    }
  }

  /**
   * Determines the number of bytes that may be read without blocking.
   */
  @Override
  public int available() throws IOException {
    synchronized (file) {
      long len = file.getLength();
      return len > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) len;
    }
  }

  /**
   * @see  FifoFile#close()
   */
  @Override
  public void close() throws IOException {
    file.close();
  }
}
