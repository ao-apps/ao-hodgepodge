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
package com.aoindustries.encoding;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Streaming versions of media encoders.
 * 
 * @see  MediaEncoder
 *
 * @author  AO Industries, Inc.
 */
final public class MediaWriter extends FilterWriter implements ValidMediaFilter {

	private final MediaEncoder encoder;

	public MediaWriter(MediaEncoder encoder, Writer out) {
        super(out);
		this.encoder = encoder;
    }

	public MediaEncoder getEncoder() {
		return encoder;
	}

	/**
	 * Gets the wrapped writer.
	 */
	public Writer getOut() {
		return out;
	}

	@Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
		return encoder.isValidatingMediaInputType(inputType);
    }

    @Override
    public MediaType getValidMediaOutputType() {
		return encoder.getValidMediaOutputType();
    }

	/**
     * @see  MediaEncoder#writePrefix(java.lang.Appendable)
     */
    public void writePrefix() throws IOException {
		encoder.writePrefix(out);
    }

	@Override
    public void write(int c) throws IOException {
		encoder.write(c, out);
	}
	
	@Override
    public void write(char cbuf[]) throws IOException {
		encoder.write(cbuf, out);
	}

	@Override
	public void write(char cbuf[], int off, int len) throws IOException {
		encoder.write(cbuf, off, len, out);
	}

	@Override
    public void write(String str) throws IOException {
		encoder.write(str, out);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		encoder.write(str, off, len, out);
	}

	@Override
    public MediaWriter append(char c) throws IOException {
		encoder.append(c, out);
		return this;
	}

	@Override
	public MediaWriter append(CharSequence csq) throws IOException {
		encoder.append(csq, out);
		return this;
	}

	@Override
    public MediaWriter append(CharSequence csq, int start, int end) throws IOException {
		encoder.append(csq, start, end, out);
		return this;
	}

    /**
	 * @see  MediaEncoder#writeSuffix(java.lang.Appendable)
     */
    public void writeSuffix() throws IOException {
		encoder.writeSuffix(out);
    }
}
