/*
 * aocode-public - Reusable Java library of general tools with minimal external dependencies.
 * Copyright (C) 2012, 2016  AO Industries, Inc.
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

import java.io.Writer;

/**
 * Discards all data.
 */
public final class NullWriter extends Writer {

	private static final NullWriter instance = new NullWriter();

	public static NullWriter getInstance() {
		return instance;
	}

	private NullWriter() {
	}

	@Override
	public void write(int c) {
	}

	@Override
	public void write(char cbuf[]) {
	}

	@Override
	public void write(char cbuf[], int off, int len) {
	}

	@Override
	public void write(String str) {
	}

	@Override
	public void write(String str, int off, int len) {
	}

	@Override
	public NullWriter append(CharSequence csq) {
		return this;
	}

	@Override
	public NullWriter append(CharSequence csq, int start, int end) {
		return this;
	}

	@Override
	public NullWriter append(char c) {
		return this;
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}
}
