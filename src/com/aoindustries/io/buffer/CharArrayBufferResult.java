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
import com.aoindustries.encoding.MediaWriter;
import com.aoindustries.lang.NotImplementedException;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * {@inheritDoc}
 *
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class CharArrayBufferResult implements BufferResult {

	/**
	 * @see  CharArrayBufferWriter#buffer
	 */
    private final CharArrayWriter buffer;

	protected CharArrayBufferResult(CharArrayWriter buffer) {
		this.buffer = buffer;
    }

	@Override
    public long getLength() {
        return buffer.size();
    }

	private String toStringCache;

    @Override
    public String toString() {
		if(toStringCache==null) toStringCache = buffer.toString();
		return toStringCache;
    }

	@Override
    public void writeTo(MediaEncoder encoder, Writer out) throws IOException {
		writeTo(
			encoder!=null
				? new MediaWriter(encoder, out)
				: out
		);
	}

	@Override
    public void writeTo(Writer out) throws IOException {
		buffer.writeTo(out);
    }

	@Override
	public CharArrayBufferResult trim() throws IOException {
		throw new NotImplementedException("TODO");
	}
}
