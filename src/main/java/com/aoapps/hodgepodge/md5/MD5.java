/*
 * $Header: /var/cvs/aocode-public/src/com/aoindustries/md5/MD5.java,v 1.2 2008/01/06 16:47:45 orion Exp $
 *
 * MD5 in Java JDK Beta-2
 * written Santeri Paavolainen, Helsinki Finland 1996
 * (c) Santeri Paavolainen, Helsinki Finland 1996
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * See http://www.cs.hut.fi/~santtu/java/ for more information on this
 * class.
 *
 * This is rather straight re-implementation of the reference implementation
 * given in RFC1321 by RSA.
 *
 * Passes MD5 test suite as defined in RFC1321.
 *
 *
 * This Java class has been derived from the RSA Data Security, Inc. MD5
 * Message-Digest Algorithm and its reference implementation.
 *
 *
 * $Log: MD5.java,v $
 * Revision 1.5  1996/12/12 10:47:02  santtu
 * Changed GPL to LGPL
 *
 * Revision 1.4  1996/12/12 10:30:02  santtu
 * Some typos, State -> MD5State etc.
 *
 * Revision 1.3  1996/04/15 07:28:09  santtu
 * Added GPL statemets, and RSA derivate stametemetsnnts.
 *
 * Revision 1.2  1996/03/04 08:05:48  santtu
 * Added offsets to Update method
 *
 * Revision 1.1  1996/01/07 20:51:59  santtu
 * Initial revision
 *
 */

package com.aoapps.hodgepodge.md5;

import com.aoapps.lang.Strings;

/**
 * Implementation of RSA's MD5 hash generator.
 *
 * @version $Revision: 1.2 $
 * @author  Santeri Paavolainen &lt;sjpaavol@cc.helsinki.fi&gt;
 */
public class MD5 {

  /**
   * Contains internal state of the MD5 class.
   */
  static class MD5State {

    /**
     * 128-byte state.
     */
    final int[] state;

    /**
     * 64-bit character count (could be true Java long?).
     */
    final int[] count;

    /**
     * 64-byte buffer (512 bits) for storing to-be-hashed characters.
     */
    final byte[] buffer;

    MD5State() {
      buffer = new byte[64];
      count = new int[2];
      state = new int[4];

      state[0] = 0x67452301;
      state[1] = 0xefcdab89;
      state[2] = 0x98badcfe;
      state[3] = 0x10325476;

      count[0] = count[1] = 0;
    }

    /**
     * Create this State as a copy of another state.
     */
    MD5State(MD5State from) {
      this();

      int i;

      for (i = 0; i < buffer.length; i++) {
        this.buffer[i] = from.buffer[i];
      }

      for (i = 0; i < state.length; i++) {
        this.state[i] = from.state[i];
      }

      for (i = 0; i < count.length; i++) {
        this.count[i] = from.count[i];
      }
    }
  }

  /**
   * MD5 state.
   */
  private MD5State state;

  /**
   * If digest() has been called, finals is set to the current finals
   * state. Any update() causes this to be set to null.
   */
  private MD5State finals;

