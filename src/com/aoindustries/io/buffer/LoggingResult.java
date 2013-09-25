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
import com.aoindustries.util.AtomicSequence;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.io.Writer;

/**
 * Logs all write calls in a way that can be put into Java source code.
 * This is used to capture real-world scenarios for unit testing.
 *
 * This class is not thread safe.
 *
 * @author  AO Industries, Inc.
 */
public class LoggingResult implements BufferResult {

	private static final Sequence idSeq = new AtomicSequence();

	final long id = idSeq.getNextSequenceValue();
	private final BufferResult wrapped;
	private final Writer log;

	protected LoggingResult(BufferResult wrapped, Writer log) throws IOException {
		this.wrapped = wrapped;
		this.log = log;
    }
	@Override
    public long getLength() throws IOException {
		log.write("result");
		log.write(Long.toString(id));
		log.write(".getLength();\n");
		log.flush();
		return wrapped.getLength();
    }

    @Override
    public String toString() {
		try {
			log.write("result");
			log.write(Long.toString(id));
			log.write(".toString();\n");
			log.flush();
		} catch(IOException e) {
			throw new WrappedException(e);
		}
		return wrapped.toString();
    }

	@Override
    public void writeTo(MediaEncoder encoder, Writer out) throws IOException {
		log.write("result");
		log.write(Long.toString(id));
		log.write(".writeTo(");
		if(encoder==null) log.write("null");
		else log.write(encoder.getClass().getName());
		log.write(", ");
		log.write(out.getClass().getName());
		log.write(");\n");
		log.flush();
		wrapped.writeTo(encoder, out);
	}

	@Override
    public void writeTo(Writer out) throws IOException {
		log.write("result");
		log.write(Long.toString(id));
		log.write(".writeTo(");
		log.write(out.getClass().getName());
		log.write(");\n");
		log.flush();
		wrapped.writeTo(out);
    }

	@Override
	public LoggingResult trim() throws IOException {
		LoggingResult result = new LoggingResult(wrapped.trim(), log);
		log.write("BufferedResult result");
		log.write(Long.toString(result.id));
		log.write(" = result");
		log.write(Long.toString(id));
		log.write(".trim();\n");
		log.flush();
		return result;
	}
}
