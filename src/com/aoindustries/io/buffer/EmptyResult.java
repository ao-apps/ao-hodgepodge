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
package com.aoindustries.io.buffer;

import com.aoindustries.encoding.MediaEncoder;
import java.io.Writer;

/**
 * A completely empty result.
 */
final public class EmptyResult implements BufferResult {

	private static final EmptyResult instance = new EmptyResult();

	public static EmptyResult getInstance() {
		return instance;
	}

	private EmptyResult() {
	}

	@Override
	public long getLength() {
		return 0;
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public void writeTo(MediaEncoder encoder, Writer out) {
		// Nothing to write
	}

	@Override
	public void writeTo(Writer out) {
		// Nothing to write
	}

	@Override
	public EmptyResult trim() {
		return this;
	}
}
