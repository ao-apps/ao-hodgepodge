package com.aoapps.hodgepodge.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Obtained from: http://www.source-code.biz/snippets/java/Base64Coder.java.txt
 *
 * <p>A Base64 Encoder/Decoder.</p>
 *
 * <p>This class is used to encode and decode data in Base64 format as described in RFC 1521.</p>
 *
 * <p>This is "Open Source" software and released under the <a href="https://www.gnu.org/licenses/lgpl.html">GNU/LGPL</a> license.<br>
 * It is provided "as is" without warranty of any kind.<br>
 * Copyright 2003: Christian d'Heureuse, Inventec Informatik AG, Switzerland.<br>
 * Home page: <a href="http://www.source-code.biz">www.source-code.biz</a><br></p>
 *
 * <p>Version history:<br>
 * 2003-07-22 Christian d'Heureuse (chdh): Module created.<br>
 * 2005-08-11 chdh: Lincense changed from GPL to LGPL.<br>
 * 2006-11-21 chdh:<br>
 *  &#160; Method encode(String) renamed to encodeString(String).<br>
 *  &#160; Method decode(String) renamed to decodeString(String).<br>
 *  &#160; New method encode(byte[],int) added.<br>
 *  &#160; New method decode(String) added.<br></p>
 *
 * @deprecated  Please use {@link Base64} as of Java 8.
 */
@Deprecated
public final class Base64Coder {

  /** Make no instances. */
  private Base64Coder() {
    throw new AssertionError();
  }

  // Mapping table from 6-bit nibbles to Base64 characters.
  private static final char[]    map1 = new char[64];

  static {
    int i = 0;
    for (char c = 'A'; c <= 'Z'; c++) {
      map1[i++] = c;
    }
    for (char c = 'a'; c <= 'z'; c++) {
      map1[i++] = c;
    }
    for (char c = '0'; c <= '9'; c++) {
      map1[i++] = c;
    }
    map1[i++] = '+';
    map1[i++] = '/';
  }

  // Mapping table from Base64 characters to 6-bit nibbles.
  private static final byte[]    map2 = new byte[128];

  static {
    for (int i = 0; i < map2.length; i++) {
      map2[i] = -1;
    }
    for (int i = 0; i < 64; i++) {
      map2[map1[i]] = (byte) i;
    }
  }

  /**
   * Encodes a string into Base64 format with {@link StandardCharsets#UTF_8} encoding.
   * No blanks or line breaks are inserted.
   *
   * @param s  a String to be encoded.
   * @return   A String with the Base64 encoded data.
   */
  public static String encodeString(String s) {
    return new String(encode(s.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Encodes a byte array into Base64 format.
   * No blanks or line breaks are inserted.
   *
   * @param in  an array containing the data bytes to be encoded.
   * @return    A character array with the Base64 encoded data.
   */
  public static char[] encode(byte[] in) {
    return encode(in, in.length);
  }

  /**
   * Encodes a byte array into Base64 format.
   * No blanks or line breaks are inserted.
   *
   * @param in   an array containing the data bytes to be encoded.
   * @param inLen number of bytes to process in <code>in</code>.
   * @return     A character array with the Base64 encoded data.
   */
  public static char[] encode(byte[] in, int inLen) {
    int odataLen = (inLen * 4 + 2) / 3;       // output length without padding
    int olen = ((inLen + 2) / 3) * 4;         // output length including padding
    char[] out = new char[olen];
    int ip = 0;
    int op = 0;
    while (ip < inLen) {
      int i0 = in[ip++] & 0xff;
      int i1 = ip < inLen ? in[ip++] & 0xff : 0;
      int i2 = ip < inLen ? in[ip++] & 0xff : 0;
      int o0 = i0 >>> 2;
      int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
      int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
      final int o3 = i2 & 0x3F;
      out[op++] = map1[o0];
      out[op++] = map1[o1];
      out[op] = op < odataLen ? map1[o2] : '=';
      op++;
      out[op] = op < odataLen ? map1[o3] : '=';
      op++;
    }
    return out;
  }

  /**
   * Decodes a string from Base64 format.
   *
   * @param s  a Base64 String to be decoded.
   * @return   A String containing the decoded data.
   * @throws   IllegalArgumentException if the input is not valid Base64 encoded data.
   */
  public static String decodeString(String s) {
    return new String(decode(s));
  }

  /**
   * Decodes a byte array from Base64 format.
   *
   * @param s  a Base64 String to be decoded.
   * @return   An array containing the decoded data bytes.
   * @throws   IllegalArgumentException if the input is not valid Base64 encoded data.
   */
  public static byte[] decode(String s) {
    return decode(s.toCharArray());
  }

  /**
   * Decodes a byte array from Base64 format.
   * No blanks or line breaks are allowed within the Base64 encoded data.
   *
   * @param in  a character array containing the Base64 encoded data.
   * @return    An array containing the decoded data bytes.
   * @throws    IllegalArgumentException if the input is not valid Base64 encoded data.
   */
  public static byte[] decode(char[] in) {
    int ilen = in.length;
    if (ilen % 4 != 0) {
      throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
    }
    while (ilen > 0 && in[ilen - 1] == '=') {
      ilen--;
    }
    int olen = (ilen * 3) / 4;
    byte[] out = new byte[olen];
    int ip = 0;
    int op = 0;
    while (ip < ilen) {
      int i0 = in[ip++];
      int i1 = in[ip++];
      int i2 = ip < ilen ? in[ip++] : 'A';
      int i3 = ip < ilen ? in[ip++] : 'A';
      if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) {
        throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
      }
      int b0 = map2[i0];
      int b1 = map2[i1];
      int b2 = map2[i2];
      int b3 = map2[i3];
      if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
        throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
      }
      int o0 = (b0       << 2) | (b1 >>> 4);
      int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
      int o2 = ((b2 &   3) << 6) |  b3;
      out[op++] = (byte) o0;
      if (op < olen) {
        out[op++] = (byte) o1;
      }
      if (op < olen) {
        out[op++] = (byte) o2;
      }
    }
    return out;
  }
}
