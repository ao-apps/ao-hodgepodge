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
package com.aoindustries.servlet;

import javax.servlet.ServletOutputStream;

/**
 * Discards all data.
 */
public final class NullServletOutputStream extends ServletOutputStream {

	private static final NullServletOutputStream instance = new NullServletOutputStream();

	public static NullServletOutputStream getInstance() {
		return instance;
	}

	private NullServletOutputStream() {
	}

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void write(byte[] b) {
	}

	@Override
	public void write(byte[] b, int off, int len) {
	}

	@Override
	public void write(int b) {
	}

	@Override
	public void print(String s) {
	}

	@Override
	public void print(boolean b) {
	}

	@Override
	public void print(char c) {
	}

	@Override
	public void print(int i) {
	}

	@Override
	public void print(long l) {
	}

	@Override
	public void print(float f) {
	}

	@Override
	public void print(double d) {
	}

	@Override
	public void println() {
	}

	@Override
	public void println(String s) {
	}

	@Override
	public void println(boolean b) {
	}

	@Override
	public void println(char c) {
	}

	@Override
	public void println(int i) {
	}

	@Override
	public void println(long l) {
	}

	@Override
	public void println(float f) {
	}

	@Override
	public void println(double d) {
	}
}
