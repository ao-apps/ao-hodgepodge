/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2016, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.AutoCloseables;
import com.aoapps.lang.Throwables;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Random;
import javax.crypto.Cipher;

/**
 * Pads the last block with the necessary number of bytes before closing the stream.
 * If padding is necessary without closing, use <code>finish</code>.
 */
// TODO: Use PKCS5 padding instead of random? https://www.di-mgt.com.au/cryptopad.html
public class PaddingOutputStream extends FilterOutputStream {

  /**
   * Note: This is not a {@linkplain SecureRandom#getInstanceStrong() strong instance} to avoid blocking.
   */
  private static final SecureRandom secureRandom = new SecureRandom();

  private final int blockSize;
  private final Random random;
  private final byte padding;

  private long byteCount;

  /**
   * Uses the given source of random bytes for padding.
   *
   * @param  blockSize  When {@code <= 0}, no padding will be performed.  This is consistent with zero returned from
   *                    {@link Cipher#getBlockSize()} when not a block cipher.
   *
   * @deprecated  Please use {@link SecureRandom}.  This method will stay, but will remain deprecated since it should
   *              only be used after careful consideration.
   */
  @Deprecated // Java 9: (forRemoval = false)
  public PaddingOutputStream(OutputStream out, int blockSize, Random random) {
    super(out);
    this.blockSize = blockSize;
    this.random = random;
    this.padding = 0; // Unused
  }

  /**
   * Uses the given source of random bytes for padding.
   *
   * @param  blockSize  When {@code <= 0}, no padding will be performed.  This is consistent with zero returned from
   *                    {@link Cipher#getBlockSize()} when not a block cipher.
   */
  public PaddingOutputStream(OutputStream out, int blockSize, SecureRandom secureRandom) {
    this(out, blockSize, (Random) secureRandom);
  }

  /**
   * Uses a default instance of {@link SecureRandom}, which is not a {@linkplain SecureRandom#getInstanceStrong() strong instance}
   * to avoid blocking, as the source of random bytes for padding.
   *
   * @param  blockSize  When {@code <= 0}, no padding will be performed.  This is consistent with zero returned from
   *                    {@link Cipher#getBlockSize()} when not a block cipher.
   */
  public PaddingOutputStream(OutputStream out, int blockSize) {
    this(out, blockSize, (Random) secureRandom);
  }

  /**
   * @deprecated  Please use random padding
   */
  @Deprecated
  public PaddingOutputStream(OutputStream out, int blockSize, byte padding) {
    super(out);
    this.blockSize = blockSize;
    this.random = null; // Unused
    this.padding = padding;
  }

  @Override
  public void write(int b) throws IOException {
    out.write(b);
    byteCount++;
  }

  @Override
  public void write(byte[] b) throws IOException {
    out.write(b);
    byteCount += b.length;
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
    byteCount += len;
  }

  /**
   * Pads and flushes without closing the underlying stream.
   */
  public void finish() throws IOException {
    if (blockSize > 0) {
      int lastBlockSize = (int) (byteCount % blockSize);
      if (lastBlockSize != 0 && lastBlockSize < blockSize) {
        if (random != null) {
          int garbageLen = blockSize - lastBlockSize;
          byte[] garbage = new byte[garbageLen];
          random.nextBytes(garbage);
          out.write(garbage, 0, garbageLen);
          byteCount += garbageLen;
          lastBlockSize += garbageLen;
        } else {
          do {
            out.write(padding);
            byteCount++;
            lastBlockSize++;
          } while (lastBlockSize < blockSize);
        }
        assert lastBlockSize == blockSize;
      }
    }
    out.flush();
  }

  /**
   * Pads, flushes, and closes the underlying stream.
   */
  @Override
  @SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
  public void close() throws IOException {
    Throwable t0 = null;
    try {
      finish();
    } catch (Throwable t) {
      t0 = Throwables.addSuppressed(t0, t);
    }
    AutoCloseables.closeAndThrow(t0, IOException.class, IOException::new, out);
  }
}
