/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2019  AO Industries, Inc.
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
package com.aoindustries.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Captures comments from any lines that begin with # (preceding space, tab, and formfeed allowed).
 * <p>
 * This class is optimized for reading {@link Properties} files and assumes
 * ISO-8859-1 encoding.
 * <p>
 * Java 1.9: Read properties files via {@link Reader} in UTF-8 format
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class CommentCaptureInputStream extends InputStream {

	private final InputStream in;

	public CommentCaptureInputStream(InputStream in) {
		this.in = in;
	}

	private boolean isLeadingWhitespace = true;
	private boolean isCommentLine = false;
	private final StringBuilder lineBuffer = new StringBuilder();

	private final List<String> comments = new ArrayList<>();

	/**
	 * Adds any buffered leading whitespace and/or comment.
	 */
	private void addComment(boolean eof) {
		if(isLeadingWhitespace || isCommentLine) {
			if(lineBuffer.length() > 0 || !eof) {
				comments.add(lineBuffer.toString());
				lineBuffer.setLength(0);
			}
			isLeadingWhitespace = !eof;
			isCommentLine = false;
		}
	}

	@Override
	public int read() throws IOException {
		int b = in.read();
		if(b == -1) {
			addComment(true);
		} else {
			// This int -> char conversion by cast only words because ISO-8859-1 encoding
			char ch = (char)b;
			if(isLeadingWhitespace) {
				if(ch != ' ' && ch != '\t' && ch != '\f') {
					isLeadingWhitespace = false;
					if(ch == '#') isCommentLine = true;
				}
			}
			if(ch == '\n') {
				addComment(false);
				isLeadingWhitespace = true;
			} else if(
				(isLeadingWhitespace || isCommentLine)
				&& ch != '\r'
			) {
				lineBuffer.append(ch);
			}
		}
		return b;
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		addComment(true);
		in.close();
	}

	public List<String> getComments() {
		return comments;
	}
}
