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
package com.aoindustries.io;

import java.io.IOException;
import java.io.Writer;

/**
 * Replaces strings while writing.
 *
 * This class is not thread safe and requires external synchronization if used by more than one thread.
 *
 * @author  AO Industries, Inc.
 */
public class FindReplaceWriter extends Writer {

	private final Writer out;
	private final char[] find;
	private final char[] replace;

	/**
	 * The number of characters in find currently matched.
	 */
	private int findMatched = 0;

	/**
	 * @see  #FindReplaceWriter(java.io.Writer, char[], char[]) 
	 */
	public FindReplaceWriter(Writer out, String find, String replace) {
		this(out, find.toCharArray(), replace.toCharArray());
	}

	/**
	 * @param out  The writer that will receive the converted stream.
	 * @param find  The text to find.  No defensive copy is made.  Do not modify after giving to this writer.
	 * @param replace  The text to replace.  No defensive copy is made.  Do not modify after giving to this writer.
	 */
	public FindReplaceWriter(Writer out, char[] find, char[] replace) {
		this.out = out;
		this.find = find;
		this.replace = replace;
	}

	/**
	 * Writes any buffered match.
	 */
	private void clearBuffer() throws IOException {
		if(findMatched > 0) {
			out.write(find, 0, findMatched);
			findMatched = 0;
		}
	}

	@Override
    public void write(int c) throws IOException {
		char ch = (char)c; // Cast to char to ignore the high-order bits when comparing to the find text
		if(ch == find[findMatched]) {
			findMatched++;
			if(findMatched >= find.length) {
				if(replace.length > 0) out.write(replace);
				findMatched = 0;
			}
		} else {
			clearBuffer();
			out.write(c);
		}
	}

	@Override
    public void write(char cbuf[], int off, int len) throws IOException  {
		while(len > 0) {
			write(cbuf[off++]);
			len--;
		}
	}
	
	@Override
    public void write(String str, int off, int len) throws IOException {
		while(len > 0) {
			write(str.charAt(off++));
			len--;
		}
	}
	
	@Override
    public FindReplaceWriter append(CharSequence csq) throws IOException {
        if(csq == null) {
            write("null");
		} else {
			append(csq, 0, csq.length());
		}
		return this;
	}
	
	@Override
    public FindReplaceWriter append(CharSequence csq, int start, int end) throws IOException {
		while(start < end) {
			write(csq.charAt(start++));
		}
		return this;
	}
	
	@Override
    public FindReplaceWriter append(char c) throws IOException {
		write(c);
		return this;
	}

	@Override
    public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		clearBuffer();
		out.close();
	}
}
