/*
 * ao-hodgepodge - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2026  AO Industries, Inc.
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

package com.aoapps.hodgepodge.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.aoapps.lang.EmptyArrays;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;

public class ShellInterpreterTest {

  @Test
  public void testParseEmpty() throws IOException {
    ShellInterpreter.ParseResult result = ShellInterpreter.parse(new StringReader(""), null);
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getRawArgs());
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getArgs());
    assertTrue(result.isAtEof());
  }

  @Test
  public void testParseNewlineOnly() throws IOException {
    Reader in = new StringReader("\n");
    ShellInterpreter.ParseResult result;
    // First parse empty non-eof
    result = ShellInterpreter.parse(in, null);
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getRawArgs());
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getArgs());
    assertFalse(result.isAtEof());
    // Second parse empty at eof
    result = ShellInterpreter.parse(in, null);
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getRawArgs());
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getArgs());
    assertTrue(result.isAtEof());
  }

  @Test
  public void testCarriageReturnsSkipped() throws IOException {
    Reader in = new StringReader("\r\r\r\r\r\r\r\n\r\r");
    ShellInterpreter.ParseResult result;
    // First parse empty non-eof
    result = ShellInterpreter.parse(in, null);
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getRawArgs());
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getArgs());
    assertFalse(result.isAtEof());
    // Second parse empty at eof
    result = ShellInterpreter.parse(in, null);
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getRawArgs());
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getArgs());
    assertTrue(result.isAtEof());
  }

  @Test
  public void testTimeWhoami() throws IOException {
    String[] expectedCommand = {"time", "whoami"};
    ShellInterpreter.ParseResult result;
    // No quotes
    result = ShellInterpreter.parse(new StringReader("time whoami"), null);
    assertArrayEquals(expectedCommand, result.getRawArgs());
    assertArrayEquals(expectedCommand, result.getArgs());
    assertSame(result.getArgs(), result.getRawArgs());
    assertTrue(result.isAtEof());
    // Both quoted
    result = ShellInterpreter.parse(new StringReader("\"time\" 'whoami'"), null);
    assertArrayEquals(new String[] {"\"time\"", "'whoami'"}, result.getRawArgs());
    assertArrayEquals(expectedCommand, result.getArgs());
    assertNotSame(result.getArgs(), result.getRawArgs());
    assertTrue(result.isAtEof());
    // Extra whitespace without quotes
    result = ShellInterpreter.parse(new StringReader("   \t\t\t   \0\0\0   time \r\r\r\t\f\0\0   whoami"), null);
    assertArrayEquals(expectedCommand, result.getRawArgs());
    assertArrayEquals(expectedCommand, result.getArgs());
    assertSame(result.getArgs(), result.getRawArgs());
    assertTrue(result.isAtEof());
    // With line continuation at end
    result = ShellInterpreter.parse(new StringReader("\"time\" 'whoami'\\\n"), null);
    assertArrayEquals(new String[] {"\"time\"", "'whoami'"}, result.getRawArgs());
    assertArrayEquals(expectedCommand, result.getArgs());
    assertNotSame(result.getArgs(), result.getRawArgs());
    assertTrue(result.isAtEof());
    // With line continuation in middle
    result = ShellInterpreter.parse(new StringReader("\"time\" \\\n'whoami'"), null);
    assertArrayEquals(new String[] {"\"time\"", "'whoami'"}, result.getRawArgs());
    assertArrayEquals(expectedCommand, result.getArgs());
    assertNotSame(result.getArgs(), result.getRawArgs());
    assertTrue(result.isAtEof());
    // With line continuation both
    result = ShellInterpreter.parse(new StringReader("\"time\" \\\n'whoami'\\\n"), null);
    assertArrayEquals(new String[] {"\"time\"", "'whoami'"}, result.getRawArgs());
    assertArrayEquals(expectedCommand, result.getArgs());
    assertNotSame(result.getArgs(), result.getRawArgs());
    assertTrue(result.isAtEof());
  }

  @Test
  public void testParseNullCharacter() throws IOException {
    ShellInterpreter.ParseResult result;
    // skips null on own
    result = ShellInterpreter.parse(new StringReader("\0"), null);
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getRawArgs());
    assertSame(EmptyArrays.EMPTY_STRING_ARRAY, result.getArgs());
    assertTrue(result.isAtEof());
    // Is an argument when escaped
    result = ShellInterpreter.parse(new StringReader("\\\0"), null);
    assertArrayEquals(new String[] {"\\\0"}, result.getRawArgs());
    assertArrayEquals(new String[] {"\0"}, result.getArgs());
    assertTrue(result.isAtEof());
    // Is an argument when in double quotes
    result = ShellInterpreter.parse(new StringReader("\"\0\""), null);
    assertArrayEquals(new String[] {"\"\0\""}, result.getRawArgs());
    assertArrayEquals(new String[] {"\0"}, result.getArgs());
    assertTrue(result.isAtEof());
    // Is an argument when in single quotes
    result = ShellInterpreter.parse(new StringReader("'\0'"), null);
    assertArrayEquals(new String[] {"'\0'"}, result.getRawArgs());
    assertArrayEquals(new String[] {"\0"}, result.getArgs());
    assertTrue(result.isAtEof());
  }

  @Test
  public void testSingleQuoteOnlySingleQuoteEscape() throws IOException {
    ShellInterpreter.ParseResult result;
    // Does it support escape of '?
    result = ShellInterpreter.parse(new StringReader("'\\''"), null);
    assertArrayEquals(new String[] {"'\\''"}, result.getRawArgs());
    assertArrayEquals(new String[] {"'"}, result.getArgs());
    assertTrue(result.isAtEof());
    // Does it support not escape anything else?
    result = ShellInterpreter.parse(new StringReader("'\\\"\\a\\b\\c\\d\\e\\f'"), null);
    assertArrayEquals(new String[] {"'\\\"\\a\\b\\c\\d\\e\\f'"}, result.getRawArgs());
    assertArrayEquals(new String[] {"\\\"\\a\\b\\c\\d\\e\\f"}, result.getArgs());
    assertTrue(result.isAtEof());
    // Does it support not escape anything else, with line continuation
    result = ShellInterpreter.parse(new StringReader("'\\\"\\a\\b\\\n\\c\\d\\e\\f'"), null);
    assertArrayEquals(new String[] {"'\\\"\\a\\b\\c\\d\\e\\f'"}, result.getRawArgs());
    assertArrayEquals(new String[] {"\\\"\\a\\b\\c\\d\\e\\f"}, result.getArgs());
    assertTrue(result.isAtEof());
  }

  @Test
  public void testDoubleQuoteEscapesAll() throws IOException {
    ShellInterpreter.ParseResult result;
    // Does it support escape of '?
    result = ShellInterpreter.parse(new StringReader("\"\\'\""), null);
    assertArrayEquals(new String[] {"\"\\'\""}, result.getRawArgs());
    assertArrayEquals(new String[] {"'"}, result.getArgs());
    assertTrue(result.isAtEof());
    // Does it support escape of "?
    result = ShellInterpreter.parse(new StringReader("\"\\\"\""), null);
    assertArrayEquals(new String[] {"\"\\\"\""}, result.getRawArgs());
    assertArrayEquals(new String[] {"\""}, result.getArgs());
    assertTrue(result.isAtEof());
    // Does it escape everything else?
    result = ShellInterpreter.parse(new StringReader("\"\\\"\\a\\b\\c\\d\\e\\f\""), null);
    assertArrayEquals(new String[] {"\"\\\"\\a\\b\\c\\d\\e\\f\""}, result.getRawArgs());
    assertArrayEquals(new String[] {"\"abcdef"}, result.getArgs());
    assertTrue(result.isAtEof());
  }
}
