/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2019  AO Industries, Inc.
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
package com.aoindustries.encoding;

import com.aoindustries.io.Encoder;
import java.io.IOException;
import java.io.Writer;

/**
 * An {@link Encoder} that performs very basic XML encoding (encodes '&lt;', '&gt;', and '&amp;' only).
 * This is not a full-featured implementation.
 * Should ao-encoding project ever not depend on this project, can use
 * <code>com.aoindustries.encoding.TextInXhtmlEncoder</code> instead of this class.
 */
public class TestXmlEncoder implements Encoder {

	public static final TestXmlEncoder testXmlEncoder = new TestXmlEncoder();

	private TestXmlEncoder() {}

	@Override
	public void writePrefixTo(Appendable out) {
		// No prefix
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		if(c == '<') out.write("&lt;");
		else if(c == '>') out.write("&gt;");
		else if(c == '&') out.write("&amp;");
		else out.write(c);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		write(cbuf, 0, cbuf.length, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		for(int i = off, end = off + len; i < end; i++) {
			write(cbuf[i], out);
		}
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		write(str, 0, str.length(), out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		for(int i = off, end = off + len; i < end; i++) {
			write(str.charAt(i), out);
		}
	}

	@Override
	public Encoder append(char c, Appendable out) throws IOException {
		if(c == '<') out.append("&lt;");
		else if(c == '>') out.append("&gt;");
		else if(c == '&') out.append("&amp;");
		else out.append(c);
		return this;
	}

	@Override
	public Encoder append(CharSequence csq, Appendable out) throws IOException {
		return append(csq, 0, csq.length(), out);
	}

	@Override
	public Encoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		for(int i = start; i < end; i++) {
			append(csq.charAt(i), out);
		}
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out) {
		// No suffix
	}
}
