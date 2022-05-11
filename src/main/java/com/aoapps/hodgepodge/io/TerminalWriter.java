/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2014, 2016, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.io.NoClose;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * A writer that controls advanced features of
 * VT/100 terminals, while silently reverting to standard
 * behavior where the functions are not supported.
 *
 * @author  AO Industries, Inc.
 */
public class TerminalWriter extends PrintWriter implements NoClose {

  private static final char ESC = 0x1b;

  private static final char[] CLEAR_SCREEN = new char[]{ESC, '[', 'H', ESC, '[', 'J'};
  private static final char[] BOLD_ON = new char[]{ESC, '[', '1', 'm'};
  private static final char[] ATTRIBUTES_OFF = new char[]{ESC, '[', 'm'};
  //private static final char[] ECHO_OFF=new char[] {ESC, '[', '1', '2', 'h'};
  //private static final char[] ECHO_ON=new char[] {ESC, '[', '1', '2', 'l'};

  // If this is not enough, could also check the TERM environment variable for expected values
  private static final boolean supported = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("linux");

  public static boolean isSupported() {
    return supported;
  }

  private static String getVerboseOut(String lastVerboseString, String newVerboseString) {
    final int lastLen = lastVerboseString.length();
    final int newLen = newVerboseString.length();
    StringBuilder verboseOut = new StringBuilder();

    // Find the number of characters that match from before and now
    int sameCount = 0;
    for (int i = 0; i < newLen && i < lastLen; i++) {
      if (lastVerboseString.charAt(i) != newVerboseString.charAt(i)) {
        break;
      }
      sameCount++;
    }

    // backspace and overwrite with spaces when new is shorter than last
    for (int i = newLen; i < lastLen; i++) {
      verboseOut.append('\b');
    }
    for (int i = newLen; i < lastLen; i++) {
      verboseOut.append(' ');
    }

    // Backspace to the first character that is different
    for (int i = 0; i < (lastLen - sameCount); i++) {
      verboseOut.append('\b');
    }

    // Append part of new output that is different
    verboseOut.append(newVerboseString, sameCount, newLen);

    return verboseOut.toString();
  }

  /**
   * (Over)writes a progress line to the given PrintStream.
   * Any extra characters from previous output are covered with spaces.
   * Any common prefix is not overwritten.
   *
   * @param  lastVerboseString  The last line output
   * @param  newVerboseString   The new line to display
   * @return  The new line (newVerboseString)
   */
  public static String progressOutput(String lastVerboseString, String newVerboseString, PrintStream out) {
    out.print(getVerboseOut(lastVerboseString, newVerboseString));
    out.flush();
    return newVerboseString;
  }

  /**
   * (Over)writes a progress line to the given PrintStream.
   * Any extra characters from previous output are covered with spaces.
   * Any common prefix is not overwritten.
   *
   * @param  lastVerboseString  The last line output
   * @param  newVerboseString   The new line to display
   * @return  The new line (newVerboseString)
   */
  public static String progressOutput(String lastVerboseString, String newVerboseString, Writer out) throws IOException {
    out.write(getVerboseOut(lastVerboseString, newVerboseString));
    out.flush();
    return newVerboseString;
  }

  private boolean enabled = true;

  public TerminalWriter(Writer out) {
    super(out);
  }

  public TerminalWriter(Writer out, boolean autoFlush) {
    super(out, autoFlush);
  }

  @Override
  public boolean isNoClose() {
    return (out instanceof NoClose) && ((NoClose) out).isNoClose();
  }

  public void attributesOff() throws IOException {
    if (supported && enabled) {
      flush();
      out.write(ATTRIBUTES_OFF);
      out.flush();
    }
  }

  public void boldOn() throws IOException {
    if (supported && enabled) {
      flush();
      out.write(BOLD_ON);
      out.flush();
    }
  }

  /*
  public void echoOff() throws IOException {
    if (supported && enabled) {
      flush();
      out.write(ECHO_OFF);
      out.flush();
    }
  }

  public void echoOn() throws IOException {
    if (supported && enabled) {
      flush();
      out.write(ECHO_ON);
      out.flush();
    }
  }*/

  public void clearScreen() throws IOException {
    if (supported && enabled) {
      flush();
      out.write(CLEAR_SCREEN);
      out.flush();
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Enables the special features of the terminal writer.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @see  #progressOutput(java.lang.String, java.lang.String, java.io.Writer)
   */
  public String progressOutput(String lastVerboseString, String newVerboseString) throws IOException {
    return progressOutput(lastVerboseString, newVerboseString, out);
  }
}