  /**
   * Padding for digest().
   */
  private static final byte[] padding = {
      (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };

  /**
   * Initialize MD5 internal state (object can be reused just by
   * calling init() after every digest().
   */
  public final synchronized void init() {
    state = new MD5State();
    finals = null;
  }

  /**
   * Initialize MD5 internal state (object can be reused just by
   * calling init() after every digest().
   *
   * @deprecated  Please use {@link #init()} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final void Init() {
    init();
  }

  /**
   * Class constructor.
   */
  public MD5() {
    this.init();
  }

  /**
   * Initialize class, and update hash with ob.toString()
   *
   * @param ob Object, ob.toString() is used to update hash
   *           after initialization
   */
  public MD5(Object ob) {
    this();
    update(ob.toString());
  }

  private static int rotateLeft(int x, int n) {
    return (x << n) | (x >>> (32 - n));
  }

  private static int ff(int a, int b, int c, int d, int x, int s, int ac) {
    a = a + ((b & c) | (~b & d)) + x + ac;
    return rotateLeft(a, s) + b;
  }

  private static int gg(int a, int b, int c, int d, int x, int s, int ac) {
    a = a + ((b & d) | (c & ~d)) + x + ac;
    return rotateLeft(a, s) + b;
  }

  private static int hh(int a, int b, int c, int d, int x, int s, int ac) {
    a = a + (b ^ c ^ d) + x + ac;
    return rotateLeft(a, s) + b;
  }

  private static int ii(int a, int b, int c, int d, int x, int s, int ac) {
    a = a + (c ^ (b | ~d)) + x + ac;
    return rotateLeft(a, s) + b;
  }

  private static int[] decode(byte[] buffer, int len, int shift) {
    int[] out;
    int i;
    int j;

    out = new int[16];

    for (i = j = 0; j < len; i++, j += 4) {
      out[i] = (buffer[j + shift] & 0xff)
          | ((buffer[j + 1 + shift] & 0xff) << 8)
          | ((buffer[j + 2 + shift] & 0xff) << 16)
          | ((buffer[j + 3 + shift] & 0xff) << 24);
    }

    return out;
  }

  private static void transform(MD5State state, byte[] buffer, int shift) {
    int a = state.state[0];
    int b = state.state[1];
    int c = state.state[2];
    int d = state.state[3];
    int[] x;

    x = decode(buffer, 64, shift);

    // Round 1
    a = ff(a, b, c, d, x[0],   7, 0xd76aa478); // 1
    d = ff(d, a, b, c, x[1],  12, 0xe8c7b756); // 2
    c = ff(c, d, a, b, x[2],  17, 0x242070db); // 3
    b = ff(b, c, d, a, x[3],  22, 0xc1bdceee); // 4
    a = ff(a, b, c, d, x[4],   7, 0xf57c0faf); // 5
    d = ff(d, a, b, c, x[5],  12, 0x4787c62a); // 6
    c = ff(c, d, a, b, x[6],  17, 0xa8304613); // 7
    b = ff(b, c, d, a, x[7],  22, 0xfd469501); // 8
    a = ff(a, b, c, d, x[8],   7, 0x698098d8); // 9
    d = ff(d, a, b, c, x[9],  12, 0x8b44f7af); // 10
    c = ff(c, d, a, b, x[10],  17, 0xffff5bb1); // 11
    b = ff(b, c, d, a, x[11],  22, 0x895cd7be); // 12
    a = ff(a, b, c, d, x[12],   7, 0x6b901122); // 13
    d = ff(d, a, b, c, x[13],  12, 0xfd987193); // 14
    c = ff(c, d, a, b, x[14],  17, 0xa679438e); // 15
    b = ff(b, c, d, a, x[15],  22, 0x49b40821); // 16

    // Round 2
    a = gg(a, b, c, d, x[1],   5, 0xf61e2562); // 17
    d = gg(d, a, b, c, x[6],   9, 0xc040b340); // 18
    c = gg(c, d, a, b, x[11],  14, 0x265e5a51); // 19
    b = gg(b, c, d, a, x[0],  20, 0xe9b6c7aa); // 20
    a = gg(a, b, c, d, x[5],   5, 0xd62f105d); // 21
    d = gg(d, a, b, c, x[10],   9,  0x2441453); // 22
    c = gg(c, d, a, b, x[15],  14, 0xd8a1e681); // 23
    b = gg(b, c, d, a, x[4],  20, 0xe7d3fbc8); // 24
    a = gg(a, b, c, d, x[9],   5, 0x21e1cde6); // 25
    d = gg(d, a, b, c, x[14],   9, 0xc33707d6); // 26
    c = gg(c, d, a, b, x[3],  14, 0xf4d50d87); // 27
    b = gg(b, c, d, a, x[8],  20, 0x455a14ed); // 28
    a = gg(a, b, c, d, x[13],   5, 0xa9e3e905); // 29
    d = gg(d, a, b, c, x[2],   9, 0xfcefa3f8); // 30
    c = gg(c, d, a, b, x[7],  14, 0x676f02d9); // 31
    b = gg(b, c, d, a, x[12],  20, 0x8d2a4c8a); // 32

    // Round 3
    a = hh(a, b, c, d, x[5],   4, 0xfffa3942); // 33
    d = hh(d, a, b, c, x[8],  11, 0x8771f681); // 34
    c = hh(c, d, a, b, x[11],  16, 0x6d9d6122); // 35
    b = hh(b, c, d, a, x[14],  23, 0xfde5380c); // 36
    a = hh(a, b, c, d, x[1],   4, 0xa4beea44); // 37
    d = hh(d, a, b, c, x[4],  11, 0x4bdecfa9); // 38
    c = hh(c, d, a, b, x[7],  16, 0xf6bb4b60); // 39
    b = hh(b, c, d, a, x[10],  23, 0xbebfbc70); // 40
    a = hh(a, b, c, d, x[13],   4, 0x289b7ec6); // 41
    d = hh(d, a, b, c, x[0],  11, 0xeaa127fa); // 42
    c = hh(c, d, a, b, x[3],  16, 0xd4ef3085); // 43
    b = hh(b, c, d, a, x[6],  23,  0x4881d05); // 44
    a = hh(a, b, c, d, x[9],   4, 0xd9d4d039); // 45
    d = hh(d, a, b, c, x[12],  11, 0xe6db99e5); // 46
    c = hh(c, d, a, b, x[15],  16, 0x1fa27cf8); // 47
    b = hh(b, c, d, a, x[2],  23, 0xc4ac5665); // 48

    // Round 4
    a = ii(a, b, c, d, x[0],   6, 0xf4292244); // 49
    d = ii(d, a, b, c, x[7],  10, 0x432aff97); // 50
    c = ii(c, d, a, b, x[14],  15, 0xab9423a7); // 51
    b = ii(b, c, d, a, x[5],  21, 0xfc93a039); // 52
    a = ii(a, b, c, d, x[12],   6, 0x655b59c3); // 53
    d = ii(d, a, b, c, x[3],  10, 0x8f0ccc92); // 54
    c = ii(c, d, a, b, x[10],  15, 0xffeff47d); // 55
    b = ii(b, c, d, a, x[1],  21, 0x85845dd1); // 56
    a = ii(a, b, c, d, x[8],   6, 0x6fa87e4f); // 57
    d = ii(d, a, b, c, x[15],  10, 0xfe2ce6e0); // 58
    c = ii(c, d, a, b, x[6],  15, 0xa3014314); // 59
    b = ii(b, c, d, a, x[13],  21, 0x4e0811a1); // 60
    a = ii(a, b, c, d, x[4],   6, 0xf7537e82); // 61
    d = ii(d, a, b, c, x[11],  10, 0xbd3af235); // 62
    c = ii(c, d, a, b, x[2],  15, 0x2ad7d2bb); // 63
    b = ii(b, c, d, a, x[9],  21, 0xeb86d391); // 64

    state.state[0] += a;
    state.state[1] += b;
    state.state[2] += c;
    state.state[3] += d;
  }

  /**
   * Updates hash with the bytebuffer given (using at maximum length bytes from
   * that buffer).
   *
   * @param stat   Which state is updated
   * @param buffer Array of bytes to be hashed
   * @param offset Offset to buffer array
   * @param length Use at maximum `length' bytes (absolute
   *               maximum is buffer.length)
   */
  private void update(MD5State stat, byte[] buffer, int offset, int length) {
    finals = null;

    // Length can be told to be shorter, but not inter
    if ((length - offset) > buffer.length) {
      length = buffer.length - offset;
    }

    // compute number of bytes mod 64
    int index = (stat.count[0] >>> 3) & 0x3f;

    if ((stat.count[0] += length << 3) < (length << 3)) {
      stat.count[1]++;
    }

    stat.count[1] += length >>> 29;

    int partlen = 64 - index;

    int i;
    if (length >= partlen) {
      for (i = 0; i < partlen; i++) {
        stat.buffer[i + index] = buffer[i + offset];
      }

      transform(stat, stat.buffer, 0);

      for (i = partlen; (i + 63) < length; i += 64) {
        transform(stat, buffer, i);
      }

      index = 0;
    } else {
      i = 0;
    }

    // buffer remaining input
    if (i < length) {
      int start = i;
      for (; i < length; i++) {
        stat.buffer[index + i - start] = buffer[i + offset];
      }
    }
  }

  /*
   * update()s for other datatypes than byte[] also. update(byte[], int)
   * is only the main driver.
   */

  /**
   * Plain update, updates this object.
   */
  public void update(byte[] buffer, int offset, int length) {
    update(this.state, buffer, offset, length);
  }

  /**
   * @deprecated  Please use {@link #update(byte[], int, int)} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final void Update(byte[] buffer, int offset, int length) {
    update(buffer, offset, length);
  }

  public void update(byte[] buffer, int length) {
    update(this.state, buffer, 0, length);
  }

  /**
   * @deprecated  Please use {@link #update(byte[], int)} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final void Update(byte[] buffer, int length) {
    update(buffer, length);
  }

  /**
   * Updates hash with given array of bytes.
   *
   * @param buffer Array of bytes to use for updating the hash
   */
  public void update(byte[] buffer) {
    update(buffer, 0, buffer.length);
  }

  /**
   * Updates hash with given array of bytes.
   *
   * @param buffer Array of bytes to use for updating the hash
   *
   * @deprecated  Please use {@link #update(byte[])} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final void Update(byte[] buffer) {
    update(buffer);
  }

  /**
   * Updates hash with a single byte.
   *
   * @param b Single byte to update the hash
   */
  public void update(byte b) {
    byte[] buffer = new byte[1];
    buffer[0] = b;

    update(buffer, 1);
  }

  /**
   * Updates hash with a single byte.
   *
   * @param b Single byte to update the hash
   *
   * @deprecated  Please use {@link #update(byte)} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final void Update(byte b) {
    update(b);
  }

  /**
   * Update buffer with given string.
   *
   * @param s String to be update to hash (is used as
   *          s.getBytes())
   */
  public final void update(String s) {
    byte[] chars = s.getBytes();
    // Changed on 2004-04-10 due to getBytes(int, int, char[], byte) being deprecated
    //byte[] chars;
    //chars = new byte[s.length()];
    //s.getBytes(0, s.length(), chars, 0);

    update(chars, chars.length);
  }

  /**
   * Update buffer with given string.
   *
   * @param s String to be update to hash (is used as
   *          s.getBytes())
   *
   * @deprecated  Please use {@link #update(java.lang.String)} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final void Update(String s) {
    update(s);
  }

  /**
   * Update buffer with a single integer (only &amp; 0xff part is used,
   * as a byte).
   *
   * @param i Integer value, which is then converted to
   *          byte as i &amp; 0xff
   */
  public void update(int i) {
    update((byte) (i & 0xff));
  }

  /**
   * Update buffer with a single integer (only &amp; 0xff part is used,
   * as a byte).
   *
   * @param i Integer value, which is then converted to
   *          byte as i &amp; 0xff
   *
   * @deprecated  Please use {@link #update(int)} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final void Update(int i) {
    update(i);
  }

  private byte[] encode(int[] input, int len) {
    int i;
    int j;
    byte[] out;

    out = new byte[len];

    for (i = j = 0; j  < len; i++, j += 4) {
      out[j] = (byte) (input[i] & 0xff);
      out[j + 1] = (byte) ((input[i] >>> 8) & 0xff);
      out[j + 2] = (byte) ((input[i] >>> 16) & 0xff);
      out[j + 3] = (byte) ((input[i] >>> 24) & 0xff);
    }

    return out;
  }

  /**
   * Returns array of bytes (16 bytes) representing hash as of the
   * current state of this object. Note: getting a hash does not
   * invalidate the hash object, it only creates a copy of the real
   * state which is finalized.
   *
   * @return Array of 16 bytes, the hash of all updated bytes
   */
  public synchronized byte[] digest() {
    byte[] bits;
    int index;
    int padlen;
    MD5State fin;

    if (finals == null) {
      fin = new MD5State(state);

      bits = encode(fin.count, 8);

      index = (fin.count[0] >>> 3) & 0x3f;
      padlen = (index < 56) ? (56 - index) : (120 - index);

      update(fin, padding, 0, padlen);
      //
      update(fin, bits, 0, 8);

      // update() sets finalds to null
      finals = fin;
    }

    return encode(finals.state, 16);
  }

  /**
   * Returns array of bytes (16 bytes) representing hash as of the
   * current state of this object. Note: getting a hash does not
   * invalidate the hash object, it only creates a copy of the real
   * state which is finalized.
   *
   * @return Array of 16 bytes, the hash of all updated bytes
   *
   * @deprecated  Please use {@link #digest()} instead.
   */
  // TODO: Remove in 6.0.0 release
  @Deprecated
  public final byte[] Final() {
    return digest();
  }

  /**
   * Turns array of bytes into string representing each byte as
   * unsigned hex number.
   *
   * @param hash Array of bytes to convert to hex-string
   * @return Generated hex string
   */
  public static String asHex(byte[] hash) {
    StringBuilder buf = new StringBuilder(hash.length * 2);
    int i;

    for (i = 0; i < hash.length; i++) {
      if (((int) hash[i] & 0xff) < 0x10) {
        buf.append("0");
      }
      buf.append(Long.toString((int) hash[i] & 0xff, 16));
    }

    return buf.toString();
  }

  /**
   * Returns 32-character hex representation of this objects hash.
   *
   * @return String of this object's hash
   */
  public String asHex() {
    return asHex(this.digest());
  }

  private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  public static String getMD5String(long md5Hi, long md5Lo) {
    return new StringBuilder(32)
        .append(hexChars[((int) (md5Hi >>> 60)) & 15])
        .append(hexChars[((int) (md5Hi >>> 56)) & 15])
        .append(hexChars[((int) (md5Hi >>> 52)) & 15])
        .append(hexChars[((int) (md5Hi >>> 48)) & 15])
        .append(hexChars[((int) (md5Hi >>> 44)) & 15])
        .append(hexChars[((int) (md5Hi >>> 40)) & 15])
        .append(hexChars[((int) (md5Hi >>> 36)) & 15])
        .append(hexChars[((int) (md5Hi >>> 32)) & 15])
        .append(hexChars[((int) (md5Hi >>> 28)) & 15])
        .append(hexChars[((int) (md5Hi >>> 24)) & 15])
        .append(hexChars[((int) (md5Hi >>> 20)) & 15])
        .append(hexChars[((int) (md5Hi >>> 16)) & 15])
        .append(hexChars[((int) (md5Hi >>> 12)) & 15])
        .append(hexChars[((int) (md5Hi >>> 8)) & 15])
        .append(hexChars[((int) (md5Hi >>> 4)) & 15])
        .append(hexChars[((int) md5Hi) & 15])
        .append(hexChars[((int) (md5Lo >>> 60)) & 15])
        .append(hexChars[((int) (md5Lo >>> 56)) & 15])
        .append(hexChars[((int) (md5Lo >>> 52)) & 15])
        .append(hexChars[((int) (md5Lo >>> 48)) & 15])
        .append(hexChars[((int) (md5Lo >>> 44)) & 15])
        .append(hexChars[((int) (md5Lo >>> 40)) & 15])
        .append(hexChars[((int) (md5Lo >>> 36)) & 15])
        .append(hexChars[((int) (md5Lo >>> 32)) & 15])
        .append(hexChars[((int) (md5Lo >>> 28)) & 15])
        .append(hexChars[((int) (md5Lo >>> 24)) & 15])
        .append(hexChars[((int) (md5Lo >>> 20)) & 15])
        .append(hexChars[((int) (md5Lo >>> 16)) & 15])
        .append(hexChars[((int) (md5Lo >>> 12)) & 15])
        .append(hexChars[((int) (md5Lo >>> 8)) & 15])
        .append(hexChars[((int) (md5Lo >>> 4)) & 15])
        .append(hexChars[((int) md5Lo) & 15])
        .toString();
  }

  public static long getMD5Hi(byte[] md5) {
    return
        (((long) (md5[0] & 255)) << 56)
            | (((long) (md5[1] & 255)) << 48)
            | (((long) (md5[2] & 255)) << 40)
            | (((long) (md5[3] & 255)) << 32)
            | (((long) (md5[4] & 255)) << 24)
            | (((long) (md5[5] & 255)) << 16)
            | (((long) (md5[6] & 255)) << 8)
            | ((long) (md5[7] & 255));
  }

  @SuppressWarnings("deprecation")
  private static long getHexValue(char ch) throws IllegalArgumentException {
    return Strings.getHex(ch);
  }

  public static long getMD5Hi(String md5) throws IllegalArgumentException {
    if (md5.length() != 32) {
      throw new IllegalArgumentException("MD5 sum is not 32 characters long, length is " + md5.length());
    }
    return
        (getHexValue(md5.charAt(0)) << 60)
            | (getHexValue(md5.charAt(1)) << 56)
            | (getHexValue(md5.charAt(2)) << 52)
            | (getHexValue(md5.charAt(3)) << 48)
            | (getHexValue(md5.charAt(4)) << 44)
            | (getHexValue(md5.charAt(5)) << 40)
            | (getHexValue(md5.charAt(6)) << 36)
            | (getHexValue(md5.charAt(7)) << 32)
            | (getHexValue(md5.charAt(8)) << 28)
            | (getHexValue(md5.charAt(9)) << 24)
            | (getHexValue(md5.charAt(10)) << 20)
            | (getHexValue(md5.charAt(11)) << 16)
            | (getHexValue(md5.charAt(12)) << 12)
            | (getHexValue(md5.charAt(13)) << 8)
            | (getHexValue(md5.charAt(14)) << 4)
            | getHexValue(md5.charAt(15));
  }

  public static long getMD5Lo(byte[] md5) {
    return
        (((long) (md5[8] & 255)) << 56)
            | (((long) (md5[9] & 255)) << 48)
            | (((long) (md5[10] & 255)) << 40)
            | (((long) (md5[11] & 255)) << 32)
            | (((long) (md5[12] & 255)) << 24)
            | (((long) (md5[13] & 255)) << 16)
            | (((long) (md5[14] & 255)) << 8)
            | ((long) (md5[15] & 255));
  }

  public static long getMD5Lo(String md5) throws IllegalArgumentException {
    if (md5.length() != 32) {
      throw new IllegalArgumentException("MD5 sum is not 32 characters long, length is " + md5.length());
    }
    return
        (getHexValue(md5.charAt(16)) << 60)
            | (getHexValue(md5.charAt(17)) << 56)
            | (getHexValue(md5.charAt(18)) << 52)
            | (getHexValue(md5.charAt(19)) << 48)
            | (getHexValue(md5.charAt(20)) << 44)
            | (getHexValue(md5.charAt(21)) << 40)
            | (getHexValue(md5.charAt(22)) << 36)
            | (getHexValue(md5.charAt(23)) << 32)
            | (getHexValue(md5.charAt(24)) << 28)
            | (getHexValue(md5.charAt(25)) << 24)
            | (getHexValue(md5.charAt(26)) << 20)
            | (getHexValue(md5.charAt(27)) << 16)
            | (getHexValue(md5.charAt(28)) << 12)
            | (getHexValue(md5.charAt(29)) << 8)
            | (getHexValue(md5.charAt(30)) << 4)
            | getHexValue(md5.charAt(31));
  }
}
